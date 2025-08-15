Payments Endpoints (Detailed)

Base: `/api/payments`

Confirm Payment (Toss)

-   Method: POST `/api/payments/confirm`
-   Source: [TossPaymentController.kt: confirmPayment](https://github.com/jjoonleo/Bookquiz-back/blob/main/src/main/kotlin/kr/co/bookquiz/api/controller/TossPaymentController.kt#L12-L25)
-   Request

```json
{
    "paymentKey": "string",
    "orderId": "string",
    "amount": 1000
}
```

-   Response

```json
{
    "paymentKey": "string",
    "orderId": "string",
    "amount": 1000,
    "status": "CONFIRMED"
}
```

Notes

-   The response fields map to your `TossPayment` entity/status; extend the DTO as needed.
