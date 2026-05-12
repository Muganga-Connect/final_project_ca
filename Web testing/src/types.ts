/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

export type AppointmentStatus = 'pending' | 'confirmed' | 'completed' | 'cancelled' | 'missed';

export interface Hospital {
  id: string;
  name: string;
  location: string;
  image?: string;
}

export interface Doctor {
  id: string;
  name: string;
  email: string;
  hospitalId: string;
  hospitalName?: string;
  specialty?: string;
}

export interface Patient {
  id: string;
  name: string;
  email: string;
  phone: string;
  biometricEnabled: boolean;
  age?: number;
  gender?: string;
  bloodType?: string;
}

export interface Appointment {
  id: string;
  patientId: string;
  patientName: string;
  doctorId: string;
  date: string; // YYYY-MM-DD
  time: string; // HH:mm
  status: AppointmentStatus;
  reason?: string;
}

export interface AuthState {
  user: Doctor | null;
  isLoading: boolean;
}
