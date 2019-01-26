package seanpai.dinnersystem

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class StudentMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_main)
    }

    fun toOrder(view: View){
        startActivity(Intent(view.context,StuOrderListActivity::class.java))
    }

    fun toHis(view:View){

    }
    fun toMore(view: View){

    }
}
