package com.example.votoapp.ui.listar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListarViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ListarViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Lista de pacientes");
    }

    public LiveData<String> getText() {
        return mText;
    }
}