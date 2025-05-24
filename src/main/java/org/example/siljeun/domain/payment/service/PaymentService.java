package org.example.siljeun.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.payment.dto.PaymentConfirmRequestDto;
import org.example.siljeun.domain.payment.entity.Payment;
import org.example.siljeun.domain.payment.repository.PaymentRepository;
import org.example.siljeun.domain.reservation.service.ReservationService;
import org.example.siljeun.domain.seat.enums.SeatStatus;
import org.example.siljeun.domain.seatscheduleinfo.service.SeatScheduleInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final PaymentRepository paymentRepository;
  private final ReservationService reservationService;
  private final SeatScheduleInfoService seatScheduleInfoService;

  @Transactional
  public void savePayment(PaymentConfirmRequestDto dto) {
    Payment payment = Payment.builder()
        .paymentKey(dto.getPaymentKey())
        .seatScheduleInfoId(dto.getSeatScheduleInfoId())
        .amount(dto.getAmount())
        .build();

    paymentRepository.save(payment);

    //seatScheduleInfoService.updateSeatSchedulerInfoStatus(dto.getSeatScheduleInfoId(), SeatStatus.RESERVED);
    reservationService.save(dto.getUserId(), dto.getSeatScheduleInfoId());
  }
}
