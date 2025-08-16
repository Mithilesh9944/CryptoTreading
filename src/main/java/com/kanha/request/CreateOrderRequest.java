package com.kanha.request;

import com.kanha.domain.OrderType;
import lombok.Data;

@Data
public class CreateOrderRequest {
    private String coinId;
    private double quantity;
    private OrderType orderType;

    @Override
    public String toString() {
        return "CreateOrderRequest{" +
                "coinId='" + coinId + '\'' +
                ", quantity=" + quantity +
                ", orderType=" + orderType +
                '}';
    }
}
