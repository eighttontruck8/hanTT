package org.example.view;

import java.util.ArrayList;
import java.util.List;

public final class TimetableViewUtil {

    private TimetableViewUtil() {}

    public static List<Integer> buildPeriods(int maxPeriod) {
        List<Integer> list = new ArrayList<>();
        for (int i = 1; i <= maxPeriod; i++) {
            list.add(i);
        }
        return list;
    }
}
