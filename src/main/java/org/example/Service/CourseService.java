package org.example.Service;

import lombok.RequiredArgsConstructor;
import org.example.DTO.CourseResponse;
import org.example.Repository.CourseRepository;
import org.example.entity.Course;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    // 강의 조회
    public List<CourseResponse> getAll(Long termId) {
        // 강의 목록 정렬
        Sort sort = Sort.by(
                Sort.Order.asc("opening"),
                Sort.Order.asc("category"),
                Sort.Order.asc("courseName"),
                Sort.Order.asc("section")
        );

        // 전체 강의 조회
        List<Course> courses = (termId == null)
                ? courseRepository.findAll(sort)
                : courseRepository.findByTermId(termId, sort);

        // 특정 학기 강의만 조회
        return courses.stream()
                .map(CourseResponse::from)
                .toList();
    }
}

