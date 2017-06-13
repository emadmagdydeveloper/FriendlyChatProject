package com.example.omd.friendlychat.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.omd.friendlychat.Activities.MainActivity;
import com.example.omd.friendlychat.Editing.MyData;
import com.example.omd.friendlychat.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Delta on 27/04/2017.
 */

public class LoginFragment extends Fragment{
    private EditText login_emailText;
    private EditText login_passwordText;
    private EditText login_newpasswordText;
    private CheckBox login_checkbox_rememberMe;
    private Button   login_loginBtn;
    private Button   login_confermBtn;
    private Button   login_cancelBtn;
    private LinearLayout form;
    private TextView forgetPassword;
    Context mContext;
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private boolean saved;
    FirebaseAuth mAuth;
    ProgressDialog mDialog;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.loginfragment,container,false);
        mContext = view.getContext();
        initView(view);
        login_loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login();
                remember_Me();
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                form.setVisibility(View.VISIBLE);
            }
        });

        login_cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                form.setVisibility(View.GONE);
            }
        });

        login_confermBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //reset_updatepassword();
            }
        });

        return view;
    }

    private void initView(View view){
        login_emailText           = (EditText) view.findViewById(R.id.login_emailtext);
        login_passwordText        = (EditText) view.findViewById(R.id.login_passwordtext);
        login_newpasswordText        = (EditText) view.findViewById(R.id.login_newpasswordtext);
        login_checkbox_rememberMe = (CheckBox) view.findViewById(R.id.logincheckbox_remember_me);
        login_loginBtn            = (Button) view.findViewById(R.id.login_loginBtn);
        login_confermBtn          = (Button) view.findViewById(R.id.login_confirmBtn);
        login_cancelBtn           = (Button) view.findViewById(R.id.login_cancelBtn);
        form                      = (LinearLayout) view.findViewById(R.id.login_forgetpassword_form);

        forgetPassword = (TextView) view.findViewById(R.id.login_forgetpassword);

        mDialog = new ProgressDialog(mContext);
        mDialog.setMessage("Wait for login...");
        mDialog.setCancelable(false);
        ////////////////////////////////////////////////////////////
        mAuth = FirebaseAuth.getInstance();
        ////////////////////////////////////////////////////////////
        mPref = mContext.getSharedPreferences("loginPref",mContext.MODE_PRIVATE);
        mEditor= mPref.edit();
        saved = mPref.getBoolean("saved",false);
        if (saved == true){
            login_emailText.setText(mPref.getString("email",""));
            login_passwordText.setText(mPref.getString("password",""));
            login_checkbox_rememberMe.setChecked(true);
        }


    }
    private void login()
    {
        try {


        String mEmail = login_emailText.getText().toString();
        String mPassword = login_passwordText.getText().toString();
        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword)){
            Toast.makeText(mContext, "Empty Field", Toast.LENGTH_SHORT).show();
        }
        else {
            mDialog.show();
            mAuth.signInWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        init_MyData();
                        mDialog.dismiss();
                        startActivity(new Intent(getActivity(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(mContext,e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }catch (Exception ex){
            Toast.makeText(mContext,ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void init_MyData() {
        MyData.setMyId(mAuth.getCurrentUser().getUid().toString());
        /*DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid().toString());
        UserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue()!=null)
                {

                    UserInformation userInformation = dataSnapshot.getValue(UserInformation.class);
                    if (userInformation.getUserId().equals(MyData.getMyId().toString())) {
                        MyData.setMyId(userInformation.getUserId().toString());
                        MyData.setMyName(userInformation.getUserName());
                        MyData.setMyEmail(userInformation.getUserEmail());
                        MyData.setMyPhone(userInformation.getUserPhone());
                        MyData.setMyImage(userInformation.getUserImageUri());
                        Toast.makeText(mContext,MyData.getMyName()+""+MyData.getMyId(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
    }

    private void remember_Me()
    {
        if (login_checkbox_rememberMe.isChecked()){
            mEditor.putBoolean("saved",true);
            mEditor.putString("email",login_emailText.getText().toString());
            mEditor.putString("password",login_passwordText.getText().toString());
            mEditor.commit();
        }
        else {
            mEditor.clear();
            mEditor.commit();
        }
    }


}
