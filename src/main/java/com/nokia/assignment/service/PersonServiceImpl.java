package com.nokia.assignment.service;

import com.nokia.assignment.model.service.Person;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    private static Map<String, Person> persons = new HashMap<>();

    @Override
    public boolean add(String id, String name) {
        synchronized (persons){
            System.out.println("add operation is started for thread " + Thread.currentThread().getName());
            if (isPersonExist(id))
                return false;

            if (!isHeapMemoryHealthy())
                return false;

            persons.put(id, new Person(id, name));
            System.out.println("add operation is finished for thread " + Thread.currentThread().getName());
        }
        return true;
    }

    @Override
    public  int deleteByName(String name) {
        int numberOfDeletedItems = 0;

        synchronized (persons) {
            System.out.println("delete operation is started for thread " + Thread.currentThread().getName());
            Iterator<Map.Entry<String, Person>> it = persons.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, Person> pair = it.next();
                if (pair.getValue().getName().equals(name)) {
                    it.remove();
                    numberOfDeletedItems++;
                }
            }
            System.out.println("delete operation is finished for thread " + Thread.currentThread().getName());
        }
        return numberOfDeletedItems;
    }

    @Override
    public ArrayList<Person> searchByName(String name) {
        ArrayList<Person> personsResult = new ArrayList<>();
        synchronized (persons){
            System.out.println("Search operation is started for thread  " + Thread.currentThread().getName());
            for (Map.Entry<String, Person> entry : persons.entrySet()) {
                if (entry.getValue().getName().equals(name)) {
                    personsResult.add(entry.getValue());
                }
            }
            System.out.println("Search operation is finished for thread  " + Thread.currentThread().getName());
        }
        return personsResult;
    }

    @Override
    public ArrayList<Person> getAll() {
        synchronized (persons) {
            return (ArrayList<Person>) persons.values().stream().collect(Collectors.toList());
        }
    }

    @Override
    public void clearAll() { // internal use for junit tests. No need for the synchronized
        persons.clear();
    }

    private boolean isPersonExist(String id) {
        return (persons.get(id) != null);
    }

    private boolean isHeapMemoryHealthy() {
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long maxHeapSize = memoryBean.getHeapMemoryUsage().getMax();
        long usedHeapSize = memoryBean.getHeapMemoryUsage().getUsed();
        return ((maxHeapSize - usedHeapSize) > 5120); // Alarm when heap free size less than 5 MByte
    }
}