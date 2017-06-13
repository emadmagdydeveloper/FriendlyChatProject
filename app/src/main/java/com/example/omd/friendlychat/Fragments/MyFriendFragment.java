package com.example.omd.friendlychat.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.omd.friendlychat.Activities.ChatRoom;
import com.example.omd.friendlychat.Adapters.Adapter_Offline;
import com.example.omd.friendlychat.Adapters.Adapter_Online;
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
 * Created by Delta on 02/05/2017.
 */

public class MyFriendFragment extends Fragment
{
    View view;
    Context mContext;
    private ListView myfriends_Listview_online;
    private ListView myfriends_Listview_offline;
    FirebaseAuth mAuth;
    DatabaseReference dRef;
    DatabaseReference dRef_Friends;
    DatabaseReference dRef_Users;
    DatabaseReference dRef_State_Online;
    List<String> friendKeys;
    List<Object> friendInformation;
    List<Object> friendOnline;
    List<Object> friendOffline;
    FragmentActivity activity;
    Object object;
    String chatKey;
    RelativeLayout pb_Container;
    LinearLayout myFriend_Container;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view= inflater.inflate(R.layout.myfriendsfragment,container,false);
         init_View(view);
         getFriendsKeys();
         getFriendData_from_adapter(myfriends_Listview_offline);
         getFriendData_from_adapter(myfriends_Listview_online);
         return view;
    }

    private void init_View(View view) {

         activity = getActivity();
        pb_Container = (RelativeLayout) activity.findViewById(R.id.main_myFriend_Container);
        myFriend_Container = (LinearLayout) activity.findViewById(R.id.myFriend_Container);
         mContext = view.getContext();
         myfriends_Listview_online = (ListView) view.findViewById(R.id.myFriendsList_online);
         myfriends_Listview_offline = (ListView) view.findViewById(R.id.myFriendsList_offline);
         dRef = FirebaseDatabase.getInstance().getReference();
        //////////////////////////////////////////////////////////////////////////
         mAuth = FirebaseAuth.getInstance();

    }
   private void getFriendsKeys()
   {
       dRef_Friends = dRef.child("Friends").child(mAuth.getCurrentUser().getUid().toString());
       dRef_Friends.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               friendKeys = new ArrayList<>();
               for (DataSnapshot ds:dataSnapshot.getChildren()){
                   friendKeys.add(ds.getKey().toString());
               }
               getUserData(friendKeys);
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
   }
   private void getUserData(final List<String>keys)
   {

       dRef_Users = dRef.child("Users");
       dRef_Users.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               friendInformation = new ArrayList<>();
               for (String key:keys) {
                   UserInformation info = dataSnapshot.child(key).getValue(UserInformation.class);
                   friendInformation.add(info);
               }
               checkFriendState(friendInformation);
           }


           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
   }
   public void checkFriendState(final List<Object> userData)
   {
       dRef_State_Online = dRef.child("State").child("Online");
       dRef_State_Online.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               friendOnline = new ArrayList<>();
               friendOffline = new ArrayList<>();
               if (dataSnapshot.getValue()!=null) {
                   for (Object ob : userData) {
                       UserInformation info = (UserInformation) ob;
                       if (info !=null){
                       if (dataSnapshot.hasChild(info.getUserId().toString())) {
                           friendOnline.add(info);
                       } else {
                           friendOffline.add(info);
                       }
                   }
                   }

                   Adapter_Online adpter_Online = new Adapter_Online(mContext, friendOnline);
                   myfriends_Listview_online.setAdapter(adpter_Online);
                   adpter_Online.notifyDataSetChanged();
                   setListViewHeightBasedOnChildren(myfriends_Listview_online);
                   pb_Container.setVisibility(View.GONE);
                   myFriend_Container.setVisibility(View.VISIBLE);

                   Adapter_Offline adpter_Offline = new Adapter_Offline(mContext, friendOffline);
                   myfriends_Listview_offline.setAdapter(adpter_Offline);
                   adpter_Offline.notifyDataSetChanged();
                   setListViewHeightBasedOnChildren(myfriends_Listview_offline);
                   pb_Container.setVisibility(View.GONE);
                   myFriend_Container.setVisibility(View.VISIBLE);

               }
           }

           @Override
           public void onCancelled(DatabaseError databaseError) {

           }
       });
   }
   private void getFriendData_from_adapter(ListView listView)
   {
       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
           @Override
           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               object = adapterView.getAdapter().getItem(i);
               setUpIntent(object);

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
    private void setUpIntent(Object object)
    {
        UserInformation info = (UserInformation) object;
        Intent intent = new Intent(getActivity(),ChatRoom.class);
        intent.putExtra("friend_data",info);
        intent.putExtra("chatkey",chatKey);
        mContext.startActivity(intent);
    }


}