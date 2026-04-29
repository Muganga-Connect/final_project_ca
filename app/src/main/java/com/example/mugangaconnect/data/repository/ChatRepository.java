package com.example.mugangaconnect.data.repository;

import android.content.Context;

import com.example.mugangaconnect.BuildConfig;
import com.example.mugangaconnect.data.local.AppDatabase;
import com.example.mugangaconnect.data.local.ChatDao;
import com.example.mugangaconnect.data.model.ChatMessage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRepository {
    public interface ChatCallback {
        void onResponse(String aiReply);
        void onError(String message);
    }

    public interface HistoryCallback {
        void onResult(List<ChatMessage> messages);
    }

    private final ChatDao chatDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ChatRepository(Context context) {
        this.chatDao = new ChatDao(AppDatabase.getInstance(context));
    }

    public void sendMessage(String patientId, String text, ChatCallback callback) {
        executor.execute(() -> {
            try {
                chatDao.insert(new ChatMessage(patientId, ChatMessage.ROLE_USER, text));
                String aiReply = askGemini(text);
                chatDao.insert(new ChatMessage(patientId, ChatMessage.ROLE_AI, aiReply));
                callback.onResponse(aiReply);
            } catch (Exception e) {
                callback.onError(e.getMessage() != null ? e.getMessage() : "Unknown error");
            }
        });
    }

    public void getHistory(String patientId, HistoryCallback callback) {
        executor.execute(() -> callback.onResult(chatDao.getHistory(patientId)));
    }

    public void clearHistory(String patientId) {
        executor.execute(() -> chatDao.clearHistory(patientId));
    }

    private String askGemini(String prompt) throws Exception {
        String apiKey = BuildConfig.GEMINI_API_KEY;
        if (apiKey == null || apiKey.trim().isEmpty() || "YOUR_API_KEY_HERE".equals(apiKey)) {
            return "Gemini API key is missing. Set GEMINI_API_KEY in gradle.properties to enable AI responses.";
        }

        URL url = new URL(
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key="
                        + apiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(20000);

        JSONObject textPart = new JSONObject().put("text", prompt);
        JSONArray parts = new JSONArray().put(textPart);
        JSONObject content = new JSONObject()
                .put("role", "user")
                .put("parts", parts);
        JSONArray contents = new JSONArray().put(content);
        JSONObject body = new JSONObject().put("contents", contents);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int code = conn.getResponseCode();
        InputStream stream = code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream();
        String response = readStream(stream);
        if (code < 200 || code >= 300) {
            throw new Exception("Gemini request failed (" + code + "): " + response);
        }

        JSONObject json = new JSONObject(response);
        JSONArray candidates = json.optJSONArray("candidates");
        if (candidates == null || candidates.length() == 0) {
            return "I could not generate a response right now.";
        }

        JSONObject first = candidates.getJSONObject(0);
        JSONObject contentObj = first.optJSONObject("content");
        if (contentObj == null) return "I could not generate a response right now.";
        JSONArray aiParts = contentObj.optJSONArray("parts");
        if (aiParts == null || aiParts.length() == 0) return "I could not generate a response right now.";
        return aiParts.getJSONObject(0).optString("text", "I could not generate a response right now.");
    }

    private String readStream(InputStream stream) throws Exception {
        if (stream == null) return "";
        StringBuilder out = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) out.append(line);
        }
        return out.toString();
    }
}
