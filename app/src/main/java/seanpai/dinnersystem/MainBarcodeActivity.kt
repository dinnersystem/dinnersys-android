package seanpai.dinnersystem

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_main_barcode.*
import kotlin.math.roundToInt

class MainBarcodeActivity : AppCompatActivity() {
    private val barcode = posInfo.getString("card")
    private val name = posInfo.getString("name")
    private val balance = posInfo.getString("money")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_barcode)
        cardInfo.text = "姓名: $name\n卡片餘額: $balance(餘額非即時)\n卡號: $barcode"
        window.attributes.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(barcode,BarcodeFormat.CODE_39,metrics.widthPixels*0.8.roundToInt(),180)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.createBitmap(bitMatrix)
        barcodeImage.setImageBitmap(bitmap)
        println("${barcodeImage.width}, ${barcodeImage.height}")
    }
}
