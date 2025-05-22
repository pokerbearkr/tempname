package org.example.siljeun.domain.reservation.dto.request;

import org.example.siljeun.domain.reservation.enums.Discount;
import org.example.siljeun.domain.reservation.enums.TicketReceipt;
import org.example.siljeun.domain.reservation.validation.ValidEnum;

public record UpdatePriceRequest(
    @ValidEnum(enumClass = TicketReceipt.class)
    String ticketReceipt,
    @ValidEnum(enumClass = Discount.class)
    String discount
) {

}
