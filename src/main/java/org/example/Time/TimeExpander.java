package org.example.Time;

import org.example.entity.CourseTimeSlot;

import java.util.*;

public class TimeExpander {
    private static int toIndex(int period, int half) {
        return period * 2 + half; // period 1이면 2~3
    }

    private static int periodFromIndex(int idx) { return idx / 2; }
    private static int halfFromIndex(int idx) { return idx % 2; }

    public static List<TimeCell> expandFromDbSlot(CourseTimeSlot s, int maxPeriod) {
        DayKR day = DayKR.fromOrder(s.getDay()); // 1~7 -> DayKR

        TimeSlot slot = new TimeSlot(
                day,
                s.getStartPeriod(),
                s.getStartHalf(),
                s.getEndPeriod(),
                s.getEndHalf(),
                s.isOnline()
        );
        return expand(slot, maxPeriod);
    }
    public static List<TimeCell> expand(TimeSlot slot, int maxPeriod) {
        if (slot.online()) return List.of();

        int startIdx = toIndex(slot.sp(), slot.sh());
        int endIdx   = toIndex(slot.ep(), slot.eh());

        if (startIdx > endIdx) {
            throw new IllegalArgumentException("시간 구간이 반대로 되어있습니다: " + slot);
        }

        // 교시 제한 체크
        int minIdx = toIndex(1, 0); // 1A
        int maxIdx = toIndex(maxPeriod, 1); // maxPeriod B
        if (startIdx < minIdx || endIdx > maxIdx) {
            throw new IllegalArgumentException("교시 범위를 초과했습니다.(slot=" + slot + ", maxPeriod=" + maxPeriod + ")");
        }

        List<TimeCell> cells = new ArrayList<>();
        for (int idx = startIdx; idx <= endIdx; idx++) {
            int p = periodFromIndex(idx);
            int h = halfFromIndex(idx);
            cells.add(new TimeCell(slot.day(), p, h));
        }
        return cells;
    }
}
