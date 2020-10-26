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

    private Map<String, Person> persons = new HashMap<>();
    private SoftReference<CustomObject> personsReference = new SoftReference<>(new CustomObject(persons));

    @Override
    public boolean add(String id, String name) {
        synchronized (personsReference) {
//            System.out.println("add operation is started for thread " + Thread.currentThread().getId());
            if(personsReference.get() == null){
                personsReference = new SoftReference<>(new CustomObject(persons));
                return false;
            }
            if (isPersonExist(id))
                return false;

            personsReference.get().getPersons().put(id, new Person(id, name));
//            System.out.println("add operation is finished for thread " + Thread.currentThread().getId());
        }
        return true;
    }

    @Override
    public int deleteByName(String name) {
        int numberOfDeletedItems = 0;

        synchronized (personsReference) {
//            System.out.println("delete operation is started for thread " + Thread.currentThread().getId());
            Iterator<Map.Entry<String, Person>> it = personsReference.get().getPersons().entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, Person> pair = it.next();
                if (pair.getValue().getName().equals(name)) {
                    it.remove();
                    numberOfDeletedItems++;
                }
            }
//            System.out.println("delete operation is finished for thread " + Thread.currentThread().getId());
        }
        return numberOfDeletedItems;
    }

    @Override
    public ArrayList<Person> searchByName(String name) {
        ArrayList<Person> personsResult = new ArrayList<>();
        synchronized (personsReference) {
//            System.out.println("Search operation is started for thread  " + Thread.currentThread().getId());
            for (Map.Entry<String, Person> entry : personsReference.get().getPersons().entrySet()) {
                if (entry.getValue().getName().equals(name)) {
                    personsResult.add(entry.getValue());
                }
            }
//            System.out.println("Search operation is finished for thread  " + Thread.currentThread().getId());
        }
        return personsResult;
    }

    @Override
    public ArrayList<Person> getAll() {  // internal use for junit tests. No need to handle synchronization
        return (ArrayList<Person>) personsReference.get().getPersons().values().stream().collect(Collectors.toList());
    }

    @Override
    public void clearAll() { // internal use for junit tests. No need to handle synchronization
        personsReference.get().getPersons().clear();
    }

    private boolean isPersonExist(String id) {
        return (personsReference.get().getPersons().get(id) != null);
    }


    private class CustomObject{
        private Map<String, Person> persons;

        public CustomObject(Map<String, Person> persons) {
            this.persons = persons;
        }

        public Map<String, Person> getPersons() {
            return persons;
        }
    }
}