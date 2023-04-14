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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONArray
import org.json.JSONObject
import seanpai.dinnersystem.databinding.ActivityBeforeHistoryBinding
import seanpai.dinnersystem.databinding.BeforeBottomListViewBinding
import seanpai.dinnersystem.databinding.HistoryListCellBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class BeforeHistoryActivity : AppCompatActivity() {

    private lateinit var progressBarHandler: ProgressBarHandler
    private lateinit var activityBinding: ActivityBeforeHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityBeforeHistoryBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)
        //indicator start
        progressBarHandler = ProgressBarHandler(this)
        //indicator end
        val adapter = TableAdapter(this)
        activityBinding.tableView.adapter = adapter
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
                revDishNameArr = emptyArray()
                revHistoryArr = JSONArray("[]")
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
                            activityBinding.tableView.adapter = adapter
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
//                            alert("請重新登入","您已經登出"){
//                                positiveButton("OK"){
//                                    startActivity(Intent(this@BeforeHistoryActivity, LoginActivity::class.java))
//                                }
//                            }.show()

                            AlertDialog.Builder(this)
                                .setTitle("請重新登入")
                                .setMessage("您已經登出")
                                .setPositiveButton("OK") { _, _ ->
                                    startActivity(Intent(this@BeforeHistoryActivity, LoginActivity::class.java))
                                }
                                .show()
                        }
                    }else{
                        historyArr = JSONArray("[]")
                        val adapter = TableAdapter(this)
                        activityBinding.tableView.adapter = adapter
                        //indicator
                        progressBarHandler.hide()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        adapter.notifyDataSetChanged()
//                        alert("請嘗試重新整理或進行點餐！","無點餐資料"){
//                            positiveButton("OK"){
//                            }
//                        }.show()

                        AlertDialog.Builder(this)
                            .setTitle("請嘗試重新整理或進行點餐！")
                            .setMessage("無點餐資料")
                            .setPositiveButton("OK") { _, _ ->
                            }
                            .show()
                    }
                }else{
                    //indicator
                    progressBarHandler.hide()
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
                    //logout
//                    alert("請重新登入","您已經登出"){
//                        positiveButton("OK"){
//                            startActivity(Intent(this@BeforeHistoryActivity, LoginActivity::class.java))
//                        }
//                    }
//                    .show()

                    AlertDialog.Builder(this)
                        .setTitle("請重新登入")
                        .setMessage("您已經登出")
                        .setPositiveButton("OK") { _, _ ->
                            startActivity(Intent(this@BeforeHistoryActivity, LoginActivity::class.java))
                        }
                        .show()
                }
            },
            Response.ErrorListener {
                //indicator
                progressBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
//                alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
//                    positiveButton("OK"){}
//                }.show()

                AlertDialog.Builder(this)
                    .setTitle("請注意網路狀態，或通知開發人員!")
                    .setMessage("不知名的錯誤")
                    .setPositiveButton("OK") { _, _ ->
                    }
                    .show()
            }
        ){
            override fun getParams(): MutableMap<String, String> {
                val postParam: MutableMap<String, String> = HashMap()
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
                activityBinding.balanceText.text = "餘額：$balance$"
            } else {
                //indicator
                progressBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
//                alert("查詢餘額失敗，我們已經派出最精銳的猴子去修理這個問題，若長時間出現此問題請通知開發人員！", "請重新登入") {
//                    positiveButton("OK") {
//                        startActivity(Intent(this@BeforeHistoryActivity, LoginActivity::class.java))
//                    }
//                }.show()

                AlertDialog.Builder(this)
                    .setTitle("查詢餘額失敗，我們已經派出最精銳的猴子去修理這個問題，若長時間出現此問題請通知開發人員！")
                    .setMessage("請重新登入")
                    .setPositiveButton("OK") { _, _ ->
                        startActivity(Intent(this@BeforeHistoryActivity, LoginActivity::class.java))
                    }
                    .show()
            }

        }, Response.ErrorListener {
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
//            alert("請注意網路狀態，或通知開發人員!", "不知名的錯誤") {
//                positiveButton("OK") {}
//            }.show()

            AlertDialog.Builder(this)
                .setTitle("請注意網路狀態，或通知開發人員!")
                .setMessage("不知名的錯誤")
                .setPositiveButton("OK") { _, _ ->
                }
                .show()
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
            val binding = HistoryListCellBinding.bind(layout)
            if(revHistoryArr.length() != 0){
                if(revHistoryArr.getJSONObject(position).getJSONArray("dish").length() > 1){
                    binding.title.text = "自訂套餐(${info.getJSONArray("dish").length()}樣)"
                }else{
                    binding.title.text = revDishNameArr[position]
                }
                val isPaid = info.getJSONObject("money").getJSONArray("payment").getJSONObject(0).getString("paid")
                val paid = isPaid == "true"
                var paidStr = ""
                val date = info.getString("recv_date").split(" ")[0]
                if(paid){
                    paidStr = "已付款"
                    binding.detailTitle.text = "$date, ${info.getJSONObject("money").getString("charge")}$, 已付款"
                }else{
                    paidStr = "未付款"
                    binding.detailTitle.text = "$date, ${info.getJSONObject("money").getString("charge")}$, 未付款"
                }

                binding.infoButton.setOnClickListener {
                    val dialog = BottomSheetDialog(mContext)
                    val bottomSheet = layoutInflater.inflate(R.layout.before_bottom_list_view, null)
                    val sheetBinding = BeforeBottomListViewBinding.bind(bottomSheet)

                    sheetBinding.textMessage.text = "訂餐編號:${info.getString("id")}\n餐點內容:${revDishNameArr[position]}\n訂餐日期:${info.getString("recv_date").dropLast(3)}\n餐點金額:${info.getJSONObject("money").getString("charge")}\n付款狀態:$paidStr"
                    sheetBinding.cancelButton.text = "返回"
                    sheetBinding.cancelButton.setOnClickListener { dialog.dismiss() }
                    dialog.setContentView(bottomSheet)
                    dialog.show()

                }
            }
            return layout
        }
    }
}
