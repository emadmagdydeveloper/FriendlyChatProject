package com.example.omd.friendlychat.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.omd.friendlychat.Editing.Edit;
import com.example.omd.friendlychat.Fragments.About_Me_Fragment;
import com.example.omd.friendlychat.Fragments.All_MyFrinedsFragment;
import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class profile extends AppCompatActivity {
    private static final int RC = 1;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView choose_myImage,myImage;
    private LinearLayout ll;
    private TabLayout mTab;
    private Toolbar mToolbar ;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private RelativeLayout pb_container;
    private CoordinatorLayout container;
    private RelativeLayout profile_progressBar_image_Container;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initView();
        initTab();
        getMyPhoto();
        getIntentData();
        getSupportFragmentManager().beginTransaction().add(R.id.profile_fragmentContainer,new About_Me_Fragment(),"about_me_Fragment").commit();

    }

    private void initTab()
    {
        mTab.addTab(mTab.newTab().setText("about ").setTag("about"));
        mTab.addTab(mTab.newTab().setText("Friends").setTag("friends"));
        mTab.setTabGravity(TabLayout.GRAVITY_FILL);
        mTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getTag().equals("about")){
                    tab.select();
                   getSupportFragmentManager().beginTransaction().replace(R.id.profile_fragmentContainer,new About_Me_Fragment(),"about_me_Fragment").commit();

                }
                else if (tab.getTag().equals("friends")){
                    tab.select();
                   getSupportFragmentManager().beginTransaction().replace(R.id.profile_fragmentContainer,new All_MyFrinedsFragment(),"all_myFriend_Fragment").commit();

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }

    private void initView()
    {

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.cToolBar);
        mToolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        myImage = (ImageView) findViewById(R.id.myImage);
        ll = (LinearLayout) findViewById(R.id.ll);
        choose_myImage = (ImageView) findViewById(R.id.choose_myImage);
        mTab = (TabLayout) findViewById(R.id.profile_tab);

         //////////////////////////////////////////////////
        pb_container = (RelativeLayout) findViewById(R.id.profile_progressBar_Container);
        container = (CoordinatorLayout) findViewById(R.id.profile_Container);
        profile_progressBar_image_Container = (RelativeLayout) findViewById(R.id.profile_progressBar_image_Container);
        //////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();

    }
    private void getMyPhoto()
    {
        choose_myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,RC);
            }
        });
    }
    private void LoadMyPhoto_FromFirebase(String id)
    {

        DatabaseReference userRef = dRef.child("Users").child(id);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() !=null) {
                    UserInformation info = dataSnapshot.getValue(UserInformation.class);
                    Picasso.with(profile.this).load(info.getUserImageUri().toString()).into(myImage);
                    collapsingToolbarLayout.setTitle(info.getUserName().toString());
                    pb_container.setVisibility(View.GONE);
                    container.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            profile_progressBar_image_Container.setVisibility(View.GONE);
                        }
                    },1000);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getIntentData()
    {
        Intent intent = getIntent();
        if (intent !=null)
        {
            UserInformation info = (UserInformation) intent.getSerializableExtra("friendData");
            if (info !=null) {
                pb_container.setVisibility(View.VISIBLE);
                container.setVisibility(View.GONE);
                ll.setVisibility(View.GONE);
                LoadMyPhoto_FromFirebase(info.getUserId().toString());

            }
            else
            {
                pb_container.setVisibility(View.VISIBLE);
                container.setVisibility(View.GONE);
                LoadMyPhoto_FromFirebase(mAuth.getCurrentUser().getUid().toString());

            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            if (requestCode ==RC && resultCode ==RESULT_OK){
                Uri uri = data.getData();
                Picasso.with(profile.this).load(uri).into(myImage);
                Edit edit = new Edit(profile.this);
                edit.Edit_Image(mAuth.getCurrentUser().getUid().toString(),uri.toString());
                pb_container.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);

            }
            else {
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        profile_progressBar_image_Container.setVisibility(View.VISIBLE);
        pb_container.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
    }
}