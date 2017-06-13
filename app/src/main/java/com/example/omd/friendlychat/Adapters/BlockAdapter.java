package com.example.omd.friendlychat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.omd.friendlychat.Editing.Edit;
import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.UserInformation;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by Delta on 27/05/2017.
 */

public class BlockAdapter extends BaseAdapter {
    Context mContext;
    List<UserInformation> userInformations;
    LayoutInflater inflater;
    FirebaseAuth mAuth;

    public BlockAdapter(Context mContext, List<UserInformation> userInformations) {
        this.mContext = mContext;
        this.userInformations = userInformations;
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public int getCount() {
        return userInformations.size();
    }

    @Override
    public Object getItem(int i) {
        return userInformations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.block_row,viewGroup,false);
        UserInformation info = userInformations.get(i);
        ImageView userImage = (ImageView) view.findViewById(R.id.block_userIcon);
        TextView user_name = (TextView) view.findViewById(R.id.block_user_name);
        Button unblock_Btn = (Button) view.findViewById(R.id.block_unBlockBtn);
        Glide.with(mContext).load(info.getUserImageUri()).into(userImage);
        user_name.setText(info.getUserName().toString());
        unblock_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInformation info = userInformations.get(i);
                Edit edit = new Edit(mContext);
                edit.unBlock(mAuth.getCurrentUser().getUid().toString(),info.getUserId().toString());
            }
        });
        return view;
    }
}
