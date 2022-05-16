package com.fb.print.entity;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

public class Data implements Serializable {

    @JsonbProperty("person")
    private Person person;

    @JsonbProperty("multimedia")
    private Multimedia multimedia;

    public Person getPerson() {
        return person;
    }

    public void setPerson(final Person person) {
        this.person = person;
    }

    public Multimedia getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(final Multimedia multimedia) {
        this.multimedia = multimedia;
    }
}
