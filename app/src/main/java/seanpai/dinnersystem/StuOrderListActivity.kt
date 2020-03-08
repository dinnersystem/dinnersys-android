package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_stu_order_list.*
import org.jetbrains.anko.alert
import org.json.JSONArray
import org.json.JSONObject


var factoryNames: MutableList<String> = mutableListOf()

class StuOrderListActivity : AppCompatActivity() {

    private lateinit var progressBarHandler: ProgressBarHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_order_list)

        //indicator start
        progressBarHandler = ProgressBarHandler(this)
        //indicator end


        //indicator
        progressBarHandler.show()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator

        //recycle
        val linearLayoutManager = LinearLayoutManager(this)
        factoryList.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(factoryList.context,linearLayoutManager.orientation)
        factoryList.addItemDecoration(dividerItemDecoration)

        val dishRequest = object : StringRequest(Method.POST, dsRequestURL, Response.Listener { response ->
            allMenuJson = JSONArray("[]")
            taiwanMenuJson = JSONArray("[]")
            aiJiaMenuJson = JSONArray("[]")
            cafetMenuJson = JSONArray("[]")
            guanDonMenuJson = JSONArray("[]")
            factoryNames = mutableListOf()
            splitMenuDict = mutableMapOf()
            if (isValidJson(response)){
                allMenuJson = JSONArray(response)
                var j=0
                for(i in 0 until allMenuJson.length()) {
                    val item = allMenuJson.getJSONObject(j)
                    if(item.getString("remaining") == "2147483647"){
                        item.put("remaining", "1000")
                    }
                    if (item.getString("is_idle") == "1") {
                        allMenuJson.remove(j)
                        j -= 1
                    }else{
                        if(!factoryNames.contains(item.getJSONObject("department").getJSONObject("factory").getString("name"))) {
                            factoryNames.add(item.getJSONObject("department").getJSONObject("factory").getString("name"))
                            splitMenuDict[item.getJSONObject("department").getJSONObject("factory").getString("name")] = JSONArray("[]")
                        }
                        splitMenuDict[item.getJSONObject("department").getJSONObject("factory").getString("name")]!!.put(item)
                        if(item.getJSONObject("department").getJSONObject("factory").getString("allow_custom") == "false"){
                            randomMenuArr.put(item)
                        }
                    }
                    j += 1
                }
                factoryNames.add("Random")
                println(factoryNames)
                val factoryAdapter = FactoryAdapter(this)
                factoryList.adapter = factoryAdapter
            }else{
                //indicator
                progressBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert("請重新登入","您已經登出"){
                    positiveButton("OK"){
                        startActivity(Intent(this@StuOrderListActivity, LoginActivity::class.java))
                    }
                }.show()
            }
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
        },Response.ErrorListener {
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                positiveButton("OK"){}
            }.show()
        }){
            override fun getParams(): MutableMap<String, String> {
                val postParam: MutableMap<String, String> = HashMap()
                postParam["cmd"] = "show_dish"
                return postParam
            }
        }

        val balanceRequest = object : StringRequest(Method.POST, dsRequestURL, Response.Listener {
            if (isValidJson(it)) {
                balance = JSONObject(it).getString("money").toInt()
                VolleySingleton.getInstance(this).addToRequestQueue(dishRequest)
            } else {
                //indicator
                progressBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                if(!this.isFinishing){
                    alert("查詢餘額失敗，我們已經派出最精銳的猴子去修理這個問題，若長時間出現此問題請通知開發人員！", "請重新登入") {
                        positiveButton("OK") {
                            startActivity(Intent(this@StuOrderListActivity, LoginActivity::class.java))
                        }
                    }.show()
                }
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


    class FactoryAdapter(private var context: Context) :
        RecyclerView.Adapter<FactoryAdapter.ViewHolder>() {
        private var data = factoryNames

        init {
            this.data = factoryNames
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val cell = LayoutInflater.from(context).inflate(R.layout.factory_list_cell,parent,false)
            val viewHolder = ViewHolder(cell)
            viewHolder.chooseButton = cell.findViewById(R.id.chooseButton)
            viewHolder.factoryName = cell.findViewById(R.id.factoryName)
            return viewHolder
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val name = data[position]
            if (name == "Random"){
                holder.factoryName.text = "想不到要吃什麼？"
            }else{
                holder.factoryName.text = name
            }

            holder.chooseButton.setOnClickListener {
                if(name == "Random"){
                    startActivity(it.context, Intent(it.context, RandomOrderActivity::class.java),null)
                    return@setOnClickListener
                }
                selectedFactoryArr = splitMenuDict[name]!!
                if(selectedFactoryArr.getJSONObject(0).getJSONObject("department").getJSONObject("factory").getString("allow_custom") == "true"){
                    startActivity(it.context,Intent(it.context, GuandonOrderListActivity::class.java), null)
                }else{
                    startActivity(it.context,Intent(it.context, MainMenuActivity::class.java),null)
                }
            }
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            lateinit var factoryName: TextView
            lateinit var chooseButton: Button

        }

    }


}
