package com.rivange.app.utils;

import android.app.Activity;
import android.content.Intent;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rivange.app.R;
import com.rivange.app.activities.ChatsActivity;
import com.rivange.app.activities.MainActivity;
import com.rivange.app.activities.MatchesActivity;
import com.rivange.app.activities.ProfileActivity;
import com.rivange.app.activities.RankingActivity;

public final class BottomNavHelper {

    private BottomNavHelper() {
    }

    public static void setup(Activity activity, BottomNavigationView bottomNavigationView, int selectedItemId) {
        bottomNavigationView.setSelectedItemId(selectedItemId);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == selectedItemId) {
                return true;
            }

            Intent intent = null;

            if (id == R.id.nav_home) {
                intent = new Intent(activity, MainActivity.class);
            } else if (id == R.id.nav_matches) {
                intent = new Intent(activity, MatchesActivity.class);
            } else if (id == R.id.nav_ranking) {
                intent = new Intent(activity, RankingActivity.class);
            } else if (id == R.id.nav_chats) {
                intent = new Intent(activity, ChatsActivity.class);
            } else if (id == R.id.nav_profile) {
                intent = new Intent(activity, ProfileActivity.class);
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                return true;
            }

            return false;
        });
    }
}