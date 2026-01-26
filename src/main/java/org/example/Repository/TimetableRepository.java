package org.example.Repository;

import org.example.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    Optional<Timetable> findByUserIdAndTermId(Long userId, Long termId);
    List<Timetable> findByUserIdOrderByUpdatedAtDesc(Long userId);
}
