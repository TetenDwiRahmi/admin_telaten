package com.mobile.admintelaten.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Activity.DetailOrderActivity;
import com.mobile.admintelaten.Model.Pesanan;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PesananAdapter extends RecyclerView.Adapter<PesananAdapter.PesananViewRecHolder> {
    private Context context;
    private List<Pesanan> pesananList;

    public PesananAdapter(Context context, List<Pesanan> pesananList) {
        this.context = context;
        this.pesananList = pesananList;
    }

    @NonNull
    @Override
    public PesananAdapter.PesananViewRecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_pesanan, null);
        return new PesananAdapter.PesananViewRecHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final PesananAdapter.PesananViewRecHolder holder, int position) {
        final Pesanan p = pesananList.get(position);
        holder.idOrder.setText(p.getId_order());
        holder.tglOrder.setText(p.getTgl_order());
        holder.statusOrder.setText(p.getStatus_order());
        holder.namaCustomer.setText(p.getNama_customer());
        holder.JumlahItem.setText(p.getTotal_item() +" Item");
        Locale locale = new Locale("in", "ID");
        final NumberFormat rp = NumberFormat.getCurrencyInstance(locale);
        holder.TotalProduk.setText(rp.format(p.getTotal_bayar()));
        holder.LayoutOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailOrderActivity.class);
                i.putExtra("id_order", p.getId_order());
                i.putExtra("id_customer", p.getId_customer());
                context.startActivity(i);
                Log.d("tag", "idCus : " + p.getId_customer());
            }
        });
        holder.metodepengiriman.setText(p.getMetode_pengiriman());

        AndroidNetworking.post(Server.site + "set_order.php")
                .addBodyParameter("id_order", p.getId_order())
                .addBodyParameter("kode", String.valueOf(8))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equalsIgnoreCase("1")) {
                                if(response.getString("status").equals("1")){
                                    holder.lunas.setVisibility(View.VISIBLE);
                                    holder.belumlunas.setVisibility(View.GONE);
                                }else {
                                    holder.lunas.setVisibility(View.GONE);
                                    holder.belumlunas.setVisibility(View.VISIBLE);
                                }
                            } else {
                                holder.lunas.setVisibility(View.GONE);
                                holder.belumlunas.setVisibility(View.VISIBLE);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error status lunas pesanan adapter: " + anError);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return pesananList.size();
    }

    public class PesananViewRecHolder extends RecyclerView.ViewHolder {
        TextView statusOrder, JumlahItem, TotalProduk, tglOrder, idOrder, namaCustomer, lunas, belumlunas, metodepengiriman;
        RelativeLayout LayoutOrder;

        public PesananViewRecHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            statusOrder = itemView.findViewById(R.id.Status);
            JumlahItem = itemView.findViewById(R.id.jumlahItem);
            tglOrder = itemView.findViewById(R.id.tglOrder);
            idOrder = itemView.findViewById(R.id.idOrder);
            namaCustomer = itemView.findViewById(R.id.namaCustomer);
            TotalProduk = itemView.findViewById(R.id.totalHarga);
            LayoutOrder = itemView.findViewById(R.id.layoutOrder);
            lunas = itemView.findViewById(R.id.statusLunas);
            belumlunas = itemView.findViewById(R.id.statusBelumLunas);
            metodepengiriman = itemView.findViewById(R.id.metodepengiriman);
        }
    }
}
