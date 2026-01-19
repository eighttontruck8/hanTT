package org.example.Controller;

import lombok.RequiredArgsConstructor;
import org.example.Service.TimetablePageService;
import org.example.Service.TimetablePageService.TimetablePageVM;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TimetablePageController {
    private final TimetablePageService timetablePageService;

    @GetMapping("/timetable")
    public String timetable(@RequestParam(required = false) Long timetableId, Model model) {
        var t = timetablePageService.getOrCreate(timetableId);

        TimetablePageVM vm = timetablePageService.buildPage(t.getId());

        model.addAttribute("timetable", vm.timetable());
        model.addAttribute("termId", vm.termId());
        model.addAttribute("courseList", vm.courseList());
        model.addAttribute("pickedCourseIds", vm.pickedCourseIds());
        model.addAttribute("pickedCourseMap", vm.pickedCourseMap());
        model.addAttribute("cellMap", vm.cellMap());
        model.addAttribute("days", List.of("월","화","수","목","금"));
        model.addAttribute("periods", buildPeriods(vm.maxPeriod()));

        return "timetable";
    }

    // 제목 수정
    @PostMapping("/timetable/{id}/title")
    public String changeTitle(@PathVariable Long id, @RequestParam String title) {
        timetablePageService.changeTitle(id, title);
        return "redirect:/timetable?timetableId=" + id;
    }

    // 저장 버튼 (지금은 그냥 redirect)
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

    // 강의 제거
    @PostMapping("/timetable/{id}/courses/remove")
    public String removeCourse(@PathVariable Long id, @RequestParam Long courseId) {
        timetablePageService.removeCourse(id, courseId);
        return "redirect:/timetable?timetableId=" + id;
    }

    private List<Integer> buildPeriods(int maxPeriod) {
        return java.util.stream.IntStream.rangeClosed(1, maxPeriod).boxed().toList();
    }
}
