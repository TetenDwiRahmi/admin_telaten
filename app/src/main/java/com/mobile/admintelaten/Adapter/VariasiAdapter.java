package com.mobile.admintelaten.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Activity.DetailPesananCustomerActivity;
import com.mobile.admintelaten.Interface.InterfaceVariasi;
import com.mobile.admintelaten.Model.Customer;
import com.mobile.admintelaten.Model.Variasi;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class VariasiAdapter extends RecyclerView.Adapter<VariasiAdapter.VariasiViewRecHolder> {
    private List<Variasi> variasi;
    private Context context;
    EditText editVariasi, editHarga, editStock, editBeli;
    InterfaceVariasi interfaceVariasi;

    public VariasiAdapter(List<Variasi> variasi, InterfaceVariasi interfaceVariasi) {
        this.variasi = variasi;
        this.interfaceVariasi = interfaceVariasi;
    }

    @NonNull
    @Override
    public VariasiAdapter.VariasiViewRecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_variasi, null);
        return new VariasiAdapter.VariasiViewRecHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull VariasiAdapter.VariasiViewRecHolder holder, final int position) {

        int rowPos = ((VariasiViewRecHolder) holder).getAdapterPosition();//baris posisi
        Log.d("tag", "RowPos : " + rowPos);

        final Variasi modal = variasi.get(rowPos);

        holder.variasi.setBackgroundResource(R.drawable.table_content_cell_bg);
        holder.harga.setBackgroundResource(R.drawable.table_content_cell_bg);
        holder.stock.setBackgroundResource(R.drawable.table_content_cell_bg);
        holder.minBeli.setBackgroundResource(R.drawable.table_content_cell_bg);

        holder.variasi.setText(modal.getVariasi() + "");
        Locale localeId = new Locale("in", "ID");
        final NumberFormat rupiah = NumberFormat.getCurrencyInstance(localeId);
        holder.harga.setText(rupiah.format(modal.getHarga()));
        holder.stock.setText(modal.getStock() + "");
        holder.minBeli.setText(modal.getMin_pembelian() + "");

        holder.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(v.getRootView().getContext());
                final View dialogView = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.popup_variasi, null);

                editVariasi = dialogView.findViewById(R.id.editVariasi);
                editHarga = dialogView.findViewById(R.id.editHarga);
                editStock = dialogView.findViewById(R.id.editStock);
                editBeli = dialogView.findViewById(R.id.editPembelian);

                builder.setCancelable(true);

                AndroidNetworking.post(Server.site + "get_variasi.php")
                        .addBodyParameter("id_variasi", String.valueOf(modal.getId_variasi()))
                        .addBodyParameter("kode", String.valueOf(2))
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("kode").equalsIgnoreCase("1")) {
                                        JSONObject data = response.getJSONObject("data");
                                        editVariasi.setText(data.getString("variasi"));
                                        editHarga.setText(data.getString("harga"));
                                        editStock.setText(data.getString("stock"));
                                        editBeli.setText(data.getString("min_pembelian"));
                                    } else {
                                        Log.d("tag", "get data variasi adapter : " + response.getString("pesan"));
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
                                Log.d("tag", "error get data variasi adapter : " + anError);
                            }
                        });

                builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (editVariasi.getText().toString().length() == 0) {
                            editVariasi.setError("Wajib diisi");
                        } else if (editHarga.getText().toString().length() == 0) {
                            editHarga.setError("Wajib diisi");
                        } else if (editStock.getText().toString().length() == 0) {
                            editStock.setError("Wajib diisi");
                        }  else {
                            update_variasi(modal.getId_variasi());
                        }
                    }
                });

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Perhatian")
                        .setMessage("Apakah anda yakin ingin menghapus variasi ini?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                hapusdata(modal.getId_variasi());
                                variasi.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, variasi.size());
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Tidak", null)
                        .show();

            }
        });

    }

    private void update_variasi(final int id_variasi) {
        AndroidNetworking.post(Server.site + "set_variasi.php")
                .addBodyParameter("id_variasi", String.valueOf(id_variasi))
                .addBodyParameter("variasi", editVariasi.getText().toString())
                .addBodyParameter("harga", editHarga.getText().toString())
                .addBodyParameter("stock", editStock.getText().toString())
                .addBodyParameter("min_pembelian", editBeli.getText().toString())
                .addBodyParameter("kode", String.valueOf(2))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equalsIgnoreCase("1")) {
                                Log.d("tag", response.getString("pesan"));
                                interfaceVariasi.UpdateVariasi();
                            } else {
                                Log.d("tag", response.getString("pesan"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error ubah variasi : " + anError);
                    }
                });
    }

    private void hapusdata(int id_variasi) {
        AndroidNetworking.post(Server.site + "set_variasi.php")
                .addBodyParameter("id_variasi", String.valueOf(id_variasi))
                .addBodyParameter("kode", String.valueOf(3))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("sukses", "code : " + response);
                            if (response.getString("status").equalsIgnoreCase("1")) {
                                Toast.makeText(context, "Variasi Dihapus", Toast.LENGTH_SHORT).show();
                                interfaceVariasi.UpdateVariasi();
                            } else {
                                Log.d("tag", "pesan : " + response.getString("message"));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eror", "error hapus variasi : " + anError);
                        Toast.makeText(context, "Gagal Menghapus Variasi", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public int getItemCount() {
        return variasi.size();
    }

    public class VariasiViewRecHolder extends RecyclerView.ViewHolder {
        TextView variasi, harga, stock, aksi, minBeli;
        ImageButton update, delete;

        public VariasiViewRecHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            variasi = itemView.findViewById(R.id.variasi);
            harga = itemView.findViewById(R.id.harga);
            stock = itemView.findViewById(R.id.stock);
            minBeli = itemView.findViewById(R.id.minBeli);
            //aksi = itemView.findViewById(R.id.aksi);
            update = itemView.findViewById(R.id.update);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
