package org.example.Controller;

import lombok.RequiredArgsConstructor;
import org.example.Service.TimetablePageService.TimetablePageVM;
import org.example.Service.TimetablePageService;
import org.example.Service.TimetableService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 단일 시간표 화면만!!
@Controller
@RequiredArgsConstructor
public class TimetablePageController {
    private final TimetablePageService timetablePageService;


    @GetMapping("/timetable")
    public String timetable(@RequestParam Long timetableId, Model model) {
        TimetablePageVM vm = timetablePageService.buildPage(timetableId);

        model.addAttribute("timetable", vm.timetable());
        model.addAttribute("termId", vm.termId());
        model.addAttribute("courseList", vm.courseList());
        model.addAttribute("pickedCourseIds", vm.pickedCourseIds());
        model.addAttribute("pickedCourseMap", vm.pickedCourseMap());
        model.addAttribute("cellMap", vm.cellMap());
        model.addAttribute("days", List.of("월","화","수","목","금"));
        model.addAttribute("periods", buildPeriods(vm.maxPeriod()));
        model.addAttribute("periodTimeMap", buildPeriodTimeMap(vm.maxPeriod()));
        return "timetable";
    }

    // 제목 수정
    @PostMapping("/timetable/{id}/title")
    public String changeTitle(@PathVariable Long id, @RequestParam String title) {
        timetablePageService.changeTitle(id, title);
        return "redirect:/timetable?timetableId=" + id;
    }

    // 저장 버튼
    @PostMapping("/timetable/{id}/save")
    public String save(@PathVariable Long id) {
        return "redirect:/timetable?timetableId=" + id;
    }

    // 강의 추가
    @PostMapping("/timetable/{id}/courses/add")
    public String addCourse(@PathVariable Long id, @RequestParam Long courseId) {
        timetablePageService.addCourse(id, courseId);
        return "redirect:/timetable?timetableId=" + id;
    }

    // 강의 삭제
    @PostMapping("/timetable/{id}/courses/remove")
    public String removeCourse(@PathVariable Long id, @RequestParam Long courseId) {
        timetablePageService.removeCourse(id, courseId);
        return "redirect:/timetable?timetableId=" + id;
    }

    // 교시 표시
    private List<Integer> buildPeriods(int maxPeriod) {
        return java.util.stream.IntStream.rangeClosed(1, maxPeriod).boxed().toList();
    }

    // 시간 표시
    private java.util.Map<Integer, String> buildPeriodTimeMap(int maxPeriod) {
        java.util.Map<Integer, String> map = new java.util.LinkedHashMap<>();

        // 1교시 09:00 시작, 교시당 60분
        int startHour = 9;
        int startMin = 0;

        for (int p = 1; p <= maxPeriod; p++) {
            int s = (startHour * 60 + startMin) + (p - 1) * 60;
            int e = s + 60;

            map.put(p, formatTime(s) + "-" + formatTime(e));
        }
        return map;
    }

    private String formatTime(int minutes) {
        int h = minutes / 60;
        int m = minutes % 60;
        return String.format("%02d:%02d", h, m);
    }
}
