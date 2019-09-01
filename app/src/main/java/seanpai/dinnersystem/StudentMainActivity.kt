package seanpai.dinnersystem

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_student_main.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.centerInParent
import org.json.JSONObject

class StudentMainActivity : AppCompatActivity() {
    private var preferences: SharedPreferences? = null

    private lateinit var indicatorView: View
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)
        //indicator start
        indicatorView = View(this)
        indicatorView.setBackgroundResource(R.color.colorPrimaryDark)
        val viewParam = RelativeLayout.LayoutParams(-1, -1)
        viewParam.centerInParent()
        indicatorView.layoutParams = viewParam
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyle)
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
        //indicator end
//        val balanceURL = dsURL("get_money")
//        val balanceRequest = StringRequest(balanceURL, Response.Listener {
//            if (isInt(it)){
//                balance = it.toInt()
//            }else {
//                alert("查詢餘額失敗，我們已經派出最精銳的猴子去修理這個問題，若長時間出現此問題請通知開發人員！", "請重新登入") {
//                    positiveButton("OK") {
//                        startActivity(Intent(this@StudentMainActivity, LoginActivity::class.java))
//                    }
//                }.show()
//            }
//        }, Response.ErrorListener { alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
//            positiveButton("OK"){}
//        }.show() })
//        VolleySingleton.getInstance(this).addToRequestQueue(balanceRequest)

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isSubbed = preferences!!.getBoolean("isSubbed", false)
        if((constUsername == "06610089" || constUsername == "seanpai" ) && !isSubbed){
            FirebaseMessaging.getInstance().subscribeToTopic("seanpai.gsatnotify").addOnCompleteListener { task ->
                var msg = "訂閱通知失敗"
                if(task.isSuccessful){
                    msg = "訂閱每日通知成功"
                    preferences!!.edit().putBoolean("isSubbed", true).apply()
                }
                println(msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun toOrder(view: View){
        startActivity(Intent(view.context,StuOrderListActivity::class.java))
    }

    fun toHis(view:View){
        startActivity(Intent(view.context,MainHistoryActivity::class.java))
    }
    fun toMore(view: View){
        startActivity(Intent(view.context,MainMoreActivity::class.java))
    }
    fun showBarcode(view: View){
        //indicator
        indicatorView.visibility = View.VISIBLE
        indicatorView.bringToFront()
        progressBar.visibility = View.VISIBLE
        progressBar.bringToFront()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator

        val cardRequest = StringRequest(dsURL("get_pos"),Response.Listener {
            if (isValidJson(it)) {
                posInfo = JSONObject(it)
                //indicator
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                startActivity(Intent(view.context,MainBarcodeActivity::class.java))
            } else {
                //indicator
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert("查詢餘額失敗，我們已經派出最精銳的猴子去修理這個問題，若長時間出現此問題請通知開發人員！", "請重新登入") {
                    positiveButton("OK") {
                        startActivity(Intent(this@StudentMainActivity, LoginActivity::class.java))
                    }
                }.show()
            }
        }, Response.ErrorListener {
            //indicator
            indicatorView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert("請注意網路狀態，或通知開發人員!", "不知名的錯誤") {
                positiveButton("OK") {}
            }.show()
        })
        VolleySingleton.getInstance(this).addToRequestQueue(cardRequest)
    }
}
