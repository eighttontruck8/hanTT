package org.example.DTO;

import lombok.Builder;
import org.example.entity.Course;

@Builder
public record CourseResponse(
        Long courseId,
        Long termId,
        String courseCode,
        String courseName,
        Integer section,
        Integer capacity,
        Integer credits,
        Integer lectureHours,
        Integer labHours,
        Integer designHours,
        String category,
        String openingDept,
        String targetDept,
        Integer gradeLevel,
        String professor,
        String rawTimeText
) {
    public static CourseResponse from(Course c) {
        return CourseResponse.builder()
                .courseId(c.getId())
                .termId(c.getTermId())
                .courseCode(c.getCourseCode())
                .courseName(c.getCourseName())
                .section(c.getSection())
                .capacity(c.getCapacity())
                .credits(c.getCredits())
                .lectureHours(c.getLectureHours())
                .labHours(c.getLabHours())
                .designHours(c.getDesignHours())
                .category(c.getCategory())
                .openingDept(c.getOpening())
                .targetDept(c.getTarget())
                .gradeLevel(c.getGrade())
                .professor(c.getProfessor())
                .rawTimeText(c.getRawTimeText())
                .build();
    }
}