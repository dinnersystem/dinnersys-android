package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.json.JSONArray
import org.json.JSONObject
import seanpai.dinnersystem.databinding.ActivityStudentMainBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class StudentMainActivity : AppCompatActivity() {
    //private lateinit var preferences: SharedPreferences
    private lateinit var progBarHandler: ProgressBarHandler
    private lateinit var gestureDetector: GestureDetector
    private lateinit var activityBinding: ActivityStudentMainBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityStudentMainBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)
        //indicator start
        progBarHandler = ProgressBarHandler(this)
        //indicator end

        ogBrightness = this.window.attributes.screenBrightness


        if(!userInfo.has("name")){
//            alert("工作階段已逾時", "請重新登入") {
//                positiveButton("OK") {
//                    startActivity(Intent(this@StudentMainActivity, RemLoginActivity::class.java))
//                }
//            }.show()
            AlertDialog.Builder(this)
                .setTitle("工作階段已逾時")
                .setMessage("請重新登入")
                .setPositiveButton("OK") { _, _ ->
                    startActivity(Intent(this@StudentMainActivity, RemLoginActivity::class.java))
                }
                .show()
            return
        }

        activityBinding.titleView.text = "歡迎使用午餐系統，\n${userInfo.getString("name").trimEnd()}."
        getBarcode()

        gestureDetector = GestureDetector(this, DoubleTapListener(this))

        val onTouchView = OnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
        }

        activityBinding.barcodeView.setOnTouchListener(onTouchView)

        activityBinding.barcodeView.setOnLongClickListener {
            print("I've heard something long pressed")
            if(lighted){
                val layoutParams = this.window.attributes
                layoutParams.screenBrightness = ogBrightness
                this.window.attributes = layoutParams
            }else{
                ogBrightness = this.window.attributes.screenBrightness
                val layoutParams = this.window.attributes
                layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
                this.window.attributes = layoutParams
            }
            lighted = !lighted
            true
        }
    }

    private var back = true

    override fun onBackPressed() {
        super.onBackPressed()
        //super.onBackPressed()
        if(back){
            back = false
//            toast("再按一次以登出")
            Toast.makeText(this, "再按一次以登出", Toast.LENGTH_SHORT).show()
        }else{
            startActivity(Intent(this@StudentMainActivity, RemLoginActivity::class.java))
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
                if(!posInfo.has("card")){
//                    alert("查詢餘額失敗，我們已經派出最精銳的猴子去修理這個問題，若長時間出現此問題請通知開發人員！", "請重新登入") {
//                        positiveButton("OK") {
//                            startActivity(Intent(this@StudentMainActivity, RemLoginActivity::class.java))
//                        }
//                    }.show()
                    AlertDialog.Builder(this)
                        .setTitle("查詢餘額失敗，我們已經派出最精銳的猴子去修理這個問題，若長時間出現此問題請通知開發人員！")
                        .setMessage("請重新登入")
                        .setPositiveButton("OK") { _, _ ->
                            startActivity(Intent(this@StudentMainActivity, RemLoginActivity::class.java))
                        }
                        .show()
                    return@Listener
                }
                val metrics = DisplayMetrics()
                windowManager.defaultDisplay.getMetrics(metrics)
                val multiFormatWriter = MultiFormatWriter()
                val bitMatrix = multiFormatWriter.encode(posInfo.getString("card"),
                    BarcodeFormat.CODE_39,activityBinding.barcodeView.width,activityBinding.barcodeView.height)
                val barcodeEncoder = BarcodeEncoder()
                val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                activityBinding.barcodeView.setImageBitmap(bitmap)
                balance = posInfo.getString("money").toInt()

                val now = getCurrentDateTime()
                val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.TAIWAN)
                val dateString = dateFormat.format(now)

                activityBinding.cardDetail.text = "卡號：${posInfo.getString("card")}\n餘額：${posInfo.getString("money")}元（非即時）\n${dateString}"
            } else if (it.contains("Operation not allowed")){
                //indicator
                progBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
//                alert("工作階段已逾時", "請重新登入") {
//                    positiveButton("OK") {
//                        startActivity(Intent(this@StudentMainActivity, RemLoginActivity::class.java))
//                    }
//                }.show()
                AlertDialog.Builder(this)
                    .setTitle("工作階段已逾時")
                    .setMessage("請重新登入")
                    .setPositiveButton("OK") { _, _ ->
                        startActivity(Intent(this@StudentMainActivity, RemLoginActivity::class.java))
                    }
                    .show()
            } else {
                //indicator
                progBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
//                alert("查詢餘額失敗，我們已經派出最精銳的猴子去修理這個問題，若長時間出現此問題請通知開發人員！", "請重新登入") {
//                    positiveButton("OK") {
//                        startActivity(Intent(this@StudentMainActivity, RemLoginActivity::class.java))
//                    }
//                }.show()
                AlertDialog.Builder(this)
                    .setTitle("查詢餘額失敗，我們已經派出最精銳的猴子去修理這個問題，若長時間出現此問題請通知開發人員！")
                    .setMessage("請重新登入")
                    .setPositiveButton("OK") { _, _ ->
                        startActivity(Intent(this@StudentMainActivity, RemLoginActivity::class.java))
                    }
                    .show()
            }
        }, Response.ErrorListener {
            //indicator
            progBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            println(it)
//            alert("請注意網路狀態，或通知開發人員!", "不知名的錯誤") {
//                positiveButton("OK") {}
//            }.show()
            AlertDialog.Builder(this)
                .setTitle("請注意網路狀態，或通知開發人員!")
                .setMessage("不知名的錯誤")
                .setPositiveButton("OK") { _, _ -> }
                .show()
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
        revHistoryArr = JSONArray("[]")
        historyArr = JSONArray("[]")
        startActivity(Intent(view.context,MainHistoryActivity::class.java))
    }
    fun toMore(view: View){
        startActivity(Intent(view.context,MainMoreActivity::class.java))
    }
    fun toBefore(view: View){
        revHistoryArr = JSONArray("[]")
        historyArr = JSONArray("[]")
        startActivity(Intent(view.context, BeforeHistoryActivity::class.java))
    }

    class DoubleTapListener(context: Context): GestureDetector.SimpleOnGestureListener() {
        private val mContext = context as StudentMainActivity
        override fun onDoubleTap(e: MotionEvent): Boolean {
            println("double tap")
            if(lighted){
                val layoutParams = mContext.window.attributes
                layoutParams.screenBrightness = ogBrightness
                mContext.window.attributes = layoutParams
            }else{
                ogBrightness = mContext.window.attributes.screenBrightness
                val layoutParams = mContext.window.attributes
                layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
                mContext.window.attributes = layoutParams
            }
            lighted = !lighted
            return true
        }

        override fun onDown(e: MotionEvent): Boolean {
            return true
        }
    }

}

