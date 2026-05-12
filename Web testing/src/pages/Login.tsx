/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState, FormEvent } from 'react';
import { mockDb } from '../services/mockDb';
import { Doctor } from '../types';
import { Stethoscope, Lock, Mail, ChevronRight, AlertCircle } from 'lucide-react';
import { motion } from 'motion/react';

interface LoginProps {
  onLogin: (doctor: Doctor) => void;
}

export default function Login({ onLogin }: LoginProps) {
  const [email, setEmail] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    try {
      const doctor = await mockDb.login(email);
      if (doctor) {
        onLogin(doctor);
      } else {
        setError('Doctor not found. Please use a registered doctor email.');
      }
    } catch (err) {
      setError('An error occurred during login. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-6 font-sans">
      <div className="absolute top-0 left-0 w-full h-[300px] bg-indigo-600 -skew-y-3 origin-top-left -z-10 shadow-xl" />
      
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="w-full max-w-md"
      >
        <div className="bg-white rounded-3xl shadow-2xl overflow-hidden">
          <div className="p-10">
            <div className="flex items-center gap-3 mb-8">
              <div className="bg-indigo-600 p-2.5 rounded-2xl text-white shadow-lg shadow-indigo-200">
                <Stethoscope size={32} />
              </div>
              <div>
                <h1 className="text-2xl font-bold text-slate-900 tracking-tight leading-none">MugangaConnect+</h1>
                <p className="text-sm text-slate-500 mt-1 font-medium italic">Doctor Portal</p>
              </div>
            </div>

            <div className="mb-8">
              <h2 className="text-xl font-bold text-slate-900">Welcome back</h2>
              <p className="text-slate-500 mt-1">Please sign in to your professional account</p>
            </div>

            <form onSubmit={handleSubmit} className="space-y-6">
              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-2 px-1">Email Address</label>
                <div className="relative">
                  <Mail className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                  <input
                    type="email"
                    required
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    placeholder="doctor@muganga.com"
                    className="w-full pl-12 pr-4 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl focus:outline-none focus:ring-2 focus:ring-indigo-500/20 focus:border-indigo-500 transition-all text-slate-900 placeholder:text-slate-400"
                  />
                </div>
              </div>

              <div>
                <label className="block text-sm font-semibold text-slate-700 mb-2 px-1">Access Token</label>
                <div className="relative">
                  <Lock className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={18} />
                  <input
                    type="password"
                    disabled
                    placeholder="••••••••••••"
                    className="w-full pl-12 pr-4 py-3.5 bg-slate-50 border border-slate-200 rounded-2xl cursor-not-allowed opacity-60 text-slate-900"
                  />
                </div>
                <p className="text-[11px] text-slate-400 mt-2 px-1 italic">Demo mode: Only email required (try jean@muganga.com)</p>
              </div>

              {error && (
                <motion.div 
                  initial={{ opacity: 0, x: -10 }}
                  animate={{ opacity: 1, x: 0 }}
                  className="bg-red-50 text-red-700 p-4 rounded-2xl flex items-start gap-3 border border-red-100"
                >
                  <AlertCircle size={18} className="mt-0.5 shrink-0" />
                  <p className="text-sm font-medium">{error}</p>
                </motion.div>
              )}

              <button
                type="submit"
                disabled={isLoading}
                className="w-full bg-indigo-600 hover:bg-indigo-700 disabled:bg-indigo-400 text-white font-bold py-4 rounded-2xl transition-all shadow-lg shadow-indigo-100 flex items-center justify-center gap-2 group mt-4"
              >
                {isLoading ? (
                  <div className="w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                ) : (
                  <>
                    Sign In
                    <ChevronRight size={18} className="group-hover:translate-x-1 transition-transform" />
                  </>
                )}
              </button>
            </form>
          </div>
          
          <div className="bg-slate-50 px-10 py-6 border-t border-slate-100 flex justify-between items-center text-xs text-slate-500">
            <span className="font-medium hover:text-indigo-600 cursor-pointer transition-colors">Need assistance?</span>
            <span>&copy; 2026 MugangaConnect</span>
          </div>
        </div>
      </motion.div>
    </div>
  );
}
