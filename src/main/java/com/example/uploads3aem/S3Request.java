package com.example.uploads3aem;

import lombok.Getter;


@Getter
public class S3Request {
    // Getter e Setter
    private String getPathS3;
    private String jsonString;

    public S3Request(String getPathS3, String jsonString){
        this.getPathS3 = this.jsonString;
    }

    public void setGetPathS3(String getPathS3) {
        this.getPathS3 = getPathS3;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }
}
