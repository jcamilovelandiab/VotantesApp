package com.example.votoapp.ui.editar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class EditarViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public EditarViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is editar fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}