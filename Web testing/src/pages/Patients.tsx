/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import { mockDb } from '../services/mockDb';
import { Patient, Appointment, Doctor } from '../types';
import { 
  Search, 
  User, 
  Phone, 
  Mail, 
  Fingerprint, 
  X, 
  CalendarDays,
  FileText,
  ChevronRight,
  Droplets,
  Activity
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';

export default function Patients() {
  const { user } = useOutletContext<{ user: Doctor }>();
  const [patients, setPatients] = useState<Patient[]>([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);
  const [patientAppointments, setPatientAppointments] = useState<Appointment[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      const allPatients = await mockDb.getPatients();
      setPatients(allPatients);
      setIsLoading(false);
    }
    loadData();
  }, []);

  const handlePatientClick = async (patient: Patient) => {
    setSelectedPatient(patient);
    const appointments = await mockDb.getPatientAppointments(patient.id, user.id);
    setPatientAppointments(appointments.sort((a,b) => b.date.localeCompare(a.date)));
  };

  const filteredPatients = patients.filter(p => 
    p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    p.email.toLowerCase().includes(searchQuery.toLowerCase()) ||
    p.phone.includes(searchQuery)
  );

  return (
    <div className="space-y-6 max-w-7xl mx-auto pb-10">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h2 className="text-2xl font-bold text-slate-900 leading-tight">Patient Directory</h2>
          <p className="text-sm text-slate-500">Access patient records and history</p>
        </div>
        
        <div className="relative w-full md:w-96">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
          <input
            type="text"
            placeholder="Search by name, email or phone..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-12 pr-4 py-3 bg-white border border-slate-200 rounded-2xl shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500/10 focus:border-indigo-500 transition-all text-sm"
          />
        </div>
      </div>

      {/* Grid */}
      <motion.div 
        layout
        className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6"
      >
        <AnimatePresence mode="popLayout">
          {filteredPatients.map((patient) => (
            <motion.div
              layout
              key={patient.id}
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.95 }}
              onClick={() => handlePatientClick(patient)}
              className="bg-white p-6 rounded-[2rem] border border-slate-100 shadow-sm hover:shadow-xl transition-all cursor-pointer group hover:-translate-y-1 flex flex-col items-center text-center gap-4 relative overflow-hidden"
            >
              <div className="absolute top-4 right-4 text-emerald-500 bg-emerald-50 p-1.5 rounded-full opacity-0 group-hover:opacity-100 transition-opacity">
                 {patient.biometricEnabled && <Fingerprint size={14} />}
              </div>
              
              <div className="w-20 h-20 rounded-3xl bg-indigo-50 text-indigo-600 flex items-center justify-center font-bold text-2xl shadow-inner transform group-hover:rotate-6 transition-transform">
                {patient.name.charAt(0)}
              </div>
              
              <div className="w-full">
                <h3 className="font-bold text-slate-900 group-hover:text-indigo-600 transition-colors">{patient.name}</h3>
                <p className="text-xs text-slate-400 font-medium mt-1">ID: {patient.id.toUpperCase()}</p>
              </div>

              <div className="w-full pt-4 border-t border-slate-50 space-y-2">
                <div className="flex items-center gap-2 text-xs text-slate-600 justify-center">
                  <Phone size={12} className="text-slate-400" />
                  {patient.phone}
                </div>
                <div className="flex items-center gap-2 text-xs text-slate-600 justify-center">
                  <Mail size={12} className="text-slate-400" />
                  {patient.email}
                </div>
              </div>
              
              <div className="w-full bg-slate-50 py-2 rounded-xl text-[10px] font-bold text-slate-400 uppercase tracking-widest mt-2 group-hover:bg-indigo-600 group-hover:text-white transition-colors">
                View Profile
              </div>
            </motion.div>
          ))}
        </AnimatePresence>
      </motion.div>

      {/* Patient Detail Modal */}
      <AnimatePresence>
        {selectedPatient && (
          <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
            <motion.div 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setSelectedPatient(null)}
              className="absolute inset-0 bg-slate-900/40 backdrop-blur-sm"
            />
            
            <motion.div 
              initial={{ opacity: 0, scale: 0.9, y: 20 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.9, y: 20 }}
              className="relative w-full max-w-4xl bg-white rounded-[2.5rem] shadow-2xl overflow-hidden flex flex-col md:flex-row max-h-[90vh]"
            >
              {/* Sidebar Info */}
              <div className="w-full md:w-80 bg-slate-50 p-8 border-r border-slate-100 flex flex-col items-center">
                <motion.div 
                   layoutId={`avatar-${selectedPatient.id}`}
                   className="w-32 h-32 rounded-[2.5rem] bg-indigo-600 text-white flex items-center justify-center text-5xl font-bold shadow-2xl mb-6 shadow-indigo-200"
                >
                  {selectedPatient.name.charAt(0)}
                </motion.div>
                
                <h2 className="text-2xl font-bold text-slate-900 text-center">{selectedPatient.name}</h2>
                <div className="flex items-center gap-2 mt-2 px-3 py-1 bg-indigo-100 text-indigo-700 rounded-full text-[10px] font-black uppercase tracking-wider">
                   {selectedPatient.biometricEnabled ? 'Biometrics Active' : 'Biometric Disabled'}
                </div>

                <div className="w-full mt-8 space-y-4">
                  <div className="flex items-center gap-4 bg-white p-3 rounded-2xl shadow-sm border border-slate-100">
                    <Activity className="text-pink-500" size={20} />
                    <div>
                      <p className="text-[10px] uppercase font-bold text-slate-400">Age</p>
                      <p className="text-sm font-bold text-slate-800">{selectedPatient.age} Years</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-4 bg-white p-3 rounded-2xl shadow-sm border border-slate-100">
                    <Droplets className="text-red-500" size={20} />
                    <div>
                      <p className="text-[10px] uppercase font-bold text-slate-400">Blood Type</p>
                      <p className="text-sm font-bold text-slate-800">{selectedPatient.bloodType}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-4 bg-white p-3 rounded-2xl shadow-sm border border-slate-100">
                    <User className="text-indigo-500" size={20} />
                    <div>
                      <p className="text-[10px] uppercase font-bold text-slate-400">Gender</p>
                      <p className="text-sm font-bold text-slate-800">{selectedPatient.gender}</p>
                    </div>
                  </div>
                </div>

                <div className="flex flex-col w-full gap-2 mt-auto pt-8">
                   <div className="flex items-center gap-3 text-xs text-slate-500 px-1">
                      <Phone size={14} className="text-indigo-400" />
                      {selectedPatient.phone}
                   </div>
                   <div className="flex items-center gap-3 text-xs text-slate-500 px-1">
                      <Mail size={14} className="text-indigo-400" />
                      {selectedPatient.email}
                   </div>
                </div>
              </div>

              {/* Patient History */}
              <div className="flex-1 p-10 overflow-y-auto bg-white">
                <button 
                  onClick={() => setSelectedPatient(null)}
                  className="absolute top-6 right-6 p-2 hover:bg-slate-100 rounded-full text-slate-400 transition-colors"
                >
                  <X size={24} />
                </button>

                <div className="flex items-center gap-3 mb-8">
                   <div className="p-2.5 bg-indigo-600 text-white rounded-xl">
                      <FileText size={20} />
                   </div>
                   <h3 className="text-xl font-bold text-slate-800 tracking-tight">Appointment History</h3>
                </div>

                <div className="space-y-6 relative before:absolute before:left-5 before:top-2 before:bottom-2 before:w-px before:bg-slate-100">
                  {patientAppointments.length > 0 ? (
                    patientAppointments.map((app) => (
                      <div key={app.id} className="relative z-10 pl-12 flex flex-col sm:flex-row sm:items-center justify-between gap-4 group">
                        <div className="absolute left-3.5 top-2 w-3 h-3 rounded-full bg-white border-2 border-indigo-600 group-hover:scale-125 transition-transform" />
                        <div>
                          <div className="flex items-center gap-2 mb-1">
                             <CalendarDays size={14} className="text-slate-400" />
                             <span className="text-sm font-bold text-slate-900">{app.date}</span>
                             <span className="text-xs text-slate-400 font-medium ml-2">{app.time}</span>
                          </div>
                          <p className="text-sm text-slate-600 font-medium leading-relaxed">{app.reason}</p>
                        </div>
                        <div className="flex flex-col items-end gap-2 shrink-0">
                          <span className={`
                            px-3 py-1 rounded-full text-[9px] font-black uppercase tracking-wider border
                            ${app.status === 'completed' ? 'bg-emerald-50 text-emerald-700 border-emerald-100' : ''}
                            ${app.status === 'confirmed' ? 'bg-indigo-50 text-indigo-700 border-indigo-100' : ''}
                            ${app.status === 'pending' ? 'bg-amber-50 text-amber-700 border-amber-100' : ''}
                            ${app.status === 'cancelled' ? 'bg-red-50 text-red-700 border-red-100' : ''}
                          `}>
                            {app.status}
                          </span>
                          <button className="text-[10px] font-bold text-indigo-600 flex items-center gap-1 hover:underline">
                             Case File <ChevronRight size={10} />
                          </button>
                        </div>
                      </div>
                    ))
                  ) : (
                    <div className="text-center py-20 bg-slate-50/50 rounded-3xl border border-dashed border-slate-200">
                       <p className="text-slate-400 font-medium italic">No previous records found for this patient.</p>
                    </div>
                  )}
                </div>
                
                <div className="mt-12 flex justify-end">
                   <button className="bg-indigo-600 text-white px-8 py-3.5 rounded-2xl font-bold hover:bg-indigo-700 transition-all shadow-xl shadow-indigo-100 flex items-center gap-2">
                     <FileText size={18} /> Edit Records
                   </button>
                </div>
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
}
