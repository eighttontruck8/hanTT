package org.example.Controller;

import lombok.RequiredArgsConstructor;
import org.example.DTO.CourseResponse;
import org.example.Repository.CourseRepository;
import org.example.Repository.TermRepository;
import org.example.Service.CourseImportService;
import org.example.Service.CourseService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseImportService importService;
    private final CourseService courseService;


    @PostMapping(value="/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> importCourses(
            @RequestParam("file") MultipartFile file,
            @RequestParam int year,
            @RequestParam int semester
    ) throws IOException {

        int saved = importService.importExcel(file, year, semester);
        return Map.of("saved", saved);
    }
    @GetMapping
    public List<CourseResponse> getCourses(@RequestParam(required = false) Long termId) {
        return courseService.getAll(termId);
    }
}
