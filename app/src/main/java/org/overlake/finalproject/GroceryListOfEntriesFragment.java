package org.overlake.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.overlake.finalproject.databinding.FragmentGroceryListOfEntriesBinding;

import java.util.List;

public class GroceryListOfEntriesFragment extends Fragment implements GroceryListOfEntriesAdapter.WordListActivity {
    public static final String LIST_NAME = "list name";
    public static final String LIST = "list";
    private LiveData<List<GroceryEntry>> liveWordList;
    private GroceryEditEntryDialog dialog;
    private GroceryViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FragmentGroceryListOfEntriesBinding binding = FragmentGroceryListOfEntriesBinding.inflate(getLayoutInflater());

        Bundle args = getArguments();
        String listName = args.getString(LIST_NAME);

        viewModel = new ViewModelProvider(this).get(GroceryViewModel.class);
        liveWordList = viewModel.getList(listName);

        dialog = new GroceryEditEntryDialog(viewModel);
        dialog.setCalledFrom(LIST);
        dialog.setInList(listName);

        RecyclerView wordList = binding.recyclerView;
        wordList.setLayoutManager(new LinearLayoutManager(getContext()));
        wordList.setAdapter(new GroceryListOfEntriesAdapter(this));

        binding.add.setOnClickListener(v -> {
            dialog.show(getChildFragmentManager(), null);
        });

        binding.buttonBack.setOnClickListener(v -> {
            ((MainActivity) getActivity()).putMainContent(GroceryListOfListsFragment.class, null);
        });

        return binding.getRoot();

    }

    @Override
    public LiveData<List<GroceryEntry>> getWords() {
        return liveWordList;
    }

    @Override
    public GroceryEditEntryDialog getDialog() {
        return dialog;
    }

    @Override
    public FragmentManager getSupportFragmentManager() {
        return getChildFragmentManager();
    }

    @Override
    public GroceryViewModel getViewModel() {
        return viewModel;
    }

}
