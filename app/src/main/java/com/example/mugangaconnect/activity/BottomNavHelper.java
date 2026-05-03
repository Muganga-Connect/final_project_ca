package com.example.mugangaconnect.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.mugangaconnect.R;

public class BottomNavHelper {

    public enum Screen { DASHBOARD, SCHEDULE, AI_ASSISTANT, PROFILE }

    public static void setup(Activity activity, Screen activeScreen) {
        int[] navIds = {R.id.navDashboard, R.id.navSchedule, R.id.navAiAssistant, R.id.navProfile};
        Screen[] screens = {Screen.DASHBOARD, Screen.SCHEDULE, Screen.AI_ASSISTANT, Screen.PROFILE};

        for (int i = 0; i < navIds.length; i++) {
            LinearLayout item = activity.findViewById(navIds[i]);
            if (item == null) continue;

            if (screens[i] == activeScreen) {
                item.setBackgroundResource(R.drawable.bottom_nav_selected_bg);
                item.setPadding(dp(activity, 14), dp(activity, 8), dp(activity, 14), dp(activity, 8));
                tintItem(item, "#FFFFFF");
            } else {
                item.setBackground(null);
                item.setPadding(0, 0, 0, 0);
                tintItem(item, "#667A90");
            }

            Screen target = screens[i];
            item.setOnClickListener(v -> navigate(activity, target, activeScreen));
        }
    }

    private static void navigate(Activity activity, Screen target, Screen current) {
        if (target == current) return;
        switch (target) {
            case DASHBOARD:
                activity.startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
                break;
            case SCHEDULE:
                activity.startActivity(new Intent(activity, AppointmentManagementActivity.class));
                activity.finish();
                break;
            case AI_ASSISTANT:
                activity.startActivity(new Intent(activity, AIAssistantActivity.class));
                activity.finish();
                break;
            case PROFILE:
                activity.startActivity(new Intent(activity, ProfileActivity.class));
                activity.finish();
                break;
        }
    }

    private static void tintItem(LinearLayout item, String colorHex) {
        int color = android.graphics.Color.parseColor(colorHex);
        for (int i = 0; i < item.getChildCount(); i++) {
            View child = item.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setColorFilter(color);
            } else if (child instanceof TextView) {
                ((TextView) child).setTextColor(color);
                ((TextView) child).setTypeface(null,
                        colorHex.equals("#FFFFFF") ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
            }
        }
    }

    private static int dp(Activity activity, int value) {
        return Math.round(activity.getResources().getDisplayMetrics().density * value);
    }
}
