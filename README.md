# 🧩 Spring Boot Kafka Saga Orchestration Example

This project demonstrates a **Saga pattern** implementation using **Spring Boot Microservices**, **Apache Kafka**, and **Kafka Streams** for distributed transaction coordination.

![](/images/Intro.jpg)

---

## 🚀 Architecture Overview

### 💡 Microservices

| Service | Port | Description |
|----------|------|-------------|
| **Order Service** | `8080` | Accepts order requests and starts the Saga. Publishes `orders.created` events. |
| **Payment Service** | `8081` | Listens for `orders.created`, processes payment, and publishes `payments.event`. |
| **Stock Service** | `8082` | Listens for `orders.created`, checks stock, and publishes `stocks.event`. |
| **Postgres DBs** | 5443 / 5444 / 5445 | Each microservice has its own isolated database. |
| **Kafka & Zookeeper** | 9092 / 2181 | Message broker used for Saga event communication. |

---

## 🧱 Saga Flow

OrderService → PaymentService → StockService → OrderService (finalize)


### 1️⃣ Order Created  
A new order is created via REST endpoint:
```bash
POST http://localhost:8080/orders/create
Content-Type: application/json

{
  "amount": 250
}

```
→ Publishes event to topic orders.created



## 2️⃣ Payment Processed
PaymentService listens to orders.created.
It processes payment and emits event:
```
Topic: payments.event
Payload: { orderId: 1, paymentId: "...", success: true }
```

✅ Log example:
```
Order received at Payment : 1
```
## 3️⃣ Stock Reserved
```
Topic: stocks.event
Payload: { orderId: 1, stockId: "...", success: true }
```
✅ Log example:
```
Order received at Stock : 1
```

## 4️⃣ Order Finalized
OrderService listens to both payments.event and stocks.event.

If both are successful → marks order COMPLETED
Otherwise → marks order REFUSED and triggers rollback.

✅ Log example:
```
Order Completed : 1  Status: COMPLETED
```


## 🧩 Project Structure
my-saga-project/
│
├── common/              # Shared DTOs & utilities
├── order/               # Order microservice (starts Saga)
├── payment/             # Payment microservice
├── stock/               # Stock microservice
└── docker-compose.yml   # Kafka + Zookeeper + Postgres


🐳 Docker Setup
1️⃣ Start infrastructure (Kafka, Zookeeper, DBs)

From the project root, run:
```
docker-compose up -d
```

This will start:

Zookeeper (port 2181)

Kafka broker (port 9092)

Schema registry (port 8081)

Postgres DBs for each microservice (ports 5443–5445)

✅ Verify Kafka topics:

```
docker exec -it <kafka-container-name> kafka-topics --list --bootstrap-server localhost:9092
```

## ⚙️ Build & Run Services

Each microservice is a separate Spring Boot app.
Run them one by one in order.

🏗️ Step 1: Clean and package
mvn clean package -DskipTests

🧾 Step 2: Start services
Order Service
```
cd order
mvn spring-boot:run
```

Payment Service
```
cd payment
mvn spring-boot:run
```

Stock Service
```
cd stock
mvn spring-boot:run
```


and the other service will rollback.

🧰 Technologies Used

* Spring Boot 3.3+

* Spring Kafka / Kafka Streams

* PostgreSQL

* Docker & Docker Compose

* Maven (multi-module project)