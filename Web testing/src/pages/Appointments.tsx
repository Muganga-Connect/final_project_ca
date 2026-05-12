/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import { mockDb } from '../services/mockDb';
import { Appointment, AppointmentStatus, Doctor } from '../types';
import { 
  Search, 
  Filter, 
  Check, 
  X, 
  MoreHorizontal,
  CalendarDays,
  Clock,
  ExternalLink
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';

export default function Appointments() {
  const { user } = useOutletContext<{ user: Doctor }>();
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [filterStatus, setFilterStatus] = useState<AppointmentStatus | 'all'>('all');
  const [searchQuery, setSearchQuery] = useState('');
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      const allApps = await mockDb.getAppointments(user.id);
      setAppointments(allApps);
      setIsLoading(false);
    }
    loadData();
  }, [user.id]);

  const handleStatusUpdate = async (id: string, status: AppointmentStatus) => {
    await mockDb.updateAppointmentStatus(id, status);
    const updated = await mockDb.getAppointments(user.id);
    setAppointments(updated);
  };

  const filteredAppointments = appointments.filter(app => {
    const matchesFilter = filterStatus === 'all' || app.status === filterStatus;
    const matchesSearch = (app.patientName || '').toLowerCase().includes(searchQuery.toLowerCase()) ||
                         (app.reason || '').toLowerCase().includes(searchQuery.toLowerCase());
    return matchesFilter && matchesSearch;
  });

  const statusColors: Record<AppointmentStatus, string> = {
    pending: 'bg-amber-50 text-amber-700 border-amber-100',
    confirmed: 'bg-emerald-50 text-emerald-700 border-emerald-100',
    completed: 'bg-blue-50 text-blue-700 border-blue-100',
    cancelled: 'bg-red-50 text-red-700 border-red-100',
    missed: 'bg-slate-50 text-slate-700 border-slate-100',
  };

  return (
    <div className="space-y-6 max-w-7xl mx-auto">
      {/* Header & Controls */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div className="flex items-center gap-3">
          <div className="bg-indigo-600/10 p-2 rounded-xl text-indigo-600">
            <CalendarDays size={24} />
          </div>
          <div>
            <h2 className="text-2xl font-bold text-slate-900">Manage Appointments</h2>
            <p className="text-sm text-slate-500">Review and update schedules</p>
          </div>
        </div>

        <div className="flex flex-col sm:flex-row items-center gap-3">
          <div className="relative w-full sm:w-64">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
            <input
              type="text"
              placeholder="Search appointments..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full pl-10 pr-4 py-2.5 bg-white border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500/10 focus:border-indigo-500 transition-all text-sm"
            />
          </div>
          
          <div className="relative w-full sm:w-48">
            <Filter className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
            <select
              value={filterStatus}
              onChange={(e) => setFilterStatus(e.target.value as any)}
              className="w-full pl-10 pr-3 py-2.5 bg-white border border-slate-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-indigo-500/10 focus:border-indigo-500 transition-all text-sm appearance-none cursor-pointer"
            >
              <option value="all">All Status</option>
              <option value="pending">Pending</option>
              <option value="confirmed">Confirmed</option>
              <option value="completed">Completed</option>
              <option value="cancelled">Cancelled</option>
            </select>
          </div>
        </div>
      </div>

      {/* Main Content Area */}
      <div className="bg-white rounded-3xl border border-slate-200 shadow-sm overflow-hidden min-h-[400px] flex flex-col">
        <div className="overflow-x-auto">
          <table className="w-full text-left">
            <thead>
              <tr className="bg-slate-50/80">
                <th className="px-6 py-4 text-[11px] font-bold text-slate-500 uppercase tracking-widest border-b border-slate-200">Patient Details</th>
                <th className="px-6 py-4 text-[11px] font-bold text-slate-500 uppercase tracking-widest border-b border-slate-200 text-center">Schedule</th>
                <th className="px-6 py-4 text-[11px] font-bold text-slate-500 uppercase tracking-widest border-b border-slate-200">Medical Case</th>
                <th className="px-6 py-4 text-[11px] font-bold text-slate-500 uppercase tracking-widest border-b border-slate-200">Status</th>
                <th className="px-6 py-4 text-[11px] font-bold text-slate-500 uppercase tracking-widest border-b border-slate-200 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              <AnimatePresence mode="popLayout">
                {filteredAppointments.length > 0 ? (
                  filteredAppointments.map((app) => (
                    <motion.tr 
                      layout
                      key={app.id}
                      initial={{ opacity: 0 }}
                      animate={{ opacity: 1 }}
                      exit={{ opacity: 0 }}
                      className="hover:bg-indigo-50/30 transition-colors group"
                    >
                      <td className="px-6 py-5">
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 rounded-2xl bg-indigo-50 text-indigo-600 flex items-center justify-center font-bold text-sm">
                            {(app.patientName || 'A').charAt(0)}
                          </div>
                          <div>
                            <p className="text-sm font-bold text-slate-900 leading-none">{app.patientName || 'Anonymous'}</p>
                            <p className="text-[11px] text-slate-400 mt-1 font-medium italic">ID: {(app.patientId || 'unknown').toUpperCase()}</p>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-5">
                        <div className="flex flex-col items-center">
                          <div className="flex items-center gap-1.5 text-slate-900 font-bold text-sm">
                            <CalendarDays size={14} className="text-slate-400" />
                            {app.date}
                          </div>
                          <div className="flex items-center gap-1.5 text-slate-500 font-medium text-xs mt-1">
                            <Clock size={12} className="text-slate-300" />
                            {app.time}
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-5">
                        <div className="max-w-[200px]">
                           <p className="text-sm text-slate-700 font-medium leading-normal">{app.reason}</p>
                        </div>
                      </td>
                      <td className="px-6 py-5">
                        <span className={`
                          px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-wider border
                          ${statusColors[app.status]}
                        `}>
                          {app.status}
                        </span>
                      </td>
                      <td className="px-6 py-5 text-right">
                        <div className="flex items-center justify-end gap-2 opacity-0 group-hover:opacity-100 transition-opacity">
                          {app.status === 'pending' && (
                            <>
                              <button 
                                onClick={() => handleStatusUpdate(app.id, 'confirmed')}
                                className="p-2 bg-emerald-50 text-emerald-600 rounded-lg hover:bg-emerald-100 transition-all shadow-sm"
                                title="Confirm"
                              >
                                <Check size={18} />
                              </button>
                              <button 
                                onClick={() => handleStatusUpdate(app.id, 'cancelled')}
                                className="p-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition-all shadow-sm"
                                title="Reject"
                              >
                                <X size={18} />
                              </button>
                            </>
                          )}
                          {app.status === 'confirmed' && (
                            <button 
                              onClick={() => handleStatusUpdate(app.id, 'completed')}
                              className="p-2 bg-blue-50 text-blue-600 rounded-lg hover:bg-blue-100 transition-all shadow-sm"
                              title="Complete"
                            >
                              <Check size={18} />
                            </button>
                          )}
                          <button className="p-2 bg-slate-50 text-slate-600 rounded-lg hover:bg-slate-100 transition-all shadow-sm">
                            <MoreHorizontal size={18} />
                          </button>
                        </div>
                      </td>
                    </motion.tr>
                  ))
                ) : (
                  <tr>
                    <td colSpan={5} className="py-20 text-center">
                      <div className="flex flex-col items-center gap-3 text-slate-400">
                        <Search size={48} className="opacity-20" />
                        <p className="font-medium">No appointments found matching your criteria</p>
                        <button 
                          onClick={() => {setFilterStatus('all'); setSearchQuery('');}}
                          className="text-indigo-600 text-sm font-bold hover:underline"
                        >
                          Clear all filters
                        </button>
                      </div>
                    </td>
                  </tr>
                )}
              </AnimatePresence>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
