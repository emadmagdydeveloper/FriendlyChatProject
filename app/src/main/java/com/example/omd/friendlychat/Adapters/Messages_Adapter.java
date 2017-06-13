package com.example.omd.friendlychat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.MessageModel;
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
 * Created by Delta on 16/05/2017.
 */

public class Messages_Adapter extends BaseAdapter {
    List<MessageModel> msg_list;
    Context mContext;
    LayoutInflater inflater;
    FirebaseAuth mAuth;

    public Messages_Adapter(List<MessageModel> msg_list, Context mContext) {
        this.msg_list = msg_list;
        this.mContext = mContext;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int getCount() {
        return msg_list.size();
    }

    @Override
    public Object getItem(int i) {
        return msg_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        MessageModel mModel = msg_list.get(i);
        if (mModel.getSender_id().toString().equals(mAuth.getCurrentUser().getUid().toString())){
            view = inflater.inflate(R.layout.message_out,viewGroup,false);
            setMessages_out_Data(view,mModel);
        }
        else {
            view = inflater.inflate(R.layout.message_in,viewGroup,false);
            setMessages_in_Data(view,mModel);
        }
        return view;
    }
    private void setMessages_out_Data(View view,MessageModel mModel){
        TextView msgDate = (TextView) view.findViewById(R.id.message_date);
        TextView msgTxt = (TextView) view.findViewById(R.id.message_txt);
        TextView msgTime = (TextView) view.findViewById(R.id.message_time);
        ImageView seen = (ImageView) view.findViewById(R.id.message_seen);
        String mDate = mModel.getMessageTime().toString().substring(0,11);
        String mTime = mModel.getMessageTime().toString().substring(11);
        String mText = mModel.getMessageText().toString();

        msgDate.setText(mDate);
        msgTxt.setText(mText);
        msgTime.setText(mTime);
        seen.setImageResource(R.drawable.done_icon);
        seen.setVisibility(View.VISIBLE);
    }
    private void setMessages_in_Data(View view,MessageModel mModel){
        TextView msgDate = (TextView) view.findViewById(R.id.message_date);
        TextView msgTxt = (TextView) view.findViewById(R.id.message_txt);
        TextView msgTime = (TextView) view.findViewById(R.id.message_time);
        ImageView sender_img = (ImageView) view.findViewById(R.id.sender_img);
        String mDate = mModel.getMessageTime().toString().substring(0,11);
        String mTime = mModel.getMessageTime().toString().substring(11);
        String mText = mModel.getMessageText().toString();
        String sender_id = mModel.getSender_id().toString();
        msgDate.setText(mDate);
        msgTxt.setText(mText);
        msgTime.setText(mTime);
        getSender_Image(sender_id,sender_img);
    }
    private void getSender_Image(String sender_id, final ImageView imageView){
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dRef.child(sender_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() !=null)
                {
                    UserInformation info = dataSnapshot.getValue(UserInformation.class);
                    if (info != null)
                    {

                        String userImageUri = info.getUserImageUri().toString();
                        Picasso.with(mContext).load(userImageUri).into(imageView);
                    }
                    else
                        {
                            imageView.setImageResource(R.drawable.init_user_image);
                        }


                }
                else
                    {
                        imageView.setImageResource(R.drawable.init_user_image);

                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
