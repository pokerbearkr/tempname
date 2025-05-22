package org.example.siljeun.domain.seat.enums;

public enum SeatStatus {
    BLOCKED,    //미판매
    CANCELLED,  //취소
    RESERVED,   //예매됨
    HOLD,       //결제 진행 중
    SELECTED,   //선택됨
    AVAILABLE   //빈 좌석
}
