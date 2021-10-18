package com.mobile.admintelaten.Activity;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.mobile.admintelaten.R;
import com.mobile.admintelaten.Server.Server;
import com.mobile.admintelaten.databinding.ActivityBuktiBinding;
import com.mobile.admintelaten.databinding.ActivityDetailOrderBinding;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class BuktiActivity extends AppCompatActivity {
    private ActivityBuktiBinding binding;
    String id_order;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuktiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        Intent i = new Intent(getIntent());
        id_order = i.getStringExtra("id_order");
        getBukti();
    }

    private void getBukti() {
        AndroidNetworking.post(Server.site + "set_order.php")
                .addBodyParameter("id_order", id_order)
                .addBodyParameter("kode", String.valueOf(8))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("kode").equals("1")) {
                                final String foto = response.getString("bukti");
                                Log.d("tag", "Bukti : " + foto);

                                Picasso.get().load(Server.site_bukti_tf + foto).into(binding.bukti);

                            }
                    } catch(
                    JSONException e)

                    {
                        e.printStackTrace();
                    }
                }

        @Override
        public void onError (ANError anError){
            Log.d("tag", "error bukti : " + anError);
        }
    });
}
}