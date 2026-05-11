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
import com.example.mugangaconnect.activity.AppointmentRepository;
import com.example.mugangaconnect.utils.LocaleHelper;
import com.example.mugangaconnect.utils.SessionManager;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.example.mugangaconnect.BuildConfig;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AIAssistantActivity extends AppCompatActivity {

    private AppointmentRepository appointmentRepo;
    private SessionManager session;
    private LinearLayout chatContainer;
    private ScrollView chatScrollView;
    private GenerativeModelFutures model;
    private final Executor executor = Executors.newSingleThreadExecutor();

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
        appointmentRepo = new AppointmentRepository();

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.topStatsBar), (v, insets) -> {
                    Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                });

        chatContainer  = findViewById(R.id.chatContainer);
        chatScrollView = findScrollViewParent();

        initializeGemini();
        setupQuickActions();
        setupSendButton();
        BottomNavHelper.setup(this, BottomNavHelper.Screen.AI_ASSISTANT);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavHelper.setup(this, BottomNavHelper.Screen.AI_ASSISTANT);
    }

    // ── UI setup ──────────────────────────────────────────────────────────────

    private void initializeGemini() {
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", BuildConfig.GEMINI_API_KEY);
        model = GenerativeModelFutures.from(gm);
    }

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
     * Matches the user's message against localised keywords for immediate actions,
     * otherwise delegates to the Google Gemini AI model.
     */
    private void processAiResponse(String userMsg) {
        String lower = userMsg.toLowerCase().trim();

        if (containsKeyword(lower, R.string.ai_kw_risk)) {
            checkAppointmentRisk();
        } else if (containsKeyword(lower, R.string.ai_kw_reschedule)) {
            addAiMessage(getString(R.string.ai_reschedule_response));
            startActivity(new Intent(this, AppointmentManagementActivity.class));
        } else {
            callGeminiAI(userMsg);
        }
    }

    private void callGeminiAI(String prompt) {
        Content content = new Content.Builder()
                .addText(prompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String text = result.getText();
                runOnUiThread(() -> addAiMessage(text));
            }

            @Override
            public void onFailure(Throwable t) {
                runOnUiThread(() -> addAiMessage("Sorry, I'm having trouble connecting to my AI brain. " + t.getMessage()));
            }
        }, executor);
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
        tv.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.text_on_primary));
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
        tv.setTextColor(androidx.core.content.ContextCompat.getColor(this, R.color.text_primary));
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
