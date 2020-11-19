package codes.royal.httpcall

import android.app.*
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

    lateinit var initIntent: Intent

    companion object {
        @JvmStatic
        fun start(context: Context) {
            ContextCompat.startForegroundService(context, Intent(context, NetService::class.java))
        }

        @JvmStatic
        fun stop(context: Context) {
            context.stopService(Intent(context, NetService::class.java))
        }
    }

    // Foreground service notification =========

    private val foregroundNotificationId: Int = (System.currentTimeMillis() % 10000).toInt()
    private val foregroundNotification by lazy {
        NotificationCompat.Builder(this, foregroundNotificationChannelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle("HttpCall")
                .addAction(R.drawable.ic_cancel, "Stop", stopAction())
                .setSound(null)
                .build()
    }
    private val foregroundNotificationChannelName by lazy {
        "HttpCall"
    }
    private val foregroundNotificationChannelDescription by lazy {
        "HttpCall"
    }
    private val foregroundNotificationChannelId by lazy {
        "ForegroundServiceSample.NotificationChannel".also {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                    if (getNotificationChannel(it) == null) {
                        createNotificationChannel(NotificationChannel(
                                it,
                                foregroundNotificationChannelName,
                                NotificationManager.IMPORTANCE_MIN
                        ).also {
                            it.description = foregroundNotificationChannelDescription
                            it.lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
                            it.vibrationPattern = null
                            it.setSound(null, null)
                            it.setShowBadge(false)
                        })
                    }
                }
            }
        }
    }

    fun stopAction(): PendingIntent? {
        //stopSelf()
        val intent = Intent(this, StopActivity::class.java)
        intent.putExtras(intent)
        return PendingIntent.getActivity(this, 0, intent, 0);
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null;
    }

    override fun onCreate() {
        super.onCreate()
    }

    fun makeReq(url: String, interval: Long) {
        val que = Volley.newRequestQueue(this)

        val request = StringRequest(Request.Method.GET, url, Response.Listener<String> { response ->

            //Log.i("rrLOG", response.toString());
            Log.i("rrLOG", "Done...");

        }, Response.ErrorListener { Log.e("rrLOG", "Failed!") })
        que.add(request)

        Handler(Looper.getMainLooper()).postDelayed({
            makeReq(url, interval)
        }, interval)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        initIntent = intent

        val url = intent!!.getStringExtra("url");
        val interval = intent.getIntExtra("interval", 30);

        val intervalLong: Long = interval.toLong() * 1000;

        if (url != null) {
            makeReq(url, intervalLong)
        } else {
            Log.e("rrLOG", "URL null");
        }

        startForeground(foregroundNotificationId, foregroundNotification)
        return START_NOT_STICKY
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}