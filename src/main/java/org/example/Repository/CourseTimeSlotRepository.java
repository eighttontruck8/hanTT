package org.example.Repository;
import org.example.entity.CourseTimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseTimeSlotRepository extends JpaRepository<CourseTimeSlot, Long> {
    List<CourseTimeSlot> findByCourseId(Long courseId);
    List<CourseTimeSlot> findByCourseIdIn(List<Long> courseIds);

    void deleteByCourseIdIn(List<Long> courseIds);

    boolean existsByCourseId(Long courseId);
}
