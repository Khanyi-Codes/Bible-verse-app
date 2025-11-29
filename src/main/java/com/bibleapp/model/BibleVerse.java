package com.bibleapp.model;

public class BibleVerse {
    private String reference;
    private String text;

    public BibleVerse(String reference, String text) {
        this.reference = reference;
        this.text = text;
    }

    public String getReference() {
        return reference;
    }

    public String getText() {
        return text;
    }
}