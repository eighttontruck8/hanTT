package org.example.Controller;

import lombok.RequiredArgsConstructor;
import org.example.Repository.CourseRepository;
import org.example.Repository.CourseTimeSlotRepository;
import org.example.entity.Timetable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.example.Service.TimetableService;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService timetableService;
    private final CourseRepository courseRepository;
    private final CourseTimeSlotRepository slotRepository;

    // 시간표 생성
    @PostMapping("/timetable")
    public String create(
            @RequestParam Long termId,
            @RequestParam(required = false) String title) {
        Long userId = 1L;
        Timetable t = timetableService.getOrCreateTimetable(userId, termId, title);
        return "redirect:/timetable?timetableId=" + t.getId();
    }
}
