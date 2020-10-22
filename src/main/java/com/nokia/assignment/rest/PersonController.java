package com.nokia.assignment.rest;

import com.nokia.assignment.model.view.Person;
import com.nokia.assignment.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class PersonController {

    @Autowired
    private PersonService personService;

    @RequestMapping(method = RequestMethod.POST, value = "/person")
    @ResponseStatus(HttpStatus.OK)
    public boolean add(@RequestBody Person person) {
        return personService.add(person.getId(), person.getName());
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/person/{name}")
    @ResponseStatus(HttpStatus.OK)
    public int deleteByName(@PathVariable("name") String name) {
        return personService.deleteByName(name);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/person/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<com.nokia.assignment.model.service.Person> searchByName(@PathVariable("name") String name) {
        return personService.searchByName(name);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/person")
    @ResponseStatus(HttpStatus.OK)
    public ArrayList<com.nokia.assignment.model.service.Person> getAll() {
        return personService.getAll();
    }
}
