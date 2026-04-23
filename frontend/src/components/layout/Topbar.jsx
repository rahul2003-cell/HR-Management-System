import { Bell, Menu, Search, Sun } from 'lucide-react';
import { useUIStore, useAuthStore } from '../../store';
import { useNavigate } from 'react-router-dom';
import styles from './Topbar.module.css';

export default function Topbar({ title }) {
  const { toggleMobileSidebar } = useUIStore();
  const { user } = useAuthStore();
  return (
    <header className={styles.topbar}>
      <div className={styles.left}>
        <button className={`btn btn-ghost btn-icon ${styles.menuBtn}`} onClick={toggleMobileSidebar}>
          <Menu size={20} />
        </button>
        <div>
          <h1 className={styles.title}>{title}</h1>
        </div>
      </div>
      <div className={styles.right}>
        <button className="btn btn-ghost btn-icon"><Bell size={18} /></button>
        <div className={styles.userChip}>
          <div className="avatar" style={{width:30,height:30,fontSize:12}}>{user?.name?.[0]?.toUpperCase()||'U'}</div>
          <span className={styles.greeting}>Hi, {user?.name?.split(' ')[0] || 'there'}</span>
        </div>
      </div>
    </header>
  );
}