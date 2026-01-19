package org.example.Time;

// 시간표 한 칸을 나타냄
public record TimeCell(
        String dayKr,
        int period,
        int half
) {}