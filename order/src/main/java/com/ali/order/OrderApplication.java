package com.ali.order;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
public class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}

	@Bean
	public NewTopic createOrderTopic()
	{
		return TopicBuilder.name("orders.created")
				.partitions(1)
				.replicas(1)
				.build();
	}

	@Bean
	public NewTopic createPaymentTopic()
	{
		return TopicBuilder.name("payments.event")
				.partitions(1)
				.replicas(1)
				.build();
	}

	@Bean
	public NewTopic createStockTopic()
	{
		return TopicBuilder.name("stocks.event")
				.partitions(1)
				.replicas(1)
				.build();
	}

}
