package com.fb.print.entity;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

public class Person implements Serializable {

    @JsonbProperty("name")
    private String name;

    @JsonbProperty("age")
    private Integer age;

    @JsonbProperty("address")
    private Address address;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(final Integer age) {
        this.age = age;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(final Address address) {
        this.address = address;
    }
}
