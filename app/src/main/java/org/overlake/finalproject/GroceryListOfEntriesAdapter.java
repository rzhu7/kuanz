package org.overlake.finalproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import org.overlake.finalproject.databinding.GroceryListOfEntriesItemBinding;

import java.util.ArrayList;
import java.util.List;

public class GroceryListOfEntriesAdapter extends RecyclerView.Adapter<GroceryListOfEntriesAdapter.WordViewHolder> {

    private final LayoutInflater inflater;
    List<GroceryEntry> list;
    private final GroceryViewModel viewModel;
    private int count;
    public GroceryListOfEntriesAdapter(WordListActivity activity) {
        list = new ArrayList<>();
        inflater = activity.getLayoutInflater();
        LiveData<List<GroceryEntry>> words = activity.getWords();
        words.observe(activity, new Observer<List<GroceryEntry>>() {
            @Override
            public void onChanged(List<GroceryEntry> list) {
                GroceryListOfEntriesAdapter.this.list = list;
                notifyDataSetChanged();
            }
        });
        viewModel = activity.getViewModel();
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = GroceryListOfEntriesItemBinding.inflate(inflater).getRoot();
        view.setMinimumWidth(parent.getWidth());
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        if (list.get(position).getEntryName().equals(GroceryDao.ENTRY_NAME_PLACE_HOLDER)) {
            holder.itemView.setVisibility(View.GONE);
            Button button = holder.itemView.findViewById(R.id.button_delete);
            button.setText("");
        }
        holder.setText(list.get(position).getEntryName());
        holder.setPosition(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class WordViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        int position;
        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.textView);
            itemView.findViewById(R.id.button_delete).setOnClickListener(v -> {
                GroceryEntry word = list.get(position);
                viewModel.deleteEntry(word);
            });
        }
        public void setText(String word) {
            text.setText(word);
        }
        public void setPosition(int position) {this.position = position;}
    }

    public interface WordListActivity extends LifecycleOwner {
        LiveData<List<GroceryEntry>> getWords();
        GroceryEditEntryDialog getDialog();
        LayoutInflater getLayoutInflater();
        FragmentManager getSupportFragmentManager();
        GroceryViewModel getViewModel();
    }

}
