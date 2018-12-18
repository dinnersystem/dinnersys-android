package seanpai.dinnersystem

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.activity_stu_order_list.*

class StuOrderListActivity : AppCompatActivity() {
    val adaptor = TableAdaptor(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stu_order_list)
        orderList.adapter = adaptor
    }

    class TableAdaptor(context: Context): BaseAdapter(){
        val mContext:Context = context

        override fun getCount(): Int {
            return menuJson.length()
        }

        override fun getItem(position: Int): Any {
            return "HI"
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
        }
    }


}
