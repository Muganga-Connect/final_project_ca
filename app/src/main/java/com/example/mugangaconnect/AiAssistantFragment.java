package com.example.mugangaconnect;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AiAssistantFragment extends Fragment {

    private EditText messageEditText;
    private ImageView sendButton;
    private LinearLayout chatContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_assistant, container, false);

        messageEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);
        chatContainer = view.findViewById(R.id.chatContainer);
        
        TextView checkRiskBtn = view.findViewById(R.id.checkRiskButton);
        TextView rescheduleBtn = view.findViewById(R.id.rescheduleButton);

        if (checkRiskBtn != null) {
            checkRiskBtn.setOnClickListener(v -> sendMessage("Check Risk"));
        }

        if (rescheduleBtn != null) {
            rescheduleBtn.setOnClickListener(v -> sendMessage("Reschedule Appointment"));
        }

        if (sendButton != null) {
            sendButton.setOnClickListener(v -> {
                String text = messageEditText.getText().toString().trim();
                if (!text.isEmpty()) {
                    sendMessage(text);
                    messageEditText.setText("");
                }
            });
        }

        return view;
    }

    private void sendMessage(String text) {
        addUserMessageBubble(text);
        
        // Simulate "Thinking" and then responding
        new android.os.Handler().postDelayed(() -> {
            String response = getConsultationResponse(text.toLowerCase());
            addAiMessageBubble(response);
        }, 1500);
    }

    private String getConsultationResponse(String query) {
        if (query.contains("risk") || query.contains("check risk")) {
            return "Based on your history, you've attended 90% of your appointments. However, your last two visits were delayed by 15 mins. Your current risk of missing the next one is LOW (12%). Would you like a 1-hour early reminder?";
        } else if (query.contains("reschedule") || query.contains("appointment")) {
            return "I can help with that. You have an upcoming appointment on Friday at 2:00 PM. I see slots available on Monday at 10:00 AM or Tuesday at 3:30 PM. Which works better for you?";
        } else if (query.contains("heart") || query.contains("bpm")) {
            return "Your heart rate is currently 72 BPM, which is within the normal range for you. You've been active for 30 minutes today. Keep it up!";
        } else if (query.contains("pressure") || query.contains("blood")) {
            return "Your last blood pressure reading was 118/76 mmHg. This is optimal. Are you feeling any dizziness or headaches today?";
        } else if (query.contains("hello") || query.contains("hi")) {
            return "Hello! I'm your Muganga AI Assistant. I can help you analyze your health trends, manage appointments, or check your adherence risks. What can I do for you today?";
        } else {
            return "I understand you're asking about '" + query + "'. As your health consultant, I recommend monitoring your vitals daily. Is there a specific symptom or record you'd like me to look into?";
        }
    }

    private void addUserMessageBubble(String text) {
        View bubble = getLayoutInflater().inflate(R.layout.item_chat_user, chatContainer, false);
        TextView messageText = bubble.findViewById(R.id.messageText);
        TextView timeText = bubble.findViewById(R.id.timeText);
        
        messageText.setText(text);
        timeText.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        
        chatContainer.addView(bubble);
        scrollToBottom();
    }

    private void addAiMessageBubble(String text) {
        View bubble = getLayoutInflater().inflate(R.layout.item_chat_ai, chatContainer, false);
        TextView messageText = bubble.findViewById(R.id.messageText);
        TextView timeText = bubble.findViewById(R.id.timeText);
        
        messageText.setText(text);
        timeText.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date()));
        
        chatContainer.addView(bubble);
        scrollToBottom();
    }

    private void scrollToBottom() {
        View scrollView = (View) chatContainer.getParent();
        if (scrollView instanceof android.widget.ScrollView) {
            scrollView.post(() -> ((android.widget.ScrollView) scrollView).fullScroll(View.FOCUS_DOWN));
        }
    }
}