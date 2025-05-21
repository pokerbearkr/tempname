package org.example.siljeun.domain.concert.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.siljeun.domain.concert.dto.request.ConcertCreateRequest;
import org.example.siljeun.domain.concert.dto.request.ConcertUpdateRequest;
import org.example.siljeun.domain.concert.dto.response.ConcertDetailResponse;
import org.example.siljeun.domain.concert.dto.response.ConcertSimpleResponse;
import org.example.siljeun.domain.concert.service.ConcertService;
import org.example.siljeun.global.security.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concerts")
public class ConcertController {

  private final ConcertService concertService;

  @PostMapping
  public ResponseEntity<Long> createConcert(
      @RequestBody @Valid ConcertCreateRequest request,
      @AuthenticationPrincipal CustomUserDetails userDetails
  ) {
    Long concertId = concertService.createConcert(request, userDetails.getUserId());
    return ResponseEntity.created(URI.create("/concerts" + concertId)).body(concertId);
  }

  @PutMapping("/{concertId}")
  public ResponseEntity<Void> updateConcert(
      @PathVariable Long concertId,
      @RequestBody @Valid ConcertUpdateRequest request
  ) {
    concertService.updateConcert(concertId, request);
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{concertId}")
  public ResponseEntity<Void> deleteConcert(@PathVariable Long concertId) {
    concertService.deleteConcert(concertId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<List<ConcertSimpleResponse>> getConcertList() {
    List<ConcertSimpleResponse> concerts = concertService.getConcertList();
    return ResponseEntity.ok(concerts);
  }

  @GetMapping("/{concertId}")
  public ResponseEntity<ConcertDetailResponse> getConcertDetail(@PathVariable Long concertId) {
    ConcertDetailResponse response = concertService.getConcertDetail(concertId);
    return ResponseEntity.ok(response);
  }
}
