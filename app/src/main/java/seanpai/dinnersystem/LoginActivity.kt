package seanpai.dinnersystem

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import java.net.*


class LoginActivity : AppCompatActivity() {
    var queue:RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        CookieHandler.setDefault(CookieManager(null, CookiePolicy.ACCEPT_ALL))
        queue = Volley.newRequestQueue(this)
    }
    override fun onStop() {
        super.onStop()
        queue!!.stop()
    }
    //TODO: remPW
    fun login(view: View) {
        val usr = username.text.toString()
        val psw = password.text.toString()
        val url = "${dsURL("login")}&id=$usr&password=$psw&device_id=HELLO_FROM_ANDROID"
        val loginRequest = StringRequest(url,Response.Listener { string ->
            if(!isValidJson(string)){
                // TODO: wrong password
            }else{
                userInfo = JSONObject(string)
                // TODO: login success, see if student or dm
                if (userInfo.has("select_class")){        //dm

                }else{                                          //student
                    startActivity(Intent(view.context,StudentMainActivity::class.java))
                }
            }
        },Response.ErrorListener { error ->
            // TODO: Unknown error, maybe Internet

        })
        queue!!.add(loginRequest)
    }


}
