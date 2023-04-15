package seanpai.dinnersystem

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import seanpai.dinnersystem.databinding.ActivityMainBarcodeBinding
import kotlin.math.roundToInt

class MainBarcodeActivity : AppCompatActivity() {

    private lateinit var activityBinding: ActivityMainBarcodeBinding

    private val barcode = posInfo.getString("card")
    private val name = posInfo.getString("name")
    private val balance = posInfo.getString("money")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activityBinding = ActivityMainBarcodeBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        activityBinding.cardInfo.text = "姓名: $name\n卡片餘額: $balance(餘額非即時)\n卡號: $barcode"
        window.attributes.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(barcode,BarcodeFormat.CODE_39,metrics.widthPixels*0.8.roundToInt(),180)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.createBitmap(bitMatrix)
        activityBinding.barcodeImage.setImageBitmap(bitmap)
        println("${activityBinding.barcodeImage.width}, ${activityBinding.barcodeImage.height}")
    }
}
