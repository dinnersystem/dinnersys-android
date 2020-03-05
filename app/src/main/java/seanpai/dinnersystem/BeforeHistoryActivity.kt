package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.ListAdapter
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.activity_before_history.*
import kotlinx.android.synthetic.main.history_bottom_list_view.view.*
import kotlinx.android.synthetic.main.history_list_cell.view.*
import org.jetbrains.anko.alert
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class BeforeHistoryActivity : AppCompatActivity() {

    private lateinit var progressBarHandler: ProgressBarHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_before_history)
        //indicator start
        progressBarHandler = ProgressBarHandler(this)
        //indicator end
        val adapter = TableAdapter(this)
        tableView.adapter = adapter
        reloadData(null)

    }



    fun startInd() {
        //indicator
        progressBarHandler.show()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
    }

    fun stopInd() {
        //indicator
        progressBarHandler.hide()
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
    }

    fun reloadData(view: View?) {
        //indicator
        progressBarHandler.show()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator

        val now = getCurrentDateTime()

        //val now = java.util.Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        println(formatter.format(now))
//        val selSelf = dsURL("select_self&history=true&esti_start=${formatter.format(now)}-00:00:00&esti_end=${formatter.format(now)}-23:59:59")
        val historyRequest = object: StringRequest(Method.POST, dsRequestURL,
            Response.Listener {
                if(it != ""){
                    if(it != "[]"){
                        if (isValidJson(it)){
                            println(it)
                            historyArr = JSONArray(it)
                            for (i in 0 until historyArr.length()) {
                                val info = historyArr.getJSONObject(i)
                                if (info.getJSONArray("dish").length() > 1) {
                                    var dName = ""
                                    for (j in 0 until info.getJSONArray("dish").length()) {
                                        val food = info.getJSONArray("dish").getJSONObject(j)
                                        dName += "${food.getString("dish_name")}+"
                                    }
                                    dName = dName.dropLast(1)
                                    dishNameArr += dName
                                } else {
                                    dishNameArr += info.getJSONArray("dish").getJSONObject(0).getString("dish_name")
                                }
                            }
                            for(i in historyArr.length()-1 downTo 0){
                                revHistoryArr.put(historyArr.getJSONObject(i))
                                revDishNameArr += dishNameArr[i]
                            }
                            val adapter = TableAdapter(this)
                            tableView.adapter = adapter
                            //indicator
                            progressBarHandler.hide()
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            //indicator
                            adapter.notifyDataSetChanged()
                        }else{
                            //indicator
                            progressBarHandler.hide()
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            //indicator
                            alert("請重新登入","您已經登出"){
                                positiveButton("OK"){
                                    startActivity(Intent(this@BeforeHistoryActivity, LoginActivity::class.java))
                                }
                            }.show()
                        }
                    }else{
                        historyArr = JSONArray("[]")
                        val adapter = TableAdapter(this)
                        tableView.adapter = adapter
                        //indicator
                        progressBarHandler.hide()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        adapter.notifyDataSetChanged()
                        alert("請嘗試重新整理或進行點餐！","無點餐資料"){
                            positiveButton("OK"){
                            }
                        }.show()
                    }
                }else{
                    //indicator
                    progressBarHandler.hide()
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
                    //logout
                    alert("請重新登入","您已經登出"){
                        positiveButton("OK"){
                            startActivity(Intent(this@BeforeHistoryActivity, LoginActivity::class.java))
                        }
                    }
                }
            },
            Response.ErrorListener {
                //indicator
                progressBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                    positiveButton("OK"){}
                }.show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                var postParam: MutableMap<String, String> = HashMap()
                postParam["cmd"] = "select_self"
                postParam["history"] = "true"
                return postParam
            }
        }
        //get_money_value
//        val balanceURL = dsURL("get_pos")
        val balanceRequest = object: StringRequest(Method.POST, dsRequestURL, Response.Listener {
            if (isValidJson(it)) {
                balance = JSONObject(it).getString("money").toInt()
                VolleySingleton.getInstance(this).addToRequestQueue(historyRequest)
                balanceText.text = "餘額：$balance$"
            } else {
                //indicator
                progressBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert("查詢餘額失敗，我們已經派出最精銳的猴子去修理這個問題，若長時間出現此問題請通知開發人員！", "請重新登入") {
                    positiveButton("OK") {
                        startActivity(Intent(this@BeforeHistoryActivity, LoginActivity::class.java))
                    }
                }.show()
            }

        }, Response.ErrorListener {
            //indicator
            progressBarHandler.hide()
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
        VolleySingleton.getInstance(this).addToRequestQueue(balanceRequest)


    }

    class TableAdapter(context: Context): BaseAdapter(), ListAdapter {
        private val mContext: Context = context
        override fun getItem(position: Int): Any {
            return 1101
        }

        override fun getItemId(position: Int): Long {
            return 13
        }

        override fun getCount(): Int {
            return historyArr.length()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val info = revHistoryArr.getJSONObject(position)
            val layoutInflater = LayoutInflater.from(mContext)
            val layout = layoutInflater.inflate(R.layout.history_list_cell, parent, false)
            if(revHistoryArr.length() != 0){
                if(revHistoryArr.getJSONObject(position).getJSONArray("dish").length() > 1){
                    layout.title.text = "自訂套餐(${info.getJSONArray("dish").length()}樣)"
                }else{
                    layout.title.text = revDishNameArr[position]
                }
                val isPaid = info.getJSONObject("money").getJSONArray("payment").getJSONObject(0).getString("paid")
                val paid = isPaid == "true"
                var paidStr = ""
                val date = info.getString("recv_date").split(" ")[0]
                if(paid){
                    paidStr = "已付款"
                    layout.detailTitle.text = "$date, ${info.getJSONObject("money").getString("charge")}$, 已付款"
                }else{
                    paidStr = "未付款"
                    layout.detailTitle.text = "$date, ${info.getJSONObject("money").getString("charge")}$, 未付款"
                }

                layout.infoButton.setOnClickListener {
                    val dialog = BottomSheetDialog(mContext)
                    val bottomSheet = layoutInflater.inflate(R.layout.before_bottom_list_view, null)

                    bottomSheet.textMessage.text = "訂餐編號:${info.getString("id")}\n餐點內容:${revDishNameArr[position]}\n訂餐日期:${info.getString("recv_date").dropLast(3)}\n餐點金額:${info.getJSONObject("money").getString("charge")}\n付款狀態:$paidStr"
                    bottomSheet.cancelButton.text = "返回"
                    bottomSheet.cancelButton.setOnClickListener { dialog.dismiss() }
                    dialog.setContentView(bottomSheet)
                    dialog.show()

                }
            }
            return layout
        }
    }
}
