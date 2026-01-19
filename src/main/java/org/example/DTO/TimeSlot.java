package org.example.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TimeSlot {
    private int dayOfWeek;      // 1~7 = 월~일
    private int startPeriod;    // 1~14
    private char startHalf;     // A 또는 B
    private int endPeriod;
    private char endHalf;
    private boolean online;
}
