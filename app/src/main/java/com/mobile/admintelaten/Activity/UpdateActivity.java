package com.mobile.admintelaten.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Adapter.ItemAdapter;
import com.mobile.admintelaten.Adapter.PopUpKategoriAdapter;
import com.mobile.admintelaten.Adapter.VariasiAdapter;
import com.mobile.admintelaten.Interface.InterfaceVariasi;
import com.mobile.admintelaten.Model.Item;
import com.mobile.admintelaten.Model.Kategori;
import com.mobile.admintelaten.Model.Variasi;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityUpdateBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UpdateActivity extends AppCompatActivity implements InterfaceVariasi {
    private ActivityUpdateBinding binding;
    String id_item;
    List<Kategori> kategoriList = new ArrayList<>();
    AlertDialog.Builder builder;
    View dialogView;
    RecyclerView recyclerPopupKategori;
    PopUpKategoriAdapter adapter;
    int idKat, id_kat;
    Bitmap bitmap, decoded;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60;
    boolean addFoto = false;
    ArrayList<Variasi> listVar = new ArrayList<>();
    EditText editVariasi, editHarga, editStock, editBeli;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        Intent i = new Intent(getIntent());
        id_item = String.valueOf(i.getIntExtra("id_item", 0));
        Log.d("id_item", "code : " + id_item);

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.imageSetUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
                addFoto = true;
                Log.d("tag", "add foto : " + addFoto);
            }
        });

        binding.btnSubmitUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addFoto) {
                    update_data1();
                }
                else {
                    update_data2();
                }

            }
        });

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UpdateActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        binding.recyclerVariasi.setHasFixedSize(true);
        binding.recyclerVariasi.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));

        binding.layoutKategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupKategori();
            }
        });

        datapesanan();
        cek_variasi();

    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(UpdateActivity.this, MainActivity.class);
        startActivity(i);
    }

    private void popupKategori() {
        builder = new AlertDialog.Builder(UpdateActivity.this);
        dialogView = LayoutInflater.from(UpdateActivity.this).inflate(R.layout.popup_kategori, null);

        recyclerPopupKategori = dialogView.findViewById(R.id.recyclerPopupKategori);
        recyclerPopupKategori.setHasFixedSize(true);
        recyclerPopupKategori.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));

        adapter = new PopUpKategoriAdapter(this, kategoriList);
        recyclerPopupKategori.setAdapter(adapter);

        MenuPerkategori();

        builder.setCancelable(true);

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (adapter.getSelected() != null) {
                    idKat = adapter.getSelected().getId_kategori();
                    binding.NamaKategori.setText(adapter.getSelected().getNama_kategori());
                    Log.d("tag", "id_kategori UA : " + idKat);
                }
            }

        });
        builder.setView(dialogView);
        builder.setCancelable(true);
        builder.show();
    }


    private void MenuPerkategori() {
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
                                JSONObject kategori = null;
                                kategori = response.getJSONObject(i);
                                kategoriList.add(new Kategori(
                                        kategori.getInt("id_kategori"),
                                        kategori.getString("nama_kategori")
                                ));
                            }
                            adapter.SetKategori(kategoriList);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eror berd kategori UA", "code : " + anError);
                        Toast.makeText(UpdateActivity.this, "Gagal get Data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void update_data1() {
        if (Integer.parseInt(binding.editStock.getText().toString()) != 0) {
                AndroidNetworking.post(Server.site + "update_item.php")
                        .addBodyParameter("id_item", id_item)
                        .addBodyParameter("id_kategori", idKat + "")
                        .addBodyParameter("kode", String.valueOf(1))
                        .addBodyParameter("foto_item", getStringImage(decoded))
                        .addBodyParameter("nama_item", binding.editNamaItem.getText().toString())
                        .addBodyParameter("harga_item", binding.editHargaItem.getText().toString())
                        .addBodyParameter("stock", binding.editStock.getText().toString())
                        .addBodyParameter("min_pembelian", binding.editminBeli.getText().toString())
                        .addBodyParameter("deskripsi", binding.editDeskripsi.getText().toString())
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("kode").equalsIgnoreCase("1")) {
                                        Toast.makeText(UpdateActivity.this, "Item Diubah", Toast.LENGTH_SHORT).show();
                                        Log.d("tag", response.getString("response"));
                                        Intent i = new Intent(UpdateActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(UpdateActivity.this, "Gagal Mengubah Item", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("tag", "error update data 1... : " + anError);
                            }
                        });


        } else {
            if (!binding.editHargaItem.getText().toString().isEmpty()) {
                AndroidNetworking.post(Server.site + "update_item.php")
                        .addBodyParameter("id_item", id_item)
                        .addBodyParameter("id_kategori", idKat + "")
                        .addBodyParameter("kode", String.valueOf(1))
                        .addBodyParameter("kode_var", String.valueOf(1))
                        .addBodyParameter("foto_item", getStringImage(decoded))
                        .addBodyParameter("harga_item", binding.editHargaItem.getText().toString())
                        .addBodyParameter("min_pembelian", binding.editminBeli.getText().toString())
                        .addBodyParameter("nama_item", binding.editNamaItem.getText().toString())
                        .addBodyParameter("deskripsi", binding.editDeskripsi.getText().toString())
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("kode").equalsIgnoreCase("1")) {
                                        Toast.makeText(UpdateActivity.this, "Item Diubah", Toast.LENGTH_SHORT).show();
                                        Log.d("tag", response.getString("response"));
                                        Intent i = new Intent(UpdateActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(UpdateActivity.this, "Gagal Mengubah Item", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("tag", "error update data 1. : " + anError);
                            }
                        });
            }
        }
    }

    private void update_data2() {
        if (Integer.parseInt(binding.editStock.getText().toString()) != 0) {
            AndroidNetworking.post(Server.site + "update_item.php")
                    .addBodyParameter("id_item", id_item)
                    .addBodyParameter("id_kategori", idKat + "")
                    .addBodyParameter("kode", String.valueOf(2))
                    .addBodyParameter("nama_item", binding.editNamaItem.getText().toString())
                    .addBodyParameter("harga_item", binding.editHargaItem.getText().toString())
                    .addBodyParameter("stock", binding.editStock.getText().toString())
                    .addBodyParameter("min_pembelian", binding.editminBeli.getText().toString())
                    .addBodyParameter("deskripsi", binding.editDeskripsi.getText().toString())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("kode").equalsIgnoreCase("1")) {
                                    Toast.makeText(UpdateActivity.this, "Item Diubah", Toast.LENGTH_SHORT).show();
                                    Log.d("tag", response.getString("response"));
                                    Intent i = new Intent(UpdateActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(UpdateActivity.this, "Gagal Mengubah Item", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.d("tag", "error update data 2... : " + anError);
                        }
                    });
        } else {
            if (!binding.editHargaItem.getText().toString().isEmpty()) {
                AndroidNetworking.post(Server.site + "update_item.php")
                        .addBodyParameter("id_item", id_item)
                        .addBodyParameter("id_kategori", idKat + "")
                        .addBodyParameter("kode", String.valueOf(2))
                        .addBodyParameter("kode_var", String.valueOf(1))
                        .addBodyParameter("harga_item", binding.editHargaItem.getText().toString())
                        .addBodyParameter("min_pembelian", binding.editminBeli.getText().toString())
                        .addBodyParameter("nama_item", binding.editNamaItem.getText().toString())
                        .addBodyParameter("deskripsi", binding.editDeskripsi.getText().toString())
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("kode").equalsIgnoreCase("1")) {
                                        Toast.makeText(UpdateActivity.this, "Item Diubah", Toast.LENGTH_SHORT).show();
                                        Log.d("tag", response.getString("response"));
                                        Intent i = new Intent(UpdateActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(UpdateActivity.this, "Gagal Mengubah Item", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("tag", "error update data 2. : " + anError);
                            }
                        });
            } else {
                AndroidNetworking.post(Server.site + "update_item.php")
                        .addBodyParameter("id_item", id_item)
                        .addBodyParameter("id_kategori", idKat + "")
                        .addBodyParameter("kode", String.valueOf(2))
                        .addBodyParameter("kode_var", String.valueOf(2))
                        .addBodyParameter("nama_item", binding.editNamaItem.getText().toString())
                        .addBodyParameter("deskripsi", binding.editDeskripsi.getText().toString())
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("kode").equalsIgnoreCase("1")) {
                                        Toast.makeText(UpdateActivity.this, "Item Diubah", Toast.LENGTH_SHORT).show();
                                        Log.d("tag", response.getString("response"));
                                        Intent i = new Intent(UpdateActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(UpdateActivity.this, "Gagal Mengubah Item", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("tag", "error update data 2. : " + anError);
                            }
                        });
            }
        }
    }

    private void cek_variasi() {
        AndroidNetworking.post(Server.site + "get_variasi.php")
                .addBodyParameter("kode", String.valueOf(4))
                .addBodyParameter("id_item", id_item)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equalsIgnoreCase("0")) {
                                Log.d("tag", response.getString("pesan"));
                                binding.txtVariasi.setVisibility(View.VISIBLE);
                                binding.btnVariasi.setVisibility(View.VISIBLE);
                                binding.btnVariasi.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DialogVariasi();
                                    }
                                });
                                binding.txtHarga.setVisibility(View.VISIBLE);
                                binding.editHargaItem.setVisibility(View.VISIBLE);
                                binding.txtStock.setVisibility(View.VISIBLE);
                                binding.editStock.setVisibility(View.VISIBLE);
                                binding.txtBeli.setVisibility(View.VISIBLE);
                                binding.editminBeli.setVisibility(View.VISIBLE);
                                binding.layoutVariasi.setVisibility(View.GONE);
                                //binding.headingLayout.setVisibility(View.GONE);
                                binding.btnTambahVariasi.setVisibility(View.GONE);

                            } else {
                                get_variasi();
                                binding.txtVariasi.setVisibility(View.GONE);
                                binding.btnVariasi.setVisibility(View.GONE);
                                binding.txtHarga.setVisibility(View.GONE);
                                binding.editHargaItem.setVisibility(View.GONE);
                                binding.txtStock.setVisibility(View.GONE);
                                binding.editStock.setVisibility(View.GONE);
                                binding.txtBeli.setVisibility(View.GONE);
                                binding.editminBeli.setVisibility(View.GONE);
                                binding.layoutVariasi.setVisibility(View.VISIBLE);
                                //binding.headingLayout.setVisibility(View.VISIBLE);
                                binding.btnTambahVariasi.setVisibility(View.VISIBLE);
                                binding.btnTambahVariasi.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DialogVariasi();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error stock IA : " + anError);
                    }
                });
    }

    private void DialogVariasi() {
        builder = new AlertDialog.Builder(UpdateActivity.this);
        dialogView = LayoutInflater.from(UpdateActivity.this).inflate(R.layout.popup_variasi, null);

        editVariasi = dialogView.findViewById(R.id.editVariasi);
        editHarga = dialogView.findViewById(R.id.editHarga);
        editStock = dialogView.findViewById(R.id.editStock);
        editBeli = dialogView.findViewById(R.id.editPembelian);

        builder.setCancelable(true);

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setPositiveButton("Konfirmasi", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (editVariasi.getText().toString().length() == 0) {
                    editVariasi.setError("Wajib diisi");
                } else if (editHarga.getText().toString().length() == 0) {
                    editHarga.setError("Wajib diisi");
                } else if (editStock.getText().toString().length() == 0) {
                    editStock.setError("Wajib diisi");
                } else {
                    set_variasi();
                }
            }

        });


        builder.setView(dialogView);
        builder.setCancelable(true);
        builder.show();
    }

    private void set_variasi() {
        AndroidNetworking.post(Server.site + "set_variasi.php")
                .addBodyParameter("id_item", id_item)
                .addBodyParameter("variasi", editVariasi.getText().toString())
                .addBodyParameter("harga", editHarga.getText().toString())
                .addBodyParameter("stock", editStock.getText().toString())
                .addBodyParameter("min_pembelian", editBeli.getText().toString())
                .addBodyParameter("kode", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equalsIgnoreCase("1")) {
                                Log.d("tag", response.getString("pesan"));
                                binding.txtVariasi.setVisibility(View.GONE);
                                binding.btnVariasi.setVisibility(View.GONE);
                                binding.txtHarga.setVisibility(View.GONE);
                                binding.editHargaItem.setVisibility(View.GONE);
                                binding.editHargaItem.setText("0"); //jika tidak di set 0 maka data harga dan stock tidak di update
                                binding.txtStock.setVisibility(View.GONE);
                                binding.editStock.setVisibility(View.GONE);
                                binding.editStock.setText("0");
                                binding.txtBeli.setVisibility(View.GONE);
                                binding.editminBeli.setVisibility(View.GONE);
                                binding.layoutVariasi.setVisibility(View.VISIBLE);
                                //binding.headingLayout.setVisibility(View.VISIBLE);
                                binding.btnTambahVariasi.setVisibility(View.VISIBLE);
                                binding.btnTambahVariasi.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DialogVariasi();
                                    }
                                });
                                get_variasi();
                            } else {
                                Log.d("tag", response.getString("pesan"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error insert variasi : " + anError);
                    }
                });

    }

    private void get_variasi() {
        AndroidNetworking.post(Server.site + "get_variasi.php")
                .addBodyParameter("id_item", id_item)
                .addBodyParameter("kode", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            listVar.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject variasi = response.getJSONObject(i);
                                listVar.add(new Variasi(
                                        variasi.getInt("id_variasi"),
                                        variasi.getInt("id_item"),
                                        variasi.getString("variasi"),
                                        variasi.getDouble("stock"),
                                        variasi.getInt("min_pembelian"),
                                        variasi.getDouble("harga")));
                            }
                            adapterVariasi();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error variasi di Detail: " + anError);
                    }
                });

    }

    private void adapterVariasi() {
        VariasiAdapter adapter = new VariasiAdapter(listVar, this);
        binding.recyclerVariasi.setAdapter(adapter);

    }

    private void datapesanan() {
        AndroidNetworking.post(Server.site + "set_produk.php")
                .addBodyParameter("id_item", id_item)
                .addBodyParameter("kode", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("sukses", "code : " + response);
                            if (response.getString("kode").equalsIgnoreCase("1")) {
                                JSONObject data = response.getJSONObject("data");
                                binding.editNamaItem.setText(data.getString("nama_item"));
                                binding.editHargaItem.setText(data.getString("harga_item"));
                                binding.editStock.setText(data.getString("stock"));
                                binding.editminBeli.setText(data.getString("min_pembelian"));
                                String foto = data.getString("foto_item");
                                Picasso.get().load(Server.site_foto + foto).resize(172, 172).into(binding.imageSetUpdate);
                                binding.editDeskripsi.setText(data.getString("deskripsi"));
                                idKat = Integer.parseInt(data.getString("id_kategori")); /// ID kategori ini diambil jika admin tidak ingin mengubah kategori
                                binding.NamaKategori.setText(data.getString("nama_kategori"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("error", "code : " + anError);
                        Toast.makeText(UpdateActivity.this, "Gagal Mengambil Data", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void UpdateVariasi() {
        ///variasi jika dihapus semua dari list variasi
        AndroidNetworking.post(Server.site + "get_variasi.php")
                .addBodyParameter("kode", String.valueOf(4))
                .addBodyParameter("id_item", id_item)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equalsIgnoreCase("0")) {
                                Log.d("tag", response.getString("pesan"));
                                binding.txtVariasi.setVisibility(View.VISIBLE);
                                binding.btnVariasi.setVisibility(View.VISIBLE);
                                binding.btnVariasi.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        DialogVariasi();
                                    }
                                });
                                binding.txtHarga.setVisibility(View.VISIBLE);
                                binding.editHargaItem.setVisibility(View.VISIBLE);
                                binding.txtStock.setVisibility(View.VISIBLE);
                                binding.editStock.setVisibility(View.VISIBLE);
                                binding.txtBeli.setVisibility(View.VISIBLE);
                                binding.editminBeli.setVisibility(View.VISIBLE);
                                binding.layoutVariasi.setVisibility(View.GONE);
                                //binding.headingLayout.setVisibility(View.GONE);
                                binding.btnTambahVariasi.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error stock IA : " + anError);
                    }
                });

        AndroidNetworking.post(Server.site + "get_variasi.php")
                .addBodyParameter("id_item", id_item)
                .addBodyParameter("kode", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {

                            listVar.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject variasi = response.getJSONObject(i);
                                listVar.add(new Variasi(
                                        variasi.getInt("id_variasi"),
                                        variasi.getInt("id_item"),
                                        variasi.getString("variasi"),
                                        variasi.getDouble("stock"),
                                        variasi.getInt("min_pembelian"),
                                        variasi.getDouble("harga")));

                            }
                            adapterVariasi();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error variasi di interface: " + anError);
                    }
                });
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, baos);

        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void setToImageView(Bitmap bmp) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, bitmap_size, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));
        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        binding.imageSetUpdate.setImageBitmap(decoded);
    }

    // fungsi resize image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //mengambil fambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}