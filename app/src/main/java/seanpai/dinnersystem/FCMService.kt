package seanpai.dinnersystem


import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        if(p0.notification != null){
            println("message in: ${p0.notification!!.body}")
        }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        println("new token: $p0")
    }



}
