package org.example.DTO;

import java.util.*;
import java.util.regex.*;

public class TimeParser {
    // 예: "화01A~02B(온라인)" or "월08A~09B"
    private static final Pattern P = Pattern.compile(
            "^(월|화|수|목|금|토|일)" +
                    "(\\d{2})([AB])~(\\d{2})([AB])" +
                    "(\\(온라인\\))?$"
    );

    public static List<TimeSlot> parse(String raw) {
        if (raw == null) return List.of();
        raw = raw.trim();
        if (raw.isEmpty()) return List.of();

        List<TimeSlot> out = new ArrayList<>();
        String[] parts = raw.split("\\s*,\\s*"); // 콤마 양옆 공백 허용

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) continue;

            Matcher m = P.matcher(part);
            if (!m.matches()) {
                // 여기서 그냥 무시하지 말고 로그 찍는 게 좋음
                // throw new IllegalArgumentException("Invalid time token: " + part);
                continue;
            }

            int dow = toDow(m.group(1));
            int sp = Integer.parseInt(m.group(2));
            char sh = m.group(3).charAt(0);
            int ep = Integer.parseInt(m.group(4));
            char eh = m.group(5).charAt(0);
            boolean online = (m.group(6) != null);

            out.add(new TimeSlot(dow, sp, sh, ep, eh, online));
        }
        return out;
    }

    private static int toDow(String k) {
        return switch (k) {
            case "월" -> 1;
            case "화" -> 2;
            case "수" -> 3;
            case "목" -> 4;
            case "금" -> 5;
            case "토" -> 6;
            case "일" -> 7;
            default -> throw new IllegalArgumentException("Bad day: " + k);
        };
    }
}
