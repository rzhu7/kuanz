package org.overlake.finalproject;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface GroceryRepository {
    LiveData<List<String>> getAllLists();
    void createList(String listName);
    void deleteList(String listName);
    LiveData<List<GroceryEntry>> getList(String listName);
    void addEntry(GroceryEntry groceryEntry);
    void deleteEntry(GroceryEntry groceryEntry);

}
