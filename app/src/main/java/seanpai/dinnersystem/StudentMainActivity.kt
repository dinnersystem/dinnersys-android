package seanpai.dinnersystem

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.jetbrains.anko.alert

class StudentMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)
        val balanceURL = dsURL("get_money")
        val balanceRequest = StringRequest(balanceURL, Response.Listener {
            balance = it.toInt()
        }, Response.ErrorListener { alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
            positiveButton("OK"){}
        }.show() })
        VolleySingleton.getInstance(this).addToRequestQueue(balanceRequest)
    }

    fun toOrder(view: View){
        startActivity(Intent(view.context,StuOrderListActivity::class.java))
    }

    fun toHis(view:View){
        startActivity(Intent(view.context,MainHistoryActivity::class.java))
    }
    fun toMore(view: View){
        startActivity(Intent(view.context,MainMoreActivity::class.java))
    }
}
