package seanpai.dinnersystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ProgressBar
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_change_password.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.centerInParent

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var indicatorView: View
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        //indicator start
        indicatorView = View(this)
        indicatorView.setBackgroundResource(R.color.colorPrimaryDark)
        val viewParam = RelativeLayout.LayoutParams(-1, -1)
        viewParam.centerInParent()
        indicatorView.layoutParams = viewParam
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyle)
        progressBar.isIndeterminate = true
        val prams: RelativeLayout.LayoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        viewParam.centerInParent()
        progressBar.layoutParams = prams
        indicatorView.visibility = View.INVISIBLE
        progressBar.visibility = View.INVISIBLE
        layout.addView(indicatorView)
        layout.addView(progressBar)
        //indicator end
    }

    fun chgPW(view: View){
        //indicator
        indicatorView.visibility = View.VISIBLE
        indicatorView.bringToFront()
        progressBar.visibility = View.VISIBLE
        progressBar.bringToFront()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
        val old = oldPWText.text.toString()
        val new = newPWText.text.toString()
        val new2 = newPW2Text.text.toString()
        if (old == "" || new == "" || new2 == ""){
            //indicator
            indicatorView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert("請再試一次","請確定有填入所有輸入欄"){
                positiveButton("OK"){}
            }.show()
        }else if (old != constPassword){
            //indicator
            indicatorView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert("請再試一次","原密碼錯誤"){
                positiveButton("OK"){}
            }.show()
        }else if (new != new2){
            //indicator
            indicatorView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert("請再試一次","新密碼不吻合"){
                positiveButton("OK"){}
            }.show()
        }else if(new.contains(' ')){
            //indicator
            indicatorView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert("請再試一次","請勿輸入空白鍵"){
                positiveButton("OK"){}
            }.show()
        }else{
            val chgURL = dsURL("change_password&old_pswd=$old&new_pswd=$new")
            val chgRequest = StringRequest(chgURL, Response.Listener {
                if (it == "Invalid string."){
                    //indicator
                    indicatorView.visibility = View.INVISIBLE
                    progressBar.visibility = View.INVISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
                    alert("輸入內容僅限大小寫英數及底線!","輸入格式錯誤"){
                        positiveButton("OK"){}
                    }.show()
                }else if (it.contains("short")){
                    //indicator
                    indicatorView.visibility = View.INVISIBLE
                    progressBar.visibility = View.INVISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
                    alert("密碼長度需大於等於三字元!","輸入格式錯誤"){
                        positiveButton("OK"){}
                    }.show()
                }else if (it == ""){
                    //indicator
                    indicatorView.visibility = View.INVISIBLE
                    progressBar.visibility = View.INVISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
                    alert("請重新登入","您已經登出"){
                        positiveButton("OK"){}
                    }.show()
                }else{
                    //indicator
                    indicatorView.visibility = View.INVISIBLE
                    progressBar.visibility = View.INVISIBLE
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
                    alert("請重新登入","更改成功"){

                        positiveButton("OK"){
                            startActivity(Intent(this@ChangePasswordActivity,LoginActivity::class.java))
                        }
                    }.show()
                }
            }, Response.ErrorListener {
                //indicator
                indicatorView.visibility = View.INVISIBLE
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                    positiveButton("OK"){}
                }.show()
            })
            VolleySingleton.getInstance(this).addToRequestQueue(chgRequest)
        }



    }
}
