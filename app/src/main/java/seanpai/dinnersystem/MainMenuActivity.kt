package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlinx.android.synthetic.main.main_menu_cell.view.*

class MainMenuActivity : AppCompatActivity() {
    private val adaptor = TableAdaptor(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        println("hello there")
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
            return layout
        }
    }
}
