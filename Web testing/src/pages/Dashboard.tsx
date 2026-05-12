/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import { mockDb } from '../services/mockDb';
import { Appointment, Doctor } from '../types';
import { 
  Users, 
  Calendar, 
  Clock, 
  CheckCircle2, 
  ArrowRight,
  TrendingUp,
  X
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { Link } from 'react-router-dom';

export default function Dashboard() {
  const { user } = useOutletContext<{ user: Doctor }>();
  const [stats, setStats] = useState({ total: 0, pending: 0, confirmed: 0, completed: 0 });
  const [recentAppointments, setRecentAppointments] = useState<Appointment[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [selectedDate, setSelectedDate] = useState(new Date().toISOString().split('T')[0]);
  const [viewingAppointment, setViewingAppointment] = useState<Appointment | null>(null);

  useEffect(() => {
    async function loadData() {
      const dbStats = await mockDb.getStats(user.id);
      const allApps = await mockDb.getAppointments(user.id);
      setStats(dbStats);
      // Sort by date and time
      const sorted = allApps.sort((a, b) => {
        const dateCompare = b.date.localeCompare(a.date);
        if (dateCompare !== 0) return dateCompare;
        return a.time.localeCompare(b.time);
      });
      setRecentAppointments(sorted);
      setIsLoading(false);
    }
    loadData();
  }, [user.id]);

  if (isLoading) {
    // ... loading state ...
  }

  // ... statCards ...

  const appointmentsOnSelectedDate = recentAppointments.filter(a => a.date === selectedDate);

  return (
    <div className="space-y-8 max-w-7xl mx-auto pb-12">
      {/* Stats Grid */}
      {/* ... existing stats grid ... */}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Recent Appointments Table */}
        <motion.div 
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          className="lg:col-span-2 bg-white rounded-3xl border border-slate-100 shadow-sm overflow-hidden flex flex-col"
        >
          <div className="p-6 border-b border-slate-50 flex items-center justify-between">
            <h3 className="text-lg font-bold text-slate-800">
              Appointments for {new Date(selectedDate).toLocaleDateString('en-US', { month: 'long', day: 'numeric', year: 'numeric' })}
            </h3>
            <span className="bg-indigo-50 text-indigo-600 px-3 py-1 rounded-full text-xs font-bold">
              {appointmentsOnSelectedDate.length} Total
            </span>
          </div>
          
          <div className="flex-1 overflow-auto max-h-[600px]">
            {appointmentsOnSelectedDate.length > 0 ? (
              <table className="w-full text-left">
                <thead className="sticky top-0 bg-white z-10">
                  <tr className="bg-slate-50/50">
                    <th className="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider">Patient</th>
                    <th className="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider">Time</th>
                    <th className="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider">Reason</th>
                    <th className="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider">Action</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-50">
                  {appointmentsOnSelectedDate.map((app) => (
                    <tr key={app.id} className="hover:bg-slate-50/80 transition-colors group">
                      <td className="px-6 py-5">
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 rounded-2xl bg-indigo-50 text-indigo-600 flex items-center justify-center font-bold text-sm">
                            {app.patientName.charAt(0)}
                          </div>
                          <div>
                            <p className="text-sm font-bold text-slate-900 leading-none">{app.patientName}</p>
                            <p className="text-[10px] text-slate-400 mt-1 font-medium">PATIENT ID: {app.patientId.substring(0, 8)}</p>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-4">
                        <span className="text-sm font-bold text-indigo-600 bg-indigo-50 px-2 py-1 rounded-lg">{app.time}</span>
                      </td>
                      <td className="px-6 py-4">
                        <span className="text-sm text-slate-600 truncate max-w-[150px] inline-block">{app.reason || 'General Checkup'}</span>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <button 
                          onClick={() => setViewingAppointment(app)}
                          className="p-2 text-slate-400 hover:text-indigo-600 hover:bg-indigo-50 rounded-xl transition-all"
                        >
                          <ArrowRight size={18} />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <div className="flex flex-col items-center justify-center py-20 text-center">
                <div className="w-16 h-16 bg-slate-50 rounded-3xl flex items-center justify-center text-slate-300 mb-4">
                  <Calendar size={32} />
                </div>
                <h4 className="text-slate-900 font-bold">No appointments</h4>
                <p className="text-slate-500 text-sm mt-1">There are no bookings for this date.</p>
              </div>
            )}
          </div>
        </motion.div>

        {/* Calendar View */}
        <motion.div 
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          className="bg-white rounded-3xl border border-slate-100 shadow-sm p-6 overflow-hidden flex flex-col"
        >
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-bold text-slate-800">Calendar</h3>
            <div className="flex items-center gap-1">
              <span className="text-sm font-bold text-slate-600 px-2">May 2026</span>
            </div>
          </div>

          <div className="grid grid-cols-7 gap-1 mb-2">
            {['S', 'M', 'T', 'W', 'T', 'F', 'S'].map(day => (
              <div key={day} className="text-center text-[10px] font-bold text-slate-400 uppercase py-2">
                {day}
              </div>
            ))}
          </div>

          <div className="grid grid-cols-7 gap-1">
            {Array.from({ length: 31 }).map((_, i) => {
              const day = i + 1;
              const dateStr = `2026-05-${day.toString().padStart(2, '0')}`;
              const hasAppointments = recentAppointments.some(a => a.date === dateStr);
              const isSelected = dateStr === selectedDate;
              const isToday = day === 12;

              return (
                <button 
                  key={day}
                  onClick={() => setSelectedDate(dateStr)}
                  className={`
                    aspect-square flex flex-col items-center justify-center rounded-xl text-sm transition-all relative
                    ${isSelected ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-100 font-bold z-10' : 'hover:bg-slate-50 text-slate-600'}
                    ${!isSelected && isToday ? 'ring-2 ring-indigo-200' : ''}
                    ${hasAppointments && !isSelected ? 'text-indigo-700 font-bold' : ''}
                  `}
                >
                  {day}
                  {hasAppointments && (
                    <div className={`w-1 h-1 rounded-full absolute bottom-1.5 ${isSelected ? 'bg-white' : 'bg-indigo-600'}`} />
                  )}
                </button>
              );
            })}
          </div>

          <div className="mt-8 space-y-4 pt-6 border-t border-slate-50 flex-1">
            <h4 className="text-xs font-bold text-slate-400 uppercase tracking-widest">Time Slots Summary</h4>
            <div className="grid grid-cols-2 gap-2">
              {appointmentsOnSelectedDate.slice(0, 4).map(a => (
                <div key={a.id} className="p-2 bg-slate-50 rounded-xl text-[11px] font-bold text-slate-600 border border-slate-100">
                  {a.time} - {a.patientName.split(' ')[0]}
                </div>
              ))}
              {appointmentsOnSelectedDate.length > 4 && (
                <div className="p-2 bg-indigo-50 rounded-xl text-[11px] font-bold text-indigo-600 text-center">
                  +{appointmentsOnSelectedDate.length - 4} more
                </div>
              )}
            </div>
          </div>
        </motion.div>
      </div>

      {/* Appointment Detail Modal */}
      <AnimatePresence>
        {viewingAppointment && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <motion.div 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setViewingAppointment(null)}
              className="absolute inset-0 bg-slate-900/60 backdrop-blur-sm"
            />
            <motion.div 
              initial={{ opacity: 0, scale: 0.95, y: 20 }}
              animate={{ opacity: 1, scale: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.95, y: 20 }}
              className="bg-white rounded-[32px] shadow-2xl w-full max-w-lg overflow-hidden relative"
            >
              <div className="h-32 bg-indigo-600 relative p-8 flex items-end">
                <button 
                  onClick={() => setViewingAppointment(null)}
                  className="absolute top-6 right-6 p-2 bg-white/20 hover:bg-white/30 rounded-full text-white transition-colors"
                >
                  <X size={20} />
                </button>
                <div className="flex items-center gap-4">
                   <div className="w-16 h-16 rounded-2xl bg-white text-indigo-600 flex items-center justify-center text-2xl font-bold shadow-lg">
                      {viewingAppointment.patientName.charAt(0)}
                   </div>
                   <div className="text-white">
                      <h2 className="text-xl font-bold">{viewingAppointment.patientName}</h2>
                      <p className="text-indigo-100 text-sm">Appointment Details</p>
                   </div>
                </div>
              </div>
              
              <div className="p-8 space-y-6">
                <div className="grid grid-cols-2 gap-6">
                   <div className="space-y-1">
                      <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Date</p>
                      <p className="text-slate-900 font-semibold">{viewingAppointment.date}</p>
                   </div>
                   <div className="space-y-1">
                      <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Time Slot</p>
                      <p className="text-indigo-600 font-bold">{viewingAppointment.time}</p>
                   </div>
                   <div className="space-y-1">
                      <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Status</p>
                      <span className="bg-emerald-50 text-emerald-600 px-2.5 py-0.5 rounded-full text-[10px] font-bold uppercase">
                        {viewingAppointment.status}
                      </span>
                   </div>
                   <div className="space-y-1">
                      <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Department</p>
                      <p className="text-slate-900 font-semibold">{viewingAppointment.department || 'General'}</p>
                   </div>
                </div>

                <div className="space-y-2 pt-4 border-t border-slate-50">
                   <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">Reason for Visit</p>
                   <p className="text-slate-600 text-sm leading-relaxed italic">
                      "{viewingAppointment.reason || 'Patient did not provide a specific reason for this visit. Standard checkup recommended.'}"
                   </p>
                </div>

                <div className="flex gap-3 pt-6">
                   <button className="flex-1 bg-indigo-600 text-white py-4 rounded-2xl font-bold hover:bg-indigo-700 transition-all shadow-lg shadow-indigo-100">
                      View Medical Record
                   </button>
                   <button 
                     onClick={() => setViewingAppointment(null)}
                     className="px-6 bg-slate-50 text-slate-600 py-4 rounded-2xl font-bold hover:bg-slate-100 transition-all"
                   >
                      Close
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
