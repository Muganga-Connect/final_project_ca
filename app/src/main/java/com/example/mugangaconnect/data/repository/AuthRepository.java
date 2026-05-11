package com.example.mugangaconnect.data.repository;

import com.example.mugangaconnect.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.mugangaconnect.data.local.AppDatabase;
import com.example.mugangaconnect.data.local.UserDao;
import android.content.Context;
import java.util.HashMap;
import java.util.Map;

public class AuthRepository {

    private static final String USERS_COLLECTION = "users";

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;
    private final UserDao userDao;

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onError(String message);
    }

    public AuthRepository(Context context) {
        this.auth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
        this.userDao = AppDatabase.getInstance(context).userDao();
    }

    public void resetPassword(String email, ResetCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
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

    // ✅ Reset Password Callback
    public interface ResetCallback {
        void onSuccess();
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
                            .addOnSuccessListener(v -> {
                                new Thread(() -> userDao.upsert(user)).start();
                                callback.onSuccess(firebaseUser);
                            })
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

    public void getProfile(String uid, ProfileCallback callback) {
        // Try local Room first
        new Thread(() -> {
            User localUser = userDao.getByUid(uid);
            if (localUser != null) {
                callback.onSuccess(localUser);
            }
        }).start();

        // Always fetch from Firestore to sync
        db.collection(USERS_COLLECTION).document(uid).get()
                .addOnSuccessListener(doc -> {
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        new Thread(() -> userDao.upsert(user)).start();
                        callback.onSuccess(user);
                    }
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

    /** Update fullName and phone on the user's Firestore document. (Legacy) */
    public void updateProfile(String uid, String fullName, String phone, ProfileCallback callback) {
        db.collection(USERS_COLLECTION).document(uid)
                .update("fullName", fullName, "phone", phone)
                .addOnSuccessListener(v -> {
                    User updated = new User(uid, fullName, null, phone);
                    callback.onSuccess(updated);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    /** Update full user profile in Firestore. */
    public void updateFullProfile(User user, ProfileCallback callback) {
        db.collection(USERS_COLLECTION).document(user.getUid())
                .set(user)
                .addOnSuccessListener(v -> {
                    new Thread(() -> userDao.upsert(user)).start();
                    callback.onSuccess(user);
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