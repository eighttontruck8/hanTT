package org.example.Controller;

import lombok.RequiredArgsConstructor;
import org.example.Service.CourseTimeSlotService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseSlotController {
    private final CourseTimeSlotService slotService;

    @PostMapping("/build-slots")
    public Map<String, Object> build(@RequestParam Long termId) {
        int count = slotService.buildSlotsForTerm(termId);
        return Map.of("saved", count);
    }
}
