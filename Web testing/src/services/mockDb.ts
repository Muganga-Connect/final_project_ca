import { collection, getDocs, getDoc, doc, updateDoc, query, where, orderBy, limit } from 'firebase/firestore';
import { db } from './firebase';
import { Doctor, Patient, Appointment, AppointmentStatus, Hospital } from '../types';

class FirebaseDbService {
  // Hospitals
  async getHospitals(): Promise<Hospital[]> {
    try {
      const querySnapshot = await getDocs(collection(db, 'hospitals'));
      return querySnapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      })) as Hospital[];
    } catch (error) {
      console.error("Error fetching hospitals:", error);
      return [];
    }
  }

  // Doctor Auth Simulation
  async login(email: string, hospitalId: string): Promise<Doctor | null> {
    try {
      const q = query(
        collection(db, 'doctors'),
        where('email', '==', email),
        where('hospitalId', '==', hospitalId),
        limit(1)
      );
      const querySnapshot = await getDocs(q);

      if (querySnapshot.empty) return null;

      const docData = querySnapshot.docs[0].data();

      // Fetch Hospital details
      const hospRef = doc(db, 'hospitals', hospitalId);
      const hospSnap = await getDoc(hospRef);
      const hospitalName = hospSnap.exists() ? hospSnap.data().name : 'Unknown Hospital';

      return {
        id: querySnapshot.docs[0].id,
        ...docData,
        hospitalName
      } as Doctor;
    } catch (error) {
      console.error("Error logging in:", error);
      return null;
    }
  }

  // Patients
  async getPatients(): Promise<Patient[]> {
    try {
      const querySnapshot = await getDocs(collection(db, 'patients'));
      return querySnapshot.docs.map(d => ({
        ...d.data(),
        id: d.id,
        name: d.data().name || 'Unknown Patient'
      })) as Patient[];
    } catch (error) {
      console.error("Error fetching patients:", error);
      return [];
    }
  }

  async getPatientById(id: string): Promise<Patient | null> {
    try {
      const docRef = doc(db, 'patients', id);
      const docSnap = await getDoc(docRef);

      if (docSnap.exists()) {
        return { id: docSnap.id, ...docSnap.data() } as Patient;
      }
      return null;
    } catch (error) {
      console.error("Error fetching patient:", error);
      return null;
    }
  }

  // Appointments
  async getAppointments(doctorId?: string): Promise<Appointment[]> {
    try {
      console.log("Fetching appointments from Firestore...");
      // For testing: Fetch ALL appointments to verify connection
      // Once verified, we can re-enable the: if (doctorId) query
      const q = query(collection(db, 'appointments'));
      const querySnapshot = await getDocs(q);

      console.log(`Found ${querySnapshot.size} appointments`);

      const apps = querySnapshot.docs.map(d => {
        const data = d.data();
        return {
          ...data,
          id: d.id, // Ensure Firestore ID is always used
          patientName: data.patientName || data.patientId || 'Anonymous Patient',
          date: data.date || 'No Date',
          time: data.time || 'No Time',
          status: data.status || 'pending'
        };
      }) as Appointment[];

      return apps.sort((a, b) => (b.date || '').localeCompare(a.date || ''));
    } catch (error) {
      console.error("Error fetching appointments:", error);
      return [];
    }
  }

  async getPatientAppointments(patientId: string, doctorId?: string): Promise<Appointment[]> {
    try {
      let q = query(
        collection(db, 'appointments'),
        where('patientId', '==', patientId)
      );
      if (doctorId) {
        q = query(
          collection(db, 'appointments'),
          where('patientId', '==', patientId),
          where('doctorId', '==', doctorId)
        );
      }
      const querySnapshot = await getDocs(q);
      const apps = querySnapshot.docs.map(doc => ({
        id: doc.id,
        ...doc.data()
      })) as Appointment[];

      // Sort in memory to avoid index requirements
      return apps.sort((a, b) => b.date.localeCompare(a.date));
    } catch (error) {
      console.error("Error fetching patient appointments:", error);
      return [];
    }
  }

  async updateAppointmentStatus(id: string, status: AppointmentStatus): Promise<void> {
    try {
      const docRef = doc(db, 'appointments', id);
      await updateDoc(docRef, { status });
    } catch (error) {
      console.error("Error updating appointment status:", error);
      throw error;
    }
  }

  // Stats for Dashboard
  async getStats(doctorId?: string) {
    try {
      // Fetch ALL for verification
      const q = query(collection(db, 'appointments'));
      const querySnapshot = await getDocs(q);
      const appointments = querySnapshot.docs.map(doc => doc.data() as Appointment);

      const total = appointments.length;
      const pending = appointments.filter(a => a.status === 'pending').length;
      const confirmed = appointments.filter(a => a.status === 'confirmed').length;
      const completed = appointments.filter(a => a.status === 'completed').length;

      return { total, pending, confirmed, completed };
    } catch (error) {
      console.error("Error fetching stats:", error);
      return { total: 0, pending: 0, confirmed: 0, completed: 0 };
    }
  }
}

export const mockDb = new FirebaseDbService();
