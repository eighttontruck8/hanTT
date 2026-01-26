package org.example.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.DTO.TimetableSummary;
import org.example.Repository.*;
import org.example.entity.*;
import org.springframework.stereotype.Service;

import java.util.*;

// 시간표 생성, 조회, 목록만!!!!!
@Service
@RequiredArgsConstructor
public class TimetableService {
    private final TimetableRepository timetableRepository;

    // 새 시간표 생성
    @Transactional
    public Timetable create(Long userId, Long termId, String title) {
        String finalTitle = (title == null || title.isBlank()) ? "새 시간표" : title.trim();
        return timetableRepository.save(
                Timetable.create(userId, termId, finalTitle)
        );
    }

    // 단건 조회
    @Transactional
    public Timetable get(Long timetableId) {
        return timetableRepository.findById(timetableId)
                .orElseThrow(() -> new NoSuchElementException("시간표 없음 id=" + timetableId));
    }

    // 시간표 목록 조회
    public List<TimetableSummary> getMyTimetables(Long userId) {
        return timetableRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(t -> new TimetableSummary(t.getId(), t.getTitle()))
                .toList();
    }
}
