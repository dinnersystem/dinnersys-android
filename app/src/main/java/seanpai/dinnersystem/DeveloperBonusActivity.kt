package seanpai.dinnersystem

import android.graphics.Bitmap
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_developer_bonus.*

class DeveloperBonusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_developer_bonus)
        val url = "$dinnersysURL/frontend/u_move_u_dead/dinnersys0.png"
        val imageRequest = ImageRequest(url,
                                        Response.Listener {
                                            imageView7.setImageBitmap(it)
                                        },
                                        0,0,ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, Response.ErrorListener {

            })
        VolleySingleton.getInstance(this).addToRequestQueue(imageRequest)
    }


}
