package com.mobile.admintelaten.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Model.BarangDipesan;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BarangDipesanAdapter extends RecyclerView.Adapter<BarangDipesanAdapter.BarangDipesanViewRecHolder> {
    private List<BarangDipesan> barangDipesanList;
    private Context context;

    public BarangDipesanAdapter(List<BarangDipesan> barangDipesanList, Context context) {
        this.barangDipesanList = barangDipesanList;
        this.context = context;
    }

    @NonNull
    @Override
    public BarangDipesanAdapter.BarangDipesanViewRecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_barang_pesanan, null);
        return new BarangDipesanAdapter.BarangDipesanViewRecHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final BarangDipesanAdapter.BarangDipesanViewRecHolder holder, int position) {
        final BarangDipesan bp = barangDipesanList.get(position);
        holder.NamaItemCK.setText(bp.getNama_item());
        Locale localeId = new Locale("in", "ID");
        final NumberFormat rupiah = NumberFormat.getCurrencyInstance(localeId);
        holder.HargaItemCK.setText(rupiah.format(bp.getHarga_item()));
        holder.JumlahItemCK.setText(bp.getBanyak_item() + " x ");
        holder.Totperitem.setText(rupiah.format(bp.getTot_peritem()));
        Picasso.get().load(Server.site_foto + bp.getFoto_item()).into(holder.FotoItemCK);

        if(bp.getVariasi().equals("Tidak ada")){
            holder.VariasiCK.setVisibility(View.GONE);
        }
        else {
            holder.VariasiCK.setVisibility(View.VISIBLE);
            holder.VariasiCK.setText("Variasi : " + bp.getVariasi());
        }
    }

    @Override
    public int getItemCount() {
        return barangDipesanList.size();
    }

    public class BarangDipesanViewRecHolder extends RecyclerView.ViewHolder {
        ImageView FotoItemCK;
        TextView NamaItemCK, HargaItemCK, JumlahItemCK, VariasiCK, Totperitem;
        public BarangDipesanViewRecHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            FotoItemCK = itemView.findViewById(R.id.FotoItemCK);
            NamaItemCK = itemView.findViewById(R.id.NamaItemCK);
            HargaItemCK = itemView.findViewById(R.id.HargaItemCK);
            JumlahItemCK = itemView.findViewById(R.id.JumlahItemCK);
            VariasiCK = itemView.findViewById(R.id.VariasiCK);
            Totperitem = itemView.findViewById(R.id.total);
        }
    }
}
