package org.example.siljeun.domain.seatscheduleinfo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.siljeun.domain.schedule.entity.Schedule;
import org.example.siljeun.domain.seat.entity.Seat;
import org.example.siljeun.domain.seat.enums.SeatStatus;
import org.example.siljeun.global.entity.BaseEntity;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seat_schedule_info")
public class SeatScheduleInfo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    //회차 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    private SeatStatus status;
    private String grade;
    private int price;

    public SeatScheduleInfo(Seat seat, Schedule schedule, SeatStatus status, String grade, int price) {
        this.seat = seat;
        this.schedule = schedule;
        this.status = status;
        this.grade = grade;
        this.price = price;
    }

    public static SeatScheduleInfo from(Seat seat, Schedule schedule, SeatStatus status, String grade, int price) {
        return new SeatScheduleInfo(seat, schedule, status, grade, price);
    }

    public void updateSeatScheduleInfoStatus(SeatStatus status){
        this.status = status;
    }

    public boolean isAvailable(){
        return this.status == SeatStatus.AVAILABLE;
    }
}
