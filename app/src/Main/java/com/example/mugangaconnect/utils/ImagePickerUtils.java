package com.example.mugangaconnect.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImagePickerUtils {
    private static final String TAG = "ImagePickerUtils";
    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    
    public interface ImagePickerCallback {
        void onImageSelected(Uri imageUri);
        void onError(String error);
    }
    
    public static void requestImagePermissions(Activity activity) {
        String[] permissions;
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_MEDIA_IMAGES
            };
        } else {
            permissions = new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
        
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE_PERMISSIONS);
    }
    
    public static boolean hasImagePermissions(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                   ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                   ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                   ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    public static void showImagePickerDialog(Fragment fragment, ImagePickerCallback callback) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(fragment.requireContext());
        builder.setTitle("Select Image");
        
        String[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        
        builder.setItems(options, (dialog, which) -> {
            switch (which) {
                case 0:
                    // Take Photo
                    if (hasImagePermissions(fragment.requireContext())) {
                        dispatchTakePictureIntent(fragment, callback);
                    } else {
                        requestImagePermissions(fragment.requireActivity());
                        Toast.makeText(fragment.requireContext(), "Please grant camera permissions", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    // Choose from Gallery
                    if (hasImagePermissions(fragment.requireContext())) {
                        dispatchPickFromGalleryIntent(fragment, callback);
                    } else {
                        requestImagePermissions(fragment.requireActivity());
                        Toast.makeText(fragment.requireContext(), "Please grant storage permissions", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    // Cancel
                    dialog.dismiss();
                    break;
            }
        });
        
        builder.show();
    }
    
    private static void dispatchTakePictureIntent(Fragment fragment, ImagePickerCallback callback) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        if (takePictureIntent.resolveActivity(fragment.requireContext().getPackageManager()) != null) {
            try {
                Uri photoUri = createImageFile(fragment.requireContext());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                
                fragment.startActivityForResult(takePictureIntent, 1002);
                
                // Store the photo URI and callback for onActivityResult
                if (fragment instanceof ImagePickerResultHandler) {
                    ((ImagePickerResultHandler) fragment).setPendingImageCallback(callback);
                }
                
            } catch (IOException ex) {
                Log.e(TAG, "Error creating image file", ex);
                callback.onError("Failed to create image file");
            }
        } else {
            callback.onError("No camera app available");
        }
    }
    
    private static void dispatchPickFromGalleryIntent(Fragment fragment, ImagePickerCallback callback) {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhotoIntent.setType("image/*");
        
        if (pickPhotoIntent.resolveActivity(fragment.requireContext().getPackageManager()) != null) {
            fragment.startActivityForResult(Intent.createChooser(pickPhotoIntent, "Select Picture"), 1003);
            
            // Store the callback for onActivityResult
            if (fragment instanceof ImagePickerResultHandler) {
                ((ImagePickerResultHandler) fragment).setPendingImageCallback(callback);
            }
        } else {
            callback.onError("No gallery app available");
        }
    }
    
    private static Uri createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);
        
        return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }
    
    public interface ImagePickerResultHandler {
        void setPendingImageCallback(ImagePickerCallback callback);
        ImagePickerCallback getPendingImageCallback();
    }
    
    public static void handleActivityResult(Fragment fragment, int requestCode, int resultCode, Intent data) {
        if (fragment instanceof ImagePickerResultHandler) {
            ImagePickerCallback callback = ((ImagePickerResultHandler) fragment).getPendingImageCallback();
            
            if (callback != null) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = null;
                    
                    if (requestCode == 1002) {
                        // Camera photo - the URI is stored in the pending callback or should be handled differently
                        // For now, we'll get the last image from gallery
                        imageUri = getLastImageUri(fragment.requireContext());
                    } else if (requestCode == 1003) {
                        // Gallery photo
                        if (data != null && data.getData() != null) {
                            imageUri = data.getData();
                        }
                    }
                    
                    if (imageUri != null) {
                        callback.onImageSelected(imageUri);
                    } else {
                        callback.onError("Failed to get image URI");
                    }
                } else {
                    callback.onError("Image selection cancelled");
                }
                
                // Clear the pending callback
                ((ImagePickerResultHandler) fragment).setPendingImageCallback(null);
            }
        }
    }
    
    private static Uri getLastImageUri(Context context) {
        try {
            String[] projection = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED};
            String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";
            
            android.database.Cursor cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder
            );
            
            if (cursor != null && cursor.moveToFirst()) {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
                cursor.close();
                return uri;
            }
            
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting last image URI", e);
        }
        
        return null;
    }
}
