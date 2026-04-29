package com.example.mugangaconnect.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.repository.AppointmentRepository;
import com.example.mugangaconnect.utils.NoShowPredictor;
import com.example.mugangaconnect.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AIAssistantActivity extends AppCompatActivity {

    private EditText messageEditText;
    private ImageView sendButton;
    private LinearLayout chatContainer;
    private SessionManager session;
    private AppointmentRepository appointmentRepo;
    private String cachedRisk = "LOW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.fragment_ai_assistant);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.topStatsBar), (v, insets) -> {
                    Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                    return insets;
                });

        session = new SessionManager(this);
        appointmentRepo = new AppointmentRepository(this);

        messageEditText = findViewById(R.id.messageEditText);
        sendButton      = findViewById(R.id.sendButton);
        chatContainer   = findViewById(R.id.chatContainer);

        TextView checkRiskBtn  = findViewById(R.id.checkRiskButton);
        TextView rescheduleBtn = findViewById(R.id.rescheduleButton);

        if (checkRiskBtn != null)  checkRiskBtn.setOnClickListener(v -> sendMessage("Check Risk"));
        if (rescheduleBtn != null) rescheduleBtn.setOnClickListener(v -> sendMessage("Reschedule Appointment"));

        if (sendButton != null) {
            sendButton.setOnClickListener(v -> {
                String text = messageEditText.getText().toString().trim();
                if (!text.isEmpty()) {
                    sendMessage(text);
                    messageEditText.setText("");
                }
            });
        }

        // Pre-load risk level from real data
        String uid = session.getUid();
        if (uid != null) {
            appointmentRepo.getMissedStats(uid, new AppointmentRepository.Callback<int[]>() {
                @Override public void onResult(int[] stats) {
                    cachedRisk = NoShowPredictor.predict(stats[0], stats[1]);
                }
                @Override public void onError(String message) {}
            });
        }

        BottomNavHelper.setup(this, BottomNavHelper.Screen.AI_ASSISTANT);
    }

    private void sendMessage(String text) {
        addUserBubble(text);
        new android.os.Handler().postDelayed(
                () -> addAiBubble(getResponse(text.toLowerCase())), 1200);
    }

    private String getResponse(String query) {
        if (query.contains("risk") || query.contains("check risk")) {
            int leadHours = NoShowPredictor.getReminderLeadHours(cachedRisk);
            return "Your current no-show risk is " + cachedRisk +
                    ". Based on this, reminders will be sent " + leadHours + " hours before your appointment.";
        } else if (query.contains("reschedule") || query.contains("appointment")) {
            return "I can help with that. Head to the Schedule tab to reschedule or book a new appointment.";
        } else if (query.contains("heart") || query.contains("bpm")) {
            return "Your heart rate is currently 72 BPM, within the normal range. Keep it up!";
        } else if (query.contains("pressure") || query.contains("blood")) {
            return "Your last blood pressure reading was 118/76 mmHg — optimal. Any dizziness or headaches today?";
        } else if (query.contains("hello") || query.contains("hi")) {
            return "Hello, " + session.getFullName() + "! I'm your Muganga AI Assistant. How can I help you today?";
        } else {
            return "I understand you're asking about '" + query + "'. I recommend monitoring your vitals daily. Is there a specific symptom you'd like me to look into?";
        }
    }

    private void addUserBubble(String text) {
        View bubble = getLayoutInflater().inflate(R.layout.item_chat_user, chatContainer, false);
        ((TextView) bubble.findViewById(R.id.messageText)).setText(text);
        ((TextView) bubble.findViewById(R.id.timeText)).setText(now());
        chatContainer.addView(bubble);
        scrollDown();
    }

    private void addAiBubble(String text) {
        View bubble = getLayoutInflater().inflate(R.layout.item_chat_ai, chatContainer, false);
        ((TextView) bubble.findViewById(R.id.messageText)).setText(text);
        ((TextView) bubble.findViewById(R.id.timeText)).setText(now());
        chatContainer.addView(bubble);
        scrollDown();
    }

    private String now() {
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
    }

    private void scrollDown() {
        View sv = (View) chatContainer.getParent();
        if (sv instanceof android.widget.ScrollView)
            sv.post(() -> ((android.widget.ScrollView) sv).fullScroll(View.FOCUS_DOWN));
    }
}
