package org.example.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.Repository.CourseRepository;
import org.example.Repository.CourseTimeSlotRepository;
import org.example.Repository.TimetableCourseRepository;
import org.example.Repository.TimetableRepository;
import org.example.entity.Timetable;
import org.example.entity.TimetableCourse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimetableCourseService {
    private final TimetableRepository timetableRepository;
    private final TimetableCourseRepository timetableCourseRepository;

    private final CourseRepository courseRepository;
    private final CourseTimeSlotRepository courseTimeSlotRepository; // course_time_slots 읽기용


    // 시간표에 강의 추가
    @Transactional
    public void addCourse(Long timetableId, Long courseId) {
        timetableCourseRepository.findByTimetableIdAndCourseId(timetableId, courseId)
                .ifPresent(tc -> { throw new IllegalStateException("이미 담긴 강의입니다."); });

        if (timetableCourseRepository.existsByTimetableIdAndCourseId(timetableId, courseId))
            return; // 이미 들어있으면 무시

        TimetableCourse tc = new TimetableCourse(timetableId, courseId);
        timetableCourseRepository.save(tc);
    }

    // 시간표에 강의 삭제
    @Transactional
    public void removeCourse(Long timetableId, Long courseId) {
        timetableCourseRepository.deleteByTimetableIdAndCourseId(timetableId, courseId);
    }

    @Transactional
    public List<Long> getCourseIds(Long timetableId) {
        return timetableCourseRepository.findCourseIdsByTimetableId(timetableId);
    }

    public List<TimetableCourse> getTimetableCourses(Long timetableId) {
        return timetableCourseRepository.findByTimetableId(timetableId);
    }
}
