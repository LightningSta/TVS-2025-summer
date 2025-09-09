package org.example.databaseservice.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.databaseservice.Entity.Formula;
import org.example.databaseservice.Entity.Person;
import org.example.databaseservice.Repository.FormulaRepository;
import org.example.databaseservice.Repository.PersonRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/db/formulas")
@RequiredArgsConstructor
@Tag(name = "Forumals Methods")
public class FormulaResource {

    private final FormulaRepository formulaRepository;

    private final PersonRepository personRepository;

    private final ObjectMapper objectMapper;

    @GetMapping
    public PagedModel<Formula> getAll(Pageable pageable) {
        Page<Formula> formulas = formulaRepository.findAll(pageable);
        return new PagedModel<>(formulas);
    }

    @PostMapping("/not")
    public ResponseEntity<String> postFormula(@RequestBody String json) {
        JSONObject jsonObject = new JSONObject(json);
        System.out.println(jsonObject.toString(2));
        try {
            Person person = personRepository.findPersonByLogin(jsonObject.getString("login"));
            System.out.println(person);
            List<Formula> formulas = formulaRepository.findAllByPersonIdNot(person.getId());
            for (Formula formula : formulas) {
                formula.setPerson(null);
            }
            JSONObject answer = new JSONObject();
            answer.put("formulas", formulas);
            return new ResponseEntity<>(answer.toString(2), HttpStatus.resolve(200));
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping("/people")
    public ResponseEntity<String> getPeople(@RequestBody String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Person person = personRepository.findPersonByLogin(jsonObject.getString("login"));
            List<Formula> formulas = formulaRepository.findAllByPersonId(person.getId());
            JSONArray jsonArray = new JSONArray();
            for (Formula formula : formulas) {
                JSONObject formulaJson = new JSONObject();
                formulaJson.put("latex", formula.getFormula().get("latex"));
                formulaJson.put("name",formula.getFormula().get("name"));
                jsonArray.put(formulaJson);
            }
            return ResponseEntity.ok(jsonArray.toString(2));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Formula getOne(@PathVariable Integer id) {
        Optional<Formula> formulaOptional = formulaRepository.findById(id);
        return formulaOptional.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));
    }

    @GetMapping("/by-ids")
    public List<Formula> getMany(@RequestParam List<Integer> ids) {
        return formulaRepository.findAllById(ids);
    }

    @PostMapping
    public Formula create(@RequestBody String json) {
        JSONObject jsonObject = new JSONObject(json);
        Person person = personRepository.findPersonByLogin(jsonObject.getString("login"));
        if(person == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid login");
        }else{
            Formula formula = new Formula();
            formula.setPerson(person);
            formula.setFormula( jsonObject.getJSONObject("formula").toMap());
            return formulaRepository.save(formula);
        }
    }

    @PatchMapping("/{id}")
    public Formula patch(@PathVariable Integer id, @RequestBody JsonNode patchNode) throws IOException {
        Formula formula = formulaRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Entity with id `%s` not found".formatted(id)));

        objectMapper.readerForUpdating(formula).readValue(patchNode);

        return formulaRepository.save(formula);
    }

    @PatchMapping
    public List<Integer> patchMany(@RequestParam List<Integer> ids, @RequestBody JsonNode patchNode) throws IOException {
        Collection<Formula> formulas = formulaRepository.findAllById(ids);

        for (Formula formula : formulas) {
            objectMapper.readerForUpdating(formula).readValue(patchNode);
        }

        List<Formula> resultFormulas = formulaRepository.saveAll(formulas);
        return resultFormulas.stream()
                .map(Formula::getId)
                .toList();
    }

    @DeleteMapping("/{id}")
    public Formula delete(@PathVariable Integer id) {
        Formula formula = formulaRepository.findById(id).orElse(null);
        if (formula != null) {
            formulaRepository.delete(formula);
        }
        return formula;
    }

    @DeleteMapping
    public void deleteMany(@RequestParam List<Integer> ids) {
        formulaRepository.deleteAllById(ids);
    }
}
