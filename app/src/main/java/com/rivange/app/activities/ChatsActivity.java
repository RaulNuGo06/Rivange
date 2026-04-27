package com.rivange.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.rivange.app.R;
import com.rivange.app.adapters.ChatAdapter;
import com.rivange.app.models.ChatRoom;
import com.rivange.app.models.User;
import com.rivange.app.utils.BottomNavHelper;
import com.rivange.app.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatsActivity extends AppCompatActivity {

    private RecyclerView rvChats;
    private Button btnCreateChat;
    private ChatAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ListenerRegistration chatsListener;
    private String currentUid = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUid = auth.getUid() == null ? "" : auth.getUid();

        rvChats = findViewById(R.id.rvChats);
        btnCreateChat = findViewById(R.id.btnCreateChat);
        adapter = new ChatAdapter(chatRoom -> {
            Intent intent = new Intent(ChatsActivity.this, ChatDetailActivity.class);
            intent.putExtra(Constants.EXTRA_CHAT_ID, chatRoom.getId());
            intent.putExtra(Constants.EXTRA_CHAT_NAME, chatRoom.getName());
            startActivity(intent);
        });

        rvChats.setLayoutManager(new LinearLayoutManager(this));
        rvChats.setAdapter(adapter);

        btnCreateChat.setOnClickListener(v -> showCreateChatDialog());

        BottomNavHelper.setup(this, findViewById(R.id.bottomNavigation), R.id.nav_chats);

        listenChats();
    }

    private void listenChats() {
        chatsListener = db.collection(Constants.COLLECTION_CHATS)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null || queryDocumentSnapshots == null) {
                        Toast.makeText(this, "Error cargando chats", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<ChatRoom> list = new ArrayList<>();
                    queryDocumentSnapshots.getDocuments().forEach(doc -> {
                        ChatRoom room = doc.toObject(ChatRoom.class);
                        if (room != null) {
                            room.setId(doc.getId());
                            List<String> participants = room.getParticipants();
                            if (participants.isEmpty() || participants.contains(currentUid)) {
                                list.add(room);
                            }
                        }
                    });
                    list.sort((a, b) -> Long.compare(b.getLastMessageAt(), a.getLastMessageAt()));
                    adapter.submitList(list);
                });
    }

    private void showCreateChatDialog() {
        if (currentUid.isEmpty()) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection(Constants.COLLECTION_USERS)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<User> users = new ArrayList<>();
                    List<String> names = new ArrayList<>();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        User user = doc.toObject(User.class);
                        if (user == null) continue;
                        user.setId(doc.getId());
                        if (user.getId().equals(currentUid)) continue;
                        users.add(user);
                        names.add(user.getName() + " (" + user.getEmail() + ")");
                    }

                    if (users.isEmpty()) {
                        Toast.makeText(this, "No hay usuarios disponibles", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    LinearLayout container = new LinearLayout(this);
                    container.setOrientation(LinearLayout.VERTICAL);
                    int pad = (int) (16 * getResources().getDisplayMetrics().density);
                    container.setPadding(pad, pad / 2, pad, 0);

                    TextView label = new TextView(this);
                    label.setText("Selecciona usuario");
                    label.setTextColor(ContextCompat.getColor(this, R.color.text_dark));
                    container.addView(label);

                    Spinner spinnerUsers = new Spinner(this);
                    ArrayAdapter<String> usersAdapter = buildDialogSpinnerAdapter(names);
                    spinnerUsers.setAdapter(usersAdapter);
                    container.addView(spinnerUsers);

                    new AlertDialog.Builder(this)
                            .setTitle("Nuevo chat privado")
                            .setView(container)
                            .setNegativeButton("Cancelar", null)
                            .setPositiveButton("Crear", (dialog, which) -> {
                                User selected = users.get(spinnerUsers.getSelectedItemPosition());
                                createOrOpenPrivateChat(selected);
                            })
                            .show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "No se pudo cargar usuarios", Toast.LENGTH_SHORT).show()
                );
    }

    private void createOrOpenPrivateChat(User otherUser) {
        db.collection(Constants.COLLECTION_CHATS)
                .whereArrayContains("participants", currentUid)
                .get()
                .addOnSuccessListener(snapshot -> {
                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        ChatRoom room = doc.toObject(ChatRoom.class);
                        if (room == null) continue;
                        List<String> participants = room.getParticipants();
                        if (participants.size() == 2 && participants.contains(otherUser.getId())) {
                            openChat(doc.getId(), room.getName());
                            return;
                        }
                    }

                    String chatName = "Chat con " + otherUser.getName();
                    Map<String, Object> newChat = new HashMap<>();
                    newChat.put("id", "");
                    newChat.put("name", chatName);
                    newChat.put("lastMessage", "");
                    newChat.put("lastMessageAt", 0L);
                    newChat.put("lastSenderId", "");
                    newChat.put("participants", Arrays.asList(currentUid, otherUser.getId()));

                    db.collection(Constants.COLLECTION_CHATS)
                            .add(newChat)
                            .addOnSuccessListener(ref -> openChat(ref.getId(), chatName))
                            .addOnFailureListener(e ->
                                    Toast.makeText(this, "No se pudo crear chat", Toast.LENGTH_SHORT).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "No se pudo validar chats existentes", Toast.LENGTH_SHORT).show()
                );
    }

    private void openChat(String chatId, String chatName) {
        Intent intent = new Intent(ChatsActivity.this, ChatDetailActivity.class);
        intent.putExtra(Constants.EXTRA_CHAT_ID, chatId);
        intent.putExtra(Constants.EXTRA_CHAT_NAME, chatName);
        startActivity(intent);
    }

    private ArrayAdapter<String> buildDialogSpinnerAdapter(List<String> values) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values) {
            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(ChatsActivity.this, R.color.text_dark));
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(ContextCompat.getColor(ChatsActivity.this, R.color.text_dark));
                }
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        return adapter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (chatsListener != null) {
            chatsListener.remove();
        }
    }
}
