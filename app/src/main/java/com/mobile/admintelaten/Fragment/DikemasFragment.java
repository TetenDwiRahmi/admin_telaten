package com.mobile.admintelaten.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.mobile.admintelaten.Adapter.PesananAdapter;
import com.mobile.admintelaten.Model.Pesanan;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DikemasFragment extends Fragment {
    List<Pesanan> pesananList;
    RecyclerView recyclerDikemas;
    SwipeRefreshLayout swipe;
    ImageView img_nodata;
    TextView txtNodata;
    RelativeLayout layout_koneksi;

    public DikemasFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pesananList = new ArrayList<>();

    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        row_pesanan();
        layout_koneksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_pesanan();
            }
        });
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                row_pesanan();
            }
        });
    }

    private void row_pesanan() {
        layout_koneksi.setVisibility(View.GONE);
        swipe.setRefreshing(true);
        AndroidNetworking.post(Server.site + "adm_order.php")
                .addBodyParameter("kode", String.valueOf(5))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if (response.length() >= 1) {
                            try {
                                swipe.setRefreshing(false);
                                pesananList.clear();
                                recyclerDikemas.setVisibility(View.VISIBLE);
                                img_nodata.setVisibility(View.GONE);
                                txtNodata.setVisibility(View.GONE);
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject d = response.getJSONObject(i);
                                    pesananList.add(new Pesanan(
                                            d.getString("id_order"),
                                            d.getInt("id_customer"),
                                            d.getString("tgl_order"),
                                            d.getString("status_order"),
                                            d.getString("nama_penerima"),
                                            d.getInt("total_bayar"),
                                            d.getInt("total_item"),
                                            d.getString("metode_pengiriman")
                                    ));

                                    Log.d("tag", "Total Item : " + d.getInt("total_item"));
                                }
                                PesananAdapter adapter = new PesananAdapter(getContext(), pesananList);
                                recyclerDikemas.setAdapter(adapter);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            swipe.setRefreshing(false);
                            recyclerDikemas.setVisibility(View.GONE);
                            img_nodata.setVisibility(View.VISIBLE);
                            txtNodata.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
//                        Log.d("tag", "error pesanan : " + anError);
                        layout_koneksi.setVisibility(View.VISIBLE);
                        swipe.setRefreshing(false);
                        recyclerDikemas.setVisibility(View.GONE);
                        img_nodata.setVisibility(View.GONE);
                        txtNodata.setVisibility(View.GONE);
                    }

                });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dikemas, container, false);

//        TextView textView = view.findViewById(R.id.text_view);
//        String sTitle = getArguments().getString("title");
//        textView.setText(sTitle);
        img_nodata = view.findViewById(R.id.img_nodata);
        txtNodata = view.findViewById(R.id.txtNodata);
        swipe = view.findViewById(R.id.swipe);
        layout_koneksi = view.findViewById(R.id.layout_koneksi);

        recyclerDikemas = (RecyclerView) view.findViewById(R.id.recyclerDikemas);
        PesananAdapter adapter = new PesananAdapter(getContext(), pesananList);
        recyclerDikemas.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerDikemas.setAdapter(adapter);
        return view;
    }
}