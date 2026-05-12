/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { Doctor, Patient, Appointment, AppointmentStatus } from '../types';

const STORAGE_KEY = 'mugangaconnect_db';

interface DB {
  doctors: Doctor[];
  patients: Patient[];
  appointments: Appointment[];
}

const INITIAL_DB: DB = {
  doctors: [
    { id: 'doc1', name: 'Dr. Jean Mukasa', email: 'jean@muganga.com' },
    { id: 'doc2', name: 'Dr. Sarah Smith', email: 'sarah@muganga.com' }
  ],
  patients: [
    { id: 'p1', name: 'John Doe', email: 'john@example.com', phone: '+250 788 123 456', biometricEnabled: true, age: 34, gender: 'Male', bloodType: 'O+' },
    { id: 'p2', name: 'Jane Gakuba', email: 'jane@example.com', phone: '+250 789 654 321', biometricEnabled: false, age: 28, gender: 'Female', bloodType: 'A-' },
    { id: 'p3', name: 'Robert Habimana', email: 'robert@test.com', phone: '+250 782 000 111', biometricEnabled: true, age: 45, gender: 'Male', bloodType: 'B+' },
    { id: 'p4', name: 'Alice Mutoni', email: 'alice@test.com', phone: '+250 783 222 333', biometricEnabled: true, age: 31, gender: 'Female', bloodType: 'AB+' }
  ],
  appointments: [
    { id: 'a1', patientId: 'p1', patientName: 'John Doe', date: '2026-05-15', time: '10:00', status: 'confirmed', reason: 'Annual physical checkup' },
    { id: 'a2', patientId: 'p2', patientName: 'Jane Gakuba', date: '2026-05-12', time: '14:30', status: 'pending', reason: 'Flu symptoms' },
    { id: 'a3', patientId: 'p3', patientName: 'Robert Habimana', date: '2026-05-13', time: '09:00', status: 'pending', reason: 'Follow-up on blood pressure' },
    { id: 'a4', patientId: 'p1', patientName: 'John Doe', date: '2026-04-20', time: '11:00', status: 'completed', reason: 'Sprained ankle' },
    { id: 'a5', patientId: 'p4', patientName: 'Alice Mutoni', date: '2026-05-14', time: '16:00', status: 'confirmed', reason: 'Prenatal consult' }
  ]
};

class MockDbService {
  private db: DB;

  constructor() {
    const stored = localStorage.getItem(STORAGE_KEY);
    if (stored) {
      this.db = JSON.parse(stored);
    } else {
      this.db = INITIAL_DB;
      this.save();
    }
  }

  private save() {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(this.db));
  }

  // Doctor Auth Simulation
  async login(email: string): Promise<Doctor | null> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const doctor = this.db.doctors.find(d => d.email === email);
        resolve(doctor || null);
      }, 500);
    });
  }

  // Patients
  async getPatients(): Promise<Patient[]> {
    return this.db.patients;
  }

  async getPatientById(id: string): Promise<Patient | null> {
    return this.db.patients.find(p => p.id === id) || null;
  }

  // Appointments
  async getAppointments(): Promise<Appointment[]> {
    return this.db.appointments;
  }

  async getPatientAppointments(patientId: string): Promise<Appointment[]> {
    return this.db.appointments.filter(a => a.patientId === patientId);
  }

  async updateAppointmentStatus(id: string, status: AppointmentStatus): Promise<void> {
    const index = this.db.appointments.findIndex(a => a.id === id);
    if (index !== -1) {
      this.db.appointments[index].status = status;
      this.save();
    }
  }

  // Stats for Dashboard
  async getStats() {
    const total = this.db.appointments.length;
    const pending = this.db.appointments.filter(a => a.status === 'pending').length;
    const confirmed = this.db.appointments.filter(a => a.status === 'confirmed').length;
    const completed = this.db.appointments.filter(a => a.status === 'completed').length;

    return { total, pending, confirmed, completed };
  }
}

export const mockDb = new MockDbService();
