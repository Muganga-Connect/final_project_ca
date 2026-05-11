package com.example.mugangaconnect.data.repository;

import com.example.mugangaconnect.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
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

    public interface ProfileCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public interface ResetCallback {
        void onSuccess();
        void onError(String message);
    }

    public interface Callback {
        void onSuccess(String message);
        void onError(String error);
    }

    public AuthRepository(Context context) {
        this.auth   = FirebaseAuth.getInstance();
        this.db     = FirebaseFirestore.getInstance();
        this.userDao = AppDatabase.getInstance(context).userDao();
    }

    public AuthRepository() {
        this.auth    = FirebaseAuth.getInstance();
        this.db      = FirebaseFirestore.getInstance();
        this.userDao = null;
    }

    public void register(String fullName, String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser firebaseUser = result.getUser();
                    if (firebaseUser == null) { callback.onError("Registration failed"); return; }
                    User user = new User(firebaseUser.getUid(), fullName, email, "");
                    db.collection(USERS_COLLECTION).document(firebaseUser.getUid())
                            .set(user)
                            .addOnSuccessListener(v -> {
                                if (userDao != null) new Thread(() -> userDao.upsert(user)).start();
                                callback.onSuccess(firebaseUser);
                            })
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void login(String email, String password, AuthCallback callback) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> callback.onSuccess(result.getUser()))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void logout() { auth.signOut(); }

    public FirebaseUser getCurrentUser() { return auth.getCurrentUser(); }

    public boolean isLoggedIn() { return auth.getCurrentUser() != null; }

    public void getProfile(String uid, ProfileCallback callback) {
        if (userDao != null) {
            new Thread(() -> {
                User localUser = userDao.getByUid(uid);
                if (localUser != null) callback.onSuccess(localUser);
            }).start();
        }
        db.collection(USERS_COLLECTION).document(uid).get()
                .addOnSuccessListener(doc -> {
                    User user = doc.toObject(User.class);
                    if (user != null) {
                        if (userDao != null) new Thread(() -> userDao.upsert(user)).start();
                        callback.onSuccess(user);
                    } else {
                        callback.onError("Profile not found");
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void resetPassword(String email, ResetCallback callback) {
        auth.sendPasswordResetEmail(email)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateProfile(String uid, String fullName, String phone, ProfileCallback callback) {
        db.collection(USERS_COLLECTION).document(uid)
                .update("fullName", fullName, "phone", phone)
                .addOnSuccessListener(v -> callback.onSuccess(new User(uid, fullName, null, phone)))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateProfile(String fullName, String phone, String email, Callback callback) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) { callback.onError("User not logged in"); return; }
        Map<String, Object> data = new HashMap<>();
        data.put("fullName", fullName);
        data.put("phone", phone);
        data.put("email", email);
        data.put("updatedAt", System.currentTimeMillis());
        db.collection(USERS_COLLECTION).document(user.getUid())
                .set(data, SetOptions.merge())
                .addOnSuccessListener(v -> callback.onSuccess("Profile updated"))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateFullProfile(User user, ProfileCallback callback) {
        db.collection(USERS_COLLECTION).document(user.getUid())
                .set(user)
                .addOnSuccessListener(v -> {
                    if (userDao != null) new Thread(() -> userDao.upsert(user)).start();
                    callback.onSuccess(user);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void updateFcmToken(String uid, String token) {
        db.collection(USERS_COLLECTION).document(uid).update("fcmToken", token);
    }
}
