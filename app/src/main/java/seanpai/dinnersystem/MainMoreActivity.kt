package seanpai.dinnersystem

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_main_more.*
import org.jetbrains.anko.alert
import org.json.JSONObject

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
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://dinnersystem.ddns.net/")))
    }
    fun showBarcode(view: View){
        //indicator
        indicatorView.visibility = View.VISIBLE
        indicatorView.bringToFront()
        progressBar.visibility = View.VISIBLE
        progressBar.bringToFront()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator

        val cardRequest = StringRequest(dsURL("get_pos"),Response.Listener {
            if (isValidJson(it)) {
                posInfo = JSONObject(it)
                //indicator
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                startActivity(Intent(view.context,MainBarcodeActivity::class.java))
            } else {
                //indicator
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert("查詢餘額失敗，我們已經派出最精銳的猴子去修理這個問題，若長時間出現此問題請通知開發人員！", "請重新登入") {
                    positiveButton("OK") {
                        startActivity(Intent(this@MainMoreActivity, LoginActivity::class.java))
                    }
                }.show()
            }
        }, Response.ErrorListener {
            //indicator
            indicatorView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert("請注意網路狀態，或通知開發人員!", "不知名的錯誤") {
                positiveButton("OK") {}
            }.show()
        })
        VolleySingleton.getInstance(this).addToRequestQueue(cardRequest)
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
