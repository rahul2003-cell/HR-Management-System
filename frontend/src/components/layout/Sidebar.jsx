import { NavLink, useNavigate } from 'react-router-dom';
import { LayoutDashboard, Users, Building2, CalendarCheck, FileText, DollarSign, LogOut, Zap, UserCircle, Clock, ClipboardList, Receipt, Menu, X, ChevronRight } from 'lucide-react';
import { useAuthStore, useUIStore } from '../../store';
import toast from 'react-hot-toast';
import styles from './Sidebar.module.css';

const adminLinks = [
  { to:'/dashboard', icon:<LayoutDashboard size={18}/>, label:'Dashboard' },
  { to:'/employees', icon:<Users size={18}/>, label:'Employees' },
  { to:'/departments', icon:<Building2 size={18}/>, label:'Departments' },
  { to:'/attendance', icon:<CalendarCheck size={18}/>, label:'Attendance' },
  { to:'/leaves', icon:<FileText size={18}/>, label:'Leave Requests' },
  { to:'/payroll', icon:<DollarSign size={18}/>, label:'Payroll' },
];

const empLinks = [
  { to:'/me/profile', icon:<UserCircle size={18}/>, label:'My Profile' },
  { to:'/me/attendance', icon:<Clock size={18}/>, label:'My Attendance' },
  { to:'/me/leaves', icon:<ClipboardList size={18}/>, label:'My Leaves' },
  { to:'/me/payslips', icon:<Receipt size={18}/>, label:'My Payslips' },
];

export default function Sidebar() {
  const { user, logout } = useAuthStore();
  const { mobileSidebarOpen, closeMobileSidebar } = useUIStore();
  const navigate = useNavigate();
  const isAdmin = ['ADMIN','HR'].includes(user?.role);
  const links = isAdmin ? adminLinks : empLinks;

  const handleLogout = () => {
    logout();
    toast.success('Logged out');
    navigate('/login');
  };

  return (
    <>
      {mobileSidebarOpen && <div className={styles.overlay} onClick={closeMobileSidebar} />}
      <aside className={`${styles.sidebar} ${mobileSidebarOpen ? styles.mobileOpen : ''}`}>
        {/* Logo */}
        <div className={styles.logo}>
          <div className={styles.logoIcon}><Zap size={18} fill="white" /></div>
          <span className={styles.logoText}>Nexus<span>HR</span></span>
        </div>

        {/* Role Badge */}
        <div className={styles.roleBadge}>
          <span className={`badge badge-${isAdmin ? 'primary' : 'cyan'}`}>{user?.role || 'EMPLOYEE'}</span>
        </div>

        {/* Navigation */}
        <nav className={styles.nav}>
          <p className={styles.navLabel}>{isAdmin ? 'Management' : 'My Workspace'}</p>
          {links.map(l => (
            <NavLink key={l.to} to={l.to} className={({isActive}) => `${styles.link} ${isActive ? styles.linkActive : ''}`} onClick={closeMobileSidebar}>
              <span className={styles.linkIcon}>{l.icon}</span>
              <span>{l.label}</span>
              <ChevronRight size={14} className={styles.linkArrow} />
            </NavLink>
          ))}
        </nav>

        {/* User Info + Logout */}
        <div className={styles.bottom}>
          <div className={styles.userInfo}>
            <div className="avatar">{user?.name?.[0]?.toUpperCase() || 'U'}</div>
            <div className={styles.userDetails}>
              <p className={styles.userName}>{user?.name || user?.email?.split('@')[0]}</p>
              <p className={styles.userEmail}>{user?.email}</p>
            </div>
          </div>
          <button className={`btn btn-ghost btn-sm ${styles.logoutBtn}`} onClick={handleLogout}>
            <LogOut size={15} /> Logout
          </button>
        </div>
      </aside>
    </>
  );
}