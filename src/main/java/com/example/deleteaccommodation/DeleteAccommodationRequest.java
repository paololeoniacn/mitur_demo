package com.example.deleteaccommodation;

import lombok.Getter;

@Getter
class DeleteAccommodationRequest {
    private String identifier;
    private String type;
    private String primaryAtecoCod;
    private String startDateAtecoNew;

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPrimaryAtecoCod(String primaryAtecoCod) {
        this.primaryAtecoCod = primaryAtecoCod;
    }

    public void setStartDateAtecoNew(String startDateAtecoNew) {
        this.startDateAtecoNew = startDateAtecoNew;
    }
}
