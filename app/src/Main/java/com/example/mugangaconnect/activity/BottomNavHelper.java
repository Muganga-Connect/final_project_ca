package com.example.mugangaconnect.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.mugangaconnect.R;

public class BottomNavHelper {

    public enum Screen { DASHBOARD, SCHEDULE, AI_ASSISTANT, PROFILE }

    public static void setup(Activity activity, Screen activeScreen) {
        int[] navIds = {R.id.navDashboard, R.id.navSchedule, R.id.navAiAssistant, R.id.navProfile};
        Screen[] screens = {Screen.DASHBOARD, Screen.SCHEDULE, Screen.AI_ASSISTANT, Screen.PROFILE};



        View bottomBar = activity.findViewById(R.id.bottomBar);
        if (bottomBar != null) {
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(bottomBar, (v, insets) -> {
                androidx.core.graphics.Insets sys = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
                android.view.ViewGroup.MarginLayoutParams lp = (android.view.ViewGroup.MarginLayoutParams) v.getLayoutParams();
                // We want a fixed 24dp margin ABOVE the system navigation bar
                lp.bottomMargin = sys.bottom + dp(activity, 24);
                v.setLayoutParams(lp);
                return insets;
            });
        }

        for (int i = 0; i < navIds.length; i++) {
            LinearLayout item = activity.findViewById(navIds[i]);
            if (item == null) continue;

            if (screens[i] == activeScreen) {
                item.setBackgroundResource(R.drawable.bottom_nav_selected_bg);
                item.setPadding(dp(activity, 20), dp(activity, 12), dp(activity, 20), dp(activity, 12));
                tintItem(item, ContextCompat.getColor(activity, R.color.text_on_primary), true);
            } else {
                item.setBackground(null);
                item.setPadding(0, 0, 0, 0);
                tintItem(item, ContextCompat.getColor(activity, R.color.icon_tint), false);
            }

            Screen target = screens[i];
            item.setOnClickListener(v -> navigate(activity, target, activeScreen));
        }
    }

    private static void navigate(Activity activity, Screen target, Screen current) {
        if (target == current) return;
        Intent intent = null;
        switch (target) {
            case DASHBOARD:
                intent = new Intent(activity, MainActivity.class);
                break;
            case SCHEDULE:
                intent = new Intent(activity, AppointmentManagementActivity.class);
                break;
            case AI_ASSISTANT:
                intent = new Intent(activity, AIAssistantActivity.class);
                break;
            case PROFILE:
                intent = new Intent(activity, ProfileActivity.class);
                break;
        }
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        }
    }

    private static void tintItem(LinearLayout item, int color, boolean isBold) {
        for (int i = 0; i < item.getChildCount(); i++) {
            View child = item.getChildAt(i);
            if (child instanceof ImageView) {
                ((ImageView) child).setColorFilter(color);
            } else if (child instanceof TextView) {
                ((TextView) child).setTextColor(color);
                ((TextView) child).setTypeface(null, isBold ? Typeface.BOLD : Typeface.NORMAL);
            }
        }
    }

    private static int dp(Activity activity, int value) {
        return Math.round(activity.getResources().getDisplayMetrics().density * value);
    }
}
