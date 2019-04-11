package seanpai.dinnersystem

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import kotlinx.android.synthetic.main.activity_main_barcode.*

class MainBarcodeActivity : AppCompatActivity() {
    private val barcode = posInfo.getString("card")
    private val name = posInfo.getString("name")
    private val balance = posInfo.getString("money")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_barcode)
        cardInfo.text = "姓名: $name\n卡片餘額: $balance(餘額非即時)\n卡號: $barcode"
        val multiFormatWriter = MultiFormatWriter()
        val bitMatrix = multiFormatWriter.encode(barcode,BarcodeFormat.CODE_39,300,30)
        val barcodeEncoder = BarcodeEncoder()
        val bitmap = barcodeEncoder.createBitmap(bitMatrix)
        barcodeImage.setImageBitmap(bitmap)
    }
}
