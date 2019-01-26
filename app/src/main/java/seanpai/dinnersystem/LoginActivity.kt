package seanpai.dinnersystem
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import org.jetbrains.anko.alert
import org.jetbrains.anko.toast
import java.net.*

class LoginActivity : AppCompatActivity() {
    private var queue:RequestQueue? = null
    private val preferences = getSharedPreferences("user", Context.MODE_PRIVATE)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        CookieHandler.setDefault(CookieManager(null, CookiePolicy.ACCEPT_ALL))
        queue = Volley.newRequestQueue(this)
        if(preferences.getString("username",null) !=null){
            val name = preferences.getString("username",null)!!
            remButton.visibility = View.VISIBLE
            remButton.isEnabled = true
            remButton.text = "以${name}登入"
        }
    }
    override fun onStop() {
        super.onStop()
        queue!!.stop()
    }
    var back = true
    override fun onBackPressed() {
        //super.onBackPressed()
        if(back){
            back = false
            toast("再按一次以退出")
        }else{
            this.finishAffinity()
        }
    }

    fun remLogin(view: View){
        val usr = preferences.getString("username", "")!!
        val psw = preferences.getString("password", "")!!
        val timeStamp = (System.currentTimeMillis() / 1000).toString()
        val hashOri = "{\"id\":\"$usr\",\"password\":\"$psw\",\"time\":\"$timeStamp)\"}"
        val hash = hashOri.sha512()
        val url = "${dsURL("login")}&id=$usr&hash=$hash&device_id=HELLO_FROM_ANDROID"
        val loginRequest = StringRequest(url,Response.Listener { string ->
            if (isValidJson(string)){
                userInfo = JSONObject(string)
                startActivity(Intent(view.context,StudentMainActivity::class.java))
            }else {

            }
        },Response.ErrorListener { error ->
            alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                positiveButton("OK"){}
            }
        })
        queue!!.add(loginRequest)
    }
    fun login(view: View) {
        val usr = username.text.toString()
        val psw = password.text.toString()
        val timeStamp = (System.currentTimeMillis() / 1000).toString()
        val hashOri = "{\"id\":\"$usr\",\"password\":\"$psw\",\"time\":\"$timeStamp)\"}"
        val hash = hashOri.sha512()
        val url = "${dsURL("login")}&id=$usr&hash=$hash&device_id=HELLO_FROM_ANDROID"
        val loginRequest = StringRequest(url,Response.Listener { string ->
            if (isValidJson(string)){
                userInfo = JSONObject(string)
                if (remSwitch.isChecked){
                    preferences.edit()
                        .putString("username", usr)
                        .putString("password", psw)
                        .putString("name", userInfo.getString("name"))
                        .apply()
                }
                alert("歡迎進入點餐系統,${userInfo.getString("name")}","登入成功"){
                    positiveButton("OK"){
                        startActivity(Intent(view.context,StudentMainActivity::class.java))
                    }
                }
            }else {
                alert("請注意帳號密碼是否錯誤，若持續失敗請通知開發人員!","登入失敗"){
                    positiveButton("OK"){}
                }
            }
        },Response.ErrorListener { error ->
            alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                positiveButton("OK"){}
            }
        })
        queue!!.add(loginRequest)
    }


}
