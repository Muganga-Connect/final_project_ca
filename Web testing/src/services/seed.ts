import { collection, addDoc, setDoc, doc } from 'firebase/firestore';
import { db } from './firebase';

export async function seedDatabase() {
  console.log("Seeding database...");

  // Seed Doctors
  const doctors = [
    { id: 'doc1', name: 'Dr. Jean Mukasa', email: 'jean@muganga.com', specialty: 'General Practice' },
    { id: 'doc2', name: 'Dr. Sarah Smith', email: 'sarah@muganga.com', specialty: 'Pediatrics' }
  ];

  for (const doctor of doctors) {
    await setDoc(doc(db, 'doctors', doctor.id), doctor);
  }


  console.log("Database seeded successfully!");
}
