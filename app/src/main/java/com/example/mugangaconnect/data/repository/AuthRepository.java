package com.example.mugangaconnect.data.repository;

import android.util.Log;

import com.example.mugangaconnect.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Map;

public class AuthRepository {

    private static final String USERS_COLLECTION = "users";
    private static final String TAG = "AuthRepository";

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
                .update("fcmToken", token)
                .addOnSuccessListener(v -> Log.d(TAG, "Updated FCM token for uid=" + uid))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update FCM token for uid=" + uid, e));
    }

    /** Send a password-reset email to the given address. */
    public void resetPassword(String email, ResetCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /** Compatibility overload that uses AuthCallback as requested. */
    public void resetPassword(String email, AuthCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(v -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /** Update fullName and phone on the user's Firestore document. */
    public void updateProfile(String uid, String fullName, String phone, ProfileCallback callback) {
        db.collection(USERS_COLLECTION).document(uid)
                .update("fullName", fullName, "phone", phone)
                .addOnSuccessListener(v -> db.collection(USERS_COLLECTION).document(uid).get()
                        .addOnSuccessListener(doc -> {
                            String email = doc.getString("email");
                            callback.onSuccess(new User(uid, fullName, email, phone));
                        })
                        .addOnFailureListener(e -> callback.onError(e.getMessage())))
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

    public interface PersonalInfoCallback {
        void onSuccess(Map<String, Object> personalInfo);
        void onError(String message);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String message);
    }

    public void updatePersonalInformation(String uid, Map<String, Object> personalInfo, SimpleCallback callback) {
        db.collection(USERS_COLLECTION).document(uid)
                .set(personalInfo, SetOptions.merge())
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void getPersonalInformation(String uid, PersonalInfoCallback callback) {
        db.collection(USERS_COLLECTION).document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (!doc.exists()) {
                        callback.onError("Profile not found");
                        return;
                    }
                    callback.onSuccess(doc.getData());
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}
