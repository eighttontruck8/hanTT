package org.example.Controller;

import lombok.RequiredArgsConstructor;
import org.example.Repository.CourseRepository;
import org.example.Repository.CourseTimeSlotRepository;
import org.example.Time.TimeCell;
import org.example.Time.TimeExpander;
import org.example.entity.Course;
import org.example.entity.CourseTimeSlot;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class TimetableController {

    private final CourseRepository courseRepository;
    private final CourseTimeSlotRepository slotRepository;

    @GetMapping("/timetable")
    public String timetable(@RequestParam Long termId, Model model) {

        List<Course> courses = courseRepository.findByTermId(termId);
        List<Long> courseIds = courses.stream().map(Course::getId).toList();

        List<CourseTimeSlot> slots = slotRepository.findByCourseIdIn(courseIds);

        Map<String, List<String>> cellMap = new HashMap<>();

        int maxPeriod = 15;

        for (CourseTimeSlot s : slots) {

            // 1) TimeCell 리스트 만들기
            List<TimeCell> cells = TimeExpander.expandFromDbSlot(s, maxPeriod);

            Course course = courses.stream().filter(c -> c.getId().equals(s.getCourseId())).findFirst().orElse(null);
            String label = (course == null)
                    ? ("courseId=" + s.getCourseId())
                    : (course.getCourseCode() + " " + course.getCourseName() + " (" + course.getSection() + ")");

            for (TimeCell cell : cells) {
                String key = cell.day() + "-" + cell.period() + (cell.half() == 0 ? "A" : "B");
                cellMap.computeIfAbsent(key, k -> new ArrayList<>()).add(label);
            }
        }

        model.addAttribute("termId", termId);
        model.addAttribute("cellMap", cellMap);

        model.addAttribute("days", List.of("월","화","수","목","금")); // 주말은 제외
        model.addAttribute("periods", buildPeriods(maxPeriod)); // 1~maxPeriod
        return "timetable";
    }

    private List<Integer> buildPeriods(int maxPeriod) {
        List<Integer> list = new ArrayList<>();
        for (int i=1; i<=maxPeriod; i++) list.add(i);
        return list;
    }
}
