import { useState, useEffect } from 'react';
import { Plus, Search, Edit2, Trash2, Download, Eye, Filter, Users } from 'lucide-react';
import { empApi, deptApi, adminApi } from '../../api';
import toast from 'react-hot-toast';
import styles from './Employees.module.css';

const EMPTY = { firstName:'',lastName:'',email:'',phone:'',designation:'',departmentId:'',basicSalary:'',hra:'',allowances:'',deductions:'',joiningDate:'',gender:'MALE',employmentType:'FULL_TIME',city:'',state:'',pincode:'',password:'Hrms@123' };
const STATUS_COLORS = { ACTIVE:'success', INACTIVE:'warning', ON_LEAVE:'cyan', TERMINATED:'error' };

export default function Employees() {
  const [employees,setEmployees] = useState([]);
  const [departments,setDepartments] = useState([]);
  const [loading,setLoading] = useState(true);
  const [search,setSearch] = useState('');
  const [showModal,setShowModal] = useState(false);
  const [editing,setEditing] = useState(null);
  const [form,setForm] = useState(EMPTY);
  const [saving,setSaving] = useState(false);
  const [page,setPage] = useState(0);
  const [total,setTotal] = useState(0);
  const [view,setView] = useState(null);

  const load = async () => {
    setLoading(true);
    try {
      const res = search ? await empApi.search({q:search,page,size:10}) : await empApi.getAll({page,size:10});
      setEmployees(res.data?.content||[]);
      setTotal(res.data?.totalElements||0);
    } finally { setLoading(false); }
  };

  useEffect(()=>{deptApi.getAll().then(r=>setDepartments(r.data||[]));}, []);
  useEffect(()=>{load();}, [search,page]);

  const set = k => e => setForm(f=>({...f,[k]:e.target.value}));
  const openCreate = () => { setEditing(null); setForm(EMPTY); setShowModal(true); };
  const openEdit = e => { setEditing(e); setForm({...e,departmentId:e.departmentId,basicSalary:e.basicSalary,hra:e.hra||'',allowances:e.allowances||'',deductions:e.deductions||'',joiningDate:e.joiningDate||'',password:''}); setShowModal(true); };

  const handleSubmit = async ev => {
    ev.preventDefault(); setSaving(true);
    try {
      const payload = {...form,basicSalary:parseFloat(form.basicSalary)||0,hra:parseFloat(form.hra)||0,allowances:parseFloat(form.allowances)||0,deductions:parseFloat(form.deductions)||0,departmentId:parseInt(form.departmentId)};
      if(editing){ await empApi.update(editing.id,payload); toast.success('Employee updated'); }
      else { await empApi.create(payload); toast.success('Employee created! Default password: Hrms@123'); }
      setShowModal(false); load();
    } catch(e){ toast.error(e?.message||'Save failed'); }
    finally { setSaving(false); }
  };

  const handleDelete = async id => {
    if(!window.confirm('Terminate this employee?')) return;
    await empApi.delete(id); toast.success('Employee terminated'); load();
  };

  const fmt = v => v ? `₹${Number(v).toLocaleString('en-IN')}` : '—';

  return (
    <div>
      <div className="page-header" style={{display:'flex',alignItems:'flex-start',justifyContent:'space-between',flexWrap:'wrap',gap:16}}>
        <div>
          <h1 className="page-title">Employees</h1>
          <p className="page-subtitle">{total} total employees in the system</p>
        </div>
        <div style={{display:'flex',gap:10}}>
          <a href={adminApi.exportEmployees()} className="btn btn-ghost btn-sm" download><Download size={14}/>Export Excel</a>
          <button className="btn btn-primary" onClick={openCreate}><Plus size={16}/>Add Employee</button>
        </div>
      </div>

      {/* Search & Filter */}
      <div className={`glass ${styles.toolbar}`}>
        <div className="search-bar" style={{flex:1}}>
          <Search size={16}/>
          <input className="input" placeholder="Search by name, email, ID..." value={search} onChange={e=>{setSearch(e.target.value);setPage(0);}} style={{paddingLeft:40,width:'100%'}}/>
        </div>
        <span style={{fontSize:13,color:'var(--text2)'}}>{employees.length} showing</span>
      </div>

      {/* Table */}
      <div className="table-wrap">
        <table className="table">
          <thead><tr>
            <th>Employee</th><th>Designation</th><th>Department</th>
            <th>Salary</th><th>Status</th><th>Joined</th><th>Actions</th>
          </tr></thead>
          <tbody>
            {loading ? Array(5).fill(0).map((_,i)=>(
              <tr key={i}><td colSpan={7}><div className="skeleton" style={{height:40,borderRadius:8,margin:'4px 0'}}/></td></tr>
            )) : employees.length===0 ? (
              <tr><td colSpan={7}><div className="empty-state"><Users size={36}/>No employees found</div></td></tr>
            ) : employees.map(e=>(
              <tr key={e.id}>
                <td><div style={{display:'flex',alignItems:'center',gap:12}}>
                  <div className="avatar">{e.firstName?.[0]}</div>
                  <div>
                    <div style={{fontWeight:600}}>{e.fullName}</div>
                    <div style={{fontSize:12,color:'var(--text3)'}}>{e.employeeId} · {e.email}</div>
                  </div>
                </div></td>
                <td style={{color:'var(--text2)'}}>{e.designation||'—'}</td>
                <td><span className="badge badge-primary">{e.departmentName||'—'}</span></td>
                <td style={{fontWeight:600}}>{fmt(e.netSalary)}</td>
                <td><span className={`badge badge-${STATUS_COLORS[e.status]||'cyan'}`}>{e.status}</span></td>
                <td style={{color:'var(--text2)',fontSize:13}}>{e.joiningDate||'—'}</td>
                <td><div className="table-actions">
                  <button className="btn btn-ghost btn-sm btn-icon" onClick={()=>setView(e)} title="View"><Eye size={14}/></button>
                  <button className="btn btn-ghost btn-sm btn-icon" onClick={()=>openEdit(e)} title="Edit"><Edit2 size={14}/></button>
                  <button className="btn btn-danger btn-sm btn-icon" onClick={()=>handleDelete(e.id)} title="Terminate"><Trash2 size={14}/></button>
                </div></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Pagination */}
      {total > 10 && (
        <div style={{display:'flex',gap:8,justifyContent:'center',marginTop:20}}>
          <button className="btn btn-ghost btn-sm" disabled={page===0} onClick={()=>setPage(p=>p-1)}>← Prev</button>
          <span style={{padding:'8px 16px',fontSize:14,color:'var(--text2)'}}>Page {page+1}</span>
          <button className="btn btn-ghost btn-sm" disabled={(page+1)*10>=total} onClick={()=>setPage(p=>p+1)}>Next →</button>
        </div>
      )}

      {/* View Modal */}
      {view && (
        <div className="modal-overlay" onClick={()=>setView(null)}>
          <div className="modal-box" onClick={e=>e.stopPropagation()} style={{maxWidth:640}}>
            <div style={{display:'flex',alignItems:'center',gap:16,marginBottom:24}}>
              <div className="avatar avatar-xl">{view.firstName?.[0]}</div>
              <div><h2 style={{fontSize:22}}>{view.fullName}</h2>
                <p style={{color:'var(--text2)'}}>{view.designation} · {view.departmentName}</p>
                <span className={`badge badge-${STATUS_COLORS[view.status]}`}>{view.status}</span>
              </div>
            </div>
            <div className={styles.viewGrid}>
              {[['Employee ID',view.employeeId],['Email',view.email],['Phone',view.phone||'—'],['Gender',view.gender],['Joining Date',view.joiningDate||'—'],['Employment',view.employmentType],['Basic Salary',fmt(view.basicSalary)],['HRA',fmt(view.hra)],['Allowances',fmt(view.allowances)],['Net Salary',fmt(view.netSalary)],['PAN',view.panNumber||'—'],['Bank',view.bankName||'—']].map(([k,v])=>(
                <div key={k} className={styles.viewField}><span className={styles.viewKey}>{k}</span><span className={styles.viewVal}>{v}</span></div>
              ))}
            </div>
            <div className="modal-footer"><button className="btn btn-ghost" onClick={()=>setView(null)}>Close</button></div>
          </div>
        </div>
      )}

      {/* Create/Edit Modal */}
      {showModal && (
        <div className="modal-overlay" onClick={()=>setShowModal(false)}>
          <div className="modal-box" onClick={e=>e.stopPropagation()} style={{maxWidth:640}}>
            <h3 className="modal-title">{editing?'Edit Employee':'Add New Employee'}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-grid">
                <div className="form-group"><label className="form-label">First Name *</label><input className="input" value={form.firstName} onChange={set('firstName')} required/></div>
                <div className="form-group"><label className="form-label">Last Name *</label><input className="input" value={form.lastName} onChange={set('lastName')} required/></div>
                <div className="form-group"><label className="form-label">Email *</label><input className="input" type="email" value={form.email} onChange={set('email')} required disabled={!!editing}/></div>
                <div className="form-group"><label className="form-label">Phone</label><input className="input" value={form.phone} onChange={set('phone')}/></div>
                <div className="form-group"><label className="form-label">Designation</label><input className="input" value={form.designation} onChange={set('designation')}/></div>
                <div className="form-group"><label className="form-label">Department *</label>
                  <select className="input" value={form.departmentId} onChange={set('departmentId')} required>
                    <option value="">Select...</option>
                    {departments.map(d=><option key={d.id} value={d.id}>{d.name}</option>)}
                  </select>
                </div>
                <div className="form-group"><label className="form-label">Basic Salary (₹) *</label><input className="input" type="number" value={form.basicSalary} onChange={set('basicSalary')} required/></div>
                <div className="form-group"><label className="form-label">HRA (₹)</label><input className="input" type="number" value={form.hra} onChange={set('hra')}/></div>
                <div className="form-group"><label className="form-label">Allowances (₹)</label><input className="input" type="number" value={form.allowances} onChange={set('allowances')}/></div>
                <div className="form-group"><label className="form-label">Deductions (₹)</label><input className="input" type="number" value={form.deductions} onChange={set('deductions')}/></div>
                <div className="form-group"><label className="form-label">Joining Date</label><input className="input" type="date" value={form.joiningDate} onChange={set('joiningDate')}/></div>
                <div className="form-group"><label className="form-label">Gender</label>
                  <select className="input" value={form.gender} onChange={set('gender')}><option>MALE</option><option>FEMALE</option><option>OTHER</option></select>
                </div>
                <div className="form-group"><label className="form-label">Employment Type</label>
                  <select className="input" value={form.employmentType} onChange={set('employmentType')}><option>FULL_TIME</option><option>PART_TIME</option><option>CONTRACT</option><option>INTERN</option></select>
                </div>
                {!editing && <div className="form-group"><label className="form-label">Password</label><input className="input" value={form.password} onChange={set('password')} placeholder="Hrms@123"/></div>}
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-ghost" onClick={()=>setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>
                  {saving&&<span className="spinner-sm" style={{borderColor:'rgba(255,255,255,0.3)',borderTopColor:'#fff'}}/>}
                  {saving?'Saving...':editing?'Save Changes':'Create Employee'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}