package com.example.omd.friendlychat.Adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.UserInformation;
import com.example.omd.friendlychat.models.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.Bidi;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Delta on 15/05/2017.
 */

public class ChatRoomAdapter extends BaseAdapter {
    List<ChatModel> chatModelList;
    Context mContext;
    LayoutInflater inflater;
    Map<String,Object> map;
    FirebaseAuth mAuth;

    public ChatRoomAdapter(List<ChatModel> chatModelList, Context mContext) {
        this.chatModelList = chatModelList;
        this.mContext = mContext;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int getCount() {
        return chatModelList.size();
    }

    @Override
    public Object getItem(int i) {
        return chatModelList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.chatroom_row,viewGroup,false);
        CircleImageView friend_img = (CircleImageView) view.findViewById(R.id.chatroom_row_imgFriend);
        TextView friendName = (TextView) view.findViewById(R.id.chatroom_row_friendName);
        TextView txtmsg = (TextView) view.findViewById(R.id.chatroom_row_msg);
        TextView datetxt = (TextView) view.findViewById(R.id.chatroom_row_msgDate);
        TextView ident = (TextView) view.findViewById(R.id.identifier_txt);

        ChatModel mModel =chatModelList.get(i);
        if (mModel !=null)
        {
            datetxt.setText(mModel.getTime().toString().substring(0,11));
            if (mModel.getTitle().isEmpty()) {
                getName(friendName, mModel.getReciver_Id().toString());
                getImage(friend_img, mModel.getReciver_Id().toString());
            }
            else {
                friendName.setText(mModel.getTitle().toString());
                getImage(friend_img, mModel.getReciver_Id().toString());

            }
            Bidi bidi = new Bidi(mModel.getLastMessage().toString(),Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT);
            if (bidi.getBaseLevel()==0){
                txtmsg.setTextDirection(View.TEXT_DIRECTION_LTR);
            }
            else {
                txtmsg.setTextDirection(View.TEXT_DIRECTION_RTL);
            }
            txtmsg.setText(mModel.getLastMessage().toString());
            if (mModel.getSender_Id().toString().equals(mAuth.getCurrentUser().getUid())){
                txtmsg.setText(mModel.getLastMessage().toString());
                ident.setVisibility(View.VISIBLE);
            }
            else {
                txtmsg.setText(mModel.getLastMessage().toString());
                ident.setVisibility(View.GONE);
            }
            if (mModel.getReciver_Id().toString().equals(null))
            {

            }
        }

        return view;
    }
    private void getName(final TextView textView, String uid){

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    UserInformation inf = dataSnapshot.getValue(UserInformation.class);
                    if (inf !=null) {
                        textView.setText(inf.getUserName().toString());
                    }



                }
                else
                    {
                        textView.setText("No Name");
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    private void getImage(final CircleImageView img , String uid){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");userRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {
                    UserInformation inf = dataSnapshot.getValue(UserInformation.class);
                    if (inf !=null)
                    {
                        Picasso.with(mContext).load(inf.getUserImageUri().toString()).into(img);

                    }
                }else
                    {
                        img.setImageResource(R.drawable.init_user_image);

                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
