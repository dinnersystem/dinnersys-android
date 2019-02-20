package seanpai.dinnersystem

import android.content.Intent
import android.support.v7.app.AppCompatActivity
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
    private lateinit var indicatorView: View
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guan_don_order)
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
        val confirmString = """
            您選擇的餐點是${selOrder1.name}，價錢為${selOrder1.cost}，確定請選擇時間後按訂餐。
            請注意早上十點後將無法點餐!
        """.trimIndent()
        this.confirmText.text = confirmString
    }


    fun sendGuanDonOrder(view:View){
        //indicator
        indicatorView.visibility = View.VISIBLE
        indicatorView.bringToFront()
        progressBar.visibility = View.VISIBLE
        progressBar.bringToFront()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
        val now = getCurrentDateTime()
        val hourFormat = SimpleDateFormat("HH", Locale("zh-TW"))
        val hour = hourFormat.format(now).toInt()
        val fullFormat = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        val selTime = (if (timeButton.isChecked) "-12:00:00" else "-11:00:00")
        if(hour>10) {
            //indicator
            indicatorView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert("早上十點後無法訂餐，明日請早","超過訂餐時間") {
                positiveButton("OK"){}
            }.show()
        }else{
            val orderURL = "${ord1.url}&time=${fullFormat.format(now)}$selTime"
            val orderRequest = StringRequest(orderURL, Response.Listener {
                if (isValidJson(it)){
                    //indicator
                    indicatorView.visibility = View.INVISIBLE
                    progressBar.visibility = View.INVISIBLE
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
                        indicatorView.visibility = View.INVISIBLE
                        progressBar.visibility = View.INVISIBLE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("請確定手機時間是否正確","訂餐錯誤"){
                            positiveButton("OK"){}
                        }.show()
                    }else if (it.contains("Invalid")){
                        //indicator
                        indicatorView.visibility = View.INVISIBLE
                        progressBar.visibility = View.INVISIBLE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("發生了不知名的錯誤。請嘗試重新登入，或嘗試重新開啟程式，若持續發生問題，請通知開發人員！", "Unexpected Error"){
                            positiveButton("OK"){}
                        }.show()
                    }else if (it == ""){
                        //indicator
                        indicatorView.visibility = View.INVISIBLE
                        progressBar.visibility = View.INVISIBLE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("請重新登入", "您已經登出") {
                            positiveButton("OK") {
                                startActivity(Intent(this@GuanDonOrderActivity, LoginActivity::class.java))
                            }
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
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                    positiveButton("OK"){}
                }.show()
            })
            VolleySingleton.getInstance(this).addToRequestQueue(orderRequest)
        }
    }


}
