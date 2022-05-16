package com.fb.print.entity;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

public class Settings implements Serializable {

    @JsonbProperty("printImages")
    private boolean printImages;

    @JsonbProperty("printAddress")
    private boolean printAddress;

    @JsonbProperty("printPerson")
    private boolean printPerson;

    public boolean isPrintImages() {
        return printImages;
    }

    public void setPrintImages(final boolean printImages) {
        this.printImages = printImages;
    }

    public boolean isPrintAddress() {
        return printAddress;
    }

    public void setPrintAddress(final boolean printAddress) {
        this.printAddress = printAddress;
    }

    public boolean isPrintPerson() {
        return printPerson;
    }

    public void setPrintPerson(final boolean printPerson) {
        this.printPerson = printPerson;
    }
}
