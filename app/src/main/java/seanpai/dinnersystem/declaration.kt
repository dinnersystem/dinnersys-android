package seanpai.dinnersystem

import org.json.*

//variable
var userInfo = JSONObject("{}")
var allMenuJson = JSONArray("[]")
var taiwanMenuJson = JSONArray("[]")
var aiJiaMenuJson = JSONArray("[]")
var cafetMenuJson = JSONArray("[]")
var guanDonMenuJson = JSONArray("[]")
var balance = 0
var selectedFactoryArr = JSONArray("[]")
var selOrder1 = SelOrder("","","")
var constUsername = ""
var constPassword = ""
var quantityDict = mutableMapOf<String,Int>()
var dishDict = mutableMapOf<String,Int>()
var ord1 = ord("","")
//data structure
data class SelOrder(val id: String, val name: String, val cost: String)
data class ord(val name:String, val url: String)


var bonus = 0
//function
fun dsURL(str: String): String{
    return "https://dinnersystem.ddns.net/dinnersys_beta/backend/backend.php?cmd=$str"
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

