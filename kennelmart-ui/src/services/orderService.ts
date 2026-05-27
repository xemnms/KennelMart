import api from '../api/axios';
import { OrderResponse } from '../types/order.ts';

export const orderService = {
    async getMyOrders(page: number = 0, size: number = 10): Promise<{
        content: OrderResponse[];
        totalPages: number;
        totalElements: number;
    }> {
        const response = await api.get(`/api/orders/my-orders?page=${page}&size=${size}`);
        return response.data;
    },

    async getOrderById(orderId: string): Promise<OrderResponse> {
        const response = await api.get(`/api/orders/${orderId}`);
        return response.data;
    },

    async getMySales(page: number = 0, size: number = 10): Promise<{
        content: OrderResponse[];
        totalPages: number;
        totalElements: number;
    }> {
        const response = await api.get(`/api/orders/my-sales?page=${page}&size=${size}`);
        return response.data;
    },

    async updateOrderStatus(orderId: string, status: string): Promise<OrderResponse> {
        const response = await api.put(`/api/orders/${orderId}/status?status=${status}`);
        return response.data;
    },
};