package com.ssdiscusskiny.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ssdiscusskiny.R;
import com.ssdiscusskiny.data.ParentItem;

import java.util.List;

public class ParentAdapter extends RecyclerView.Adapter<ParentAdapter.ViewHolder>{

    private final List<ParentItem> list;
    private final Context mContext;

    private AppCompatActivity activity;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public ParentAdapter(Context mContext, AppCompatActivity activity, List<ParentItem> list){
        this.list = list;
        this.mContext = mContext;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.parent_item_layout, parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.mTitleTv.setText(list.get(position).getParentTitle());

        LinearLayoutManager l = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);

        // Since this is a nested layout, so
        // to define how many child items
        // should be prefetched when the
        // child RecyclerView is nested
        // inside the parent RecyclerView,
        // we use the following method
        l.setInitialPrefetchItemCount(list.size());

        ChildAdapter childAdapter = new ChildAdapter(mContext, activity, list.get(position).getChildList());

        holder.mChildRecyclerView.setLayoutManager(l);
        holder.mChildRecyclerView.setAdapter(childAdapter);
        holder.mChildRecyclerView.setRecycledViewPool(viewPool);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public TextView mTitleTv;
        public RecyclerView mChildRecyclerView;

        ViewHolder(View view){
            super(view);

            mTitleTv = view.findViewById(R.id.parent_item_title);
            mChildRecyclerView = view.findViewById(R.id.parent_recyclerView);
        }

    }
}
