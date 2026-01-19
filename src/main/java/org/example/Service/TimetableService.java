package org.example.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.Repository.*;
import org.example.Time.TimeCell;
import org.example.Time.TimeExpander;
import org.example.entity.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TimetableService {
    private final TimetableRepository timetableRepository;
    private final TimetableCourseRepository timetableCourseRepository;
    private final CourseRepository courseRepository;
    private final CourseTimeSlotRepository courseTimeSlotRepository; // course_time_slots 읽기용

    // 새 시간표 생성 또는 기존 시간표 반환
    @Transactional
    public Timetable getOrCreateTimetable(Long userId, Long termId, String title) {
        return timetableRepository.findByUserIdAndTermId(userId, termId)
                .orElseGet(() -> {
                    Timetable t = Timetable.create(userId, termId, title);
                    return timetableRepository.save(t);
                });
    }
    @Transactional
    public Timetable getTimetable(Long timetableId) {
        return timetableRepository.findById(timetableId)
                .orElseThrow(() ->
                        new NoSuchElementException("시간표를 찾을 수 없습니다. id=" + timetableId)
                );
    }
    // 시간표 랜더링
    public Map<String, List<String>> buildCellMap(Long timetableId, int maxPeriod) {
        List<TimetableCourse> tcs = timetableCourseRepository.findByTimetableId(timetableId);
        if (tcs.isEmpty()) return Map.of();

        List<Long> courseIds = tcs.stream().map(TimetableCourse::getCourseId).distinct().toList();
        Map<Long, Course> courseMap = new HashMap<>();
        for (Course c : courseRepository.findAllById(courseIds)) {
            courseMap.put(c.getId(), c);
        }

        // course_time_slots는 courseId로 조회 가능하도록 설정
        Map<String, List<String>> cellMap = new HashMap<>();

        for (Long courseId : courseIds) {
            Course course = courseMap.get(courseId);
            if (course == null) continue;

            List<CourseTimeSlot> slots = courseTimeSlotRepository.findByCourseId(courseId);

            for (CourseTimeSlot s : slots) {
                List<TimeCell> cells = TimeExpander.expandFromDbSlot(s, maxPeriod);

                for (TimeCell cell : cells) {
                    // key: "1-3A" 처럼 만들기(요일숫자-교시-half)
                    // half: 0(A),1(B)
                    String key = cell.day() + "-" + cell.period() + (cell.half() == 0 ? "A" : "B");

                    String label = course.getCourseName()
                            + " (" + course.getCourseCode() + "-" + course.getSection() + ") "
                            + (course.getProfessor() == null ? "" : course.getProfessor());

                    cellMap.computeIfAbsent(key, k -> new ArrayList<>()).add(label);
                }
            }
        }
        return cellMap;
    }
}
