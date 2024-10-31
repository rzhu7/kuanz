package org.overlake.finalproject;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {GroceryEntry.class}, version = 1) // alternatively isntead of the WordListDatabaseRepository changes
// I THINK u can just change the version number here to one higher so that it's using a new version of the same database
// (effectively a new database) -- im not sure about this tho im just guessing
public abstract class GroceryDatabase extends RoomDatabase {
    public abstract GroceryDao wordDao();

}
