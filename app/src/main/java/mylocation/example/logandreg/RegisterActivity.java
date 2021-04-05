package mylocation.example.logandreg;

import android.content.Context;
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

public class RegisterActivity extends AppCompatActivity {
    DatabaseHelper db;
    EditText mTextUsername;
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

//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        String URL = "http://78.60.2.145:8001/registracija2/";;
//        final String TAG ="RegisterActivity";
//        JsonObjectRequest objectRequest = new JsonObjectRequest(
//                Request.Method.GET,
//                URL,
//                null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d(TAG, "pirma: " + response.toString());
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e("Rest Response", error.toString());
//                    }
//                }
//        );
//        requestQueue.add(objectRequest);


        //SIUNTIMUI IR SERVA
        Button btn = (Button)this.findViewById(R.id.button);
        final TextView txt = (TextView)this.findViewById(R.id.textView);
        final RequestQueue queue = Volley.newRequestQueue((Context)this);
        final String url = "http://78.60.2.145:8001/registracija/";

//        final JSONObject req_data = new JSONObject();
//        try {
//            req_data.put("id", "1");
//            req_data.put("username", mTextUsername);
//            req_data.put("password", mTextPassword);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        /////


        db = new DatabaseHelper(this);
        mTextUsername = (EditText)findViewById(R.id.eidttext_username);
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
                try {
                    pwd = encrypt(mTextPassword.getText().toString().trim(), raktas);
                    //outputText.setText(user);

                    //
                    outputString = decrypt(pwd, raktas);
                    Log.d(TAG, "onClick: " + pwd);
                    Log.d(TAG, "onClick: " + outputString);
                    //
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String cnf_pwd = mTextCnfPassword.getText().toString().trim();
                try {
                    cnf_pwd = encrypt(mTextCnfPassword.getText().toString().trim(), raktas);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                final JSONObject req_data = new JSONObject();
                try {
                    req_data.put("id", "1");
                    req_data.put("username", user);
                    req_data.put("password", pwd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(pwd.equals(cnf_pwd)){
                    long val = db.addUser(user,pwd);
                    if(val > 0){
                    Toast.makeText(RegisterActivity.this, "You have registered", Toast.LENGTH_SHORT).show();
                    Intent moveToLogin = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(moveToLogin);

                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url, req_data, (Response.Listener)(new Response.Listener() {
                            // $FF: synthetic method
                            // $FF: bridge method
                            public void onResponse(Object var1) {
                                this.onResponse((JSONObject)var1);
                                //Log.d(TAG, "Response: " + var1.toString());
                            }

                            public final void onResponse(JSONObject response) {
                                //TextView var10000 = txt;
                                //Intrinsics.checkNotNullExpressionValue(var10000, "txt");
                                //String var2 = "Response: %s";
                                //Object[] var3 = new Object[]{response.toString()};
                                //boolean var4 = false;
                                //String var10001 = String.format(var2, Arrays.copyOf(var3, var3.length));
                                //Intrinsics.checkNotNullExpressionValue(var10001, "java.lang.String.format(this, *args)");
                                //var10000.setText((CharSequence)var10001);
                                //Log.d(TAG, "Response: " + response.toString());
                            }
                        }), (Response.ErrorListener)(new Response.ErrorListener() {
                            public final void onErrorResponse(VolleyError error) {
                                //TextView var10000 = txt;
                                //Intrinsics.checkNotNullExpressionValue(var10000, "txt");
                                //var10000.setText((CharSequence)error.toString());
                            }
                        }));
                        queue.add((Request)jsonObjectRequest);
                        Toast.makeText((Context)RegisterActivity.this, (CharSequence)"Button clicked", Toast.LENGTH_SHORT).show();





                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();

                    }
                }
                else{
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


        //JSON FETCHING
//        rq = Volley.newRequestQueue(this);
//        sendjsonrequest();
        //



    }

//    public void sendjsonrequest(){
//
//        final String url = "http://78.60.2.145:8001/registracija2/";
//        final String TAG ="RegisterActivity";
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url, null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.d(TAG, "Response: " + response.toString());
//                Log.e("Rest response", response.toString());
//                Log.d(TAG, "Response: gauta ");
////                String user2;
////                String pwd2;
////                try {
////                    user2 = response.getString("username");
////                    pwd2 = response.getString("password");
////                } catch (JSONException e) {
////                    e.printStackTrace();
////                }
//
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("Error response", error.toString());
//
//            }
//        });
//        rq.add(jsonObjectRequest);
//
//
//    }







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