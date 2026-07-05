package com.ecommerce.payment.model;

import java.math.BigDecimal;

public class PaymentRequest {
    private Long orderId;
    private Long userId;
    private BigDecimal amount;
    private Payment.PaymentMethod method;

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Payment.PaymentMethod getMethod() { return method; }
    public void setMethod(Payment.PaymentMethod method) { this.method = method; }
}
