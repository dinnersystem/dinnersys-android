package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_guandon_order_list.*
import kotlinx.android.synthetic.main.activity_guandon_order_list.view.*
import kotlinx.android.synthetic.main.guandon_list_cell.view.*

class GuandonOrderListActivity : AppCompatActivity() {
    var totalCost = 0
    var totalSelected = 0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guandon_order_list)
        val adaptor = TableAdaptor(this, {updateValue()})
        this.guandonTableView.adapter = adaptor
        this.balanceText.text = "餘額：" + balance.toString() + "$"
        adaptor.notifyDataSetChanged()

    }

    fun updateValue() {
        totalCost = 0
        totalSelected = 0
        dishDict.clear()
        for(item in quantityDict){
            if(item.value != 0){
                val tmp = selectedFactoryArr.getJSONObject(item.key.toInt()).getString("dish_cost").toInt()
                val id = selectedFactoryArr.getJSONObject(item.key.toInt()).getString("dish_id")
                dishDict[id] = item.value
                totalSelected += item.value
                totalCost += tmp*item.value
            }
        }
        totalText.text = "$totalCost$"
        guandonButton.isEnabled = (totalCost >= 30 && totalSelected < 30)
    }

    fun sendOrder(view:View){
        var urltmp = ""
        var nametmp = ""
        urltmp = dsURL("make_self_order")
        for(item in dishDict){
            for(i in 0 until item.value){
                urltmp += "&dish_id[]=${item.key}"
            }
        }
        for(item in quantityDict){
            nametmp += "${selectedFactoryArr.getJSONObject(item.key.toInt()).getString("dish_name")}*${item.value}+"
        }
        nametmp.dropLast(1)
        ord1 = ord(nametmp,urltmp)
        selOrder1 = SelOrder("",nametmp,totalCost.toString())
        startActivity(Intent(view.context,GuanDonOrderActivity::class.java))
    }

    class TableAdaptor(context: Context, updValue: () -> Unit): BaseAdapter(){
        private val mContext: Context = context
        private val updateValue = updValue
        override fun getCount(): Int {
            return selectedFactoryArr.length()
        }

        override fun getItem(position: Int): Any {
            return selectedFactoryArr.getJSONObject(position).getString("dish_name")
        }

        override fun getItemId(position: Int): Long {
            return selectedFactoryArr.getJSONObject(position).getString("dish_cost").toLong()
        }
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val layout = layoutInflater.inflate(R.layout.guandon_list_cell, parent, false)
            val dishCost = selectedFactoryArr.getJSONObject(position).getString("dish_cost")
            val dishName = selectedFactoryArr.getJSONObject(position).getString("dish_name")
            val dishID = selectedFactoryArr.getJSONObject(position).getString("dish_id")
            val dishRemain = selectedFactoryArr.getJSONObject(position).getString("remaining")
            quantityDict[position.toString()] = 0
            layout.plus_button.setOnClickListener {
                var quantity = quantityDict[position.toString()]!!
                quantity += 1
                if(quantity==5 || quantity == dishRemain.toInt()){
                    layout.plus_button.isEnabled = false
                    layout.stepperDisplay.text = "${quantity}份"
                    quantityDict[position.toString()] = quantity
                }else {
                    layout.stepperDisplay.text = "${quantity}份"
                    quantityDict[position.toString()] = quantity
                }
                if(quantity>1){
                    layout.minus_button.isEnabled = true
                }
                updateValue()
            }
            layout.minus_button.setOnClickListener {
                var quantity = quantityDict[position.toString()]!!
                quantity -= 1
                if(quantity==0){
                    layout.minus_button.isEnabled = false
                    layout.stepperDisplay.text = "${quantity}份"
                    quantityDict[position.toString()] = quantity
                }else {
                    layout.stepperDisplay.text = "${quantity}份"
                    quantityDict[position.toString()] = quantity
                }
                if(quantity<5 && quantity< dishRemain.toInt()){
                    layout.plus_button.isEnabled = true
                }
                updateValue()
            }
            layout.titleText.text = dishName
            layout.detailTitleText.text = "$dishCost$, 剩${dishRemain}個"
            layout.stepperDisplay.text = "0份"
            layout.minus_button.isEnabled = false
            layout.guandonButton.isEnabled = false
            return layout
        }
    }
}
