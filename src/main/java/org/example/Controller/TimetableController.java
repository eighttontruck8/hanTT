package org.example.Controller;

import lombok.RequiredArgsConstructor;
import org.example.Repository.CourseRepository;
import org.example.Repository.CourseTimeSlotRepository;
import org.example.entity.Timetable;
import org.example.view.TimetableViewUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.example.Service.TimetableService;
import org.example.Service.TimetableCourseService;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService timetableService;
    private final TimetableCourseService timetableCourseService;
    private final CourseRepository courseRepository;
    private final CourseTimeSlotRepository slotRepository;

//    // 시간표 생성 폼
//    @GetMapping("/timetable/new")
//    public String newTimetableForm(@RequestParam Long termId, Model model) {
//        model.addAttribute("termId", termId);
//        return "timetable_new";
//    } 이건 일단 보류 ...

    @GetMapping("/timetable/neww")
    public String newTimetablePage(
            @RequestParam(required = false) Long termId,
            @RequestParam(required = false) String title,
            Model model
    ) {
        Long userId = 1L;
        Long resolvedTermId = (termId == null) ? 1L : termId; // TODO: 나중에 현재학기 로직

        Timetable tt = timetableService.getOrCreateTimetable(userId, resolvedTermId, title);

        int maxPeriod = 15;
        Map<String, List<String>> cellMap = timetableService.buildCellMap(tt.getId(), maxPeriod);

        model.addAttribute("timetable", tt);
        model.addAttribute("termId", tt.getTermId());
        model.addAttribute("cellMap", cellMap);
        model.addAttribute("days", List.of("월","화","수","목","금"));
        model.addAttribute("periods", TimetableViewUtil.buildPeriods(maxPeriod));

        return "timetable";
    }
    // 시간표 생성
    @PostMapping("/timetable")
    public String create(
            @RequestParam Long termId,
            @RequestParam(required = false) String title) {
        Long userId = 1L;
        Timetable t = timetableService.getOrCreateTimetable(userId, termId, title);
        return "redirect:/timetable?timetableId=" + t.getId();
    }

    // 시간표 id로 조회
    @GetMapping("/timetable/{timetableId}")
    public String timetableById(@PathVariable Long timetableId, Model model) {

        // 1) 시간표 정보
        Timetable tt = timetableService.getTimetable(timetableId);
//        // 2) 이 시간표에 담긴 courseId 목록
//        List<Long> courseIds = timetableCourseService.getCourseIds(timetableId);
//
//        // 3) course / slots 조회
//        List<Course> courses = courseRepository.findByIdIn(courseIds);
//        List<CourseTimeSlot> slots = slotRepository.findByCourseIdIn(courseIds);
        int maxPeriod = 15;

//        // courseId -> Course 빠르게 찾기
//        Map<Long, Course> courseMap = new HashMap<>();
//        for (Course c : courses) courseMap.put(c.getId(), c);
//
//        for (CourseTimeSlot s : slots) {
//            List<TimeCell> cells = TimeExpander.expandFromDbSlot(s, maxPeriod);
//
//            Course course = courseMap.get(s.getCourseId());
//            String label = (course == null)
//                    ? ("courseId=" + s.getCourseId())
//                    : (course.getCourseCode() + " " + course.getCourseName() + " (" + course.getSection() + ")");
//
//            for (TimeCell cell : cells) {
//                String key = cell.day() + "-" + cell.period() + (cell.half() == 0 ? "A" : "B");
//                cellMap.computeIfAbsent(key, k -> new ArrayList<>()).add(label);
//            }
//        }
        Map<String, List<String>> cellMap = timetableService.buildCellMap(timetableId, maxPeriod);

        model.addAttribute("timetable", tt);
        model.addAttribute("termId", tt.getTermId());
        model.addAttribute("cellMap", cellMap);
        model.addAttribute("days", List.of("월","화","수","목","금"));
        model.addAttribute("periods", TimetableViewUtil.buildPeriods(maxPeriod));
        return "timetable";
    }
}
