package com.nokia.assignment;

import com.nokia.assignment.model.service.Person;
import com.nokia.assignment.rest.PersonController;
import com.nokia.assignment.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AssignmentApplicationTests {

    @Autowired
    private PersonController personController;

    @Autowired
    private PersonService personService;

    @Autowired
    private TestRestTemplate restTemplate;

    private final String PERSON_URL = "/person/";

    @Test
    void contextLoads() {
        assertThat(personController).isNotNull();
    }

    @Test
    public void getEmptyPersons() {
        intializeTest();
        ArrayList<Person> persons = getAllPersons();
        assertEquals(0, persons.size());
    }

    @Test
    public void searchForNonExistingPersonInEmptyList() {
        intializeTest();
        ArrayList<Person> persons = searchPersonByName(ModelFactory.randomString());
        assertEquals(0, persons.size());
    }

    @Test
    public void addPersonsWithDifferentIds() {
        intializeTest();
        // Given
        com.nokia.assignment.model.view.Person person1 = ModelFactory.person(5);
        com.nokia.assignment.model.view.Person person2 = ModelFactory.person(6);
        com.nokia.assignment.model.view.Person person3 = ModelFactory.person(7);
        com.nokia.assignment.model.view.Person person4 = ModelFactory.person(8);

        // When
        addPerson(person1);
        addPerson(person2);
        addPerson(person3);
        addPerson(person4);

        // Then all persons are added
        ArrayList<Person> persons = getAllPersons();
        assertEquals(4, persons.size());
    }

    @Test
    public void addPersonsWithSameAndDifferentId() {
        intializeTest();
        com.nokia.assignment.model.view.Person person = ModelFactory.person();

        // Add person
        boolean result = addPerson(person);
        // Then
        assertTrue(result, "Invalid add person result");

        // Add the person again
        result = addPerson(person);
        //Then
        assertFalse(result, "Invalid add person result");

        // Add the same person but with same id and different name
        person.setName(person.getName().concat(ModelFactory.randomString()));
        result = addPerson(person);
        //Then
        assertFalse(result, "Invalid add person result");

        // Add another person with different id
        person.setId(person.getId().concat(ModelFactory.randomString()));
        //Then
        result = addPerson(person);
        assertTrue(result, "Invalid add person result");

        // Get all persons
        ArrayList<Person> persons = getAllPersons();
        // Then two persons are only added
        assertEquals(2, persons.size());
    }

    @Test
    public void searchByName() {
        intializeTest();
        // Given
        com.nokia.assignment.model.view.Person person1 = ModelFactory.person(5);
        com.nokia.assignment.model.view.Person person2 = ModelFactory.person(6);
        com.nokia.assignment.model.view.Person person3 = ModelFactory.person(7);
        com.nokia.assignment.model.view.Person person4 = ModelFactory.person(8);

        // person4 & 3 have the same name
        person4.setName(person3.getName());

        // When
        addPerson(person1);
        addPerson(person2);
        addPerson(person3);
        addPerson(person4);

        // Then search for the name of person1
        ArrayList<Person> persons = searchPersonByName(person1.getName());
        assertEquals(1, persons.size());

        // Then search for the name of person3 & 4
        persons = searchPersonByName(person3.getName());
        assertEquals(2, persons.size());
    }

    @Test
    public void deletePerson() {
        intializeTest();
        // Given
        com.nokia.assignment.model.view.Person person1 = ModelFactory.person(5);
        com.nokia.assignment.model.view.Person person2 = ModelFactory.person(6);
        com.nokia.assignment.model.view.Person person3 = ModelFactory.person(7);
        com.nokia.assignment.model.view.Person person4 = ModelFactory.person(8);

        // person4 & 3 have the same name
        person4.setName(person3.getName());

        // When
        addPerson(person1);
        addPerson(person2);
        addPerson(person3);
        addPerson(person4);

        // Then delete all persons with name of person1
        int numberOfDeletedPersons = deletePersons(person1.getName());
        assertEquals(1, numberOfDeletedPersons);

        // Then delete non existing name
        numberOfDeletedPersons = deletePersons(person1.getName());
        assertEquals(0, numberOfDeletedPersons);

        // Then delete all persons with name of 3 & 4
        numberOfDeletedPersons = deletePersons(person3.getName());
        assertEquals(2, numberOfDeletedPersons);

        // Get all persons
        ArrayList<Person> persons = getAllPersons();
        // Only one exists which is person2
        assertEquals(1, persons.size());
        assertEquals(person2.getId(), persons.get(0).getId());
        assertEquals(person2.getName(), persons.get(0).getName());
    }

    @Test
    public void addPersonsConcurrentRequests() throws InterruptedException {
        intializeTest();
        // Given
        int numberOfPersonsPerThread = 1000;
        Thread thread1 = new Thread(() -> addPersons(numberOfPersonsPerThread));
        Thread thread2 = new Thread(() -> addPersons(numberOfPersonsPerThread));
        Thread thread3 = new Thread(() -> addPersons(numberOfPersonsPerThread));

        thread1.start();
        thread2.start();
        thread3.start();

        // wait until all threads completed
        thread1.join();
        thread2.join();
        thread3.join();

        // Then all persons are added
        ArrayList<Person> persons = getAllPersons();
        assertEquals(3000, persons.size());
    }

    @Test
    public void deletePersonsConcurrentRequests() throws InterruptedException {
        intializeTest();

        // Given
        ArrayList<com.nokia.assignment.model.view.Person> persons = ModelFactory.persons(10);
        // Add all persons
        for(com.nokia.assignment.model.view.Person person: persons){
            addPerson(person);
        }

        Thread thread1 = new Thread(() -> addPersons(100)); // add more persons
        Thread thread2 = new Thread(() -> deletePersons(persons)); // delete all 10 persons added initially

        thread1.start();
        thread2.start();

        // wait until all threads completed
        thread1.join();
        thread2.join();

        // Then
        ArrayList<Person> result = getAllPersons();
        assertEquals(100, result.size());
    }

    private ArrayList<Person> getAllPersons() {
        return getPersons(PERSON_URL);
    }

    private ArrayList<Person> searchPersonByName(String name) {
        return getPersons(PERSON_URL + name);
    }

    private ArrayList<Person> getPersons(String url) {
        ResponseEntity<ArrayList<Person>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<ArrayList<Person>>() {
        });
        assertHttpStatus(response.getStatusCode());
        return response.getBody();
    }

    private boolean addPerson(com.nokia.assignment.model.view.Person person) {
        HttpEntity<com.nokia.assignment.model.view.Person> input = new HttpEntity<>(person);
        ResponseEntity<Boolean> result = restTemplate.exchange(PERSON_URL, HttpMethod.POST, input, Boolean.class);
        assertHttpStatus(result.getStatusCode());
        return result.getBody();
    }

    private int deletePersons(String name) {
        ResponseEntity<Integer> response = restTemplate.exchange(PERSON_URL + name, HttpMethod.DELETE, null, Integer.class);
        assertHttpStatus(response.getStatusCode());
        return response.getBody();
    }

    private void assertHttpStatus(HttpStatus httpStatus) {
        assertTrue(HttpStatus.OK.equals(httpStatus), "Invalid http status " + httpStatus);
    }

    private void intializeTest() {
        personService.clearAllPersons();
    }

    private void addPersons(int number) {
        for (int i = 0; i < number; ++i) {
            String uniqueString = "person_" + i + "_" + Thread.currentThread().getId();
            com.nokia.assignment.model.view.Person person = ModelFactory.person(uniqueString, uniqueString);
            addPerson(person);
            sleep();
        }
    }

    private void deletePersons(ArrayList<com.nokia.assignment.model.view.Person> persons) {
        for (com.nokia.assignment.model.view.Person person: persons) {
            deletePersons(person.getName());
            sleep();
        }
    }

    private void sleep() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
