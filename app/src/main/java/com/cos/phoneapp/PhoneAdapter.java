package com.cos.phoneapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PhoneAdapter extends RecyclerView.Adapter<PhoneAdapter.MyViewHolder> {

    private static final String TAG = "ContactAdapter";
    private List<Phone> phones;
    private MainActivity mActivity;

    public PhoneAdapter(MainActivity mainActivity) {
        this.mActivity = mainActivity;
        this.phones = mActivity.getContactList();
    }

    public void setItems(List<Phone> phones) {
        this.phones = phones;
        notifyDataSetChanged();
    }

    public void setItem(int position, Phone phone) {
        phones.get(position).setName(phone.getName());
        phones.get(position).setTel(phone.getTel());
        notifyDataSetChanged();
    }

    public void addItem(Phone phone) {
        this.phones.add(phone);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        this.phones.remove(position);
        notifyDataSetChanged();
    }

    public void removeAll() {
        this.phones = new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phone_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Phone phone = phones.get(position);
        holder.setItem(phone);
        holder.itemView.setOnClickListener(v -> {
            mActivity.updateContact(phone, position);
        });
    }

    @Override
    public int getItemCount() {
        return phones.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvTel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvName = itemView.findViewById(R.id.tv_name);
            this.tvTel = itemView.findViewById(R.id.tv_tel);
        }

        public void setItem(Phone phone) {
            tvName.setText(phone.getName());
            tvTel.setText(phone.getTel());
        }

    }
}