package seanpai.dinnersystem

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.jetbrains.anko.alert
import org.json.JSONArray
import org.json.JSONObject


class StuOrderListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_order_list)

        val url = dsURL("show_dish")
        val balanceURL = dsURL("get_money")

        val balanceRequest = StringRequest(balanceURL, Response.Listener {
            balance = it.trim().toInt()
        }, Response.ErrorListener { alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
            positiveButton("OK"){}
        }.show() })

        val dishRequest = StringRequest(url, Response.Listener { response ->
            allMenuJson = JSONArray("[]")
            taiwanMenuJson = JSONArray("[]")
            aiJiaMenuJson = JSONArray("[]")
            cafetMenuJson = JSONArray("[]")
            guanDonMenuJson = JSONArray("[]")
            if (isValidJson(response)){
                allMenuJson = JSONArray(response)
                var j=0
                for(i in 0 until allMenuJson.length()) {
                    val item = allMenuJson.getJSONObject(j)
                    if (item.getString("is_idle") == "1") {
                        allMenuJson.remove(j)
                        j -= 1
                    } else when (item.getJSONObject("department").getJSONObject("factory").getString("name")) {
                        "台灣小吃部" -> taiwanMenuJson.put(item)
                        "愛家便當" -> aiJiaMenuJson.put(item)
                        "關東煮" -> guanDonMenuJson.put(item)
                        "合作社" -> cafetMenuJson.put(item)
                        else -> {}
                    }
                    j += 1
                }
                for(i in 0 until guanDonMenuJson.length()){
                    val remainURL = dsURL("get_remaining") + "&id=${guanDonMenuJson.getJSONObject(i).getString("dish_id")}"
                    val remainRequest = StringRequest(remainURL, Response.Listener { remainResponse ->
                        if(isValidJson(remainResponse)){
                            guanDonMenuJson.put(i,JSONObject(remainResponse))
                        }else{
                            alert("請重新登入","您已經登出"){
                                positiveButton("OK"){
                                    startActivity(Intent(this@StuOrderListActivity, LoginActivity::class.java))
                                }
                            }.show()
                        }
                    }, Response.ErrorListener {
                        alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                            positiveButton("OK"){}
                        }.show()
                    })
                    VolleySingleton.getInstance(this).addToRequestQueue(remainRequest)
                }
                println("everything should be alright")
            }else{
                alert("請重新登入","您已經登出"){
                    positiveButton("OK"){
                        startActivity(Intent(this@StuOrderListActivity, LoginActivity::class.java))
                    }
                }.show()
            }
        },Response.ErrorListener {
            alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                positiveButton("OK"){}
            }.show()
        })
        VolleySingleton.getInstance(this).addToRequestQueue(dishRequest)
        VolleySingleton.getInstance(this).addToRequestQueue(balanceRequest)
    }


    fun toTaiwan(view:View){
        selectedFactoryArr = taiwanMenuJson
        println("startingActivity......................")
        startActivity(Intent(view.context, MainMenuActivity::class.java))
    }
    fun toAiJia(view:View){
        selectedFactoryArr = aiJiaMenuJson
        startActivity(Intent(view.context, MainMenuActivity::class.java))
    }
    fun toCafet(view:View){
        selectedFactoryArr = cafetMenuJson
        startActivity(Intent(view.context, MainMenuActivity::class.java))
    }
    fun toGuanDon(view:View){
        selectedFactoryArr = guanDonMenuJson
        startActivity(Intent(view.context, GuandonOrderListActivity::class.java))
    }

}
