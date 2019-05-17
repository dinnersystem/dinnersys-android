package seanpai.dinnersystem

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_stu_order_list.*
import org.jetbrains.anko.alert
import org.json.JSONArray
import org.json.JSONObject


class StuOrderListActivity : AppCompatActivity() {

    private lateinit var indicatorView : View
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_order_list)

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
        layout.addView(indicatorView)
        layout.addView(progressBar)
        //indicator end

        val url = dsURL("show_dish")
        val balanceURL = dsURL("get_pos")
        //indicator
        indicatorView.visibility = View.VISIBLE
        indicatorView.bringToFront()
        progressBar.visibility = View.VISIBLE
        progressBar.bringToFront()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator

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
                    }
                    else when (item.getJSONObject("department").getJSONObject("factory").getString("name")) {
                        "台灣小吃部" -> taiwanMenuJson.put(item)
                        "愛佳便當" -> aiJiaMenuJson.put(item)
                        "關東煮" -> guanDonMenuJson.put(item)
                        "合作社" -> cafetMenuJson.put(item)
                        else -> {}
                    }
                    if(item.getString("remaining") == "2147483647"){
                        item.put("remaining", "1000")
                    }
                    j += 1
                }
//                for(i in 0 until guanDonMenuJson.length()){
//                    val remainURL = dsURL("get_remaining") + "&id=${guanDonMenuJson.getJSONObject(i).getString("dish_id")}"
//                    val remainRequest = StringRequest(remainURL, Response.Listener { remainResponse ->
//                        if(isValidJson(remainResponse)){
//                            guanDonMenuJson.put(i, JSONObject(remainResponse))
//                        }else{
//                            //indicator
//                            indicatorView.visibility = View.INVISIBLE
//                            progressBar.visibility = View.INVISIBLE
//                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                            //indicator
//                            alert("請重新登入","您已經登出"){
//                                positiveButton("OK"){
//                                    startActivity(Intent(this@StuOrderListActivity, LoginActivity::class.java))
//                                }
//                            }.show()
//                        }
//                    }, Response.ErrorListener {
//                        //indicator
//                        indicatorView.visibility = View.INVISIBLE
//                        progressBar.visibility = View.INVISIBLE
//                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
//                        //indicator
//                        alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
//                            positiveButton("OK"){}
//                        }.show()
//                    })
//                    //VolleySingleton.getInstance(this).addToRequestQueue(remainRequest)
//                }
                println("everything should be alright")
            }else{
                //indicator
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert("請重新登入","您已經登出"){
                    positiveButton("OK"){
                        startActivity(Intent(this@StuOrderListActivity, LoginActivity::class.java))
                    }
                }.show()
            }
            //indicator
            indicatorView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
        },Response.ErrorListener {
            //indicator
            indicatorView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                positiveButton("OK"){}
            }.show()
        })

        val balanceRequest = StringRequest(balanceURL, Response.Listener {
            if (isValidJson(it)) {
                balance = JSONObject(it).getString("money").toInt()
                VolleySingleton.getInstance(this).addToRequestQueue(dishRequest)
            } else {
                //indicator
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert("查詢餘額失敗，我們已經派出最精銳的猴子去修理這個問題，若長時間出現此問題請通知開發人員！", "請重新登入") {
                    positiveButton("OK") {
                        startActivity(Intent(this@StuOrderListActivity, LoginActivity::class.java))
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
    fun toAd(view: View){
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://forms.gle/ZzhtizScCsuMk5e87")
            )
        )
    }

}
