package com.rivange.app.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.rivange.app.R;
import com.rivange.app.adapters.RankingAdapter;
import com.rivange.app.models.RankingEntry;
import com.rivange.app.utils.BottomNavHelper;
import com.rivange.app.utils.Constants;
import com.rivange.app.utils.RankingRules;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RankingActivity extends AppCompatActivity {

    private RecyclerView rvRanking;
    private RankingAdapter adapter;
    private FirebaseFirestore db;
    private ListenerRegistration rankingListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        db = FirebaseFirestore.getInstance();

        rvRanking = findViewById(R.id.rvRanking);
        adapter = new RankingAdapter();

        rvRanking.setLayoutManager(new LinearLayoutManager(this));
        rvRanking.setAdapter(adapter);

        BottomNavHelper.setup(this, findViewById(R.id.bottomNavigation), R.id.nav_ranking);

        listenRanking();
    }

    private void listenRanking() {
        rankingListener = db.collection(Constants.COLLECTION_RANKING)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null || queryDocumentSnapshots == null) {
                        Toast.makeText(this, "Error cargando ranking", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    List<RankingEntry> list = new ArrayList<>();
                    queryDocumentSnapshots.getDocuments().forEach(doc -> {
                        RankingEntry entry = doc.toObject(RankingEntry.class);
                        if (entry != null) {
                            entry.setId(doc.getId());
                            if (entry.getRankName().isEmpty()) {
                                entry.setRankName(RankingRules.getRankName(entry.getPoints()));
                            }
                            list.add(entry);
                        }
                    });

                    list.sort(Comparator.comparingInt(RankingEntry::getPoints).reversed());
                    for (int i = 0; i < list.size(); i++) {
                        list.get(i).setPosition(i + 1);
                    }

                    adapter.submitList(list);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rankingListener != null) {
            rankingListener.remove();
        }
    }
}
