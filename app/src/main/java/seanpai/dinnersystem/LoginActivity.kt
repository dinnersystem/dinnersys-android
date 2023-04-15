package seanpai.dinnersystem

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.firebase.crashlytics.FirebaseCrashlytics
import org.json.JSONArray
import org.json.JSONObject
import seanpai.dinnersystem.databinding.ActivityLoginBinding
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy

class LoginActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences
    private lateinit var progressBarHandler: ProgressBarHandler
    private lateinit var keystoreHelper: KeyStoreHelper
    private lateinit var preferencesHelper: SharedPreferencesHelper
    private lateinit var activityBinding: ActivityLoginBinding
    var schoolList: MutableList<String> = mutableListOf()
    var chosenName = ""
    var chosenID = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)
        CookieHandler.setDefault(CookieManager(null, CookiePolicy.ACCEPT_ALL))
        //indicator start
        progressBarHandler = ProgressBarHandler(this)
        //indicator end

        progressBarHandler.show()
        val spinnerRequest = object: StringRequest(Method.POST, dsRequestURL, Response.Listener {
            progressBarHandler.hide()
            if(isValidJson(it)){
                schoolInfo = JSONArray(it)
                for( i in 0 until schoolInfo.length()){
                    val school = schoolInfo.getJSONObject(i)
                    schoolList.add(school.getString("name"))
                }
                val adapter = ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item,schoolList)
                activityBinding.spinner.adapter = adapter
                activityBinding.spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        chosenID = schoolInfo.getJSONObject(position).getString("id")
                        chosenName = schoolInfo.getJSONObject(position).getString("name")
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }

                if (schoolList.size > 0){
                    chosenID = schoolInfo.getJSONObject(0).getString("id")
                    chosenName = schoolInfo.getJSONObject(0).getString("name")
                }else{
//                    alert("發生錯誤，請聯絡開發人員！\n錯誤訊息：No School.","無法取得學校資訊"){
//                        positiveButton("OK"){
//                            startActivity(Intent(this@LoginActivity, RemLoginActivity::class.java))
//                        }
//                    }
                    AlertDialog.Builder(this)
                        .setTitle("無法取得學校資訊")
                        .setMessage("發生錯誤，請聯絡開發人員！\n錯誤訊息：No School.")
                        .setPositiveButton("OK"){ _, _ ->
                            startActivity(Intent(this@LoginActivity, RemLoginActivity::class.java))
                        }
                        .show()
                }
            }else{
//                alert("發生錯誤，請聯絡開發人員！\n錯誤訊息：$it","無法取得學校資訊"){
//                    positiveButton("OK"){
//                        startActivity(Intent(this@LoginActivity, RemLoginActivity::class.java))
//                    }
//                }
                AlertDialog.Builder(this)
                    .setTitle("無法取得學校資訊")
                    .setMessage("發生錯誤，請聯絡開發人員！\n錯誤訊息：$it")
                    .setPositiveButton("OK"){ _, _ ->
                        startActivity(Intent(this@LoginActivity, RemLoginActivity::class.java))
                    }
                    .show()
            }
        }, Response.ErrorListener {
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            //logout
//            alert("請確定你的網路連接！","無法取得學校資訊"){
//                positiveButton("OK"){
//                    startActivity(Intent(this@LoginActivity, RemLoginActivity::class.java))
//                }
//            }
            AlertDialog.Builder(this)
                .setTitle("無法取得學校資訊")
                .setMessage("請確定你的網路連接！")
                .setPositiveButton("OK"){ _, _ ->
                    startActivity(Intent(this@LoginActivity, RemLoginActivity::class.java))
                }
                .show()
        }){
            override fun getParams(): MutableMap<String, String> {
                val postParam: MutableMap<String, String> = HashMap()
                postParam["cmd"] = "show_organization"
                return postParam
            }
        }

        VolleySingleton.getInstance(this).addToRequestQueue(spinnerRequest)

        preferences = this.getSharedPreferences("dinnersys_data",Context.MODE_PRIVATE)
        preferencesHelper = SharedPreferencesHelper(applicationContext)
        keystoreHelper = KeyStoreHelper(applicationContext,preferencesHelper)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelID = resources.getString(R.string.default_notification_channel_id)
            val channelName = resources.getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(channelID,channelName,NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }

    }


    fun login(view: View) {
        //indicator
        progressBarHandler.show()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
        //keyboard
        activityBinding.username.onEditorAction(EditorInfo.IME_ACTION_DONE)
        activityBinding.password.onEditorAction(EditorInfo.IME_ACTION_DONE)
        val usr = activityBinding.username.text.toString()
        val psw = activityBinding.password.text.toString()
        val timeStamp = (System.currentTimeMillis() / 1000).toString()
        val hashOri = "{\"id\":\"$usr\",\"password\":\"$psw\",\"time\":\"$timeStamp\"}"
        println(hashOri)
        val hash = hashOri.sha512()
        //val url = "${dsURL("login")}&id=$usr&password=$psw&time=$timeStamp&device_id=HELLO_FROM_ANDROID"
        val loginRequest = object : StringRequest(Method.POST, dsRequestURL,Response.Listener { string ->
            println(isValidJson(string))
            if (isValidJson(string)){
                constPassword = psw
                constUsername = usr
                userInfo = JSONObject(string)
                //indicator
                progressBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                if (activityBinding.remSwitch.isChecked){
                    preferences.edit()
                        .putString("username", usr)
                        .putString("org_id", chosenID)
                        .putString("name", userInfo.getString("name").trimEnd())
                        .apply()

                    preferencesHelper.setInput(keystoreHelper.encrypt(psw))
                }
                FirebaseCrashlytics.getInstance().setUserId(usr)
                if(userInfo.getJSONArray("valid_oper").toString().contains("select_class") && !userInfo.getJSONArray("valid_oper").toString().contains("select_others")){
                    startActivity(Intent(view.context,DinnermanMainActivity::class.java))
                }else{
                    startActivity(Intent(view.context,StudentMainActivity::class.java))
                }

            }else {
                //indicator
                progressBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator

//                alert("請注意帳號密碼是否錯誤，若持續失敗請通知開發人員!","登入失敗"){
//                    positiveButton("OK"){}
//                }.show()
                AlertDialog.Builder(this)
                    .setTitle("登入失敗")
                    .setMessage("請注意帳號密碼是否錯誤，若持續失敗請通知開發人員!")
                    .setPositiveButton("OK"){ _, _ ->}
                    .show()
            }
        },Response.ErrorListener { error ->
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            println(error)
//            alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
//                positiveButton("OK"){}
//            }.show()
            AlertDialog.Builder(this)
                .setTitle("不知名的錯誤")
                .setMessage("請注意網路狀態，或通知開發人員!")
                .setPositiveButton("OK"){ _, _ ->}
                .show()
        }){
            override fun getParams(): MutableMap<String, String> {
                val postParam: MutableMap<String, String> = HashMap()
                postParam["cmd"] = "login"
                postParam["id"] = usr
                postParam["password"] = psw
                postParam["time"] = timeStamp
                postParam["org_id"] = chosenID
                postParam["device_id"] = "HELLO_FROM_ANDROID"
                return postParam
            }
        }

//        if (usr.length == 5) {
//            //indicator
//            progressBarHandler.hide()
//            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//            //indicator
//            alert("App版已不支援午餐股長，請透過網頁版查看班級訂單!", "不支援午餐股長") {
//                positiveButton("OK") {}
//            }.show()
//        } else {
            VolleySingleton.getInstance(this).addToRequestQueue(loginRequest)
//        }
    }
}
