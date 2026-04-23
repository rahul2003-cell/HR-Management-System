import { useState, useEffect } from 'react';
import { CheckCircle, XCircle, Clock, FileText } from 'lucide-react';
import { leaveApi } from '../../api';
import toast from 'react-hot-toast';

const STATUS_COLORS = { PENDING:'warning', APPROVED:'success', REJECTED:'error', CANCELLED:'cyan' };
const LEAVE_COLORS = { CASUAL:'primary', SICK:'error', EARNED:'success', MATERNITY:'cyan', PATERNITY:'cyan', UNPAID:'warning' };

export default function Leaves() {
  const [leaves,setLeaves] = useState([]); const [loading,setLoading] = useState(true);
  const [filter,setFilter] = useState('PENDING'); const [actionModal,setActionModal] = useState(null);
  const [remarks,setRemarks] = useState(''); const [saving,setSaving] = useState(false);
  const load = () => { setLoading(true); leaveApi.getAll({page:0,size:50}).then(r=>setLeaves(r.data?.content||[])).finally(()=>setLoading(false)); };
  useEffect(load,[]);
  const filtered = filter==='ALL' ? leaves : leaves.filter(l=>l.status===filter);
  const handleAction = async(status) => {
    setSaving(true);
    try { await leaveApi.action(actionModal.id,{status,adminRemarks:remarks}); toast.success(`Leave ${status.toLowerCase()}`); setActionModal(null); load(); }
    catch(e){ toast.error(e?.message||'Failed'); } finally { setSaving(false); }
  };
  const counts = { ALL:leaves.length, PENDING:leaves.filter(l=>l.status==='PENDING').length, APPROVED:leaves.filter(l=>l.status==='APPROVED').length, REJECTED:leaves.filter(l=>l.status==='REJECTED').length };
  return (
    <div>
      <div className="page-header"><h1 className="page-title">Leave Management</h1><p className="page-subtitle">Review and manage leave requests</p></div>
      <div style={{display:'flex',gap:8,marginBottom:20,flexWrap:'wrap'}}>
        {Object.entries(counts).map(([k,v])=>(
          <button key={k} className={`tag ${filter===k?'active':''}`} onClick={()=>setFilter(k)}>{k} <strong>{v}</strong></button>
        ))}
      </div>
      <div className="table-wrap">
        <table className="table">
          <thead><tr><th>Employee</th><th>Leave Type</th><th>From</th><th>To</th><th>Days</th><th>Reason</th><th>Status</th><th>Actions</th></tr></thead>
          <tbody>
            {loading?Array(5).fill(0).map((_,i)=><tr key={i}><td colSpan={8}><div className="skeleton" style={{height:36,borderRadius:8}}/></td></tr>):
            filtered.length===0?<tr><td colSpan={8}><div className="empty-state"><FileText size={36}/>No {filter.toLowerCase()} leave requests</div></td></tr>:
            filtered.map(l=>(
              <tr key={l.id}>
                <td><div style={{display:'flex',alignItems:'center',gap:10}}><div className="avatar">{l.employeeName?.[0]}</div><div><div style={{fontWeight:600}}>{l.employeeName}</div><div style={{fontSize:12,color:'var(--text3)'}}>{l.employeeCode}</div></div></div></td>
                <td><span className={`badge badge-${LEAVE_COLORS[l.leaveType]||'primary'}`}>{l.leaveType}</span></td>
                <td style={{color:'var(--text2)',fontSize:13}}>{l.fromDate}</td>
                <td style={{color:'var(--text2)',fontSize:13}}>{l.toDate}</td>
                <td style={{fontWeight:700,textAlign:'center'}}>{l.totalDays}</td>
                <td style={{color:'var(--text2)',fontSize:13,maxWidth:200,overflow:'hidden',textOverflow:'ellipsis',whiteSpace:'nowrap'}}>{l.reason||'—'}</td>
                <td><span className={`badge badge-${STATUS_COLORS[l.status]}`}>{l.status}</span></td>
                <td>{l.status==='PENDING'&&<div className="table-actions">
                  <button className="btn btn-success btn-sm" onClick={()=>{setActionModal(l);setRemarks('');}}><CheckCircle size={13}/>Action</button>
                </div>}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      {actionModal&&<div className="modal-overlay" onClick={()=>setActionModal(null)}>
        <div className="modal-box" onClick={e=>e.stopPropagation()} style={{maxWidth:420}}>
          <h3 className="modal-title">Leave Action</h3>
          <div style={{background:'var(--surface2)',borderRadius:10,padding:16,marginBottom:20}}>
            <p style={{fontWeight:600,marginBottom:4}}>{actionModal.employeeName}</p>
            <p style={{fontSize:13,color:'var(--text2)'}}>{actionModal.leaveType} · {actionModal.fromDate} to {actionModal.toDate} ({actionModal.totalDays} days)</p>
            <p style={{fontSize:13,color:'var(--text2)',marginTop:4}}>{actionModal.reason}</p>
          </div>
          <div className="form-group" style={{marginBottom:20}}><label className="form-label">Admin Remarks</label><textarea className="input" rows={3} value={remarks} onChange={e=>setRemarks(e.target.value)} style={{resize:'vertical'}} placeholder="Optional remarks..."/></div>
          <div style={{display:'flex',gap:10,justifyContent:'flex-end'}}>
            <button className="btn btn-ghost" onClick={()=>setActionModal(null)}>Cancel</button>
            <button className="btn btn-danger" onClick={()=>handleAction('REJECTED')} disabled={saving}><XCircle size={15}/>Reject</button>
            <button className="btn btn-success" onClick={()=>handleAction('APPROVED')} disabled={saving}><CheckCircle size={15}/>Approve</button>
          </div>
        </div>
      </div>}
    </div>
  );
}