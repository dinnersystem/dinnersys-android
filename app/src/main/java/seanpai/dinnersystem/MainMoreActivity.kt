package seanpai.dinnersystem

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainMoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_more)

    }

    fun chgPass(view: View){
        startActivity(Intent(view.context,ChangePasswordActivity::class.java))
    }
    fun webVer(view: View){
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://dinnersystem.ddns.net/")))
    }
    fun devThank(view: View){
        bonus += 1
        if(bonus == 13){
            bonus = 0
            startActivity(Intent(view.context,DeveloperBonusActivity::class.java))
        }else{
            startActivity(Intent(view.context, DeveloperNormalActivity::class.java))
        }
    }
}
