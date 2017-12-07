package com.example.omd.friendlychat.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omd.friendlychat.Adapters.Messages_Adapter;
import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.ChatModel;
import com.example.omd.friendlychat.models.MessageModel;
import com.example.omd.friendlychat.models.UserInformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoom extends AppCompatActivity {
    private ProgressDialog mDialog;
    private static final int RC_pickup=1;
    private static final int RC_select=2;
    private ImageView pickup_image;
    private ImageView view_media;
    private ImageView back;
    private CircleImageView friend_icon;
    private FloatingActionButton sendMeassage;
    private EditText message_Area;
    Toolbar mToolbar;
    UserInformation friend_Information;
    private DatabaseReference dRef;
    private FirebaseAuth mAuth;
    private ListView msgs_list;
    private String msgRef1;
    private String msgRef2;
    private String msgreciver;
    private CardView mCard;
    private TextView respons_message;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        init_View();
        pickup_imageFromCamera(pickup_image);
        select_media(view_media);
        setSupportActionBar(mToolbar);
        Intent intent = getIntent();
        friend_Information = (UserInformation) intent.getSerializableExtra("friend_data");
        msgreciver = intent.getStringExtra("reciver_id");

        if (!intent.equals(null)){

            if (friend_Information!=null){
                displayName_image(friend_icon,mToolbar,friend_Information.getUserId().toString());
                msgRef1 = mAuth.getCurrentUser().getUid().toString() + "_" + friend_Information.getUserId().toString();
                DisplayAll_Message(msgRef1);
                sendMessages(mAuth.getCurrentUser().getUid().toString(), friend_Information.getUserId().toString());
            }
            else if (friend_Information==null){
                if (msgreciver !=null){
                    Check_Reciever_id(msgreciver);
                    msgRef2 = intent.getStringExtra("chatRef");
                    if (!msgRef2.equals(null)&&!msgRef2.equals("")){
                        displayName_image(friend_icon,mToolbar,msgreciver);
                        DisplayAll_Message(msgRef2);
                        sendMessages(mAuth.getCurrentUser().getUid().toString(), msgreciver);
                    }
                }
            }
        }
        setUpIntent(back);
        displayDate();
    }

    private void init_View()
    {
        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference();
        mToolbar = (Toolbar) findViewById(R.id.chatRoomToolBar);
        mCard = (CardView) findViewById(R.id.ChatRoom_card);
        respons_message = (TextView) findViewById(R.id.respons_message_chatRoom);
        back = (ImageView) findViewById(R.id.toolbar_back);
        friend_icon = (CircleImageView) findViewById(R.id.friend_icon);
        sendMeassage = (FloatingActionButton) findViewById(R.id.sendBtn);
        message_Area = (EditText) findViewById(R.id.message_area);
        pickup_image = (ImageView) findViewById(R.id.pickup_image);
        view_media = (ImageView) findViewById(R.id.view_Media);
        msgs_list = (ListView) findViewById(R.id.listview_messages);
        mDialog = new ProgressDialog(ChatRoom.this);
        mDialog.setMessage("Loading messages ....");
        mDialog.setCanceledOnTouchOutside(true);
    }

    private void setUpIntent(ImageView image)
    {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatRoom.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
    }

    private void creatChatRooms(String uid1,String uid2)
    {
        DatabaseReference chatRoomRef1 = dRef.child("ChatRooms").child(uid1);
        chatRoomRef1.child(uid2).setValue(true);
        DatabaseReference chatRoomRef2 = dRef.child("ChatRooms").child(uid2);
        chatRoomRef2.child(uid1).setValue(true);


    }

    private void creatMessages(MessageModel mModel, String uid_1, String uid_2)
    {
        DatabaseReference chatRef1 = dRef.child("Messages").child(uid_1+"_"+uid_2).push();
        DatabaseReference chatRef2 = dRef.child("Messages").child(uid_2+"_"+uid_1).push();
        chatRef1.setValue(mModel);
        chatRef2.setValue(mModel);

    }

    private void creatChats(String lastMsg,String date,String uid_1,String uid_2,String title)
    {
        ChatModel cModel1 = new ChatModel(lastMsg,date,uid_1,uid_2,title);
        ChatModel cModel2 = new ChatModel(lastMsg,date,uid_1,uid_1,title);
        DatabaseReference chatRef1 = dRef.child("Chats").child(uid_1+"_"+uid_2);
        DatabaseReference chatRef2 = dRef.child("Chats").child(uid_2+"_"+uid_1);

        chatRef1.setValue(cModel1);
        chatRef2.setValue(cModel2);

    }

    private void sendMessages(final String uid_1, final String uid_2)
    {
        sendMeassage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mMessage = message_Area.getText().toString();
                if (!TextUtils.isEmpty(mMessage)){

                    String mDate = new SimpleDateFormat("MMM dd,yyyy hh:mm aa").format(new Date().getTime());
                    if (friend_Information!=null){
                    creatChats(mMessage,mDate,uid_1,uid_2,"");
                }else {
                        creatChats(mMessage,mDate,uid_1,uid_2,"");

                    }
                    creatMessages(new MessageModel(message_Area.getText().toString(), mDate, "", uid_1), uid_1, uid_2);
                    creatChatRooms(uid_1,uid_2);

                }
                message_Area.setText(null);

            }
        });
    }

    private void pickup_imageFromCamera(ImageView imageView)
    {
       imageView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
               startActivityForResult(intent,RC_pickup);
           }
       });

   }

    private void select_media(ImageView imageView)
    {
       imageView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
               intent.setType("*/*");
               startActivityForResult(intent,RC_pickup);
           }
       });
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_pickup){
            if (resultCode == RESULT_OK){
                if (data!=null){
                    Uri url =data.getData();
                    Toast.makeText(this,url.toString(), Toast.LENGTH_SHORT).show();
            }
            else {
                    Toast.makeText(this, "emtpt data", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }


        }
        else if (requestCode==RC_select){
            if (resultCode == RESULT_OK){
                if (data!=null){
                    Uri url =data.getData();
                    Toast.makeText(this,url.toString(), Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "emtpt data", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
            }


        }
    }

    private void DisplayAll_Message(String msgRef1)
    {
        mDialog.show();

        DatabaseReference msgRef = dRef.child("Messages").child(msgRef1);
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MessageModel> msg_list = new ArrayList<MessageModel>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    MessageModel mModel = ds.getValue(MessageModel.class);
                    msg_list.add(mModel);
                }
                Messages_Adapter mAdpter = new Messages_Adapter(msg_list, ChatRoom.this);
                msgs_list.setAdapter(mAdpter);
                mAdpter.notifyDataSetChanged();
                mDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void displayDate()
    {
        msgs_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView date = (TextView) view.findViewById(R.id.message_date);
                int vis = date.getVisibility();
                if (vis == 8){
                    date.setVisibility(View.VISIBLE);
                }
                else if (vis == 0){
                    date.setVisibility(View.GONE);
                }
                   // date.setVisibility(View.VISIBLE);


            }
        });
    }
    private void displayName_image(final ImageView imageView, final Toolbar mToolbar, final String reciver_id)
    {

        final DatabaseReference chatRef = dRef.child("Chats").child(mAuth.getCurrentUser().getUid().toString()+"_"+reciver_id);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {

                    if (friend_Information !=null) {
                        if (!friend_Information.getUserImageUri().equals(null) && !friend_Information.getUserName().toString().equals(null)) {
                            Picasso.with(ChatRoom.this).load(friend_Information.getUserImageUri().toString()).into(imageView);
                            mToolbar.setTitle(friend_Information.getUserName().toString());

                        } else {


                        }


                    }else
                        {


                        }
                } else {
                    ChatModel cModel = dataSnapshot.getValue(ChatModel.class);
                    if (cModel.getTitle().isEmpty()) {
                        DatabaseReference userRef = dRef.child("Users").child(reciver_id);
                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() !=null) {
                                    UserInformation info = dataSnapshot.getValue(UserInformation.class);
                                    if (info!=null)
                                    {
                                        Picasso.with(ChatRoom.this).load(info.getUserImageUri().toString()).into(imageView);
                                        mToolbar.setTitle(info.getUserName().toString());
                                    }else
                                        {

                                        }


                                }
                                else
                                    {
                                        mToolbar.setTitle("No Name");
                                        mToolbar.setSubtitle(null);
                                        imageView.setImageResource(R.drawable.user_image_title);
                                    }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        mToolbar.setTitle(cModel.getTitle().toString());
                        Display_Image(reciver_id, imageView);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void Display_Image(String user_id, final ImageView imageView)
    {

        DatabaseReference userRef = dRef.child("Users").child(user_id);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInformation info = dataSnapshot.getValue(UserInformation.class);
                Picasso.with(ChatRoom.this).load(info.getUserImageUri().toString()).into(imageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void Check_Reciever_id(String reciever_id)
    {
        if (reciever_id !=null)
        {
            DatabaseReference userRef =dRef.child("Users").child(reciever_id);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.getValue() ==null)
                    {
                        mCard.setVisibility(View.GONE);
                        respons_message.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
