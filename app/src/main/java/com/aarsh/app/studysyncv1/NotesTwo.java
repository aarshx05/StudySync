package com.aarsh.app.studysyncv1;

public class NotesTwo {

    private String dataName;
    private String dataId;
    private String dataAge;


    public NotesTwo() {
    }

    public NotesTwo(String dataName, String dataId, String dataAge) {
        this.dataName = dataName;
        this.dataId = dataId;
        this.dataAge = dataAge;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getDataAge() {
        return dataAge;
    }

    public void setDataAge(String dataAge) {
        this.dataAge = dataAge;
    }

}