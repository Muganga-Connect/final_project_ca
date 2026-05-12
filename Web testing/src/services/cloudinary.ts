/**
 * Cloudinary Upload Service for MugangaConnect+ Web Portal
 * 
 * Uses the same Cloudinary account as the Android app to ensure
 * profile images are accessible across both platforms.
 */

const CLOUD_NAME = 'dn4vlox7r';
const API_KEY = '554832713174717';
const API_SECRET = 'S2Ka3w7VykbDfzjQnzQIFQmixlY';
const UPLOAD_URL = `https://api.cloudinary.com/v1_1/${CLOUD_NAME}/image/upload`;

// Folder names matching Android app's CloudinaryConfig
const PROFILE_IMAGES_FOLDER = 'profile_images';

/**
 * Generate a SHA-1 signature for Cloudinary signed uploads.
 * Uses the Web Crypto API available in all modern browsers.
 */
async function generateSignature(paramsToSign: Record<string, string>): Promise<string> {
  // Sort parameters alphabetically and create the string to sign
  const sortedKeys = Object.keys(paramsToSign).sort();
  const signatureString = sortedKeys
    .map(key => `${key}=${paramsToSign[key]}`)
    .join('&') + API_SECRET;

  // Generate SHA-1 hash using Web Crypto API
  const encoder = new TextEncoder();
  const data = encoder.encode(signatureString);
  const hashBuffer = await crypto.subtle.digest('SHA-1', data);
  
  // Convert hash to hex string
  const hashArray = Array.from(new Uint8Array(hashBuffer));
  return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
}

/**
 * Upload an image file to Cloudinary.
 * Returns the secure URL of the uploaded image.
 */
export async function uploadToCloudinary(file: File, folder: string = PROFILE_IMAGES_FOLDER): Promise<string> {
  const timestamp = Math.round(Date.now() / 1000).toString();

  // Parameters that need to be signed (alphabetical order matters)
  const paramsToSign: Record<string, string> = {
    folder,
    timestamp,
  };

  const signature = await generateSignature(paramsToSign);

  // Build the upload form data
  const formData = new FormData();
  formData.append('file', file);
  formData.append('folder', folder);
  formData.append('timestamp', timestamp);
  formData.append('api_key', API_KEY);
  formData.append('signature', signature);

  const response = await fetch(UPLOAD_URL, {
    method: 'POST',
    body: formData,
  });

  if (!response.ok) {
    const errorData = await response.json();
    console.error('Cloudinary upload error:', errorData);
    throw new Error(`Cloudinary upload failed: ${errorData.error?.message || response.statusText}`);
  }

  const result = await response.json();
  console.log('Cloudinary upload successful:', result.secure_url);
  return result.secure_url;
}
