package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_main_order.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.centerInParent
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*

class MainOrderActivity : AppCompatActivity() {
    private lateinit var progressBarHandler: ProgressBarHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_order)
        //indicator start
        progressBarHandler = ProgressBarHandler(this)
        //indicator end

//        val confirmString = """
//            您選擇的餐點是${selOrder1.name}，價錢為${selOrder1.cost}，確定請按訂餐。
//            請注意早上十點後將無法點餐!
//        """.trimIndent()
        foodArray.clear()
        foodArray.add(FoodInfo(selOrder1.name,"x1", selOrder1.cost))
        foodArray.add(FoodInfo("小計","x1", selOrder1.cost))

        val layoutManager = LinearLayoutManager(this)
        orderList.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(orderList.context,layoutManager.orientation)
        orderList.addItemDecoration(dividerItemDecoration)

        val adapter = OrderAdapter(this)
        orderList.adapter = adapter
        adapter.notifyDataSetChanged()

        //this.confirmText.text = confirmString
    }

    fun sendOrder(view: View){

        payBool = null
        selectedTime = ""
        canOrder = false
        orderIDParam.clear()
        orderIDParam.add(selOrder1.id)
        startActivity(Intent(this,ConfirmOrderActivity::class.java))

        return
        //indicator
        progressBarHandler.show()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
        val now = getCurrentDateTime()
        val hourFormat = SimpleDateFormat("HHmm", Locale.TAIWAN)
        val hour = hourFormat.format(now).toInt()
        println(now)
        val fullFormat = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        if(hour>1010) {
        //if(false){
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert("早上十點十分後無法訂餐，明日請早","超過訂餐時間") { positiveButton("OK"){} }.show()
        }else{
            //val orderURL = dsURL("make_self_order&dish_id[]=${selOrder1.id}&time=${fullFormat.format(now)}-12:00:00")
            val orderRequest = object: StringRequest(Method.POST, dsRequestURL, Response.Listener {
                if (isValidJson(it)){
                    val orderInfo = JSONArray(it)
                    val orderID = orderInfo.getJSONObject(0).getString("id")
                    //indicator
                    progressBarHandler.hide()
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
                    alert("訂單編號$orderID,請記得付款！", "點餐成功"){
                        positiveButton("OK"){
                            startActivity(Intent(this@MainOrderActivity, StudentMainActivity::class.java))
                        }
                    }.show()
                }else{
                    println(it)
                    if(it.contains("Off") || it.contains("Impossible")){
                        //indicator
                        progressBarHandler.hide()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("請不要在00:00-04:00之間或於點餐時間外點餐！","訂餐錯誤"){
                            positiveButton("OK"){}
                        }.show()
                    }else if (it.contains("Invalid")){
                        //indicator
                        progressBarHandler.hide()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("發生了不知名的錯誤。請嘗試重新登入，或嘗試重新開啟程式，若持續發生問題，請通知開發人員！", "Unexpected Error"){
                            positiveButton("OK"){}
                        }.show()
                    }else if (it == ""){
                        //indicator
                        progressBarHandler.hide()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("請重新登入", "您已經登出") {
                            positiveButton("OK") {
                                startActivity(Intent(this@MainOrderActivity, LoginActivity::class.java))
                            }
                        }.show()
                    }else if (it.contains("exceed")){
                        //indicator
                        progressBarHandler.hide()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("您的訂單中似乎有一或多項餐點已售完", "餐點已售完") {
                            positiveButton("OK") {
                                startActivity(Intent(this@MainOrderActivity, LoginActivity::class.java))
                            }
                        }.show()
                    }else{
                        //indicator
                        progressBarHandler.hide()
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        //indicator
                        alert("發生了不知名的錯誤。請嘗試重新登入，或嘗試重新開啟程式，若持續發生問題，請通知開發人員！", "Unexpected Error"){
                            positiveButton("OK"){}
                        }.build().apply {
                            setCancelable(false)
                            setCanceledOnTouchOutside(false)
                        }.show()
                    }
                }
            }, Response.ErrorListener {
                //indicator
                progressBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                    positiveButton("OK"){}
                }.build().apply {
                    setCancelable(false)
                    setCanceledOnTouchOutside(false)
                }.show()
            }){
                override fun getParams(): MutableMap<String, String> {
                    var postParam: MutableMap<String, String> = HashMap()
                    postParam["cmd"] = "make_self_order"
                    postParam["dish_id[0]"] = selOrder1.id
                    postParam["time"] = "${fullFormat.format(now)}-12:00:00"
                    return postParam
                }
            }
            VolleySingleton.getInstance(this).addToRequestQueue(orderRequest)
        }
    }

    class OrderAdapter(private var context: Context):
        RecyclerView.Adapter<OrderAdapter.ViewHolder>() {


        class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            lateinit var nameText: TextView
            lateinit var qtyText: TextView
            lateinit var costText: TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val cell = LayoutInflater.from(context).inflate(R.layout.order_list_cell,parent,false)
            val viewHolder = ViewHolder(cell)
            viewHolder.nameText = cell.findViewById(R.id.nameText)
            viewHolder.qtyText = cell.findViewById(R.id.qtyText)
            viewHolder.costText = cell.findViewById(R.id.costText)
            return viewHolder
        }

        override fun getItemCount(): Int {
            return foodArray.count()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val info = foodArray[position]
            holder.nameText.text = info.name
            holder.qtyText.text = info.qty
            holder.costText.text = info.cost + '$'
        }
    }
}
