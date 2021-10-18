package com.mobile.admintelaten.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    String id_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        Intent i = new Intent(getIntent());
        id_item = String.valueOf(i.getIntExtra("id_item", 0));
        Log.d("id_item", "code : " + id_item);

        data();

    }

    private void data() {
        binding.progress.setVisibility(View.VISIBLE);
        binding.cardDetail.setVisibility(View.GONE);
        AndroidNetworking.post(Server.site + "set_produk.php")
                .addBodyParameter("id_item", id_item)
                .addBodyParameter("kode", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            binding.progress.setVisibility(View.GONE);
                            binding.cardDetail.setVisibility(View.VISIBLE);
                            Log.d("sukses", "code : " + response);
                            if (response.getString("kode").equalsIgnoreCase("1")) {
                                JSONObject data = response.getJSONObject("data");
                                binding.txtNamaKategori.setText(data.getString("nama_kategori"));
                                String foto = data.getString("foto_item");
                                Picasso.get().load(Server.site_foto + foto).into(binding.image);
                                binding.txtNamaItem.setText(data.getString("nama_item"));
                                binding.txtNamaItem2.setText(data.getString("nama_item"));
                                binding.txtDeskripsi.setText(data.getString("deskripsi"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", "code : " + anError);
                        Toast.makeText(DetailActivity.this, "Gagal get Data", Toast.LENGTH_SHORT).show();
                    }
                });

        AndroidNetworking.post(Server.site + "get_variasi.php")
                .addBodyParameter("id_item", id_item)
                .addBodyParameter("kode", String.valueOf(3))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equalsIgnoreCase("1")) {
                                Locale localeId = new Locale("in", "ID");
                                final NumberFormat rupiah = NumberFormat.getCurrencyInstance(localeId);
                                int a = response.getInt("minimum");//harga Minimum
                                int b = response.getInt("maximum");
                                int c = response.getInt("minStock");
                                int d = response.getInt("maxStock");
                                double e = response.getDouble("minBeli");
                                double f = response.getDouble("maxBeli");
                                if (a != b || c != d) {
                                    binding.txtHargaItem.setText(rupiah.format(a) + " - " + rupiah.format(b));
                                    binding.txtStock.setText("Stock : " + c + " - " + d);
                                } else {
                                    binding.txtHargaItem.setText(rupiah.format(a));
                                    binding.txtStock.setText("Stock : " + c);
                                }
                                if(e == 0 && f == 0){
                                    binding.txtbeli.setVisibility(View.GONE);
                                    binding.txtminbeli.setVisibility(View.GONE);
                                }else{
                                    if(e == f){
                                        binding.txtminbeli.setText(e +  " produk");
                                    }else {
                                        binding.txtminbeli.setText(e + " - " + f +  " produk");
                                    }
                                    Log.d("tag", "min beli: " + e + " - " + f);
                                }

                            } else if (response.getString("kode").equalsIgnoreCase("2")) {
                                JSONObject data = response.getJSONObject("data");
                                Locale localeId = new Locale("in", "ID");
                                final NumberFormat rupiah = NumberFormat.getCurrencyInstance(localeId);
                                int hrg = Integer.parseInt(data.getString("harga_item"));
                                binding.txtHargaItem.setText(rupiah.format(hrg));
                                binding.txtStock.setText("Stock : " + data.getString("stock"));
                                if(data.getInt("min_pembelian") == 0){
                                    binding.txtbeli.setVisibility(View.GONE);
                                    binding.txtminbeli.setVisibility(View.GONE);
                                }else {
                                    binding.txtminbeli.setText(data.getInt("min_pembelian") + " produk");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error harga dan stock : " + anError);
                    }
                });
    }
}