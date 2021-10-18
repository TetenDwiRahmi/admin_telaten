package com.mobile.admintelaten.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.Activity.DetailActivity;
import com.mobile.admintelaten.Activity.UpdateActivity;
import com.mobile.admintelaten.Model.Item;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewRecHolder> {

    private Context context;
    private List<Item> itemList;

    public ItemAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewRecHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.show_item, null);
        return new ItemViewRecHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ItemViewRecHolder holder, final int position) {
        final Item item = itemList.get(position);
        holder.txtNamaItem.setText(item.getNama_item());
        holder.txtNamaKategori.setText(item.getNama_kategori());

        AndroidNetworking.post(Server.site + "get_variasi.php")
                .addBodyParameter("id_item", String.valueOf(item.getId_item()))
                .addBodyParameter("kode", String.valueOf(3))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equalsIgnoreCase("1")) {
                                Locale localeId = new Locale("in", "ID");
                                final NumberFormat rupiah = NumberFormat.getCurrencyInstance(localeId);
                                int a = response.getInt("minimum");//harga Minimum
                                int b = response.getInt("maximum");
                                int c = response.getInt("minStock");
                                int d = response.getInt("maxStock");
                                if (a != b && c != d) {
                                    holder.txtHargaItem.setText(rupiah.format(a) + " - " + rupiah.format(b));
                                    holder.txtStock.setText("Stock : " + c + " - " + d);
                                } else {
                                    holder.txtHargaItem.setText(rupiah.format(a));
                                    holder.txtStock.setText("Stock : " + c);
                                }
                            } else if (response.getString("kode").equalsIgnoreCase("2")) {
                                JSONObject data = response.getJSONObject("data");
                                Locale localeId = new Locale("in", "ID");
                                final NumberFormat rupiah = NumberFormat.getCurrencyInstance(localeId);
                                int hrg = Integer.parseInt(data.getString("harga_item"));
                                holder.txtHargaItem.setText(rupiah.format(hrg));
                                holder.txtStock.setText("Stock : " + data.getString("stock"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("tag", "error harga dan stock : " + anError);
                    }
                });

        holder.imageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, UpdateActivity.class);
                i.putExtra("id_item", item.getId_item());
                i.putExtra("id_kategori", item.getId_kategori());
                context.startActivity(i);
            }
        });
        holder.imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Perhatian")
                        .setMessage("Apakah Anda yakin ingin menghapus item ini?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                hapusdata(item.getId_item());
                                itemList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, itemList.size());
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Tidak", null)
                        .show();

            }
        });
        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailActivity.class);
                i.putExtra("id_item", item.getId_item());
                context.startActivity(i);
            }
        });

        //untuk mengambil gambar Item admintelaten di folder upload kemudian ditampilkan d
        Picasso.get().load(Server.site_foto + item.getFoto_item()).resize(172, 172).into(holder.image);
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void filterList(ArrayList<Item> itemListFull) {
        itemList = itemListFull;
        notifyDataSetChanged();
    }

    public void hapusdata(int id_item) {
        AndroidNetworking.post(Server.site + "set_produk.php")
                .addBodyParameter("id_item", id_item + "")
                .addBodyParameter("kode", String.valueOf(3))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("sukses", "code : " + response);
                            if (response.getString("status").equalsIgnoreCase("1")) {
                                Toast.makeText(context, "Item Dihapus", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d("eror", "code : " + anError);
                        Toast.makeText(context, "Gagal Menghapus Item", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    class ItemViewRecHolder extends RecyclerView.ViewHolder {
        TextView txtNamaItem, txtHargaItem, txtNamaKategori, txtStock;
        ImageView image, imageUpdate, imageDelete;
        CardView cardItem;

        public ItemViewRecHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            txtNamaItem = itemView.findViewById(R.id.txtNamaItem);
            txtHargaItem = itemView.findViewById(R.id.txtHargaItem);
            txtStock = itemView.findViewById(R.id.txtStockItem);
            txtNamaKategori = itemView.findViewById(R.id.txtKategoriItem);
            image = itemView.findViewById(R.id.imageItem);
            imageUpdate = itemView.findViewById(R.id.imageUpdate);
            imageDelete = itemView.findViewById(R.id.imageDelete);
            cardItem = itemView.findViewById(R.id.LayoutItem);
        }
    }
}
