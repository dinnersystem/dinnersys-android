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
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.main_menu_cell.view.*

class MainMenuActivity : AppCompatActivity() {
    private val adaptor = TableAdaptor(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        this.tableView.adapter = adaptor
        this.balanceText.text = "餘額：" + balance.toString() + "$"
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
            val dishCost = selectedFactoryArr.getJSONObject(position).getString("dish_cost")
            val dishID = selectedFactoryArr.getJSONObject(position).getString("dish_id")
            val dishName = selectedFactoryArr.getJSONObject(position).getString("dish_name")
            layout.title.text = dishName
            layout.detailTitle.text = "$dishCost$"
            layout.proceedOrderButton.setOnClickListener {
                selOrder1 = SelOrder(dishID,dishName,dishCost)
                mContext.startActivity(Intent(mContext,MainOrderActivity::class.java))
            }
            val bestSeller = selectedFactoryArr.getJSONObject(position).getString("best_seller")
            val isBestSeller = bestSeller == "true"
            if(isBestSeller){
                layout.title.setTextColor(ContextCompat.getColor(mContext,R.color.special))
                layout.detailTitle.text = layout.detailTitle.text.toString() + "，人氣商品！"
                layout.detailTitle.setTextColor(ContextCompat.getColor(mContext,R.color.special))
            }else{
                layout.title.setTextColor(Color.BLACK)
                layout.detailTitle.setTextColor(Color.BLACK)
            }
            return layout
        }
    }
}
