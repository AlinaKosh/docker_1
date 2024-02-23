package ru.alinka.restproject.FirstRestApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.alinka.restproject.FirstRestApp.model.Person;

@Repository
public interface PeopleRepository extends JpaRepository<Person, Integer> {
}
