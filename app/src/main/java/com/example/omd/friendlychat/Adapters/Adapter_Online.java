package com.example.omd.friendlychat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.UserInformation;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Delta on 10/05/2017.
 */

public class Adapter_Online extends BaseAdapter{

    Context mContext;
    List<Object> friendsData;
    LayoutInflater inflater;

    public Adapter_Online(Context mContext, List<Object> friendsData) {
        this.mContext = mContext;
        this.friendsData = friendsData;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        if (friendsData.size()!=0) {
                UserInformation information = (UserInformation) friendsData.get(i);
                if (!information.equals(null)){
                view = inflater.inflate(R.layout.myfriendlist_row_online, viewGroup, false);
                CircleImageView friendImage = (CircleImageView) view.findViewById(R.id.addfriend_userIcon);
                TextView frindName = (TextView) view.findViewById(R.id.addfriend_user_name);
                ImageView online = (ImageView) view.findViewById(R.id.online);
                online.setImageResource(R.drawable.onlin_icon);
                frindName.setText(information.getUserName().toString());
                // Glide.with(view.getContext()).load(information.getUserImageUri().toString()).into(friendImage)
                    Picasso.with(mContext).load(information.getUserImageUri().toString()).into(friendImage);

        }
        }
        return view;
    }

}
