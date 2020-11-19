package codes.royal.httpcall

import android.app.ActivityManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ServiceBroadcast: BroadcastReceiver() {

    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Loop through the running services
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                // If the service is running then return true
                return true
            }
        }
        return false
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (isServiceRunning(context, NetService::class.java)) {
            val serviceIntent = Intent(context, NetService::class.java)
            context.stopService(serviceIntent)
        }
    }

}
