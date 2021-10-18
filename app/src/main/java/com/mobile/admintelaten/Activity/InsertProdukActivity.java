package com.mobile.admintelaten.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Adapter.VariasiAdapter;
import com.mobile.admintelaten.Interface.InterfaceVariasi;
import com.mobile.admintelaten.Model.Variasi;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityInsertProdukBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;

public class InsertProdukActivity extends AppCompatActivity implements InterfaceVariasi {
    private ActivityInsertProdukBinding binding;
    AlertDialog.Builder builder;
    View dialogView;
    EditText editVariasi, editHarga, editStock, editPembelian;
    String id_item, nama_item;
    ArrayList<Variasi> listVar = new ArrayList<>();
    Bitmap bitmap, decoded;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60;
    boolean addFoto = false;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInsertProdukBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        alertDialog = new SpotsDialog.Builder().setContext(InsertProdukActivity.this).setMessage("Sedang Menyimpan Data ...").setCancelable(false).build();

        Intent i = new Intent(getIntent());
        id_item = i.getStringExtra("id_item");
        nama_item = i.getStringExtra("nama_item");
        Log.d("tag", "ID ITEM Produk : " + id_item);

        binding.editNamaItem.setText(nama_item);

        binding.btnVariasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogVariasi();
            }
        });

        binding.recyclerVariasi.setHasFixedSize(true);
        binding.recyclerVariasi.setLayoutManager(new LinearLayoutManager(this, GridLayoutManager.VERTICAL, false));

        binding.imageSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
                addFoto = true;
            }
        });

        binding.btnSubmitInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addFoto && !binding.editDeskripsi.getText().toString().isEmpty()){
                    insert_produk();
                }else {
                    new androidx.appcompat.app.AlertDialog.Builder(InsertProdukActivity.this)
                            .setTitle("Perhatian")
                            .setMessage("Mohon lengkapi data produk anda !!")
                            .setCancelable(false)
                            .setNegativeButton("Oke", null)
                            .show();
                }
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
        Intent i = new Intent(InsertProdukActivity.this, InsertActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void insert_produk() {
        if(!binding.editHargaItem.getText().toString().isEmpty() && !binding.editStock.getText().toString().isEmpty()){
            if(!binding.editminBeli.getText().toString().isEmpty()){
                alertDialog.show();
                AndroidNetworking.post(Server.site + "insert_item.php")
                        .addBodyParameter("id_item", id_item)
                        .addBodyParameter("kode", String.valueOf(2))
                        .addBodyParameter("foto_item", getStringImage(decoded))
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
                                        Toast.makeText(InsertProdukActivity.this, "Item Ditambahkan", Toast.LENGTH_SHORT).show();
                                        Log.d("tag", response.getString("response"));
                                        Intent i = new Intent(InsertProdukActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(InsertProdukActivity.this, "Item Gagal Ditambahkan", Toast.LENGTH_SHORT).show();
                                    }
                                    alertDialog.cancel();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                alertDialog.cancel();
                                Toast.makeText(InsertProdukActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                            }
                        });
            }else {
                alertDialog.show();
                AndroidNetworking.post(Server.site + "insert_item.php")
                        .addBodyParameter("id_item", id_item)
                        .addBodyParameter("kode", String.valueOf(2))
                        .addBodyParameter("foto_item", getStringImage(decoded))
                        .addBodyParameter("harga_item", binding.editHargaItem.getText().toString())
                        .addBodyParameter("stock", binding.editStock.getText().toString())
                        .addBodyParameter("deskripsi", binding.editDeskripsi.getText().toString())
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("kode").equalsIgnoreCase("1")) {
                                        Toast.makeText(InsertProdukActivity.this, "Item Ditambahkan", Toast.LENGTH_SHORT).show();
                                        Log.d("tag", response.getString("response"));
                                        Intent i = new Intent(InsertProdukActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        Toast.makeText(InsertProdukActivity.this, "Item Gagal Ditambahkan", Toast.LENGTH_SHORT).show();
                                    }
                                    alertDialog.cancel();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onError(ANError anError) {
                                alertDialog.cancel();
                                Toast.makeText(InsertProdukActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }else{
            alertDialog.show();
            AndroidNetworking.post(Server.site + "insert_item.php")
                    .addBodyParameter("id_item", id_item)
                    .addBodyParameter("kode", String.valueOf(2))
                    .addBodyParameter("foto_item", getStringImage(decoded))
                    .addBodyParameter("deskripsi", binding.editDeskripsi.getText().toString())
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.getString("kode").equalsIgnoreCase("1")) {
                                    Toast.makeText(InsertProdukActivity.this, "Item Ditambahkan", Toast.LENGTH_SHORT).show();
                                    Log.d("tag", response.getString("response"));
                                    Intent i = new Intent(InsertProdukActivity.this, MainActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Toast.makeText(InsertProdukActivity.this, "Item Gagal Ditambahkan", Toast.LENGTH_SHORT).show();
                                }
                                alertDialog.cancel();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onError(ANError anError) {
                            alertDialog.cancel();
                            Toast.makeText(InsertProdukActivity.this, "Koneksi Bermasalah", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void DialogVariasi() {
        builder = new AlertDialog.Builder(InsertProdukActivity.this);
        dialogView = LayoutInflater.from(InsertProdukActivity.this).inflate(R.layout.popup_variasi, null);

        editVariasi = dialogView.findViewById(R.id.editVariasi);
        editHarga = dialogView.findViewById(R.id.editHarga);
        editStock = dialogView.findViewById(R.id.editStock);
        editPembelian = dialogView.findViewById(R.id.editPembelian);

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
                }else {
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
                    .addBodyParameter("min_pembelian", editPembelian.getText().toString())
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
                                    binding.txtStock.setVisibility(View.GONE);
                                    binding.txtBeli.setVisibility(View.GONE);
                                    binding.editStock.setVisibility(View.GONE);
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
                            Log.d("tag", "error insert variasi 1: " + anError);
                        }
                    });
    }

    private void adapterVariasi() {
        VariasiAdapter adapter = new VariasiAdapter(listVar, this);
        binding.recyclerVariasi.setAdapter(adapter);

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
        binding.imageSet.setImageBitmap(decoded);
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