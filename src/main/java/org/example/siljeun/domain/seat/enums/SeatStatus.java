package org.example.siljeun.domain.seat.enums;

public enum SeatStatus {
    BLOCKED,    //미판매
    CANCELLED,  //취소 - 취소표는 특정 시간대에 한 번에 풀어놓는 상태를 고려하여 넣어놓았으나 현재 구현 상태에서는 사용하지 않음
    RESERVED,   //예매됨
    HOLD,       //결제 진행 중
    SELECTED,   //선택됨
    AVAILABLE   //빈 좌석
}
