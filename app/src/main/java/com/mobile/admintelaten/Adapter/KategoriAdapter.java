package com.mobile.admintelaten.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Activity.ItemKategoriActivity;
import com.mobile.admintelaten.Interface.InterfaceKategoriUpdate;
import com.mobile.admintelaten.Model.Kategori;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class KategoriAdapter extends RecyclerView.Adapter<KategoriAdapter.KategoriViewHolder> {
    List<Kategori> kategoriList;
    Context context;
    EditText nama_kategori;
    AlertDialog.Builder builder;
    View dialogView;
    InterfaceKategoriUpdate kategoriUpdate;

    public KategoriAdapter(List<Kategori> kategoriList, InterfaceKategoriUpdate kategoriUpdate) {
        this.kategoriList = kategoriList;
        this.kategoriUpdate = kategoriUpdate;
    }

    @NonNull
    @Override
    public KategoriViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_kategori, null);
        return new KategoriAdapter.KategoriViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final KategoriViewHolder holder, final int position) {
        final Kategori kategori = kategoriList.get(position);
        holder.kategori.setText(kategori.getNama_kategori());
        holder.update_kategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder = new AlertDialog.Builder(v.getRootView().getContext());
                dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.popup_update_kategori, null);
                nama_kategori = dialogView.findViewById(R.id.editNamaKategori);
                builder.setCancelable(true);

                get_nmKategori(kategori.getId_kategori());

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (nama_kategori.getText().toString().isEmpty()) {
                            nama_kategori.setError("Harus diisi");
                        } else {
                            AndroidNetworking.post(Server.site + "set_kategori.php")
                                    .addBodyParameter("id_kategori", String.valueOf(kategori.getId_kategori()))
                                    .addBodyParameter("nama_kategori", nama_kategori.getText().toString())
                                    .addBodyParameter("kode", String.valueOf(6))
                                    .setPriority(Priority.MEDIUM)
                                    .build()
                                    .getAsJSONObject(new JSONObjectRequestListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if (response.getString("status").equalsIgnoreCase("1")) {
                                                    kategoriUpdate.UpdateKategori();
                                                    Toast.makeText(context, "Kategori Diubah", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    kategoriUpdate.UpdateKategori();
                                                    Toast.makeText(context, "Gagal Update Data", Toast.LENGTH_SHORT).show();

                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                            Log.d("tag", "error update KA : " + anError);
                                        }
                                    });
                        }
                    }
                });
            }
        });

        AndroidNetworking.post(Server.site + "set_kategori.php")
                .addBodyParameter("id_kategori", String.valueOf(kategori.getId_kategori()))
                .addBodyParameter("kode", String.valueOf(7))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equalsIgnoreCase("1")) {
                                holder.jml_produk.setText(response.getString("jml") + " Produk");
                            } else {
                                holder.jml_produk.setText(response.getString("jml") + " Produk");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error update KA : " + anError);
                    }
                });

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ItemKategoriActivity.class);
                i.putExtra("id_kategori", kategori.getId_kategori());
                i.putExtra("nama_kategori", kategori.getNama_kategori());
                context.startActivity(i);
            }
        });

        holder.delete_kategori.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("Perhatian")
                        .setMessage("Apabila anda menghapus kategori ini maka produk dari kategori tersebut juga terhapus otomatis!!")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                hapusdata(kategori.getId_kategori());
                                kategoriList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, kategoriList.size());
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Tidak", null)
                        .show();

            }
        });
    }

    private void hapusdata(int id_kategori) {
        AndroidNetworking.post(Server.site + "set_kategori.php")
                .addBodyParameter("id_kategori", String.valueOf(id_kategori))
                .addBodyParameter("kode", String.valueOf(8))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("sukses", "code : " + response);
                            if (response.getString("status").equalsIgnoreCase("1")) {
                                kategoriUpdate.UpdateKategori();
                                Toast.makeText(context, "Kategori Dihapus", Toast.LENGTH_SHORT).show();
                            }else {
                                kategoriUpdate.UpdateKategori();
                                Toast.makeText(context, "Kategori Gagal Dihapus", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eror", "code : " + anError);
                        Toast.makeText(context, "Gagal Menghapus Kategori", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void get_nmKategori(int id_kategori) {
        AndroidNetworking.post(Server.site + "set_kategori.php")
                .addBodyParameter("kode", String.valueOf(2))
                .addBodyParameter("id_kategori", String.valueOf(id_kategori))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equalsIgnoreCase("1")) {
                                JSONObject data = response.getJSONObject("data");
                                nama_kategori.setText(data.getString("nama_kategori"));
                            }
                            builder.setView(dialogView);
                            builder.setCancelable(true);
                            builder.show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error nmKat KA : " + anError);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return kategoriList.size();
    }

    public void filterList(ArrayList<Kategori> kategoriListFull) {
        kategoriList = kategoriListFull;
        notifyDataSetChanged();
    }

    public class KategoriViewHolder extends RecyclerView.ViewHolder {
        TextView kategori, jml_produk;
        ImageButton update_kategori, delete_kategori;
        RelativeLayout layout;

        public KategoriViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            kategori = itemView.findViewById(R.id.kategori);
            jml_produk = itemView.findViewById(R.id.jml_produk);
            update_kategori = itemView.findViewById(R.id.update_kategori);
            delete_kategori = itemView.findViewById(R.id.delete_kategori);
            layout = itemView.findViewById(R.id.contextKategori);
        }
    }
}
