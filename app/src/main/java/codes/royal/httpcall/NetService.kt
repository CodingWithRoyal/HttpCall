package codes.royal.httpcall

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class NetService : Service() {

    lateinit var handler: Handler;

    fun makeReq(url: String, interval: Long) {
        val que = Volley.newRequestQueue(this)

        val request = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->

            //Log.i("rrLOG", response.toString());
            Log.i("rrLOG", "Done...");

        }, Response.ErrorListener { Log.e("rrLOG", "Failed!") })
        que.add(request)

        handler.postDelayed({
            makeReq(url, interval)
        }, interval)
    }

    private val CHANNEL_ID = "ForegroundService Kotlin"
    companion object {
        fun startService(context: Context, message: String, url: String, interval: Long) {
            val startIntent = Intent(context, NetService::class.java)
            startIntent.putExtra("inputExtra", message)
            startIntent.putExtra("url", url)
            startIntent.putExtra("interval", interval)
            ContextCompat.startForegroundService(context, startIntent)
        }
        fun stopService(context: Context) {
            val stopIntent = Intent(context, NetService::class.java)
            context.stopService(stopIntent)
        }
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val url = intent!!.getStringExtra("url");
        val interval = intent.getLongExtra("interval", 30000);

        if (url != null) {
            handler = Handler(Looper.getMainLooper())
            makeReq(url, interval)
        } else {
            Log.e("rrLOG", "URL null");
        }

        //do heavy work on a background thread
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
                this,
                0, notificationIntent, 0
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("HttpCall")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)
        //stopSelf();
        return START_STICKY
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null);
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "HttpCall",
                    NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }
}