package com.mobile.admintelaten.Activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;


import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobile.admintelaten.Adapter.ItemAdapter;
import com.mobile.admintelaten.Model.Item;
import com.mobile.admintelaten.Model.Kategori;
import com.mobile.admintelaten.PrefManager;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    List<Item> itemList;
    RecyclerView recyclerView;
    PrefManager prefManager;
    String id_item, id_kategori;
    TextView txtUser;
    Boolean isFabOpen = false;
    FloatingActionButton fab, fab1, fab2;
    CardView card1, card2;
    Animation fab_open, fab_close, rotate_forward, rotate_backward, card_open, card_close;
    RelativeLayout layout_koneksi;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        prefManager = new PrefManager(this);

        layout_koneksi = findViewById(R.id.layout_koneksi);
        layout_koneksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data();
            }
        });

        data();

        binding.swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                data();
            }
        });

        txtUser = findViewById(R.id.txtUser);

        Intent i = new Intent(getIntent());
        id_item = String.valueOf(i.getIntExtra("id_item", 0));
        Log.d("id_item", "code : " + id_item);

        id_kategori = String.valueOf(i.getIntExtra("id_kategori", 0));
        Log.d("id_kategori", "code" + id_kategori);

        ///Floating Action Button

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        card1 = (CardView) findViewById(R.id.card1);
        card2 = (CardView) findViewById(R.id.card2);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_backward);
        card_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_open);
        card_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_close);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
            }
        });

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, InsertActivity.class);
                startActivity(i);
                finish();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, KategoriActivity.class);
                startActivity(i);
                finish();
            }
        });

        //show and hide while scrolling
        binding.recyclerViewMain.setOnTouchListener(new TranslateAnimation(this, fab));

        ///END FAB

        binding.btnNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.customer_nav:
                        Intent in = new Intent(MainActivity.this, CustomerActivity.class);
                        startActivity(in);
                        break;
                    case R.id.pesanan_nav:
                        Intent it = new Intent(MainActivity.this, PesananActivity.class);
                        startActivity(it);
                        break;
                    case  R.id.akun_nav:
                        Intent intent = new Intent(MainActivity.this, AkunActivity.class);
                        startActivity(intent);
                }
                return true;
            }
        });

        recyclerView = findViewById(R.id.recyclerViewMain);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();


        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

    }

    private void filter(String text) {
        ArrayList<Item> filteredList = new ArrayList<>();
        for (Item item : itemList) {
            if (item.getNama_item().toLowerCase().contains(text.toLowerCase()) || item.getNama_kategori().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        ItemAdapter adapter = new ItemAdapter(MainActivity.this, itemList);
        binding.recyclerViewMain.setAdapter(adapter);
        adapter.filterList(filteredList);

    }
    private void animateFAB() {
        /*jika fab dalam keadaan false*/
        if (isFabOpen) {
            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            card1.startAnimation(card_close);
            card2.startAnimation(card_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            /*jika dalam keadaan true*/
            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            card1.startAnimation(card_open);
            card2.startAnimation(card_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }

    private void data() {
        binding.progress.setVisibility(View.VISIBLE);
        binding.swipe.setRefreshing(true);
        layout_koneksi.setVisibility(View.GONE);
        AndroidNetworking.post(Server.site + "set_produk.php")
                .addBodyParameter("kode", String.valueOf(2))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            binding.progress.setVisibility(View.GONE);
                            binding.swipe.setRefreshing(false);
                            binding.fab.setVisibility(View.VISIBLE);
                            binding.searchBar.setVisibility(View.VISIBLE);
                            itemList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject item = null;
                                item = response.getJSONObject(i);
                                itemList.add(new Item(
                                        item.getInt("id_item"),
                                        item.getInt("id_kategori"),
                                        item.getString("nama_kategori"),
                                        item.getString("nama_item"),
                                        item.getInt("harga_item"),
                                        item.getInt("stock"),
                                        item.getString("foto_item")
                                ));
                            }
                            ItemAdapter adapter = new ItemAdapter(MainActivity.this, itemList);
                            recyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onError(ANError anError) {
//                        Log.d("tag", "onErrorResponse : " + anError);
                        layout_koneksi.setVisibility(View.VISIBLE);
                        binding.fab.setVisibility(View.GONE);
                        binding.progress.setVisibility(View.GONE);
                        binding.swipe.setRefreshing(false);
                        binding.searchBar.setVisibility(View.GONE);
                    }
                });
    }
}