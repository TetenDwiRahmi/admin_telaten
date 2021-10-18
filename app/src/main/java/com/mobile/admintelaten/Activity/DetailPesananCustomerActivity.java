package com.mobile.admintelaten.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Adapter.PesananAdapter;
import com.mobile.admintelaten.Model.Pesanan;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityDetailPesananCustomerBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailPesananCustomerActivity extends AppCompatActivity {
    private ActivityDetailPesananCustomerBinding binding;
    List<Pesanan> pesananList;
    int id_cus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailPesananCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        Intent i = new Intent(getIntent());
        id_cus = i.getIntExtra("id_customer", 0);
        Log.d("IDC", "IDcus : " + id_cus);

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailPesananCustomerActivity.this, CustomerActivity.class);
                startActivity(i);
                finish();
            }
        });

        pesananList = new ArrayList<>();
        binding.recyclerPesanan.setHasFixedSize(true);
        binding.recyclerPesanan.setLayoutManager(new LinearLayoutManager(this));

        binding.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailPesananCustomerActivity.this, DetailCustomerActivity.class);
                i.putExtra("id_customer", id_cus);
                startActivity(i);
                finish();
            }
        });

        getCustomer();
        getPesanan();

        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPesanan();
            }
        });

    }

    private void getCustomer() {
        AndroidNetworking.post(Server.site + "get_customer.php")
                .addBodyParameter("kode", String.valueOf(2))
                .addBodyParameter("id_customer", String.valueOf(id_cus))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("kode").equals("1")){
                                JSONObject k = response.getJSONObject("data");
                                binding.txtNama.setText(k.getString("username"));
                                binding.txtNamaCus.setText(k.getString("username"));
                                binding.txtHP.setText(k.getString("nohp"));
                                Picasso.get().load(Server.site_foto_customer + k.getString("foto_customer")).into(binding.imageCustomer);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error DPCA : " + anError);
                    }
                });
    }

    private void getPesanan() {
        binding.swipe.setRefreshing(true);
        AndroidNetworking.post(Server.site + "adm_order.php")
                .addBodyParameter("kode", String.valueOf(6))
                .addBodyParameter("id_customer", String.valueOf(id_cus))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() >= 1) {
                            try {
                                binding.swipe.setRefreshing(false);
                                pesananList.clear();
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject d = response.getJSONObject(i);
                                    pesananList.add(new Pesanan(
                                            d.getString("id_order"),
                                            d.getInt("id_customer"),
                                            d.getString("tgl_order"),
                                            d.getString("status_order"),
                                            d.getString("nama_penerima"),
                                            d.getInt("total_bayar"),
                                            d.getInt("total_item"),
                                            d.getString("metode_pengiriman")
                                    ));
                                    binding.txtbanyak.setText(d.getString("banyak_order") + " Pesanan");
                                    Log.d("tag", "Total Item : " + d.getInt("total_item"));
                                }
                                PesananAdapter adapter = new PesananAdapter(DetailPesananCustomerActivity.this, pesananList);
                                binding.recyclerPesanan.setAdapter(adapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else{
                            binding.swipe.setRefreshing(false);
                            binding.imgNodata.setVisibility(View.VISIBLE);
                            binding.txtNodata.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error pesanan : " + anError);
                    }

                });
    }
}