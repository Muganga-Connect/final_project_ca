package com.example.mugangaconnect.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.mugangaconnect.config.CloudinaryConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageUploadUtils {
    private static final String TAG = "ImageUploadUtils";
    
    private final Cloudinary cloudinary;
    private final ExecutorService executor;
    private final FirebaseFirestore firestore;
    private final FirebaseAuth auth;
    
    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String error);
    }
    
    public ImageUploadUtils(Context context) {
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", CloudinaryConfig.CLOUD_NAME);
        config.put("api_key", CloudinaryConfig.API_KEY);
        config.put("api_secret", CloudinaryConfig.API_SECRET);
        config.put("secure", true);
        
        this.cloudinary = new Cloudinary(config);
        this.executor = Executors.newSingleThreadExecutor();
        this.firestore = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
    }
    
    public void uploadImageToCloudinary(Uri imageUri, String folder, UploadCallback callback) {
        executor.execute(() -> {
            try {
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser == null) {
                    callback.onError("User not authenticated");
                    return;
                }
                
                String publicId = folder + "/" + currentUser.getUid() + "/" + UUID.randomUUID().toString();
                
                Map<String, Object> uploadParams = new HashMap<>();
                uploadParams.put("public_id", publicId);
                uploadParams.put("resource_type", "auto");
                uploadParams.put("folder", folder);
                uploadParams.put("overwrite", false);
                uploadParams.put("invalidate", true);
                
                // Upload to Cloudinary
                Map<String, Object> uploadResult = cloudinary.uploader().upload(imageUri, uploadParams);
                String imageUrl = (String) uploadResult.get("secure_url");
                
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    // Save image URL to Firestore
                    saveImageUrlToFirestore(currentUser.getUid(), imageUrl, folder, callback);
                } else {
                    callback.onError("Failed to get image URL from Cloudinary");
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error uploading image to Cloudinary", e);
                callback.onError("Upload failed: " + e.getMessage());
            }
        });
    }
    
    private void saveImageUrlToFirestore(String userId, String imageUrl, String folder, UploadCallback callback) {
        Map<String, Object> imageData = new HashMap<>();
        imageData.put("imageUrl", imageUrl);
        imageData.put("folder", folder);
        imageData.put("uploadedAt", System.currentTimeMillis());
        imageData.put("userId", userId);
        
        firestore.collection("user_images")
                .add(imageData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Image URL saved to Firestore");
                    callback.onSuccess(imageUrl);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error saving image URL to Firestore", e);
                    // Even if Firestore fails, the image is uploaded to Cloudinary
                    callback.onSuccess(imageUrl);
                });
    }
    
    public void uploadProfileImage(Uri imageUri, UploadCallback callback) {
        uploadImageToCloudinary(imageUri, CloudinaryConfig.PROFILE_IMAGES_FOLDER, callback);
    }
    
    public void uploadAIAssistantImage(Uri imageUri, UploadCallback callback) {
        uploadImageToCloudinary(imageUri, CloudinaryConfig.AI_ASSISTANT_IMAGES_FOLDER, callback);
    }
    
    public void deleteImageFromCloudinary(String publicId, UploadCallback callback) {
        executor.execute(() -> {
            try {
                Map<String, Object> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                String resultStr = (String) result.get("result");
                
                if ("ok".equals(resultStr)) {
                    callback.onSuccess("Image deleted successfully");
                } else {
                    callback.onError("Failed to delete image");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error deleting image from Cloudinary", e);
                callback.onError("Delete failed: " + e.getMessage());
            }
        });
    }
}
