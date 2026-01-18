package org.example.entity;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses",
        uniqueConstraints = @UniqueConstraint(name="uq_course_term_code_section",
                columnNames = {"term_id","course_code","section"}))
public class courses {
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

    @Column(name="credits")
    private Integer credits;

    @Column(name="professor", length=50)
    private String professor;

    @Column(name="raw_time_text", length=255)
    private String rawTimeText;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name="updated_at", nullable=false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
