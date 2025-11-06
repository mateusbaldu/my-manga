import { create } from 'zustand'
import { persist } from 'zustand/middleware'

interface User {
  username: string
  name: string
  roles: Array<{ id: number; name: string }>
}

interface AuthState {
  token: string | null
  user: User | null
  isAuthenticated: boolean
  isAdmin: boolean
  setAuth: (token: string, user: User) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      isAuthenticated: false,
      isAdmin: false,
      setAuth: (token, user) =>
        set({
          token,
          user,
          isAuthenticated: true,
          isAdmin: user.roles.some((role) => role.name === 'ADMIN'),
        }),
      logout: () =>
        set({
          token: null,
          user: null,
          isAuthenticated: false,
          isAdmin: false,
        }),
    }),
    {
      name: 'auth-storage',
    }
  )
)
