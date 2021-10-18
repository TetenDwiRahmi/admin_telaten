package com.mobile.admintelaten.Activity;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Adapter.PembayaranAdapter;
import com.mobile.admintelaten.Interface.InterfacePembayaran;
import com.mobile.admintelaten.Model.Pembayaran;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityPembayaranBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PembayaranActivity extends AppCompatActivity implements InterfacePembayaran {
    private ActivityPembayaranBinding binding;
    List<Pembayaran> pembayaranList = new ArrayList<>();
    Dialog dialog;
    EditText editNamaBank, editNamaPemilik, editNoRek;
    Button btnSubmit, btnCancel;
    RelativeLayout layout_koneksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPembayaranBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dialog = new Dialog(this);

        binding.recyclerBank.setHasFixedSize(true);
        binding.recyclerBank.setLayoutManager(new LinearLayoutManager(this));

        binding.btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insert_bank();
            }
        });

        layout_koneksi = findViewById(R.id.layout_koneksi);
        layout_koneksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_bank();
            }
        });

        get_bank();
        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                get_bank();
            }
        });

    }

    private void insert_bank() {
        dialog.setContentView(R.layout.popup_bank);
        editNamaPemilik = dialog.findViewById(R.id.editNamaPemilik);
        editNamaBank = dialog.findViewById(R.id.editNamaBank);
        editNoRek = dialog.findViewById(R.id.editNoRek);
        btnSubmit = dialog.findViewById(R.id.btnSubmit);
        btnCancel = dialog.findViewById(R.id.btnCancel);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndroidNetworking.post(Server.site + "set_pembayaran.php")
                        .addBodyParameter("kode", String.valueOf(4))
                        .addBodyParameter("nama_bank", editNamaBank.getText().toString())
                        .addBodyParameter("no_rek", editNoRek.getText().toString())
                        .addBodyParameter("atas_nama", editNamaPemilik.getText().toString())
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("kode").equals("1")) {
                                        dialog.cancel();
                                        Toast.makeText(PembayaranActivity.this, "Pembayaran Ditambahkan", Toast.LENGTH_SHORT).show();
                                        get_bank();
                                    } else {
                                        Toast.makeText(PembayaranActivity.this, "Pembayaran Gagal Ditambahkan", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("tag", "error insert pembayaran : " + anError);
                            }
                        });
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void get_bank() {
        layout_koneksi.setVisibility(View.GONE);
        binding.swipe.setRefreshing(true);
        AndroidNetworking.post(Server.site + "set_pembayaran.php")
                .addBodyParameter("kode", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            binding.LayoutPembayaran.setVisibility(View.VISIBLE);
                            pembayaranList.clear();
                            binding.swipe.setRefreshing(false);
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject p = null;
                                p = response.getJSONObject(i);
                                pembayaranList.add(new Pembayaran(
                                        p.getInt("id_bank"),
                                        p.getString("nama_bank"),
                                        p.getString("no_rek"),
                                        p.getString("atas_nama")
                                ));
                            }
                            adapter_bank();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
//                        Log.d("tag", "error pembayaran : " + anError);
                        layout_koneksi.setVisibility(View.VISIBLE);
                        binding.LayoutPembayaran.setVisibility(View.GONE);
                        binding.swipe.setRefreshing(false);
                    }
                });
    }

    @Override
    public void UpdatePembayaran() {
        AndroidNetworking.post(Server.site + "set_pembayaran.php")
                .addBodyParameter("kode", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            pembayaranList.clear();
                            binding.swipe.setRefreshing(false);
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject p = null;
                                p = response.getJSONObject(i);
                                pembayaranList.add(new Pembayaran(
                                        p.getInt("id_bank"),
                                        p.getString("nama_bank"),
                                        p.getString("no_rek"),
                                        p.getString("atas_nama")
                                ));
                            }
                            adapter_bank();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error pembayaran : " + anError);
                    }
                });
    }

    private void adapter_bank() {
        PembayaranAdapter adapter = new PembayaranAdapter(pembayaranList, this);
        binding.recyclerBank.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}