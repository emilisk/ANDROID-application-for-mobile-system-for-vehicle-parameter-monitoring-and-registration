package mylocation.example.logandreg;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class RegisterActivity extends AppCompatActivity {
    DatabaseHelper db;
    EditText mTextUsername;
    EditText mTextEmail;
    EditText mTextPassword;
    EditText mTextCnfPassword;
    Button mButtonRegister;
    TextView mTextViewLogin;

    // kodavimas
    String outputString;
    String AES = "AES";
    TextView outputText;
    String raktas = "burbuliukas";
    // kodavimas

    //JSON Fetching
    RequestQueue rq;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //SIUNTIMUI IR SERVA
        Button btn = (Button)this.findViewById(R.id.button);
        final TextView txt = (TextView)this.findViewById(R.id.textView);
        final RequestQueue queue = Volley.newRequestQueue((Context)this);
        final String url = "http://78.60.2.145:8001/registracija/";


        db = new DatabaseHelper(this);
        mTextUsername = (EditText)findViewById(R.id.eidttext_username);
        mTextEmail = (EditText)findViewById(R.id.eidttext_email);
        mTextPassword = (EditText)findViewById(R.id.edittext_password);
        mTextCnfPassword = (EditText)findViewById(R.id.edittext_cnf_password);
        mButtonRegister = (Button) findViewById(R.id.button_register);
        mTextViewLogin = (TextView) findViewById(R.id.testview_login);
        mTextViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent (RegisterActivity.this, MainActivity.class);
                startActivity(LoginIntent);
            }
        });
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            private static final String TAG ="RegisterActivity";

            @Override
            public void onClick(View view) {
                String user = mTextUsername.getText().toString().trim();
                String email = mTextEmail.getText().toString().trim();
//                try {
//                    user = encrypt(mTextUsername.getText().toString().trim(), mTextPassword.getText().toString().trim());
//                    //outputText.setText(user);
//
//                    //
//                    outputString = decrypt(user, mTextPassword.getText().toString().trim());
//                    Log.d(TAG, "onClick: " + user);
//                    Log.d(TAG, "onClick: " + outputString);
//                    //
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                String pwd = mTextPassword.getText().toString().trim();
//                try {
//                    pwd = encrypt(mTextPassword.getText().toString().trim(), raktas);
//                    //outputText.setText(user);
//
//                    //
//                    outputString = decrypt(pwd, raktas);
//                    Log.d(TAG, "onClick: " + pwd);
//                    Log.d(TAG, "onClick: " + outputString);
//                    //
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                String cnf_pwd = mTextCnfPassword.getText().toString().trim();
//                try {
//                    cnf_pwd = encrypt(mTextCnfPassword.getText().toString().trim(), raktas);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                final JSONObject req_data = new JSONObject();
                try {
                    req_data.put("id", "1");
                    req_data.put("username", user);
                    req_data.put("email", email);
                    req_data.put("password", pwd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(pwd.equals(cnf_pwd) && validateEmailAddress(mTextEmail) == true && validatePassword(mTextPassword) == true && validateUsername(mTextUsername)) {
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url, req_data, (Response.Listener) (new Response.Listener() {
                        public void onResponse(Object var1) {
                            this.onResponse((JSONObject) var1);
                            Log.d(TAG, "Rest Response: " + var1.toString());
                            Toast.makeText(RegisterActivity.this, "You have registered", Toast.LENGTH_SHORT).show();
                            Intent moveToLogin = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(moveToLogin);
                        }

                        public final void onResponse(JSONObject response) {

                        }
                    }), (Response.ErrorListener) (new Response.ErrorListener() {
                        public final void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "Rest Response: " + error.toString());
                            Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();
                        }
                    }));
                    queue.add((Request) jsonObjectRequest);
                    Toast.makeText((Context) RegisterActivity.this, (CharSequence) "Button clicked", Toast.LENGTH_SHORT).show();


                }
//                else{
//                    Toast.makeText(RegisterActivity.this, "Password is not matching", Toast.LENGTH_SHORT).show();
//
//                }
                    if (!pwd.equals(cnf_pwd)) {
                        Toast.makeText(RegisterActivity.this, "Password is not matching", Toast.LENGTH_SHORT).show();

                    }

            }
        });





        // KODAVIMAS
        try {
            outputString = encrypt(mTextPassword.getText().toString(), raktas);
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
        ///


        // Dekodavimas
        try {
            outputString = decrypt(outputString, raktas);
           //
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    private boolean validateEmailAddress (EditText mTextEmail){
        String emailinput = mTextEmail.getText().toString();

        if(!emailinput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailinput).matches()){
            return true;
        }else{
            Toast.makeText(this,"Invalid Email Address",Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean validatePassword (EditText mTextPassword){
        String passwordinput = mTextPassword.getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if(passwordinput.isEmpty()) {
            Toast.makeText(this, "Password field cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        }else if (!passwordinput.matches(passwordVal)) {
            Toast.makeText(this, "Password is too weak, it must have 1 special symbol, at least 4 characters", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private boolean validateUsername (EditText mTextUsername){
        String usernameinput = mTextUsername.getText().toString();
        String noWhiteSpace = "\\A\\w{4,20}\\z";

        if(usernameinput.isEmpty()) {
            Toast.makeText(this, "Username field cannot be empty", Toast.LENGTH_SHORT).show();
            return false;
        } else if (usernameinput.length() >= 15) {
            Toast.makeText(this, "Username is too long, 15 symbols is the max", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!usernameinput.matches(noWhiteSpace)) {
            Toast.makeText(this, "White space are not allowed, 4 characters at least", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }


    private String decrypt(String outputString, String raktas) throws Exception {
        SecretKeySpec key = generateKey(raktas);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = android.util.Base64.decode(outputString, android.util.Base64.DEFAULT);
        byte[] decValue = c.doFinal(decodedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }

    private String encrypt(String Data, String raktas) throws Exception {
        SecretKeySpec key = generateKey(raktas);
        Cipher c = Cipher.getInstance(AES);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
//        String encryptedValue = Base64.getEncoder(encVal, Base64.DEFAULT);
        String encryptedValue = android.util.Base64.encodeToString(encVal, android.util.Base64.DEFAULT);
        return  encryptedValue;
    }

    private SecretKeySpec generateKey(String password) throws Exception {
        final MessageDigest digest = MessageDigest.getInstance(("SHA-256"));
        byte[] bytes = password.getBytes("UTF-8");
        digest.update(bytes, 0, bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        return secretKeySpec;
    }


}