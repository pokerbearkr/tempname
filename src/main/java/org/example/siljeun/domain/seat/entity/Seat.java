package org.example.siljeun.domain.seat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.siljeun.domain.seat.dto.request.SeatCreateRequest;
import org.example.siljeun.domain.venue.entity.Venue;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(
    name = "seat",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"venue_id", "section", "seat_row", "seat_column"}) //O공연장의 O구역 O열 O좌석은 고유하다
    }
)
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //공연장 FK
    @ManyToOne
    @JoinColumn(name = "venue_id")
    private Venue venue;

    private String section;
    @Column(name = "seat_row")
    private String row;
    @Column(name = "seat_column")
    private String column;
    private String defaultGrade;
    private int defaultPrice;

    public Seat(Venue venue, String section, String row, String column, String defaultGrade, int defaultPrice) {
        this.venue = venue;
        this.section = section;
        this.row = row;
        this.column = column;
        this.defaultGrade = defaultGrade;
        this.defaultPrice = defaultPrice;
    }

    public static Seat from(Venue venue, SeatCreateRequest request) {
        return new Seat(venue, request.section(), request.row(), request.column(), request.defaultGrade(), request.defaultPrice());
    }
}
