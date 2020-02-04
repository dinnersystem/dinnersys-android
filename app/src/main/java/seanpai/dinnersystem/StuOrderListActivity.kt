package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
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

        //recycle
        val linearLayoutManager = LinearLayoutManager(this)
        factoryList.layoutManager = linearLayoutManager
        val dividerItemDecoration = DividerItemDecoration(factoryList.context,linearLayoutManager.orientation)
        factoryList.addItemDecoration(dividerItemDecoration)

        val dishRequest = StringRequest(url, Response.Listener { response ->
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
                    }
//                    else when (item.getJSONObject("department").getJSONObject("factory").getString("name")) {
//                        "台灣小吃部" -> taiwanMenuJson.put(item)
//                        "愛佳便當" -> aiJiaMenuJson.put(item)
//                        "關東煮" -> guanDonMenuJson.put(item)
//                        "合作社" -> cafetMenuJson.put(item)
//                        else -> {
//                        }
//                    }
                    j += 1
                }
                println(factoryNames)
                val factoryAdapter = FactoryAdapter(this)
                factoryList.adapter = factoryAdapter
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

    class FactoryAdapter : RecyclerView.Adapter<FactoryAdapter.ViewHolder> {
        private var context: Context
        private var data = factoryNames

        constructor(context: Context) : super() {
            this.context = context
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
            holder.factoryName.text = name
            holder.chooseButton.setOnClickListener {
                selectedFactoryArr = splitMenuDict[name]!!
                if(selectedFactoryArr.getJSONObject(0).getJSONObject("department").getJSONObject("factory").getString("allow_custom") == "true"){
                    startActivity(it.context,Intent(it.context, GuandonOrderListActivity::class.java), null)
                }else{
                    startActivity(it.context,Intent(it.context, MainMenuActivity::class.java),null)
                }
            }
        }

        class ViewHolder : RecyclerView.ViewHolder {
            lateinit var factoryName: TextView
            lateinit var chooseButton: Button

            constructor(itemView: View) : super(itemView)
        }

    }


}
