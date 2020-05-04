package seanpai.dinnersystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import kotlinx.android.synthetic.main.activity_change_password.*
import org.jetbrains.anko.alert
import java.util.HashMap

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var progressBarHandler: ProgressBarHandler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        //indicator start
        progressBarHandler = ProgressBarHandler(this)
        //indicator end
    }

    fun chgPW(view: View){
        //indicator
        progressBarHandler.show()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
        val old = oldPWText.text.toString()
        val new = newPWText.text.toString()
        val new2 = newPW2Text.text.toString()
        if (old == "" || new == "" || new2 == ""){
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert("請再試一次","請確定有填入所有輸入欄"){
                positiveButton("OK"){}
            }.show()
        }else if (old != constPassword){
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert("請再試一次","原密碼錯誤"){
                positiveButton("OK"){}
            }.show()
        }else if (new != new2){
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert("請再試一次","新密碼不吻合"){
                positiveButton("OK"){}
            }.show()
        }else if(new.contains(' ')){
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
            alert("請再試一次","請勿輸入空白鍵"){
                positiveButton("OK"){}
            }.show()
        }else{
            val chgRequest = object : StringRequest(Method.POST , dsRequestURL, Response.Listener {
                if (it == "Invalid string."){
                    //indicator
                    progressBarHandler.hide()
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
                    alert("輸入內容僅限大小寫英數及底線!","輸入格式錯誤"){
                        positiveButton("OK"){}
                    }.show()
                }else if (it.contains("short")){
                    //indicator
                    progressBarHandler.hide()
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
                    alert("密碼長度需大於等於三字元!","輸入格式錯誤"){
                        positiveButton("OK"){}
                    }.show()
                }else if (it == ""){
                    //indicator
                    progressBarHandler.hide()
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
                    alert("請重新登入","您已經登出"){
                        positiveButton("OK"){}
                    }.show()
                }else{
                    //indicator
                    progressBarHandler.hide()
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
                progressBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
                alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                    positiveButton("OK"){}
                }.show()
            }){
                override fun getParams(): MutableMap<String, String> {
                    val postParam: MutableMap<String, String> = HashMap()
                    postParam["cmd"] = "change_password"
                    postParam["old_pswd"] = old
                    postParam["new_pswd"] = new
                    return postParam
                }
            }
            VolleySingleton.getInstance(this).addToRequestQueue(chgRequest)
        }



    }
}
