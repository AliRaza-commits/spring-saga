package com.ali.order;

import com.ali.common.Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
public class SagaListener {
    @Autowired
    private OrderRepository orderRepository;

    private ConcurrentHashMap<Long, Set<String>> confirmed = new ConcurrentHashMap<>();

//    @KafkaListener(topics = "payments.event", groupId = "order-service-group")
//    public void onPayment(Dto.paymentPayload paymentPayload)
//    {
//        System.out.println("FInal:"+paymentPayload);
//        if (paymentPayload.success())
//        {
//            confirmed.computeIfAbsent(paymentPayload.orderId().toString(),(k) -> new CopyOnWriteArraySet<>()).add("Payment");
//            confirmCheck(paymentPayload.orderId());
//        }
//    }

    @KafkaListener(topics = "stocks.event", groupId = "order-service-group")
    public void onStock(Dto.stockPayload stockPayload)
    {
        if (stockPayload.success())
        {
            confirmed.computeIfAbsent(stockPayload.orderId(),(k) -> new CopyOnWriteArraySet<>()).add("Stock");
            confirmCheck(stockPayload.orderId());
        }
    }

    public void confirmCheck(Long orderId)
    {
        var set = confirmed.getOrDefault(orderId,Set.of());
        if (set.contains("Stock"))
        {
            Order o = orderRepository.findById(orderId).orElse(null);
            o.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(o);
            System.out.println("Order Completed : "+o.id+" Status: "+o.status);
        }

    }

}
