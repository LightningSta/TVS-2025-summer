package org.example.databaseservice.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.POST;
import lombok.RequiredArgsConstructor;
import org.example.databaseservice.Entity.Person;
import org.example.databaseservice.Repository.PersonRepository;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/db/person")
@RequiredArgsConstructor
@Tag(name = "Persons Methods")
public class PersonResource {

    private final PersonRepository personRepository;

    private final ObjectMapper objectMapper;


    @PostMapping("/exist")
    public ResponseEntity<Boolean> exist(@RequestBody String json) {
        JSONObject jsonObj = new JSONObject(json);
        Person person = personRepository.findPersonByLogin(jsonObj.getString("login"));
        if (person == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/auth")
    public ResponseEntity<Person> auth(@RequestBody String json) {
        System.out.println(json);
        JSONObject jsonObj = new JSONObject(json);
        Person person = personRepository.findPersonByLogin(jsonObj.getString("login"));
        System.out.println(person);
        if (person == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }else{
            System.out.println(passwordEncoder.matches(jsonObj.getString("password"), person.getPassword()));
            if(passwordEncoder.matches(jsonObj.getString("password"), person.getPassword())){
                person.setPassword(null);
                person.setId(null);
                return new ResponseEntity<>(person, HttpStatus.OK);
            }else{
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
    }

    @GetMapping
    public PagedModel<Person> getAll(Pageable pageable) {
        Page<Person> people = personRepository.findAll(pageable);
        return new PagedModel<>(people);
    }

    @GetMapping("/{id}")
    public Person getOne(@PathVariable Integer id) {
        Optional<Person> personOptional = personRepository.findById(id);
        return personOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @GetMapping("/by-ids")
    public List<Person> getMany(@RequestParam List<Integer> ids) {
        return personRepository.findAllById(ids);
    }

    @PostMapping
    public Person create(@RequestBody Person person) {
        Person person_exist = personRepository.findPersonByLogin(person.getLogin());
        System.out.println(person_exist);
        if (person_exist == null) {
            return personRepository.save(person);
        }
        return person;
    }
    @RequestMapping("/test")
    public String test() {
        return "Hello World";
    }

    @PatchMapping("/{id}")
    public Person patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
        Person person = personRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(person).readValue(patchNode);

        return personRepository.save(person);
    }

    @PatchMapping
    public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) throws IOException {
        Collection<Person> people = personRepository.findAllById(ids);

        for (Person person : people) {
            objectMapper.readerForUpdating(person).readValue(patchNode);
        }

        List<Person> resultPeople = personRepository.saveAll(people);
        return resultPeople.stream()
                .map(Person::getId)
                .toList();
    }

    @DeleteMapping("/{id}")
    public Person delete(@PathVariable Integer id) {
        Person person = personRepository.findById(id).orElse(null);
        if (person != null) {
            personRepository.delete(person);
        }
        return person;
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Integer> ids) {
        personRepository.deleteAllById(ids);
    }
}
