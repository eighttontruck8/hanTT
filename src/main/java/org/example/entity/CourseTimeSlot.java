package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    public void setDayOrder(int order) {
    }

    public void setDayKr(String name) {
    }
}
