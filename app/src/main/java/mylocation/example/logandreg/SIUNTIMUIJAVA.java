package mylocation.example.logandreg;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;


public final class SIUNTIMUIJAVA extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_siuntimui);
        Button btn = (Button)this.findViewById(R.id.button);
        final TextView txt = (TextView)this.findViewById(R.id.textView);
        final RequestQueue queue = Volley.newRequestQueue((Context)this);
        final String url = "http://78.60.2.145:8001/test_api/";

        final JSONObject req_data = new JSONObject();
        try {
            req_data.put("id", "GAVAI?");
            req_data.put("vardas", "JEIGUTAIPTAIZJBS");
            req_data.put("pavarde", "netestinis");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn.setOnClickListener((View.OnClickListener)(new View.OnClickListener() {

            public final void onClick(View it) {
                TextView var10000 = txt;
                Intrinsics.checkNotNullExpressionValue(var10000, "txt");
                var10000.setText((CharSequence)"Clicked");
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(1, url, req_data, (Response.Listener)(new Response.Listener() {
                    // $FF: synthetic method
                    // $FF: bridge method
                    public void onResponse(Object var1) {
                        this.onResponse((JSONObject)var1);
                    }

                    public final void onResponse(JSONObject response) {
                        TextView var10000 = txt;
                        Intrinsics.checkNotNullExpressionValue(var10000, "txt");
                        String var2 = "Response: %s";
                        Object[] var3 = new Object[]{response.toString()};
                        boolean var4 = false;
                        String var10001 = String.format(var2, Arrays.copyOf(var3, var3.length));
                        Intrinsics.checkNotNullExpressionValue(var10001, "java.lang.String.format(this, *args)");
                        var10000.setText((CharSequence)var10001);
                    }
                }), (Response.ErrorListener)(new Response.ErrorListener() {
                    public final void onErrorResponse(VolleyError error) {
                        TextView var10000 = txt;
                        Intrinsics.checkNotNullExpressionValue(var10000, "txt");
                        var10000.setText((CharSequence)error.toString());
                    }
                }));
                queue.add((Request)jsonObjectRequest);
                Toast.makeText((Context)SIUNTIMUIJAVA.this, (CharSequence)"Button clicked", Toast.LENGTH_SHORT).show();
            }
        }));
    }
}
