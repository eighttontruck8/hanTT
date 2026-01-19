package org.example.Repository;

import jakarta.transaction.Transactional;
import org.example.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByTermId(Long termId);
    List<Course> findByIdIn(List<Long> ids);
    long countByTermId(Long termId);

    @Modifying
    @Transactional
    @Query("delete from Course c where c.termId = :termId")
    void deleteByTermId(@Param("termId") Long termId);
}