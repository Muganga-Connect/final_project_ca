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
    { 
      id: 'doc1', 
      name: 'Dr. Jean Mukasa', 
      email: 'jean@muganga.com', 
      specialty: 'General Practice', 
      department: 'General', 
      hospitalId: 'hosp_001',
      rating: 4.8,
      profileImage: 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?auto=format&fit=crop&w=256&h=256',
      availability: {
        days: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'],
        startTime: '08:00',
        endTime: '17:00',
        slotDuration: 30
      }
    },
    { 
      id: 'doc2', 
      name: 'Dr. Sarah Smith', 
      email: 'sarah@muganga.com', 
      specialty: 'Pediatrics', 
      department: 'Pediatrics', 
      hospitalId: 'hosp_002',
      rating: 4.9,
      profileImage: 'https://images.unsplash.com/photo-1594824476967-48c8b964273f?auto=format&fit=crop&w=256&h=256',
      availability: {
        days: ['Monday', 'Wednesday', 'Friday'],
        startTime: '09:00',
        endTime: '16:00',
        slotDuration: 60
      }
    },
    { 
      id: 'doc3', 
      name: 'Dr. David Kagame', 
      email: 'david@muganga.com', 
      specialty: 'Cardiology', 
      department: 'General', 
      hospitalId: 'hosp_003',
      rating: 4.7,
      profileImage: 'https://images.unsplash.com/photo-1537368910025-700350fe46c7?auto=format&fit=crop&w=256&h=256',
      availability: {
        days: ['Tuesday', 'Thursday', 'Saturday'],
        startTime: '10:00',
        endTime: '18:00',
        slotDuration: 45
      }
    }
  ];

  for (const doctor of doctors) {
    await setDoc(doc(db, 'doctors', doctor.id), doctor);
  }

  console.log("Database seeded successfully!");
}
