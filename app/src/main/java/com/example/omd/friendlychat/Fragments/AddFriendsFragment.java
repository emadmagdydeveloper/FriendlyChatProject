package com.example.omd.friendlychat.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.omd.friendlychat.Activities.ChatRoom;
import com.example.omd.friendlychat.Activities.profile;
import com.example.omd.friendlychat.Editing.Edit;
import com.example.omd.friendlychat.Editing.MyData;
import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.Notifications_Model;
import com.example.omd.friendlychat.models.Notifications_Read_Model;
import com.example.omd.friendlychat.models.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Delta on 26/04/2017.
 */

public class AddFriendsFragment extends Fragment {

    private RelativeLayout pb_container;
    private LinearLayout addFriend_Container;
    private ListView adduser_listView;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef_Users;
    List<Object> listusers_Information;
    private DatabaseReference dRef;
    Context mContext;
    FragmentActivity activity;
    Adapter adapter;
    DatabaseReference mRef_Friends;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.addfriendsfragment, container, false);
        init_View(view);
        getUsersData_FromFirebase();
        return view;
    }

    private void init_View(View view) {
        mContext = view.getContext();
        addFriend_Container = (LinearLayout) view.findViewById(R.id.addfriend_Container);

        pb_container = (RelativeLayout) view.findViewById(R.id.progress_bar_Container);
        adduser_listView = (ListView) view.findViewById(R.id.adduser_listView);

        activity = getActivity();
        pb_container.setVisibility(View.VISIBLE);
        addFriend_Container.setVisibility(View.GONE);

        //////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();
        mRef_Friends = FirebaseDatabase.getInstance().getReference().child("Friends");
        mRef_Users = FirebaseDatabase.getInstance().getReference().child("Users");
    }

    private void getUsersData_FromFirebase() {

        mRef_Users.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listusers_Information = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (!ds.getKey().equals(mAuth.getCurrentUser().getUid().toString())) {
                        UserInformation userInformation = ds.getValue(UserInformation.class);
                        listusers_Information.add(userInformation);

                    }
                }
                Check_Notin_Block(listusers_Information);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void Check_Notin_Block(final List<Object> user_info) {
        DatabaseReference blockRef = FirebaseDatabase.getInstance().getReference().child("Blocks");
        blockRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Object> user_data = new ArrayList<>();
                if (dataSnapshot.getValue() != null) {
                    for (Object ob : user_info) {
                        UserInformation inf = (UserInformation) ob;
                        if (inf != null) {
                            if (!dataSnapshot.hasChild(inf.getUserId().toString())) {
                                user_data.add(inf);
                            }
                        }

                    }

                    adapter = new Adapter(user_data, mContext);
                    adduser_listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    pb_container.setVisibility(View.GONE);
                    addFriend_Container.setVisibility(View.VISIBLE);
                } else {

                    adapter = new Adapter(user_info, mContext);
                    adduser_listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    pb_container.setVisibility(View.GONE);
                    addFriend_Container.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    class Adapter extends BaseAdapter {

        List<Object> list_users;
        Context context;
        LayoutInflater inflater;
        UserInformation information;
        DatabaseReference ref, dRef;
        FirebaseAuth mAuth;


        public Adapter() {
        }

        public Adapter(List<Object> list_users, Context context) {
            this.list_users = list_users;
            this.context = context;
            mAuth = FirebaseAuth.getInstance();
            ref = FirebaseDatabase.getInstance().getReference().child("Friends").child(mAuth.getCurrentUser().getUid().toString());
            dRef = FirebaseDatabase.getInstance().getReference();
        }

        @Override
        public int getCount() {
            return list_users.size();
        }

        @Override
        public Object getItem(int i) {
            return list_users.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup)
        {
            inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.userlist_row, viewGroup, false);
            information = (UserInformation) list_users.get(i);
            ImageView userIcon_listView_users = (ImageView) view.findViewById(R.id.addfriend_userIcon);
            TextView userName = (TextView) view.findViewById(R.id.addfriend_user_name);
            TextView userEmail = (TextView) view.findViewById(R.id.addfriend_userEmail);
            Glide.with(mContext).load(information.getUserImageUri().toString()).into(userIcon_listView_users);
            userName.setText(information.getUserName().toString());
            userEmail.setText(information.getUserEmail().toString());

            final ImageView popmenu = (ImageView) view.findViewById(R.id.addfriendpopmenu);
            final Button addFriend_addfriendBtn = (Button) view.findViewById(R.id.addfriend_addFriendBtn);
            final Button addFriend_cancelRequestBtn = (Button) view.findViewById(R.id.addfriend_cancelRequestBtn);
            final Button addFriend_acceptBtn = (Button) view.findViewById(R.id.addfriend_acceptBtn);
            final Button addFrind_reject_Btn = (Button) view.findViewById(R.id.addfriend_rejectBtn);
            final TextView friend_txt = (TextView) view.findViewById(R.id.addfriend_textBtn);

            popmenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu = new PopupMenu(mContext, view);
                    popupMenu.getMenuInflater().inflate(R.menu.popmenu, popupMenu.getMenu());
                    popupMenu.show();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.popmenu_viewprofile) {
                                UserInformation information = (UserInformation) list_users.get(i);
                                Intent intent = new Intent(mContext, profile.class);
                                intent.putExtra("friendData", information);
                                mContext.startActivity(intent);

                            } else if (item.getItemId() == R.id.popmenu_sendmessage) {
                                UserInformation information = (UserInformation) list_users.get(i);
                                Intent intent = new Intent(mContext, ChatRoom.class);
                                intent.putExtra("friend_data", information);
                                mContext.startActivity(intent);

                            }
                            return true;
                        }
                    });
                }
            });
            Check_iam_notin_userBlock(mAuth.getCurrentUser().getUid().toString(), information.getUserId().toString(), addFriend_addfriendBtn, addFriend_cancelRequestBtn, addFriend_acceptBtn, addFrind_reject_Btn, friend_txt,popmenu);
            //check_user_notin_FriendRequest_Ref(mAuth.getCurrentUser().getUid().toString(), information.getUserId().toString(), addFriend_addfriendBtn, addFriend_cancelRequestBtn, addFriend_acceptBtn, addFrind_reject_Btn, friend_txt);





            addFriend_addfriendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    final UserInformation information2 = (UserInformation) list_users.get(i);
                    DatabaseReference FriendRequest_Ref = dRef.child("FriendRequest");
                    FriendRequest_Ref.child(information2.getUserId().toString()).child(mAuth.getCurrentUser().getUid().toString()).setValue(true);
                    Edit edit = new Edit(mContext);
                    edit.add_Notifications(mAuth.getCurrentUser().getUid().toString(),information2.getUserId().toString(),"sent a friend request at");


                }
            });
            addFriend_cancelRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final UserInformation information3 = (UserInformation) list_users.get(i);
                    final DatabaseReference notfRef = dRef.child("FriendRequest").child(information3.getUserId().toString());
                    notfRef.child(mAuth.getCurrentUser().getUid().toString()).getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                                DatabaseReference notRef = dRef.child("Notifications").child(information3.getUserId().toString()).child(mAuth.getCurrentUser().getUid().toString());
                                notRef.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Edit edit = new Edit(mContext);
                                            edit.add_Notifications(mAuth.getCurrentUser().getUid().toString(),information3.getUserId().toString(),"canceled a friend request at");
                                            Toast.makeText(mContext, "Request is canceled", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });
                            }
                        }
                    });


                }
            });

            friend_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popMenu = new PopupMenu(mContext, view);
                    popMenu.getMenuInflater().inflate(R.menu.popmenu_unfriend_block, popMenu.getMenu());
                    popMenu.show();
                    popMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {

                            if (menuItem.getItemId() == R.id.menu_unfriend) {


                                UserInformation inf = (UserInformation) list_users.get(i);
                                Edit edit = new Edit(mContext);
                                edit.Remove_Friend(mAuth.getCurrentUser().getUid(), inf.getUserId().toString());


                            } else if (menuItem.getItemId() == R.id.menu_Block) {
                                UserInformation inf = (UserInformation) list_users.get(i);
                                Edit edit = new Edit(mContext);
                                edit.BlockUsers(mAuth.getCurrentUser().getUid(), inf.getUserId().toString());

                            }
                            return true;
                        }
                    });
                }
            });

            addFriend_acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    final UserInformation user_Data1 = (UserInformation) list_users.get(i);

                    DatabaseReference FriendrequstRef = dRef.child("FriendRequest").child(mAuth.getCurrentUser().getUid()).child(user_Data1.getUserId());
                    FriendrequstRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                DatabaseReference friendsRef1 = dRef.child("Friends");
                                friendsRef1.child(mAuth.getCurrentUser().getUid().toString()).child(user_Data1.getUserId().toString()).setValue(true);
                                DatabaseReference friendsRef2 = dRef.child("Friends");

                                friendsRef2.child(user_Data1.getUserId().toString()).child(mAuth.getCurrentUser().getUid().toString()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Edit edit = new Edit(mContext);
                                            edit.add_Notifications(mAuth.getCurrentUser().getUid().toString(),user_Data1.getUserId().toString(),"accept a friend request at");

                                        }

                                    }
                                });
                            }
                        }
                    });


                }

            });
            addFrind_reject_Btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String dateFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm aa").format(new Date());
                    final UserInformation m_info = (UserInformation) list_users.get(i);
                    DatabaseReference notfRef = dRef.child("Notifications").child(m_info.getUserId().toString()).push();
                    // DatabaseReference notfRef = dRef.child("Notifications").child(m_info.getUserId().toString()).child(mAuth.getCurrentUser().getUid().toString());
                    DatabaseReference NotfReadRef = dRef.child("Notifications_readed").child(m_info.getUserId()).push();
                    Notifications_Read_Model read = new Notifications_Read_Model(mAuth.getCurrentUser().getUid().toString(),false);
                    NotfReadRef.setValue(read);
                    Notifications_Model n_Model = new Notifications_Model(mAuth.getCurrentUser().getUid().toString(), MyData.getMyName() + " reject a friend request at "+dateFormat);
                    notfRef.setValue(n_Model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                UserInformation userDataFrom_List = (UserInformation) list_users.get(i);
                                DatabaseReference m_notfRef = dRef.child("FriendRequest").child(mAuth.getCurrentUser().getUid().toString());
                                m_notfRef.child(userDataFrom_List.getUserId().toString()).getRef().removeValue();

                            }
                        }
                    });
                }
            });

            return view;
        }


        private void check_user_notin_FriendRequest_Ref(final String myId, final String userId, final Button btnadd, final Button btncancel, final Button btnaddfriend, final Button btnreject, final TextView friend_txtBtn) {


            DatabaseReference FriendRequest_Ref = dRef.child("FriendRequest").child(myId);
            FriendRequest_Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (dataSnapshot.hasChild(userId)) {
                            btnaddfriend.setVisibility(View.VISIBLE);
                            btnreject.setVisibility(View.VISIBLE);
                            friend_txtBtn.setVisibility(View.GONE);
                            btnadd.setVisibility(View.GONE);
                            btncancel.setVisibility(View.GONE);
                        } else {


                            Check_Is_friend(myId, userId, btnadd, btncancel, btnaddfriend, btnreject, friend_txtBtn);

                        }
                    } else {

                        Check_Is_friend(myId, userId, btnadd, btncancel, btnaddfriend, btnreject, friend_txtBtn);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        private void check_user_notin_FriendRequest_Ref2(final String userId, final String myId, final Button btnadd, final Button btncancel, final Button btnaddfriend, final Button btnreject, final TextView friend_txtBtn) {
            DatabaseReference FriendRequest_Ref = dRef.child("FriendRequest").child(userId);
            FriendRequest_Ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        if (dataSnapshot.hasChild(myId)) {
                            btnaddfriend.setVisibility(View.GONE);
                            btnreject.setVisibility(View.GONE);
                            friend_txtBtn.setVisibility(View.GONE);
                            btnadd.setVisibility(View.GONE);
                            btncancel.setVisibility(View.VISIBLE);


                        } else {

                            btnaddfriend.setVisibility(View.GONE);
                            btnreject.setVisibility(View.GONE);
                            friend_txtBtn.setVisibility(View.GONE);
                            btnadd.setVisibility(View.VISIBLE);
                            btncancel.setVisibility(View.GONE);

                        }
                    } else {

                        btnaddfriend.setVisibility(View.GONE);
                        btnreject.setVisibility(View.GONE);
                        friend_txtBtn.setVisibility(View.GONE);
                        btnadd.setVisibility(View.VISIBLE);
                        btncancel.setVisibility(View.GONE);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        private void Check_Is_friend(final String myId, final String userId, final Button addBtn, final Button cancel_Btn, final Button btnaddfriend, final Button btnreject, final TextView friendtext_Btn) {
            DatabaseReference friendRef = dRef.child("Friends").child(myId);
            friendRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {


                        if (dataSnapshot.hasChild(userId)) {
                            btnaddfriend.setVisibility(View.GONE);
                            btnreject.setVisibility(View.GONE);
                            addBtn.setVisibility(View.GONE);
                            cancel_Btn.setVisibility(View.GONE);
                            friendtext_Btn.setVisibility(View.VISIBLE);


                        } else {
                            check_user_notin_FriendRequest_Ref2(userId, myId, addBtn, cancel_Btn, btnaddfriend, btnreject, friendtext_Btn);


                        }

                    } else {
                        check_user_notin_FriendRequest_Ref2(userId, myId, addBtn, cancel_Btn, btnaddfriend, btnreject, friendtext_Btn);


                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        private void Check_iam_notin_userBlock(final String myId, final String userId, final Button addBtn, final Button cancel_Btn, final Button btnaddfriend, final Button btnreject, final TextView friendtext_Btn, final ImageView popmenu)
        {
            DatabaseReference blockRef = dRef.child("Blocks").child(userId);
            blockRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() !=null)
                    {
                     if (dataSnapshot.hasChild(myId))
                     {
                         btnaddfriend.setVisibility(View.GONE);
                         btnreject.setVisibility(View.GONE);
                         addBtn.setVisibility(View.GONE);
                         cancel_Btn.setVisibility(View.GONE);
                         friendtext_Btn.setVisibility(View.GONE);
                         popmenu.setVisibility(View.GONE);
                     }
                     else
                         {
                             check_user_notin_FriendRequest_Ref(myId,userId,addBtn,cancel_Btn,btnaddfriend,btnreject,friendtext_Btn);
                         }
                    }
                    else
                        {
                            check_user_notin_FriendRequest_Ref(myId,userId,addBtn,cancel_Btn,btnaddfriend,btnreject,friendtext_Btn);

                        }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }


    }



}