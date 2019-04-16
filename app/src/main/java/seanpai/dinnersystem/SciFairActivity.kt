package seanpai.dinnersystem

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_sci_fair.*

class SciFairActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sci_fair)
    }

    fun go(view: View){
        dsIP = ipText.text.toString()
        startActivity(Intent(view.context,LoginActivity::class.java))
    }
}
