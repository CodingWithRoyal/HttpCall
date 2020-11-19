package codes.royal.httpcall

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Variable to hold service class name
        val serviceClass = NetService::class.java

        // Initialize a new Intent instance
        val intent = Intent(applicationContext, serviceClass)

        val btn = findViewById<Button>(R.id.btn)
        val url = findViewById<EditText>(R.id.urlInput)
        val interval = findViewById<EditText>(R.id.intervalInput)

        ui(btn, url, interval, serviceClass, intent);
    }

    private fun ui(btn:Button, url:EditText, interval:EditText, serviceClass: Class<*>, intent:Intent) {
        if (isServiceRunning(serviceClass)) {
            Toast.makeText(this, "Service already running...", Toast.LENGTH_SHORT).show()
            //finish()

            btn.text = "Stop"
            url.visibility = View.GONE
            interval.visibility = View.GONE

            btn.setOnClickListener {
                stopService(intent)

                ui(btn, url, interval, serviceClass, intent) // update UI
            }
        } else {
            btn.text = "Start"
            url.visibility = View.VISIBLE
            interval.visibility = View.VISIBLE

            btn.setOnClickListener {
                if (isServiceRunning(serviceClass))
                    stopService(intent)

                if (url.text.toString() == "") {
                    Toast.makeText(this, "interval must not be empty", Toast.LENGTH_SHORT).show()
                } else if (interval.text.toString() == "") {
                    Toast.makeText(this, "interval must not be empty", Toast.LENGTH_SHORT).show()
                } else {
                    intent.putExtra("url", url.text.toString())
                    intent.putExtra("interval", interval.text.toString().toInt())
                    startService(intent)

                    ui(btn, url, interval, serviceClass, intent) // update UI
                }
            }
        }
    }

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
}