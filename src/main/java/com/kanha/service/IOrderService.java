package com.kanha.service;

import com.kanha.domain.OrderType;
import com.kanha.model.Coin;
import com.kanha.model.Order;
import com.kanha.model.OrderItem;
import com.kanha.model.User;

import java.util.List;

public interface IOrderService {
    Order createOrder(User user, OrderItem orderItem, OrderType orderType);
    Order getOrderById(Long orderId) throws Exception;
    List<Order> getAllOrdersOfUser(Long userId,OrderType orderType,String assetSymbol);
    Order processOrder(Coin coin,double quantity,OrderType orderType,User user) throws Exception;
}
