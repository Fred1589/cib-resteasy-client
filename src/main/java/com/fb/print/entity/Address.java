package com.fb.print.entity;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

public class Address implements Serializable {

    @JsonbProperty("postbox")
    private String postBox;

    @JsonbProperty("street")
    private String street;

    @JsonbProperty("city")
    private String city;

    public String getPostBox() {
        return postBox;
    }

    public void setPostBox(final String postBox) {
        this.postBox = postBox;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }
}
