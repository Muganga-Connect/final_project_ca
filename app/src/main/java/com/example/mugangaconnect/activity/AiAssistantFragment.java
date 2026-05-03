package com.example.mugangaconnect.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.example.mugangaconnect.R;
import com.example.mugangaconnect.data.model.ChatMessage;
import com.example.mugangaconnect.data.repository.ChatRepository;
import com.example.mugangaconnect.utils.ImagePickerUtils;
import com.example.mugangaconnect.utils.ImageUploadUtils;
import com.example.mugangaconnect.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AiAssistantFragment extends Fragment implements ImagePickerUtils.ImagePickerResultHandler {

    private EditText messageEditText;
    private ImageView sendButton;
    private ImageView uploadButton;
    private LinearLayout chatContainer;
    private ChatRepository chatRepo;
    private SessionManager session;
    private ImageUploadUtils imageUploadUtils;
    private ImagePickerUtils.ImagePickerCallback pendingImageCallback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ai_assistant, container, false);

        messageEditText = view.findViewById(R.id.messageEditText);
        sendButton = view.findViewById(R.id.sendButton);
        uploadButton = view.findViewById(R.id.uploadButton);
        chatContainer = view.findViewById(R.id.chatContainer);
        session = new SessionManager(requireContext());
        chatRepo = new ChatRepository(requireContext());
        imageUploadUtils = new ImageUploadUtils(requireContext());

        loadHistory();
        
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

        if (uploadButton != null) {
            uploadButton.setOnClickListener(v -> {
                if (ImagePickerUtils.hasImagePermissions(requireContext())) {
                    ImagePickerUtils.showImagePickerDialog(this, new ImagePickerUtils.ImagePickerCallback() {
                        @Override
                        public void onImageSelected(Uri imageUri) {
                            uploadImageToCloudinary(imageUri);
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    ImagePickerUtils.requestImagePermissions(requireActivity());
                    Toast.makeText(requireContext(), "Please grant camera and storage permissions", Toast.LENGTH_SHORT).show();
                }
            });
        }

        View clearBtn = view.findViewById(R.id.clearChatBtn);
        if (clearBtn != null) {
            clearBtn.setOnClickListener(v -> {
                String uid = session.getUid();
                if (uid != null) chatRepo.clearHistory(uid);
                chatContainer.removeAllViews();
            });
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatRepo = null;
        session = null;
        imageUploadUtils = null;
        pendingImageCallback = null;
    }

    private void sendMessage(String text) {
        addUserMessageBubble(text);
        String patientId = session.getUid();
        if (patientId == null) return;

        chatRepo.sendMessage(patientId, text, new ChatRepository.ChatCallback() {
            @Override
            public void onResponse(String aiReply) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> addAiMessageBubble(aiReply));
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        addAiMessageBubble("Sorry, I couldn't process that. Please try again."));
            }
        });
    }

    private void loadHistory() {
        String patientId = session.getUid();
        if (patientId == null) return;

        chatRepo.getHistory(patientId, messages -> {
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                chatContainer.removeAllViews();
                for (ChatMessage msg : messages) {
                    if (ChatMessage.ROLE_USER.equals(msg.getRole())) {
                        addUserMessageBubble(msg.getContent());
                    } else {
                        addAiMessageBubble(msg.getContent());
                    }
                }
            });
        });
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

    private void uploadImageToCloudinary(Uri imageUri) {
        addUserMessageBubble("[Image uploaded]");
        
        imageUploadUtils.uploadAIAssistantImage(imageUri, new ImageUploadUtils.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                    sendMessage("I've uploaded an image. Please analyze it: " + imageUrl);
                });
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Upload failed: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void setPendingImageCallback(ImagePickerUtils.ImagePickerCallback callback) {
        this.pendingImageCallback = callback;
    }

    @Override
    public ImagePickerUtils.ImagePickerCallback getPendingImageCallback() {
        return pendingImageCallback;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImagePickerUtils.handleActivityResult(this, requestCode, resultCode, data);
    }
}
