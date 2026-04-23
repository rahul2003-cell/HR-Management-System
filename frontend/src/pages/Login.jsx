import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Zap, Mail, Lock, Eye, EyeOff, ArrowRight, Users, BarChart3, Shield } from 'lucide-react';
import { authApi } from '../api';
import { useAuthStore } from '../store';
import toast from 'react-hot-toast';
import styles from './Login.module.css';

const PARTICLES = Array.from({length:20},(_,i)=>({id:i,x:Math.random()*100,y:Math.random()*100,delay:Math.random()*4,size:Math.random()*3+1}));

export default function Login() {
  const [email,setEmail] = useState('');
  const [password,setPassword] = useState('');
  const [showPw,setShowPw] = useState(false);
  const [loading,setLoading] = useState(false);
  const { login, isAuthenticated, user } = useAuthStore();
  const nav = useNavigate();

  useEffect(()=>{
    if(isAuthenticated){ nav(['ADMIN','HR'].includes(user?.role)?'/dashboard':'/me/profile'); }
  },[isAuthenticated]);

  const fill = (e,p) => { setEmail(e); setPassword(p); };

  const handleSubmit = async(e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const res = await authApi.login({email,password});
      const {accessToken,user:u} = res.data;
      login(accessToken,{...u, name:u.name||u.email.split('@')[0]});
      toast.success('Welcome back!');
      nav(['ADMIN','HR'].includes(u.role)?'/dashboard':'/me/profile');
    } catch(err){ toast.error(err?.message||'Invalid credentials'); }
    finally { setLoading(false); }
  };

  return (
    <div className={styles.page}>
      {/* Animated background */}
      <div className={styles.bg}>
        <div className={styles.orb1}/><div className={styles.orb2}/><div className={styles.orb3}/>
        <div className={styles.grid}/>
        {PARTICLES.map(p=>(
          <div key={p.id} className={styles.particle} style={{left:`${p.x}%`,top:`${p.y}%`,width:p.size,height:p.size,animationDelay:`${p.delay}s`}}/>
        ))}
      </div>

      <div className={styles.container}>
        {/* Left Panel */}
        <div className={styles.leftPanel}>
          <div className={styles.brandArea}>
            <div className={styles.logoWrap}>
              <div className={styles.logoIcon}><Zap size={22} fill="white"/></div>
              <span className={styles.logoText}>Nexus<span>HR</span></span>
            </div>
            <h1 className={styles.headline}>Manage your team<br/>with <span>intelligence</span></h1>
            <p className={styles.subtext}>The complete HR solution for modern organizations. Attendance, payroll, leaves — all in one place.</p>
          </div>
          <div className={styles.features}>
            {[[<Users size={20}/>, 'Employee Management', '360° employee profiles & org charts'],
              [<BarChart3 size={20}/>, 'Analytics & Reports', 'Real-time dashboards with export'],
              [<Shield size={20}/>, 'Role-Based Access', 'Admin, HR and Employee portals']].map(([icon,title,desc])=>(
              <div key={title} className={styles.feature}>
                <div className={styles.featureIcon}>{icon}</div>
                <div><p className={styles.featureTitle}>{title}</p><p className={styles.featureDesc}>{desc}</p></div>
              </div>
            ))}
          </div>
          {/* 3D floating cards */}
          <div className={styles.floatCard1}>
            <span className="badge badge-success">● 12 Present Today</span>
          </div>
          <div className={styles.floatCard2}>
            <span style={{fontSize:12,fontWeight:600,color:'var(--text2)'}}>Payroll generated ✓</span>
          </div>
        </div>

        {/* Right Panel - Login Form */}
        <div className={styles.rightPanel}>
          <div className={styles.card}>
            <div className={styles.cardHeader}>
              <h2>Sign in to HRMS</h2>
              <p>Enter your credentials to continue</p>
            </div>

            <form onSubmit={handleSubmit} className={styles.form}>
              <div className={styles.formGroup}>
                <label className="form-label">Email Address</label>
                <div className={styles.inputWrap}>
                  <Mail size={16} className={styles.inputIcon}/>
                  <input className={`input ${styles.paddedInput}`} type="email" placeholder="you@nexushr.com"
                    value={email} onChange={e=>setEmail(e.target.value)} required/>
                </div>
              </div>
              <div className={styles.formGroup}>
                <label className="form-label">Password</label>
                <div className={styles.inputWrap}>
                  <Lock size={16} className={styles.inputIcon}/>
                  <input className={`input ${styles.paddedInput}`} type={showPw?'text':'password'} placeholder="••••••••"
                    value={password} onChange={e=>setPassword(e.target.value)} required/>
                  <button type="button" className={styles.eyeBtn} onClick={()=>setShowPw(s=>!s)}>
                    {showPw?<EyeOff size={15}/>:<Eye size={15}/>}
                  </button>
                </div>
              </div>
              <button type="submit" className={`btn btn-primary btn-lg ${styles.submitBtn}`} disabled={loading}>
                {loading?<span className="spinner-sm" style={{borderColor:'rgba(255,255,255,0.3)',borderTopColor:'#fff'}}/>:<ArrowRight size={18}/>}
                {loading?'Signing in...':'Sign In'}
              </button>
            </form>

            <div className={styles.divider}><span>Demo Accounts</span></div>
            <div className={styles.demoGrid}>
              {[['Admin','admin@nexushr.com','admin123','primary'],['HR Manager','priya.sharma@nexushr.com','admin123','cyan'],['Employee','ananya.iyer@nexushr.com','Hrms@123','success']].map(([role,e,p,color])=>(
                <button key={role} className={`${styles.demoBtn} ${styles['demo'+color]}`} onClick={()=>fill(e,p)}>
                  <strong>{role}</strong><span>{e}</span>
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}