package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_stu_order_list.*
import org.jetbrains.anko.alert
import org.json.JSONArray


class StuOrderListActivity : AppCompatActivity() {
    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_order_list)
        queue = Volley.newRequestQueue(this)
        val url = dsURL("show_dish")
        val dishRequest = StringRequest(url, Response.Listener { response ->
            if (isValidJson(response)){
                allMenuJson = JSONArray(response)
                var j=0
                for(i in 0 until allMenuJson.length()){
                    val item = allMenuJson.getJSONObject(j)
                    if (item.getString("is_idle") == "1"){
                        allMenuJson.remove(j)
                        j-=1
                    }else when (item.getJSONObject("factory").getString("name")){
                        "台灣小吃部" -> taiwanMenuJson.put(item)
                        "愛家便當" -> aiJiaMenuJson.put(item)
                        "關東煮" -> guanDonMenuJson.put(item)
                        "合作社" -> cafetMenuJson.put(item)
                        else -> println("no")
                    }
                    j+=1
                }
            }else{
                alert("請重新登入","您已經登出"){
                    positiveButton("OK"){
                        startActivity(Intent(this@StuOrderListActivity, LoginActivity::class.java))
                    }
                }
            }
        },Response.ErrorListener { error ->
            alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                positiveButton("OK"){}
            }
        })
        queue!!.add(dishRequest)
    }
    override fun onStop() {
        super.onStop()
        queue!!.stop()
    }
    //TODO: next activity
    fun toTaiwan(view:View){}
    fun toAiJia(view:View){}
    fun toCafet(view:View){}
    fun toGuanDon(view:View){}

}
