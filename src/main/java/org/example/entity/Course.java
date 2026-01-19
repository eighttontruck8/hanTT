package org.example.entity;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "courses"
        ,
        uniqueConstraints = @UniqueConstraint(name="uq_course_term_code_section",
                columnNames = {"term_id","course_code","section"})
)
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="course_id")
    private Long id;

    @Column(name="term_id", nullable=false)
    private Long termId;

    @Column(name="course_code", length=30, nullable=false)
    private String courseCode;

    @Column(name="course_name", length=50, nullable=false)
    private String courseName;

    @Column(name="section", nullable=false)
    private Integer section;

    @Column(name="capacity")
    private Integer capacity;

    @Column(name="lecture_hours")
    private Integer lectureHours;

    @Column(name="lab_hours")
    private Integer labHours;

    @Column(name="design_hours")
    private Integer designHours;

    @Column(name="credits")
    private Integer credits;

    @Column(name="category")
    private String category;

    @Column(name="target_dept")
    private String target;

    @Column(name="opening_dept")
    private String opening;

    @Column(name="grade_level")
    private String grade;

    @Column(name="professor", length=50)
    private String professor;

    @Column(name="raw_time_text", length=255)
    private String rawTimeText;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    private Course(
            Long termId,
            String courseCode,
            String courseName,
            Integer section,
            Integer credits,
            String professor,
            String rawTimeText,
            Integer lectureHours,
            Integer labHours,
            Integer designHours,
            Integer capacity,
            String category,
            String opening,
            String target,
            String grade
    ) {
        this.termId = termId;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.section = section;
        this.credits = credits;
        this.professor = professor;
        this.rawTimeText = rawTimeText;
        this.lectureHours = lectureHours;
        this.labHours = labHours;
        this.designHours = designHours;
        this.capacity = capacity;
        this.category = category;
        this.opening = opening;
        this.target = target;
        this.grade = grade;
    }

    public Course() {

    }


    public static Course create(
            Long termId,
            String courseCode,
            String courseName,
            Integer section,
            Integer credits,
            String professor,
            String rawTimeText,
            Integer lectureHours,
            Integer labHours,
            Integer designHours,
            Integer capacity,
            String category,
            String opening,
            String target,
            String grade
    ) {
        return new Course(
                termId,
                courseCode.trim(),
                courseName.trim(),
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
                grade
        );
    }
}
