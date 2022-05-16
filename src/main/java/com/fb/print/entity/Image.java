package com.fb.print.entity;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;

public class Image implements Serializable {

    @JsonbProperty("identifier")
    private String identifier;

    @JsonbProperty("rtfReference")
    private RtfReferenceEnum rtfReference;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public RtfReferenceEnum getRtfReference() {
        return rtfReference;
    }

    public void setRtfReference(final RtfReferenceEnum rtfReference) {
        this.rtfReference = rtfReference;
    }
}
