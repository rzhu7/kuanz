package org.overlake.finalproject;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import java.util.List;

public class GroceryDatabaseRepository implements GroceryRepository {
    public static final String DATABASE_NAME = "grocery_database";
    private final GroceryDao dao;
    public GroceryDatabaseRepository(Context context) {
        GroceryDatabase db = Room.databaseBuilder(context, GroceryDatabase.class, DATABASE_NAME)
                .allowMainThreadQueries()
                .build();
        dao = db.wordDao();
    }
    @Override
    public LiveData<List<String>> getAllLists() {
        // TODO: alphabetize
        return dao.getAllLists();
    }
    @Override
    public void createList(String listName) {
        GroceryEntry groceryEntry = new GroceryEntry(listName, GroceryDao.ENTRY_NAME_PLACE_HOLDER);
        addEntry(groceryEntry);
    }
    @Override
    public void deleteList(String listName) {
        dao.deleteEntry(listName);
    }
    @Override
    public LiveData<List<GroceryEntry>> getList(String listName) {
        // TODO: alphabetize
        return dao.getList(listName);
    }
    @Override
    public void addEntry(GroceryEntry groceryEntry) {
        dao.addEntry(groceryEntry);
    }
    @Override
    public void deleteEntry(GroceryEntry groceryEntry) {
        dao.deleteEntry(groceryEntry);
    }
}
