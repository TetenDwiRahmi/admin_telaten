package com.mobile.admintelaten.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityDetailCustomerBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailCustomerActivity extends AppCompatActivity {
    private ActivityDetailCustomerBinding binding;
    int id_cus;
    String no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailCustomerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = new Intent(getIntent());
        id_cus = i.getIntExtra("id_customer", 0);
        Log.d("IDC", "IDcus : " + id_cus);

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailCustomerActivity.this, CustomerActivity.class);
                startActivity(i);
                finish();
            }
        });

        binding.btnHp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.line2.setVisibility(View.VISIBLE);
                binding.editHPCus.setVisibility(View.GONE);
                binding.btnHp.setVisibility(View.GONE);
            }
        });

        binding.btnAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.line3.setVisibility(View.VISIBLE);
                binding.line4.setVisibility(View.GONE);
            }
        });

        binding.btnSubmitAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAlamat();
            }
        });

        binding.btnCancelAlamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.line3.setVisibility(View.GONE);
                binding.line4.setVisibility(View.VISIBLE);
            }
        });

        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHP();
            }
        });

        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.line2.setVisibility(View.GONE);
                binding.editHPCus.setVisibility(View.VISIBLE);
                binding.btnHp.setVisibility(View.VISIBLE);
            }
        });
        getCustomer();
    }


    private void updateAlamat() {
        AndroidNetworking.post(Server.site + "update_customer.php")
                .addBodyParameter("id_customer", String.valueOf(id_cus))
                .addBodyParameter("kode", String.valueOf(1))
                .addBodyParameter("alamat", binding.alamat.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("1")){
                                Toast.makeText(DetailCustomerActivity.this, "Berhasil Mengubah Alamat", Toast.LENGTH_SHORT).show();
                                binding.line4.setVisibility(View.VISIBLE);
                                getCustomer();
                                binding.line3.setVisibility(View.GONE);
                            }else {
                                Toast.makeText(DetailCustomerActivity.this, "Gagal Mengubah Alamat", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error update: " + anError);
                    }
                });
    }


    private void updateHP() {
        AndroidNetworking.post(Server.site + "update_customer.php")
                .addBodyParameter("id_customer", String.valueOf(id_cus))
                .addBodyParameter("kode", String.valueOf(2))
                .addBodyParameter("nohp", binding.editHp.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("status").equals("1")){
                                Toast.makeText(DetailCustomerActivity.this, "Berhasil Mengubah No Hp", Toast.LENGTH_SHORT).show();
                                binding.editHPCus.setVisibility(View.VISIBLE);
                                binding.btnHp.setVisibility(View.VISIBLE);
                                getCustomer();
                                binding.line2.setVisibility(View.GONE);
                            }else {
                                Toast.makeText(DetailCustomerActivity.this, "Gagal Mengubah No HP", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error update: " + anError);
                    }
                });
    }


    private void getCustomer() {
        AndroidNetworking.post(Server.site + "get_customer.php")
                .addBodyParameter("id_customer", String.valueOf(id_cus))
                .addBodyParameter("kode", String.valueOf(2))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("kode").equals("1")){
                                JSONObject k = response.getJSONObject("data");
                                binding.editHp.setText(k.getString("nohp"));
                                binding.alamat.setText(k.getString("alamat"));
                                binding.txtNamaCus.setText(k.getString("username"));
                                binding.editNamaCus.setText(k.getString("username"));
                                binding.editEmailCus.setText(k.getString("email"));
                                binding.editHPCus.setText(k.getString("nohp"));
                                binding.editAlamat.setText(k.getString("alamat"));
                                Picasso.get().load(Server.site_foto_customer + k.getString("foto_customer")).into(binding.imgCus);

                                no = "62" + k.getString("nohp");
                                binding.imgHp.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String url_wa = "https://api.whatsapp.com/send?phone=" + no;
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(url_wa));
                                        startActivity(i);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error get customer di DCA : " +anError);
                    }
                });
    }
}