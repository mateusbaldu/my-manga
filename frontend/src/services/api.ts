import axios from 'axios'
import { useAuthStore } from '../store/authStore'

const api = axios.create({
  baseURL: '/my-manga',
  headers: {
    'Content-Type': 'application/json',
  },
})

api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      useAuthStore.getState().logout()
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  name: string
  email: string
  username: string
  password: string
}

export interface Manga {
  id: number
  title: string
  author: string
  description: string
  rating: number
  keywords: string
  status: string
  genres: string
  volumes: Volume[]
}

export interface Volume {
  id: number
  volumeNumber: number
  price: number
  chapters: string
  releaseDate: string
  quantity: number
  mangaId: number
  mangaTitle: string
}

export interface Order {
  id: number
  createdAt: string
  finalPrice: number
  paymentMethod: string
  status: string
  items: OrderItem[]
  username: string
}

export interface OrderItem {
  mangaTitle: string
  volumeNumber: number
  quantity: number
  unitPrice: number
}

export interface Address {
  id: number
  cep: string
  street: string
  number: string
  complement: string
  locality: string
  city: string
  state: string
}

export const authApi = {
  login: (data: LoginRequest) => api.post('/login', data),
  register: (data: RegisterRequest) => api.post('/users/new', data),
  forgotPassword: (email: string) => api.post('/login/forgot-password', { email }),
}

export const mangaApi = {
  getAll: (page = 0, size = 12) => api.get<any>(`/mangas/all?page=${page}&size=${size}`),
  getById: (id: number) => api.get<Manga>(`/mangas/${id}`),
  search: (keyword: string, page = 0, size = 12) =>
    api.get<any>(`/mangas/search/${keyword}?page=${page}&size=${size}`),
  create: (data: any) => api.post('/mangas/new', data),
  update: (id: number, data: any) => api.patch(`/mangas/${id}`, data),
  delete: (id: number) => api.delete(`/mangas/${id}`),
}

export const volumeApi = {
  getAll: (mangaId: number, page = 0, size = 20) =>
    api.get<any>(`/mangas/${mangaId}/volumes/all?page=${page}&size=${size}`),
  getById: (mangaId: number, volumeId: number) =>
    api.get<Volume>(`/mangas/${mangaId}/volumes/${volumeId}`),
  create: (mangaId: number, data: any[]) => api.post(`/mangas/${mangaId}/volumes/new`, data),
  update: (mangaId: number, volumeId: number, data: any) =>
    api.patch(`/mangas/${mangaId}/volumes/${volumeId}`, data),
  delete: (mangaId: number, volumeId: number) =>
    api.delete(`/mangas/${mangaId}/volumes/${volumeId}`),
}

export const orderApi = {
  create: (data: { paymentMethod: string; items: { volumeId: number; quantity: number }[] }) =>
    api.post<Order>('/orders/new', data),
  getById: (id: number) => api.get<Order>(`/orders/${id}`),
  getByUsername: (username: string, page = 0, size = 10) =>
    api.get<any>(`/orders/user/${username}?page=${page}&size=${size}`),
  getAll: (page = 0, size = 10) => api.get<any>(`/orders/all?page=${page}&size=${size}`),
  update: (id: number, data: any) => api.put(`/orders/${id}`, data),
  delete: (id: number) => api.delete(`/orders/${id}`),
}

export const userApi = {
  getByUsername: (username: string) => api.get(`/users?username=${username}`),
  update: (username: string, data: any) => api.patch(`/users/${username}`, data),
  delete: (id: number) => api.delete(`/users/${id}`),
}

export const addressApi = {
  getAll: (username: string, page = 0, size = 10) =>
    api.get<any>(`/users/${username}/address/all?page=${page}&size=${size}`),
  getById: (username: string, addressId: number) =>
    api.get<Address>(`/users/${username}/address/${addressId}`),
  create: (username: string, data: { cep: string; number: string; complement: string }) =>
    api.post<Address>(`/users/${username}/address/new`, data),
  update: (username: string, addressId: number, data: any) =>
    api.patch(`/users/${username}/address/${addressId}`, data),
  delete: (username: string, addressId: number) =>
    api.delete(`/users/${username}/address/${addressId}`),
}

export default api
