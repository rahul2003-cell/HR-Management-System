import { useState, useEffect } from 'react';
import { Plus, Edit2, Trash2, Building2, Users } from 'lucide-react';
import { deptApi } from '../../api';
import toast from 'react-hot-toast';

const DEPT_COLORS = ['purple','cyan','green','amber','danger'];

export default function Departments() {
  const [depts,setDepts] = useState([]); const [loading,setLoading] = useState(true);
  const [showModal,setShowModal] = useState(false); const [editing,setEditing] = useState(null);
  const [form,setForm] = useState({name:'',description:'',headName:''});
  const [saving,setSaving] = useState(false);
  const load = () => { setLoading(true); deptApi.getAll().then(r=>setDepts(r.data||[])).finally(()=>setLoading(false)); };
  useEffect(load,[]);
  const set = k => e => setForm(f=>({...f,[k]:e.target.value}));
  const openCreate = () => { setEditing(null); setForm({name:'',description:'',headName:''}); setShowModal(true); };
  const openEdit = d => { setEditing(d); setForm({name:d.name,description:d.description||'',headName:d.headName||''}); setShowModal(true); };
  const handleSubmit = async ev => {
    ev.preventDefault(); setSaving(true);
    try {
      if(editing){ await deptApi.update(editing.id,form); toast.success('Updated'); }
      else { await deptApi.create(form); toast.success('Department created'); }
      setShowModal(false); load();
    } catch(e){ toast.error(e?.message||'Failed'); } finally { setSaving(false); }
  };
  const handleDelete = async id => { if(!window.confirm('Delete this department?')) return; await deptApi.delete(id); toast.success('Deleted'); load(); };
  return (
    <div>
      <div className="page-header" style={{display:'flex',alignItems:'flex-start',justifyContent:'space-between'}}>
        <div><h1 className="page-title">Departments</h1><p className="page-subtitle">{depts.length} departments configured</p></div>
        <button className="btn btn-primary" onClick={openCreate}><Plus size={16}/>Add Department</button>
      </div>
      <div style={{display:'grid',gridTemplateColumns:'repeat(auto-fill,minmax(280px,1fr))',gap:20}}>
        {loading ? Array(5).fill(0).map((_,i)=><div key={i} className="skeleton" style={{height:180,borderRadius:20}}/>) :
          depts.map((d,i)=>(
            <div key={d.id} className="glass stat-card" style={{padding:24}}>
              <div style={{display:'flex',alignItems:'flex-start',justifyContent:'space-between',marginBottom:16}}>
                <div className={`stat-icon ${DEPT_COLORS[i%DEPT_COLORS.length]}`}><Building2 size={22}/></div>
                <div style={{display:'flex',gap:6}}>
                  <button className="btn btn-ghost btn-sm btn-icon" onClick={()=>openEdit(d)}><Edit2 size={13}/></button>
                  <button className="btn btn-danger btn-sm btn-icon" onClick={()=>handleDelete(d.id)}><Trash2 size={13}/></button>
                </div>
              </div>
              <h3 style={{fontSize:18,fontWeight:700,marginBottom:4}}>{d.name}</h3>
              <p style={{fontSize:13,color:'var(--text2)',marginBottom:12}}>{d.description||'No description'}</p>
              {d.headName && <p style={{fontSize:12,color:'var(--text3)',marginBottom:12}}>Head: <strong style={{color:'var(--text2)'}}>{d.headName}</strong></p>}
              <div style={{display:'flex',alignItems:'center',gap:8}}>
                <Users size={14} style={{color:'var(--primary-light)'}}/><span style={{fontSize:14,fontWeight:700}}>{d.employeeCount}</span><span style={{fontSize:13,color:'var(--text2)'}}>employees</span>
              </div>
            </div>
          ))}
      </div>
      {showModal && (
        <div className="modal-overlay" onClick={()=>setShowModal(false)}>
          <div className="modal-box" onClick={e=>e.stopPropagation()}>
            <h3 className="modal-title">{editing?'Edit Department':'New Department'}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-grid">
                <div className="form-group full"><label className="form-label">Department Name *</label><input className="input" value={form.name} onChange={set('name')} required/></div>
                <div className="form-group full"><label className="form-label">Description</label><input className="input" value={form.description} onChange={set('description')}/></div>
                <div className="form-group full"><label className="form-label">Department Head</label><input className="input" value={form.headName} onChange={set('headName')}/></div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn btn-ghost" onClick={()=>setShowModal(false)}>Cancel</button>
                <button type="submit" className="btn btn-primary" disabled={saving}>{saving?'Saving...':editing?'Update':'Create'}</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}