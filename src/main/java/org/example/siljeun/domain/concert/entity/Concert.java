package org.example.siljeun.domain.concert.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.siljeun.domain.venue.entity.Venue;
import org.example.siljeun.global.entity.BaseEntity;

@Entity
@Getter
@Table(name = "concert")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String title;

  @Lob
  private String description;

  @Enumerated(EnumType.STRING)
  private ConcertCategory category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "venue_id", nullable = false)
  private Venue venue;

  private int cancelCharge;

  @Column(nullable = false)
  private Long viewCount = 0L;


  @Builder
  public Concert(String title, String description, ConcertCategory category, Venue venue,
      int cancelCharge) {
    this.title = title;
    this.description = description;
    this.category = category;
    this.venue = venue;
    this.cancelCharge = cancelCharge;
  }

  public void update(String title, String description, ConcertCategory category, Venue venue,
      int cancelCharge) {
    this.title = title;
    this.description = description;
    this.category = category;
    this.venue = venue;
    this.cancelCharge = cancelCharge;
  }

  public void addViewCount(Long count) {
    this.viewCount += count;
  }
}