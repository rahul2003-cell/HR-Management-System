import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { useAuthStore } from './store';
import Layout from './components/layout/Layout';
import Login from './pages/Login';
import Dashboard from './pages/admin/Dashboard';
import Employees from './pages/admin/Employees';
import Departments from './pages/admin/Departments';
import Attendance from './pages/admin/Attendance';
import Leaves from './pages/admin/Leaves';
import Payroll from './pages/admin/Payroll';
import MyProfile from './pages/employee/MyProfile';
import MyAttendance from './pages/employee/MyAttendance';
import MyLeaves from './pages/employee/MyLeaves';
import MyPayslips from './pages/employee/MyPayslips';

const qc = new QueryClient({ defaultOptions: { queries: { staleTime: 60000 } } });

function Guard({ children, adminOnly }) {
  const { isAuthenticated, user } = useAuthStore();
  if (!isAuthenticated) return <Navigate to="/login" replace />;
  if (adminOnly && !['ADMIN','HR'].includes(user?.role)) return <Navigate to="/me/profile" replace />;
  return children;
}

export default function App() {
  return (
    <QueryClientProvider client={qc}>
      <BrowserRouter>
        <Toaster position="top-right" toastOptions={{
          style: { background:'#0d0d20', color:'#f1f5f9', border:'1px solid rgba(255,255,255,0.08)' },
          duration: 3500,
        }} />
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/" element={<Guard><Layout /></Guard>}>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<Guard adminOnly><Dashboard /></Guard>} />
            <Route path="employees" element={<Guard adminOnly><Employees /></Guard>} />
            <Route path="departments" element={<Guard adminOnly><Departments /></Guard>} />
            <Route path="attendance" element={<Guard adminOnly><Attendance /></Guard>} />
            <Route path="leaves" element={<Guard adminOnly><Leaves /></Guard>} />
            <Route path="payroll" element={<Guard adminOnly><Payroll /></Guard>} />
            <Route path="me/profile" element={<MyProfile />} />
            <Route path="me/attendance" element={<MyAttendance />} />
            <Route path="me/leaves" element={<MyLeaves />} />
            <Route path="me/payslips" element={<MyPayslips />} />
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </QueryClientProvider>
  );
}