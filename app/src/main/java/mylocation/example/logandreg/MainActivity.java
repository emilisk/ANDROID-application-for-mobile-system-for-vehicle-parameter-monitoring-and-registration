package mylocation.example.logandreg;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {
    EditText mTextUsername;
    EditText mTextPassword;
    Button mButtonLogin;
    TextView mTextViewRegister;
    DatabaseHelper db;

    // kodavimas
    String outputString;
    String AES = "AES";
    TextView outputText;
    String raktas = "burbuliukas";
    private static final String TAG ="MainActivity";
    // kodavimas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);
        mTextUsername = (EditText)findViewById(R.id.eidttext_username);
        mTextPassword = (EditText)findViewById(R.id.edittext_password);
        mButtonLogin = (Button) findViewById(R.id.button_login);
        mTextViewRegister = (TextView) findViewById(R.id.testview_register);
        mTextViewRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick (View view) {
                Intent registerIntent = new Intent (MainActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = mTextUsername.getText().toString().trim();
                String pwd = mTextPassword.getText().toString().trim();
                try {
                    pwd = encrypt(mTextPassword.getText().toString().trim(), raktas);
                    //outputText.setText(user);


                    outputString = decrypt(pwd, raktas);
                    Log.d(TAG, "onClick: " + pwd);
                    Log.d(TAG, "onClick: " + outputString);
                } catch (Exception e) {
                    e.printStackTrace();
                }




                final JSONObject req_data = new JSONObject();
                try {
                    req_data.put("username", user);
                    req_data.put("password", pwd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                String URL = "http://78.60.2.145:8001/registracija2/";;
                final String TAG ="MainActivity";
                JsonObjectRequest objectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        URL,
                        req_data,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d(TAG, "pirma: " + response.toString());
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Rest Response: " + error.toString());
                            }
                        }
                );
                requestQueue.add(objectRequest);








                Boolean res = db.checkUser(user, pwd);
                if(res == true)
                {
                    Toast.makeText(MainActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
                    Intent mapsactivityIntent = new Intent (MainActivity.this, MapsActivity.class);
                    startActivity(mapsactivityIntent);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Login Error", Toast.LENGTH_SHORT).show();

                }
            }
        });











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