package com.mobile.admintelaten.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Activity.PembayaranActivity;
import com.mobile.admintelaten.Interface.InterfacePembayaran;
import com.mobile.admintelaten.Model.Pembayaran;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class PembayaranAdapter extends RecyclerView.Adapter<PembayaranAdapter.PembayaranViewHolder> {
    private Context context;
    private List<Pembayaran> pembayaran;
    Dialog dialog;
    EditText editNamaBank, editNamaPemilik, editNoRek;
    Button btnSubmit, btnCancel;
    InterfacePembayaran interfacePembayaran;

    public PembayaranAdapter(List<Pembayaran> pembayaran, InterfacePembayaran interfacePembayaran) {
        this.pembayaran = pembayaran;
        this.interfacePembayaran = interfacePembayaran;
    }

    @NonNull
    @Override
    public PembayaranAdapter.PembayaranViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_bank, null);
        return new PembayaranAdapter.PembayaranViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PembayaranAdapter.PembayaranViewHolder holder, final int position) {
        final Pembayaran p = pembayaran.get(position);
        holder.nama_bank.setText(p.getNama_bank());
        holder.no_rek.setText(p.getNo_rek() );
        holder.atas_nama.setText(p.getAtas_nama() + " | ");

        holder.imagedelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Perhatian")
                        .setMessage("Apakah anda yakin menghapus akun ini?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                hapusdata(p.getId_bank());
                                pembayaran.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, pembayaran.size());
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Tidak", null)
                        .show();
            }
        });

        holder.imageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(context);
                dialog.setContentView(R.layout.popup_bank);
                editNamaPemilik = dialog.findViewById(R.id.editNamaPemilik);
                editNamaBank = dialog.findViewById(R.id.editNamaBank);
                editNoRek = dialog.findViewById(R.id.editNoRek);
                btnSubmit = dialog.findViewById(R.id.btnSubmit);
                btnCancel = dialog.findViewById(R.id.btnCancel);

                AndroidNetworking.post(Server.site  + "set_pembayaran.php")
                        .addBodyParameter("kode", String.valueOf(5))
                        .addBodyParameter("id_bank", String.valueOf(p.getId_bank()))
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(new JSONObjectRequestListener() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("kode").equals("1")){
                                        JSONObject data = response.getJSONObject("data");
                                        editNamaBank.setText(data.getString("nama_bank"));
                                        editNamaPemilik.setText(data.getString("atas_nama"));
                                        editNoRek.setText(data.getString("no_rek"));
                                    }else{
                                        Toast.makeText(context, "Gagal mengambil data", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.d("tag", "error get data pembayaran : " + anError);
                            }
                        });

                btnSubmit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (editNamaBank.getText().toString().isEmpty()) {
                            editNamaBank.setError("Harus diisi");
                        } else if (editNoRek.getText().toString().isEmpty()) {
                            editNoRek.setError("Harus diisi");
                        } else if (editNamaPemilik.getText().toString().isEmpty()) {
                            editNamaPemilik.setError("Harus diisi");
                        } else {
                            AndroidNetworking.post(Server.site + "set_pembayaran.php")
                                    .addBodyParameter("kode", String.valueOf(3))
                                    .addBodyParameter("id_bank", String.valueOf(p.getId_bank()))
                                    .addBodyParameter("nama_bank", editNamaBank.getText().toString())
                                    .addBodyParameter("no_rek", editNoRek.getText().toString())
                                    .addBodyParameter("atas_nama", editNamaPemilik.getText().toString())
                                    .setPriority(Priority.MEDIUM)
                                    .build()
                                    .getAsJSONObject(new JSONObjectRequestListener() {
                                        @Override
                                        public void onResponse(JSONObject response) {
                                            try {
                                                if (response.getString("status").equals("1")) {
                                                    dialog.cancel();
                                                    interfacePembayaran.UpdatePembayaran();
                                                    Toast.makeText(context, "Akun Diubah", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(context, "Akun Gagal Diubah", Toast.LENGTH_SHORT).show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onError(ANError anError) {
                                            Log.d("tag", "error update pembayaran : " + anError);
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
        });
    }

    private void hapusdata(int id_bank) {
        AndroidNetworking.post(Server.site + "set_pembayaran.php")
                .addBodyParameter("id_bank", String.valueOf(id_bank))
                .addBodyParameter("kode", String.valueOf(2))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("1")){
                                interfacePembayaran.UpdatePembayaran();
                                Toast.makeText(context, "Akun Dihapus", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(context, "Akun Gagal Dihapus", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error hapus pembayaran : " + anError);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return pembayaran.size();
    }

    public class PembayaranViewHolder extends RecyclerView.ViewHolder {
        TextView nama_bank, no_rek, atas_nama;
        ImageButton imageUpdate, imagedelete;
        public PembayaranViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            nama_bank = itemView.findViewById(R.id.nama_bank);
            no_rek = itemView.findViewById(R.id.no_rek);
            atas_nama = itemView.findViewById(R.id.atas_nama);
            imageUpdate = itemView.findViewById(R.id.btn_update);
            imagedelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
