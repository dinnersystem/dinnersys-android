package seanpai.dinnersystem

import android.graphics.Bitmap
import android.media.Image
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_developer_bonus.*

class DeveloperBonusActivity : AppCompatActivity() {
    var queue:RequestQueue? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer_bonus)
        queue = Volley.newRequestQueue(this)
        val url = "http://dinnersystem.ddns.net/dinnersys_beta/frontend/images/dinnersys0.png"
        val imageRequest = ImageRequest(url,
                                        Response.Listener {
                                            imageView7.setImageBitmap(it)
                                        },
                                        0,0,ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, Response.ErrorListener {

            })
        queue!!.add(imageRequest)
    }

    override fun onStop() {
        super.onStop()
        queue!!.stop()
    }
}
