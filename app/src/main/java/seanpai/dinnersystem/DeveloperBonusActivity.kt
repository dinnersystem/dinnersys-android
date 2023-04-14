package seanpai.dinnersystem

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.ImageRequest
import seanpai.dinnersystem.databinding.ActivityDeveloperBonusBinding

class DeveloperBonusActivity : AppCompatActivity() {
    private lateinit var activityBinding: ActivityDeveloperBonusBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityDeveloperBonusBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        val url = "$dinnersysURL/frontend/u_move_u_dead/dinnersys0.png"
        val imageRequest = ImageRequest(url,
            {
                activityBinding.imageView7.setImageBitmap(it)
            },
                                        0,0,ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, {

            })
        VolleySingleton.getInstance(this).addToRequestQueue(imageRequest)
    }


}
