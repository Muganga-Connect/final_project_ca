import { collection, addDoc, setDoc, doc } from 'firebase/firestore';
import { db } from './firebase';

export async function seedDatabase() {
  console.log("Seeding database...");

  // Seed Hospitals
  const hospitals = [
    { id: 'hosp_001', name: 'King Faisal Hospital', location: 'Kigali, Rwanda' },
    { id: 'hosp_002', name: 'CHUK Hospital', location: 'Kigali, Rwanda' },
    { id: 'hosp_003', name: 'Legacy Clinics', location: 'Kigali, Rwanda' }
  ];

  for (const hospital of hospitals) {
    await setDoc(doc(db, 'hospitals', hospital.id), hospital);
  }

  // Seed Doctors
  const doctors = [
    { id: 'doc1', name: 'Dr. Jean Mukasa', email: 'jean@muganga.com', specialty: 'General Practice', hospitalId: 'hosp_001' },
    { id: 'doc2', name: 'Dr. Sarah Smith', email: 'sarah@muganga.com', specialty: 'Pediatrics', hospitalId: 'hosp_002' }
  ];

  for (const doctor of doctors) {
    await setDoc(doc(db, 'doctors', doctor.id), doctor);
  }

  console.log("Database seeded successfully!");
}
