package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import seanpai.dinnersystem.databinding.ActivityGuandonOrderListBinding
import seanpai.dinnersystem.databinding.GuandonListCellBinding

class GuandonOrderListActivity : AppCompatActivity() {
    var totalCost = 0
    var totalSelected = 0
    var noodleID = mutableListOf<String>()
    var noodleCount = 0
    val lowCost = selectedFactoryArr.getJSONObject(0).getJSONObject("department").getJSONObject("factory").getString("minimum").toInt()

    private lateinit var activityBinding: ActivityGuandonOrderListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityGuandonOrderListBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)
        val adaptor = TableAdaptor(this, {updateValue()})
        activityBinding.guandonTableView.adapter = adaptor
        activityBinding.balanceText.text = "餘額：" + balance.toString() + "$"
        dishIDtoIndex = IntArray(1000)
        for(i in 0 until selectedFactoryArr.length()){
            quantityDict[selectedFactoryArr.getJSONObject(i).getString("dish_id")] = 0
            if(selectedFactoryArr.getJSONObject(i).getJSONObject("department").getString("name") == "麵類"){
                noodleID.add(selectedFactoryArr.getJSONObject(i).getString("dish_id"))
            }
        }
        println(quantityDict)
        adaptor.notifyDataSetChanged()
    }

    fun updateValue() {
        totalCost = 0
        totalSelected = 0
        noodleCount = 0
        dishDict.clear()
        for(item in quantityDict){
            if(item.value != 0){
                val tmp = selectedFactoryArr.getJSONObject(dishIDtoIndex[item.key.toInt()]).getString("dish_cost").toInt()
                val id = selectedFactoryArr.getJSONObject(dishIDtoIndex[item.key.toInt()]).getString("dish_id")
                dishDict[id] = item.value
                totalSelected += item.value
                totalCost += tmp*item.value

                if(selectedFactoryArr.getJSONObject(dishIDtoIndex[item.key.toInt()]).getJSONObject("department").getString("name") == "麵類"){
                    noodleCount += item.value
                }
            }
        }
        activityBinding.totalText.text = "$totalCost$"
        activityBinding.guandonButton.isEnabled = (totalCost >= lowCost && totalSelected <= 20 && noodleCount < 2)
    }

    fun sendOrder(view:View){
        var urltmp = ""
        var nametmp = ""
        guanDonParam = emptyArray()
        urltmp = dsURL("make_self_order")
        for(item in dishDict){
            for(i in 0 until item.value){
                urltmp += "&dish_id[]=${item.key}"
                guanDonParam += item.key
            }
        }
        foodArray.clear()
        for(item in quantityDict){
            if (item.value != 0) {
                nametmp += "${selectedFactoryArr.getJSONObject(dishIDtoIndex[item.key.toInt()]).getString("dish_name")}*${item.value}+"
                foodArray.add(FoodInfo(selectedFactoryArr.getJSONObject(dishIDtoIndex[item.key.toInt()]).getString("dish_name"),"x" + item.value.toString(),(selectedFactoryArr.getJSONObject(dishIDtoIndex[item.key.toInt()]).getString("dish_cost").toInt()*item.value).toString()))
            }
        }
        foodArray.add(FoodInfo("小計","x" + totalSelected.toString(),totalCost.toString()))
        nametmp = nametmp.dropLast(1)
        ord1 = ord(nametmp,urltmp)
        val confirmDishName = "自訂餐點(${totalSelected}樣)"
        confirmData = ConfirmStruct(confirmDishName,selectedFactoryArr.getJSONObject(0).getJSONObject("department").getJSONObject("factory").getString("name"),totalCost.toString())
        selOrder1 = SelOrder("",nametmp,totalCost.toString())
        startActivity(Intent(view.context,GuanDonOrderActivity::class.java))
    }

    class TableAdaptor(context: Context, updValue: () -> Unit): BaseAdapter(){
        private val mContext: Context = context
        private val page = context as GuandonOrderListActivity
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
            val binding = GuandonListCellBinding.bind(layout)
            val dishCost = selectedFactoryArr.getJSONObject(position).getString("dish_cost")
            val dishName = selectedFactoryArr.getJSONObject(position).getString("dish_name")
            val dishID = selectedFactoryArr.getJSONObject(position).getString("dish_id")
            var dishRemain = selectedFactoryArr.getJSONObject(position).getString("remaining")
			
			/* ---modified by lawrence--- */
			val factory = selectedFactoryArr.getJSONObject(position).getJSONObject("department").getJSONObject("factory")
            var factoryLimit = false
            if(factory.getString("daily_produce") != "-1")
                factoryLimit = (factory.getString("daily_produce").toInt() < factory.getString("remaining").toInt())
            if(factoryLimit) dishRemain = "0"
			/* ------------------------- */
			
            dishIDtoIndex[dishID.toInt()] = position
            binding.plusButton.setOnClickListener {
                var quantity = quantityDict[dishID]!!
                quantity += 1
                if(quantity==5 || quantity == dishRemain.toInt()){
                    binding.plusButton.isEnabled = false
                    binding.stepperDisplay.text = "${quantity}份"
                    quantityDict[dishID] = quantity
                }else {
                    binding.stepperDisplay.text = "${quantity}份"
                    quantityDict[dishID] = quantity
                }
                if (quantity >= 1) {
                    binding.minusButton.isEnabled = true
                }
                updateValue()
            }
            binding.minusButton.setOnClickListener {
                var quantity = quantityDict[dishID]!!
                quantity -= 1
                if(quantity==0){
                    binding.minusButton.isEnabled = false
                    binding.minusButton.text = "${quantity}份"
                    quantityDict[dishID] = quantity
                }else {
                    binding.minusButton.text = "${quantity}份"
                    quantityDict[dishID] = quantity
                }
                if(quantity<5 && quantity< dishRemain.toInt()){
                    binding.minusButton.isEnabled = true
                }
                updateValue()
            }
            binding.titleText.text = dishName
            binding.detailTitleText.text = "$dishCost$, 剩${dishRemain}個"
            val bestSeller = selectedFactoryArr.getJSONObject(position).getString("best_seller")
            val isBestSeller = bestSeller == "true"
            if(isBestSeller){
                binding.titleText.setTextColor(ContextCompat.getColor(mContext,R.color.special))
                binding.detailTitleText.text = binding.detailTitleText.text.toString() + "，人氣商品！"
                binding.detailTitleText.setTextColor(ContextCompat.getColor(mContext,R.color.special))
            }else{
                binding.titleText.setTextColor(Color.BLACK)
                binding.detailTitleText.setTextColor(Color.BLACK)
            }
            binding.stepperDisplay.text = "${quantityDict[dishID]!!}份"
            binding.plusButton.isEnabled = dishRemain.toInt() != 0 && quantityDict[dishID]!! < 5
            binding.minusButton.isEnabled = quantityDict[dishID]!! > 0
            return layout
        }
    }
}
