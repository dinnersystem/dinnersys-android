package seanpai.dinnersystem

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.pm.PackageInfoCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.oss.licenses.OssLicensesActivity
import kotlinx.android.synthetic.main.activity_app_info.*

class AppInfoActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_info)
        val pkgManager = this.packageManager
        val info = pkgManager.getPackageInfo(this.packageName, 0)
        val appVersion = "${info.versionName} (${PackageInfoCompat.getLongVersionCode(info)})"
        DinnerSysInfo.clear()
        DinnerSysInfo.add(Pair("程式名稱","板橋高中午餐系統"))
        DinnerSysInfo.add(Pair("程式版本",appVersion))
        DinnerSysInfo.add(Pair("主要開發者(iOS&Android App)","白翔云"))
        DinnerSysInfo.add(Pair("主要開發者(網頁, 後端, 資料庫)","吳邦寧"))
        DinnerSysInfo.add(Pair("後續維護","板橋高中資訊社"))
        DinnerSysInfo.add(Pair("如有任何問題，歡迎來信\ndinnersys@gmail.com",""))

        //DinnerSysInfo.add(Pair("",""))

        val layoutManager = LinearLayoutManager(this)
        infoList.layoutManager = layoutManager
        val dividerItemDecoration = DividerItemDecoration(infoList.context,layoutManager.orientation)
        infoList.addItemDecoration(dividerItemDecoration)

        val adapter = InfoAdapter(this)
        infoList.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    class InfoAdapter(private var context: Context):
        RecyclerView.Adapter<InfoAdapter.ViewHolder>() {


        class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
            lateinit var titleText: TextView
            lateinit var detailText: TextView
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val cell = LayoutInflater.from(context).inflate(R.layout.simple_left_title,parent,false)
            val viewHolder = ViewHolder(cell)
            viewHolder.detailText = cell.findViewById(R.id.detailText)
            viewHolder.titleText = cell.findViewById(R.id.leftTitle)
            return viewHolder
        }

        override fun getItemCount(): Int {

            return DinnerSysInfo.count()
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val info = DinnerSysInfo[position]
            holder.titleText.text = info.first
            holder.detailText.text = info.second
        }
    }
}
