package com.mobile.admintelaten.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobile.admintelaten.Model.Kategori;
import com.mobile.admintelaten.R;

import java.util.ArrayList;
import java.util.List;

public class PopUpKategoriAdapter extends RecyclerView.Adapter<PopUpKategoriAdapter.PopUpKategoriAdapterViewRecHolder> {
    private Context context;
    private List<Kategori> kategoriList;
    private int checkedPosistion = -1;

    public PopUpKategoriAdapter(Context context, List<Kategori> kategoriList) {
        this.context = context;
        this.kategoriList = kategoriList;
    }

    public void SetKategori (List<Kategori> kategoriList){
        this.kategoriList = new ArrayList<>();
        this.kategoriList = kategoriList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PopUpKategoriAdapter.PopUpKategoriAdapterViewRecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.show_popup_kategori, null);
        return new PopUpKategoriAdapter.PopUpKategoriAdapterViewRecHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopUpKategoriAdapter.PopUpKategoriAdapterViewRecHolder holder, int position) {
        holder.bind(kategoriList.get(position));
    }

    @Override
    public int getItemCount() {
        return kategoriList.size();
    }

    public class PopUpKategoriAdapterViewRecHolder extends RecyclerView.ViewHolder {
        TextView nmCategori;
        ImageView imageCeklis;

        public PopUpKategoriAdapterViewRecHolder(@NonNull View itemView) {
            super(itemView);
            nmCategori = itemView.findViewById(R.id.nmCategori);
            imageCeklis = itemView.findViewById(R.id.imageCeklis);
        }

        void bind(final Kategori kategoriList){
            if(checkedPosistion == -1){
                imageCeklis.setVisibility(View.GONE);
            }else {
                if(checkedPosistion == getAdapterPosition()){
                    imageCeklis.setVisibility(View.VISIBLE);
                }else {
                    imageCeklis.setVisibility(View.GONE);
                }
            }

            nmCategori.setText(kategoriList.getNama_kategori());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imageCeklis.setVisibility(View.VISIBLE);
                    if(checkedPosistion != getAdapterPosition()){
                        notifyItemChanged(checkedPosistion);
                        checkedPosistion = getAdapterPosition();
                    }

                }
            });

        }
    }


    public Kategori getSelected(){
        if(checkedPosistion != -1){
            return kategoriList.get(checkedPosistion);
        }
        return null;
    }
}
