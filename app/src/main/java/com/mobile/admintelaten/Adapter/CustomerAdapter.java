package com.mobile.admintelaten.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.admintelaten.Activity.DetailPesananCustomerActivity;
import com.mobile.admintelaten.Model.Customer;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewRecHolder> {
    private List<Customer> customers;
    private Context context;

    public CustomerAdapter(List<Customer> customers, Context context) {
        this.customers = customers;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomerAdapter.CustomerViewRecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_customer, null);
        return new CustomerAdapter.CustomerViewRecHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomerAdapter.CustomerViewRecHolder holder, int position) {
        final Customer customer = customers.get(position);
        holder.txtNama.setText(customer.getUsername());
        holder.txtHp.setText(customer.getNohp());
        Picasso.get().load(Server.site_foto_customer + customer.getFoto_customer()).into(holder.imageCustomer);
        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailPesananCustomerActivity.class);
                i.putExtra("id_customer", customer.getId_customer());
                Log.d("tag", "IDCUS : " + customer.getId_customer());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public class CustomerViewRecHolder extends RecyclerView.ViewHolder {
        TextView txtNama, txtHp;
        ImageView imageCustomer;
        RelativeLayout cardItem;

        public CustomerViewRecHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNama);
            txtHp = itemView.findViewById(R.id.txtHP);
            imageCustomer = itemView.findViewById(R.id.imageCustomer);
            cardItem = itemView.findViewById(R.id.cardItem);
        }
    }
}
