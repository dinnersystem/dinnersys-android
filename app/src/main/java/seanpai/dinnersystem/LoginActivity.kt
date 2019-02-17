package seanpai.dinnersystem

import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.constraint.ConstraintLayout
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject
import org.jetbrains.anko.alert
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
        val viewParam = LinearLayout.LayoutParams(-1,-1)
        viewParam.gravity = Gravity.CENTER
        indicatorView.layoutParams = viewParam
        progressBar = ProgressBar(this,null, android.R.attr.progressBarStyle)
        progressBar.isIndeterminate = true
        val prams: LinearLayout.LayoutParams = LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        prams.gravity = Gravity.CENTER
        progressBar.layoutParams = prams
        indicatorView.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        //indicator end

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        if(preferences!!.getString("username",null) !=null){
            val name = preferences!!.getString("name",null)!!
            remButton.visibility = View.VISIBLE
            remButton.isEnabled = true
            remButton.text = "以${name}登入"
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
        val usr = preferences!!.getString("username", "")!!
        val psw = preferences!!.getString("password", "")!!
        val timeStamp = (System.currentTimeMillis() / 1000).toString()
        val hashOri = "{\"id\":\"$usr\",\"password\":\"$psw\",\"time\":\"$timeStamp\"}"
        val hash = hashOri.sha512()
        val url = "${dsURL("login")}&id=$usr&hash=$hash&device_id=HELLO_FROM_ANDROID"
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
            alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                positiveButton("OK"){}
            }.show()
        })
        VolleySingleton.getInstance(this).addToRequestQueue(loginRequest)
    }
    fun login(view: View) {
        //indicator
        indicatorView.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
        val usr = username.text.toString()
        val psw = password.text.toString()
        val timeStamp = (System.currentTimeMillis() / 1000).toString()
        val hashOri = "{\"id\":\"$usr\",\"password\":\"$psw\",\"time\":\"$timeStamp\"}"
        println(hashOri)
        val hash = hashOri.sha512()
        val url = "${dsURL("login")}&id=$usr&hash=$hash&device_id=HELLO_FROM_ANDROID"
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


}
