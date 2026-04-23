import { useState, useEffect } from 'react';
import { CalendarCheck, CheckCircle, XCircle, Clock, Wifi } from 'lucide-react';
import { attendanceApi, empApi } from '../../api';
import { format } from 'date-fns';
import toast from 'react-hot-toast';

const STATUS_OPTIONS = ['PRESENT','ABSENT','HALF_DAY','LATE','ON_LEAVE','HOLIDAY','WORK_FROM_HOME'];
const STATUS_COLORS = { PRESENT:'success', ABSENT:'error', HALF_DAY:'warning', LATE:'amber', ON_LEAVE:'cyan', HOLIDAY:'primary', WORK_FROM_HOME:'cyan' };
const STATUS_ICONS = { PRESENT:<CheckCircle size={14}/>, ABSENT:<XCircle size={14}/>, LATE:<Clock size={14}/>, WORK_FROM_HOME:<Wifi size={14}/> };

export default function Attendance() {
  const [date,setDate] = useState(format(new Date(),'yyyy-MM-dd'));
  const [records,setRecords] = useState([]); const [loading,setLoading] = useState(true);
  const [employees,setEmployees] = useState([]); const [showModal,setShowModal] = useState(false);
  const [form,setForm] = useState({employeeId:'',attendanceDate:date,checkIn:'09:00',checkOut:'18:00',status:'PRESENT',remarks:''});
  const [saving,setSaving] = useState(false);

  const load = () => { setLoading(true); attendanceApi.getByDate(date).then(r=>setRecords(r.data||[])).finally(()=>setLoading(false)); };
  useEffect(load,[date]);
  useEffect(()=>{ empApi.getAll({size:100}).then(r=>setEmployees(r.data?.content||[])); },[]);

  const handleMark = async ev => {
    ev.preventDefault(); setSaving(true);
    try {
      await attendanceApi.mark({...form,employeeId:parseInt(form.employeeId)});
      toast.success('Attendance marked'); setShowModal(false); load();
    } catch(e){ toast.error(e?.message||'Failed'); } finally { setSaving(false); }
  };

  const present = records.filter(r=>r.status==='PRESENT').length;
  const absent = records.filter(r=>r.status==='ABSENT').length;
  const wfh = records.filter(r=>r.status==='WORK_FROM_HOME').length;

  return (
    <div>
      <div className="page-header" style={{display:'flex',alignItems:'flex-start',justifyContent:'space-between',flexWrap:'wrap',gap:16}}>
        <div><h1 className="page-title">Attendance</h1><p className="page-subtitle">Track daily attendance</p></div>
        <div style={{display:'flex',gap:10,alignItems:'center'}}>
          <input className="input" type="date" value={date} onChange={e=>setDate(e.target.value)} style={{width:180}}/>
          <button className="btn btn-primary" onClick={()=>setShowModal(true)}><CalendarCheck size={16}/>Mark Attendance</button>
        </div>
      </div>

      {/* Summary */}
      <div style={{display:'grid',gridTemplateColumns:'repeat(4,1fr)',gap:16,marginBottom:24}}>
        {[['Total',records.length,'cyan'],['Present',present,'green'],['Absent',absent,'error'],['WFH',wfh,'primary']].map(([l,v,c])=>(
          <div key={l} className={`stat-card ${c}`} style={{padding:20}}>
            <div className={`stat-value`} style={{fontSize:28}}>{v}</div>
            <div className="stat-label">{l}</div>
          </div>
        ))}
      </div>

      <div className="table-wrap">
        <table className="table">
          <thead><tr><th>Employee</th><th>Status</th><th>Check In</th><th>Check Out</th><th>Hours</th><th>Remarks</th></tr></thead>
          <tbody>
            {loading ? Array(5).fill(0).map((_,i)=><tr key={i}><td colSpan={6}><div className="skeleton" style={{height:36,borderRadius:8}}/></td></tr>) :
              records.length===0 ? <tr><td colSpan={6}><div className="empty-state"><CalendarCheck size={36}/>No records for this date</div></td></tr> :
              records.map(r=>(
                <tr key={r.id}>
                  <td><div style={{display:'flex',alignItems:'center',gap:10}}>
                    <div className="avatar">{r.employeeName?.[0]}</div>
                    <div><div style={{fontWeight:600}}>{r.employeeName}</div><div style={{fontSize:12,color:'var(--text3)'}}>{r.employeeCode}</div></div>
                  </div></td>
                  <td><span className={`badge badge-${STATUS_COLORS[r.status]||'cyan'}`}>{STATUS_ICONS[r.status]} {r.status?.replace(/_/g,' ')}</span></td>
                  <td style={{fontFamily:'monospace',color:'var(--text2)'}}>{r.checkIn||'—'}</td>
                  <td style={{fontFamily:'monospace',color:'var(--text2)'}}>{r.checkOut||'—'}</td>
                  <td style={{fontWeight:600,color:'var(--primary-light)'}}>{r.hoursWorked?`${r.hoursWorked.toFixed(1)}h`:'—'}</td>
                  <td style={{color:'var(--text2)',fontSize:13}}>{r.remarks||'—'}</td>
                </tr>
              ))}
          </tbody>
        </table>
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={()=>setShowModal(false)}>
          <div className="modal-box" onClick={e=>e.stopPropagation()}>
            <h3 className="modal-title">Mark Attendance</h3>
            <form onSubmit={handleMark}>
              <div className="form-grid">
                <div className="form-group full"><label className="form-label">Employee *</label>
                  <select className="input" value={form.employeeId} onChange={e=>setForm(f=>({...f,employeeId:e.target.value}))} required>
                    <option value="">Select employee...</option>
                    {employees.map(e=><option key={e.id} value={e.id}>{e.fullName} ({e.employeeId})</option>)}
                  </select>
                </div>
                <div className="form-group"><label className="form-label">Date</label><input className="input" type="date" value={form.attendanceDate} onChange={e=>setForm(f=>({...f,attendanceDate:e.target.value}))}/></div>
                <div className="form-group"><label className="form-label">Status</label>
                  <select className="input" value={form.status} onChange={e=>setForm(f=>({...f,status:e.target.value}))}>
                    {STATUS_OPTIONS.map(s=><option key={s}>{s}</option>)}
                  </select>
                </div>
                <div className="form-group"><label className="form-label">Check In</label><input className="input" type="time" value={form.checkIn} onChange={e=>setForm(f=>({...f,checkIn:e.target.value}))}/></div>
                <div className="form-group"><label className="form-label">Check Out</label><input className="input" type="time" value={form.checkOut} onChange={e=>setForm(f=>({...f,checkOut:e.target.value}))}/></div>
                <div className="form-group full"><label className="form-label">Remarks</label><input className="input" value={form.remarks} onChange={e=>setForm(f=>({...f,remarks:e.target.value}))}/></div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-ghost" onClick={()=>setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>{saving?'Saving...':'Mark Attendance'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}