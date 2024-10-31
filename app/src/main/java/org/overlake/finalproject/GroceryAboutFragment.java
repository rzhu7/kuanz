package org.overlake.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.overlake.finalproject.databinding.FragmentGroceryAboutBinding;

public class GroceryAboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentGroceryAboutBinding binding = FragmentGroceryAboutBinding.inflate(inflater);
        return binding.getRoot();
    }
}
