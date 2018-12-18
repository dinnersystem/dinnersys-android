package seanpai.dinnersystem

import org.json.*

//variable
var userInfo = JSONObject("{}")
var menuJson = JSONArray("[]")

//function
fun dsURL(str: String): String{
    return "http://dinnersystem.ddns.net/dinnersys_beta/backend/backend.php?cmd=$str"
}



fun isValidJson(str: String): Boolean{
    try {
        val json = JSONObject(str)
    }catch (err: JSONException){
        try {
            val arr = JSONArray(str)
        }catch (aerr: JSONException){
            return false
        }
    }
    return true
}