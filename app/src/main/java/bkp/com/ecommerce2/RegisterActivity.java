package bkp.com.ecommerce2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.IntentService;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private Button createAccountButton;
    private EditText InputName, InputPhoneNumber, InputPassword;
    private ProgressDialog loadingBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        createAccountButton = (Button)findViewById(R.id.register_btn);
        InputName = (EditText)findViewById(R.id.register_username_input);
        InputPhoneNumber = (EditText)findViewById(R.id.register_phone_number_input);
        InputPassword = (EditText)findViewById(R.id.register_password_input);
        loadingBar = new ProgressDialog(this);

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                creatAccount();

            }
        });

    }

    private void creatAccount() {

        String name = InputName.getText().toString();
        String phone = InputPhoneNumber.getText().toString();
        String password = InputPassword.getText().toString();

        if(TextUtils.isEmpty(name)){
            Toast.makeText(this,"Please write your name....",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(phone)){
            Toast.makeText(this,"Please write your phone number....",Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please write your password....",Toast.LENGTH_LONG).show();
        }
        else{

            loadingBar.setTitle("Create Account");
            loadingBar.setMessage("Please wait, While we checking the credentials.");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            validatingPhoneNumber(name,phone,password);


        }




    }

    private void validatingPhoneNumber(final String name, final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(!(dataSnapshot.child("users").child(phone).exists())){

                    HashMap<String, Object>userDataMap = new HashMap<>();
                    userDataMap.put("phone",phone);
                    userDataMap.put("password",password);
                    userDataMap.put("name",name);

                    RootRef.child("users").child(phone).updateChildren(userDataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                                Toast.makeText(RegisterActivity.this,"Congratulations your account has been created...",Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();

                                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                startActivity(intent);


                            }else{
                                loadingBar.dismiss();
                                Toast.makeText(RegisterActivity.this,"Network error: Please try again after sometimes...",Toast.LENGTH_LONG).show();

                            }

                        }
                    });


                }else{
                    Toast.makeText(RegisterActivity.this,"this "+  phone +" already exists",Toast.LENGTH_LONG).show();
                    loadingBar.dismiss();
                    Toast.makeText(RegisterActivity.this,"Please try again using another number",Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                    startActivity(intent);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
