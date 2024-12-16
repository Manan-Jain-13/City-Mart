package com.ecom.service;

import com.ecom.entity.OrderRequest;
import com.ecom.entity.ProductOrder;

import java.util.List;

public interface OrderService {

    public void  saveOrder(Integer userId, OrderRequest orderRequest) throws Exception;

    public List<ProductOrder> getOrderByUser(Integer userId);

    public ProductOrder updateOrderStatus(Integer id,String status);

    public List<ProductOrder> getAllOrders();

    public ProductOrder getOrdersByOrderId(String orderId);

}
