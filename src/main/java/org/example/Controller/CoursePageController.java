package org.example.Controller;

import lombok.RequiredArgsConstructor;
import org.example.Repository.CourseRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class CoursePageController {
    private final CourseRepository courseRepository;

    @GetMapping("/courses")
    public String coursesPage(Model model) {
        model.addAttribute("courses", courseRepository.findAll());
        return "courses"; // templates/courses.html
    }
}
