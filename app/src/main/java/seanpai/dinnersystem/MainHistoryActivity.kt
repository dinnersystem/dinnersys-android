package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONArray
import org.json.JSONObject
import seanpai.dinnersystem.databinding.ActivityMainHistoryBinding
import seanpai.dinnersystem.databinding.HistoryBottomListViewBinding
import seanpai.dinnersystem.databinding.HistoryListCellBinding
import seanpai.dinnersystem.databinding.PaymPwAlertBinding
import java.text.SimpleDateFormat
import java.util.*

class MainHistoryActivity : AppCompatActivity() {
    private lateinit var progressBarHandler: ProgressBarHandler
    private lateinit var activityBinding: ActivityMainHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainHistoryBinding.inflate(layoutInflater)
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

        dishNameArr = emptyArray()

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
//                        alert("請重新登入","您已經登出"){
//                            positiveButton("OK"){
//                                startActivity(Intent(this@MainHistoryActivity, LoginActivity::class.java))
//                            }
//                        }.show()
                        AlertDialog.Builder(this)
                            .setTitle("請重新登入")
                            .setMessage("您已經登出")
                            .setPositiveButton("OK") { _, _ ->
                                startActivity(Intent(this@MainHistoryActivity, LoginActivity::class.java))
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
//                            startActivity(Intent(this@MainHistoryActivity, LoginActivity::class.java))
//                        }
//                    }
                    AlertDialog.Builder(this)
                        .setTitle("請重新登入")
                        .setMessage("您已經登出")
                        .setPositiveButton("OK") { _, _ ->
                            startActivity(Intent(this@MainHistoryActivity, LoginActivity::class.java))
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
                postParam["esti_start"] = "${formatter.format(now)}-00:00:00"
                postParam["esti_end"] = "${formatter.format(now)}-23:59:59"
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
//                        startActivity(Intent(this@MainHistoryActivity, LoginActivity::class.java))
//                    }
//                }.show()
                AlertDialog.Builder(this)
                    .setTitle("查詢餘額失敗，我們已經派出最精銳的猴子去修理這個問題，若長時間出現此問題請通知開發人員！")
                    .setMessage("請重新登入")
                    .setPositiveButton("OK") { _, _ ->
                        startActivity(Intent(this@MainHistoryActivity, LoginActivity::class.java))
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
                var postParam: MutableMap<String, String> = HashMap()
                postParam["cmd"] = "get_pos"
                return postParam
            }
        }
        VolleySingleton.getInstance(this).addToRequestQueue(balanceRequest)


    }

