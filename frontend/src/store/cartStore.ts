import { create } from 'zustand'
import { persist } from 'zustand/middleware'

export interface CartItem {
  volumeId: number
  mangaTitle: string
  volumeNumber: number
  price: number
  quantity: number
  maxQuantity: number
}

interface CartState {
  items: CartItem[]
  addItem: (item: CartItem) => void
  removeItem: (volumeId: number) => void
  updateQuantity: (volumeId: number, quantity: number) => void
  clearCart: () => void
  getTotalPrice: () => number
  getTotalItems: () => number
}

export const useCartStore = create<CartState>()(
  persist(
    (set, get) => ({
      items: [],
      addItem: (item) =>
        set((state) => {
          const existingItem = state.items.find((i) => i.volumeId === item.volumeId)
          if (existingItem) {
            return {
              items: state.items.map((i) =>
                i.volumeId === item.volumeId
                  ? { ...i, quantity: Math.min(i.quantity + item.quantity, i.maxQuantity) }
                  : i
              ),
            }
          }
          return { items: [...state.items, item] }
        }),
      removeItem: (volumeId) =>
        set((state) => ({
          items: state.items.filter((item) => item.volumeId !== volumeId),
        })),
      updateQuantity: (volumeId, quantity) =>
        set((state) => ({
          items: state.items.map((item) =>
            item.volumeId === volumeId
              ? { ...item, quantity: Math.min(quantity, item.maxQuantity) }
              : item
          ),
        })),
      clearCart: () => set({ items: [] }),
      getTotalPrice: () => {
        const state = get()
        return state.items.reduce((total, item) => total + item.price * item.quantity, 0)
      },
      getTotalItems: () => {
        const state = get()
        return state.items.reduce((total, item) => total + item.quantity, 0)
      },
    }),
    {
      name: 'cart-storage',
    }
  )
)
