package com.example.milab_app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.io.File;
import java.util.ArrayList;

public class CustomGalleryActivity extends AppCompatActivity {

    ArrayList<String> f = new ArrayList<>();
    File[] listFile;
    private final String folderName = "Sugar Friendly";
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        getFromSdcard();
        viewPager = findViewById(R.id.viewPagerMain);
        viewPagerAdapter = new ViewPagerAdapter(this, f);
        viewPager.setAdapter(viewPagerAdapter);
    }

    public void getFromSdcard() {
        File file = new File(android.os.Environment.getExternalStorageDirectory(), folderName);
        if (file.isDirectory()) {
            listFile = file.listFiles();
            assert listFile != null;
            for (File value : listFile) {
                f.add(value.getAbsolutePath());
            }
        }
    }
}
