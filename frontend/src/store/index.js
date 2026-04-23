import { create } from 'zustand';
import { persist } from 'zustand/middleware';

export const useAuthStore = create(
  persist(
    (set) => ({
      user: null,
      token: null,
      isAuthenticated: false,
      login: (token, user) => {
        localStorage.setItem('hrms_token', token);
        set({ token, user, isAuthenticated: true });
      },
      logout: () => {
        localStorage.removeItem('hrms_token');
        localStorage.removeItem('hrms_user');
        set({ token: null, user: null, isAuthenticated: false });
      },
      updateUser: u => set(s => ({ user: { ...s.user, ...u } })),
    }),
    { name: 'hrms-auth', partialize: s => ({ user: s.user, token: s.token, isAuthenticated: s.isAuthenticated }) }
  )
);

export const useUIStore = create(set => ({
  sidebarOpen: true,
  mobileSidebarOpen: false,
  toggleSidebar: () => set(s => ({ sidebarOpen: !s.sidebarOpen })),
  toggleMobileSidebar: () => set(s => ({ mobileSidebarOpen: !s.mobileSidebarOpen })),
  closeMobileSidebar: () => set({ mobileSidebarOpen: false }),
}));