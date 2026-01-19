package org.example.Controller;

import lombok.RequiredArgsConstructor;
import org.example.Repository.CourseRepository;
import org.example.Service.CourseService;
import org.example.Service.CourseTimeSlotService;
import org.example.entity.Course;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CoursePageController {
    private final CourseRepository courseRepository;
    private final CourseTimeSlotService courseTimeSlotService;
    private final CourseService courseService;

    @GetMapping("/courses")
    public String courses(@RequestParam(required = false) Long termId, Model model) {

        var courses = courseService.getAll(termId); // 정렬 포함

        model.addAttribute("termId", termId);
        model.addAttribute("courses", courses);
        return "courses";
    }

    @PostMapping("/courses/slots/build")
    public String buildSlots(@RequestParam Long termId) {
        courseTimeSlotService.buildSlotsForTerm(termId);

        return "redirect:/timetable?termId=" + termId;
    }
}
