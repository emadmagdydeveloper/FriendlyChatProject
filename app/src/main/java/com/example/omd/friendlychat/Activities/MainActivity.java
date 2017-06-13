package com.example.omd.friendlychat.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omd.friendlychat.Editing.Edit;
import com.example.omd.friendlychat.Editing.MyData;
import com.example.omd.friendlychat.Fragments.AddFriendsFragment;
import com.example.omd.friendlychat.Fragments.CallFragment;
import com.example.omd.friendlychat.Fragments.ChatFragment;
import com.example.omd.friendlychat.Fragments.FriendRequestFragment;
import com.example.omd.friendlychat.Fragments.MyFriendFragment;
import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.Notifications_Read_Model;
import com.example.omd.friendlychat.models.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    RelativeLayout pb_Container;
    LinearLayout myFriend_Container;
    private CircleImageView my_image;
    private TextView my_name;
    private TextView mfriendsmenu;
    private LinearLayout myProfile;
    private TabLayout mtab;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private TextView notf_counter;
    private ImageView notf_img;
    Toolbar toolbar;

    ImageView backBtn;
    DatabaseReference mdref;
    FirebaseAuth mAuth ;
    DatabaseReference dRef;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, new ChatFragment()).commit();
        Edit edit = new Edit(MainActivity.this);
        edit.Update_All_Notifications();
        getNotification_count();

        //////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();
        /////////////////////////////////////////
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /////////////////////////////////////////
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.notification_view,null);
        notf_counter = (TextView) view.findViewById(R.id.text_Counter);
        notf_img = (ImageView) view.findViewById(R.id.notification_img);
        /////////////////////////////////////////
        myFriend_Container = (LinearLayout) findViewById(R.id.myFriend_Container);
        pb_Container = (RelativeLayout) findViewById(R.id.main_myFriend_Container);
        ////////////////////////////////////////
        my_image = (CircleImageView) findViewById(R.id.myfriendfragment_userIcon);
        my_name = (TextView) findViewById(R.id.myfriendfragment_userName);
        myProfile = (LinearLayout) findViewById(R.id.myData_section);
        Display_userData(my_image,my_name);

        backBtn = (ImageView) findViewById(R.id.backBtn);
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mfriendsmenu = (TextView) findViewById(R.id.myFriendsmenu);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mNavigationView = (NavigationView) findViewById(R.id.navView);

        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        mtab = (TabLayout) findViewById(R.id.tabLayout);
        mtab.addTab(mtab.newTab().setIcon(R.drawable.s_home).setTag("home"));
        mtab.addTab(mtab.newTab().setIcon(R.drawable.call).setTag("call"));
        mtab.addTab(mtab.newTab().setIcon(R.drawable.addfriends).setTag("addfriend"));


        mtab.addTab(mtab.newTab().setCustomView(view).setTag("notification"));
       // mtab.addTab(mtab.newTab().setIcon(R.drawable.notification_blue).setTag("notification"));
        //mtab.setTabGravity(TabLayout.GRAVITY_FILL);
        mtab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getTag().equals("call")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new CallFragment()).commit();
                    tab.setIcon(R.drawable.s_call);
                    notf_img.setImageResource(R.drawable.notification_blue);
                    getSupportActionBar().setTitle("Call");



                } else if (tab.getTag().equals("home")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new ChatFragment()).commit();
                    tab.setIcon(R.drawable.s_home);
                    notf_img.setImageResource(R.drawable.notification_blue);
                    getSupportActionBar().setTitle("FriendlyChat");



                } else if (tab.getTag().equals("addfriend")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new AddFriendsFragment()).commit();
                    tab.setIcon(R.drawable.s_addfriends);
                    notf_img.setImageResource(R.drawable.notification_blue);
                    getSupportActionBar().setTitle("Add Friend");
                }
                else if (tab.getTag().equals("notification"))
                {

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new FriendRequestFragment()).commit();
                    notf_img.setImageResource(R.drawable.notification_white);
                    getSupportActionBar().setTitle("Notifications");
                    Read_ALL_Notifications();


                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getTag().equals("call")) {
                    tab.setIcon(R.drawable.call);
                } else if (tab.getTag().equals("home")) {
                    tab.setIcon(R.drawable.home);

                } else if (tab.getTag().equals("addfriend")) {
                    tab.setIcon(R.drawable.addfriends);

                }



            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {


            }
        });

        mfriendsmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mDrawerLayout.openDrawer(mNavigationView);
                pb_Container.setVisibility(View.VISIBLE);
                myFriend_Container.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction().add(R.id.myfriendfragmentcontainer, new MyFriendFragment()).commit();
                getSupportFragmentManager().beginTransaction().replace(R.id.myfriendfragmentcontainer, new MyFriendFragment()).commit();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawerLayout.closeDrawer(mNavigationView);
                getSupportFragmentManager().beginTransaction().remove(new MyFriendFragment()).commit();

            }
        });

        SetUpIntent();

    }



    @Override
    protected void onStart()
    {
        super.onStart();
        mdref = FirebaseDatabase.getInstance().getReference().child("State").child("Offline").child(mAuth.getCurrentUser().getUid().toString());
        deleteChild_From_Offline(mdref.getRef());
        mdref = FirebaseDatabase.getInstance().getReference().child("State").child("Online").child(mAuth.getCurrentUser().getUid().toString());
        mdref.setValue(true);
        show_Notification_Numbers(notf_counter);

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }
    private void deleteChild_From_Offline(DatabaseReference ref)
    {
        ref.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
    @Override
    protected void onPause()
    {
        super.onPause();
        mDrawerLayout.closeDrawer(mNavigationView);
        getSupportFragmentManager().beginTransaction().remove(new MyFriendFragment()).commit();

    }
    private void Display_userData(final ImageView imageView, final TextView t_name)
    {
        DatabaseReference userRef = dRef.child("Users").child(mAuth.getCurrentUser().getUid().toString());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {
                    UserInformation info = dataSnapshot.getValue(UserInformation.class);
                    if (info !=null){
                    Picasso.with(MainActivity.this).load(info.getUserImageUri().toString()).into(imageView);
                    t_name.setText(info.getUserName().toString());


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void SetUpIntent()



    {
        myProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,profile.class));
            }
        });

    }
    private void show_Notification_Numbers(final TextView notf_numbers)
    {
            DatabaseReference NotfRef = FirebaseDatabase.getInstance().getReference().child("Notifications_count").child(mAuth.getCurrentUser().getUid().toString());
            NotfRef.child("Count").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        int count = dataSnapshot.getValue(Integer.class);
                        if (count > 9) {

                            notf_numbers.setText("+9");
                            notf_numbers.setVisibility(View.VISIBLE);
                        } else if (count <= 9 && count > 0) {
                            notf_numbers.setVisibility(View.VISIBLE);
                            notf_numbers.setText(String.valueOf(count));
                        } else if (count == 0) {
                            notf_numbers.setVisibility(View.GONE);
                        }
                    } else {

                        notf_numbers.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




    }
    private void getNotification_count()
    {
        DatabaseReference NotfRef = FirebaseDatabase.getInstance().getReference().child("Notifications_readed");
        NotfRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null){

                    if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid().toString())) {
                        int notf_numbers = 0;
                        for (DataSnapshot ds : dataSnapshot.child(MyData.getMyId().toString()).getChildren()) {
                            Notifications_Read_Model mRead_model = ds.getValue(Notifications_Read_Model.class);
                            if (mRead_model.isRead() == false) {
                                notf_numbers++;
                            }
                            Toast.makeText(MainActivity.this, notf_numbers + "", Toast.LENGTH_SHORT).show();
                            DatabaseReference NotfCountRef = FirebaseDatabase.getInstance().getReference().child("Notifications_count");
                            NotfCountRef.child(MyData.getMyId().toString()).child("Count").setValue(notf_numbers);
                        }

                    }

                }
                else
                    {
                         DatabaseReference NotfCountRef = FirebaseDatabase.getInstance().getReference().child("Notifications_count");
                        NotfCountRef.child(mAuth.getCurrentUser().getUid().toString()).child("Count").setValue(0);
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }



    private void Read_ALL_Notifications()
    {
        DatabaseReference NotfReadRef = FirebaseDatabase.getInstance().getReference().child("Notifications_readed");
        NotfReadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null){
                    if (dataSnapshot.hasChild(mAuth.getCurrentUser().getUid().toString()))
                    {
                        DatabaseReference NotfreadRef = FirebaseDatabase.getInstance().getReference().child("Notifications_readed").child(mAuth.getCurrentUser().getUid().toString());
                        NotfreadRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                for (DataSnapshot ds :dataSnapshot.getChildren())
                                {
                                    Notifications_Read_Model read_model = ds.getValue(Notifications_Read_Model.class);
                                    if (read_model.isRead()==false)
                                    {
                                        DatabaseReference NotfRead_Ref = FirebaseDatabase.getInstance().getReference().child("Notifications_readed");
                                        NotfRead_Ref.child(MyData.getMyId().toString()).child(ds.getKey().toString()).child("read").setValue(true);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}

