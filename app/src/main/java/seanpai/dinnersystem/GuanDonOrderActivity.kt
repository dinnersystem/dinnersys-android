package seanpai.dinnersystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_guan_don_order.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.centerInParent
import org.json.JSONArray
import org.threeten.bp.LocalDateTime
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class GuanDonOrderActivity : AppCompatActivity() {
    private lateinit var progressBarHandler: ProgressBarHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guan_don_order)
        //indicator start
        progressBarHandler = ProgressBarHandler(this)
        //indicator end
        val confirmString = """
            您選擇的餐點是${selOrder1.name}，價錢為${selOrder1.cost}，確定請選擇時間後按訂餐。
            請於取餐時間兩個小時前訂餐！
        """.trimIndent()
        this.confirmText.text = confirmString
    }


    fun sendGuanDonOrder(view:View){
        //indicator
        progressBarHandler.show()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
        val now = getCurrentDateTime()
        val hourFormat = SimpleDateFormat("HHmm", Locale("zh-TW"))
        val hour = hourFormat.format(now).toInt()
        val fullFormat = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        val selTime = (if (timeButton.isChecked) "-12:00:00" else "-11:00:00")
        if((selTime == "-12:00:00" && hour>1010) || (selTime == "-11:00:00" && hour>910)) {
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            val hour = if (selTime == "-12:00:00") "十" else "九"
            alert("早上${hour}點十分後無法訂餐，明日請早","超過訂餐時間") {
                positiveButton("OK"){}
            }.show()
        }else{
            //val orderURL = "${ord1.url}&time=${fullFormat.format(now)}$selTime"
            val orderRequest = object: StringRequest(Method.POST, dsRequestURL, Response.Listener {
                if (isValidJson(it)){
                    //indicator
                    progressBarHandler.hide()
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
                    val orderInfo = JSONArray(it)
                    val orderID = orderInfo.getJSONObject(0).getString("id")
                    alert("訂單編號$orderID,請記得付款！", "點餐成功"){
                        positiveButton("OK"){
                            startActivity(Intent(this@GuanDonOrderActivity, StudentMainActivity::class.java))
                        }
                    }.show()
                }else{
                    if(it.contains("Off") || it.contains("Impossible")){
                        //indicator
                        progressBarHandler.hide()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("請確定手機時間是否正確","訂餐錯誤"){
                            positiveButton("OK"){}
                        }.build().apply {
                            setCancelable(false)
                            setCanceledOnTouchOutside(false)
                        }.show()
                    }else if (it.contains("Invalid")){
                        //indicator
                        progressBarHandler.hide()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("發生了不知名的錯誤。請嘗試重新登入，或嘗試重新開啟程式，若持續發生問題，請通知開發人員！", "Unexpected Error"){
                            positiveButton("OK"){}
                        }.build().apply {
                            setCancelable(false)
                            setCanceledOnTouchOutside(false)
                        }.show()
                    }else if (it == ""){
                        //indicator
                        progressBarHandler.hide()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("請重新登入", "您已經登出") {
                            positiveButton("OK") {
                                startActivity(Intent(this@GuanDonOrderActivity, LoginActivity::class.java))
                            }
                        }.build().apply {
                            setCancelable(false)
                            setCanceledOnTouchOutside(false)
                        }.show()
                    }else if (it == "daily limit exceed"){
                        //indicator
                        progressBarHandler.hide()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("您所想要訂購的餐點已被別人先訂走或已達今日總訂單上限，請重新點餐。") {
                            positiveButton("OK") {
                                startActivity(Intent(this@GuanDonOrderActivity, MainMenuActivity::class.java))
                            }
                        }.build().apply {
                            setCancelable(false)
                            setCanceledOnTouchOutside(false)
                        }.show()
                    }else{
                        //indicator
                        indicatorView.visibility = View.INVISIBLE
                        progressBar.visibility = View.INVISIBLE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("發生了不知名的錯誤。請嘗試重新登入，或嘗試重新開啟程式，若持續發生問題，請通知開發人員！", "Unexpected Error"){
                            positiveButton("OK"){}
                        }.show()
                    }
                }
            }, Response.ErrorListener {
                //indicator
                progressBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                    positiveButton("OK"){}
                }.show()
            }){
                override fun getParams(): MutableMap<String, String> {
                    var postParam: MutableMap<String, String> = HashMap()
                    postParam["cmd"] = "make_self_order"

                    var dishIDParamCount = 0
                    for (did in guanDonParam){
                        postParam["dish_id[${dishIDParamCount++}]"] = did
                    }

                    postParam["time"] = "${fullFormat.format(now)}$selTime"
                    return postParam
                }
            }
            VolleySingleton.getInstance(this).addToRequestQueue(orderRequest)
        }
    }


}
