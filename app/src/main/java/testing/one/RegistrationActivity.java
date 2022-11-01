package testing.one;

import static data.DbRef.DbName;
import static data.DbRef.databaseReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class RegistrationActivity extends AppCompatActivity {
     private FirebaseAuth mAuth;
     private EditText Reg_Firstname,Reg_Lastname,Reg_Email,Reg_CellNumber,Reg_Username,Reg_Age,Reg_Password,Reg_RepeatPassword;
     private Button Register;
     private ImageView BannerLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        BannerLogo = findViewById(R.id.banner_logo);
        Reg_Firstname = findViewById(R.id.reg_Firstname);
        Reg_Lastname = findViewById(R.id.reg_Lastname);
        Reg_Email = findViewById(R.id.email);
        Reg_CellNumber = findViewById(R.id.reg_Cellphone);
        Reg_Username = findViewById(R.id.reg_username);
        Reg_Age = findViewById(R.id.reg_Age);
        Reg_Password = findViewById(R.id.reg_Password);
        Reg_RepeatPassword = findViewById(R.id.reg_ReapeatPassword);
        Register= findViewById(R.id.btnRegister);

        BannerLogo.setOnClickListener(View ->{
            Intent intent = new Intent(RegistrationActivity.this,MainActivity.class);
            startActivity(intent);

        });

        Register.setOnClickListener( view -> {
            //extracting the data from the textboxes into string variables
            final String Firstname = Reg_Firstname.getText().toString().trim();
            final String Lastname = Reg_Lastname.getText().toString().trim();
            final String Email = Reg_Email.getText().toString().trim();
            final String CellNumber = Reg_CellNumber.getText().toString().trim();
            final String Username = Reg_Username.getText().toString().trim();
            final String Age =Reg_Age.getText().toString().trim();
            final String Password = Reg_Password.getText().toString().trim();
            final String Repeat_Password = Reg_RepeatPassword.getText().toString().trim();
            final int ageValue = Age.isEmpty()||Age==""?0:Integer.valueOf(Age);


            if ((isConnected(RegistrationActivity.this)) == false) {
                showCustomDialog();
            } else {
                //checking iff all fields have been filled in
                if (Firstname.isEmpty() || Lastname.isEmpty() || Email.isEmpty() || CellNumber.isEmpty() || Username.isEmpty() || Age.isEmpty() || Password.isEmpty() || Repeat_Password.isEmpty()) {
                    if (Firstname.isEmpty()) {
                        Reg_Firstname.setError("Firstname is required");
                        Reg_Firstname.requestFocus();
                        return;
                    }
                    if (Lastname.isEmpty()) {
                        Reg_Lastname.setError("Lastname is required");
                        Reg_Lastname.requestFocus();
                        return;
                    }
                    if (Email.isEmpty()) {
                        Reg_Email.setError("Email is required");
                        Reg_Email.requestFocus();
                        return;
                    }
                    if (CellNumber.isEmpty()) {
                        Reg_CellNumber.setError("Cellnumber is required");
                        Reg_CellNumber.requestFocus();
                        return;
                    }
                    if (Username.isEmpty()) {
                        Reg_Username.setError("Username is required");
                        Reg_Username.requestFocus();
                        return;
                    }
                    if (Age.isEmpty()) {
                        Reg_Age.setError("Age is required");
                        Reg_Age.requestFocus();
                        return;
                    }
                    if (Password.isEmpty()) {
                        Reg_Password.setError("Password is required");
                        Reg_Password.requestFocus();
                        return;
                    }
                    if (Repeat_Password.isEmpty()) {
                        Reg_RepeatPassword.setError("Repeat Password is required");
                        Reg_RepeatPassword.requestFocus();
                        return;
                    }
                    //Reg_ErrorMessages.append("Please fill in all the fields");
                    //Toast.makeText(RegistrationActivity.this,"Please fill in all the fields",Toast.LENGTH_SHORT).show();


                    //validating email

                    if (!Patterns.EMAIL_ADDRESS.matcher(Email).matches()) {

                        Reg_Email.setError("Email format is invalid");
                        Reg_Email.requestFocus();
                        //Reg_ErrorMessages.append("\nEmail format is invalid");
                        return;

                        //Toast.makeText(RegistrationActivity.this,"Invalid email",Toast.LENGTH_SHORT).show();
                    }

                    //validating phone number

                    if (CellNumber.length() != 10) {
                        Reg_CellNumber.setError("Cell number must be 10 digits long");
                        Reg_CellNumber.requestFocus();
                        // Reg_ErrorMessages.append("\nCell number must be 10 digits");
                        //Toast.makeText(RegistrationActivity.this,"Cell number must have 10 digits",Toast.LENGTH_SHORT).show();
                    }


                    if (ageValue > 100) {
                        Reg_Age.setError("Age must not be greater than 100");
                        Reg_Age.requestFocus();
                        //Reg_ErrorMessages.append("\nAge must be less than 100");
                        //Toast.makeText(RegistrationActivity.this,"Age cannot be greater than 100",Toast.LENGTH_SHORT).show();
                    }


                    if (!Password.equals(Repeat_Password)) {
                        Reg_Password.setError("Passwords do not match");
                        Reg_Password.requestFocus();
                        Reg_RepeatPassword.requestFocus();

                        //Reg_ErrorMessages.append("\nPasswords do not match");
                        //Toast.makeText(RegistrationActivity.this,"Passwords do not match",Toast.LENGTH_SHORT).show();
                    }
                    if ((Password.equals(Repeat_Password)) && Password.length() < 8) {
                        Reg_Password.setError("Password must be more han 8 characters in length");
                        Reg_Password.requestFocus();
                        Reg_RepeatPassword.requestFocus();
                        //Reg_ErrorMessages.append("\nPassword must be greater than 8 characters");
                        //Toast.makeText(RegistrationActivity.this,"Password cannot be less than 8 characters",Toast.LENGTH_SHORT).show();
                    }
                    //Toast.makeText(RegistrationActivity.this,Reg_ErrorMessages.getText().toString(),Toast.LENGTH_SHORT).show();
                } else {

                    databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //checking if Username is not registered
                            if (snapshot.hasChild(Username)) {
                                Toast.makeText(RegistrationActivity.this, "Username is already Registered", Toast.LENGTH_SHORT).show();
                            } else {

                                //sending data to firebase realtime db
                                //Username will be used as a unique ID
                                //other fields will come under Username
                                databaseReference.child(DbName).child(Username).child("Firstname").setValue(Firstname);
                                databaseReference.child(DbName).child(Username).child("Lastname").setValue(Lastname);
                                databaseReference.child(DbName).child(Username).child("Cell Number").setValue(CellNumber);
                                databaseReference.child(DbName).child(Username).child("Age").setValue(Age);
                                databaseReference.child(DbName).child(Username).child("Password").setValue(Password);
                                //display feedback
                                Toast.makeText(RegistrationActivity.this, "User registered Successfully.", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }
            }




        } );


    }
    private void showCustomDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
        builder.setMessage("Please connect to the internet to proceed further")
                .setCancelable(false)
                .setPositiveButton("Connect", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //redirecting the user to setting->WiFi
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //redirect the user back to the landing screen
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).show();
    }
    private boolean isConnected(RegistrationActivity login) {

        ConnectivityManager connectivityManager = (ConnectivityManager) login.getSystemService(Context.CONNECTIVITY_SERVICE);
        //wifi connection -> getting the current status
        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //Mobile Connectivity
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return wifiConn != null && wifiConn.isConnected() || (mobileConn != null && mobileConn.isConnected());
    }


}