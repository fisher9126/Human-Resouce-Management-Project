import axios from "axios";



const api = axios.create({
  baseURL: '/api/v1',
  headers: { 'Content-Type': 'application/json' },
});

// Gan token vao moi request
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('hrm_token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  // Voi FormData: xoa Content-Type de trinh duyet tu set kem boundary
  if (config.data instanceof FormData) {
    delete config.headers['Content-Type'];
  }
  return config;
});

// Xu ly loi chung: 401 -> logout
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response && err.response.status === 401) {
      localStorage.removeItem('hrm_token');
      localStorage.removeItem('hrm_user');
      if (!window.location.pathname.includes('/login')) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(err);
  }
);

export default api;
