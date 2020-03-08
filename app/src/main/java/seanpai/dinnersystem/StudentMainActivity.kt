package seanpai.dinnersystem

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.firebase.messaging.FirebaseMessaging
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_student_main.*
import org.jetbrains.anko.alert
import org.json.JSONObject

class StudentMainActivity : AppCompatActivity() {
    private lateinit var preferences: SharedPreferences
    private lateinit var progBarHandler: ProgressBarHandler
    private var lighted = false
    private var ogLightness: Float = 0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)
        //indicator start
        progBarHandler = ProgressBarHandler(this)
        //indicator end

        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val isSubbed = preferences.getBoolean("isSubbed", false)
        if((constUsername == "06610089") && !isSubbed){
            FirebaseMessaging.getInstance().subscribeToTopic("seanpai.gsatnotify").addOnCompleteListener { task ->
                var msg = "訂閱通知失敗"
                if(task.isSuccessful){
                    msg = "訂閱每日通知成功"
                    preferences.edit().putBoolean("isSubbed", true).apply()
                }
                println(msg)
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
        }

        titleView.text = "歡迎使用午餐系統，\n${userInfo.getString("name").trimEnd()}."
        getBarcode()


        barcodeView.setOnLongClickListener {
            if(lighted){
                window.attributes.screenBrightness = ogLightness
            }else{
                ogLightness = window.attributes.screenBrightness
                window.attributes.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
            }
            true
        }
    }

    private fun getBarcode(){
        progBarHandler.show()
        val cardRequest = object: StringRequest(Method.POST, dsRequestURL,Response.Listener {
            if (isValidJson(it)) {
                posInfo = JSONObject(it)
                //indicator
                progBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                val metrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(metrics)
                val multiFormatWriter = MultiFormatWriter()
                val bitMatrix = multiFormatWriter.encode(posInfo.getString("card"),
                    BarcodeFormat.CODE_39,barcodeView.width,barcodeView.height)
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                barcodeView.setImageBitmap(bitmap)
                cardDetail.text = "卡號：${posInfo.getString("card")}\n餘額：${posInfo.getString("money")}元（非即時）"
            } else if (it.contains("\"Operation not allowed\"")){
                //indicator
                progBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert("工作階段已逾時", "請重新登入") {
                    positiveButton("OK") {
                        startActivity(Intent(this@StudentMainActivity, LoginActivity::class.java))
                    }
                }.show()
            } else {
                //indicator
                progBarHandler.hide()
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
            progBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert("請注意網路狀態，或通知開發人員!", "不知名的錯誤") {
                positiveButton("OK") {}
            }.show()
        }){
            override fun getParams(): MutableMap<String, String> {
                val postParam: MutableMap<String, String> = HashMap()
                postParam["cmd"] = "get_pos"
                return postParam
            }
        }
        VolleySingleton.getInstance(this).addToRequestQueue(cardRequest)
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
    fun toBefore(view: View){
        startActivity(Intent(view.context, BeforeHistoryActivity::class.java))
    }


}
