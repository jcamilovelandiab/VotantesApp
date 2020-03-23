package com.example.votoapp.ui.creditos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.votoapp.R;

public class CreditosFragment extends Fragment {

    private CreditosViewModel creditosViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        creditosViewModel =
                ViewModelProviders.of(this).get(CreditosViewModel.class);
        View root = inflater.inflate(R.layout.fragment_creditos, container, false);
        return root;
    }
}