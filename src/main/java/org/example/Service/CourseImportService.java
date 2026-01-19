package org.example.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.example.DTO.CourseResponse;
import org.example.Repository.CourseRepository;
import org.example.Repository.TermRepository;
import org.example.entity.Term;
import org.example.entity.Course;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseImportService {

    private final CourseRepository courseRepository;
    private final TermRepository termRepository;

    @Transactional
    public int importExcel(MultipartFile file, int year, int semester) throws IOException {
        Term term = termRepository.findByYearAndSemester(year, semester)
                .orElseGet(() -> termRepository.save(new Term(year, semester)));
        System.out.println("before delete count=" + courseRepository.countByTermId(term.getId()));

        courseRepository.deleteByTermId(term.getId());
        courseRepository.flush();

        System.out.println("after delete count=" + courseRepository.countByTermId(term.getId()));


        int saved = 0;

        try (InputStream is = file.getInputStream();
             Workbook wb = WorkbookFactory.create(is)) {

            Sheet sheet = wb.getSheetAt(0);

            // 0행이 헤더라고 가정하고 1행부터
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);

                if (row == null) continue;
                System.out.println("row=" + r + ", code=" + getString(row, 1) + ", grade=" + getInt(row, 12));

                String  courseCode = getString(row, 1);
                String  courseName = getString(row, 2);
                Integer section    = getInt(row, 3);
                Integer capacity   = getInt(row, 4);
                Integer credits    = getInt(row, 5);
                Integer lectureHours    = getInt(row, 6);
                Integer labHours       = getInt(row, 7);
                Integer designHours     = getInt(row, 8);
                String category = getString(row, 9);
                String opening = getString(row, 10);
                String target = getString(row, 11);
                String grade = getString(row, 12);
                String  professor  = getString(row, 13);
                String  rawTimeText    = getString(row, 14);

                if (courseCode == null || courseCode.isBlank()) continue;
                if (courseName == null || courseName.isBlank()) continue;
                if (section == null) section = 0;

                Course c =  Course.create(
                        term.getId(),
                        courseCode,
                        courseName,
                        section,
                        credits,
                        professor,
                        rawTimeText,
                        lectureHours,
                        labHours,
                        designHours,
                        capacity,
                        category,
                        opening,
                        target,
                        grade);

                try {
                    courseRepository.save(c);
                    saved++;
                } catch (DataIntegrityViolationException e) {
                }
            }
        }
        return saved;
    }

    private String getString(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue();
    }

    private Integer getInt(Row row, int cellIndex) {
        Cell cell = row.getCell(cellIndex);
        if (cell == null) return null;
        if (cell.getCellType() == CellType.NUMERIC) return (int) cell.getNumericCellValue();
        String s = cell.toString().trim();
        if (s.isEmpty()) return null;
        try { return Integer.parseInt(s); } catch (Exception e) { return null; }
    }
}
