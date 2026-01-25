package org.example.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "timetable_courses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_timetable_course",
                        columnNames = {"timetable_id", "course_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableCourse {

    @Id
    @Column(name = "timetable_course_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timetable_id", nullable = false)
    private Long timetableId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "color", length = 7)
    private String colorCode;

    public TimetableCourse(Long timetableId, Long courseId, String colorCode) {
        this.timetableId = timetableId;
        this.courseId = courseId;
        this.colorCode = colorCode;
    }

    public static TimetableCourse create(Long timetableId, Long courseId, String color) {
        return new TimetableCourse(timetableId, courseId, colorCode);
    }
}
