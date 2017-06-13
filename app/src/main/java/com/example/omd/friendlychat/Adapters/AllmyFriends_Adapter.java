package com.example.omd.friendlychat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.omd.friendlychat.Activities.ChatRoom;
import com.example.omd.friendlychat.Activities.profile;
import com.example.omd.friendlychat.Editing.Edit;
import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Delta on 10/05/2017.
 */

public class AllmyFriends_Adapter extends BaseAdapter{

    Context mContext;
    List<Object> friendsData;
    LayoutInflater inflater;
    FirebaseAuth mAuth;
    DatabaseReference dRef;
    public ImageView popMenu;
    static int flag;
    public AllmyFriends_Adapter(Context mContext, List<Object> friendsData,int flag) {
        this.mContext = mContext;
        this.friendsData = friendsData;
        this.flag = flag;
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public int getCount() {
        return friendsData.size();
    }

    @Override
    public Object getItem(int i) {
        return friendsData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        if (friendsData.size()!=0) {
            UserInformation information = (UserInformation) friendsData.get(i);
            if (!information.equals(null)){
                view = inflater.inflate(R.layout.myfriends_profile_row, viewGroup, false);
                ImageView friendImage = (ImageView) view.findViewById(R.id.myfriend_profile_userIcon);
                popMenu = (ImageView) view.findViewById(R.id.myfriendpopmenu_profile);
                TextView frindName = (TextView) view.findViewById(R.id.myfriend_profile_user_name);
                frindName.setText(information.getUserName().toString());
                //Glide.with(mContext).load(information.getUserImageUri().toString()).into(friendImage);
                Picasso.with(mContext).load(information.getUserImageUri().toString()).into(friendImage);

                if (mAuth.getCurrentUser().getUid().equals(information.getUserId().toString()))
               {
                   popMenu.setVisibility(View.INVISIBLE);
               }
               if (flag == 1)
               {
                   popMenu.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           final PopupMenu menu = new PopupMenu(mContext, view);
                           menu.getMenuInflater().inflate(R.menu.popmenu_profile, menu.getMenu());
                           menu.show();
                           menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                               @Override
                               public boolean onMenuItemClick(MenuItem item) {
                                   if (item.getItemId() == R.id.popmenu_sendmessage) {
                                       UserInformation information = (UserInformation) friendsData.get(i);
                                       Intent intent = new Intent(mContext, ChatRoom.class);
                                       intent.putExtra("friend_data", information);
                                       mContext.startActivity(intent);
                                   } else if (item.getItemId() == R.id.popmenu_viewprofile) {
                                       UserInformation information = (UserInformation) friendsData.get(i);
                                       Intent intent = new Intent(mContext, profile.class);
                                       intent.putExtra("friendData", information);
                                       intent.putExtra("myId",mAuth.getCurrentUser().getUid().toString());
                                       mContext.startActivity(intent);
                                   } else if (item.getItemId() == R.id.popmenu_removeFreind) {
                                       UserInformation information = (UserInformation) friendsData.get(i);
                                       Edit edit = new Edit(mContext);
                                       edit.Remove_Friend(mAuth.getCurrentUser().getUid().toString(),information.getUserId().toString());


                                   } else if (item.getItemId() == R.id.popmenu_block) {
                                       UserInformation information = (UserInformation) friendsData.get(i);
                                       Edit edit = new Edit(mContext);
                                       edit.BlockUsers(mAuth.getCurrentUser().getUid().toString(),information.getUserId().toString());

                                   }
                                   return true;
                               }
                           });
                       }


                   });
               }
               else if (flag == 2)
               {
                   isFriend(mAuth.getCurrentUser().getUid().toString(),information.getUserId().toString(),popMenu);
                   popMenu.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           final PopupMenu menu = new PopupMenu(mContext, view);
                           menu.getMenuInflater().inflate(R.menu.popmenu_viewer, menu.getMenu());
                           menu.show();
                           menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                               @Override
                               public boolean onMenuItemClick(MenuItem item) {
                                   if (item.getItemId() == R.id.popmenu_sendmessage) {
                                       UserInformation information = (UserInformation) friendsData.get(i);
                                       Intent intent = new Intent(mContext, ChatRoom.class);
                                       intent.putExtra("friend_data", information);
                                       mContext.startActivity(intent);
                                   } else if (item.getItemId() == R.id.popmenu_viewprofile) {
                                       UserInformation information = (UserInformation) friendsData.get(i);
                                       Intent intent = new Intent(mContext, profile.class);
                                       intent.putExtra("friendData", information);
                                       mContext.startActivity(intent);
                                   }
                                   else if (item.getItemId() == R.id.popmenu_addFriend) {
                                       UserInformation information = (UserInformation) friendsData.get(i);
                                       Edit edit = new Edit(mContext);
                                       edit.AddFriend(mAuth.getCurrentUser().getUid().toString(),information.getUserId().toString());

                                   }
                                   return true;
                               }
                           });
                       }


                   });

               }

            }

        }
        return view;
    }
    private void isFriend(String myId, final String userId, final ImageView imageView)
    {
        DatabaseReference friendRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(myId);
        friendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() !=null)
                {
                    if (dataSnapshot.hasChild(userId)){
                        imageView.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
