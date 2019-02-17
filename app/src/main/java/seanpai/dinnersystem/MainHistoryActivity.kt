package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_main_history.*
import kotlinx.android.synthetic.main.guandon_list_cell.view.*
import kotlinx.android.synthetic.main.history_bottom_list_view.view.*
import kotlinx.android.synthetic.main.history_list_cell.view.*
import kotlinx.android.synthetic.main.paym_pw_alert.view.*
import org.jetbrains.anko.alert
import org.json.JSONArray
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
        //indicator end
        val adapter = TableAdapter(this)
        tableView.adapter = adapter
        reloadData(null)

    }


    companion object {
        fun init(): MainHistoryActivity = MainHistoryActivity()
    }

    fun reloadData(view: View?) {
        //indicator
        indicatorView.visibility = View.VISIBLE
        indicatorView.bringToFront()
        progressBar.visibility = View.VISIBLE
        progressBar.bringToFront()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
        //get_money_value
        val balanceURL = dsURL("get_money")
        val balanceRequest = StringRequest(balanceURL, Response.Listener {
            balance = it.trim().toInt()
            balanceText.text = "餘額：$balance$"
        }, Response.ErrorListener {
            //indicator
            indicatorView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
            positiveButton("OK"){}
        }.show() })
        val now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())

        //val now = java.util.Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        println(formatter.format(now))
        val selSelf = dsURL("select_self&history=true&esti_start=${formatter.format(now)}-00:00:00&esti_end=${formatter.format(now)}-23:59:59")
        val historyRequest = StringRequest(selSelf,
            Response.Listener {
                println(it)
                if(it != ""){
                    if(it != "[]"){
                    if (isValidJson(it)){
                        historyArr = JSONArray(it)
                        val adapter = TableAdapter(this)
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

        VolleySingleton.getInstance(this).addToRequestQueue(balanceRequest)
        VolleySingleton.getInstance(this).addToRequestQueue(historyRequest)


    }

    class TableAdapter(context: Context): BaseAdapter() {
        val page = MainHistoryActivity.init()
        val mContext: Context = context
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
                    layout.title.text = info.getJSONArray("dish").getString(0)
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
                    bottomSheet.textMessage.text = "訂餐編號:${info.getString("id")}\n餐點內容:${info.getString("dish_name")}\n訂餐日期:${info.getString("recv_date").dropLast(3)}\n餐點金額:${info.getJSONObject("money").getString("charge")}\n付款狀態:$paidStr"
                    bottomSheet.paymentButton.setOnClickListener{
                        val alert = AlertDialog.Builder(mContext)
                        val inputView = layoutInflater.inflate(R.layout.paym_pw_alert, null)
                        val paymentPwText = inputView.paymentPW
                        alert.setView(inputView)
                        alert.setTitle("請輸入繳款密碼")
                        alert.setMessage("預設為身分證後四碼")
                        alert.setPositiveButton(R.string.send){ dialog, which ->
                            //indicator
                            page.indicatorView.visibility = View.VISIBLE
                            page.indicatorView.bringToFront()
                            page.progressBar.visibility = View.VISIBLE
                            page.progressBar.bringToFront()
                            page.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            //indicator
                            val paymentPw = paymentPwText.text
                            if (paymentPw.isBlank()){
                                //indicator
                                page.indicatorView.visibility = View.INVISIBLE
                                page.progressBar.visibility = View.INVISIBLE
                                page.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                //indicator
                                val errorAlertBuilder = AlertDialog.Builder(mContext)
                                errorAlertBuilder.setTitle("錯誤")
                                errorAlertBuilder.setMessage("繳款密碼不可為空")
                                errorAlertBuilder.setPositiveButton("OK") { _, _ -> }
                                val errorAlert = errorAlertBuilder.create()
                                errorAlert.show()
                            }else{
                                val timeStamp = (System.currentTimeMillis() / 1000).toString()
                                val paymentString = paymentPw.toString()
                                val usr = constUsername
                                val pwd = constPassword
                                val noHash = "{\"id\":\"${info.getString("id")}\",\"usr_id\":\"$usr\",\"usr_password\":\"$pwd\",\"pmt_password\":\"$paymentString\",\"time\":\"$timeStamp\"}"
                                val hash = noHash.sha512()
                                val paymentURL = dsURL("payment_self&target=true&order_id=${info.getString("id")}&hash=$hash")
                                val paymentRequest = StringRequest(paymentURL, Response.Listener {
                                    if(isValidJson(it)){
                                        //indicator
                                        page.indicatorView.visibility = View.INVISIBLE
                                        page.progressBar.visibility = View.INVISIBLE
                                        page.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                        //indicator
                                        val successAlertBuilder = AlertDialog.Builder(mContext)
                                        successAlertBuilder.setTitle("繳款完成").setMessage("請注意付款狀況，實際情況仍以頁面為主")
                                        successAlertBuilder.setPositiveButton("OK"){_, _ ->
                                                page.reloadData(null)
                                        }
                                        val successAlert = successAlertBuilder.create()
                                        successAlert.show()
                                    }else{
                                        //indicator
                                        page.indicatorView.visibility = View.INVISIBLE
                                        page.progressBar.visibility = View.INVISIBLE
                                        page.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                        //indicator
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
                                }, Response.ErrorListener {
                                    //indicator
                                    page.indicatorView.visibility = View.INVISIBLE
                                    page.progressBar.visibility = View.INVISIBLE
                                    page.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                                    //indicator
                                    val errorAlertBuilder = AlertDialog.Builder(mContext)
                                    errorAlertBuilder.setTitle("不知名的錯誤")
                                    errorAlertBuilder.setMessage("請注意網路狀態，或通知開發人員!")
                                    errorAlertBuilder.setPositiveButton("OK") { _, _ -> }
                                    val errorAlert = errorAlertBuilder.create()
                                    errorAlert.show()
                                })
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
                        //indicator
                        page.indicatorView.visibility = View.VISIBLE
                        page.indicatorView.bringToFront()
                        page.progressBar.visibility = View.VISIBLE
                        page.progressBar.bringToFront()
                        page.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        val deleteURL = dsURL("delete_self&order_id=${info.getString("id")}")
                        val deleteRequest = StringRequest(deleteURL, Response.Listener {
                            //indicator
                            page.indicatorView.visibility = View.INVISIBLE
                            page.progressBar.visibility = View.INVISIBLE
                            page.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            //indicator
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
                        }, Response.ErrorListener {
                            //indicator
                            page.indicatorView.visibility = View.INVISIBLE
                            page.progressBar.visibility = View.INVISIBLE
                            page.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            //indicator
                            val errorAlertBuilder = AlertDialog.Builder(mContext)
                            errorAlertBuilder.setTitle("不知名的錯誤")
                            errorAlertBuilder.setMessage("請注意網路狀態，或通知開發人員!")
                            errorAlertBuilder.setPositiveButton("OK") { _, _ ->
                                startActivity(mContext,Intent(page,LoginActivity::class.java),null)
                            }
                            val errorAlert = errorAlertBuilder.create()
                            errorAlert.show()
                        })
                        VolleySingleton.getInstance(mContext).addToRequestQueue(deleteRequest)
                    }
                    bottomSheet.cancelButton.setOnClickListener { dialog.dismiss() }
                    if (paid){
                        bottomSheet.paymentButton.isEnabled = false
                        bottomSheet.paymentButton.text = "已成功付款"
                        bottomSheet.deleteButton.isEnabled = false
                        bottomSheet.paymentButton.text = "已付款者請聯絡合作社取消"
                    }else{
                        bottomSheet.paymentButton.text = "以學生證付款(餘額:$balance)"
                        bottomSheet.deleteButton.text = "取消訂單"
                    }
                    val now = Date.from(java.time.LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                    val hourFormat = SimpleDateFormat("HHmm", Locale("zh-TW"))
                    val hour = hourFormat.format(now).toInt()
                    if(hour>1030){
                        bottomSheet.paymentButton.isEnabled = false
                        bottomSheet.paymentButton.text = "已超過繳款時間(10:30)"
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
