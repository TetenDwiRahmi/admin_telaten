package com.mobile.admintelaten.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Adapter.KategoriAdapter;
import com.mobile.admintelaten.Interface.InterfaceKategoriUpdate;
import com.mobile.admintelaten.Model.Kategori;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityKategoriBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KategoriActivity extends AppCompatActivity implements InterfaceKategoriUpdate {
    private ActivityKategoriBinding binding;
    List<Kategori> kategoriList = new ArrayList<>();
    AlertDialog.Builder builder;
    View dialogView;
    EditText nama_kategori;
    RelativeLayout layout_koneksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityKategoriBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        binding.recyclerKategori.setHasFixedSize(true);
        binding.recyclerKategori.setLayoutManager(new LinearLayoutManager(this));

        layout_koneksi = findViewById(R.id.layout_koneksi);
        layout_koneksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getKategori();
            }
        });

        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getKategori();
            }
        });

        getKategori();

        binding.searchKategori.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        binding.btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popup_insert();
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
        Intent i = new Intent(KategoriActivity.this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void popup_insert() {
        builder = new AlertDialog.Builder(KategoriActivity.this);
        dialogView = LayoutInflater.from(KategoriActivity.this).inflate(R.layout.popup_add_kategori, null);
        nama_kategori = dialogView.findViewById(R.id.editNamaKategori);

        builder.setCancelable(true);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Tambah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(nama_kategori.getText().toString().length() == 0){
                    nama_kategori.setError("Wajib diisi");
                }else{
                    insert_kategori();
                }
            }
        });
        builder.setView(dialogView);
        builder.setCancelable(true);
        builder.show();
    }

    private void insert_kategori() {
        AndroidNetworking.post(Server.site + "set_kategori.php")
                .addBodyParameter("nama_kategori", nama_kategori.getText().toString())
                .addBodyParameter("kode", String.valueOf(3))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equalsIgnoreCase("1")) {
                                new androidx.appcompat.app.AlertDialog.Builder(KategoriActivity.this)
                                        .setTitle("Perhatian")
                                        .setMessage("Kategori produk sudah terdaftar !!")
                                        .setCancelable(false)
                                        .setNegativeButton("Oke", null)
                                        .show();
                            } else {
                                getKategori();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error insert kategori: " + anError);
                        Toast.makeText(KategoriActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void filter(String text) {
        ArrayList<Kategori> filteredList = new ArrayList<>();
        for (Kategori kategori : kategoriList) {
            if (kategori.getNama_kategori().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(kategori);
            }
        }
        KategoriAdapter adapter = new KategoriAdapter(kategoriList, KategoriActivity.this);
        binding.recyclerKategori.setAdapter(adapter);
        adapter.filterList(filteredList);

    }

    private void getKategori() {
        layout_koneksi.setVisibility(View.GONE);
        binding.swipe.setRefreshing(true);
        AndroidNetworking.post(Server.site + "set_kategori.php")
                .addBodyParameter("kode", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            binding.swipe.setRefreshing(false);
                            binding.layoutKategori.setVisibility(View.VISIBLE);
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject k = null;
                                k = response.getJSONObject(i);
                                kategoriList.add(new Kategori(
                                        k.getInt("id_kategori"),
                                        k.getString("nama_kategori")));
                            }
                            adapter_kat();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
//                        Log.d("tag", "error get Kategori activity : " + anError);
                        layout_koneksi.setVisibility(View.VISIBLE);
                        binding.layoutKategori.setVisibility(View.GONE);
                        binding.swipe.setRefreshing(false);
                    }
                });
    }

    private void adapter_kat() {
        KategoriAdapter adapter = new KategoriAdapter(kategoriList, this);
        binding.recyclerKategori.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void UpdateKategori() {
        AndroidNetworking.post(Server.site + "set_kategori.php")
                .addBodyParameter("kode", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            kategoriList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject k = null;
                                k = response.getJSONObject(i);
                                kategoriList.add(new Kategori(
                                        k.getInt("id_kategori"),
                                        k.getString("nama_kategori")));
                            }
                            adapter_kat();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error get Kategori activity : " + anError);
                    }
                });
    }
}