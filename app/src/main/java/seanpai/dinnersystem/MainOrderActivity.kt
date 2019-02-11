package seanpai.dinnersystem

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main_order.*
import org.jetbrains.anko.alert
import org.json.JSONArray
import org.threeten.bp.LocalDateTime
import java.text.SimpleDateFormat
import java.util.*

class MainOrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_order)
        val confirmString = """
            您選擇的餐點是${selOrder1.name}，價錢為${selOrder1.cost}，確定請按訂餐。
            請注意早上十點後將無法點餐!
        """.trimIndent()
        this.confirmText.text = confirmString
    }

    fun sendOrder(view: View){
        val now = LocalDateTime.now()
        val hourFormat = SimpleDateFormat("HH", Locale("zh-TW"))
        val hour = hourFormat.format(now).toInt()
        val fullFormat = SimpleDateFormat("yyyy/MM/dd", Locale("zh-TW"))
        if(hour>10) {
            alert("早上十點後無法訂餐，明日請早","超過訂餐時間") { positiveButton("OK"){} }
        }else{
            val orderURL = dsURL("make_self_order&dish_id[]=${selOrder1.id}&time=${fullFormat.format(now)}-12:00:00")
            val orderRequest = StringRequest(orderURL, Response.Listener {
                if (isValidJson(it)){
                    val orderInfo = JSONArray(it)
                    val orderID = orderInfo.getJSONObject(0).getString("id")
                    alert("訂單編號$orderID,請記得付款！", "點餐成功"){
                        positiveButton("OK"){
                            startActivity(Intent(this@MainOrderActivity, StudentMainActivity::class.java))
                        }
                    }
                }else{
                    if(it.contains("Off") || it.contains("Impossible")){
                        alert("請確定手機時間是否正確","訂餐錯誤"){
                            positiveButton("OK"){}
                        }
                    }else if (it.contains("Invalid")){
                        alert("發生了不知名的錯誤。請嘗試重新登入，或嘗試重新開啟程式，若持續發生問題，請通知開發人員！", "Unexpected Error"){
                            positiveButton("OK"){}
                        }
                    }else if (it == ""){
                        alert("請重新登入", "您已經登出") {
                            positiveButton("OK") {
                                startActivity(Intent(this@MainOrderActivity, LoginActivity::class.java))
                            }
                        }
                    }else{
                        alert("發生了不知名的錯誤。請嘗試重新登入，或嘗試重新開啟程式，若持續發生問題，請通知開發人員！", "Unexpected Error"){
                            positiveButton("OK"){}
                        }
                    }
                }
            }, Response.ErrorListener {
                alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                    positiveButton("OK"){}
                }
            })
            VolleySingleton.getInstance(this).addToRequestQueue(orderRequest)
        }
    }

}
