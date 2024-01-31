package com.aarsh.app.studysyncv1;

public class EventItem {
    private String text;
    private String imageUrl;

    public EventItem(String text, String imageUrl) {
        this.text = text;
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return text;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}