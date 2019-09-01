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
    private lateinit var indicatorView : View
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        CookieHandler.setDefault(CookieManager(null, CookiePolicy.ACCEPT_ALL))
        //indicator start
        indicatorView = View(this)
        indicatorView.setBackgroundResource(R.color.colorPrimaryDark)
        val viewParam = RelativeLayout.LayoutParams(-1, -1)
        viewParam.centerInParent()
        indicatorView.layoutParams = viewParam
        progressBar = ProgressBar(this,null, android.R.attr.progressBarStyle)
        progressBar.isIndeterminate = true
        val prams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        prams.centerInParent()
        progressBar.layoutParams = prams
        indicatorView.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        layout.addView(indicatorView)
        layout.addView(progressBar)
        indicatorView.bringToFront()
        progressBar.bringToFront()
        //indicator end

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if(preferences!!.getString("username",null) !=null){
            val name = preferences!!.getString("name",null)!!
            remButton.visibility = View.VISIBLE
            remButton.isEnabled = true
            remButton.text = "以${name}登入"
        }
        val url = "https://dinnersystem.com/dinnersys_beta/frontend/u_move_u_dead/version.txt"
        //val url = "http://25.10.211.133/dinnersys_beta/frontend/u_move_u_dead/version.txt"
        val versionRequest = StringRequest(url, Response.Listener {
            //indicator
            indicatorView.visibility = View.VISIBLE
            indicatorView.bringToFront()
            progressBar.visibility = View.VISIBLE
            progressBar.bringToFront()
            window.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
            //indicator
            var update = true
            val version = JSONObject(it).getJSONArray("android")
            println(it)
            for (i in 0 until version.length()) {
                val ver = version.getInt(i)
                if (ver == currentVersion) {
                    update = false
                    break
                }
            }
            if (update) {
                //indicator
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert("請至Google Play更新最新版本的點餐系統!", "偵測到更新版本") {
                    positiveButton("OK(跳轉至GooglePlay)") {
                        val packageName = packageName
                        try {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)))
                        } catch (e: android.content.ActivityNotFoundException) {
                            startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)
                                )
                            )
                        }
                    }
                }.show()

            }
            //indicator
            indicatorView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator

        }, Response.ErrorListener {
            alert("請注意網路連線", "不知名的錯誤") {
                positiveButton("OK") {}
            }
        })
        VolleySingleton.getInstance(this).addToRequestQueue(versionRequest)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelID = resources.getString(R.string.default_notification_channel_id)
            val channelName = resources.getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(channelID,channelName,NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

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
        //indicator
        indicatorView.visibility = View.VISIBLE
        indicatorView.bringToFront()
        progressBar.visibility = View.VISIBLE
        progressBar.bringToFront()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
        //keyboard
        username.onEditorAction(EditorInfo.IME_ACTION_DONE)
        password.onEditorAction(EditorInfo.IME_ACTION_DONE)
        val usr = preferences!!.getString("username", "")!!
        val psw = preferences!!.getString("password", "")!!
        //val timeStamp = (System.currentTimeMillis() / 1000).toString()
        //val hashOri = "{\"id\":\"$usr\",\"password\":\"$psw\",\"time\":\"$timeStamp\"}"
        //val hash = hashOri.sha512()
        val url = "${dsURL("login")}&id=$usr&password=$psw&device_id=HELLO_FROM_ANDROID"
        val loginRequest = StringRequest(url,Response.Listener { string ->
            if (isValidJson(string)){
                constPassword = psw
                constUsername = usr
                userInfo = JSONObject(string)
                //indicator
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert("歡迎進入點餐系統,${userInfo.getString("name")}","登入成功"){
                    username.text.clear()
                    password.text.clear()
                    positiveButton("OK"){
                        startActivity(Intent(view.context,StudentMainActivity::class.java))
                    }
                }.show()
            }else {
                //indicator
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert("請注意帳號密碼是否錯誤，若持續失敗請通知開發人員!","登入失敗"){
                    positiveButton("OK"){}
                }.show()
            }
        },Response.ErrorListener { error ->
            //indicator
            indicatorView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            println(error)
            alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                positiveButton("OK"){}
            }.show()
        })
        VolleySingleton.getInstance(this).addToRequestQueue(loginRequest)
    }
    fun login(view: View) {
        //indicator
        indicatorView.visibility = View.VISIBLE
        indicatorView.bringToFront()
        progressBar.visibility = View.VISIBLE
        progressBar.bringToFront()
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
        val url = "${dsURL("login")}&id=$usr&password=$psw&time=$timeStamp&device_id=HELLO_FROM_ANDROID"
        println(url)

        val loginRequest = StringRequest(url,Response.Listener { string ->
            println(isValidJson(string))
            if (isValidJson(string)){
                constPassword = psw
                constUsername = usr
                userInfo = JSONObject(string)
                //indicator
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                if (remSwitch.isChecked){
                    preferences!!.edit()
                        .putString("username", usr)
                        .putString("password", psw)
                        .putString("name", userInfo.getString("name").dropLast(1))
                        .apply()
                }
                alert("歡迎進入點餐系統,${userInfo.getString("name")}","登入成功"){
                    positiveButton("OK"){
                        username.text.clear()
                        password.text.clear()
                        startActivity(Intent(view.context,StudentMainActivity::class.java))
                    }
                }.show()
            }else {
                //indicator
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert("請注意帳號密碼是否錯誤，若持續失敗請通知開發人員!","登入失敗"){
                    positiveButton("OK"){}
                }.show()
            }
        },Response.ErrorListener { error ->
            //indicator
            indicatorView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            println(error)
            alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
            positiveButton("OK"){}
        }.show()
        })
        if (usr.length == 5) {
            //indicator
            indicatorView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
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
