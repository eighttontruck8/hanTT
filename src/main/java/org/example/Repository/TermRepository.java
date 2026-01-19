package org.example.Repository;

import org.example.entity.Term;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TermRepository extends JpaRepository<Term, Long> {

    Optional<Term> findByYearAndSemester(int year, int semester);
}
