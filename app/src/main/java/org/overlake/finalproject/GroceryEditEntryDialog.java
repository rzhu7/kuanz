package org.overlake.finalproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class GroceryEditEntryDialog extends DialogFragment {

    private static final String TITLE_ADD_ENTRY = "Add Entry";
    private static final String TITLE_ADD_LIST = "Add List";
    private static final String BUTTON_ADD = "Add";
    private static final String BUTTON_CANCEL = "Cancel";
    private final GroceryUpdater updater;
    private String calledFrom;
    private String inList; // null if called from GroceryListOfLists, listName if from just listFragment

    public GroceryEditEntryDialog(GroceryUpdater updater) {
        this.updater = updater;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View view = getLayoutInflater().inflate(R.layout.fragment_grocery_edit_entry, null);
        EditText editText = view.findViewById(R.id.input);

        String title = null;
        if (calledFrom.equals(GroceryListOfListsFragment.LIST_OF_LISTS)) {
            title = TITLE_ADD_LIST;
        } else if (calledFrom.equals(GroceryListOfEntriesFragment.LIST)) {
            title = TITLE_ADD_ENTRY;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title)
                .setView(view)
                .setPositiveButton(BUTTON_ADD, (dialog, which) -> {
                    String userInput = editText.getText().toString();
                    if (calledFrom.equals(GroceryListOfListsFragment.LIST_OF_LISTS)) {
                        updater.createList(userInput);
                    } else if (calledFrom.equals(GroceryListOfEntriesFragment.LIST)) {
                        updater.addEntry(inList, userInput);
                    }
                })
                .setNeutralButton(BUTTON_CANCEL, null);

        return builder.create();
    }
    public void setCalledFrom(String calledFrom) {
        this.calledFrom = calledFrom;
    }
    public void setInList(String listName) {
        this.inList = listName;
    }

    public interface GroceryUpdater {
        void createList(String listName);
        void addEntry(String listName, String entryName);
        void deleteEntry(GroceryEntry entry);
    }

}
