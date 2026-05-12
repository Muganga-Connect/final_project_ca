import { initializeApp } from 'firebase/app';
import { getFirestore } from 'firebase/firestore';
import { getAuth } from 'firebase/auth';

const firebaseConfig = {
  apiKey: "AIzaSyAw1KLVc4Ye7gm0MdrshvAj5n6SwMHluOw",
  authDomain: "mugangaconnect-fe6e8.firebaseapp.com",
  projectId: "mugangaconnect-fe6e8",
  storageBucket: "mugangaconnect-fe6e8.firebasestorage.app",
  messagingSenderId: "788599600402",
  appId: "1:788599600402:web:0319db97d3854ea059efcb" // Using android ID as placeholder or assuming it might work/user will update
};

const app = initializeApp(firebaseConfig);
export const db = getFirestore(app);
export const auth = getAuth(app);
