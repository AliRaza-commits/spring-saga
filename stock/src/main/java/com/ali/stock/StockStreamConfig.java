package com.ali.stock;

import com.ali.common.Dto;
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

import java.util.UUID;

@Configuration
@EnableKafkaStreams
public class StockStreamConfig {
    @Autowired
    private KafkaTemplate<String, Dto.stockPayload> kafkaTemplate;

    @Bean
    KStream<String, Dto.paymentPayload> stockStreams(StreamsBuilder streamsBuilder)
    {
        var paymentSerdes = new JsonSerde<>(Dto.paymentPayload.class);
        var stockSerdes = new JsonSerde<>(Dto.stockPayload.class);

        KStream<String, Dto.paymentPayload> payloadKStream = streamsBuilder.stream("payments.event", Consumed.with(Serdes.String(), paymentSerdes));

        payloadKStream.peek((k,v) -> {
            String stockId = UUID.randomUUID().toString();
            var stock = new Dto.stockPayload(stockId, v.orderId(), true);
            System.out.println("Order received order At Stock: "+v.orderId());
            kafkaTemplate.send("stocks.event",v.orderId().toString(),stock);
        });
        return payloadKStream;
    }

}
