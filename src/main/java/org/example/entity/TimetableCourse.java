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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "timetable_id", nullable = false)
    private Long timetableId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    public TimetableCourse(Long timetableId, Long courseId) {
        this.timetableId = timetableId;
        this.courseId = courseId;
    }
}
