package com.example.omd.friendlychat.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.omd.friendlychat.Adapters.FriendRequest_Adapter;
import com.example.omd.friendlychat.Adapters.Notifications_Adapter;
import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.Notifications_Model;
import com.example.omd.friendlychat.models.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Delta on 08/06/2017.
 */

public class FriendRequest_notifications_Fragment extends Fragment {
    Context mContext;
    private ListView mfriendRequest_ListView,notifications_listView;
    private RelativeLayout friendRequest_progressBar_Container,notifications_progressBar_Container;
    private TextView noFriendrequest_txt,nonotification_txt,see_all_FriendRequest,see_all_notifications;
    private AppBarLayout mAppBar;
    FirebaseAuth mAuth;
    DatabaseReference dRef;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.friendrequest_notifications,container,false);
        init_View(view);
        friendRequest_progressBar_Container.setVisibility(View.VISIBLE);
        notifications_progressBar_Container.setVisibility(View.VISIBLE);
        getUsers_Keys_From_FriendRequest();
        getNotifications();
        ShowAll_FriendRequests(see_all_FriendRequest,mAppBar);
        ShowAll_Notifications(see_all_notifications,mAppBar);
        return view;
    }




    private void init_View(View view) {
        mContext = view.getContext();
        mfriendRequest_ListView = (ListView) view.findViewById(R.id.FriendRequest_listView);
        notifications_listView = (ListView) view.findViewById(R.id.notifications_listView);
        mAppBar = (AppBarLayout) getActivity().findViewById(R.id.main_AppBar);
        see_all_FriendRequest = (TextView) view.findViewById(R.id.see_all_FriendRequest);
        see_all_notifications = (TextView) view.findViewById(R.id.see_all_notifications);
        nonotification_txt = (TextView) view.findViewById(R.id.nonotification_txt);
        noFriendrequest_txt = (TextView) view.findViewById(R.id.noFriendrequest_txt);
        friendRequest_progressBar_Container = (RelativeLayout) view.findViewById(R.id.friendRequest_progressBar_Container);
        notifications_progressBar_Container = (RelativeLayout) view.findViewById(R.id.notifications_progressBar_Container);
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();
    }
    private void getUsers_Keys_From_FriendRequest()
    {

        DatabaseReference friendRequest_Ref1 = dRef.child("FriendRequest").child(mAuth.getCurrentUser().getUid().toString());
        friendRequest_Ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() !=null)
                {
                    if (dataSnapshot.getChildrenCount()>3)
                    {
                        see_all_FriendRequest.setVisibility(View.VISIBLE);

                    }
                    else if (dataSnapshot.getChildrenCount()==0)
                    {
                        see_all_FriendRequest.setVisibility(View.GONE);

                    }
                }
                else
                {

                    see_all_FriendRequest.setVisibility(View.GONE);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Query friendRequest_Ref = dRef.child("FriendRequest").child(mAuth.getCurrentUser().getUid().toString()).limitToLast(3);

        //DatabaseReference friendRequest_Ref = dRef.child("FriendRequest").child(mAuth.getCurrentUser().getUid().toString());
        friendRequest_Ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String>keys = new ArrayList<String>();
                if (dataSnapshot.getValue() !=null)
                {
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        keys.add(ds.getKey().toString());
                    }


                    List<String> inverse_list = new ArrayList<String>();
                    for (int invers =keys.size()-1;invers>=0;invers--)
                    {
                        inverse_list.add(keys.get(invers));
                    }
                    getUser_Data(inverse_list);

                }
                else
                    {
                        friendRequest_progressBar_Container.setVisibility(View.GONE);
                        noFriendrequest_txt.setVisibility(View.VISIBLE);
                        mfriendRequest_ListView.setVisibility(View.GONE);
                        see_all_FriendRequest.setVisibility(View.GONE);


                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getUser_Data(final List<String> keys)
    {
        DatabaseReference userRef = dRef.child("Users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() !=null)
                {
                    List<UserInformation> user_Data = new ArrayList<UserInformation>();
                    for (String key :keys)
                    {
                        UserInformation info = dataSnapshot.child(key).getValue(UserInformation.class);
                        user_Data.add(info);
                    }


                    if (user_Data.size()==0)
                    {
                        friendRequest_progressBar_Container.setVisibility(View.GONE);
                        noFriendrequest_txt.setVisibility(View.VISIBLE);
                        see_all_FriendRequest.setVisibility(View.GONE);
                    }
                    else if (user_Data.size()>0)
                    {
                        FriendRequest_Adapter adapter = new FriendRequest_Adapter(user_Data,mContext);
                        mfriendRequest_ListView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(mfriendRequest_ListView);
                        friendRequest_progressBar_Container.setVisibility(View.GONE);
                        noFriendrequest_txt.setVisibility(View.GONE);

                    }



                }else
                    {


                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getNotifications()
    {
        DatabaseReference friendRequest_Ref1 = dRef.child("Notifications").child(mAuth.getCurrentUser().getUid().toString());
        friendRequest_Ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() !=null)
                {
                    if (dataSnapshot.getChildrenCount()>5)
                    {
                        see_all_notifications.setVisibility(View.VISIBLE);

                    }
                    else if (dataSnapshot.getChildrenCount()==0)
                    {
                        see_all_notifications.setVisibility(View.GONE);

                    }
                }
                else
                {

                    see_all_notifications.setVisibility(View.GONE);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        final Query notfRef = dRef.child("Notifications").child(mAuth.getCurrentUser().getUid().toString()).limitToLast(5);
        //final DatabaseReference notfRef = dRef.child("Notifications").child(mAuth.getCurrentUser().getUid().toString());
        notfRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!= null)
                {
                    List<Notifications_Model> notfText_list = new ArrayList<>();
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {

                        Notifications_Model n_Model = ds.getValue(Notifications_Model.class);
                        notfText_list.add(n_Model);

                    }
                    if (dataSnapshot.getChildrenCount()>5)
                    {
                        see_all_notifications.setVisibility(View.VISIBLE);
                    }

                    if (notfText_list.size()==0)
                    {
                        notifications_progressBar_Container.setVisibility(View.GONE);
                        nonotification_txt.setVisibility(View.VISIBLE);
                        see_all_notifications.setVisibility(View.GONE);
                    }

                    else if (notfText_list.size()>0)
                    {

                        List<Notifications_Model> notf_invers_list= new ArrayList<Notifications_Model>();
                        for (int index =notfText_list.size()-1;index>=0;index--)
                        {
                            notf_invers_list.add(notfText_list.get(index));
                        }
                        Notifications_Adapter notifications_adapter = new Notifications_Adapter(mContext,notf_invers_list);
                        notifications_listView.setAdapter(notifications_adapter);
                        notifications_adapter.notifyDataSetChanged();
                        setListViewHeightBasedOnChildren(notifications_listView);
                        notifications_progressBar_Container.setVisibility(View.GONE);
                        nonotification_txt.setVisibility(View.GONE);


                    }





                }
                else
                    {
                        notifications_progressBar_Container.setVisibility(View.GONE);
                        nonotification_txt.setVisibility(View.VISIBLE);
                        notifications_listView.setVisibility(View.GONE);
                        see_all_notifications.setVisibility(View.GONE);
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void ShowAll_FriendRequests(TextView see_all_FriendRequest, final AppBarLayout mAppBar) {
        see_all_FriendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new FriendRequests_Fragment()).commit();
                mAppBar.setVisibility(View.GONE);
            }
        });
    }
    private void ShowAll_Notifications(TextView see_all_notifications, final AppBarLayout mAppBar) {
        see_all_notifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new Notifications_Fragment()).commit();
                mAppBar.setVisibility(View.GONE);
            }
        });

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
}
