package seanpai.dinnersystem

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import org.jetbrains.anko.alert
import org.jetbrains.anko.centerInParent
import org.jetbrains.anko.toast
import java.net.*

class LoginActivity : AppCompatActivity() {
    private var preferences: SharedPreferences? = null
    private lateinit var progressBarHandler: ProgressBarHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        CookieHandler.setDefault(CookieManager(null, CookiePolicy.ACCEPT_ALL))
        //indicator start
        progressBarHandler = ProgressBarHandler(this)
        //indicator end

        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelID = resources.getString(R.string.default_notification_channel_id)
            val channelName = resources.getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(channelID,channelName,NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

    }

    private var back = true
    override fun onBackPressed() {
        //super.onBackPressed()
        if(back){
            back = false
            toast("再按一次以退出")
        }else{
            this.finishAffinity()
        }
    }

    fun login(view: View) {
        //indicator
        progressBarHandler.show()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
        //keyboard
        username.onEditorAction(EditorInfo.IME_ACTION_DONE)
        password.onEditorAction(EditorInfo.IME_ACTION_DONE)
        val usr = username.text.toString()
        val psw = password.text.toString()
        val timeStamp = (System.currentTimeMillis() / 1000).toString()
        val hashOri = "{\"id\":\"$usr\",\"password\":\"$psw\",\"time\":\"$timeStamp\"}"
        println(hashOri)
        val hash = hashOri.sha512()
        //val url = "${dsURL("login")}&id=$usr&password=$psw&time=$timeStamp&device_id=HELLO_FROM_ANDROID"
        var loginRequest = object : StringRequest(Method.POST, dsRequestURL,Response.Listener { string ->
            println(isValidJson(string))
            if (isValidJson(string)){
                constPassword = psw
                constUsername = usr
                userInfo = JSONObject(string)
                //indicator
                progressBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                if (remSwitch.isChecked){
                    preferences!!.edit()
                        .putString("username", usr)
                        .putString("password", psw)
                        .putString("name", userInfo.getString("name").trimEnd())
                        .apply()
                }
                alert("歡迎進入點餐系統,${userInfo.getString("name").trimEnd()}","登入成功"){
                    positiveButton("OK"){
                        username.text.clear()
                        password.text.clear()
                        startActivity(Intent(view.context,StudentMainActivity::class.java))
                    }
                }.build().apply {
                    setCancelable(false)
                    setCanceledOnTouchOutside(false)
                }.show()
            }else {
                //indicator
                progressBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator

                alert("請注意帳號密碼是否錯誤，若持續失敗請通知開發人員!","登入失敗"){
                    positiveButton("OK"){}
                }.show()
            }
        },Response.ErrorListener { error ->
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            println(error)
            alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
            positiveButton("OK"){}
        }.show()
        }){
            override fun getParams(): MutableMap<String, String> {
                var postParam: MutableMap<String, String> = HashMap()
                postParam["cmd"] = "login"
                postParam["id"] = usr
                postParam["password"] = psw
                postParam["time"] = timeStamp
                postParam["device_id"] = "HELLO_FROM_ANDROID"
                return postParam
            }
        }

        if (usr.length == 5) {
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert("App版已不支援午餐股長，請透過網頁版查看班級訂單!", "不支援午餐股長") {
                positiveButton("OK") {}
            }.show()
        } else {
            VolleySingleton.getInstance(this).addToRequestQueue(loginRequest)
        }
    }
}
