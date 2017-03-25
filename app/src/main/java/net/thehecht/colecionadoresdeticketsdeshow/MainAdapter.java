package net.thehecht.colecionadoresdeticketsdeshow;


import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    SortedList<DataSnapshot> dataset = new SortedList<DataSnapshot>(DataSnapshot.class, new SortedList.Callback<DataSnapshot>() {
        @Override
        public int compare(DataSnapshot data1, DataSnapshot data2) {
            return (int)(data2.child("createdAt").getValue(Long.class) - data1.child("createdAt").getValue(Long.class));
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public boolean areContentsTheSame(DataSnapshot oldItem, DataSnapshot newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(DataSnapshot item1, DataSnapshot item2) {
            return item1.getKey().equals(item2.getKey());
        }

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }
    });

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.comment);
        }
    }

    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d("Recycler", "onCreateViewHolder");
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main, parent, false));
    }

    @Override
    public void onBindViewHolder(MainAdapter.ViewHolder holder, int position) {
        holder.textView.setText(dataset.get(position).child("comment").getValue(String.class));
        Log.d("Recycler", "onBindViewHolder");
    }

    public void addItem(DataSnapshot data) {
        dataset.add(data);
        notifyDataSetChanged();
    }

    public void removeItem(DataSnapshot data) {
        dataset.remove(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
