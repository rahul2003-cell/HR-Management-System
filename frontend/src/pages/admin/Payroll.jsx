import { useState, useEffect } from 'react';
import { DollarSign, Download, Play, CheckSquare } from 'lucide-react';
import { payrollApi } from '../../api';
import toast from 'react-hot-toast';
import { format } from 'date-fns';

const STATUS_COLORS = { GENERATED:'warning', PAID:'success', CANCELLED:'error' };

export default function Payroll() {
  const now = new Date();
  const [month,setMonth] = useState(now.getMonth()+1);
  const [year,setYear] = useState(now.getFullYear());
  const [records,setRecords] = useState([]); const [loading,setLoading] = useState(false);
  const [generating,setGenerating] = useState(false);

  const load = async() => { setLoading(true); payrollApi.getMonthly(month,year).then(r=>setRecords(r.data||[])).finally(()=>setLoading(false)); };
  useEffect(load,[month,year]);

  const generate = async() => {
    setGenerating(true);
    try { await payrollApi.generate({month,year}); toast.success('Payroll generated!'); load(); }
    catch(e){ toast.error(e?.message||'Generation failed'); } finally { setGenerating(false); }
  };

  const markPaid = async(id) => {
    await payrollApi.markPaid(id); toast.success('Marked as paid'); load();
  };

  const fmt = v => v!=null ? `₹${Number(v).toLocaleString('en-IN')}` : '—';
  const totalNet = records.reduce((s,r)=>s+(r.netSalary||0),0);
  const months = ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'];

  return (
    <div>
      <div className="page-header" style={{display:'flex',alignItems:'flex-start',justifyContent:'space-between',flexWrap:'wrap',gap:16}}>
        <div><h1 className="page-title">Payroll</h1><p className="page-subtitle">Manage salary processing</p></div>
        <div style={{display:'flex',gap:10,alignItems:'center'}}>
          <select className="input" value={month} onChange={e=>setMonth(parseInt(e.target.value))} style={{width:110}}>
            {months.map((m,i)=><option key={i} value={i+1}>{m}</option>)}
          </select>
          <select className="input" value={year} onChange={e=>setYear(parseInt(e.target.value))} style={{width:90}}>
            {[2024,2025,2026].map(y=><option key={y}>{y}</option>)}
          </select>
          {records.length===0 ? (
            <button className="btn btn-primary" onClick={generate} disabled={generating}><Play size={15}/>{generating?'Generating...':'Generate Payroll'}</button>
          ) : (
            <a href={payrollApi.downloadExcel(month,year)} className="btn btn-cyan" download><Download size={15}/>Export Excel</a>
          )}
        </div>
      </div>

      {/* Summary cards */}
      {records.length>0&&<div style={{display:'grid',gridTemplateColumns:'repeat(3,1fr)',gap:16,marginBottom:24}}>
        {[['Total Employees',records.length,'purple'],['Total Payroll',fmt(totalNet),'green'],['Paid',records.filter(r=>r.status==='PAID').length+' of '+records.length,'cyan']].map(([l,v,c])=>(
          <div key={l} className={`stat-card ${c}`} style={{padding:20}}><div className="stat-value" style={{fontSize:records.length>0&&l==='Total Payroll'?20:28}}>{v}</div><div className="stat-label">{l}</div></div>
        ))}
      </div>}

      <div className="table-wrap">
        <table className="table">
          <thead><tr><th>Employee</th><th>Basic</th><th>HRA</th><th>Allowances</th><th>Gross</th><th>Deductions</th><th>Net Salary</th><th>Days</th><th>Status</th><th>Actions</th></tr></thead>
          <tbody>
            {loading?Array(5).fill(0).map((_,i)=><tr key={i}><td colSpan={10}><div className="skeleton" style={{height:36,borderRadius:8}}/></td></tr>):
            records.length===0?<tr><td colSpan={10}><div className="empty-state"><DollarSign size={36}/>No payroll generated yet. Click "Generate Payroll" to start.</div></td></tr>:
            records.map(r=>(
              <tr key={r.id}>
                <td><div style={{display:'flex',alignItems:'center',gap:10}}><div className="avatar">{r.employeeName?.[0]}</div><div><div style={{fontWeight:600,fontSize:13}}>{r.employeeName}</div><div style={{fontSize:11,color:'var(--text3)'}}>{r.employeeCode}</div></div></div></td>
                <td style={{fontSize:13}}>{fmt(r.basicSalary)}</td>
                <td style={{fontSize:13}}>{fmt(r.hra)}</td>
                <td style={{fontSize:13}}>{fmt(r.allowances)}</td>
                <td style={{fontWeight:600}}>{fmt(r.grossSalary)}</td>
                <td style={{color:'var(--error)',fontSize:13}}>-{fmt(r.totalDeductions)}</td>
                <td style={{fontWeight:700,color:'var(--success)',fontSize:15}}>{fmt(r.netSalary)}</td>
                <td style={{fontSize:12,color:'var(--text2)',textAlign:'center'}}>{r.presentDays}P/{r.absentDays}A</td>
                <td><span className={`badge badge-${STATUS_COLORS[r.status]}`}>{r.status}</span></td>
                <td><div style={{display:'flex',gap:6}}>
                  <a href={payrollApi.downloadSlip(r.id)} className="btn btn-ghost btn-sm" download title="Download PDF"><Download size={13}/>Slip</a>
                  {r.status==='GENERATED'&&<button className="btn btn-success btn-sm" onClick={()=>markPaid(r.id)}><CheckSquare size={13}/>Pay</button>}
                </div></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}