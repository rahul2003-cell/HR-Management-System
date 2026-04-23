import { useState, useEffect } from 'react';
import { Download, DollarSign } from 'lucide-react';
import { payrollApi, empApi } from '../../api';

const MONTHS = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];
const STATUS_COLORS = { GENERATED:'warning', PAID:'success', CANCELLED:'error' };

export default function MyPayslips() {
  const [empId,setEmpId] = useState(null); const [payslips,setPayslips] = useState([]);
  const [loading,setLoading] = useState(true);
  useEffect(()=>{ empApi.getMe().then(r=>setEmpId(r.data?.id)); },[]);
  useEffect(()=>{ if(empId){ payrollApi.getByEmployee(empId).then(r=>setPayslips(r.data||[])).finally(()=>setLoading(false)); } },[empId]);
  const fmt = v => v!=null?`₹${Number(v).toLocaleString('en-IN')}`:'—';
  return (
    <div>
      <div className="page-header"><h1 className="page-title">My Payslips</h1><p className="page-subtitle">{payslips.length} payslips available</p></div>
      <div style={{display:'grid',gridTemplateColumns:'repeat(auto-fill,minmax(300px,1fr))',gap:20}}>
        {loading?Array(4).fill(0).map((_,i)=><div key={i} className="skeleton" style={{height:200,borderRadius:20}}/>) :
        payslips.length===0?<div className="empty-state" style={{gridColumn:'1/-1'}}><DollarSign size={48}/>No payslips yet</div>:
        payslips.map(p=>(
          <div key={p.id} className="glass stat-card" style={{padding:24}}>
            <div style={{display:'flex',justifyContent:'space-between',marginBottom:16}}>
              <div><div style={{fontSize:18,fontWeight:700}}>{MONTHS[(p.month||1)-1]} {p.year}</div><span className={`badge badge-${STATUS_COLORS[p.status]}`}>{p.status}</span></div>
              <a href={payrollApi.downloadSlip(p.id)} className="btn btn-ghost btn-sm" download><Download size={14}/>PDF</a>
            </div>
            <div style={{display:'grid',gridTemplateColumns:'1fr 1fr',gap:10}}>
              {[['Gross',fmt(p.grossSalary),'var(--text)'],['Deductions',fmt(p.totalDeductions),'var(--error)'],['Net Salary',fmt(p.netSalary),'var(--success)'],['Days Present',p.presentDays,'var(--text)']].map(([l,v,c])=>(
                <div key={l} style={{padding:10,background:'var(--surface2)',borderRadius:8}}>
                  <div style={{fontSize:11,color:'var(--text3)',textTransform:'uppercase',letterSpacing:'0.06em',marginBottom:2}}>{l}</div>
                  <div style={{fontWeight:700,color:c}}>{v}</div>
                </div>
              ))}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}