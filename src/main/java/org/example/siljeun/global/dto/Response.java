package org.example.siljeun.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record Response<T>(

    boolean success,
    String message,
    @JsonInclude(Include.NON_NULL)
    T data
) {

  public static <T> Response<T> from(String message) {
    return new Response<>(true, message, null);
  }
}
// 컨트롤러에서 반환타입 선언할 때 Response<> 내부 제네릭 타입을 null로 하는지 String으로 하는지?
// 메서드에서 제네릭 타입 두 번 써야하는 이유

// 제네릭은 빌드 타임에 타입 안정성 확보 -> 실행됐을때는 타입이 고정됨
// 제네릭은 형태를 고정하는 것.
