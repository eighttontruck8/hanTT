package org.example.Service;

import lombok.RequiredArgsConstructor;
import org.example.DTO.CourseResponse;
import org.example.Repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    // 강의 조회
    public List<CourseResponse> getAll(Long termId) {
        // 전체 강의 조회
        if (termId == null) {
            return courseRepository.findAll()
                    .stream().map(CourseResponse::from)
                    .toList();
        }
        // 특정 학기 강의만 조회
        return courseRepository.findByTermId(termId)
                .stream().map(CourseResponse::from)
                .toList();
    }
}

