package com.manpreetsingh.firetest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by msgil on 8/12/2017.
 */

public class AdapterForRecycler extends RecyclerView.Adapter<AdapterForRecycler.ViewHolder> {

    private List<ListItemRecycler> recyclerItems;
    private Context context;

    public AdapterForRecycler(List<ListItemRecycler> recyclerItems, Context context) {
        this.recyclerItems = recyclerItems;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_recycler,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        ListItemRecycler items  = recyclerItems.get(position);

        holder.textViewHead.setText(items.getHead());
        holder.textViewDesc.setText(items.getDesc());

    }

    @Override
    public int getItemCount() {
        return recyclerItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textViewHead;
        public TextView textViewDesc;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewHead = (TextView) itemView.findViewById(R.id.textViewHead);
            textViewDesc = (TextView) itemView.findViewById(R.id.textViewDesc);

        }
    }


}
