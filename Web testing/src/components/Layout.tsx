/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { Outlet, Link, useLocation } from 'react-router-dom';
import { Doctor } from '../types';
import { 
  LayoutDashboard, 
  CalendarDays, 
  Users, 
  User,
  LogOut, 
  Stethoscope,
  Menu,
  X
} from 'lucide-react';
import { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';

interface LayoutProps {
  user: Doctor;
  onLogout: () => void;
}

export default function Layout({ user, onLogout }: LayoutProps) {
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const location = useLocation();

  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
    { name: 'Appointments', path: '/appointments', icon: CalendarDays },
    { name: 'Patients', path: '/patients', icon: Users },
    { name: 'Profile', path: '/profile', icon: User },
  ];

  return (
    <div className="flex h-screen bg-slate-50 overflow-hidden font-sans">
      {/* Mobile Sidebar Overlay */}
      <AnimatePresence>
        {!isSidebarOpen && (
          <motion.div 
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={() => setIsSidebarOpen(true)}
            className="md:hidden fixed inset-0 bg-black/20 z-40 backdrop-blur-sm"
          />
        )}
      </AnimatePresence>

      {/* Sidebar */}
      <motion.aside 
        initial={false}
        animate={{ width: isSidebarOpen ? 260 : 80 }}
        className="bg-white border-r border-slate-200 z-50 flex flex-col transition-all duration-300 ease-in-out"
      >
        <div className="p-6 flex items-center gap-3 border-b border-slate-100 h-20">
          <div className="bg-indigo-600 p-2 rounded-xl text-white">
            <Stethoscope size={24} />
          </div>
          {isSidebarOpen && (
            <motion.span 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="font-bold text-slate-800 text-lg tracking-tight whitespace-nowrap"
            >
              MugangaConnect+
            </motion.span>
          )}
        </div>

        <nav className="flex-1 px-4 py-8 space-y-2">
          {navItems.map((item) => {
            const isActive = location.pathname === item.path;
            const Icon = item.icon;
            return (
              <Link
                key={item.path}
                to={item.path}
                className={`
                  flex items-center gap-3 px-3 py-3 rounded-lg transition-colors
                  ${isActive 
                    ? 'bg-indigo-50 text-indigo-700 font-medium shadow-sm border border-indigo-100' 
                    : 'text-slate-500 hover:bg-slate-50 hover:text-slate-900'}
                `}
              >
                <Icon size={22} className={isActive ? 'text-indigo-600' : 'text-slate-400'} />
                {isSidebarOpen && (
                  <motion.span 
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    className="whitespace-nowrap"
                  >
                    {item.name}
                  </motion.span>
                )}
              </Link>
            );
          })}
        </nav>

        <div className="p-4 border-t border-slate-100">
          <button
            onClick={onLogout}
            className="flex items-center gap-3 px-3 py-3 rounded-lg text-slate-500 hover:bg-red-50 hover:text-red-700 transition-colors w-full group"
          >
            <LogOut size={22} className="text-slate-400 group-hover:text-red-500" />
            {isSidebarOpen && (
              <motion.span 
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                className="whitespace-nowrap"
              >
                Sign Out
              </motion.span>
            )}
          </button>
        </div>
      </motion.aside>

      {/* Main Content */}
      <div className="flex-1 flex flex-col min-w-0">
        <header className="h-20 bg-white border-b border-slate-200 flex items-center justify-between px-8">
          <div className="flex items-center gap-4">
            <button 
              onClick={() => setIsSidebarOpen(!isSidebarOpen)}
              className="p-2 hover:bg-slate-100 rounded-lg text-slate-500 md:flex hidden"
            >
              <Menu size={20} />
            </button>
            <h1 className="text-xl font-semibold text-slate-800">
              {navItems.find(i => i.path === location.pathname)?.name || 'Portal'}
            </h1>
          </div>

          <div className="flex items-center gap-4">
            <div className="text-right hidden sm:block">
              <p className="text-sm font-semibold text-slate-900">{user.name}</p>
              <p className="text-[10px] text-indigo-600 font-bold uppercase tracking-wider">{user.hospitalName || 'General Hospital'}</p>
              <p className="text-xs text-slate-500">{user.email}</p>
            </div>
            <Link to="/profile" className="w-10 h-10 rounded-full bg-gradient-to-tr from-indigo-500 to-purple-500 flex items-center justify-center text-white font-bold shadow-md overflow-hidden hover:ring-2 hover:ring-indigo-500 transition-all">
              {user.profileImage ? (
                <img src={user.profileImage} alt="Profile" className="w-full h-full object-cover" />
              ) : (
                user.name.charAt(0)
              )}
            </Link>
          </div>
        </header>

        <main className="flex-1 overflow-y-auto p-8">
          <Outlet context={{ user }} />
        </main>
      </div>
    </div>
  );
}
