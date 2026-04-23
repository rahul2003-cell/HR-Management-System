import { useState, useEffect } from 'react';
import { Plus, FileText } from 'lucide-react';
import { leaveApi, empApi } from '../../api';
import toast from 'react-hot-toast';

const STATUS_COLORS = { PENDING:'warning', APPROVED:'success', REJECTED:'error', CANCELLED:'cyan' };
const LEAVE_TYPES = ['CASUAL','SICK','EARNED','MATERNITY','PATERNITY','UNPAID'];

export default function MyLeaves() {
  const [empId,setEmpId] = useState(null); const [leaves,setLeaves] = useState([]);
  const [loading,setLoading] = useState(true); const [showModal,setShowModal] = useState(false);
  const [form,setForm] = useState({leaveType:'CASUAL',fromDate:'',toDate:'',reason:''});
  const [saving,setSaving] = useState(false);

  useEffect(()=>{ empApi.getMe().then(r=>setEmpId(r.data?.id)); },[]);
  useEffect(()=>{ if(empId){ setLoading(true); leaveApi.getByEmployee(empId,{page:0,size:20}).then(r=>setLeaves(r.data?.content||[])).finally(()=>setLoading(false)); } },[empId]);

  const handleApply = async ev => {
    ev.preventDefault(); setSaving(true);
    try { await leaveApi.apply(empId,form); toast.success('Leave applied!'); setShowModal(false); setForm({leaveType:'CASUAL',fromDate:'',toDate:'',reason:''}); leaveApi.getByEmployee(empId,{page:0,size:20}).then(r=>setLeaves(r.data?.content||[])); }
    catch(e){ toast.error(e?.message||'Failed'); } finally { setSaving(false); }
  };

  return (
    <div>
      <div className="page-header" style={{display:'flex',alignItems:'flex-start',justifyContent:'space-between'}}>
        <div><h1 className="page-title">My Leaves</h1><p className="page-subtitle">{leaves.length} leave requests</p></div>
        <button className="btn btn-primary" onClick={()=>setShowModal(true)}><Plus size={16}/>Apply Leave</button>
      </div>
      <div className="table-wrap">
        <table className="table">
          <thead><tr><th>Leave Type</th><th>From</th><th>To</th><th>Days</th><th>Reason</th><th>Status</th><th>Remarks</th></tr></thead>
          <tbody>
            {loading?Array(5).fill(0).map((_,i)=><tr key={i}><td colSpan={7}><div className="skeleton" style={{height:36,borderRadius:8}}/></td></tr>):
            leaves.length===0?<tr><td colSpan={7}><div className="empty-state"><FileText size={36}/>No leave requests yet</div></td></tr>:
            leaves.map(l=>(
              <tr key={l.id}>
                <td><span className="badge badge-primary">{l.leaveType}</span></td>
                <td style={{color:'var(--text2)',fontSize:13}}>{l.fromDate}</td>
                <td style={{color:'var(--text2)',fontSize:13}}>{l.toDate}</td>
                <td style={{fontWeight:700,textAlign:'center'}}>{l.totalDays}</td>
                <td style={{color:'var(--text2)',fontSize:13,maxWidth:200,overflow:'hidden',textOverflow:'ellipsis',whiteSpace:'nowrap'}}>{l.reason||'—'}</td>
                <td><span className={`badge badge-${STATUS_COLORS[l.status]}`}>{l.status}</span></td>
                <td style={{color:'var(--text2)',fontSize:13}}>{l.adminRemarks||'—'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {showModal&&<div className="modal-overlay" onClick={()=>setShowModal(false)}>
        <div className="modal-box" onClick={e=>e.stopPropagation()}>
          <h3 className="modal-title">Apply for Leave</h3>
          <form onSubmit={handleApply}>
            <div className="form-grid">
              <div className="form-group full"><label className="form-label">Leave Type *</label>
                <select className="input" value={form.leaveType} onChange={e=>setForm(f=>({...f,leaveType:e.target.value}))}>
                  {LEAVE_TYPES.map(t=><option key={t}>{t}</option>)}
                </select>
              </div>
              <div className="form-group"><label className="form-label">From Date *</label><input className="input" type="date" value={form.fromDate} onChange={e=>setForm(f=>({...f,fromDate:e.target.value}))} required/></div>
              <div className="form-group"><label className="form-label">To Date *</label><input className="input" type="date" value={form.toDate} onChange={e=>setForm(f=>({...f,toDate:e.target.value}))} required/></div>
              <div className="form-group full"><label className="form-label">Reason</label><textarea className="input" rows={3} value={form.reason} onChange={e=>setForm(f=>({...f,reason:e.target.value}))} style={{resize:'vertical'}} placeholder="Reason for leave..."/></div>
            </div>
            <div className="modal-footer">
              <button type="button" className="btn btn-ghost" onClick={()=>setShowModal(false)}>Cancel</button>
              <button type="submit" className="btn btn-primary" disabled={saving}>{saving?'Applying...':'Apply Leave'}</button>
            </div>
          </form>
        </div>
      </div>}
    </div>
  );
}