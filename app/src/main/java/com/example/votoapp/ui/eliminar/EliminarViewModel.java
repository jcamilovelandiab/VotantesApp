package com.example.votoapp.ui.eliminar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EliminarViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EliminarViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is eliminar fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}