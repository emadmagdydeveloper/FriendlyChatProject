package com.example.omd.friendlychat.Editing;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omd.friendlychat.Activities.Login_Register;
import com.example.omd.friendlychat.models.Notifications_Model;
import com.example.omd.friendlychat.models.Notifications_Read_Model;
import com.example.omd.friendlychat.models.StatusModel;
import com.example.omd.friendlychat.models.UserInformation;
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
import java.util.Date;

/**
 * Created by Delta on 19/05/2017.
 */

public class Edit {
    DatabaseReference dRef;
    FirebaseAuth mAuth ;
    Context mContext;
    ProgressDialog mDialog;
    public static boolean flag;

    private AlertDialog.Builder mBuilder;


    public Edit(Context mContext) {
        this.mContext = mContext;
        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Deleting...");
        mDialog.setCanceledOnTouchOutside(false);
         dRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mBuilder = new AlertDialog.Builder(mContext);

    }

    public Edit() {
    }

    public void Edit_Image(String myId ,String imageUri)
    {
        DatabaseReference userRef = dRef.child("Users").child(myId);
        userRef.child("userImageUri").setValue(imageUri);
    }
    public void Edit_name(final String myId , TextView myName)
    {

        String statusTxt = myName.getText().toString();
        final EditText editText = new EditText(mContext);
        editText.setText(statusTxt);
        mBuilder.setView(editText);
        mBuilder.setTitle("Change Name");
        mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!editText.getText().toString().isEmpty()){
                    DatabaseReference userRef = dRef.child("Users").child(myId);
                    userRef.child("userName").setValue(editText.getText().toString());
                      }

            }
        });
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }

        });
        mBuilder.show();
        mBuilder.create();
    }
    public void Edit_phone(final String myId , TextView myPhone)
    {

        String statusTxt = myPhone.getText().toString();
        final EditText editText = new EditText(mContext);
        editText.setText(statusTxt);
        mBuilder.setView(editText);
        mBuilder.setTitle("Change Name");
        mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!editText.getText().toString().isEmpty()){
                    DatabaseReference userRef = dRef.child("Users").child(myId);
                    userRef.child("userPhone").setValue(editText.getText().toString());
                }

            }
        });
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }

        });
        mBuilder.show();
        mBuilder.create();
    }
    public void Edit_status(final String myId, TextView myStatus)
    {
        final String statusTxt = myStatus.getText().toString();
        final EditText editText = new EditText(mContext);
        editText.setText(statusTxt);
        mBuilder.setView(editText);
        mBuilder.setTitle("Change Status");
        mBuilder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (!editText.getText().toString().isEmpty()&&!editText.getText().toString().equals(statusTxt)){
                    DatabaseReference statusRef = dRef.child("Status").child(myId);
                    statusRef.setValue(new StatusModel(editText.getText().toString()));
                    setMyStatus_toall_friends(editText.getText().toString());
                }

            }
        });
        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }

        });
        mBuilder.show();
        mBuilder.create();
    }

    private void setMyStatus_toall_friends(final String myStatus) {
        DatabaseReference myFriendRef = dRef.child("Friends").child(mAuth.getCurrentUser().getUid().toString());
        myFriendRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null) {

                    if (flag == true) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {


                            Toast.makeText(mContext, flag + "", Toast.LENGTH_SHORT).show();
                            add_notifications(ds.getKey().toString(), myStatus);


                        }


                    }
                    else
                        {
                         flag=false;
                        }
                }
                else
                    {

                        flag=false;
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void Remove_Friend(final String myId, final String friend_id)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Delete ?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDialog.show();
                DatabaseReference friendsRef = dRef.child("Friends").child(myId).child(friend_id);
                friendsRef.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {

                            DatabaseReference FriendsRef = dRef.child("Friends").child(friend_id).child(myId);
                            FriendsRef.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @RequiresApi(api = Build.VERSION_CODES.N)
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())

                                    {
                                        String dateFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm aa").format(new Date());
                                        DatabaseReference NotfRef = dRef.child("Notifications").child(friend_id).push();
                                        Notifications_Model n_Model = new Notifications_Model(myId,MyData.getMyName().toString() + " delete friendship at "+dateFormat);
                                        NotfRef.setValue(n_Model);
                                        DatabaseReference NotfReadRef = dRef.child("Notifications_readed").child(friend_id).push();
                                        Notifications_Read_Model readnotf = new Notifications_Read_Model(myId,false);
                                        NotfReadRef.setValue(readnotf);



                                    }
                                }
                            });
                            mDialog.dismiss();
                            Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mDialog.dismiss();
                    }
                });
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(mContext, "Canceled", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create();
        builder.show();
        builder.setCancelable(false);
    }
    public void BlockUsers(final String myId, final String user_id)
    {
       AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
       builder.setTitle("Block and unfriend");
       builder.setMessage("Block ?");
       builder.setPositiveButton("Block", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               mDialog.show();
               DatabaseReference blockRef = dRef.child("Blocks").child(myId).child(user_id);
               blockRef.setValue(true);
               DatabaseReference friendsRef = dRef.child("Friends").child(myId).child(user_id);
               friendsRef.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                   @RequiresApi(api = Build.VERSION_CODES.N)
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       if (task.isSuccessful())
                       {
                           String dateFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm aa").format(new Date());
                           DatabaseReference NotfRef = dRef.child("Notifications").child(user_id).push();
                           Notifications_Model n_Model = new Notifications_Model(myId, MyData.getMyName() + " block you at "+dateFormat);
                           NotfRef.setValue(n_Model);
                           DatabaseReference NotfReadRef = dRef.child("Notifications_readed").child(user_id).push();
                           Notifications_Read_Model readnotf = new Notifications_Read_Model(myId,false);
                           NotfReadRef.setValue(readnotf);

                           mDialog.dismiss();
                           Toast.makeText(mContext, "blocked", Toast.LENGTH_SHORT).show();
                       }
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       mDialog.dismiss();
                       Toast.makeText(mContext,e.getMessage(), Toast.LENGTH_SHORT).show();
                   }
               });
           }
       });
       builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               Toast.makeText(mContext, "Canceled", Toast.LENGTH_SHORT).show();
           }
       });

       builder.create();
       builder.show();
       builder.setCancelable(false);


   }
    public void unBlock(final String myId , final String userId)
    {
        if (!myId.equals(null)&&!userId.equals(null))
        {
            DatabaseReference blockRef = dRef.child("Blocks").child(myId);
            blockRef.child(userId).getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {
                        String dateFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm aa").format(new Date());
                        DatabaseReference NotfRef = dRef.child("Notifications").child(userId).push();
                        Notifications_Model n_Model = new Notifications_Model(myId, MyData.getMyName().toString() + " delete block at "+dateFormat);
                        NotfRef.setValue(n_Model);
                        DatabaseReference NotfReadRef = dRef.child("Notifications_readed").child(userId).push();
                        Notifications_Read_Model readnotf = new Notifications_Read_Model(myId,false);
                        NotfReadRef.setValue(readnotf);

                        AddFriend(myId,userId);

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(mContext,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }
    public void AddFriend(String myId,String user_id)
    {
       DatabaseReference friendRef = dRef.child("Friends").child(myId).child(user_id);
       friendRef.setValue(true);
   }
///////////////////Delete My Account
    public void Delete_Account(final String myId, final FirebaseAuth mAuth, final FragmentActivity activity)
    {
       mDialog.show();
        Delete_From_ChatRoom(myId,mAuth,activity);


   }
    private void Delete_From_ChatRoom(final String myId, final FirebaseAuth mAuth, final FragmentActivity activity)
    {
        DatabaseReference chatRoomRef = dRef.child("ChatRooms").child(myId);
        chatRoomRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Delete_From_chats(myId,mAuth,activity);
                }
                else {
                    Delete_From_chats(myId,mAuth,activity);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "error in chatrooms", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void Delete_From_chats(final String myId, final FirebaseAuth mAuth, final FragmentActivity activity)
    {
        DatabaseReference chatsRef = dRef.child("Chats");
        chatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() !=null)
                {
                    for (DataSnapshot ds :dataSnapshot.getChildren())
                    {
                        if (ds.getKey().startsWith(myId))
                        {
                            ds.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                       Delelte_From_Messages(myId,mAuth,activity);
                                    }
                                    else
                                        {
                                            Delelte_From_Messages(myId,mAuth,activity);

                                        }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext, "error in chats", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }
                else
                    {
                        Delelte_From_Messages(myId,mAuth,activity);
                    }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void Delelte_From_Messages(final String myId, final FirebaseAuth mAuth, final FragmentActivity activity)
    {
        DatabaseReference msgRef = dRef.child("Messages");
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() !=null)
                {
                    for (DataSnapshot ds:dataSnapshot.getChildren())
                    {
                        if (ds.getKey().startsWith(myId))
                        {
                            ds.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        Delete_From_Friends(myId,mAuth,activity);
                                    }
                                    else
                                        {
                                            Delete_From_Friends(myId,mAuth,activity);

                                        }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext, "Error in messages", Toast.LENGTH_SHORT).show();
                                    mDialog.dismiss();
                                }
                            });
                        }
                    }
                }
                else
                    {
                        Delete_From_Friends(myId,mAuth,activity);

                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void Delete_From_Friends(final String myId, final FirebaseAuth mAuth, final FragmentActivity activity)
    {
        DatabaseReference friendsRef = dRef.child("Friends").child(myId);
        friendsRef.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Delete_From_State(myId,mAuth,activity);
                }
                else
                    {
                        Delete_From_State(myId,mAuth,activity);

                    }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Error in del Friends", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void Delete_From_State(final String myId, final FirebaseAuth mAuth, final FragmentActivity activity)
    {
        DatabaseReference StateRef = dRef.child("State").child("Online").child(myId);
        StateRef.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Delete_From_Status(myId,mAuth,activity);
                }
                else
                    {
                        Delete_From_Status(myId,mAuth,activity);
                    }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "error in state", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void Delete_From_Status(final String myId, final FirebaseAuth mAuth, final FragmentActivity activity)
    {
        DatabaseReference Status = dRef.child("Status").child(myId);
        Status.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Delete_From_Users(myId,mAuth,activity);
                }
                else
                    {
                        Delete_From_Users(myId,mAuth,activity);
                    }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Error in Status", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void Delete_From_Users(final String myId, final FirebaseAuth mAuth, final FragmentActivity activity)
    {
        DatabaseReference UsersRef = dRef.child("Users").child(myId);
        UsersRef.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    SignOut(mAuth,activity);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Error in ", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void SignOut(final FirebaseAuth mAuth, final FragmentActivity activity)
    {
        mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    mAuth.signOut();
                    mContext.startActivity(new Intent(activity,Login_Register.class));
                    mDialog.dismiss();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "error in user", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void Update_All_Notifications()
    {

         DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid().toString());
         UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {

                    UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                    if (MyData.getMyId().equals(userInformation.getUserId().toString())) {

                        if (userInformation.getUserId().equals(MyData.getMyId().toString())) {
                            MyData.setMyId(userInformation.getUserId().toString());
                            MyData.setMyName(userInformation.getUserName());
                            MyData.setMyEmail(userInformation.getUserEmail());
                            MyData.setMyPhone(userInformation.getUserPhone());
                            MyData.setMyImage(userInformation.getUserImageUri());
                            Toast.makeText(mContext, MyData.getMyName() + "" + MyData.getMyId(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
/////////////////////////////////////
    public void Delete_Conversation(final String myId,String userId)
    {
        DatabaseReference chatRoomsRef = dRef.child("ChatRooms").child(myId).child(userId);
        chatRoomsRef.getRef().removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        DatabaseReference chatsRef = dRef.child("Chats").child(myId+"_"+userId);
        chatsRef.getRef().removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        DatabaseReference msgRef = dRef.child("Messages").child(myId+"_"+userId);
        msgRef.getRef().removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    private void add_notifications(String userId, String myStatus)
    {
        String dateFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm aa").format(new Date());
        DatabaseReference notfRef = dRef.child("Notifications").child(userId.toString()).push();
        DatabaseReference NotfReadRef = dRef.child("Notifications_readed").child(userId).push();
        Notifications_Read_Model read = new Notifications_Read_Model(mAuth.getCurrentUser().getUid().toString(),false);
        NotfReadRef.setValue(read);
        Notifications_Model notifications_model = new Notifications_Model(mAuth.getCurrentUser().getUid().toString(), MyData.getMyName() + " Change status to "+myStatus+" at "+dateFormat);
        notfRef.setValue(notifications_model);


    }

    public void add_Notifications(String myId,String userId,String msg)
    {
        String dateFormat = new SimpleDateFormat("MMM dd,yyyy hh:mm aa").format(new Date());
        DatabaseReference NotfRef = dRef.child("Notifications").child(userId).push();
        DatabaseReference NotfReadRef = dRef.child("Notifications_readed").child(userId).push();
        Notifications_Read_Model read = new Notifications_Read_Model(myId,false);
        NotfReadRef.setValue(read);
        Notifications_Model m_Model = new Notifications_Model(myId,MyData.getMyName().toString()+" "+msg+" "+dateFormat);
        NotfRef.setValue(m_Model);

    }
}

