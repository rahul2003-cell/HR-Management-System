import { useState, useEffect } from 'react';
import { Calendar, CheckCircle, XCircle, Clock, TrendingUp } from 'lucide-react';
import { attendanceApi, empApi } from '../../api';
import { useAuthStore } from '../../store';
import { format, startOfMonth, endOfMonth } from 'date-fns';

const STATUS_COLORS = { PRESENT:'success', ABSENT:'error', HALF_DAY:'warning', LATE:'warning', ON_LEAVE:'cyan', HOLIDAY:'primary', WORK_FROM_HOME:'cyan' };

export default function MyAttendance() {
  const [empId,setEmpId] = useState(null);
  const [records,setRecords] = useState([]); const [summary,setSummary] = useState(null);
  const [loading,setLoading] = useState(true);
  const [month,setMonth] = useState(format(new Date(),'yyyy-MM'));
  const { user } = useAuthStore();

  useEffect(()=>{
    empApi.getMe().then(r=>setEmpId(r.data?.id));
  },[]);

  useEffect(()=>{
    if(!empId) return;
    const [y,m] = month.split('-');
    const from = format(startOfMonth(new Date(y,m-1)),'yyyy-MM-dd');
    const to = format(endOfMonth(new Date(y,m-1)),'yyyy-MM-dd');
    setLoading(true);
    Promise.all([
      attendanceApi.getByEmployee(empId,from,to),
      attendanceApi.getSummary(empId,from,to)
    ]).then(([r,s])=>{ setRecords(r.data||[]); setSummary(s.data); }).finally(()=>setLoading(false));
  },[empId,month]);

  return (
    <div>
      <div className="page-header" style={{display:'flex',alignItems:'flex-start',justifyContent:'space-between'}}>
        <div><h1 className="page-title">My Attendance</h1><p className="page-subtitle">Your attendance records</p></div>
        <input className="input" type="month" value={month} onChange={e=>setMonth(e.target.value)} style={{width:160}}/>
      </div>
      {summary&&<div style={{display:'grid',gridTemplateColumns:'repeat(4,1fr)',gap:16,marginBottom:24}}>
        {[['Present',summary.present,'green'],['Absent',summary.absent,'error'],['Late',summary.late,'amber'],['Attendance %',summary.attendancePercent+'%','purple']].map(([l,v,c])=>(
          <div key={l} className={`stat-card ${c}`} style={{padding:20}}><div className="stat-value" style={{fontSize:26}}>{v}</div><div className="stat-label">{l}</div></div>
        ))}
      </div>}
      {summary&&<div style={{marginBottom:24}}>
        <div style={{display:'flex',justifyContent:'space-between',marginBottom:8,fontSize:13}}><span style={{color:'var(--text2)'}}>Attendance Rate</span><strong>{summary.attendancePercent}%</strong></div>
        <div className="progress-bar"><div className="progress-fill" style={{width:`${summary.attendancePercent}%`}}/></div>
      </div>}
      <div className="table-wrap">
        <table className="table">
          <thead><tr><th>Date</th><th>Status</th><th>Check In</th><th>Check Out</th><th>Hours</th></tr></thead>
          <tbody>
            {loading?Array(10).fill(0).map((_,i)=><tr key={i}><td colSpan={5}><div className="skeleton" style={{height:32,borderRadius:8}}/></td></tr>):
            records.length===0?<tr><td colSpan={5}><div className="empty-state"><Calendar size={36}/>No attendance for this month</div></td></tr>:
            records.map(r=>(
              <tr key={r.id}>
                <td style={{fontWeight:600}}>{r.attendanceDate}</td>
                <td><span className={`badge badge-${STATUS_COLORS[r.status]||'cyan'}`}>{r.status?.replace(/_/g,' ')}</span></td>
                <td style={{fontFamily:'monospace',color:'var(--text2)'}}>{r.checkIn||'—'}</td>
                <td style={{fontFamily:'monospace',color:'var(--text2)'}}>{r.checkOut||'—'}</td>
                <td style={{fontWeight:600,color:'var(--primary-light)'}}>{r.hoursWorked?`${r.hoursWorked.toFixed(1)}h`:'—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}