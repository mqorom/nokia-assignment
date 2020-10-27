package com.nokia.assignment;

import com.nokia.assignment.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OutOfMemoryTests {

    @Autowired
    private PersonService personService;

    @Test
    public void outOfMemory() {
        initializeTest();
        String personName = "person";
        int staticPersonsNumber = 10000;
        for (int i = 0; i < staticPersonsNumber; ++i) {
            personService.add("id_" + i, personName);
        }

        // Given
        int i = 0;
        System.out.println("Filling the memory by adding huge number of persons, please wait");
        while (true) {
            String uniqueString = "person_" + i++ + ModelFactory.randomString(5000);
            boolean isAdded = personService.add(uniqueString, uniqueString);
            if (!isAdded) { // When OOM happened the add method returns false because id here is always unique
                System.out.println("out of memory achieved and handled successfully!");
                break;
            }
            ModelFactory.sleep(2);
        }

        // Make sure that data is still available in memory by calling search method
        assertEquals(staticPersonsNumber, personService.searchByName(personName).size());

        // delete staticPersonsNumber of persons
        assertEquals(staticPersonsNumber, personService.deleteByName(personName));

        // Now the memory should be fine after deleting $staticPersonsNumber of persons so we can add more persons again!
        assertTrue(personService.add("id1", personName));
        assertTrue(personService.add("id2", personName));
    }

    private void initializeTest() {
        personService.clearAll();
    }
}
