package com.nokia.assignment;

import com.nokia.assignment.service.PersonService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OutOfMemoryTests {

    @Autowired
    private PersonService personService;

    @Test
    public void outOfMemory() throws InterruptedException {
        initializeTest();
        String personName = "person1";

        personService.add("id1", personName);
        personService.add("id2", personName);

        // Given
        int i = 0;
        while (true) {
            System.out.println("i=" + i);
            String uniqueString = "person_" + i++ + ModelFactory.randomString(5000);
            boolean isAdded = personService.add(uniqueString, uniqueString);
            if (!isAdded) { // When OOM happened the add method returns false because id here is always unqiue
                System.out.println("out of memory achieved");
                break;
            }
            ModelFactory.sleep(2);
        }

        // Make sure that data is still saved

        // two persons with name personName
        assertEquals(personService.searchByName(personName).size(), 2);

        // delete 3 persons
        assertEquals(personService.deleteByName(personName), 2);
    }

    private void initializeTest() {
        personService.clearAll();
    }

}
