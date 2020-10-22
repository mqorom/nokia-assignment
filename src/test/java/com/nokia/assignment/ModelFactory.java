package com.nokia.assignment;

import com.nokia.assignment.model.view.Person;

import java.util.ArrayList;
import java.util.Random;

public class ModelFactory {

    private static Random random = new Random();

    public static String randomString() {
        return randomString(10);
    }

    public static String randomString(int numberOfCharacter) {
        return random.ints(97, 123) // a-z
                .limit(numberOfCharacter) // 10 character
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static Person person() {
        return new Person(randomString(), randomString());
    }

    public static Person person(int IdLength) {
        return new Person(randomString(IdLength), randomString());
    }

    public static Person person(String id, String name) {
        return new Person(id, name);
    }

    public static ArrayList<Person> persons(int numberOfPersons) {
        ArrayList<Person> persons = new ArrayList<>();
        for (int i = 1; i <= numberOfPersons; ++i) {
            String uniqueString = "person_" + i;
            persons.add(person(uniqueString, uniqueString));
        }
        return persons;
    }
}
