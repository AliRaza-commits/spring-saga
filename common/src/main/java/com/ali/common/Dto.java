package com.ali.common;

import java.io.Serializable;
import java.math.BigDecimal;

public class Dto {
    public static record orderPayload(Long orderId, BigDecimal amount, boolean forcePaymentFail, boolean forceStockFail) implements Serializable {};
    public static record paymentPayload(String paymentId, Long orderId,boolean success) implements Serializable {}
    public static record stockPayload(String stockId, Long orderId, boolean success) implements Serializable {}
}
