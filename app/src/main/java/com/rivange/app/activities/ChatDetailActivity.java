package com.rivange.app.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.rivange.app.R;
import com.rivange.app.adapters.MessageAdapter;
import com.rivange.app.models.ChatMessage;
import com.rivange.app.models.User;
import com.rivange.app.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class ChatDetailActivity extends AppCompatActivity {

    private RecyclerView rvMessages;
    private EditText etMessage;
    private Button btnSendMessage;

    private MessageAdapter adapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private String chatId;
    private String chatName;
    private String currentUserName = "Usuario";

    private ListenerRegistration messagesListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        chatId = getIntent().getStringExtra(Constants.EXTRA_CHAT_ID);
        chatName = getIntent().getStringExtra(Constants.EXTRA_CHAT_NAME);

        if (chatName != null) {
            setTitle(chatName);
        }

        rvMessages = findViewById(R.id.rvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);

        adapter = new MessageAdapter();
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setAdapter(adapter);

        btnSendMessage.setOnClickListener(v -> sendMessage());

        loadCurrentUserName();
        listenMessages();
    }

    private void loadCurrentUserName() {
        String uid = auth.getUid();
        if (uid == null) return;

        db.collection(Constants.COLLECTION_USERS)
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null && user.getName() != null && !user.getName().trim().isEmpty()) {
                        currentUserName = user.getName();
                    }
                });
    }

    private void listenMessages() {
        if (TextUtils.isEmpty(chatId)) return;

        messagesListener = db.collection(Constants.COLLECTION_MESSAGES)
                .whereEqualTo("chatId", chatId)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Error cargando mensajes", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (queryDocumentSnapshots == null) {
                        return;
                    }

                    List<ChatMessage> list = new ArrayList<>();
                    queryDocumentSnapshots.getDocuments().forEach(doc -> {
                        ChatMessage message = doc.toObject(ChatMessage.class);
                        if (message != null) {
                            message.setId(doc.getId());
                            list.add(message);
                        }
                    });

                    list.sort((m1, m2) -> Long.compare(m1.getCreatedAt(), m2.getCreatedAt()));

                    adapter.submitList(list);
                    if (!list.isEmpty()) {
                        rvMessages.scrollToPosition(list.size() - 1);
                    }
                });
    }

    private void sendMessage() {
        String text = etMessage.getText().toString().trim();
        String uid = auth.getUid();

        if (TextUtils.isEmpty(text) || TextUtils.isEmpty(chatId) || uid == null) {
            return;
        }

        ChatMessage message = new ChatMessage(
                "",
                chatId,
                uid,
                currentUserName,
                text,
                System.currentTimeMillis()
        );

        btnSendMessage.setEnabled(false);

        db.collection(Constants.COLLECTION_MESSAGES)
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    db.collection(Constants.COLLECTION_CHATS)
                            .document(chatId)
                            .update(
                                    "lastMessage", text,
                                    "lastMessageAt", System.currentTimeMillis(),
                                    "lastSenderId", uid
                            );

                    etMessage.setText("");
                    btnSendMessage.setEnabled(true);
                })
                .addOnFailureListener(e -> {
                    btnSendMessage.setEnabled(true);
                    Toast.makeText(this, "No se pudo enviar el mensaje", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messagesListener != null) {
            messagesListener.remove();
        }
    }
}