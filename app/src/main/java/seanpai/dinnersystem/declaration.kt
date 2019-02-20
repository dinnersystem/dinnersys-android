package seanpai.dinnersystem

import org.json.*
import java.text.SimpleDateFormat
import java.util.*

//variable
var currentVersion = 201900003
var userInfo = JSONObject("{}")
var allMenuJson = JSONArray("[]")
var taiwanMenuJson = JSONArray("[]")
var aiJiaMenuJson = JSONArray("[]")
var cafetMenuJson = JSONArray("[]")
var guanDonMenuJson = JSONArray("[]")
var balance = 0
var selectedFactoryArr = JSONArray("[]")
var historyArr = JSONArray("[]")
var selOrder1 = SelOrder("","","")
var constUsername = ""
var constPassword = ""
var quantityDict = mutableMapOf<String,Int>()
var dishDict = mutableMapOf<String,Int>()
var ord1 = ord("","")
var dishNameArr: Array<String> = emptyArray()
var dishCostArr: Array<Int> = emptyArray()
//data structure
data class SelOrder(val id: String, val name: String, val cost: String)
data class ord(val name:String, val url: String)


var bonus = 0
//function
fun dsURL(str: String): String{
    return "https://dinnersystem.ddns.net/dinnersys_beta/backend/backend.php?cmd=$str"
}

fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
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

