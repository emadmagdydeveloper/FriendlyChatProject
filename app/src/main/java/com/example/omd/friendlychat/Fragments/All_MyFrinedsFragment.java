package com.example.omd.friendlychat.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.omd.friendlychat.Adapters.AllmyFriends_Adapter;
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
 * Created by Delta on 18/05/2017.
 */

public class All_MyFrinedsFragment extends Fragment{
    private ListView allMyfriends_list;
    private RelativeLayout allMyFriends_progressBar_Container;
    private TextView respons;
    private FirebaseAuth mAuth;
    private DatabaseReference dRef;
    private List<String> friendKeys;
    List<Object> friendInformation;
    Context mContext;
    Handler handler;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_myfriendsfragment,container,false);
        initView(view);
        getDataFromIntent();
        return view;

    }

    private void initView(View view)
    {
        mContext =view.getContext();
        allMyfriends_list = (ListView) view.findViewById(R.id.allMyFriends_list);
        respons = (TextView) view.findViewById(R.id.respons);
        allMyFriends_progressBar_Container = (RelativeLayout) view.findViewById(R.id.allMyFriends_progressBar_Container);
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();



    }
    private void getFriendsKeys(String id, final int num)
    {
        DatabaseReference dRef_Friends = dRef.child("Friends").child(id);
        dRef_Friends.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendKeys = new ArrayList<>();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    friendKeys.add(ds.getKey().toString());
                }
                getUserData(friendKeys,num);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getUserData(final List<String>keys, final int num)
    {

        DatabaseReference dRef_Users = dRef.child("Users");
        dRef_Users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendInformation = new ArrayList<>();
                for (String key:keys) {
                    UserInformation info = dataSnapshot.child(key).getValue(UserInformation.class);
                    friendInformation.add(info);
                }

                handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        if (friendInformation.size()==0)
                        {
                            allMyFriends_progressBar_Container.setVisibility(View.GONE);
                            respons.setVisibility(View.VISIBLE);
                            allMyfriends_list.setVisibility(View.GONE);

                        }
                        else if (friendInformation.size()>0)
                        {
                            allMyFriends_progressBar_Container.setVisibility(View.GONE);
                            respons.setVisibility(View.GONE);
                            allMyfriends_list.setVisibility(View.VISIBLE);
                            AllmyFriends_Adapter mAdapter = new AllmyFriends_Adapter(mContext,friendInformation,num);
                            allMyfriends_list.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                            setListViewHeightBasedOnChildren(allMyfriends_list);
                        }

                    }
                },3000);



            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getDataFromIntent(){
        Intent intent =getActivity().getIntent();
        if (intent != null){
            UserInformation information = (UserInformation) intent.getSerializableExtra("friendData");

            if (information != null)
            {

                getFriendsKeys(information.getUserId().toString(),2);

            }
            else {
                getFriendsKeys(mAuth.getCurrentUser().getUid().toString(),1);

            }

        }

    }
    public static void setListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight=0;
        View view = null;

        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            view = listAdapter.getView(i, view, listView);

            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        ViewGroup.LayoutParams.MATCH_PARENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();

        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + ((listView.getDividerHeight()) * (listAdapter.getCount()));

        listView.setLayoutParams(params);
        listView.requestLayout();

    }

    @Override
    public void onStart() {
        super.onStart();
        allMyFriends_progressBar_Container.setVisibility(View.VISIBLE);
    }
}
