package com.evgeny.emailclient;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.mail.Message;

/**
 * Created by Evgeny on 06.12.2016.
 */

public class MailAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Message[] data;

    public MailAdapter(Message[] data) {
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return MyViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {

        });
        holder.bindTo(data[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }
}
