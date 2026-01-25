package org.example.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.Repository.*;
import org.example.entity.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class TimetableService {
    private final TimetableRepository timetableRepository;

    // 새 시간표 생성 또는 기존 시간표 반환
    @Transactional
    public Timetable getOrCreateTimetable(Long userId, Long termId, String title) {
        return timetableRepository.findByUserIdAndTermId(userId, termId)
                .orElseGet(() -> {
                    Timetable t = Timetable.create(userId, termId, title);
                    return timetableRepository.save(t);
                });
    }
}
