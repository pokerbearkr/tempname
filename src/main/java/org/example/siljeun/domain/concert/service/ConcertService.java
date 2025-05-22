package org.example.siljeun.domain.concert.service;

import java.util.List;
import org.example.siljeun.domain.concert.dto.request.ConcertCreateRequest;
import org.example.siljeun.domain.concert.dto.request.ConcertUpdateRequest;
import org.example.siljeun.domain.concert.dto.response.ConcertDetailResponse;
import org.example.siljeun.domain.concert.dto.response.ConcertSimpleResponse;

public interface ConcertService {

  Long createConcert(ConcertCreateRequest request, Long userId);

  void updateConcert(Long concertId, ConcertUpdateRequest request);

  void deleteConcert(Long concertId);

  List<ConcertSimpleResponse> getConcertList();

  ConcertDetailResponse getConcertDetail(Long concertId);

  List<ConcertSimpleResponse> getPopularConcerts();
}
