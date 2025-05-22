package org.example.siljeun.domain.reservation.dto.response;

import java.time.LocalDateTime;
import org.example.siljeun.domain.reservation.entity.Reservation;
import org.example.siljeun.domain.reservation.enums.Discount;
import org.example.siljeun.domain.reservation.enums.TicketReceipt;

public record ReservationInfoResponse(
    Long id,
    Long userId,
    Long concertId,
    String concertTitle,
    String venueName,
    LocalDateTime startTime,
    String seatGrade,
    String seatSection,
    String seatRow,
    String seatNumber,
    TicketReceipt ticketReceipt,
    int price,
    Discount discount,
    LocalDateTime cancelDeadline
) {

  public static ReservationInfoResponse from(Reservation reservation) {
    return new ReservationInfoResponse(
        reservation.getId(),
        reservation.getUser().getId(),
        reservation.getSeatScheduleInfo().getSchedule().getConcert().getId(),
        reservation.getSeatScheduleInfo().getSchedule().getConcert().getTitle(),
        reservation.getSeatScheduleInfo().getSeat().getVenue().getName(),
        reservation.getSeatScheduleInfo().getSchedule().getStartTime(),
        reservation.getSeatScheduleInfo().getGrade(),
        reservation.getSeatScheduleInfo().getSeat().getSection(),
        reservation.getSeatScheduleInfo().getSeat().getRow(),
        reservation.getSeatScheduleInfo().getSeat().getColumn(),
        reservation.getTicketReceipt(),
        reservation.getPrice(),
        reservation.getDiscount(),
        reservation.getSeatScheduleInfo().getSchedule().getStartTime().minusDays(1)
    );
  }
}
