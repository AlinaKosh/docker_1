package ru.alinka.restproject.FirstRestApp.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.alinka.restproject.FirstRestApp.dto.PersonDTO;
import ru.alinka.restproject.FirstRestApp.model.Person;
import ru.alinka.restproject.FirstRestApp.services.PeopleServices;
import ru.alinka.restproject.FirstRestApp.util.PersonErrorResponse;
import ru.alinka.restproject.FirstRestApp.util.PersonNotCreatedException;
import ru.alinka.restproject.FirstRestApp.util.PersonNotFoundException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController //эта аннотация говорит о там, что вернётся не представление, а буду возвращаться данные @Controller+@ResponseBody над каждым методом
@RequestMapping("/people")
public class PeopleController {
    private final PeopleServices peopleServices;
    private final ModelMapper modelMapper;//ModelMapper - эта штука помогает нам не вручную писать необходимые поля, а конвертировать всё то, что нам надо автоматически,
    //то есть нам не надо писать, к примеру: person.setName(personDTO.getName()) и так далее с другими полями, ModelMapper сам понимает то, что нам необходимо передать,
    //если бы у нас было не 3 поля как у нас, а 100 полей, то это было долго прописывать всё в ручную (мы внедрили бин в Spring Boot (посмотри в FirstRestApplication))

    @Autowired
    public PeopleController(PeopleServices peopleServices, ModelMapper modelMapper) {
        this.peopleServices = peopleServices;
        this.modelMapper = modelMapper;
    }
    @GetMapping()
    public List<PersonDTO> getPeople(){
        return peopleServices.findAll().stream().map(this::convertToPersonDTO)
                .collect(Collectors.toList()); //Jackson автоматически конвертирует в JSON
    }

    @PostMapping()
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid PersonDTO personDTO,
                                             BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            StringBuilder errorsMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error:errors){
                errorsMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }
            throw new PersonNotCreatedException(errorsMsg.toString());
        }
        peopleServices.save(convertToPerson(personDTO));
        //Отправляем HTTP ответ с пустым телом и статусом 200
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public PersonDTO getPerson(@PathVariable("id") int id){
        //Статус 200
        return convertToPersonDTO(peopleServices.findOne(id));
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id){
        peopleServices.delete(id);
        //Отправляем HTTP ответ с пустым телом и статусом 200
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e){
        PersonErrorResponse response = new PersonErrorResponse(
                "Person with this wasn't found!",
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); //NOT_FOUND - 404
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e){
        PersonErrorResponse response = new PersonErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); //NOT_FOUND - 404
    }

    private Person convertToPerson(PersonDTO personDTO) {
        return modelMapper.map(personDTO, Person.class);
    }

    private PersonDTO convertToPersonDTO(Person person){
        return modelMapper.map(person, PersonDTO.class);
    }

}
