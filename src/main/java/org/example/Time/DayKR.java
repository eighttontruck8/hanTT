package org.example.Time;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum DayKR {
    월(1), 화(2), 수(3), 목(4), 금(5), 토(6), 일(7);

    private final int order;

    DayKR(int order){ this.order = order; }

    public static DayKR from(String s){ return DayKR.valueOf(s); }
    public static DayKR fromOrder(int o){
        for (DayKR d: values()) if (d.order == o) return d;
        throw new IllegalArgumentException("bad day order: " + o);
    }
}
