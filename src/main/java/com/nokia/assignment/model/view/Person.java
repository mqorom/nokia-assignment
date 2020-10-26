package com.nokia.assignment.model.view;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Person {

    @NotNull(message="Id can't be null")
//    @Size(min = 3, max = 25)
    private String id;

    @NotNull(message="Name can't be null")
//    @Size(min = 4, max = 25)
    private String name;

    public Person(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
