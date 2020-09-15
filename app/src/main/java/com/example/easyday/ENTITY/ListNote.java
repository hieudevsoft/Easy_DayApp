package com.example.easyday.ENTITY;

import java.util.List;

public class ListNote {
    private List<Note> listNote;

    public ListNote(List<Note> listNote) {
        this.listNote = listNote;
    }

    public ListNote() {
    }

    public List<Note> getListNote() {
        return listNote;
    }

    public void setListNote(List<Note> listNote) {
        this.listNote = listNote;
    }
}
