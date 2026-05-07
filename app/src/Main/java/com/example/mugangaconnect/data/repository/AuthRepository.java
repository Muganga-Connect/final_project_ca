package com.example.mugangaconnect.data.repository;

import com.example.mugangaconnect.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;

public class AuthRepository {

    private static final String USERS_COLLECTION = "users";

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public AuthRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    // ✅ Simple Callback interface (used by PersonalInformationActivity)
    public interface Callback {
        void onSuccess(String message);
        void onError(String error);
    }

    // ✅ Auth Callback (used by login/register)
    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String message);
    }

    // ✅ Profile Callback (returns full User object)
    public interface ProfileCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    // ✅ updateProfile — called from PersonalInformationActivity
    public void updateProfile(String fullName, String phone, String email, Callback callback) {
        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            callback.onError("User not logged in");
            return;
        }

        Map<String, Object> profileData = new HashMap<>();
        profileData.put("fullName", fullName);
        profileData.put("phone", phone);
        profileData.put("email", email);
        profileData.put("updatedAt", System.currentTimeMillis());

        db.collection(USERS_COLLECTION).document(user.getUid())
                .set(profileData, SetOptions.merge())
                .addOnSuccessListener(v -> callback.onSuccess("Profile updated"))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ✅ getProfile with simple Callback (called from PersonalInformationActivity)
    public void getProfile(String uid, Callback callback) {
        db.collection(USERS_COLLECTION).document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        callback.onSuccess("Profile loaded");
                    } else {
                        callback.onError("Profile not found");
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ✅ getProfile with ProfileCallback (returns full User object)
    public void getProfile(String uid, ProfileCallback callback) {
        db.collection(USERS_COLLECTION).document(uid).get()
                .addOnSuccessListener(doc -> {
                    User user = doc.toObject(User.class);
                    if (user != null) callback.onSuccess(user);
                    else callback.onError("Profile not found");
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ✅ register
    public void register(String fullName, String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser firebaseUser = result.getUser();
                    if (firebaseUser == null) { callback.onError("Registration failed"); return; }

                    User user = new User(firebaseUser.getUid(), fullName, email, "");
                    db.collection(USERS_COLLECTION)
                            .document(firebaseUser.getUid())
                            .set(user)
                            .addOnSuccessListener(v -> callback.onSuccess(firebaseUser))
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ✅ login
    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(result.getUser()))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    // ✅ logout
    public void logout() {
        auth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public void updateFcmToken(String uid, String token) {
        db.collection(USERS_COLLECTION).document(uid)
                .update("fcmToken", token);
    }
}