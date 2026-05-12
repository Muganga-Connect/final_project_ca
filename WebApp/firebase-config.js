// TODO: Add your Firebase configuration here
// Ask your Firebase project admin for these values
// Firebase Console → Project Settings → Your apps → Web app → firebaseConfig

const firebaseConfig = {
  apiKey: "",
  authDomain: "",
  projectId: "",
  storageBucket: "",
  messagingSenderId: "",
  appId: ""
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);
const auth = firebase.auth();
const db = firebase.firestore();
