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
import seanpai.dinnersystem.databinding.ActivityMainMenuBinding
import seanpai.dinnersystem.databinding.MainMenuCellBinding

class MainMenuActivity : AppCompatActivity() {
    private val adaptor = TableAdaptor(this)
    private lateinit var activityBinding: ActivityMainMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)
        activityBinding.tableView.adapter = adaptor
        activityBinding.balanceText.text = "餘額：" + balance.toString() + "$"
    }


    class TableAdaptor(context: Context): BaseAdapter(){
        private val mContext: Context = context
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
            val layout = layoutInflater.inflate(R.layout.main_menu_cell, parent, false)
            val binding = MainMenuCellBinding.bind(layout)
            val dishCost = selectedFactoryArr.getJSONObject(position).getString("dish_cost")
            val dishID = selectedFactoryArr.getJSONObject(position).getString("dish_id")
            val dishName = selectedFactoryArr.getJSONObject(position).getString("dish_name")
            val factoryName = selectedFactoryArr.getJSONObject(position).getJSONObject("department").getJSONObject("factory").getString("name")
            binding.title.text = dishName
            binding.detailTitle.text = "$dishCost$"
            binding.proceedOrderButton.setOnClickListener {
                selOrder1 = SelOrder(dishID,dishName,dishCost)
                confirmData = ConfirmStruct(dishName,factoryName,dishCost)
                mContext.startActivity(Intent(mContext,MainOrderActivity::class.java))
            }
            val bestSeller = selectedFactoryArr.getJSONObject(position).getString("best_seller")
            val isBestSeller = bestSeller == "true"
            if(isBestSeller){
                binding.title.setTextColor(ContextCompat.getColor(mContext,R.color.special))
                binding.detailTitle.text = binding.detailTitle.text.toString() + "，人氣商品！"
                binding.detailTitle.setTextColor(ContextCompat.getColor(mContext,R.color.special))
            }else{
                binding.title.setTextColor(Color.BLACK)
                binding.detailTitle.setTextColor(Color.BLACK)
            }
            return layout
        }
    }
}
