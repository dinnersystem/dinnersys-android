package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.appcompat.app.AlertDialog
import android.view.*
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_main_history.*
import kotlinx.android.synthetic.main.guandon_list_cell.view.*
import kotlinx.android.synthetic.main.history_bottom_list_view.view.*
import kotlinx.android.synthetic.main.history_list_cell.view.*
import kotlinx.android.synthetic.main.paym_pw_alert.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.centerInParent
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class MainHistoryActivity : AppCompatActivity() {
    private lateinit var indicatorView : View
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_history)
        //indicator start
        indicatorView = View(this)
        indicatorView.setBackgroundResource(R.color.colorPrimaryDark)
        val viewParam = RelativeLayout.LayoutParams(-1, -1)
        viewParam.centerInParent()
        indicatorView.layoutParams = viewParam
        progressBar = ProgressBar(this,null, android.R.attr.progressBarStyle)
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
        val adapter = TableAdapter(this)
        tableView.adapter = adapter
        reloadData(null)

    }



    fun startInd() {
        //indicator
        indicatorView.visibility = View.VISIBLE
        indicatorView.bringToFront()
        progressBar.visibility = View.VISIBLE
        progressBar.bringToFront()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
    }

    fun stopInd() {
        //indicator
        indicatorView.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
    }

    fun reloadData(view: View?) {
        //indicator
        indicatorView.visibility = View.VISIBLE
        indicatorView.bringToFront()
        progressBar.visibility = View.VISIBLE
        progressBar.bringToFront()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator

        val now = getCurrentDateTime()

        //val now = java.util.Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        println(formatter.format(now))
        val selSelf = dsURL("select_self&history=true&esti_start=${formatter.format(now)}-00:00:00&esti_end=${formatter.format(now)}-23:59:59")
        val historyRequest = StringRequest(selSelf,
            Response.Listener {
                if(it != ""){
                    if(it != "[]"){
                    if (isValidJson(it)){
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
                        tableView.adapter = adapter
                        //indicator
                        indicatorView.visibility = View.INVISIBLE
                        progressBar.visibility = View.INVISIBLE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        adapter.notifyDataSetChanged()
                    }else{
                        //indicator
                        indicatorView.visibility = View.INVISIBLE
                        progressBar.visibility = View.INVISIBLE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("請重新登入","您已經登出"){
                            positiveButton("OK"){
                                startActivity(Intent(this@MainHistoryActivity, LoginActivity::class.java))
                            }
                        }.show()
                    }
                    }else{
                        //indicator
                        indicatorView.visibility = View.INVISIBLE
                        progressBar.visibility = View.INVISIBLE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("請嘗試重新整理或進行點餐！","無點餐資料"){
                            positiveButton("OK"){

                            }
                        }.show()
                    }
                }else{
                    //indicator
                    indicatorView.visibility = View.INVISIBLE
                    progressBar.visibility = View.INVISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
                    //logout
                    alert("請重新登入","您已經登出"){
                        positiveButton("OK"){
                            startActivity(Intent(this@MainHistoryActivity, LoginActivity::class.java))
                        }
                    }
                }
            },
            Response.ErrorListener {
                //indicator
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                    positiveButton("OK"){}
                }.show()
            }
        )
        //get_money_value
        val balanceURL = dsURL("get_pos")
        val balanceRequest = StringRequest(balanceURL, Response.Listener {
            if (isValidJson(it)) {
                balance = JSONObject(it).getString("money").toInt()
                VolleySingleton.getInstance(this).addToRequestQueue(historyRequest)
                balanceText.text = "餘額：$balance$"
            } else {
                //indicator
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert("查詢餘額失敗，我們已經派出最精銳的猴子去修理這個問題，若長時間出現此問題請通知開發人員！", "請重新登入") {
                    positiveButton("OK") {
                        startActivity(Intent(this@MainHistoryActivity, LoginActivity::class.java))
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

    class TableAdapter(context: Context): BaseAdapter() {
        val mContext: Context = context
        val activity = context as MainHistoryActivity
        private lateinit var indicatorView: View
        private lateinit var progressBar: ProgressBar
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
            if(historyArr.length() != 0){
                if(historyArr.getJSONObject(position).getJSONArray("dish").length() > 1){
                    layout.title.text = "自訂套餐(${info.getJSONArray("dish").length()}樣)"
                }else{
                    layout.title.text = dishNameArr[position]
                }
                val isPaid = info.getJSONObject("money").getJSONArray("payment").getJSONObject(0).getString("paid")
                val paid = isPaid == "true"
                var paidStr = ""
                if(paid){
                    paidStr = "已付款"
                    layout.detailTitle.text = "${info.getJSONObject("money").getString("charge")}$, 已付款"
                }else{
                    paidStr = "未付款"
                    layout.detailTitle.text = "${info.getJSONObject("money").getString("charge")}$, 未付款"
                }

                layout.infoButton.setOnClickListener {
                    val dialog = BottomSheetDialog(mContext)
                    val bottomSheet = layoutInflater.inflate(R.layout.history_bottom_list_view, null)
                    //indicator start
                    indicatorView = View(bottomSheet.context)
                    indicatorView.setBackgroundResource(R.color.colorPrimaryDark)
                    val viewParam = RelativeLayout.LayoutParams(-1, -1)
                    viewParam.centerInParent()
                    indicatorView.layoutParams = viewParam
                    progressBar = ProgressBar(bottomSheet.context, null, android.R.attr.progressBarStyle)
                    progressBar.isIndeterminate = true
                    val prams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    )
                    prams.centerInParent()
                    progressBar.layoutParams = prams
                    indicatorView.visibility = View.INVISIBLE
                    progressBar.visibility = View.INVISIBLE
                    bottomSheet.bottomSheet.addView(indicatorView)
                    bottomSheet.bottomSheet.addView(progressBar)
                    //indicator end
                    val now = getCurrentDateTime()
                    val hourFormat = SimpleDateFormat("HHmm", Locale.TAIWAN)
                    val hour = hourFormat.format(now).toInt()
                    val timeBool = info.getString("recv_date").contains("11:00")
                    val timeString = if (timeBool) "09:30" else "10:30"
                    bottomSheet.textMessage.text =
                        "訂餐編號:${info.getString("id")}\n餐點內容:${dishNameArr[position]}\n訂餐日期:${info.getString("recv_date").dropLast(
                            3
                        )}\n餐點金額:${info.getJSONObject("money").getString("charge")}\n付款狀態:$paidStr\n請於${timeString}前付款！"
                    bottomSheet.paymentButton.setOnClickListener{
                        val alert = AlertDialog.Builder(mContext)
                        val inputView = layoutInflater.inflate(R.layout.paym_pw_alert, null)
                        val paymentPwText = inputView.paymentPW
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
                                val paymentURL =
                                    dsURL("payment_self&target=true&order_id=${info.getString("id")}&password=$paymentString&time=$timeStamp")
                                val paymentRequest = StringRequest(paymentURL, Response.Listener {
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
                                })
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
                    bottomSheet.deleteButton.setOnClickListener {
                        activity.startInd()
                        val deleteURL = dsURL("delete_self&order_id=${info.getString("id")}")
                        val deleteRequest = StringRequest(deleteURL, Response.Listener {
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
                        })
                        dialog.dismiss()
                        VolleySingleton.getInstance(mContext).addToRequestQueue(deleteRequest)
                    }
                    bottomSheet.cancelButton.setOnClickListener { dialog.dismiss() }
                    if (paid){
                        bottomSheet.paymentButton.isEnabled = false
                        bottomSheet.paymentButton.text = "已成功付款"
                        bottomSheet.deleteButton.isEnabled = false
                        bottomSheet.deleteButton.text = "已付款者請聯絡合作社取消"
                    }else{
                        if((timeBool && hour>930) || (!timeBool && hour>1030)){
                            val timeString = if (timeBool) "09:30" else "10:30"
                            bottomSheet.paymentButton.isEnabled = false
                            bottomSheet.paymentButton.text = "已超過繳款時間($timeString)"
                        }else if(balance >= info.getJSONObject("money").getString("charge").toInt()) {
                            bottomSheet.paymentButton.text = "以學生證付款(餘額:$balance)"
                        }else{
                            bottomSheet.paymentButton.isEnabled = false
                            bottomSheet.paymentButton.text = "餘額不足（您只剩${balance}元）"
                        }
                        bottomSheet.deleteButton.text = "取消訂單"
                    }

                    bottomSheet.cancelButton.text = "返回"
                    dialog.setContentView(bottomSheet)
                    dialog.show()

                }
            }
            return layout
        }
    }

}
