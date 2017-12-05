package com.example.omd.friendlychat.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.omd.friendlychat.Editing.Edit;
import com.example.omd.friendlychat.Editing.MyData;
import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.Notifications_Model;
import com.example.omd.friendlychat.models.Notifications_Read_Model;
import com.example.omd.friendlychat.models.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Delta on 09/06/2017.
 */

public class FriendRequest_Adapter extends BaseAdapter {

    List<UserInformation> informations;
    Context mContext;
    LayoutInflater inflater;
    FirebaseAuth mAuth;
    DatabaseReference dRef;

    public FriendRequest_Adapter(List<UserInformation> informations, Context mContext) {
        this.informations = informations;
        this.mContext = mContext;
        mAuth =FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public int getCount() {
        return informations.size();
    }

    @Override
    public Object getItem(int i) {
        return informations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.friendrequest_row,viewGroup,false);
        ImageView userImage = (ImageView) view.findViewById(R.id.notification_userIcon);
        TextView notfTxt = (TextView) view.findViewById(R.id.notification_txt);
        Button addFriend_Btn = (Button) view.findViewById(R.id.notification_addBtn);
        Button reject_Btn = (Button) view.findViewById(R.id.notification_rejectBtn);

        UserInformation userInformation = informations.get(i);
        Picasso.with(mContext).load(userInformation.getUserImageUri().toString()).into(userImage);
        notfTxt.setText(userInformation.getUserName().toString()+" "+"sent a friend request");

        addFriend_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final UserInformation userinfo = informations.get(i);
                DatabaseReference friendsRef2 = dRef.child("Friends");
                friendsRef2.child(userinfo.getUserId().toString()).child(mAuth.getCurrentUser().getUid().toString()).setValue(true);
                DatabaseReference friendsRef = dRef.child("Friends");
                friendsRef.child(mAuth.getCurrentUser().getUid().toString()).child(userinfo.getUserId().toString()).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            UserInformation listdata = informations.get(i);
                            String mdateFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm aa").format(new Date().getTime());
                            DatabaseReference notfRef = dRef.child("Notifications").child(listdata.getUserId().toString()).push();

                            DatabaseReference NotfReadRef = dRef.child("Notifications_readed").child(listdata.getUserId()).push();
                            Notifications_Read_Model read = new Notifications_Read_Model(mAuth.getCurrentUser().getUid().toString(),false);
                            NotfReadRef.setValue(read);
                            //DatabaseReference notfRef = dRef.child("Notifications").child(listdata.getUserId().toString()).child(mAuth.getCurrentUser().getUid().toString());
                            Notifications_Model n_Model = new Notifications_Model(mAuth.getCurrentUser().getUid(),MyData.getMyName()+" accept a friend request at "+mdateFormat);
                            notfRef.setValue(n_Model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        UserInformation uiform = informations.get(i);
                                        DatabaseReference FriendRequest_ref = dRef.child("FriendRequest").child(mAuth.getCurrentUser().getUid().toString());
                                        FriendRequest_ref.child(uiform.getUserId().toString()).getRef().removeValue();

                                    }
                                }
                            });

                        }

                    }
                });
            }
        });
        reject_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final UserInformation inf = informations.get(i);
                DatabaseReference m_notfRef = dRef.child("FriendRequest").child(mAuth.getCurrentUser().getUid().toString());
                m_notfRef.child(inf.getUserId().toString()).getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Edit edit = new Edit(mContext);
                            edit.add_Notifications(mAuth.getCurrentUser().getUid().toString(),inf.getUserId().toString().toString(),"reject a friend request at");

                        }
                    }
                });



            }
        });
        return view;
    }
}
