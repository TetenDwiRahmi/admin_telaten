package com.mobile.admintelaten.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.admintelaten.Interface.InterfaceKategoriVariasi;
import com.mobile.admintelaten.Model.Kategori;
import com.mobile.admintelaten.R;

import java.util.ArrayList;
import java.util.List;

public class ProdukKategoriAdapter extends RecyclerView.Adapter <ProdukKategoriAdapter.KategoriViewRecHolder> {

    private Context context;
    private List<Kategori> kategoriList;
    private int checkedPosistion = -1;
    InterfaceKategoriVariasi interfaceKategoriVariasi;

    public ProdukKategoriAdapter(List<Kategori> kategoriList, InterfaceKategoriVariasi interfaceKategoriVariasi) {
        this.kategoriList = kategoriList;
        this.interfaceKategoriVariasi = interfaceKategoriVariasi;
    }

    @NonNull
    @Override
    public KategoriViewRecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_produk_kategori, null);
        return new ProdukKategoriAdapter.KategoriViewRecHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final KategoriViewRecHolder holder, final int position) {
        final Kategori Kategori = kategoriList.get(position);
        holder.NamaKategori.setText(Kategori.getNama_kategori());
        holder.NamaKategori.setOnCheckedChangeListener(null);
        holder.NamaKategori.setChecked(checkedPosistion == position);
        holder.NamaKategori.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkedPosistion = position;
                interfaceKategoriVariasi.KategoriTerpilih(Kategori.getId_kategori());
            }
        });
    }

    @Override
    public int getItemCount() {
        return kategoriList.size();
    }

    public void filterList(ArrayList<Kategori> KategoriListFull) {
        kategoriList = KategoriListFull;
        notifyDataSetChanged();
    }


    class KategoriViewRecHolder extends RecyclerView.ViewHolder {
        RadioButton NamaKategori;

        public KategoriViewRecHolder(@NonNull View KategoriView) {
            super(KategoriView);
            context = KategoriView.getContext();
            NamaKategori = KategoriView.findViewById(R.id.NamaKategori);

            NamaKategori.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkedPosistion = getAdapterPosition();
                    notifyDataSetChanged();
//
//                    Toast.makeText(context, "Variasi Anda : " + variasi.getText(), Toast.LENGTH_SHORT).show();
//                    Log.d("tag", "Variasi anda : " + variasi.getText());

                }
            });
        }
    }
}
