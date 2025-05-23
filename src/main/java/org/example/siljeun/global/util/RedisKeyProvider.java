package org.example.siljeun.global.util;

public class RedisKeyProvider {
    public static String seatStatusKey(Long scheduleId){
        return "seatStatus:" + scheduleId;
    }

    public static String userSelectedSeatKey(Long userId, Long scheduleId){
        return "user:"+userId+":scheduleSelected:"+scheduleId;
    }
}
