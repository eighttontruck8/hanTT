package org.example.Repository;

import org.example.entity.TimetableCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TimetableCourseRepository extends JpaRepository<TimetableCourse, Long> {
    List<TimetableCourse> findByTimetableId(Long timetableId);
    boolean existsByTimetableIdAndCourseId(Long timetableId, Long courseId);
    void deleteByTimetableIdAndCourseId(Long timetableId, Long courseId);
    long countByTimetableId(Long timetableId);
}
