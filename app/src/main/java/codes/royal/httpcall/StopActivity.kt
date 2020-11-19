package codes.royal.httpcall

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class StopActivity: AppCompatActivity() {

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        // Loop through the running services
        for (service in activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                // If the service is running then return true
                return true
            }
        }
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stop)

        // Variable to hold service class name
        val serviceClass = NetService::class.java

        // Initialize a new Intent instance
        val intent = Intent(applicationContext, serviceClass)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.finishAffinity()
        } else{
            this.finish()
            System.exit(0)
        }

        if (isServiceRunning(serviceClass)) {
            Toast.makeText(this, "Stopping Service", Toast.LENGTH_SHORT).show()
            stopService(intent)
        }
    }

}