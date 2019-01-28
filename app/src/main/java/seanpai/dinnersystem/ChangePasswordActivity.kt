package seanpai.dinnersystem

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_change_password.*
import org.jetbrains.anko.alert

class ChangePasswordActivity : AppCompatActivity() {
    var queue: RequestQueue = Volley.newRequestQueue(this)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
    }

    override fun onStop() {
        super.onStop()
        queue.stop()
    }
    fun chgPW(view: View){
        val old = oldPWText.text.toString()
        val new = newPWText.text.toString()
        val new2 = newPW2Text.text.toString()
        if (old == "" || new == "" || new2 == ""){
            alert("請再試一次","請確定有填入所有輸入欄"){
                positiveButton("OK"){}
            }
        }else if (old != constPassword){
            alert("請再試一次","原密碼錯誤"){
                positiveButton("OK"){}
            }
        }else if (new != new2){
            alert("請再試一次","新密碼不吻合"){
                positiveButton("OK"){}
            }
        }else if(new.contains(' ')){
            alert("請再試一次","請勿輸入空白鍵"){
                positiveButton("OK"){}
            }
        }else{
            val chgURL = dsURL("change_password&old_pswd=$old&new_pswd=$new")
            val chgRequest = StringRequest(chgURL, Response.Listener {
                if (it == "Invalid string."){
                    alert("輸入內容僅限大小寫英數及底線!","輸入格式錯誤"){
                        positiveButton("OK"){}
                    }
                }else if (it.contains("short")){
                    alert("密碼長度需大於等於三字元!","輸入格式錯誤"){
                        positiveButton("OK"){}
                    }
                }else if (it == ""){
                    alert("請重新登入","您已經登出"){
                        positiveButton("OK"){}
                    }
                }else{
                    alert("請重新登入","更改成功"){
                        positiveButton("OK"){
                            startActivity(Intent(this@ChangePasswordActivity,LoginActivity::class.java))
                        }
                    }
                }
            }, Response.ErrorListener {
                alert ("請注意網路狀態，或通知開發人員!","不知名的錯誤"){
                    positiveButton("OK"){}
                }
            })
            queue.add(chgRequest)
        }



    }
}
