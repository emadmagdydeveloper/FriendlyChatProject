package com.example.omd.friendlychat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.Notifications_Model;
import com.example.omd.friendlychat.models.UserInformation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Delta on 09/06/2017.
 */

public class Notifications_Adapter extends BaseAdapter {
    LayoutInflater inflater;
    Context mContext;
    List<Notifications_Model> notf_list;
    DatabaseReference dRef;

    public Notifications_Adapter(Context mContext, List<Notifications_Model> notf_list) {
        this.mContext = mContext;
        this.notf_list = notf_list;
        dRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public int getCount() {
        return notf_list.size();
    }

    @Override
    public Object getItem(int i) {
        return notf_list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.notifications_row,viewGroup,false);
        TextView notificationText = (TextView) view.findViewById(R.id.notification_text);
        ImageView userImage = (ImageView) view.findViewById(R.id.user_icon);
        Notifications_Model n_Model = notf_list.get(i);
        notificationText.setText(n_Model.getNotificationtext());

        getUserImage(userImage,n_Model.getUserid());

        return view;
    }
    private void getUserImage(final ImageView userImage, final String userId)
    {
        DatabaseReference userRef = dRef.child("Users").child(userId);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {
                    UserInformation uInfo = dataSnapshot.getValue(UserInformation.class);
                    if (uInfo != null){
                    Picasso.with(mContext).load(uInfo.getUserImageUri()).into(userImage);
                }
                }
                else
                    {
                        userImage.setImageResource(R.drawable.noimage);
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