    class TableAdapter(context: Context): BaseAdapter() {
        val mContext: Context = context
        val activity = context as MainHistoryActivity
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
            val info = historyArr.getJSONObject(position)
            val layoutInflater = LayoutInflater.from(mContext)
            val layout = layoutInflater.inflate(R.layout.history_list_cell, parent, false)
            val cellBinding = HistoryListCellBinding.bind(layout)
            if(historyArr.length() != 0){
                if(historyArr.getJSONObject(position).getJSONArray("dish").length() > 1){
                    cellBinding.title.text = "自訂套餐(${info.getJSONArray("dish").length()}樣)"
                }else{
                    cellBinding.title.text = dishNameArr[position]
                }
                val isPaid = info.getJSONObject("money").getJSONArray("payment").getJSONObject(0).getString("paid")
                val paid = isPaid == "true"
                var paidStr = ""
                if(paid){
                    paidStr = "已付款"
                    cellBinding.detailTitle.text = "${info.getJSONObject("money").getString("charge")}$, 已付款"
                }else{
                    paidStr = "未付款"
                    cellBinding.detailTitle.text = "${info.getJSONObject("money").getString("charge")}$, 未付款"
                }

                cellBinding.infoButton.setOnClickListener {
                    val dialog = BottomSheetDialog(mContext)
                    val bottomSheet = layoutInflater.inflate(R.layout.history_bottom_list_view, null)
                    val sheetBinding = HistoryBottomListViewBinding.bind(bottomSheet)
                    val now = getCurrentDateTime()
                    val hourFormat = SimpleDateFormat("HHmm", Locale.TAIWAN)
                    val hour = hourFormat.format(now).toInt()
                    val timeBool = info.getString("recv_date").contains("11:00")
                    val timeString = if (timeBool) "09:10" else "10:10"
                    sheetBinding.textMessage.text =
                        "訂餐編號:${info.getString("id")}\n餐點內容:${dishNameArr[position]}\n訂餐日期:${info.getString("recv_date").dropLast(
                            3
                        )}\n餐點金額:${info.getJSONObject("money").getString("charge")}\n付款狀態:$paidStr\n請於${timeString}前付款！"
                    sheetBinding.paymentButton.setOnClickListener{
                        val alert = AlertDialog.Builder(mContext)
                        val inputView = layoutInflater.inflate(R.layout.paym_pw_alert, null)
                        val inputBinding = PaymPwAlertBinding.bind(inputView)
                        val paymentPwText = inputBinding.paymentPW
                        alert.setView(inputView)
                        alert.setTitle("請輸入驗證碼")
                        alert.setMessage("預設為身分證後四碼")
                        alert.setPositiveButton(R.string.send){ dialog, which ->
                            activity.startInd()
                            val paymentPw = paymentPwText.text
                            if (paymentPw.isBlank()){
                                activity.stopInd()
                                val errorAlertBuilder = AlertDialog.Builder(mContext)
                                errorAlertBuilder.setTitle("錯誤")
                                errorAlertBuilder.setMessage("繳款密碼不可為空")
                                errorAlertBuilder.setPositiveButton("OK") { _, _ -> }
                                val errorAlert = errorAlertBuilder.create()
                                errorAlert.show()
                            }else{
                                val timeStamp = (System.currentTimeMillis() / 1000).toString()
                                val paymentString = paymentPw.toString()
                                //val usr = constUsername
                                //val pwd = constPassword
                                //val noHash = "{\"id\":\"${info.getString("id")}\",\"usr_id\":\"$usr\",\"usr_password\":\"$pwd\",\"pmt_password\":\"$paymentString\",\"time\":\"$timeStamp\"}"
                                //val hash = noHash.sha512()
//                                val paymentURL =
//                                    dsURL("payment_self&target=true&order_id=${info.getString("id")}&password=$paymentString&time=$timeStamp")
                                val paymentRequest = object : StringRequest(Method.POST, dsRequestURL, Response.Listener {
                                    if(isValidJson(it)){
                                        activity.stopInd()
                                        val successAlertBuilder = AlertDialog.Builder(mContext)
                                        successAlertBuilder.setTitle("繳款完成").setMessage("請注意付款狀況，實際情況仍以頁面為主")
                                        successAlertBuilder.setPositiveButton("OK"){_, _ ->
                                            activity.reloadData(null)
                                        }
                                        val successAlert = successAlertBuilder.create()
                                        successAlert.show()
                                    }else{
                                        activity.stopInd()
                                        if(it == ""){
                                            val errorAlertBuilder = AlertDialog.Builder(mContext)
                                            errorAlertBuilder.setTitle("你已經登出")
                                            errorAlertBuilder.setMessage("請注意登入狀態，或通知開發人員!")
                                            errorAlertBuilder.setPositiveButton("OK") { _, _ -> }
                                            val errorAlert = errorAlertBuilder.create()
                                            errorAlert.show()
                                        }else if( it.contains("denied")){
                                            val errorAlertBuilder = AlertDialog.Builder(mContext)
                                            errorAlertBuilder.setTitle("權限錯誤")
                                            errorAlertBuilder.setMessage("請注意帳號狀態，或通知開發人員!")
                                            errorAlertBuilder.setPositiveButton("OK") { _, _ -> }
                                            val errorAlert = errorAlertBuilder.create()
                                            errorAlert.show()
                                        }else if(it.contains("wrong")){
                                            val errorAlertBuilder = AlertDialog.Builder(mContext)
                                            errorAlertBuilder.setTitle("密碼錯誤")
                                            errorAlertBuilder.setMessage("請注意密碼是否正確，或通知開發人員!")
                                            errorAlertBuilder.setPositiveButton("OK") { _, _ -> }
                                            val errorAlert = errorAlertBuilder.create()
                                            errorAlert.show()
                                        }else if(it.contains("punish")){
                                            val errorAlertBuilder = AlertDialog.Builder(mContext)
                                            errorAlertBuilder.setTitle("嘗試次數過多")
                                            errorAlertBuilder.setMessage("請稍後(約10秒)再試")
                                            errorAlertBuilder.setPositiveButton("OK") { _, _ -> }
                                            val errorAlert = errorAlertBuilder.create()
                                            errorAlert.show()
                                        }else if(it.contains("Unable")){
                                            val errorAlertBuilder = AlertDialog.Builder(mContext)
                                            errorAlertBuilder.setTitle("付款系統錯誤")
                                            errorAlertBuilder.setMessage("未成功付款，請通知開發者")
                                            errorAlertBuilder.setPositiveButton("OK") { _, _ -> }
                                            val errorAlert = errorAlertBuilder.create()
                                            errorAlert.show()
                                        }
                                    }
                                    activity.reloadData(null)
                                }, Response.ErrorListener {
                                    activity.stopInd()
                                    val errorAlertBuilder = AlertDialog.Builder(mContext)
                                    errorAlertBuilder.setTitle("不知名的錯誤")
                                    errorAlertBuilder.setMessage("請注意網路狀態，或通知開發人員!")
                                    errorAlertBuilder.setPositiveButton("OK") { _, _ -> }
                                    val errorAlert = errorAlertBuilder.create()
                                    errorAlert.show()
                                }){
                                    override fun getParams(): MutableMap<String, String> {
                                        var postParam: MutableMap<String, String> = HashMap()
                                        postParam["cmd"] = "payment_self"
                                        postParam["target"] = "true"
                                        postParam["order_id"] = info.getString("id")
                                        postParam["password"] = paymentString
                                        postParam["time"] = timeStamp
                                        return postParam
                                    }
                                }
                                paymentRequest.retryPolicy = DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT )
                                dialog.dismiss()
                                VolleySingleton.getInstance(mContext).addToRequestQueue(paymentRequest)
                            }
                        }
                        alert.setNegativeButton(R.string.cancel){ dialog, _ ->
                            dialog.dismiss()
                        }
                        val paymentAlert = alert.create()
                        paymentAlert.show()
                    }
                    sheetBinding.deleteButton.setOnClickListener {
                        activity.startInd()
//                        val deleteURL = dsURL("delete_self&order_id=${info.getString("id")}")
                        val deleteRequest = object :StringRequest(Method.POST, dsRequestURL, Response.Listener {
                            activity.stopInd()
                            if(it == ""){
                                val errorAlertBuilder = AlertDialog.Builder(mContext)
                                errorAlertBuilder.setTitle("你已經登出")
                                errorAlertBuilder.setMessage("請注意登入狀態，或通知開發人員!")
                                errorAlertBuilder.setPositiveButton("OK") { _, _ -> }
                                val errorAlert = errorAlertBuilder.create()
                                errorAlert.show()
                            }else if( it.contains("denied") ){
                                val errorAlertBuilder = AlertDialog.Builder(mContext)
                                errorAlertBuilder.setTitle("權限錯誤")
                                errorAlertBuilder.setMessage("請注意帳號狀態，或通知開發人員!")
                                errorAlertBuilder.setPositiveButton("OK") { _, _ -> }
                                val errorAlert = errorAlertBuilder.create()
                                errorAlert.show()
                            }else if( it.contains("Invalid") ){
                                val errorAlertBuilder = AlertDialog.Builder(mContext)
                                errorAlertBuilder.setTitle("發生錯誤")
                                errorAlertBuilder.setMessage("請稍後再試")
                                errorAlertBuilder.setPositiveButton("OK") { _, _ -> }
                                val errorAlert = errorAlertBuilder.create()
                                errorAlert.show()
                            }
                            activity.reloadData(null)
                        }, Response.ErrorListener {
                            activity.stopInd()
                            val errorAlertBuilder = AlertDialog.Builder(mContext)
                            errorAlertBuilder.setTitle("不知名的錯誤")
                            errorAlertBuilder.setMessage("請注意網路狀態，或通知開發人員!")
                            errorAlertBuilder.setPositiveButton("OK") { _, _ ->
                                startActivity(mContext,Intent(activity,LoginActivity::class.java),null)
                            }
                            val errorAlert = errorAlertBuilder.create()
                            errorAlert.show()
                        }){
                            override fun getParams(): MutableMap<String, String> {
                                var postParam: MutableMap<String, String> = HashMap()
                                postParam["cmd"] = "delete_self"
                                postParam["order_id"] = info.getString("id")
                                return postParam
                            }
                        }
                        dialog.dismiss()
                        VolleySingleton.getInstance(mContext).addToRequestQueue(deleteRequest)
                    }
                    sheetBinding.cancelButton.setOnClickListener { dialog.dismiss() }
                    if (paid){
                        sheetBinding.paymentButton.isEnabled = false
                        sheetBinding.paymentButton.text = "已成功付款"
                        sheetBinding.deleteButton.isEnabled = false
                        sheetBinding.deleteButton.text = "已付款者請聯絡合作社取消"
                    }else{
                        if((timeBool && hour>910) || (!timeBool && hour>1010)){
                            val timeString = if (timeBool) "09:10" else "10:10"
                            sheetBinding.paymentButton.isEnabled = false
                            sheetBinding.paymentButton.text = "已超過繳款時間($timeString)"
                        }else if(balance >= info.getJSONObject("money").getString("charge").toInt()) {
                            sheetBinding.paymentButton.text = "以學生證付款(餘額:$balance)"
                        }else{
                            sheetBinding.paymentButton.isEnabled = false
                            sheetBinding.paymentButton.text = "餘額不足（您只剩${balance}元）"
                        }
                        sheetBinding.deleteButton.text = "取消訂單"
                    }

                    sheetBinding.cancelButton.text = "返回"
                    dialog.setContentView(bottomSheet)
                    dialog.show()

                }
            }
            return layout
        }
    }
}
