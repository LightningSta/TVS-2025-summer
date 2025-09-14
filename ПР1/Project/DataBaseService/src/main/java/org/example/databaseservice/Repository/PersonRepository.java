package org.example.databaseservice.Repository;

import org.example.databaseservice.Entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PersonRepository extends JpaRepository<Person, Integer> {
    Person findPersonByLogin(String login);
}