package com.nokia.assignment.service;

import com.nokia.assignment.model.service.Person;
import org.springframework.stereotype.Service;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonService {

    private SoftReference<Map<String, Person>> personsReference = new SoftReference<>(new HashMap<>());

    @Override
    public boolean add(String id, String name) {
        synchronized (personsReference) {
            System.out.println("add operation is started for thread " + Thread.currentThread().getId());
            if (personsReference.get() == null) { // It will be null when the OOM is reached
                return false;
            }

            if (isPersonExist(id))
                return false;

            personsReference.get().put(id, new Person(id, name));
            System.out.println("add operation is finished for thread " + Thread.currentThread().getId());
        }
        return true;
    }

    @Override
    public int deleteByName(String name) {
        int numberOfDeletedItems = 0;

        synchronized (personsReference) {
            System.out.println("delete operation is started for thread " + Thread.currentThread().getId());
            Iterator<Map.Entry<String, Person>> it = personsReference.get().entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, Person> pair = it.next();
                if (pair.getValue().getName().equals(name)) {
                    it.remove();
                    numberOfDeletedItems++;
                }
            }
            System.out.println("delete operation is finished for thread " + Thread.currentThread().getId());
        }
        return numberOfDeletedItems;
    }

    @Override
    public ArrayList<Person> searchByName(String name) {
        ArrayList<Person> personsResult = new ArrayList<>();
        synchronized (personsReference) {
            System.out.println("Search operation is started for thread  " + Thread.currentThread().getId());
            for (Map.Entry<String, Person> entry : personsReference.get().entrySet()) {
                if (entry.getValue().getName().equals(name)) {
                    personsResult.add(entry.getValue());
                }
            }
            System.out.println("Search operation is finished for thread  " + Thread.currentThread().getId());
        }
        return personsResult;
    }

    @Override
    public ArrayList<Person> getAll() {  // internal use for junit tests. No need to handle synchronization
        return (ArrayList<Person>) personsReference.get().values().stream().collect(Collectors.toList());
    }

    @Override
    public void clearAll() { // internal use for junit tests. No need to handle synchronization
        personsReference.get().clear();
    }

    private boolean isPersonExist(String id) {
        return (personsReference.get().get(id) != null);
    }
}