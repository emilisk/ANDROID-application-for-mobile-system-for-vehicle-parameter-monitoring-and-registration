package mylocation.example.logandreg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {

    Button mButtonLogin2;
    Button mButtonRegister2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mButtonLogin2 = (Button) findViewById(R.id.buttonlogin2);
        mButtonRegister2 = (Button) findViewById(R.id.buttonregister2);

        mButtonLogin2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent (MainActivity2.this, MainActivity.class);
                Toast.makeText(MainActivity2.this, "Log in", Toast.LENGTH_SHORT).show();
                startActivity(LoginIntent);
            }
        });

        mButtonRegister2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent LoginIntent = new Intent (MainActivity2.this, RegisterActivity.class);
                Toast.makeText(MainActivity2.this, "Register", Toast.LENGTH_SHORT).show();
                startActivity(LoginIntent);
            }
        });


    }
}