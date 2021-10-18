package com.mobile.admintelaten.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Adapter.BarangDipesanAdapter;
import com.mobile.admintelaten.Model.BarangDipesan;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityDetailOrderBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DetailOrderActivity extends AppCompatActivity {
    private ActivityDetailOrderBinding binding;
    String id_order, id_customer;
    List<BarangDipesan> barangDipesanList;
    AlertDialog.Builder builder;
    View dialogView;
    Dialog dialog;
    ImageView img_bukti;
    TextView txtbukti;
    EditText editKet;
    Button btnAjukan, btnCancel;
    String no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        dialog = new Dialog(this);

        Intent i = new Intent(getIntent());
        id_order = i.getStringExtra("id_order");
        id_customer = String.valueOf(i.getIntExtra("id_customer", 0));
        Log.d("id_order", "idorder : " + id_order + " id_cus : " + id_customer);

        barangDipesanList = new ArrayList<>();
        binding.recyclerDO.setHasFixedSize(true);
        binding.recyclerDO.setLayoutManager(new LinearLayoutManager(this));

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DetailOrderActivity.this, PesananActivity.class);
                startActivity(i);
            }
        });

        binding.btnDiterima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailOrderActivity.this)
                        .setTitle("Konfirmasi Penerimaan Barang")
                        .setMessage("Pastikan pelanggan sudah menerima pesanannya.")
                        .setCancelable(false)
                        .setPositiveButton("Ya, barang sudah diterima", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                barang_diterima();
                            }
                        })
                        .setNegativeButton("Belum", null)
                        .show();
            }
        });


        getItemPesanan();
        getStatusdanMetode();
        getAlamat();

    }

    private void getBukti() {
        AndroidNetworking.post(Server.site + "set_order.php")
                .addBodyParameter("id_order", id_order)
                .addBodyParameter("kode", String.valueOf(8))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equalsIgnoreCase("1")) {
                                if (response.getString("status").equalsIgnoreCase("1")) {
                                    binding.TandaiLunas.setVisibility(View.GONE);
                                    binding.SudahLunas.setVisibility(View.VISIBLE);
                                } else {
                                    binding.TandaiLunas.setVisibility(View.VISIBLE);
                                    binding.SudahLunas.setVisibility(View.GONE);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error status lunas 1 : " + anError);
                    }
                });

        AndroidNetworking.post(Server.site + "set_order.php")
                .addBodyParameter("id_order", id_order)
                .addBodyParameter("kode", String.valueOf(8))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equals("1")) {
                                final String foto = response.getString("bukti");
                                Log.d("tag", "Bukti : " + foto);
                                if (foto.equals("no_image.jpg")) {
                                    binding.txtBukti.setVisibility(View.GONE);
                                    binding.imgBukti.setVisibility(View.GONE);
                                } else {
                                    binding.txtBukti.setVisibility(View.VISIBLE);
                                    binding.imgBukti.setVisibility(View.VISIBLE);
                                    Picasso.get().load(Server.site_bukti_tf + foto).into(binding.imgBukti);
                                    binding.imgBukti.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent i = new Intent(DetailOrderActivity.this, BuktiActivity.class);
                                            i.putExtra("id_order", id_order);
                                            startActivity(i);
                                            finish();
                                        }
                                    });
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error bukti : " + anError);
                    }
                });
    }

    private void lunas() {
        AndroidNetworking.post(Server.site + "set_order.php")
                .addBodyParameter("id_order", id_order)
                .addBodyParameter("kode", String.valueOf(10))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equalsIgnoreCase("1")) {
                                binding.TandaiLunas.setVisibility(View.GONE);
                                binding.SudahLunas.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error status lunas 10: " + anError);
                    }
                });
    }


    private void barang_diterima() {
        AndroidNetworking.post(Server.site + "set_order.php")
                .addBodyParameter("id_order", id_order)
                .addBodyParameter("kode", String.valueOf(3))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equalsIgnoreCase("1")) {
                                Toast.makeText(DetailOrderActivity.this, "Pesanan Diterima", Toast.LENGTH_SHORT).show();
//                                Intent i = new Intent(DetailOrderActivity.this, OrderActivity.class);
//                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(DetailOrderActivity.this, "Pesanan Diterima", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error barang diterima : " + anError);
                    }
                });
    }

    private void batal() {
        binding.btnBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.popup_batal);
                editKet = dialog.findViewById(R.id.editKet);
                btnAjukan = dialog.findViewById(R.id.btnAjukan);
                btnCancel = dialog.findViewById(R.id.btnCancel);

                btnAjukan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editKet.getText().toString().length() == 0) {
                            editKet.setError("Alasan dibutuhkan");
                        } else {
                            AndroidNetworking.post(Server.site + "set_order.php")
                                    .addBodyParameter("id_order", id_order)
                                    .addBodyParameter("kode", String.valueOf(2))
                                    .addBodyParameter("keterangan", editKet.getText().toString())
                                    .setPriority(Priority.MEDIUM)
                                    .build()
                                    .getAsJSONObject(new JSONObjectRequestListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if (response.getString("kode").equalsIgnoreCase("1")) {
                                                    dialog.cancel();
                                                    Toast.makeText(DetailOrderActivity.this, "Pesanan Dibatalkan", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                } else {
                                                    Toast.makeText(DetailOrderActivity.this, "Pesanan Gagal Dibatalkan", Toast.LENGTH_SHORT).show();
                                                }


                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                            Log.d("tag", "error alasan : " + anError);
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

//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }

    private void getAlamat() {
        binding.progress.setVisibility(View.VISIBLE);
        AndroidNetworking.post(Server.site + "get_customer.php")
                .addBodyParameter("id_customer", id_customer)
                .addBodyParameter("id_order", id_order)
                .addBodyParameter("kode", String.valueOf(3))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equalsIgnoreCase("1")) {
                                JSONObject data = response.getJSONObject("data");
                                binding.progress.setVisibility(View.GONE);
                                Log.d("data ", " code :" + response);
                                binding.NamaPengirimDO.setText(data.getString("nama_penerima"));
                                binding.NoHPPengirimDO.setText(" | " + data.getString("nohp_penerima"));

                                String pembayaran = data.getString("metode_pembayaran");
                                if (pembayaran.equals("Bayar nanti ke penjual")) {
                                    binding.AlamatPengirimDO.setVisibility(View.GONE);
                                    binding.KodePosDO.setVisibility(View.GONE);
                                } else {
                                    binding.AlamatPengirimDO.setVisibility(View.VISIBLE);
                                    binding.KodePosDO.setVisibility(View.VISIBLE);
                                    binding.AlamatPengirimDO.setText(data.getString("alamat_penerima"));
                                    binding.KodePosDO.setText(data.getString("kodepos"));
                                }

                                int a, b, c;
                                a = Integer.parseInt(data.getString("total_harga"));
                                b = Integer.parseInt(data.getString("ongkir"));
                                c = Integer.parseInt(data.getString("total_bayar"));
                                Locale localeId = new Locale("in", "ID");
                                final NumberFormat rupiah = NumberFormat.getCurrencyInstance(localeId);

                                binding.HargaSubTotalDO.setText(rupiah.format(a));
                                binding.SubTotalPengirimanDO.setText(rupiah.format(b));
                                binding.TotalPembayaranDO.setText(rupiah.format(c));

                                no = data.getString("nohp_penerima");
                                binding.btnHub.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String url_wa = "https://api.whatsapp.com/send?phone=" + "+62" + no;
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
                        Log.d("tag", "error get Alamat : " + anError);
                    }
                });
    }

    private void getStatusdanMetode() {
        AndroidNetworking.post(Server.site + "set_order.php")
                .addBodyParameter("id_order", id_order)
                .addBodyParameter("kode", String.valueOf(6))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject s = response.getJSONObject(i);
                                binding.MetodePembayaran.setText(s.getString("metode"));
                                binding.statusPembayaran.setText(s.getString("status"));
                                binding.NoPesanan.setText(s.getString("id_order"));
                                binding.TglOrder.setText(s.getString("tgl_order"));
                                binding.JamOrder.setText(s.getString("jam_order"));

                                String bayar = s.getString("metode");

                                Log.d("tag", "Tanggal : " + s.getString("tgl_order"));

                                String status = s.getString("status");
                                if (status.equals("Pembayaran Tertunda") || status.equals("Tunggu Konfirmasi")) {
                                    binding.btnBatal.setVisibility(View.VISIBLE);
                                    binding.btnHub.setVisibility(View.VISIBLE);
                                    batal();
                                    getBukti();
                                    binding.TandaiLunas.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new AlertDialog.Builder(DetailOrderActivity.this)
                                                    .setTitle("Perhatian")
                                                    .setMessage("Pastikan pelanggan sudah membayar pesanan.")
                                                    .setCancelable(false)
                                                    .setPositiveButton("Tandai Lunas", new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            updateBB();
                                                            lunas();
                                                        }
                                                    })
                                                    .setNegativeButton("Batal", null)
                                                    .show();
                                        }
                                    });
                                } else if (status.equals("Segera Dikirim")) {
                                    getBukti();
                                    if (bayar.equals("Bayar nanti ke penjual")) {
                                        binding.statusPembayaran.setText("Siap Diambil");
                                        binding.btnBatal.setVisibility(View.VISIBLE);
                                        binding.btnHub.setVisibility(View.VISIBLE);
                                        binding.btnDiterima.setVisibility(View.VISIBLE);
                                        batal();
                                        tandai_lunas();
                                    } else {
                                        binding.statusPembayaran.setText("Sedang mengirim paket");
                                        binding.btnBatal.setVisibility(View.VISIBLE);
                                        binding.btnHub.setVisibility(View.VISIBLE);
                                        binding.btnDiterima.setVisibility(View.VISIBLE);
                                        batal();
                                        tandai_lunas();
                                    }

                                } else if (status.equals("Dikemas")) {
                                    getBukti();
                                    tandai_lunas();
                                    if (bayar.equals("Bayar nanti ke penjual")) {
                                        binding.btnBatal.setVisibility(View.VISIBLE);
                                        binding.btnDiambil.setVisibility(View.VISIBLE);
                                        batal();
                                        binding.btnDiambil.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                binding.btnDiambil.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        new AlertDialog.Builder(DetailOrderActivity.this)
                                                                .setTitle("Tandai pesanan siap diambil pelanggan")
                                                                .setMessage("Pastikan pesanan sudah selesai dikemas.")
                                                                .setCancelable(false)
                                                                .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int id) {
                                                                        kirim_pelanggan();
                                                                    }
                                                                })
                                                                .setNegativeButton("Batal", null)
                                                                .show();
                                                    }
                                                });
                                            }
                                        });
                                        binding.relative3.setVisibility(View.VISIBLE);
                                        binding.btnHub.setVisibility(View.VISIBLE);
                                    } else {
                                        tandai_lunas();
                                        binding.btnBatal.setVisibility(View.VISIBLE);
                                        binding.btnKirim.setVisibility(View.VISIBLE);
                                        batal();
                                        binding.btnKirim.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                new AlertDialog.Builder(DetailOrderActivity.this)
                                                        .setTitle("Pesanan ingin dikirim ke pelanggan?")
                                                        .setCancelable(false)
                                                        .setPositiveButton("IYA", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                kirim_pelanggan();
                                                            }
                                                        })
                                                        .setNegativeButton("Batal", null)
                                                        .show();
                                            }
                                        });
                                        binding.relative3.setVisibility(View.VISIBLE);
                                        binding.btnHub.setVisibility(View.VISIBLE);
                                    }

                                } else if (status.equals("Selesai")) {
                                    tandai_lunas();
                                    getBukti();
                                    if (bayar.equals("Bayar nanti ke penjual")) {
                                        binding.statusPembayaran.setText("Sudah Diambil");
                                        binding.relative2.setVisibility(View.VISIBLE);
                                        binding.btnHub.setVisibility(View.VISIBLE);
                                        getSelesai();
                                    } else {
                                        binding.statusPembayaran.setText("Selesai");
                                        binding.relative2.setVisibility(View.VISIBLE);
                                        binding.btnHub.setVisibility(View.VISIBLE);
                                        getSelesai();
                                    }

                                } else if (status.equals("Dibatalkan pembeli")) {
                                    tandai_lunas();
                                    getBukti();
                                    binding.relative1.setVisibility(View.VISIBLE);
                                    binding.btnHub.setVisibility(View.VISIBLE);
                                    getBatal();
                                } else if (status.equals("Dibatalkan penjual")) {
                                    tandai_lunas();
                                    getBukti();
                                    binding.relative1.setVisibility(View.VISIBLE);
                                    binding.btnHub.setVisibility(View.VISIBLE);
                                    getBatal();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void tandai_lunas() {
        binding.TandaiLunas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(DetailOrderActivity.this)
                        .setTitle("Perhatian")
                        .setMessage("Pastikan pelanggan sudah membayar pesanan.")
                        .setCancelable(false)
                        .setPositiveButton("Tandai Lunas", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                lunas();
                            }
                        })
                        .setNegativeButton("Batal", null)
                        .show();
            }
        });
    }

    private void updateBB() {
        AndroidNetworking.post(Server.site + "set_order.php")
                .addBodyParameter("id_order", id_order)
                .addBodyParameter("kode", String.valueOf(11))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equalsIgnoreCase("1")) {
                                binding.TandaiLunas.setVisibility(View.GONE);
                                binding.SudahLunas.setVisibility(View.VISIBLE);
                                Log.d("tag", response.getString("pesan"));
                                finish();
                            } else {
                                Log.d("tag", response.getString("pesan"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "erro konfirmasi : " + anError);
                    }
                });
    }

    private void kirim_pelanggan() {
        AndroidNetworking.post(Server.site + "set_order.php")
                .addBodyParameter("kode", String.valueOf(4))
                .addBodyParameter("id_order", id_order)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equalsIgnoreCase("1")) {
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error : " + anError);
                    }
                });
    }

    private void getSelesai() {
        AndroidNetworking.post(Server.site + "set_order.php")
                .addBodyParameter("id_order", id_order)
                .addBodyParameter("kode", String.valueOf(9))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject s = response.getJSONObject(i);
                                binding.waktuSelesai.setText("Waktu selesai : " + s.getString("tgl_status") + " " + s.getString("jam_status"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error selesai : " + anError);
                    }
                });
    }

    private void getBatal() {
        AndroidNetworking.post(Server.site + "set_order.php")
                .addBodyParameter("id_order", id_order)
                .addBodyParameter("kode", String.valueOf(9))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject s = response.getJSONObject(i);
                                if (s.getString("status_order").equals("Dibatalkan pembeli")) {
                                    binding.txtPembeli.setVisibility(View.VISIBLE);
                                } else {
                                    binding.txtPenjual.setVisibility(View.VISIBLE);
                                }
                                binding.keterangan.setText("Alasan dibatalkan : " + s.getString("keterangan"));

                                binding.waktubatal.setText("Waktu dibatalkan : " + s.getString("tgl_status") + " " + s.getString("jam_status"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error batal  3: " + anError);
                    }
                });
    }

    private void getItemPesanan() {
        AndroidNetworking.post(Server.site + "set_order.php")
                .addBodyParameter("id_order", id_order)
                .addBodyParameter("kode", String.valueOf(5))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() >= 1) {
                            try {
                                Log.d("tag", "IDO : " + id_order);
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject d = response.getJSONObject(i);
                                    barangDipesanList.add(new BarangDipesan(
                                            d.getString("nama_item"),
                                            d.getString("variasi"),
                                            d.getInt("harga_item"),
                                            d.getInt("banyak_item"),
                                            d.getString("foto_item"),
                                            d.getInt("total_peritem")
                                    ));
                                }

                                BarangDipesanAdapter adapter = new BarangDipesanAdapter(barangDipesanList, DetailOrderActivity.this);
                                binding.recyclerDO.setAdapter(adapter);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(DetailOrderActivity.this, "Item Tidak Ada", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "errordetail : " + anError);
                    }
                });
    }
}