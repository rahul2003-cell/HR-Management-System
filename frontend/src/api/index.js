import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 15000,
});

api.interceptors.request.use(cfg => {
  const token = localStorage.getItem('hrms_token');
  if (token) cfg.headers.Authorization = `Bearer ${token}`;
  return cfg;
});

api.interceptors.response.use(
  res => res.data,
  err => {
    if (err.response?.status === 401) {
      localStorage.removeItem('hrms_token');
      localStorage.removeItem('hrms_user');
      window.location.href = '/login';
    }
    return Promise.reject(err.response?.data || err);
  }
);

export const authApi = {
  login: d => api.post('/auth/login', d),
  me: () => api.get('/auth/me'),
};

export const empApi = {
  getAll: p => api.get('/employees', { params: p }),
  search: p => api.get('/employees/search', { params: p }),
  getById: id => api.get(`/employees/${id}`),
  getMe: () => api.get('/employees/me'),
  create: d => api.post('/employees', d),
  update: (id, d) => api.put(`/employees/${id}`, d),
  delete: id => api.delete(`/employees/${id}`),
};

export const deptApi = {
  getAll: () => api.get('/departments'),
  create: d => api.post('/departments', d),
  update: (id, d) => api.put(`/departments/${id}`, d),
  delete: id => api.delete(`/departments/${id}`),
};

export const attendanceApi = {
  getByDate: date => api.get(`/attendance/date/${date}`),
  getByEmployee: (id, from, to) => api.get(`/attendance/employee/${id}`, { params: { from, to } }),
  getSummary: (id, from, to) => api.get(`/attendance/summary/${id}`, { params: { from, to } }),
  mark: d => api.post('/attendance/mark', d),
  checkIn: d => api.post('/attendance/checkin', d),
};

export const leaveApi = {
  getAll: p => api.get('/leaves', { params: p }),
  getPending: () => api.get('/leaves/pending'),
  getByEmployee: (id, p) => api.get(`/leaves/employee/${id}`, { params: p }),
  apply: (empId, d) => api.post(`/leaves/apply/${empId}`, d),
  action: (id, d) => api.patch(`/leaves/${id}/action`, d),
};

export const payrollApi = {
  getByEmployee: id => api.get(`/payroll/employee/${id}`),
  getMonthly: (month, year) => api.get('/payroll/monthly', { params: { month, year } }),
  generate: d => api.post('/payroll/generate', d),
  markPaid: id => api.patch(`/payroll/${id}/pay`),
  downloadSlip: id => `${import.meta.env.VITE_API_URL || 'http://localhost:8080/api'}/payroll/${id}/slip/pdf`,
  downloadExcel: (m, y) => `${import.meta.env.VITE_API_URL || 'http://localhost:8080/api'}/payroll/monthly/excel?month=${m}&year=${y}`,
};

export const adminApi = {
  dashboard: () => api.get('/admin/dashboard'),
  exportEmployees: () => `${import.meta.env.VITE_API_URL || 'http://localhost:8080/api'}/admin/export/employees`,
};

export default api;