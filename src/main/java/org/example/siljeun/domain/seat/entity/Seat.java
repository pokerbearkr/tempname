package org.example.siljeun.domain.seat.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.siljeun.domain.venue.entity.Venue;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seat")
public class Seat {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  //공연장 FK
  @ManyToOne
  @JoinColumn(name = "venue_id")
  private Venue venue;

  private String section;

  @Column(name = "`row`")
  private String row;
  private String number;

}
