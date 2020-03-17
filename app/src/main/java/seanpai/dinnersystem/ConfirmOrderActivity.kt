package seanpai.dinnersystem

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_confirm_order.*
import kotlinx.android.synthetic.main.paym_pw_alert.view.*
import org.jetbrains.anko.alert
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class ConfirmOrderActivity : AppCompatActivity() {

    private lateinit var progressBarHandler: ProgressBarHandler


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_order)

        progressBarHandler = ProgressBarHandler(this)

        val layoutManager = LinearLayoutManager(this)
        confirmList.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(confirmList.context,layoutManager.orientation)
        confirmList.addItemDecoration(dividerItemDecoration)

        //confirmContentList: source of recycleView
        //confirmData: source of confirmActivity



        confirmContentList.clear()
        confirmContentList.add(Pair(confirmData.dishName, confirmData.dishCost+"$"))
        confirmContentList.add(Pair("取餐時間","請選擇"))
        confirmContentList.add(Pair("付款方式","請選擇"))

        //factory bounds
        val dish = splitMenuDict[confirmData.factoryName]!!.getJSONObject(0)
        paymentTimeString = dish.getJSONObject("department").getJSONObject("factory").getString("payment_time")
        prepareTimeString = dish.getJSONObject("department").getJSONObject("factory").getString("prepare_time")
        paymentTime = paymentTimeString.split(":")
        prepareTime = prepareTimeString.split(":")
        factory = dish.getJSONObject("department").getJSONObject("factory")

        val adapter = ConfirmAdapter(this)
        confirmList.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    fun reloadData(){
        println(payBool != null && selectedTime != "")
        if(payBool != null){
            if(payBool!!){
                confirmContentList[2] = Pair("付款方式","以學生證付款")
            }else if (payBool == false){
                confirmContentList[2] = Pair("付款方式","暫不付款")
            }
        }
        if(selectedTime != ""){
            confirmContentList[1] = Pair("取餐時間",selectedTime)
        }
        canOrder = payBool != null && selectedTime != ""
        val adapter = ConfirmAdapter(this)
        confirmList.adapter = adapter
        adapter.notifyDataSetChanged()
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

    class ConfirmAdapter(context: Context): RecyclerView.Adapter<ConfirmAdapter.ViewHolder>(){
        val activity = context as ConfirmOrderActivity
        val mContext = context

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
            lateinit var headerText: TextView
            lateinit var contentView: View
            lateinit var titleText: TextView
            lateinit var detailText: TextView
            lateinit var sendButton: Button
        }

        override fun getItemViewType(position: Int): Int {
            return if(position == confirmContentList.size) R.layout.confirm_send_button else R.layout.confirm_order_cell
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {
            if(viewType == R.layout.confirm_send_button){
                val cell = LayoutInflater.from(mContext).inflate(R.layout.confirm_send_button,parent,false)
                val viewHolder = ViewHolder(cell)
                viewHolder.sendButton = cell.findViewById(R.id.sendButton)
                return viewHolder
            }
            val cell = LayoutInflater.from(mContext).inflate(R.layout.confirm_order_cell,parent,false)
            val viewHolder = ViewHolder(cell)
            viewHolder.contentView = cell.findViewById(R.id.contentView)
            viewHolder.detailText = cell.findViewById(R.id.detailText)
            viewHolder.headerText = cell.findViewById(R.id.headerText)
            viewHolder.titleText = cell.findViewById(R.id.titleText)

            return viewHolder
        }

        override fun getItemCount(): Int {
            return confirmContentList.size + 1
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if(position != confirmContentList.size){
                val info = confirmContentList[position]
                if(position == 0){
                    holder.headerText.text = confirmData.factoryName
                    holder.contentView.isClickable = false
                    holder.contentView.isFocusable = false
                }
                else{
                    holder.headerText.text = ""
                }


                holder.titleText.text = info.first
                holder.detailText.text = info.second

                if (position == 1){
                    val now = getCurrentDateTime()
                    var dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
                    val currentDateString = dateFormat.format(now)
                    val upperBoundTimeString = factory.getString("avail_upper_bound")
                    val lowerBoundTimeString = factory.getString("upper_bound")
                    val upperBoundDateString = "$currentDateString $upperBoundTimeString"
                    val lowerBoundDateString = "$currentDateString $lowerBoundTimeString"
                    dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.TAIWAN)
                    val upperBound = dateFormat.parse(upperBoundDateString)
                    val lowerBound = dateFormat.parse(lowerBoundDateString)
                    var availTime = upperBound
                    val pickerTime: MutableList<String> = mutableListOf()
                    holder.contentView.setOnClickListener {
                        pickerTime.clear()
                        availTime = upperBound
                        while(availTime <= lowerBound){
                            pickerTime.add(dateFormat.format(availTime))
                            availTime = addTime(availTime,1,0,0)
                        }
                        val builder = AlertDialog.Builder(mContext)
                        builder.setTitle("請選擇取餐時間")
                        builder.setItems(pickerTime.toTypedArray()) { dialog, which ->
                            selectedTime = pickerTime[which]
                            println(selectedTime)
                            activity.reloadData()
                        }
                        val dialog = builder.create()
                        dialog.show()
                    }

                    if (holder.detailText.text == "請選擇"){
                        holder.detailText.setTextColor(ContextCompat.getColor(mContext,R.color.special))
                    }else{
                        holder.detailText.setTextColor(ContextCompat.getColor(mContext,R.color.pureBlack))
                    }
                }else if(position == 2){
                    if (confirmData.dishCost.toInt() > balance){
                        holder.headerText.text = "學生證餘額不足(${balance}元)"
                    }
                    holder.headerText.setTextColor(ContextCompat.getColor(mContext,R.color.special))
                    holder.contentView.setOnClickListener {
                        val builder = AlertDialog.Builder(mContext)
                        builder.setTitle("請選擇付款方式")
                        if (confirmData.dishCost.toInt() > balance){
                            val pickerPayment = arrayOf("暫不付款")
                            println(pickerPayment)
                            builder.setItems(pickerPayment){ dialog, which ->
                                if (which == 0){
                                    payBool = false
                                }
                                activity.reloadData()
                            }
                            //builder.setMessage("學生證餘額不足(${balance}元)")
                            val dialog = builder.create()
                            dialog.show()
                        }else{
                            val pickerPayment = arrayOf("學生證付款(餘額:${balance}元)","暫不付款")
                            builder.setItems(pickerPayment) { dialog, which ->
                                println(which)
                                if (which == 0){
                                    payBool = true
                                }else if (which == 1){
                                    payBool = false
                                }
                                println(payBool)
                                activity.reloadData()
                            }
                            builder.create().show()
                        }

                    }

                    if (holder.detailText.text == "請選擇"){
                        holder.detailText.setTextColor(ContextCompat.getColor(mContext,R.color.special))
                    }else{
                        holder.detailText.setTextColor(ContextCompat.getColor(mContext,R.color.pureBlack))
                    }
                }
            }else{
                if(canOrder){
                    holder.sendButton.text = "送出訂單"
                    holder.sendButton.setTextColor(ContextCompat.getColor(mContext, R.color.pureBlack))
                    holder.sendButton.background = ContextCompat.getDrawable(mContext, R.drawable.button_outline)
                    holder.sendButton.setOnClickListener {
                        activity.startInd()
                        //time
                        val now = getCurrentDateTime()
                        var dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
                        val currentDateString = dateFormat.format(now)
                        dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss",Locale.TAIWAN)
                        val upperBound = dateFormat.parse(currentDateString + " " + factory.getString("upper_bound"))
                        val orderDeadLine = addTime(upperBound,-1*prepareTime[0].toInt(),-1*prepareTime[1].toInt(), -1* prepareTime[2].toInt())
                        val paymentDeadLine = addTime(upperBound,-1* paymentTime[0].toInt(),-1*paymentTime[1].toInt(), -1* paymentTime[2].toInt())
//                        if (false) {
                        if (now > orderDeadLine){
                            activity.stopInd()
                            dateFormat = SimpleDateFormat("aHH:mm", Locale.TAIWAN)
                            var time = dateFormat.format(orderDeadLine)
                            time = time.replace("PM","下午").replace("AM","上午")
                            activity.alert("${time}後無法訂餐，明日請早","超過訂餐時間") { positiveButton("OK"){} }.show()
                        }else{
                            //send order
                            val orderRequest = object: StringRequest(Method.POST, dsRequestURL, Response.Listener {
                                if (isValidJson(it)){
                                    val orderInfo = JSONArray(it)
                                    val orderID = orderInfo.getJSONObject(0).getString("id")

                                    val layoutInflater = LayoutInflater.from(mContext)
                                    if(payBool!!){
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
//                                val paymentURL =
//                                    dsURL("payment_self&target=true&order_id=${info.getString("id")}&password=$paymentString&time=$timeStamp")
                                                val paymentRequest = object : StringRequest(Method.POST, dsRequestURL, Response.Listener {
                                                    if(isValidJson(it)){
                                                        activity.stopInd()
                                                        val successAlertBuilder = AlertDialog.Builder(mContext)
                                                        successAlertBuilder.setTitle("繳款完成").setMessage("請注意付款狀況，實際情況仍以頁面為主")
                                                        successAlertBuilder.setPositiveButton("OK"){_, _ ->
                                                            activity.startActivity(Intent(mContext, StudentMainActivity::class.java))
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
                                                        postParam["order_id"] = orderID
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
                                    }else{
                                        //indicator
                                        activity.stopInd()
                                        //indicator
                                        activity.alert("訂單編號$orderID,請記得付款！", "點餐成功"){
                                            positiveButton("OK"){
                                                activity.startActivity(Intent(mContext,StudentMainActivity::class.java))
                                            }
                                        }.show()
                                    }


                                }else{
                                    println(it)
                                    if(it.contains("Off") || it.contains("Impossible")){
                                        //indicator
                                        activity.stopInd()
                                        //indicator
                                        activity.alert("請不要在00:00-04:00之間或於點餐時間外點餐！","訂餐錯誤"){
                                            positiveButton("OK"){}
                                        }.show()
                                    }else if (it.contains("Invalid")){
                                        //indicator
                                        activity.stopInd()
                                        //indicator
                                        activity.alert("發生了不知名的錯誤。請嘗試重新登入，或嘗試重新開啟程式，若持續發生問題，請通知開發人員！", "Unexpected Error"){
                                            positiveButton("OK"){}
                                        }.show()
                                    }else if (it == ""){
                                        //indicator
                                        activity.stopInd()
                                        //indicator
                                        activity.alert("請重新登入", "您已經登出") {
                                            positiveButton("OK") {
                                                activity.startActivity(Intent(mContext,StudentMainActivity::class.java))
                                            }
                                        }.show()
                                    }else if (it.contains("exceed")){
                                        //indicator
                                        activity.stopInd()
                                        //indicator
                                        activity.alert("您的訂單中似乎有一或多項餐點已售完", "餐點已售完") {
                                            positiveButton("OK") {
                                                activity.startActivity(Intent(mContext,StudentMainActivity::class.java))
                                            }
                                        }.show()
                                    }else{
                                        //indicator
                                        activity.stopInd()
                                        //indicator
                                        activity.alert("發生了不知名的錯誤。請嘗試重新登入，或嘗試重新開啟程式，若持續發生問題，請通知開發人員！", "Unexpected Error"){
                                            positiveButton("OK"){}
                                        }.build().apply {
                                            setCancelable(false)
                                            setCanceledOnTouchOutside(false)
                                        }.show()
                                    }
                                }
                            }, Response.ErrorListener {
                                //indicator
                                activity.stopInd()
                                //indicator
                                activity.alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                                    positiveButton("OK"){}
                                }.build().apply {
                                    setCancelable(false)
                                    setCanceledOnTouchOutside(false)
                                }.show()
                            }){
                                override fun getParams(): MutableMap<String, String> {
                                    val postParam: MutableMap<String, String> = HashMap()
                                    postParam["cmd"] = "make_self_order"

                                    var dishIDParamCount = 0
                                    for (did in orderIDParam){
                                        postParam["dish_id[${dishIDParamCount++}]"] = did
                                    }

                                    postParam["time"] = selectedTime.replace(" ", "-")
                                    return postParam
                                }
                            }
                            VolleySingleton.getInstance(mContext).addToRequestQueue(orderRequest)
                        }


                    }
                }else{
                    holder.sendButton.background = ContextCompat.getDrawable(mContext, R.drawable.confirm_cell_shape)
                    holder.sendButton.text = "請選擇內容"
                    holder.sendButton.isClickable = false
                    holder.sendButton.setTextColor(ContextCompat.getColor(mContext, R.color.special))
                }
            }


        }

    }
}
