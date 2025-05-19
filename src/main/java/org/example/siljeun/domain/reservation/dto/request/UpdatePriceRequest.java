package org.example.siljeun.domain.reservation.dto.request;

public record UpdatePriceRequest(
    String ticketReceipt,
    String discount
) {

}
