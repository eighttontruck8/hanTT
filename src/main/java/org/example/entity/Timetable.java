package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "timetables")
//        , uniqueConstraints = {
//        @UniqueConstraint(
//                name = "uq_terms_year_semester",
//                columnNames = {"user", "term_id"} // 한 학기에 한 시간표만 만들도록 제한
//        )}
public class Timetable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timetable_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId; // 로그인 전이면 null 허용(테이블도 null 허용이어야 함)

    @Column(name = "term_id")
    private Long termId;

    @Column(name = "title")
    private String title;

    @Column(name = "visibility")
    private String visibility; // PUBLIC/PRIVATE 등(네 DB 타입에 맞춰)

    @Column(name = "theme_color")
    private String themeColor;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected Timetable() { }

    public Timetable(Long userId, Long termId, String title) {
        this.userId = userId;
        this.termId = termId;
        this.title = (title == null || title.isBlank()) ? "새 시간표" : title;
        this.visibility = "PUBLIC";
        this.themeColor = "BLUE";
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
