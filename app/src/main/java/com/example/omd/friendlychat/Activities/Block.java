package com.example.omd.friendlychat.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.omd.friendlychat.Fragments.Block_Fragment;
import com.example.omd.friendlychat.R;

public class Block extends AppCompatActivity {

    Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        getSupportFragmentManager().beginTransaction().add(R.id.block_Fragment_container,new Block_Fragment()).commit();
        initView();
    }

    private void initView() {
        mToolbar = (Toolbar) findViewById(R.id.blockToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
