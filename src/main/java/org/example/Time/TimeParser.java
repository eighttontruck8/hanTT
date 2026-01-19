package org.example.Time;
import java.util.*;
import java.util.regex.*;

public class TimeParser {

    // 요일 + 시작(교시+half) + ~ + 끝(교시+half)
    // EX. 화08A~09B
    private static final Pattern RANGE =
            Pattern.compile("([월화수목금토일])\\s*(\\d{1,2})([AB])\\s*~\\s*(\\d{1,2})([AB])");

    public static List<TimeSlot> parse(String raw) {
        List<TimeSlot> slots = new ArrayList<>();

        if (raw == null || raw.isBlank()) return slots;
        boolean online = raw.contains("온라인") || raw.contains("비대면") || raw.contains("원격");

        Matcher m = RANGE.matcher(raw);

        while (m.find()) {
            // 1. 요일
            int day = DayKR.from(m.group(1)).getOrder();

            // 2. 시작 교시
            int sp = Integer.parseInt(m.group(2));
            char sh = m.group(3).charAt(0);

            // 3. 끝 교시
            int ep = Integer.parseInt(m.group(4));
            char eh = m.group(5).charAt(0);

            // 4. TimeSlot 생성
            slots.add(new TimeSlot(day, sp, sh, ep, eh, online));
        }
        return slots;
    }
}
