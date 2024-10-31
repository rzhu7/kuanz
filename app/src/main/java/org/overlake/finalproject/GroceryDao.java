package org.overlake.finalproject;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GroceryDao {
    // used to create a placeholder entry when user creates a new list but hasn't added an actual entry
    String ENTRY_NAME_PLACE_HOLDER = "place_holder";
    @Query("SELECT * FROM grocery_entry")
    LiveData<List<GroceryEntry>> getWords();
    @Query("SELECT DISTINCT grocery_entry.listName FROM grocery_entry")
    LiveData<List<String>> getAllLists();
    @Query("SELECT * FROM grocery_entry WHERE grocery_entry.listName = :listName AND grocery_entry.entryName != ''")
    LiveData<List<GroceryEntry>> getList(String listName);
    @Query("DELETE FROM grocery_entry WHERE grocery_entry.listName = :listName")
    void deleteEntry(String listName);
    @Insert
    void addEntry(GroceryEntry groceryEntry);
    @Delete
    void deleteEntry(GroceryEntry groceryEntry);

}
