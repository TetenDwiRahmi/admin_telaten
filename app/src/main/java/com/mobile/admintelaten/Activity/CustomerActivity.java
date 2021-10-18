package com.mobile.admintelaten.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.mobile.admintelaten.Adapter.CustomerAdapter;
import com.mobile.admintelaten.Model.Customer;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityCustomerBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CustomerActivity extends AppCompatActivity {
    private ActivityCustomerBinding binding;
    List<Customer> customerList;
    RelativeLayout layout_koneksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        customerList = new ArrayList<>();
        binding.recyclerCustomer.setHasFixedSize(true);
        binding.recyclerCustomer.setLayoutManager(new LinearLayoutManager(this));

        layout_koneksi = findViewById(R.id.layout_koneksi);
        layout_koneksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_customer();
            }
        });
        get_customer();
        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                get_customer();
            }
        });

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(CustomerActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void get_customer() {
        layout_koneksi.setVisibility(View.GONE);
        binding.swipe.setRefreshing(true);
        AndroidNetworking.post(Server.site + "get_customer.php")
                .addBodyParameter("kode", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() >= 1) {
                            try {
                                binding.swipe.setRefreshing(false);
                                customerList.clear();
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject k = response.getJSONObject(i);
                                    customerList.add(new Customer(
                                            k.getInt("id_customer"),
                                            k.getString("username"),
                                            k.getString("email"),
                                            k.getString("nohp"),
                                            k.getString("foto_customer"),
                                            ""
                                    ));

                                    binding.layoutPelanggan.setVisibility(View.VISIBLE);
                                    CustomerAdapter adapter = new CustomerAdapter(customerList,CustomerActivity.this);
                                    binding.recyclerCustomer.setAdapter(adapter);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
//                        Log.d("tag", "error :" + anError);
                        layout_koneksi.setVisibility(View.VISIBLE);
                        binding.swipe.setRefreshing(false);
                        binding.layoutPelanggan.setVisibility(View.GONE);
                    }
                });
    }

}