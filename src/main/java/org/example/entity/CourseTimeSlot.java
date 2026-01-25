package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.Time.TimeSlot;

@Entity
@Table(name="course_time_slots")
@Getter
@Setter
public class CourseTimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="course_id", nullable=false)
    private Long courseId;

    private int day;
    private int startPeriod;
    private int startHalf;
    private int endPeriod;
    private int endHalf;
    private boolean online;

    public static CourseTimeSlot from(Long courseId, TimeSlot ts) {
        CourseTimeSlot cts = new CourseTimeSlot();
        cts.setCourseId(courseId);
        cts.setDay(ts.day());
        cts.setStartPeriod(ts.sp());
        cts.setStartHalf(ts.sh() == 'A' ? 0 : 1);
        cts.setEndPeriod(ts.ep());
        cts.setEndHalf(ts.eh() == 'A' ? 0 : 1);
        cts.setOnline(ts.online());
        return cts;
    }

}
