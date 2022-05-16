package com.fb.print.entity;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.util.List;

public class Multimedia implements Serializable {

    @JsonbProperty("faces")
    private List<Image> faces;

    @JsonbProperty("house")
    private Image house;

    public List<Image> getFaces() {
        return faces;
    }

    public void setFaces(final List<Image> faces) {
        this.faces = faces;
    }

    public Image getHouse() {
        return house;
    }

    public void setHouse(final Image house) {
        this.house = house;
    }
}
