package com.example.mugangaconnect.data.repository;

import com.example.mugangaconnect.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AuthRepository {

    private static final String USERS_COLLECTION = "users";

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String message);
    }

    public AuthRepository() {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

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

    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(result.getUser()))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void logout() {
        auth.signOut();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public void getProfile(String uid, ProfileCallback callback) {
        db.collection(USERS_COLLECTION).document(uid).get()
                .addOnSuccessListener(doc -> {
                    User user = doc.toObject(User.class);
                    if (user != null) callback.onSuccess(user);
                    else callback.onError("Profile not found");
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateFcmToken(String uid, String token) {
        db.collection(USERS_COLLECTION).document(uid)
                .update("fcmToken", token);
    }

    /** Send a password-reset email to the given address. */
    public void resetPassword(String email, ResetCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /** Update fullName and phone on the user's Firestore document. */
    public void updateProfile(String uid, String fullName, String phone, ProfileCallback callback) {
        db.collection(USERS_COLLECTION).document(uid)
                .update("fullName", fullName, "phone", phone)
                .addOnSuccessListener(v -> {
                    User updated = new User(uid, fullName, null, phone);
                    callback.onSuccess(updated);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public interface ProfileCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public interface ResetCallback {
        void onSuccess();
        void onError(String message);
    }
}