export interface OrderItemResponse {
  mangaTitle: string;
  volumeNumber: number;
  quantity: number;
  unitPrice: number;
}

export interface OrderResponse {
  id: number;
  createdAt: string;
  finalPrice: number;
  paymentMethod: string;
  status: string;
  items: OrderItemResponse[];
  username: string;
}
