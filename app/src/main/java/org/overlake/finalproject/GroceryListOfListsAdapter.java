package org.overlake.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import org.overlake.finalproject.databinding.GroceryListOfListsItemBinding;

import java.util.ArrayList;
import java.util.List;

public class GroceryListOfListsAdapter extends RecyclerView.Adapter<GroceryListOfListsAdapter.ListViewHolder> {
    private final ListOfListsActivity activity;
    private final LayoutInflater inflater;
    private List<String> lists;

    public GroceryListOfListsAdapter(ListOfListsActivity activity) {

        this.activity = activity;
        inflater = activity.getLayoutInflater();

        LiveData<List<String>> liveAllLists = activity.getAllLists();
        lists = new ArrayList<>();
        liveAllLists.observe(activity, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> lists) {
                GroceryListOfListsAdapter.this.lists = lists;
                notifyDataSetChanged();
            }
        });

    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = GroceryListOfListsItemBinding.inflate(inflater).getRoot();
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        if (lists.get(position).equals("list1")) {
            holder.itemView.setVisibility(View.GONE);
            Button button = holder.itemView.findViewById(R.id.button_delete);
            button.setText("");
        } // delete this if clause later; it's here just for recording video making it pretty
        // will result in error in the future if we reset the database
        holder.setText(lists.get(position));
        holder.setListName(lists.get(position));
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        String listName;
        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            itemView.setOnClickListener(v -> {
                activity.navigateToListFragment(listName);
            });
            itemView.findViewById(R.id.button_delete).setOnClickListener(v -> {
                activity.getViewModel().deleteList(listName);
            });
        }
        public void setText(String word) {
            textView.setText(word);
        }
        public void setListName(String listName) {
            this.listName = listName;
        }
    }

    public interface ListOfListsActivity extends LifecycleOwner {
        LayoutInflater getLayoutInflater();
        LiveData<List<String>> getAllLists();
        GroceryViewModel getViewModel();
        void navigateToListFragment(String listName);
    }

}
