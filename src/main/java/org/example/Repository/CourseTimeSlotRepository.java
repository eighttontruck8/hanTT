package org.example.Repository;
import org.example.entity.CourseTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseTimeSlotRepository extends JpaRepository<CourseTimeSlot, Long> {
    void deleteByCourseId(Long courseId);
    void deleteByCourseIdIn(List<Long> courseIds);
}
