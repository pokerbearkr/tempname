package org.example.siljeun.domain.venue.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.siljeun.global.entity.BaseEntity;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "venue")
public class Venue extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String name;

  @Column(nullable = false)
  private String location;

  @Column(nullable = false)
  private Integer seatCapacity;

  public Venue(String name, String location, int seatCapacity) {
    this.name = name;
    this.location = location;
    this.seatCapacity = seatCapacity;
  }

  public void update(String name, String location, int seatCapacity) {
    this.name = name;
    this.location = location;
    this.seatCapacity = seatCapacity;
  }
}