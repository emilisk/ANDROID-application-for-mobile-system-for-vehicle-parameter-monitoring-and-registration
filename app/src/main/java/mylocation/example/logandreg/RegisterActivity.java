package mylocation.example.logandreg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import kotlin.jvm.internal.Intrinsics;

public class RegisterActivity extends AppCompatActivity {
    DatabaseHelper db;
    EditText mTextUsername;
    EditText mTextPassword;
    EditText mTextCnfPassword;
    Button mButtonRegister;
    TextView mTextViewLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


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
            @Override
            public void onClick(View view) {
                String user = mTextUsername.getText().toString().trim();
                String pwd = mTextPassword.getText().toString().trim();
                String cnf_pwd = mTextCnfPassword.getText().toString().trim();

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




    }
}