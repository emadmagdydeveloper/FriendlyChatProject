package com.example.omd.friendlychat.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.omd.friendlychat.Activities.MainActivity;
import com.example.omd.friendlychat.Editing.MyData;
import com.example.omd.friendlychat.R;
import com.example.omd.friendlychat.models.UserInformation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Delta on 27/04/2017.
 */

public class RegisterFragment extends Fragment {


    private CircleImageView userImage;
    private EditText register_name;
    private EditText register_email;
    private EditText register_password;
    private EditText register_phone;
    private ImageView check_name;
    private ImageView check_phone;
    private ImageView check_email;
    private ImageView check_password;
    private Button register_signupBtn;
    Context mContext;
    static boolean isCorrectname = false;
    static boolean isCorrectphone = false;
    static boolean isCorrectemail = false;
    static boolean isCorrectpassword = false;
    static boolean isCorrectphoto = false;
    public final int RC = 1;
    Uri uri = null;
    String userImagUri,userName,userPhone,userEmail,userPassword;
    UserInformation info;
    String userId;
    FirebaseAuth mAuth;
    DatabaseReference dRef;
    ProgressDialog mDialog;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registerfragment,container,false);
        initView(view);

        //////////////////////////
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setType("image/*");
                startActivityForResult(intent,RC);
            }
        });
        //////////////////////////
        register_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                checkName();
                Toast.makeText(mContext,charSequence, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });
        /////////////////////////
        register_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                checkPhone();
            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });
        ////////////////////////
        register_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                checkEmail();
            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });
        /////////////////////////
        register_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                checkPassword();

            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });
        ////////////////////////

        register_signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.show();
                signup();
            }
        });
        return view;
    }

    private void initView(View view) {
        userImage = (CircleImageView) view.findViewById(R.id.register_addphoto);
        register_name = (EditText) view.findViewById(R.id.register_name);
        register_phone = (EditText) view.findViewById(R.id.register_phone);
        register_email = (EditText) view.findViewById(R.id.register_email);
        register_password = (EditText) view.findViewById(R.id.register_password);
        register_signupBtn = (Button) view.findViewById(R.id.register_signupBtn);
        check_name = (ImageView) view.findViewById(R.id.check_name);
        check_phone = (ImageView) view.findViewById(R.id.check_phone);
        check_email = (ImageView) view.findViewById(R.id.check_email);
        check_password = (ImageView) view.findViewById(R.id.check_password);

        //////////////////////////////

        mContext = view.getContext();
        //////////////////////////////
        isCorrectemail=false;
        isCorrectname = false;
        isCorrectpassword = false;
        isCorrectphone = false;
        isCorrectphoto = false;
        uri =null;
        register_name.setText(null);
        register_email.setText(null);
        register_phone.setText(null);
        register_password.setText(null);
        //////////////////////////////

        mAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance().getReference().child("Users");
        //////////////////////////////
        mDialog = new ProgressDialog(getActivity());
        mDialog.setMessage("Wait for registration..");
        mDialog.setCancelable(false);
    }

    private void checkName()         {
        try {
            if (register_name.getText().toString().isEmpty()){
                Toast.makeText(mContext, "Name is empty", Toast.LENGTH_SHORT).show();
                check_name.setImageResource(R.drawable.check_false);
                check_name.setVisibility(View.VISIBLE);
                isCorrectname = false;
            }
            else if (!register_name.getText().toString().matches("^\\w((\\s)?(\\w))*$"))
            {
                check_name.setImageResource(R.drawable.check_false);
                check_name.setVisibility(View.VISIBLE);
                isCorrectname = false;
            }
            else {
                check_name.setImageResource(R.drawable.check_true);
                check_name.setVisibility(View.VISIBLE);
                isCorrectname = true;
            }

        }catch (Exception ex){
            isCorrectname = false;
            Toast.makeText(mContext,ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPhone()        {
        try {
            if (register_phone.getText().toString().isEmpty()){
                Toast.makeText(mContext, "Check your phone", Toast.LENGTH_SHORT).show();
                check_phone.setImageResource(R.drawable.check_false);
                check_phone.setVisibility(View.VISIBLE);
                isCorrectphone = false;
            }
            else{

                if (register_phone.getText().toString().matches("^(010|011|012)[0-9]{8}$"))
                 {
                check_phone.setImageResource(R.drawable.check_true);
                check_phone.setVisibility(View.VISIBLE);
                isCorrectphone = true;
                }else {
                    check_phone.setImageResource(R.drawable.check_false);
                    check_phone.setVisibility(View.VISIBLE);
                    isCorrectphone = false;

                }


        }
        }catch (Exception ex){
            isCorrectphone = false;
            Toast.makeText(mContext,ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkEmail()        {

        try {
            if (register_email.getText().toString().isEmpty()){
                Toast.makeText(mContext, "Check your email", Toast.LENGTH_SHORT).show();
                check_email.setImageResource(R.drawable.check_false);
                isCorrectemail = false;
            }
            else {
                if (register_email.getText().toString().matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
                    check_email.setImageResource(R.drawable.check_true);
                    check_email.setVisibility(View.VISIBLE);
                    isCorrectemail = true;
                }else {
                    check_email.setImageResource(R.drawable.check_false);
                    check_email.setVisibility(View.VISIBLE);
                    isCorrectemail = false;

                }

            }
        }catch (Exception ex){
            isCorrectemail = false;
            Toast.makeText(mContext,ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPassword()     {
        try {
            if (register_password.getText().toString().isEmpty()){
                Toast.makeText(mContext, "Check your password", Toast.LENGTH_SHORT).show();
                check_password.setImageResource(R.drawable.check_false);
                isCorrectpassword = false;
            }
            else {

                if (register_password.getText().toString().matches(".{8,}")){
                    check_password.setImageResource(R.drawable.check_true);
                    check_password.setVisibility(View.VISIBLE);
                    isCorrectpassword = true;
                }
                else {
                    check_password.setImageResource(R.drawable.check_false);
                    check_password.setVisibility(View.VISIBLE);
                    isCorrectpassword = false;

                }

            }
        }catch (Exception ex){
            isCorrectpassword = false;
            Toast.makeText(mContext,ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
    }


    private void signup(){

        if (isCorrectphoto == true && isCorrectname == true && isCorrectemail==true && isCorrectphone == true && isCorrectpassword == true){

             userImagUri = uri.toString();
             userName    = register_name.getText().toString();
             userPhone   = register_phone.getText().toString();
             userEmail   = register_email.getText().toString();
             userPassword= register_password.getText().toString();


            mAuth.createUserWithEmailAndPassword(userEmail,userPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){

                        if (mAuth.getCurrentUser()!=null){


                            userId = mAuth.getCurrentUser().getUid();
                            info = new UserInformation(userId,userImagUri,userName,userPhone,userEmail);
                            MyData.setMyId(userId);
                            MyData.setMyName(userName);
                            MyData.setMyEmail(userEmail);
                            MyData.setMyPhone(userPhone);
                            MyData.setMyImage(userImagUri);
                            DatabaseReference dRef2 =dRef.child(info.getUserId());
                            dRef2.setValue(info).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        CreateStatus();
                                        Toast.makeText(mContext, userId+"\n"+userImagUri+"\n"+userName, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getActivity(),MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        mDialog.dismiss();
                                        Toast.makeText(mContext, "Register successfuly", Toast.LENGTH_SHORT).show();
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


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mDialog.dismiss();
                    Toast.makeText(mContext,e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }
        else if (uri == null){

            mDialog.dismiss();
            Toast.makeText(mContext,"Choose your photo", Toast.LENGTH_SHORT).show();
        }else if (isCorrectname == false)
            {
                mDialog.dismiss();
                Toast.makeText(mContext,"Check your name", Toast.LENGTH_SHORT).show();

            }
        else if (isCorrectphone == false)
        {
            mDialog.dismiss();
            Toast.makeText(mContext,"Check your phone", Toast.LENGTH_SHORT).show();

        }
        else if (isCorrectemail == false)
        {
            mDialog.dismiss();
            Toast.makeText(mContext,"Check your email", Toast.LENGTH_SHORT).show();

        }
        else if (isCorrectpassword == false)
        {
            mDialog.dismiss();
            Toast.makeText(mContext,"Check your password", Toast.LENGTH_SHORT).show();

        }
        else {

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC && resultCode == Activity.RESULT_OK)
        {
            if (data!= null){
                uri = data.getData();
                if (uri != null){
                userImage.setImageURI(uri);
                isCorrectphoto = true;
            }
                else
                {
                    isCorrectphoto = false;
                }
            }
            else
            {
                Toast.makeText(mContext, "Please choose your photo", Toast.LENGTH_SHORT).show();
                isCorrectphoto = false;
            }
        }
        else
        {
            isCorrectphoto = false;
        }
    }
    private void CreateStatus(){
        DatabaseReference statusRef = FirebaseDatabase.getInstance().getReference().child("Status").child(mAuth.getCurrentUser().getUid().toString());
        statusRef.child("status").setValue("");
    }

}
