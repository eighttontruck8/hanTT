package org.example.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.Time.TimeParser;
import org.example.Time.TimeSlot;
import org.example.Repository.CourseRepository;
import org.example.Repository.CourseTimeSlotRepository;
import org.example.entity.Course;
import org.example.entity.CourseTimeSlot;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseTimeSlotService {

    private final CourseRepository courseRepository;
    private final CourseTimeSlotRepository slotRepository;

    @Transactional
    public int buildSlotsForTerm(Long termId) {
        List<Course> courses = courseRepository.findByTermId(termId);

        // term 전체 재생성
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        slotRepository.deleteByCourseIdIn(courseIds);

        int saved = 0;

        for (Course c : courses) {
            List<TimeSlot> slots = TimeParser.parse(c.getRawTimeText());

            for (TimeSlot ts : slots) {
                CourseTimeSlot e = new CourseTimeSlot();
                e.setCourseId(c.getId());

                e.setDay(ts.day().getOrder());
                e.setStartPeriod(ts.sp());
                e.setStartHalf(ts.sh());
                e.setEndPeriod(ts.ep());
                e.setEndHalf(ts.eh());
                e.setOnline(ts.online());

                slotRepository.save(e);
                saved++;
            }
        }
        return saved;
    }
}
