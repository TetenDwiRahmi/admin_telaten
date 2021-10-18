package com.mobile.admintelaten.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityOngkirBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class OngkirActivity extends AppCompatActivity {
    private ActivityOngkirBinding binding;
    Locale localeId;
    NumberFormat rupiah;
    Dialog dialog;
    TextView txtJarak;
    EditText jarak, tarif;
    Button btnUpdate, btnCancel;
    RelativeLayout layout_koneksi;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOngkirBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        alertDialog = new SpotsDialog.Builder().setContext(OngkirActivity.this).setMessage("Sedang Mengambil Data ...").setCancelable(false).build();

        dialog = new Dialog(this);

        localeId = new Locale("in", "ID");
        rupiah = NumberFormat.getCurrencyInstance(localeId);

        layout_koneksi = findViewById(R.id.layout_koneksi);
        layout_koneksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_ongkir();
            }
        });

        get_ongkir();
        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                get_ongkir();
            }
        });

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(OngkirActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void get_ongkir() {
        alertDialog.show();
        layout_koneksi.setVisibility(View.GONE);
        binding.swipe.setRefreshing(true);
        AndroidNetworking.post(Server.site + "set_jarak.php")
                .addBodyParameter("kode", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            binding.layoutOngkir.setVisibility(View.VISIBLE);
                            binding.layoutDeskripsi.setVisibility(View.VISIBLE);
                            if (response.getString("kode").equals("1")) {
                                binding.swipe.setRefreshing(false);
                                JSONObject data = response.getJSONObject("data");
                                int tarif = Integer.parseInt(data.getString("tarif"));
                                binding.tarif2.setText(rupiah.format(tarif));
                                binding.imageUpdate2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        update_jarak2();
                                    }
                                });
                            }
                            alertDialog.cancel();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
