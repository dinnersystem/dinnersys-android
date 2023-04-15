package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import seanpai.dinnersystem.databinding.ActivityMainOrderBinding

class MainOrderActivity : AppCompatActivity() {
    private lateinit var progressBarHandler: ProgressBarHandler
    private lateinit var activityBinding: ActivityMainOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainOrderBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)
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
        activityBinding.orderList.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(activityBinding.orderList.context,layoutManager.orientation)
        activityBinding.orderList.addItemDecoration(dividerItemDecoration)

        val adapter = OrderAdapter(this)
        activityBinding.orderList.adapter = adapter
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
