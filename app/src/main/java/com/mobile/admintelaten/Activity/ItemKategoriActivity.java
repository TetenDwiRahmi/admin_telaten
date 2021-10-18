package com.mobile.admintelaten.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.mobile.admintelaten.Adapter.ItemAdapter;
import com.mobile.admintelaten.Model.Item;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityItemKategoriBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ItemKategoriActivity extends AppCompatActivity {
    private ActivityItemKategoriBinding binding;
    List<Item> itemList = new ArrayList<>();
    int id_kategori;
    String nm_kat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemKategoriBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        Intent i = new Intent(getIntent());
        id_kategori = i.getIntExtra("id_kategori", 0);
        nm_kat = i.getStringExtra("nama_kategori");

        binding.nmKat.setText(nm_kat);

        binding.recyclerKategori.setHasFixedSize(true);
        binding.recyclerKategori.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));
        
        get_item();

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(ItemKategoriActivity.this, KategoriActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void get_item() {
        AndroidNetworking.post(Server.site + "set_kategori.php")
                .addBodyParameter("id_kategori", String.valueOf(id_kategori))
                .addBodyParameter("kode", String.valueOf(4))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = null;
                                item = response.getJSONObject(i);
                                itemList.add(new Item(
                                        item.getInt("id_item"),
                                        item.getInt("id_kategori"),
                                        item.getString("nama_kategori"),
                                        item.getString("nama_item"),
                                        item.getInt("harga_item"),
                                        item.getInt("stock"),
                                        item.getString("foto_item")
                                ));
                            }
                            ItemAdapter adapter = new ItemAdapter(ItemKategoriActivity.this, itemList);
                            binding.recyclerKategori.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
//                        Log.d("eror", "error Item kategory : : " + anError);
                        Toast.makeText(ItemKategoriActivity.this, "Produk Tidak Ada", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}