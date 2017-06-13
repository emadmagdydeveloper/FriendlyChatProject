package com.example.omd.friendlychat.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.omd.friendlychat.Adapters.BlockAdapter;
import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Delta on 27/05/2017.
 */

public class Block_Fragment extends Fragment {
    ListView listView_Block;
    TextView block_text;
    Context mContext;
    FirebaseAuth mAuth;
    DatabaseReference dRef;
    //ProgressDialog mDialog;
    private RelativeLayout blockFragment_progressBar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.block_fragment,container,false);
        initView(view);
        Display_BlockedUsers(mAuth.getCurrentUser().getUid().toString());
        return view;
    }

    private void initView(View view) {
        mContext = view.getContext();
        listView_Block = (ListView) view.findViewById(R.id.listView_blockUsers);
        block_text = (TextView) view.findViewById(R.id.block_text);
        blockFragment_progressBar = (RelativeLayout) view.findViewById(R.id.blockFragment_progressBar);
        blockFragment_progressBar.setVisibility(View.VISIBLE);
        listView_Block.setVisibility(View.GONE);
        block_text.setVisibility(View.GONE);
       /* mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Loading blocked users .....");
        mDialog.show();*/
        ////////////////////////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();
    }
    public void Display_BlockedUsers(String myId)
    {
        DatabaseReference blockRef = dRef.child("Blocks").child(myId);
        blockRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null)
                {

                    List<String> usersKeys = new ArrayList<String>();
                    for (DataSnapshot ds :dataSnapshot.getChildren())
                    {
                        usersKeys.add(ds.getKey().toString());
                    }
                    getUsersData(usersKeys);
                }
                else
                    {

                        block_text.setVisibility(View.VISIBLE);
                        listView_Block.setVisibility(View.GONE);
                        blockFragment_progressBar.setVisibility(View.GONE);

                       // mDialog.dismiss();
                    }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUsersData(final List<String> usersKeys) {
        DatabaseReference usersRef = dRef.child("Users");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null)
                {
                    List<UserInformation> usersData = new ArrayList<>();
                    for (String key :usersKeys)
                    {
                        usersData.add(dataSnapshot.child(key).getValue(UserInformation.class));
                    }
                    if (usersData.size()==0)
                    {
                        block_text.setVisibility(View.VISIBLE);
                        listView_Block.setVisibility(View.GONE);
                        blockFragment_progressBar.setVisibility(View.GONE);
                       // mDialog.dismiss();
                    }
                    else {
                        block_text.setVisibility(View.GONE);
                        listView_Block.setVisibility(View.VISIBLE);
                        blockFragment_progressBar.setVisibility(View.GONE);
                        BlockAdapter adapter = new BlockAdapter(mContext, usersData);
                        listView_Block.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        //mDialog.dismiss();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
