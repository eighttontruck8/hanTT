package org.example.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.DTO.TimeParser;
import org.example.DTO.TimeSlot;
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

        // term 전체 재생성 방식(깔끔)
        List<Long> courseIds = courses.stream().map(Course::getId).toList();
        slotRepository.deleteByCourseIdIn(courseIds);

        int saved = 0;

        for (Course c : courses) {
            List<TimeSlot> slots = TimeParser.parse(c.getRawTimeText());

            for (TimeSlot ts : slots) {
                CourseTimeSlot e = new CourseTimeSlot();
                e.setCourseId(c.getId());
                e.setDayOfWeek(ts.getDayOfWeek());
                e.setStartPeriod(ts.getStartPeriod());
                e.setStartHalf(ts.getStartHalf());
                e.setEndPeriod(ts.getEndPeriod());
                e.setEndHalf(ts.getEndHalf());
                e.setOnline(ts.isOnline());

                slotRepository.save(e);
                saved++;
            }
        }
        return saved;
    }
}
