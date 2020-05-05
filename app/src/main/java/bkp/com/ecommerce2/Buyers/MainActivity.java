package bkp.com.ecommerce2.Buyers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import bkp.com.ecommerce2.Prevalent.Prevalent;
import bkp.com.ecommerce2.R;
import bkp.com.ecommerce2.Sellers.SellerHomeActivity;
import bkp.com.ecommerce2.Sellers.SellerRegistrationActivity;
import bkp.com.ecommerce2.model.Users;
import io.paperdb.Paper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Button joinNowButton, loginButton;
    private TextView sellerBegin;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sellerBegin = findViewById(R.id.seller_begin);
        joinNowButton = (Button)findViewById(R.id.main_join_now_btn);
        loginButton = (Button)findViewById(R.id.main_login_btn);
        loadingBar = new ProgressDialog(this);

        Paper.init(this);

        sellerBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SellerRegistrationActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        joinNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

        String userPhoneKey = Paper.book().read(Prevalent.UserPhoneKey);
        String userPasswordKey = Paper.book().read(Prevalent.UserPasswordKey);

        if(userPhoneKey != "" && userPasswordKey != ""){
            if(!TextUtils.isEmpty(userPhoneKey) && !TextUtils.isEmpty(userPasswordKey)){

                allowAccess(userPhoneKey,userPasswordKey);

                loadingBar.setTitle("Already logged in");
                loadingBar.setMessage("Please wait......");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            Intent intent = new Intent(MainActivity.this, SellerHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void allowAccess(final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("users").child(phone).exists()){

                    Users usersData = dataSnapshot.child("users").child(phone).getValue(Users.class);
                    if(usersData.getPhone().equals(phone)){
                        if(usersData.getPassword().equals(password)){
                            Toast.makeText(MainActivity.this,"Please wait, You are already logged in...",Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            Prevalent.currentOnlineUser = usersData;
                            startActivity(intent);
                        }else{
                            loadingBar.dismiss();
                            Toast.makeText(MainActivity.this,"Password is incorrect...",Toast.LENGTH_LONG).show();
                        }
                    }

                }else{

                    Toast.makeText(MainActivity.this,"Account with this " + phone + "number does not exists.",Toast.LENGTH_LONG).show();
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
