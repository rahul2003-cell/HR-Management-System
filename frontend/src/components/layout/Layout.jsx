import { Outlet, useLocation } from 'react-router-dom';
import Sidebar from './Sidebar';
import Topbar from './Topbar';

const titles = {
  '/dashboard':'Dashboard','/employees':'Employees','/departments':'Departments',
  '/attendance':'Attendance','/leaves':'Leave Management','/payroll':'Payroll',
  '/me/profile':'My Profile','/me/attendance':'My Attendance',
  '/me/leaves':'My Leaves','/me/payslips':'My Payslips',
};

export default function Layout() {
  const loc = useLocation();
  const title = titles[loc.pathname] || 'HRMS';
  return (
    <div className="app-layout">
      <div className="app-bg" /><div className="grid-pattern" />
      <Sidebar />
      <div className="main-content">
        <Topbar title={title} />
        <main className="page-content"><Outlet /></main>
      </div>
    </div>
  );
}