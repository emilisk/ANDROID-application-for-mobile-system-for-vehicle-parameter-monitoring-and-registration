package mylocation.example.logandreg

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import mylocation.example.logandreg.R
import org.json.JSONObject

class Siuntimui : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_siuntimui)

        val btn = findViewById<Button>(R.id.button)
        val txt = findViewById<TextView>(R.id.textView)
        val queue = Volley.newRequestQueue(this)
        val url = "http://78.60.2.145:8001/test_api/"

        val req_data = JSONObject()
        req_data.put("id", "informacija")
        req_data.put("vardas", "testinis")
        req_data.put("pavarde", "netestinis")


        btn.setOnClickListener {
            txt.text = "Clicked"

//            val stringRequest = StringRequest(
//                Request.Method.GET, url,
//                Response.Listener { response ->
//                    txt.text = response.toString()
//                }, Response.ErrorListener { error -> txt.text = error.toString() })
//
//            queue.add(stringRequest)

            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, req_data,
                    Response.Listener { response ->
                        txt.text = "Response: %s".format(response.toString())
                    },
                    Response.ErrorListener {
                        error -> txt.text = error.toString()
                    }
            )

            queue.add(jsonObjectRequest)

            Toast.makeText(this, "Button clicked", Toast.LENGTH_LONG).show()
        }

    }
}



