package org.overlake.finalproject;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

/*
 *  GroceryViewModel is for both GroceryListOfListsFragment and GroceryListOfEntriesFragment
 *  There is no separate database for storing these two "lists" because they are simply columns
 */
public class GroceryViewModel extends AndroidViewModel implements GroceryEditEntryDialog.GroceryUpdater {

    GroceryRepository repository;

    public GroceryViewModel(Application application) {
        super(application);
        repository = new GroceryDatabaseRepository(application.getApplicationContext());
    }

    public LiveData<List<String>> getAllLists() {
        return repository.getAllLists();
    }
    @Override
    public void createList(String listName) {
        repository.createList(listName);
    }
    public void deleteList(String listName) {
        repository.deleteList(listName);
    }

    public LiveData<List<GroceryEntry>> getList(String listName) {
        return repository.getList(listName);
    }

    @Override
    public void addEntry(String listName, String entryName) {
        repository.addEntry(new GroceryEntry(listName, entryName));
    }

    @Override
    public void deleteEntry(GroceryEntry entry) {
        repository.deleteEntry(entry);
    }

}
