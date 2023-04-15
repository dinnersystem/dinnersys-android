package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray
import seanpai.dinnersystem.databinding.ActivityGuanDonOrderBinding
import java.text.SimpleDateFormat
import java.util.*

class GuanDonOrderActivity : AppCompatActivity() {
    private lateinit var progressBarHandler: ProgressBarHandler
    private lateinit var activityBinding: ActivityGuanDonOrderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityGuanDonOrderBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)
        //indicator start
        progressBarHandler = ProgressBarHandler(this)
        //indicator end

        val layoutManager = LinearLayoutManager(this)
        activityBinding.guandonOrderList.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(activityBinding.guandonOrderList.context,layoutManager.orientation)
        activityBinding.guandonOrderList.addItemDecoration(dividerItemDecoration)

        val adapter = GuanDonOrderAdapter(this)
        activityBinding.guandonOrderList.adapter = adapter
        adapter.notifyDataSetChanged()
    }


    fun sendGuanDonOrder(view:View){

        payBool = null
        selectedTime = ""
        canOrder = false
        orderIDParam.clear()
        orderIDParam.addAll(guanDonParam)
        startActivity(Intent(this,ConfirmOrderActivity::class.java))

    }

    class GuanDonOrderAdapter(private var context: Context):
        RecyclerView.Adapter<GuanDonOrderAdapter.ViewHolder>() {


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
