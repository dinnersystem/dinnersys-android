package seanpai.dinnersystem

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

//variable
var currentVersion = 202000003
const val dinnersysURL = "https://dinnersystem.com/dinnersys_beta"
const val dsRequestURL = "https://dinnersystem.com/dinnersys_beta/backend/backend.php"
var userInfo = JSONObject("{}")
var posInfo = JSONObject("{}")
var schoolInfo = JSONArray("[]")
var allMenuJson = JSONArray("[]")
var taiwanMenuJson = JSONArray("[]")
var aiJiaMenuJson = JSONArray("[]")
var cafetMenuJson = JSONArray("[]")
var guanDonMenuJson = JSONArray("[]")
var splitMenuDict = mutableMapOf<String,JSONArray>()
var randomMenuArr = JSONArray("[]")
var balance = 0
var selectedFactoryArr = JSONArray("[]")
var historyArr = JSONArray("[]")
var revHistoryArr = JSONArray("[]")
var DMHistoryArr = mutableMapOf<String, JSONArray>()
var DMFactoryName = mutableListOf<String>()
var DMListArr: MutableList<Pair<String, JSONObject>> = mutableListOf()
var DMDishNameArr: MutableList<String> = mutableListOf()
var selOrder1 = SelOrder("","","")
var constUsername = ""
var constPassword = ""
var quantityDict = mutableMapOf<String,Int>()
var dishDict = mutableMapOf<String,Int>()
var dishIDtoIndex = IntArray(1000)
var ord1 = ord("","")
var dishNameArr: Array<String> = emptyArray()
var revDishNameArr: Array<String> = emptyArray()
var dishCostArr: Array<Int> = emptyArray()
var guanDonParam: Array<String> = emptyArray()
var DinnerSysInfo: MutableList<Pair<String, String>> = mutableListOf()
var foodArray: MutableList<FoodInfo> = mutableListOf()
var lighted = false
var ogBrightness = 0.toFloat()
var confirmContentList: MutableList<Pair<String, String>> = mutableListOf()
var confirmData = ConfirmStruct("","","")
var paymentTime: List<String> = mutableListOf()
var prepareTime: List<String> = mutableListOf()
var paymentTimeString = ""
var prepareTimeString = ""
var factory = JSONObject("{}")
var orderIDParam: MutableList<String> = mutableListOf()
var canOrder = false
var payBool: Boolean? = null
var selectedTime = ""

//data structure
data class SelOrder(val id: String, val name: String, val cost: String)
data class ord(val name:String, val url: String)
data class FoodInfo(val name: String, val qty: String, val cost: String)
data class ConfirmStruct(
    val dishName: String,
    val factoryName: String,
    val dishCost: String
)

var bonus = 0
//function
fun dsURL(str: String): String{
    return "$dinnersysURL/backend/backend.php?cmd=$str"
    //return "http://25.10.211.133/dinnersys_beta/backend/backend.php?cmd=$str"
}

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}

fun isInt(str: String): Boolean {
    try {
        val int = str.toInt()
    } catch (err: NumberFormatException) {
        return false
    }
    return true
}

fun isValidJson(str: String): Boolean{
    try {
        val json = JSONObject(str)
    }catch (err: JSONException){
        try {
            val arr = JSONArray(str)
        }catch(err: JSONException) {
            return false
        }
    }
    return true
}

fun addTime(date: Date, hour: Int, min: Int, sec: Int): Date{
    val calender = Calendar.getInstance()
    calender.time = date
    calender.add(Calendar.HOUR_OF_DAY, hour)
    calender.add(Calendar.MINUTE, min)
    calender.add(Calendar.SECOND, sec)
    return calender.time
}