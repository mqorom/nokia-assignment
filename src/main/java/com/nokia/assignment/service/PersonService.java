package com.nokia.assignment.service;

import com.nokia.assignment.model.service.Person;

import java.util.ArrayList;

public interface PersonService {
    boolean addPerson(String id, String name);

    int deletePerson(String name);

    ArrayList<Person> searchPersonByName(String name);

    ArrayList<Person> getAllPersons();

    void clearAllPersons();
}
