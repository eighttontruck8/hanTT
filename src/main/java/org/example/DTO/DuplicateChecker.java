package org.example.DTO;

import org.example.Time.TimeCell;

import java.util.*;

public class DuplicateChecker {

    public record Clash(TimeCell cell, Long existingCourseId, Long newCourseId) {}

    public static List<Clash> findClashes(
            Map<TimeCell, Long> occupied,
            Long newCourseId,
            List<TimeCell> newCells
    ) {
        List<Clash> clashes = new ArrayList<>();
        for (TimeCell cell : newCells) {
            Long existing = occupied.get(cell);
            if (existing != null && !existing.equals(newCourseId)) {
                clashes.add(new Clash(cell, existing, newCourseId));
            }
        }
        return clashes;
    }

    public static void occupy(Map<TimeCell, Long> occupied, Long courseId, List<TimeCell> cells) {
        for (TimeCell cell : cells) {
            occupied.put(cell, courseId);
        }
    }
}
