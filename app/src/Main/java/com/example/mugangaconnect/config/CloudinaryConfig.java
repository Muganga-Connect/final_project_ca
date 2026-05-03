package com.example.mugangaconnect.config;

public class CloudinaryConfig {
    // Replace these with your actual Cloudinary credentials
    public static final String CLOUD_NAME = "dn4vlox7r";
    public static final String API_KEY = "554832713174717";
    public static final String API_SECRET = "S2Ka3w7VykbDfzjQnzQIFQmixlY";
    
    // Folder names for organizing images
    public static final String PROFILE_IMAGES_FOLDER = "profile_images";
    public static final String AI_ASSISTANT_IMAGES_FOLDER = "ai_assistant_images";
    
    // Image upload settings
    public static final int MAX_IMAGE_SIZE_MB = 10;
    public static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/webp"
    };
}
