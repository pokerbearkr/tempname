package org.example.siljeun.global.util;

public class RedisKeyProvider {

    //회차에 따른 회차별 좌석 정보 Id와 상태
    public static String seatStatusKey(Long scheduleId){
        return "seatStatus:" + scheduleId;
    }

    //유저가 선점한 특정 회차의 좌석 상태 정보 Id
    public static String userSelectedSeatKey(Long userId, Long scheduleId){
        return "user:"+userId+":schedule:"+scheduleId;
    }

    //회차별 좌석 상태 정보 점유중
    public static String seatOccupyKey(Long seatScheduleInfoId){
        return "seat:occupy:"+seatScheduleInfoId;
    }

    public static String trackExpiresKey(String status){
        return "expires:"+status;
    }
}
