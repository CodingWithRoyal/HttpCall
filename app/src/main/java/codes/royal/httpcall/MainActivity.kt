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

        val btn = findViewById<Button>(R.id.btn)
        val url = findViewById<EditText>(R.id.urlInput)
        val interval = findViewById<EditText>(R.id.intervalInput)

        ui(btn, url, interval, NetService::class.java)
    }

    private fun ui(btn:Button, url:EditText, interval:EditText, serviceClass: Class<*>) {
        if (isServiceRunning(serviceClass)) {
            btn.text = "Stop"
            url.visibility = View.GONE
            interval.visibility = View.GONE

            btn.setOnClickListener({
                if (isServiceRunning(serviceClass))
                    NetService.stopService(this)

                ui(btn, url, interval, serviceClass) // update UI
            })
        } else {
            btn.text = "Start"
            url.visibility = View.VISIBLE
            interval.visibility = View.VISIBLE

            btn.setOnClickListener {
                if (isServiceRunning(serviceClass))
                    NetService.stopService(this)

                if (url.text.toString() == "") {
                    Toast.makeText(this, "interval must not be empty", Toast.LENGTH_SHORT).show()
                } else if (interval.text.toString() == "") {
                    Toast.makeText(this, "interval must not be empty", Toast.LENGTH_SHORT).show()
                } else {
                    NetService.startService(this, "Click to stop...", url.text.toString(), interval.text.toString().toLong()*1000)

                    ui(btn, url, interval, serviceClass) // update UI
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