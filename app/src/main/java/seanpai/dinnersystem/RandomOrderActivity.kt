package seanpai.dinnersystem

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import seanpai.dinnersystem.databinding.ActivityRandomOrderBinding
import kotlin.math.round
import kotlin.random.Random

class RandomOrderActivity : AppCompatActivity() {

    private var randomming = false
    private var totalDishCount = 0
    private var totalPossibilities = 0.toDouble()
    private var taskHandler = Handler()
    private var currentRandom = 0
    var randomUpper = 0

    private lateinit var activityBinding: ActivityRandomOrderBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityRandomOrderBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)

        randomming = false
        totalDishCount = randomMenuArr.length()
        totalPossibilities = 1.0/totalDishCount.toDouble()
        totalPossibilities = round(totalPossibilities*10000)/100
        activityBinding.infoText.text = "今日共${totalDishCount}，每個餐點中獎機率平均，各為$totalPossibilities%。"
    }

    fun startRandom(view: View){
        if(!randomming){
            randomming = true
            randomUpper = Random.nextInt(18,27)
            currentRandom = 0
            taskHandler.post(object : Runnable{
                override fun run() {
                    taskHandler.postDelayed(this, 200)
                    roulette()
                }
            })
        }
    }
    
    private fun roulette(){
        if(randomUpper < currentRandom){
            taskHandler.removeCallbacksAndMessages(null)
            randomming = false
            val randomIndex = Random.nextInt(0,totalDishCount-1)
            val item = randomMenuArr.getJSONObject(randomIndex)
            activityBinding.nameText.text = item.getString("dish_name")
            val dishName = item.getString("dish_name")
            val dishID = item.getString("dish_id")
            val dishCost = item.getString("dish_cost")
            val factoryName = item.getJSONObject("department").getJSONObject("factory").getString("name")
            Handler().postDelayed({
                selOrder1 = SelOrder(dishID,dishName,dishCost)
                confirmData = ConfirmStruct(dishName,factoryName,dishCost)
                startActivity(Intent(this,MainOrderActivity::class.java))
            }, 2000)
        }else{
            val randomIndex = Random.nextInt(0,totalDishCount-1)
            val item = randomMenuArr.getJSONObject(randomIndex)
            activityBinding.nameText.text = item.getString("dish_name")
            currentRandom += 1
        }
    }
}
