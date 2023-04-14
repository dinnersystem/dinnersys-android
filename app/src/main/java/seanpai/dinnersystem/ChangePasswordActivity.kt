package seanpai.dinnersystem

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import seanpai.dinnersystem.databinding.ActivityChangePasswordBinding
import java.util.HashMap

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var progressBarHandler: ProgressBarHandler
    private lateinit var activityBinding: ActivityChangePasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)
        //indicator start
        progressBarHandler = ProgressBarHandler(this)
        //indicator end
    }

    fun chgPW(view: View){
        //indicator
        progressBarHandler.show()
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        //indicator
        val old = activityBinding.oldPWText.text.toString()
        val new = activityBinding.newPWText.text.toString()
        val new2 = activityBinding.newPW2Text.text.toString()
        if (old == "" || new == "" || new2 == ""){
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
//            alert("請再試一次","請確定有填入所有輸入欄"){
//                positiveButton("OK"){}
//            }.show()
            val builder = AlertDialog.Builder(this)
            builder.setTitle("請再試一次")
            builder.setMessage("請確定有填入所有輸入欄")
            builder.setPositiveButton("OK", null)
            builder.show()
        }else if (old != constPassword){
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
//            alert("請再試一次","原密碼錯誤"){
//                positiveButton("OK"){}
//            }.show()
            val builder = AlertDialog.Builder(this)
            builder.setTitle("請再試一次")
            builder.setMessage("原密碼錯誤")
            builder.setPositiveButton("OK", null)
            builder.show()
        }else if (new != new2){
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
//            alert("請再試一次","新密碼不吻合"){
//                positiveButton("OK"){}
//            }.show()
            val builder = AlertDialog.Builder(this)
            builder.setTitle("請再試一次")
            builder.setMessage("新密碼不吻合")
            builder.setPositiveButton("OK", null)
            builder.show()
        }else if(new.contains(' ')){
            //indicator
            progressBarHandler.hide()
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            //indicator
//            alert("請再試一次","請勿輸入空白鍵"){
//                positiveButton("OK"){}
//            }.show()
            val builder = AlertDialog.Builder(this)
            builder.setTitle("請再試一次")
            builder.setMessage("請勿輸入空白鍵")
            builder.setPositiveButton("OK", null)
            builder.show()
        }else{
            val chgRequest = object : StringRequest(Method.POST , dsRequestURL, Response.Listener {
                if (it == "Invalid string."){
                    //indicator
                    progressBarHandler.hide()
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
//                    alert("輸入內容僅限大小寫英數及底線!","輸入格式錯誤"){
//                        positiveButton("OK"){}
//                    }.show()
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("輸入格式錯誤")
                    builder.setMessage("輸入內容僅限大小寫英數及底線!")
                    builder.setPositiveButton("OK", null)
                    builder.show()
                }else if (it.contains("short")){
                    //indicator
                    progressBarHandler.hide()
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
//                    alert("密碼長度需大於等於三字元!","輸入格式錯誤"){
//                        positiveButton("OK"){}
//                    }.show()
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("輸入格式錯誤")
                    builder.setMessage("密碼長度需大於等於三字元!")
                    builder.setPositiveButton("OK", null)
                    builder.show()
                }else if (it == ""){
                    //indicator
                    progressBarHandler.hide()
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
//                    alert("請重新登入","您已經登出"){
//                        positiveButton("OK"){}
//                    }.show()
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("您已經登出")
                    builder.setMessage("請重新登入")
                    builder.setPositiveButton("OK", null)
                    builder.show()
                }else{
                    //indicator
                    progressBarHandler.hide()
                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    //indicator
//                    alert("請重新登入","更改成功"){
//
//                        positiveButton("OK"){
//                            startActivity(Intent(this@ChangePasswordActivity,LoginActivity::class.java))
//                        }
//                    }.show()
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("更改成功")
                    builder.setMessage("請重新登入")
                    builder.setPositiveButton("OK") { dialog, which ->
                        startActivity(
                            Intent(
                                this@ChangePasswordActivity,
                                LoginActivity::class.java
                            )
                        )
                    }
                    builder.show()
                }
            }, Response.ErrorListener {
                //indicator
                progressBarHandler.hide()
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                //indicator
//                alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
//                    positiveButton("OK"){}
//                }.show()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("不知名的錯誤")
                builder.setMessage("請注意網路狀態，或通知開發人員!")
                builder.setPositiveButton("OK", null)
                builder.show()
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
