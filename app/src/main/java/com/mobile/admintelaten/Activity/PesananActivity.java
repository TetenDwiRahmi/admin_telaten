package com.mobile.admintelaten.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.mobile.admintelaten.Fragment.BatalFragment;
import com.mobile.admintelaten.Fragment.BelumBayarFragment;
import com.mobile.admintelaten.Fragment.DikemasFragment;
import com.mobile.admintelaten.Fragment.DikirimFragment;
import com.mobile.admintelaten.Fragment.SelesaiFragment;
import com.mobile.admintelaten.databinding.ActivityPesananBinding;

import java.util.ArrayList;
import java.util.List;

public class PesananActivity extends AppCompatActivity {
    private ActivityPesananBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPesananBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AndroidNetworking.initialize(this);

        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ArrayList<String> arrayList = new ArrayList<>();

        arrayList.add("Belum Bayar");
        arrayList.add("Dikirim");
        arrayList.add("Selesai");
        arrayList.add("Dibatalkan");

        viewPager(binding.viewPager, arrayList);
        binding.tabs.setupWithViewPager(binding.viewPager);

    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(PesananActivity.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    private void viewPager(ViewPager viewPager, ArrayList<String> arrayList) {
        FragmenAdapter adapter = new FragmenAdapter(getSupportFragmentManager());

        BelumBayarFragment belumBayarFragment = new BelumBayarFragment();
        adapter.addFragment(belumBayarFragment, "Belum Bayar");
        belumBayarFragment = new BelumBayarFragment();

        DikemasFragment dikemasFragment = new DikemasFragment();
        adapter.addFragment(dikemasFragment, "Dikemas");
        dikemasFragment = new DikemasFragment();

        DikirimFragment dikirimFragment = new DikirimFragment();
        adapter.addFragment(dikirimFragment, "Dikirim");
        dikirimFragment = new DikirimFragment();

        SelesaiFragment selesaiFragment = new SelesaiFragment();
        adapter.addFragment(selesaiFragment, "Selesai");
        selesaiFragment = new SelesaiFragment();

        BatalFragment batalFragment = new BatalFragment();
        adapter.addFragment(batalFragment, "Dibatalkan");
        batalFragment = new BatalFragment();

        viewPager.setAdapter(adapter);
    }


    private class FragmenAdapter extends FragmentPagerAdapter {
        ArrayList<String> arrayList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();

        public void addFragment(Fragment fragment, String title) {
            arrayList.add(title);
            fragmentList.add(fragment);
        }

        public FragmenAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return arrayList.get(position);
        }
    }
}