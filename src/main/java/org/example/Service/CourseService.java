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

    public List<CourseResponse> getAll(Long termId) {
        if (termId == null) {
            return courseRepository.findAll()
                    .stream().map(CourseResponse::from)
                    .toList();
        }
        return courseRepository.findByTermId(termId)
                .stream().map(CourseResponse::from)
                .toList();
    }
}