//                        Log.d("tag", "error ongkir 1 : " + anError);
                        alertDialog.cancel();
                        layout_koneksi.setVisibility(View.VISIBLE);
                        binding.swipe.setRefreshing(false);
                        binding.layoutOngkir.setVisibility(View.GONE);
                        binding.layoutDeskripsi.setVisibility(View.GONE);
                    }
                });

        AndroidNetworking.post(Server.site + "set_jarak.php")
                .addBodyParameter("kode", String.valueOf(2))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equals("1")) {
                                JSONObject data = response.getJSONObject("data");
                                int tarif = Integer.parseInt(data.getString("tarif"));
                                binding.tarif1.setText(rupiah.format(tarif));
                                binding.jarak1.setText("0 Km - " + data.getString("estimasi") + " Km");
                                binding.imageUpdate1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        update_jarak1();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
//                        Log.d("tag", "error ongkir 1 : " + anError);
                        layout_koneksi.setVisibility(View.VISIBLE);
                        binding.swipe.setRefreshing(false);
                        binding.layoutOngkir.setVisibility(View.GONE);
                        binding.layoutDeskripsi.setVisibility(View.GONE);
                    }
                });

        AndroidNetworking.post(Server.site + "set_jarak.php")
                .addBodyParameter("kode", String.valueOf(3))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equals("1")) {
                                JSONObject data = response.getJSONObject("data");
                                int tarif = Integer.parseInt(data.getString("tarif"));
                                binding.tarif3.setText(rupiah.format(tarif) + " / Km");
                                binding.jarak3.setText("> " + data.getString("estimasi") + " Km");
                                binding.imageUpdate3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        update_jarak3();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
//                        Log.d("tag", "error ongkir 1 : " + anError);
                        layout_koneksi.setVisibility(View.VISIBLE);
                        binding.swipe.setRefreshing(false);
                        binding.layoutOngkir.setVisibility(View.GONE);
                        binding.layoutDeskripsi.setVisibility(View.GONE);
                    }
                });
    }

    private void update_jarak3() {
        dialog.setContentView(R.layout.popup_update_jarak);
        jarak = dialog.findViewById(R.id.jarak);
        tarif = dialog.findViewById(R.id.tarif);
        btnUpdate = dialog.findViewById(R.id.btnUpdate);
        btnCancel = dialog.findViewById(R.id.btnCancel);

        AndroidNetworking.post(Server.site + "set_jarak.php")
                .addBodyParameter("kode", String.valueOf(3))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equals("1")) {
//                                dialog.cancel();
                                JSONObject data = response.getJSONObject("data");
                                tarif.setText(data.getString("tarif"));
                                jarak.setText(data.getString("estimasi"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error update ongkir 1 : " + anError);
                    }
                });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jarak.getText().toString().isEmpty() || tarif.getText().toString().isEmpty()) {
                    jarak.setError("Harus diisi");
                    tarif.setError("Harus diisi");
                } else {
                    AndroidNetworking.post(Server.site + "set_jarak.php")
                            .addBodyParameter("kode", String.valueOf(6))
                            .addBodyParameter("tarif", tarif.getText().toString())
                            .addBodyParameter("estimasi", jarak.getText().toString())
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("kode").equals("1")) {
                                            Toast.makeText(OngkirActivity.this, "Tarif diubah", Toast.LENGTH_SHORT).show();
                                            get_ongkir();
                                            dialog.cancel();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.d("tag", "error update ongkir 6 : " + anError);
                                }
                            });
                }
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

    private void update_jarak2() {
        dialog.setContentView(R.layout.popup_update_jarak);
        jarak = dialog.findViewById(R.id.jarak);
        txtJarak = dialog.findViewById(R.id.txtJarak);
        tarif = dialog.findViewById(R.id.tarif);
        btnUpdate = dialog.findViewById(R.id.btnUpdate);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        jarak.setVisibility(View.GONE);
        txtJarak.setVisibility(View.GONE);

        AndroidNetworking.post(Server.site + "set_jarak.php")
                .addBodyParameter("kode", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equals("1")) {
//                                dialog.cancel();
                                JSONObject data = response.getJSONObject("data");
                                tarif.setText(data.getString("tarif"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error update ongkir 1 : " + anError);
                    }
                });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tarif.getText().toString().isEmpty()) {
                    tarif.setError("Harus diisi");
                } else {
                    AndroidNetworking.post(Server.site + "set_jarak.php")
                            .addBodyParameter("kode", String.valueOf(4))
                            .addBodyParameter("tarif", tarif.getText().toString())
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("kode").equals("1")) {
                                            Toast.makeText(OngkirActivity.this, "Tarif diubah", Toast.LENGTH_SHORT).show();
                                            get_ongkir();
                                            dialog.cancel();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.d("tag", "error update ongkir 4 : " + anError);
                                }
                            });
                }
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

    private void update_jarak1() {
        dialog.setContentView(R.layout.popup_update_jarak);
        jarak = dialog.findViewById(R.id.jarak);
        tarif = dialog.findViewById(R.id.tarif);
        btnUpdate = dialog.findViewById(R.id.btnUpdate);
        btnCancel = dialog.findViewById(R.id.btnCancel);

        AndroidNetworking.post(Server.site + "set_jarak.php")
                .addBodyParameter("kode", String.valueOf(2))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equals("1")) {
//                                dialog.cancel();
                                JSONObject data = response.getJSONObject("data");
                                tarif.setText(data.getString("tarif"));
                                jarak.setText(data.getString("estimasi"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error update ongkir 1 : " + anError);
                    }
                });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jarak.getText().toString().isEmpty() || tarif.getText().toString().isEmpty()) {
                    jarak.setError("Harus diisi");
                    tarif.setError("Harus diisi");
                } else {
                    AndroidNetworking.post(Server.site + "set_jarak.php")
                            .addBodyParameter("kode", String.valueOf(5))
                            .addBodyParameter("tarif", tarif.getText().toString())
                            .addBodyParameter("estimasi", jarak.getText().toString())
                            .setPriority(Priority.MEDIUM)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("kode").equals("1")) {
                                            Toast.makeText(OngkirActivity.this, "Tarif diubah", Toast.LENGTH_SHORT).show();
                                            get_ongkir();
                                            dialog.cancel();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(ANError anError) {
                                    Log.d("tag", "error update ongkir 5 : " + anError);
                                }
                            });
                }
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
}