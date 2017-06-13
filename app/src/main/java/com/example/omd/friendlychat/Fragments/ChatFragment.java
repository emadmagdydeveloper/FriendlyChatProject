package com.example.omd.friendlychat.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.omd.friendlychat.Activities.Block;
import com.example.omd.friendlychat.Activities.ChatRoom;
import com.example.omd.friendlychat.Activities.Login_Register;
import com.example.omd.friendlychat.Activities.profile;
import com.example.omd.friendlychat.Adapters.ChatRoomAdapter;
import com.example.omd.friendlychat.Editing.Edit;
import com.example.omd.friendlychat.Editing.MyData;
import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.ChatModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

public class ChatFragment extends Fragment {
    private ListView chatroom_list;
    Context mContext;
    FirebaseAuth mAuth;
    ProgressDialog mDialog;
    AlertDialog.Builder mBuilder;
    DatabaseReference dRef;
    DatabaseReference mdref;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.chatfragment,container,false);
        mContext = view.getContext();
        init_View(view);
        setHasOptionsMenu(true);
        getAllChatRooms();
        getChatRef();
        Delete_Conversation(chatroom_list);
        return view;
    }

    private void init_View(View view) {
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();
        chatroom_list = (ListView) view.findViewById(R.id.chatroom_list);
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("wait for deleting your account");
        mDialog.setCancelable(false);
        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setMessage("Are you sure to delete your account ?");
        mBuilder.setTitle("Warning");


    }
    private void getAllChatRooms(){
        DatabaseReference chatRoomRef = dRef.child("ChatRooms").child(mAuth.getCurrentUser().getUid().toString());
        chatRoomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String>roomKeys = new ArrayList<String>();
                for (DataSnapshot ds:dataSnapshot.getChildren()){
                    roomKeys.add(mAuth.getCurrentUser().getUid().toString()+"_"+ds.getKey());
                }
                get_Lastmessages(roomKeys);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void get_Lastmessages(final List<String> roomKeys){
        DatabaseReference chatRef = dRef.child("Chats");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<ChatModel> cModel_List = new ArrayList<>();
                for (String key:roomKeys) {
                    ChatModel cModel= dataSnapshot.child(key).getValue(ChatModel.class);
                    cModel_List.add(cModel);
                      }


                ChatRoomAdapter mAdapter =new ChatRoomAdapter(cModel_List,mContext);
                chatroom_list.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void getChatRef(){
        chatroom_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChatModel cModel = (ChatModel) adapterView.getAdapter().getItem(i);
                String chatRef = mAuth.getCurrentUser().getUid().toString()+"_"+cModel.getReciver_Id().toString();
                String reciver_id = cModel.getReciver_Id().toString();
                setUpIntent(chatRef,reciver_id);
            }
        });
    }

    private void setUpIntent(String chatRef,String reciver_id) {
        Intent intent = new Intent(getActivity(),ChatRoom.class);
        intent.putExtra("chatRef",chatRef);
        intent.putExtra("reciver_id",reciver_id);
        startActivity(intent);
    }
    private void Delete_Conversation(ListView listView)
    {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, final int i, long l) {


                PopupMenu popupMenu = new PopupMenu(mContext,view, Gravity.CENTER_VERTICAL);
                popupMenu.getMenuInflater().inflate(R.menu.delete_conversation,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId()==R.id.popmenu_delete_conversation)
                        {
                            final AlertDialog.Builder dd = new AlertDialog.Builder(mContext);

                            View m_View = getActivity().getLayoutInflater().inflate(R.layout.alert_dialog,null);
                            Object ob = adapterView.getAdapter().getItem(i);
                            final ChatModel cModel = (ChatModel) ob;
                            final Edit edit = new Edit(mContext);
                            TextView title = (TextView) m_View.findViewById(R.id.d_title);
                            TextView content = (TextView) m_View.findViewById(R.id.d_content);
                            Button cancel = (Button) m_View.findViewById(R.id.d_cancel);
                            Button delete_conversation = (Button) m_View.findViewById(R.id.d_deleteConversation);
                            title.setText("Delete Conversation ?");
                            content.setText("Once you delete your copy of the conversation, it cannot be undone");
                            dd.setView(m_View);
                            final AlertDialog aD = dd.show();
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    aD.dismiss();
                                }
                            });
                            delete_conversation.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    edit.Delete_Conversation(mAuth.getCurrentUser().getUid().toString(),cModel.getReciver_Id().toString());
                                    aD.dismiss();
                                }
                            });


                        }
                        return true;
                    }
                });
                popupMenu.show();
                return true;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu,menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.block)
        {
            getActivity().startActivity(new Intent(mContext, Block.class));
        }
        else if (item.getItemId() == R.id.viewProfile) {
            getActivity().startActivity(new Intent(mContext,profile.class));

        }

        else if (item.getItemId() == R.id.signout) {
            mdref = FirebaseDatabase.getInstance().getReference().child("State").child("Online").child(mAuth.getCurrentUser().getUid().toString());
            deleteChild_From_Online(mdref.getRef());

            String sd = new SimpleDateFormat("MMM ,dd/yyyy hh:mm aa").format(new Date().getTime());
            mdref = FirebaseDatabase.getInstance().getReference().child("State").child("Offline").child(mAuth.getCurrentUser().getUid().toString());
            mdref.setValue(sd);
            MyData.setMyId(null);
            MyData.setMyName(null);
            MyData.setMyEmail(null);
            MyData.setMyPhone(null);
            MyData.setMyImage(null);
            mAuth.signOut();
            startActivity(new Intent(getActivity(), Login_Register.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else if (item.getItemId() == R.id.deleteaccount) {
            mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    DeleteAcount();
                }
            });
            mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            mBuilder.show();
        }
        return true;
    }
    private void DeleteAcount()
    {
        Edit edit = new Edit(mContext);
        edit.Delete_Account(mAuth.getCurrentUser().getUid().toString(),mAuth,getActivity());

    }
    private void deleteChild_From_Online(DatabaseReference ref)
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
    public void onStart() {
        super.onStart();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
                    mContext.startActivity(new Intent(getActivity(),Login_Register.class));
                }
            }
        };
    }
}
