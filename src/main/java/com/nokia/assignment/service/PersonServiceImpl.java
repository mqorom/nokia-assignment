package com.nokia.assignment.service;

import com.nokia.assignment.model.service.Person;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    private ArrayList<Person> persons = new ArrayList<>();


    @Override
    public synchronized boolean addPerson(String id, String name) {
        if (isPersonExist(id))
            return false;
        return persons.add(new Person(id, name));
    }

    @Override
    public synchronized int deletePerson(String name) {
        int originalPersons = persons.size();
        persons.removeIf(person -> person.getName().equals(name));
        return originalPersons - persons.size();
    }

    @Override
    public ArrayList<Person> searchPersonByName(String name) {
        return (ArrayList<Person>) persons.stream().filter(obj -> obj.getName().equals(name)).collect(Collectors.toList());
    }

    @Override
    public ArrayList<Person> getAllPersons() {
        return persons;
    }

    @Override
    public void clearAllPersons() {
        persons = new ArrayList<>();
    }

    private boolean isPersonExist(String id) {
        Optional<Person> person = persons.stream().filter(obj -> obj.getId().equals(id)).findFirst();
        return person.isPresent();
    }
}