package org.example.Service;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.DTO.CellItem;
import org.example.Repository.*;
import org.example.Time.TimeCell;
import org.example.Time.TimeExpander;
import org.example.Time.TimeParser;
import org.example.Time.TimeSlot;
import org.example.entity.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

// 시간표 화면 데이터 조립!!
@Service
@RequiredArgsConstructor
public class TimetablePageService {

    private final TermRepository termRepository;
    private final TimetableRepository timetableRepository;
    private final TimetableCourseRepository timetableCourseRepository;

    private final CourseRepository courseRepository;
    private final CourseTimeSlotRepository courseTimeSlotRepository;

    public record TimetablePageVM(
            Timetable timetable,
            Long termId,
            List<Course> courseList,
            Set<Long> pickedCourseIds,
            Map<Long, Course> pickedCourseMap,
            Map<String, List<CellItem>> cellMap,
            int maxPeriod
    ) {
        public Object getTimetable() {
            return timetable;
        }
    }

    private static final List<String> PALETTE = List.of(
            "#FFD6E7", "#D6F5FF", "#D6FFE6", "#FFF3D6", "#E7D6FF",
            "#FFE6D6", "#D6FFD9", "#D6E0FF", "#FFF0B3", "#E0E0E0"
    );

    @Transactional
    public TimetablePageVM buildPage(Long timetableId) {
        Timetable tt = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new NoSuchElementException("시간표 없음 id=" + timetableId));

        Long termId = tt.getTermId();
        int maxPeriod = 9;


        // 1) 왼쪽 강의 목록: 해당 termId의 모든 course
        List<Course> courseList = courseRepository.findByTermId(termId);

        // 2) picked: timetable_courses에서 courseId만 뽑기
        List<TimetableCourse> links = timetableCourseRepository.findByTimetableId(tt.getId());
        Set<Long> pickedCourseIds = links.stream()
                .map(TimetableCourse::getCourseId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<Long, String> colorByCourseId = links.stream()
                .collect(Collectors.toMap(
                        TimetableCourse::getCourseId,
                        tc -> tc.getColorCode() == null ? "#E0E0E0" : tc.getColorCode(),
                        (a, b) -> a,
                        LinkedHashMap::new
                ));
        System.out.println("pickedCourseIds=" + pickedCourseIds);

        // 3) pickedCourseMap: courseId -> Course
        Map<Long, Course> pickedCourseMap = new HashMap<>();
        if (!pickedCourseIds.isEmpty()) {
            for (Course c : courseRepository.findAllById(pickedCourseIds)) {
                pickedCourseMap.put(c.getId(), c);
            }
        }
        System.out.println("pickedCourseMap keys=" + pickedCourseMap.keySet());


        // 4) 오른쪽 시간표 칸 만들기
        Map<String, List<CellItem>> cellMap
                = buildCellMap(pickedCourseIds, pickedCourseMap, colorByCourseId, maxPeriod);

        return new TimetablePageVM(
                tt,
                termId,
                courseList,
                pickedCourseIds,
                pickedCourseMap,
                cellMap,
                maxPeriod
        );

    }

    @Transactional(readOnly = false)
    public void changeTitle(Long timetableId, String title) {
        Timetable tt = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new NoSuchElementException("시간표 없음 id=" + timetableId));

        String newTitle = (title == null || title.isBlank()) ? "새 시간표" : title.trim();
        tt.changeTitle(newTitle);
    }

    @Transactional
    public void addCourse(Long timetableId, Long courseId) {

        // 1) 중복된 강의일 경우
        if (timetableCourseRepository.existsByTimetableIdAndCourseId(timetableId, courseId)) {
            throw new IllegalStateException("이미 담긴 과목입니다.");
        }

        // 2) 다음 색 결정 (담긴 과목 개수 기준)
        long pickedCount = timetableCourseRepository.countByTimetableId(timetableId);
        String color = PALETTE.get((int)(pickedCount % PALETTE.size()));

        // 3) 저장
        timetableCourseRepository.save(
                TimetableCourse.create(timetableId, courseId, color)
        );

        // 4) 시간 슬롯 생성
        if (!courseTimeSlotRepository.existsByCourseId(courseId)) {

            Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("course 없음 id=" + courseId));

            // rawTimeText → TimeSlot 파싱
            List<TimeSlot> slots = TimeParser.parse(course.getRawTimeText());

            for (TimeSlot ts : slots) {
                courseTimeSlotRepository.save(
                        CourseTimeSlot.from(courseId, ts)
                );
            }
        }
    }

    @Transactional
    public void removeCourse(Long timetableId, Long courseId) {
        timetableCourseRepository.deleteByTimetableIdAndCourseId(timetableId, courseId);
    }

    // -----------------------------------------------------------------

    private Long resolveCurrentTermId() {
        // terms 테이블에서 id가 가장 큰 걸 현재로 간주.
        return termRepository.findTopByOrderByIdDesc()
                .map(Term::getId)
                .orElse(1L);
    }

    private Map<String, List<CellItem>> buildCellMap(
            Set<Long> pickedCourseIds,
            Map<Long, Course> pickedCourseMap,
            Map<Long, String> colorByCourseId,
            int maxPeriod
    ) {
        if (pickedCourseIds == null || pickedCourseIds.isEmpty()) return new HashMap<>();

        List<CourseTimeSlot> slots = courseTimeSlotRepository.findByCourseIdIn(new ArrayList<>(pickedCourseIds));

        Map<String, List<CellItem>> cellMap = new HashMap<>();

        for (CourseTimeSlot s : slots) {
            Course c = pickedCourseMap.get(s.getCourseId());
            if (c == null) continue;

            List<TimeCell> cells = TimeExpander.expandFromDbSlot(s, maxPeriod);

            String label = c.getCourseName()
                    + " (" + c.getSection() + "분반) "
                    + (c.getProfessor() == null ? "" : c.getProfessor());

            // 색상 가져오기
            String color = colorByCourseId.getOrDefault(c.getId(), "#E0E0E0");

            // 교시 순서 보장
            cells.sort(Comparator.comparingInt(TimeCell::period));

            for (TimeCell cell : cells) {
                // key는 "월-3A" 같은 형식으로 통일
                String key = cell.dayKr() + "-" + cell.period() + (cell.half() == 0 ? "A" : "B");
                boolean cont = false;

                // ✅ (1) 같은 교시 A가 있으면 B는 연속으로 처리
                if (cell.half() == 1) { // B
                    String samePeriodAKey = cell.dayKr() + "-" + cell.period() + "A";
                    List<CellItem> aItems = cellMap.get(samePeriodAKey);
                    if (aItems != null) {
                        cont = aItems.stream().anyMatch(it -> it.courseId().equals(c.getId()));
                    }
                }
                // ✅ (2) 이전 교시(같은 half)가 있으면 연속 처리
                if (!cont && cell.period() > 1) {
                    // 바로 윗 교시 key
                    String prevKey = cell.dayKr() + "-" + (cell.period() - 1) + (cell.half()==0 ? "A":"B");
                    // prevKey에 같은 text가 이미 있으면 continuation 처리
                    List<CellItem> prevItems = cellMap.get(prevKey);

                    if (prevItems != null) {
                        cont = prevItems.stream().anyMatch(it -> it.courseId().equals(c.getId()));
                    }
                }

                CellItem item = new CellItem(c.getId(), label, color, cont);
                cellMap.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
            }
        }
        return cellMap;
    }
}
