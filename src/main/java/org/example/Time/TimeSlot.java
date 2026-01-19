package org.example.Time;

// 강의 한 칸을 나타냄
public record TimeSlot(
        int day, // 요일 : 1~7 => 월~일
        int sp,  // 시작 교시
        int sh, // 시작 half : 0=A, 1=B라는뜻
        int ep, // 종료 교시
        int eh, // 종료 half
        boolean online
) {}

