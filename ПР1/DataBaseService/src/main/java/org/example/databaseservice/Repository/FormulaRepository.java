package org.example.databaseservice.Repository;

import org.example.databaseservice.Entity.Formula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface FormulaRepository extends JpaRepository<Formula, Integer> {
    List<Formula> findAllByPersonIdNot(Integer personId);
    List<Formula> findAllByPersonId(Integer personId);
}