package seanpai.dinnersystem

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen

class DinnerSystem: Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
    }
}