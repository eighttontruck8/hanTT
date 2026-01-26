package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Getter
@Entity
@Table(name = "timetables")
public class Timetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "term_id")
    private Long termId;

    @Column(name = "title")
    private String title;

    @Column(name = "visibility")
    private String visibility; // PUBLIC/PRIVATE

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected Timetable() { }

    public Timetable(Long userId, Long termId, String title) {
        this.userId = userId;
        this.termId = termId;
        this.title = (title == null || title.isBlank()) ? "새 시간표" : title;
        this.visibility = "PUBLIC";
        this.createdAt = now();
        this.updatedAt = now();
    }

    public static Timetable create(Long userId, Long termId, String title) {
        String safe = (title == null || title.isBlank()) ? "새 시간표" : title.trim();
        return new Timetable(userId, termId, safe);
    }

    public void changeTitle(String title) {
        String safe = (title == null || title.isBlank()) ? "새 시간표" : title.trim();
        this.title = safe;
    }

}
