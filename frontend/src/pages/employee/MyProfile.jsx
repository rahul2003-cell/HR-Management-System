import { useState, useEffect } from 'react';
import { User, Mail, Phone, Building2, Calendar, DollarSign, CreditCard } from 'lucide-react';
import { empApi } from '../../api';
import { useAuthStore } from '../../store';
import styles from './Employee.module.css';

export default function MyProfile() {
  const [emp, setEmp] = useState(null); const [loading, setLoading] = useState(true);
  const { user } = useAuthStore();
  useEffect(()=>{ empApi.getMe().then(r=>setEmp(r.data)).finally(()=>setLoading(false)); },[]);
  const fmt = v => v!=null?`₹${Number(v).toLocaleString('en-IN')}`:'—';
  if(loading) return <div style={{display:'flex',justifyContent:'center',padding:60}}><div className="spinner"/></div>;
  if(!emp) return <div className="empty-state"><User size={48}/>Profile not found</div>;
  const sections = [
    { title:'Personal Information', icon:<User size={16}/>, fields:[['Full Name',emp.fullName],['Email',emp.email],['Phone',emp.phone||'—'],['Gender',emp.gender],['Date of Birth',emp.dateOfBirth||'—'],['Address',`${emp.city||''} ${emp.state||''} ${emp.pincode||''}`]] },
    { title:'Work Information', icon:<Building2 size={16}/>, fields:[['Employee ID',emp.employeeId],['Designation',emp.designation||'—'],['Department',emp.departmentName||'—'],['Employment Type',emp.employmentType],['Joining Date',emp.joiningDate||'—'],['Status',emp.status]] },
    { title:'Salary Information', icon:<DollarSign size={16}/>, fields:[['Basic Salary',fmt(emp.basicSalary)],['HRA',fmt(emp.hra)],['Allowances',fmt(emp.allowances)],['Deductions',fmt(emp.deductions)],['Gross Salary',fmt(emp.grossSalary)],['Net Salary',fmt(emp.netSalary)]] },
    { title:'Bank Information', icon:<CreditCard size={16}/>, fields:[['Bank Name',emp.bankName||'—'],['Account No',emp.bankAccount||'—'],['IFSC Code',emp.ifscCode||'—'],['PAN Number',emp.panNumber||'—']] },
  ];
  return (
    <div>
      <div className="page-header"><h1 className="page-title">My Profile</h1><p className="page-subtitle">Your personal and professional information</p></div>
      <div className={styles.profileHeader}>
        <div className="avatar avatar-xl">{emp.firstName?.[0]}</div>
        <div className={styles.profileInfo}>
          <h2 style={{fontSize:24,fontWeight:800}}>{emp.fullName}</h2>
          <p style={{color:'var(--text2)',marginBottom:8}}>{emp.designation} · {emp.departmentName}</p>
          <div style={{display:'flex',gap:8,flexWrap:'wrap'}}>
            <span className="badge badge-primary">{emp.employeeId}</span>
            <span className={`badge badge-${emp.status==='ACTIVE'?'success':'warning'}`}>{emp.status}</span>
            <span className="badge badge-cyan">{emp.employmentType}</span>
          </div>
        </div>
        <div className={styles.profileStats}>
          {[['Net Salary',fmt(emp.netSalary),'success'],['Gross Salary',fmt(emp.grossSalary),'purple']].map(([l,v,c])=>(
            <div key={l} className={`stat-card ${c}`} style={{padding:16,minWidth:140}}>
              <div className="stat-value" style={{fontSize:20}}>{v}</div>
              <div className="stat-label">{l}</div>
            </div>
          ))}
        </div>
      </div>
      <div className={styles.sectionsGrid}>
        {sections.map(sec=>(
          <div key={sec.title} className={`glass ${styles.section}`}>
            <div className={styles.sectionTitle}>{sec.icon}<span>{sec.title}</span></div>
            <div className={styles.fieldsGrid}>
              {sec.fields.map(([k,v])=>(
                <div key={k} className={styles.field}><span className={styles.fieldKey}>{k}</span><span className={styles.fieldVal}>{v}</span></div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}