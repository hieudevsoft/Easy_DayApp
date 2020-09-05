package com.example.easyday.CONTROL;

import com.example.easyday.ENTITY.Note;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class SendListNote extends ViewModel {

    MutableLiveData<List<Note>> listNote = new MutableLiveData<>();

    public MutableLiveData<List<Note>> getListNote() {
        return listNote;
    }
}
