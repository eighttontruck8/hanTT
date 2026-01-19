package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "terms", uniqueConstraints = {
        @UniqueConstraint(
                name = "uq_terms_year_semester",
                columnNames = {"year", "semester"}
        )
}
)
public class Term {
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer year;      // 2026

    @Column(nullable = false)
    private Integer semester; // 1-1학기, 2-2학기, 3-여름학기,4-겨울학기

    protected Term() {
    }

    public Term(int year, int semester) {
        this.year = year;
        this.semester = semester;
    }
}
