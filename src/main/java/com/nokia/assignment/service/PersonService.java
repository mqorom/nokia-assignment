package com.nokia.assignment.service;

import com.nokia.assignment.model.service.Person;

import java.util.ArrayList;

public interface PersonService {
    boolean add(String id, String name);

    int deleteByName(String name);

    ArrayList<Person> searchByName(String name);

    ArrayList<Person> getAll();

    void clearAll();
}
