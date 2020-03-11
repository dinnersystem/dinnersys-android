package seanpai.dinnersystem

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main_more.*

class MainMoreActivity : AppCompatActivity() {
    private lateinit var indicatorView : View
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_more)
        //indicator start
        indicatorView = View(this)
        indicatorView.setBackgroundResource(R.color.colorPrimaryDark)
        val viewParam = LinearLayout.LayoutParams(-1,-1)
        viewParam.gravity = Gravity.CENTER
        indicatorView.layoutParams = viewParam
        progressBar = ProgressBar(this,null, android.R.attr.progressBarStyle)
        progressBar.isIndeterminate = true
        val prams: LinearLayout.LayoutParams = LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        prams.gravity = Gravity.CENTER
        progressBar.layoutParams = prams
        indicatorView.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        layout.addView(indicatorView)
        layout.addView(progressBar)
        //indicator end
    }

    fun chgPass(view: View){
        startActivity(Intent(view.context,ChangePasswordActivity::class.java))
    }
    fun webVer(view: View){
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(dinnersysURL)))
    }
    fun devThank(view: View){
        bonus += 1
        if(bonus == 13){
            bonus = 0
            startActivity(Intent(view.context,DeveloperBonusActivity::class.java))
        }else{
            startActivity(Intent(view.context, AppInfoActivity::class.java))
        }
    }
    fun foodPolicies(view: View){
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("$dinnersysURL/frontend/FoodPolicies.pdf")))
    }
}
