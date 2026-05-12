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
  TrendingUp
} from 'lucide-react';
import { motion } from 'motion/react';
import { Link } from 'react-router-dom';

export default function Dashboard() {
  const { user } = useOutletContext<{ user: Doctor }>();
  const [stats, setStats] = useState({ total: 0, pending: 0, confirmed: 0, completed: 0 });
  const [recentAppointments, setRecentAppointments] = useState<Appointment[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function loadData() {
      const dbStats = await mockDb.getStats(user.id);
      const allApps = await mockDb.getAppointments(user.id);
      setStats(dbStats);
      setRecentAppointments(allApps.sort((a, b) => b.date.localeCompare(a.date)).slice(0, 5));
      setIsLoading(false);
    }
    loadData();
  }, [user.id]);

  if (isLoading) {
    return <div className="animate-pulse space-y-8">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {[1,2,3,4].map(i => <div key={i} className="h-32 bg-slate-200 rounded-2xl" />)}
      </div>
      <div className="h-64 bg-slate-200 rounded-2xl" />
    </div>;
  }

  const statCards = [
    { label: 'Total Appointments', value: stats.total, icon: Calendar, color: 'bg-indigo-600', text: 'text-indigo-600' },
    { label: 'Pending Requests', value: stats.pending, icon: Clock, color: 'bg-amber-500', text: 'text-amber-500' },
    { label: 'Confirmed Today', value: stats.confirmed, icon: CheckCircle2, color: 'bg-emerald-500', text: 'text-emerald-500' },
    { label: 'Active Patients', value: stats.total, icon: Users, color: 'bg-pink-600', text: 'text-pink-600' },
  ];

  return (
    <div className="space-y-8 max-w-7xl mx-auto">
      {/* Stats Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {statCards.map((stat, index) => (
          <motion.div
            key={stat.label}
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: index * 0.1 }}
            className="bg-white p-6 rounded-3xl shadow-sm border border-slate-100 flex flex-col gap-4 relative overflow-hidden group hover:shadow-md transition-shadow"
          >
            <div className={`w-12 h-12 rounded-2xl ${stat.color} text-white flex items-center justify-center shadow-lg transform group-hover:scale-110 transition-transform`}>
              <stat.icon size={24} />
            </div>
            <div>
              <p className="text-sm font-semibold text-slate-500 uppercase tracking-wider">{stat.label}</p>
              <div className="flex items-baseline gap-2">
                <h3 className="text-3xl font-bold text-slate-900 mt-1">{stat.value}</h3>
                <span className="text-xs text-emerald-600 font-bold flex items-center gap-0.5">
                  <TrendingUp size={12} /> +12%
                </span>
              </div>
            </div>
            <div className="absolute top-0 right-0 p-4 opacity-5 pointer-events-none transform translate-x-2 -translate-y-2">
              <stat.icon size={80} />
            </div>
          </motion.div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Recent Appointments */}
        <motion.div 
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          className="lg:col-span-2 bg-white rounded-3xl border border-slate-100 shadow-sm overflow-hidden flex flex-col"
        >
          <div className="p-6 border-b border-slate-50 flex items-center justify-between">
            <h3 className="text-lg font-bold text-slate-800">Recent Appointments</h3>
            <Link 
              to="/appointments" 
              className="text-indigo-600 text-sm font-semibold flex items-center gap-1 hover:gap-2 transition-all"
            >
              View All <ArrowRight size={16} />
            </Link>
          </div>
          <div className="overflow-x-auto">
            <table className="w-full text-left">
              <thead>
                <tr className="bg-slate-50/50">
                  <th className="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider">Patient</th>
                  <th className="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider">Date & Time</th>
                  <th className="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider">Reason</th>
                  <th className="px-6 py-4 text-xs font-bold text-slate-400 uppercase tracking-wider">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-50">
                {recentAppointments.map((app) => (
                  <tr key={app.id} className="hover:bg-slate-50/80 transition-colors group">
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
                    <td className="px-6 py-4">
                      <div className="text-sm text-slate-600 font-medium">
                        <div className="flex items-center gap-1.5">
                           <span className="text-slate-900">{app.date}</span>
                        </div>
                        <div className="text-xs text-slate-400 font-normal">{app.time}</div>
                      </div>
                    </td>
                    <td className="px-6 py-4">
                      <span className="text-sm text-slate-600 truncate max-w-[150px] inline-block">{app.reason}</span>
                    </td>
                    <td className="px-6 py-4">
                      <span className={`
                        px-2.5 py-1 rounded-full text-[10px] font-bold uppercase tracking-wider
                        ${app.status === 'confirmed' ? 'bg-emerald-50 text-emerald-700' : ''}
                        ${app.status === 'pending' ? 'bg-amber-50 text-amber-700' : ''}
                        ${app.status === 'completed' ? 'bg-blue-50 text-blue-700' : ''}
                        ${app.status === 'cancelled' ? 'bg-red-50 text-red-700' : ''}
                      `}>
                        {app.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </motion.div>

        {/* Calendar View */}
        <motion.div 
          initial={{ opacity: 0, x: 20 }}
          animate={{ opacity: 1, x: 0 }}
          className="bg-white rounded-3xl border border-slate-100 shadow-sm p-6 overflow-hidden"
        >
          <div className="flex items-center justify-between mb-6">
            <h3 className="text-lg font-bold text-slate-800">Appointment Calendar</h3>
            <div className="flex items-center gap-1">
              <button className="p-2 hover:bg-slate-50 rounded-lg text-slate-400">
                <ArrowRight className="rotate-180" size={18} />
              </button>
              <span className="text-sm font-bold text-slate-600 px-2">May 2026</span>
              <button className="p-2 hover:bg-slate-50 rounded-lg text-slate-400">
                <ArrowRight size={18} />
              </button>
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
              const isToday = day === 12;

              return (
                <div 
                  key={day}
                  className={`
                    aspect-square flex flex-col items-center justify-center rounded-xl text-sm transition-all cursor-pointer relative
                    ${isToday ? 'bg-indigo-600 text-white shadow-lg shadow-indigo-100 font-bold' : 'hover:bg-slate-50 text-slate-600'}
                    ${hasAppointments && !isToday ? 'bg-indigo-50 text-indigo-700 font-bold ring-1 ring-indigo-100' : ''}
                  `}
                >
                  {day}
                  {hasAppointments && (
                    <div className={`w-1 h-1 rounded-full absolute bottom-1.5 ${isToday ? 'bg-white' : 'bg-indigo-600'}`} />
                  )}
                </div>
              );
            })}
          </div>

          <div className="mt-8 space-y-4 pt-6 border-t border-slate-50">
            <h4 className="text-xs font-bold text-slate-400 uppercase tracking-widest">Upcoming Today</h4>
            <div className="space-y-3">
              {recentAppointments
                .filter(a => a.date === '2026-05-12')
                .map(a => (
                  <div key={a.id} className="flex items-center gap-3 p-3 bg-slate-50 rounded-2xl">
                    <div className="w-1.5 h-8 bg-indigo-600 rounded-full" />
                    <div>
                      <p className="text-sm font-bold text-slate-800 leading-none">{a.time}</p>
                      <p className="text-xs text-slate-500 mt-1">{a.patientName}</p>
                    </div>
                  </div>
                ))}
              {recentAppointments.filter(a => a.date === '2026-05-12').length === 0 && (
                <p className="text-sm text-slate-400 italic">No appointments for selected date.</p>
              )}
            </div>
          </div>
        </motion.div>
      </div>
    </div>
  );
}
