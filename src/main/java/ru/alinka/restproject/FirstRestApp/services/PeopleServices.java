package ru.alinka.restproject.FirstRestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alinka.restproject.FirstRestApp.model.Person;
import ru.alinka.restproject.FirstRestApp.repositories.PeopleRepository;
import ru.alinka.restproject.FirstRestApp.util.PersonNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PeopleServices {
    private final PeopleRepository peopleRepository;

    @Autowired
    public PeopleServices(PeopleRepository peopleRepository) {
        this.peopleRepository = peopleRepository;
    }
    public List<Person> findAll(){
        return peopleRepository.findAll();
    }
    public Person findOne(int id){
        Optional<Person> foundPerson = peopleRepository.findById(id);
        return foundPerson.orElseThrow(PersonNotFoundException::new);
    }

    @Transactional
    public void save(Person person){
        enrichPerson(person); //чтобы также в модель записывались необходимые параметры
        peopleRepository.save(person);
    }

    @Transactional
    public void delete(int id){
        peopleRepository.deleteById(id);
    }

    @Transactional
    public void update(int id, Person upadtePerson){
        upadtePerson.setId(id);
        enrichPerson(upadtePerson);
        peopleRepository.save(upadtePerson);
    }

    private void enrichPerson(Person person) {
        person.setCreatedAt(LocalDateTime.now());
        person.setUpdatedAt(LocalDateTime.now());
        person.setCreatedWho("ADMIN");
    }
}
