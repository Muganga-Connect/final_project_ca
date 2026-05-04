package com.example.mugangaconnect.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.Appointment;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.utils.LocaleHelper;
import com.example.mugangaconnect.utils.SessionManager;

import java.util.List;

public class AIAssistantActivity extends AppCompatActivity {

    private AppointmentRepository appointmentRepo;
    private SessionManager session;
    private LinearLayout chatContainer;
    private ScrollView chatScrollView;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_ai_assistant);

        session = new SessionManager(this);
        appointmentRepo = new AppointmentRepository(this);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.topStatsBar), (v, insets) -> {
                    Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                });

        chatContainer  = findViewById(R.id.chatContainer);
        chatScrollView = findScrollViewParent();

        setupQuickActions();
        setupSendButton();
        BottomNavHelper.setup(this, BottomNavHelper.Screen.AI_ASSISTANT);
    }

    // ── UI setup ──────────────────────────────────────────────────────────────

    private void setupQuickActions() {
        TextView checkRiskBtn  = findViewById(R.id.checkRiskButton);
        TextView rescheduleBtn = findViewById(R.id.rescheduleButton);

        if (checkRiskBtn != null) {
            checkRiskBtn.setOnClickListener(v -> {
                addUserMessage(getString(R.string.ai_check_risk_question));
                checkAppointmentRisk();
            });
        }

        if (rescheduleBtn != null) {
            rescheduleBtn.setOnClickListener(v -> {
                addUserMessage(getString(R.string.reschedule_appointment));
                addAiMessage(getString(R.string.ai_reschedule_response));
                startActivity(new Intent(this, AppointmentManagementActivity.class));
            });
        }
    }

    private void setupSendButton() {
        EditText messageInput = findViewById(R.id.messageEditText);
        View sendButton       = findViewById(R.id.sendButton);

        if (sendButton != null) {
            sendButton.setOnClickListener(v -> {
                if (messageInput == null) return;
                String message = messageInput.getText().toString().trim();
                if (!message.isEmpty()) {
                    addUserMessage(message);
                    processAiResponse(message);
                    messageInput.setText("");
                }
            });
        }
    }

    // ── AI logic ──────────────────────────────────────────────────────────────

    /**
     * Matches the user's message against localised keywords loaded from strings.xml,
     * so the AI understands input in English, French, AND Kinyarwanda.
     */
    private void processAiResponse(String userMsg) {
        String lower = userMsg.toLowerCase().trim();

        if (containsKeyword(lower, R.string.ai_kw_risk)) {
            checkAppointmentRisk();
        } else if (containsKeyword(lower, R.string.ai_kw_hello)
                || containsKeyword(lower, R.string.ai_kw_hi)) {
            addAiMessage(getString(R.string.ai_hello_response));
        } else if (containsKeyword(lower, R.string.ai_kw_reschedule)) {
            addAiMessage(getString(R.string.ai_reschedule_response));
            startActivity(new Intent(this, AppointmentManagementActivity.class));
        } else if (containsKeyword(lower, R.string.ai_kw_reminder)) {
            addAiMessage(getString(R.string.ai_reminder_response));
        } else {
            addAiMessage(getString(R.string.ai_default_response));
        }
    }

    /** Returns true if the message contains the localised keyword for the given string resource. */
    private boolean containsKeyword(String lowerMessage, int keywordResId) {
        return lowerMessage.contains(getString(keywordResId).toLowerCase());
    }

    private void checkAppointmentRisk() {
        String uid = session.getUid();
        if (uid == null) return;

        appointmentRepo.getForPatient(uid, new AppointmentRepository.Callback<List<Appointment>>() {
            @Override
            public void onResult(List<Appointment> data) {
                int missed = 0;
                for (Appointment a : data) {
                    if (Appointment.Status.MISSED.name().equals(a.getStatus())) missed++;
                }

                final String response;
                if (missed >= 2) {
                    response = getString(R.string.ai_risk_high, missed);
                } else if (missed == 1) {
                    response = getString(R.string.ai_risk_medium);
                } else {
                    response = getString(R.string.ai_risk_low);
                }
                runOnUiThread(() -> addAiMessage(response));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> addAiMessage(getString(R.string.ai_error)));
            }
        });
    }

    // ── Chat bubble helpers ───────────────────────────────────────────────────

    private void addUserMessage(String message) {
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setBackgroundResource(R.drawable.user_message_bg);
        tv.setTextColor(android.graphics.Color.WHITE);
        tv.setPadding(32, 24, 32, 24);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.END;
        p.setMargins(100, 16, 16, 16);
        tv.setLayoutParams(p);

        chatContainer.addView(tv);
        scrollToBottom();
    }

    private void addAiMessage(String message) {
        TextView tv = new TextView(this);
        tv.setText(message);
        tv.setBackgroundResource(R.drawable.ai_message_bg);
        tv.setTextColor(android.graphics.Color.BLACK);
        tv.setPadding(32, 24, 32, 24);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.START;
        p.setMargins(16, 16, 100, 16);
        tv.setLayoutParams(p);

        chatContainer.addView(tv);
        scrollToBottom();
    }

    private void scrollToBottom() {
        if (chatScrollView != null) {
            chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
        }
    }

    /** Walks up the view hierarchy to find the ScrollView that wraps chatContainer. */
    private ScrollView findScrollViewParent() {
        View v = chatContainer;
        while (v != null) {
            if (v.getParent() instanceof ScrollView) return (ScrollView) v.getParent();
            if (v.getParent() instanceof View) v = (View) v.getParent();
            else break;
        }
        return null;
    }
}
