package com.ali.order;

import com.ali.common.Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private OrderRepository orderRepository;
    @PostMapping("/create")
    public Order createOrder(@RequestBody Dto.orderPayload req) {
        Order o = new Order();
        o.setAmount(req.amount());
        o.setStatus(OrderStatus.NEW);
        orderRepository.save(o);

        var payload = new Dto.orderPayload(o.getId(),o.getAmount(),false, false);
        System.out.println("Send Order data from Order"+payload);
        kafkaTemplate.send("orders.created", o.id.toString() ,payload);
        return o;
    }
}
