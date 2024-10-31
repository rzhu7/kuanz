package org.overlake.finalproject;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// This models a database
@Entity(tableName = "grocery_entry")
public class GroceryEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "listName")
    @NonNull
    private String listName;
    @ColumnInfo(name = "entryName")
    @NonNull
    private String entryName;

    public GroceryEntry(@NonNull String listName, @NonNull String entryName) {
        this.listName = listName;
        this.entryName = entryName;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    @NonNull
    public String getEntryName() {
        return entryName;
    }
    @NonNull
    public String getListName() {
        return listName;
    }
}
