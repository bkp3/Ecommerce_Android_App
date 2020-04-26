package bkp.com.ecommerce2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import bkp.com.ecommerce2.Admin.AdminCategoryActivity;
import bkp.com.ecommerce2.Prevalent.Prevalent;
import bkp.com.ecommerce2.model.Users;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
//import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;


public class LoginActivity extends AppCompatActivity {

    private EditText InputNumber, InputPassword;
    private Button LoginButton;
    private ProgressDialog loadingBar;

    private TextView AdminLink, NotAdminLink, ForgotPasswordLink;
    private String parentdbName="users";
    private CheckBox chkBoxRememberMe;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginButton = (Button)findViewById(R.id.login_btn);
        InputNumber = (EditText)findViewById(R.id.login_phone_number_input);
        InputPassword = (EditText)findViewById(R.id.login_password_input);
        AdminLink = (TextView)findViewById(R.id.admin_panel_link);
        NotAdminLink = (TextView)findViewById(R.id.not_admin_panel_link);
        ForgotPasswordLink = findViewById(R.id.forgot_passwrod_link);

        loadingBar = new ProgressDialog(this);

        chkBoxRememberMe = (CheckBox)findViewById(R.id.remember_me_chkb);
        Paper.init(this);

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loginUser();

            }
        });

        ForgotPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                intent.putExtra("check","login");
                startActivity(intent);

            }
        });

        AdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Admin Login");
                AdminLink.setVisibility(View.INVISIBLE);
                NotAdminLink.setVisibility(View.VISIBLE);
                parentdbName = "Admins";

            }
        });

        NotAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginButton.setText("Login");
                AdminLink.setVisibility(View.VISIBLE);
                NotAdminLink.setVisibility(View.INVISIBLE);
                parentdbName = "users";
            }
        });



    }

    private void loginUser() {

        String phone = InputNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please write your phone number....",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please write your password....",Toast.LENGTH_LONG).show();
        }else{

            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait, While we checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            allowAccessToAccount(phone,password);
        }

    }

    private void allowAccessToAccount(final String phone, final String password) {

        if(chkBoxRememberMe.isChecked()){

            Paper.book().write(Prevalent.UserPhoneKey,phone);
            Paper.book().write(Prevalent.UserPasswordKey,password);
        }


        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child(parentdbName).child(phone).exists()){

                    Users usersData = dataSnapshot.child(parentdbName).child(phone).getValue(Users.class);
                    if(usersData.getPhone().equals(phone)){
                        if(usersData.getPassword().equals(password)){
                            if(parentdbName.equals("Admins")){

                                Toast.makeText(LoginActivity.this,"Welcome Admin, You are Logged in successfully...",Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);

                            }else if(parentdbName.equals("users")){

                                Toast.makeText(LoginActivity.this,"Logged in successfully...",Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                                Intent intent = new Intent(LoginActivity.this,HomeActivity.class);
                                Prevalent.currentOnlineUser = usersData;
                                startActivity(intent);

                            }
                        }else{
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this,"Password is incorrect...",Toast.LENGTH_LONG).show();
                        }
                    }

                }else{

                    Toast.makeText(LoginActivity.this,"Account with this " + phone + "number does not exists.",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
//                    Toast.makeText(LoginActivity.this,"You need to create a new account.",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
