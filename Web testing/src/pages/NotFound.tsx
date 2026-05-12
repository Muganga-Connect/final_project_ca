/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { Link } from 'react-router-dom';
import { Home, AlertCircle, ArrowLeft } from 'lucide-react';
import { motion } from 'motion/react';

export default function NotFound() {
  return (
    <div className="min-h-screen bg-slate-50 flex items-center justify-center p-6 font-sans">
      <div className="max-w-md w-full text-center">
        <motion.div
           initial={{ opacity: 0, scale: 0.8 }}
           animate={{ opacity: 1, scale: 1 }}
           className="bg-white p-12 rounded-[3rem] shadow-2xl border border-slate-100 flex flex-col items-center gap-6"
        >
          <div className="bg-red-50 p-6 rounded-full text-red-500 shadow-inner">
             <AlertCircle size={64} strokeWidth={1.5} />
          </div>
          
          <div>
            <h1 className="text-6xl font-black text-slate-900 tracking-tighter">404</h1>
            <h2 className="text-xl font-bold text-slate-800 mt-2">Page Not Found</h2>
            <p className="text-slate-500 mt-4 leading-relaxed">
              The page you are looking for doesn't exist or has been moved to another location.
            </p>
          </div>

          <div className="w-full h-px bg-slate-50 my-2" />

          <Link 
            to="/" 
            className="w-full bg-indigo-600 text-white py-4 rounded-2xl font-bold flex items-center justify-center gap-3 hover:bg-slate-900 transition-all shadow-xl shadow-indigo-100 group"
          >
            <ArrowLeft size={18} className="group-hover:-translate-x-1 transition-transform" />
            Back to Portal
          </Link>
          
          <Link 
            to="/dashboard" 
            className="flex items-center gap-2 text-sm font-bold text-slate-400 hover:text-indigo-600 transition-colors"
          >
            <Home size={16} /> 
            Go to Dashboard
          </Link>
        </motion.div>
      </div>
    </div>
  );
}
