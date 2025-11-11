package com.ali.payment;

import com.ali.common.Dto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.Map;
import java.util.UUID;

@Configuration
@EnableKafkaStreams
public class PaymentStreamsConfig {
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    KafkaTemplate<String, Dto.paymentPayload> kafkaTemplate;

    @Bean
    KStream<String, Dto.orderPayload> orderStream(StreamsBuilder streamsBuilder)
    {
        JsonSerde<Dto.orderPayload> orderSerde = new JsonSerde<>(Dto.orderPayload.class);
        JsonSerde<Dto.paymentPayload> paymentSerde = new JsonSerde<>(Dto.paymentPayload.class);

        orderSerde.deserializer().addTrustedPackages("*");
        orderSerde.serializer().setAddTypeInfo(false);
        paymentSerde.serializer().setAddTypeInfo(false);

        KStream<String, Dto.orderPayload> kStream = streamsBuilder.stream("orders.created", Consumed.with(Serdes.String(),orderSerde));

        kStream.peek((k,v) -> {
            String paymentId = UUID.randomUUID().toString();
            var payment = new Dto.paymentPayload(paymentId, v.orderId(), true);
            System.out.println("Order received order At Payment : "+v.orderId());
            kafkaTemplate.send("payments.event",v.orderId().toString(),payment);
        });
        return kStream;
    }
}
