package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONArray
import org.json.JSONObject
import seanpai.dinnersystem.databinding.ActivityDinnermanMainBinding
import java.text.SimpleDateFormat
import java.util.*

class DinnermanMainActivity : AppCompatActivity() {

    private lateinit var progressBarHandler: ProgressBarHandler
    private lateinit var activityBinding: ActivityDinnermanMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityDinnermanMainBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        progressBarHandler = ProgressBarHandler(this)

        val linearLayoutManager = LinearLayoutManager(this)
        activityBinding.tableView.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(activityBinding.tableView.context, linearLayoutManager.orientation)
        activityBinding.tableView.addItemDecoration(dividerItemDecoration)

        reloadData()
    }
    private var back = true
    override fun onBackPressed() {
        //super.onBackPressed()
        if(back){
            back = false
//            toast("再按一次以登出")
            Toast.makeText(this, "再按一次以登出", Toast.LENGTH_SHORT).show()
        }else{
            startActivity(Intent(this@DinnermanMainActivity, RemLoginActivity::class.java))
        }
    }

    private fun reloadData(){
        val now = getCurrentDateTime()

        progressBarHandler.show()

        //val now = java.util.Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN)
        println(formatter.format(now))
//        val selSelf = dsURL("select_self&history=true&esti_start=${formatter.format(now)}-00:00:00&esti_end=${formatter.format(now)}-23:59:59")
        val historyRequest = object: StringRequest(Method.POST, dsRequestURL,
            Response.Listener {
                historyArr = JSONArray("[]")
                DMFactoryName.clear()
                DMHistoryArr.clear()
                DMListArr.clear()
                dishNameArr = emptyArray()
                DMDishNameArr.clear()
                if(it != ""){
                    if(it != "[]"){
                        if (isValidJson(it)){
                            println(it)
                            historyArr = JSONArray(it)
                            for (i in 0 until historyArr.length()) {
                                val info = historyArr.getJSONObject(i)
                                val dishFactoryName = info.getJSONArray("dish").getJSONObject(0).getJSONObject("department").getJSONObject("factory").getString("name")
                                if(!DMFactoryName.contains(dishFactoryName)){
                                    DMFactoryName.add(dishFactoryName)
                                    DMHistoryArr[dishFactoryName] = JSONArray("[]")
                                }
                                DMHistoryArr[dishFactoryName]!!.put(info)
                            }
                            for(i in DMHistoryArr){
                                val nameJSONString = "{\"name\":\"${i.key}\"}"
                                DMListArr.add(Pair("name", JSONObject(nameJSONString)))
                                DMDishNameArr.add("")
                                for(order in 0 until i.value.length()){
                                    DMListArr.add(Pair("order", i.value.getJSONObject(order)))
                                    if (i.value.getJSONObject(order).getJSONArray("dish").length() > 1) {
                                        var dName = ""
                                        for (j in 0 until i.value.getJSONObject(order).getJSONArray("dish").length()) {
                                            val food = i.value.getJSONObject(order).getJSONArray("dish").getJSONObject(j)
                                            dName += "${food.getString("dish_name")}+"
                                        }
                                        dName = dName.dropLast(1)
                                        DMDishNameArr.add(dName)
                                    } else {
                                        DMDishNameArr.add(i.value.getJSONObject(order).getJSONArray("dish").getJSONObject(0).getString("dish_name"))
                                    }
                                }
                            }
                            progressBarHandler.hide()
                            val adapter = HistoryAdapter(this)
                            activityBinding.tableView.adapter = adapter
                            adapter.notifyDataSetChanged()
                        }else{
                            progressBarHandler.hide()
//                            alert("請重新登入","您已經登出"){
//                                positiveButton("OK"){
//                                    startActivity(Intent(this@DinnermanMainActivity, LoginActivity::class.java))
//                                }
//                            }.show()
                            AlertDialog.Builder(this)
                                .setTitle("請重新登入")
                                .setMessage("您已經登出")
                                .setPositiveButton("OK") { _, _ ->
                                    startActivity(Intent(this@DinnermanMainActivity, LoginActivity::class.java))
                                }
                                .show()
                        }
                    }else{
                        progressBarHandler.hide()
                        historyArr = JSONArray("[]")
                        DMFactoryName.clear()
                        DMHistoryArr.clear()
                        DMListArr.clear()
                        val adapter = HistoryAdapter(this)
                        activityBinding.tableView.adapter = adapter

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
                    progressBarHandler.hide()
                    //logout
//                    alert("請重新登入","您已經登出"){
//                        positiveButton("OK"){
//                            startActivity(Intent(this@DinnermanMainActivity, LoginActivity::class.java))
//                        }
//                    }
                    AlertDialog.Builder(this)
                        .setTitle("請重新登入")
                        .setMessage("您已經登出")
                        .setPositiveButton("OK") { _, _ ->
                            startActivity(Intent(this@DinnermanMainActivity, LoginActivity::class.java))
                        }
                        .show()
                }
            },
            Response.ErrorListener {
                progressBarHandler.hide()
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
                postParam["cmd"] = "select_class"
                postParam["esti_start"] = "${formatter.format(now)}-00:00:00"
                postParam["esti_end"] = "${formatter.format(now)}-23:59:59"
                postParam["history"] = "true"
                return postParam
            }
        }
        VolleySingleton.getInstance(this).addToRequestQueue(historyRequest)
    }

    class HistoryAdapter(private var context: Context) :
        RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
        private var data = DMListArr

        init {
            this.data = DMListArr
        }

        override fun getItemViewType(position: Int): Int {
            return if(DMListArr[position].first == "name"){
                R.layout.dm_factory_name_cell
            }else{
                R.layout.history_list_cell
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            if (viewType == R.layout.dm_factory_name_cell){
                val cell = LayoutInflater.from(context).inflate(R.layout.dm_factory_name_cell,parent,false)
                val viewHolder = ViewHolder(cell)
                viewHolder.factoryName = cell.findViewById(R.id.factoryName)
                return viewHolder
            }
            val cell = LayoutInflater.from(context).inflate(R.layout.history_list_cell,parent,false)
            val viewHolder = ViewHolder(cell)
            viewHolder.detailTitle = cell.findViewById(R.id.detailTitle)
            viewHolder.title = cell.findViewById(R.id.title)
            viewHolder.infoButton = cell.findViewById(R.id.infoButton)

            return viewHolder
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val element = DMListArr[position]
            val type = element.first
            val info = element.second
            if(type == "name"){
                holder.factoryName.text = info.getString("name")
            }else{
                if(info.getJSONArray("dish").length() > 1){
                    holder.title.text = "自訂套餐(${info.getJSONArray("dish").length()}樣)"
                }else{
                    holder.title.text = info.getJSONArray("dish").getJSONObject(0).getString("dish_name")
                }
                val paidString = info.getJSONObject("money").getJSONArray("payment").getJSONObject(0).getString("paid")
                var isPaid = ""
                if(paidString == "true"){
                    isPaid = "已付款"
                }else{
                    isPaid = "未付款"
                }
                holder.detailTitle.text = "${info.getJSONObject("user").getString("seat_no")}${info.getJSONObject("user").getString("name").trimEnd()}, ${info.getJSONObject("money").getString("charge")}$, $isPaid"
                holder.infoButton.setOnClickListener {
                    val infoAlertBuilder = AlertDialog.Builder(context)
                    infoAlertBuilder.setTitle("訂單內容")
                    infoAlertBuilder.setMessage("訂餐編號:${info.getString("id")}\n" +
                            "餐點內容:${DMDishNameArr[position]}\n" +
                            "訂購人:${info.getJSONObject("user").getString("seat_no")}${info.getJSONObject("user").getString("name")}\n" +
                            "訂餐日期:${info.getString("recv_date").dropLast(3)}\n" +
                            "餐點金額:${info.getJSONObject("money").getString("charge")}\n" +
                            "付款狀態:$isPaid\n")
                    infoAlertBuilder.setPositiveButton("OK"){_, _ ->}
                    val infoAlert = infoAlertBuilder.create()
                    infoAlert.show()
                }
            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            lateinit var title: TextView
            lateinit var detailTitle: TextView
            lateinit var infoButton: Button
            lateinit var factoryName: TextView
        }

    }
}
