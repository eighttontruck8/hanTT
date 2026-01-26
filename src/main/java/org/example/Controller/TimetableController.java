package org.example.Controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.DTO.TimetableSummary;
import org.example.Repository.CourseRepository;
import org.example.Repository.CourseTimeSlotRepository;
import org.example.entity.Timetable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.example.Service.TimetableService;

import java.util.*;

// 시간표 목록/생성
@Controller
@RequiredArgsConstructor
public class TimetableController {
    private final TimetableService timetableService;
    private final CourseRepository courseRepository;
    private final CourseTimeSlotRepository slotRepository;

    // 시간표 조회
    @GetMapping("/timetables")
    public String list(HttpSession session, Model model) {
        Long userId = (Long) session.getAttribute("LOGIN_MEMBER_ID"); // 네 프로젝트 키로 맞춰
        if (userId == null) return "redirect:/login"; // 혹은 예외 처리

        List<TimetableSummary> timetables = timetableService.getMyTimetables(userId);

        // A안: 없으면 생성 화면으로
        if (timetables.isEmpty()) {
            return "redirect:/timetables/new?reason=empty";
        }

        model.addAttribute("timetables", timetables);
        return "timetables/list";
    }

    @GetMapping("/timetables/new")
    public String newForm(@RequestParam(required = false) String reason, Model model) {
        if ("empty".equals(reason)) {
            model.addAttribute("message", "아직 만든 시간표가 없어요. 먼저 생성해 주세요!");
        }
        return "timetables/new";
    }

    // 시간표 생성
    @PostMapping("/timetables")
    public String create(@RequestParam String title, HttpSession session) {
        Long userId = (Long) session.getAttribute("LOGIN_MEMBER_ID");
        if (userId == null) return "redirect:/login";

        Long termId = resolveCurrentTermId();
        Timetable t = timetableService.create(userId, termId, title);

        return "redirect:/timetable?timetableId=" + t.getId();
    }

    // 현재 학기
    private Long resolveCurrentTermId() {
        return 2L;
    }
}
