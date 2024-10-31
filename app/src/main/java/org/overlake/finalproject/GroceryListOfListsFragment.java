package org.overlake.finalproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.overlake.finalproject.databinding.FragmentGroceryListOfListsBinding;

import java.util.ArrayList;
import java.util.List;

public class GroceryListOfListsFragment extends Fragment implements GroceryListOfListsAdapter.ListOfListsActivity {
    public static final String LIST_OF_LISTS = "list of lists";
    private GroceryViewModel viewModel;
    private GroceryEditEntryDialog dialog;
    private LiveData<List<String>> liveAllLists;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentGroceryListOfListsBinding binding = FragmentGroceryListOfListsBinding.inflate(getLayoutInflater());

        viewModel = new ViewModelProvider(this).get(GroceryViewModel.class);
        dialog = new GroceryEditEntryDialog(viewModel);
        dialog.setCalledFrom(LIST_OF_LISTS);

        liveAllLists = viewModel.getAllLists();

        RecyclerView wordList = binding.recyclerView;
        wordList.setLayoutManager(new LinearLayoutManager(getContext()));
        wordList.setAdapter(new GroceryListOfListsAdapter(this));

        binding.add.setOnClickListener(v -> {
            dialog.show(getChildFragmentManager(), null);
        });

        return binding.getRoot();

    }
    public GroceryViewModel getViewModel() {
        return viewModel;
    }
    @Override
    public LiveData<List<String>> getAllLists() {
        return liveAllLists;
    }
    @Override
    public void navigateToListFragment(String listName) {
        Bundle args = new Bundle();
        args.putString(GroceryListOfEntriesFragment.LIST_NAME, listName);
        ((MainActivity) getActivity()).putMainContent(GroceryListOfEntriesFragment.class, args);
    }

}
