package com.example.omd.friendlychat.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.omd.friendlychat.Editing.Edit;
import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.StatusModel;
import com.example.omd.friendlychat.models.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Delta on 18/05/2017.
 */

public class About_Me_Fragment extends Fragment {
    private TextView profile_myName,profile_myEmail,profile_myPhone;
    private TextView myStatus;
    private ImageView edit_myStatus;
    private ImageView edit_myName;
    private ImageView edit_myEmail;
    private ImageView edit_myPhone;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private Context mContext;
    private RelativeLayout Status_progressBar_Container,About_progressBar_Container;
    private LinearLayout Status_Container,About_Container;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_me,container,false);
        initView(view);
        getDataFromIntent();
        EditMy_Status(mAuth.getCurrentUser().getUid().toString());
        EditMy_Name(mAuth.getCurrentUser().getUid().toString());
        EditMy_Phone(mAuth.getCurrentUser().getUid().toString());
        return view;
    }

    private void initView(View view)
    {
        mContext = view.getContext();
        profile_myName = (TextView) view.findViewById(R.id.profile_myName);
        profile_myEmail = (TextView) view.findViewById(R.id.profile_myEmail);
        profile_myPhone = (TextView) view.findViewById(R.id.profile_myPhone);
        myStatus = (TextView) view.findViewById(R.id.myStatus);
        edit_myStatus = (ImageView) view.findViewById(R.id.editmyStatus);
        edit_myName = (ImageView) view.findViewById(R.id.editmyName);
        edit_myEmail = (ImageView) view.findViewById(R.id.editmyEmail);
        edit_myPhone = (ImageView) view.findViewById(R.id.editmyPhone);
        ////////////////////////////////////////////////////////////////////////
        Status_progressBar_Container = (RelativeLayout) view.findViewById(R.id.Status_progressBar_Container);
        About_progressBar_Container = (RelativeLayout) view.findViewById(R.id.About_progressBar_Container);
        Status_Container = (LinearLayout) view.findViewById(R.id.Status_Container);
        About_Container = (LinearLayout) view.findViewById(R.id.About_Container);
        ////////////////////////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();
    }
    private void LoadMyData_FromFirebase(String id)
    {
        DatabaseReference userRef = dRef.child("Users").child(id);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInformation info = dataSnapshot.getValue(UserInformation.class);
                profile_myName.setText(info.getUserName().toString());
                profile_myEmail.setText(info.getUserEmail().toString());
                profile_myPhone.setText(info.getUserPhone());
                About_progressBar_Container.setVisibility(View.GONE);
                About_Container.setVisibility(View.VISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void EditMy_Status(final String myId)
    {
        edit_myStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Edit edit = new Edit(mContext);
                edit.Edit_status(myId,myStatus);
                edit.flag=true;
            }

        });


    }
    private void EditMy_Name(final String myId)
    {
        edit_myName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Edit edit = new Edit(mContext);
                edit.Edit_name(myId,profile_myName);
            }

        });


    }
    private void EditMy_Phone(final String myId)
    {
        edit_myPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Edit edit = new Edit(mContext);
                edit.Edit_phone(myId,profile_myPhone);
            }

        });


    }
    private void Display_MyStatus(String id)
    {
        DatabaseReference statusRef = dRef.child("Status").child(id);
        statusRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                if (!dataSnapshot.equals(null)) {
                    StatusModel mStatus = dataSnapshot.getValue(StatusModel.class);
                    if (mStatus !=null)
                    {
                        String m_Status =mStatus.getStatus().toString();
                        if (!m_Status.equals(null)&&!m_Status.isEmpty())
                        {
                            myStatus.setText(m_Status);
                            Status_progressBar_Container.setVisibility(View.GONE);
                            Status_Container.setVisibility(View.VISIBLE);


                        }else
                            {
                                Status_progressBar_Container.setVisibility(View.GONE);
                                Status_Container.setVisibility(View.VISIBLE);
                            }
                    }



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void getDataFromIntent()
    {
        Intent intent = getActivity().getIntent();
        if (intent != null)
        {
            UserInformation information = (UserInformation) intent.getSerializableExtra("friendData");
            if (information != null)
            {
                Status_progressBar_Container.setVisibility(View.VISIBLE);
                About_progressBar_Container.setVisibility(View.VISIBLE);
                Status_Container.setVisibility(View.INVISIBLE);
                About_Container.setVisibility(View.INVISIBLE);


                edit_myEmail.setVisibility(View.INVISIBLE);
                edit_myName.setVisibility(View.INVISIBLE);
                edit_myPhone.setVisibility(View.INVISIBLE);
                edit_myStatus.setVisibility(View.INVISIBLE);
                Display_MyStatus(information.getUserId().toString());
                LoadMyData_FromFirebase(information.getUserId().toString());


            }
            else
            {

                Status_progressBar_Container.setVisibility(View.VISIBLE);
                About_progressBar_Container.setVisibility(View.VISIBLE);
                Status_Container.setVisibility(View.INVISIBLE);
                About_Container.setVisibility(View.INVISIBLE);

                LoadMyData_FromFirebase(mAuth.getCurrentUser().getUid().toString());
                Display_MyStatus(mAuth.getCurrentUser().getUid().toString());



            }

        }





    }


}
