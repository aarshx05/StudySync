package com.aarsh.app.studysyncv1;

public class Notes {
    private String notesName;
    private String notesId;

    public Notes(){}
    public Notes(String notesName, String notesId) {
        this.notesName = notesName;
        this.notesId = notesId;
    }


    public String getNotesId() {
        return notesId;
    }

    public void setNotesId(String notesId) {
        this.notesId = notesId;
    }

    public String getNotesName() {
        return notesName;
    }

    public void setNotesName(String notesName) {
        this.notesName = notesName;
    }
}