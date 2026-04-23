import { useEffect, useState } from 'react';
import { Users, UserCheck, Clock, FileText, DollarSign, Building2, TrendingUp, ArrowUpRight, Download } from 'lucide-react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, PieChart, Pie, Cell, LineChart, Line } from 'recharts';
import { adminApi } from '../../api';
import { format } from 'date-fns';
import styles from './Dashboard.module.css';

const COLORS = ['#6366f1','#06b6d4','#10b981','#f59e0b','#f43f5e'];

const monthlyData = [
  {m:'Jan',sal:1800000,emp:10},{m:'Feb',sal:1900000,emp:10},{m:'Mar',sal:2000000,emp:11},
  {m:'Apr',sal:2100000,emp:11},{m:'May',sal:2200000,emp:12},{m:'Jun',sal:2150000,emp:12},
  {m:'Jul',sal:2300000,emp:12},{m:'Aug',sal:2250000,emp:12},{m:'Sep',sal:2400000,emp:12},
  {m:'Oct',sal:2500000,emp:12},{m:'Nov',sal:2600000,emp:12},{m:'Dec',sal:2700000,emp:12},
];

const fmt = v => v>=100000 ? `₹${(v/100000).toFixed(1)}L` : `₹${(v/1000).toFixed(0)}K`;

export default function Dashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminApi.dashboard().then(r => setStats(r.data)).catch(console.error).finally(()=>setLoading(false));
  }, []);

  const statCards = stats ? [
    { label:'Total Employees', value:stats.totalEmployees, icon:<Users size={22}/>, color:'purple', change:'+2 this month' },
    { label:'Present Today', value:stats.presentToday, icon:<UserCheck size={22}/>, color:'green', change:`of ${stats.activeEmployees} active` },
    { label:'Pending Leaves', value:stats.pendingLeaves, icon:<FileText size={22}/>, color:'amber', change:'need review' },
    { label:'Departments', value:stats.totalDepartments, icon:<Building2 size={22}/>, color:'cyan', change:'active units' },
    { label:'Monthly Payroll', value:stats.monthlyPayroll?`₹${Number(stats.monthlyPayroll).toLocaleString('en-IN')}`:'-', icon:<DollarSign size={22}/>, color:'purple', change:'current month' },
    { label:'Active Employees', value:stats.activeEmployees, icon:<TrendingUp size={22}/>, color:'green', change:`${((stats.activeEmployees/stats.totalEmployees)*100).toFixed(0)}% of total` },
  ] : [];

  const deptData = stats?.byDepartment ? Object.entries(stats.byDepartment).map(([name,value])=>({name,value})) : [];

  const Skel = ({h=80,r=16}) => <div className="skeleton" style={{height:h,borderRadius:r,width:'100%'}}/>;

  return (
    <div>
      <div className="page-header">
        <p style={{fontSize:12,fontWeight:700,letterSpacing:'0.1em',textTransform:'uppercase',color:'var(--primary-light)',marginBottom:8}}>
          Overview · {format(new Date(),'EEEE, dd MMMM yyyy')}
        </p>
        <h1 className="page-title">Dashboard</h1>
        <p className="page-subtitle">Welcome back! Here's what's happening today.</p>
      </div>

      {/* Stats Grid */}
      <div className={styles.statsGrid}>
        {loading ? Array(6).fill(0).map((_,i)=><Skel key={i} h={140} r={20}/>) :
          statCards.map(s=>(
            <div key={s.label} className={`stat-card ${s.color}`}>
              <div className={`stat-icon ${s.color}`}>{s.icon}</div>
              <div className="stat-value">{s.value}</div>
              <div className="stat-label">{s.label}</div>
              <div className="stat-change up"><ArrowUpRight size={12}/>{s.change}</div>
            </div>
          ))}
      </div>

      <div className={styles.chartsRow}>
        {/* Payroll Trend */}
        <div className={`glass ${styles.chartCard}`}>
          <div className={styles.chartHeader}>
            <h3>Payroll Trend 2024</h3>
            <a href={adminApi.exportEmployees()} className="btn btn-ghost btn-sm" download><Download size={14}/>Export</a>
          </div>
          <ResponsiveContainer width="100%" height={220}>
            <BarChart data={monthlyData} margin={{top:5,right:5,left:0,bottom:0}}>
              <XAxis dataKey="m" tick={{fill:'var(--text3)',fontSize:11}} axisLine={false} tickLine={false}/>
              <YAxis tickFormatter={fmt} tick={{fill:'var(--text3)',fontSize:11}} axisLine={false} tickLine={false}/>
              <Tooltip formatter={v=>[`₹${Number(v).toLocaleString('en-IN')}`,'']} contentStyle={{background:'var(--bg3)',border:'1px solid var(--border)',borderRadius:10,color:'var(--text)'}}/>
              <Bar dataKey="sal" fill="url(#barGrad)" radius={[6,6,0,0]}/>
              <defs><linearGradient id="barGrad" x1="0" y1="0" x2="0" y2="1"><stop offset="0%" stopColor="#6366f1"/><stop offset="100%" stopColor="#4f46e5" stopOpacity={0.6}/></linearGradient></defs>
            </BarChart>
          </ResponsiveContainer>
        </div>

        {/* Department Distribution */}
        <div className={`glass ${styles.chartCard}`}>
          <div className={styles.chartHeader}><h3>By Department</h3></div>
          {loading ? <Skel h={220}/> : (
            <div className={styles.pieWrap}>
              <ResponsiveContainer width="60%" height={200}>
                <PieChart>
                  <Pie data={deptData} cx="50%" cy="50%" innerRadius={55} outerRadius={80} dataKey="value" paddingAngle={3}>
                    {deptData.map((_,i)=><Cell key={i} fill={COLORS[i%COLORS.length]}/>)}
                  </Pie>
                  <Tooltip contentStyle={{background:'var(--bg3)',border:'1px solid var(--border)',borderRadius:10}}/>
                </PieChart>
              </ResponsiveContainer>
              <div className={styles.legend}>
                {deptData.map((d,i)=>(
                  <div key={d.name} className={styles.legendItem}>
                    <span className={styles.legendDot} style={{background:COLORS[i%COLORS.length]}}/>
                    <span>{d.name}</span>
                    <span className={styles.legendVal}>{d.value}</span>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </div>

      {/* Recent Joinees */}
      <div className={`glass ${styles.tableSection}`}>
        <div className={styles.chartHeader}><h3>Recent Joinees</h3><span className="badge badge-primary">{stats?.recentJoinees?.length || 0} this month</span></div>
        {loading ? <Skel h={200}/> : (
          <div className="table-wrap" style={{border:'none',borderRadius:0,background:'transparent'}}>
            <table className="table">
              <thead><tr><th>Employee</th><th>Designation</th><th>Department</th><th>Joined</th></tr></thead>
              <tbody>
                {stats?.recentJoinees?.map(e=>(
                  <tr key={e.employeeId}>
                    <td><div style={{display:'flex',alignItems:'center',gap:10}}>
                      <div className="avatar">{e.name?.[0]}</div>
                      <div><div style={{fontWeight:600}}>{e.name}</div><div style={{fontSize:12,color:'var(--text3)'}}>{e.employeeId}</div></div>
                    </div></td>
                    <td><span style={{color:'var(--text2)'}}>{e.designation||'—'}</span></td>
                    <td><span className="badge badge-primary">{e.department||'—'}</span></td>
                    <td style={{color:'var(--text2)',fontSize:13}}>{e.joinDate}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}