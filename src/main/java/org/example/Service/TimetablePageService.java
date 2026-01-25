package org.example.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
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

import static java.awt.SystemColor.text;

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
    ) {}

    private static final List<String> PALETTE = List.of(
            "#FFD6E7", "#D6F5FF", "#D6FFE6", "#FFF3D6", "#E7D6FF",
            "#FFE6D6", "#D6FFD9", "#D6E0FF", "#FFF0B3", "#E0E0E0"
    );

    // 1) /timetable 들어오면 timetableId 없을 때 자동 생성
    @Transactional
    public Timetable getOrCreate(Long timetableId) {
        if (timetableId != null) {
            return timetableRepository.findById(timetableId)
                    .orElseThrow(() -> new IllegalArgumentException("시간표 없음: " + timetableId));
        }

        Long userId = 1L; // 임시
        Long currentTermId = resolveCurrentTermId();

        return timetableRepository.findByUserIdAndTermId(userId, currentTermId)
                .orElseGet(() -> timetableRepository.save(
                        Timetable.create(userId, currentTermId, "새 시간표")
                ));
    }

    @Transactional
    public TimetablePageVM buildPage(Long timetableId) {
        Timetable tt = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new NoSuchElementException("시간표 없음 id=" + timetableId));

        Long termId = tt.getTermId();
        int maxPeriod = 15;

        // 1) 왼쪽 강의 목록: 해당 termId의 모든 course
        List<Course> courseList = courseRepository.findByTermId(termId);

        // 2) picked: timetable_courses에서 courseId만 뽑기
        List<TimetableCourse> links = timetableCourseRepository.findByTimetableId(tt.getId());
        Set<Long> pickedCourseIds = links.stream()
                .map(TimetableCourse::getCourseId)
                .collect(Collectors.toCollection(LinkedHashSet::new));

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
                = buildCellMap(timetableId, pickedCourseIds, pickedCourseMap, maxPeriod);

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

    @Transactional
    public void changeTitle(Long timetableId, String title) {
        Timetable tt = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new NoSuchElementException("시간표 없음 id=" + timetableId));

        String newTitle = (title == null || title.isBlank()) ? "새 시간표" : title.trim();
        tt.changeTitle(newTitle); // ✅ 엔티티에서 setter 대신 메서드로
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
        // 1) terms 테이블에서 id가 가장 큰 걸 현재로 간주.
        return termRepository.findTopByOrderByIdDesc()
                .map(Term::getId)
                .orElse(1L);
    }

    private Map<String, List<CellItem>> buildCellMap(
            Long timetableId,
            Set<Long> pickedCourseIds,
            Map<Long, Course> pickedCourseMap,
            int maxPeriod) {
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
            String color = timetableCourseRepository
                    .findByTimetableIdAndCourseId(timetableId, c.getId())
                    .map(TimetableCourse::getColorCode)
                    .orElse("#E0E0E0");

            CellItem item = new CellItem(label, color);
            for (TimeCell cell : cells) {
                // !! key는 "월-3A" 같은 형식으로 통일
                String key = cell.dayKr() + "-" + cell.period() + (cell.half() == 0 ? "A" : "B");
                cellMap.computeIfAbsent(key, k -> new ArrayList<>()).add(item);
            }
        }
        return cellMap;
    }
}
