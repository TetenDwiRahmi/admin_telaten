package com.mobile.admintelaten.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Adapter.ProdukKategoriAdapter;
import com.mobile.admintelaten.Interface.InterfaceKategoriVariasi;
import com.mobile.admintelaten.Model.Kategori;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityInsertBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class InsertActivity extends AppCompatActivity implements InterfaceKategoriVariasi {
    private ActivityInsertBinding binding;
    List<Kategori> kategoriList;
    int id_kat;
    android.app.AlertDialog alertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        alertDialog = new SpotsDialog.Builder().setContext(InsertActivity.this).setMessage("Sedang Menyimpan Data ...").setCancelable(false).build();

        kategoriList = new ArrayList<>();

        binding.recyclerKategori.setHasFixedSize(true);
        binding.recyclerKategori.setLayoutManager(new LinearLayoutManager(this));

        binding.btnLanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertProduk();
            }
        });

        getKategori();

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(InsertActivity.this, MainActivity.class);
        startActivity(i);
    }

    private void getKategori() {
        AndroidNetworking.post(Server.site + "set_kategori.php")
                .addBodyParameter("kode", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject k = null;
                                    k = response.getJSONObject(i);
                                    kategoriList.add(new Kategori(
                                            k.getInt("id_kategori"),
                                            k.getString("nama_kategori")));
                                }
                                adapterKategori();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error get Kategori : " + anError);
                    }
                });
    }

    private void adapterKategori() {
        ProdukKategoriAdapter adapter = new ProdukKategoriAdapter(kategoriList, this);
        binding.recyclerKategori.setAdapter(adapter);
    }

    @Override
    public void KategoriTerpilih(int id_kategori) {
        id_kat = id_kategori;
        AndroidNetworking.post(Server.site + "set_kategori.php")
                .addBodyParameter("kode", String.valueOf(2))
                .addBodyParameter("id_kategori", String.valueOf(id_kategori))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("kode").equalsIgnoreCase("1")){
                                JSONObject data = response.getJSONObject("data");
                                binding.txtTerpilih.setText("Kategori terpilih : " + data.getString("nama_kategori"));
                            }else{
                                Log.d("tag", "kategori terpilih : " + response.getString("pesan"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error kategori terpilih : " + anError);
                    }
                });
    }

    private void insertProduk() {
        if (binding.editNamaItem.getText().toString().length() == 0) {
            binding.editNamaItem.setError("Wajib diisi");
        }else if(id_kat == 0){
            new AlertDialog.Builder(InsertActivity.this)
                    .setTitle("Perhatian")
                    .setMessage("Anda belum memilih kategori !")
                    .setCancelable(false)
                    .setNegativeButton("Oke", null)
                    .show();

        } else {
            alertDialog.show();
            AndroidNetworking.post(Server.site + "insert_item.php")
                    .addBodyParameter("id_kategori", String.valueOf(id_kat))
                    .addBodyParameter("nama_item", binding.editNamaItem.getText().toString())
                    .addBodyParameter("kode", String.valueOf(1))
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("kode").equalsIgnoreCase("1")) {
                                    alertDialog.cancel();
                                    new androidx.appcompat.app.AlertDialog.Builder(InsertActivity.this)
                                            .setTitle("Perhatian")
                                            .setMessage("Produk sudah terdaftar !!")
                                            .setCancelable(false)
                                            .setNegativeButton("Oke", null)
                                            .show();
                                }else{
                                    Intent i = new Intent(InsertActivity.this, InsertProdukActivity.class);
                                    i.putExtra("id_item", response.getString("id_item"));
                                    i.putExtra("nama_item", binding.editNamaItem.getText().toString());
                                    Log.d("tag", "ID ITEM Terakhir : " + response.getString("id_item"));
                                    startActivity(i);
                                    alertDialog.cancel();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            alertDialog.cancel();
                            Log.d("tag", "error insert produk : " + anError);
                        }
                    });
        }
    }
}