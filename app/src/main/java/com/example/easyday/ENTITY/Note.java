package com.example.easyday.ENTITY;

import java.io.Serializable;
import java.util.List;

public class Note implements Serializable {
    private String idNote;
    private String title;
    private String content;
    private int level;

    private List<ImageNote> imgNotes;

    public Note(String idNote,String title, String content, int level, List<ImageNote> imgNotes) {
        this.title = title;
        this.content = content;
        this.imgNotes = imgNotes;
        this.level = level;
        this.idNote = idNote;
    }

    public Note(String idNote,String title, String content, int level) {
        this.title = title;
        this.content = content;
        this.level = level;
        this.idNote = idNote;
    }

    public String getIdNote() {
        return idNote;
    }

    public void setIdNote(String idNote) {
        this.idNote = idNote;
    }

    public Note(String idNote, String title, String content) {
        this.title = title;
        this.content = content;
        this.idNote = idNote;
    }

    public Note() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<ImageNote> getImgNotes() {
        return imgNotes;
    }

    public void setImgNotes(List<ImageNote> imgNotes) {
        this.imgNotes = imgNotes;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
