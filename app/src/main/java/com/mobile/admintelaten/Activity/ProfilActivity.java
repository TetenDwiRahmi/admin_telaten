package com.mobile.admintelaten.Activity;

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
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.PrefManager;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityProfilBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfilActivity extends AppCompatActivity {
    private ActivityProfilBinding binding;
    ImageView imageProfil;
    PrefManager prefManager;
    Bitmap bitmap, decoded;
    int PICK_IMAGE_REQUEST = 1;
    int bitmap_size = 60;
    boolean addFoto = false;
    RelativeLayout layout_koneksi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfilBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        imageProfil = findViewById(R.id.imageProfil);

        prefManager = new PrefManager(this);

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfilActivity.this, MainActivity.class);
                startActivity(i);
            }
        });

        binding.imageProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoto = true;
                showFileChooser();
            }
        });

        binding.btnUpdateProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addFoto) {
                    if (binding.editPassword.getText().toString().length() == 0) {
                        update_tanpa_pass();
                    } else {
                        updateprofil();
                    }
                } else {
                    if (binding.editPassword.getText().toString().length() == 0) {
                        update_username();
                    } else {
                        update_tanpa_foto();
                    }
                }
            }
        });

        Log.d("id User", " id : " + prefManager.getIdUser());

        layout_koneksi = findViewById(R.id.layout_koneksi);
        layout_koneksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataprofil();
            }
        });

        dataprofil();
    }

    private void dataprofil() {
        layout_koneksi.setVisibility(View.GONE);
        AndroidNetworking.post(Server.site + "set_profil.php")
                .addBodyParameter("id", prefManager.getIdUser())
                .addBodyParameter("kode", String.valueOf(1))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            binding.layout.setVisibility(View.VISIBLE);
                            if(response.getString("kode").equals("1")){
                                JSONObject data = response.getJSONObject("data");
                                binding.editUsername.setText(data.getString("username"));
                                Picasso.get().load(Server.site_foto_admin + data.getString("foto_user")).into(binding.imageProfil);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
//                        Log.d("tag", "error profil : " +anError);
                        layout_koneksi.setVisibility(View.VISIBLE);
                        binding.layout.setVisibility(View.GONE);
                    }
                });
    }

    private void update_username() {
        AndroidNetworking.post(Server.site + "set_profil.php")
                .addBodyParameter("kode", String.valueOf(2))
                .addBodyParameter("id", prefManager.getIdUser())
                .addBodyParameter("username", binding.editUsername.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getString("status").equalsIgnoreCase("1")) {
                                Toast.makeText(ProfilActivity.this, "Profil Diubah", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(ProfilActivity.this, AkunActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(ProfilActivity.this, "Gagal Mengubah Profil", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error update username : " + anError);
                    }
                });
    }

    private void update_tanpa_foto() {
        AndroidNetworking.post(Server.site + "set_profil.php")
                .addBodyParameter("kode", String.valueOf(2))
                .addBodyParameter("id", prefManager.getIdUser())
                .addBodyParameter("username", binding.editUsername.getText().toString())
                .addBodyParameter("password", binding.editPassword.getText().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getString("status").equalsIgnoreCase("1")) {
                                Toast.makeText(ProfilActivity.this, "Profil Diubah", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(ProfilActivity.this, AkunActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(ProfilActivity.this, "Gagal Mengubah Profil", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error update tanpa foto : " + anError);
                    }
                });
    }

    private void update_tanpa_pass() {
        AndroidNetworking.post(Server.site + "set_profil.php")
                .addBodyParameter("kode", String.valueOf(2))
                .addBodyParameter("id", prefManager.getIdUser())
                .addBodyParameter("username", binding.editUsername.getText().toString())
                .addBodyParameter("foto_user", getStringImage(decoded))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getString("status").equalsIgnoreCase("1")) {
                                Toast.makeText(ProfilActivity.this, "Profil Diubah", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(ProfilActivity.this, AkunActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(ProfilActivity.this, "Gagal Mengubah Profil", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error update tanpa pass : " + anError);
                    }
                });
    }

    private void updateprofil() {
        AndroidNetworking.post(Server.site + "set_profil.php")
                .addBodyParameter("kode", String.valueOf(2))
                .addBodyParameter("id", prefManager.getIdUser())
                .addBodyParameter("username", binding.editUsername.getText().toString())
                .addBodyParameter("password", binding.editPassword.getText().toString())
                .addBodyParameter("foto_user", getStringImage(decoded))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if (response.getString("status").equalsIgnoreCase("1")) {
                                Toast.makeText(ProfilActivity.this, "Profil Diubah", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(ProfilActivity.this, AkunActivity.class);
                                startActivity(i);
                            } else {
                                Toast.makeText(ProfilActivity.this, "Gagal Mengubah Profil", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error update profil : " + anError);
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
        binding.imageProfil.setImageBitmap(decoded);
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
                //mengambil gambar dari Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // 512 adalah resolusi tertinggi setelah image di resize, bisa di ganti.
                setToImageView(getResizedBitmap(bitmap, 512));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}