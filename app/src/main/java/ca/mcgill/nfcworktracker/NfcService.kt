package ca.mcgill.nfcworktracker

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.IBinder
import android.util.Log
import ca.mcgill.nfcworktracker.history.HistoryDataPoint
import ca.mcgill.nfcworktracker.history.HistoryDatabaseHelper
import java.io.IOException
import java.time.Instant
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class NfcService : Service() {

    private val selfStopReceiver = NotificationActionBroadcastReceiver()
    private val tagPresenceCheckScheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1)
    private var tag: Tag? = null

    private val checkTagStillPresent = Runnable {
        assert(tag != null)
        if (isTagStillPresent()) {
            Log.d("nfc", "tag still there: $tag")
        } else {
            PendingIntent.getBroadcast(this, 0, Intent(ACTION_STOP_SELF), PendingIntent.FLAG_IMMUTABLE).send()
        }
    }

    private lateinit var startTime: Instant

    companion object {
        const val SERVICE_NOTIFICATION_CHANNEL_ID = "service-notification-channel"
        const val SERVICE_NOTIFICATION_CHANNEL_NAME = "Tracking Active"
        const val ACTION_STOP_SELF = "ca.mcgill.nfcworktracker.action_stop_self"

        private var instance: NfcService? = null
            set(value) {
                field = value
                Observer.notifyFromSetter()
            }
        private val hasInstanceRunning: Boolean
            get() {
                return instance != null
            }
        val startTimeOfInstance: Instant?
            get() {
                return instance?.startTime
            }
    }

    object Observer {
        private val hasInstanceRunningChangeListeners = ArrayList<Listener>()

        fun addChangeListener(callImmediately: Boolean = false, listener: Listener): Listener {
            hasInstanceRunningChangeListeners.add(listener)
            if (callImmediately) {
                listener.callback(hasInstanceRunning)
            }
            return listener
        }

        fun removeChangeListener(listener: Listener): Boolean {
            return hasInstanceRunningChangeListeners.remove(listener)
        }

        internal fun notifyFromSetter() {
            hasInstanceRunningChangeListeners.forEach {
                it.callback(hasInstanceRunning)
            }
        }

        interface Listener {
            fun callback(newStatus: Boolean)

            fun remove(): Boolean {
                return removeChangeListener(this@Listener)
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (instance != null) {
            Log.w("nfc", "duplicate start of service was stopped")
            return START_NOT_STICKY
        }

        val messages: Array<NdefMessage>?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, NdefMessage::class.java)
        } else {
            @Suppress("DEPRECATION") //checked SDK_INT above
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            @Suppress("DEPRECATION") //checked SDK_INT above
            messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                ?.filterIsInstance<NdefMessage>()?.toTypedArray()
        }
        val relevantRecord = if (messages == null) null else getRelevantRecord(messages)
        if (tag == null || messages == null || relevantRecord == null) {
            throw java.lang.RuntimeException("${this::class.qualifiedName} must be started with nfc tag information!")
        }

        //print content of payload in record with relevant mime type
        Log.i("nfc", "started service with tag \"${String(relevantRecord.payload)}\"")

        //start service
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(NotificationChannel(
            SERVICE_NOTIFICATION_CHANNEL_ID, SERVICE_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW))

        val stopIntent = Intent(ACTION_STOP_SELF)
        val stopAction = Notification.Action.Builder(
            Icon.createWithResource(this, R.drawable.ic_stop_circle),
            "Stop Tracker",
            PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)
        ).build()
        val nf: Notification = Notification.Builder(this, SERVICE_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_nfc)
            .setContentTitle("Tracking active")
            .setContentText("Your session is being recorded.")
            .addAction(stopAction)
            .build()
        startForeground(1, nf)

        startTracking()

        instance = this
        return START_NOT_STICKY
    }

    private fun startTracking() {
        startTime = Instant.now()
        tagPresenceCheckScheduler.scheduleAtFixedRate(checkTagStillPresent, 0, 1, TimeUnit.SECONDS)
    }

    private fun stopTracking() {
        val databaseHelper = HistoryDatabaseHelper(application as MyApplication)
        databaseHelper.insertIntoDatabase(HistoryDataPoint(
            startTime,
            Instant.now()
        ))
    }

    private inner class NotificationActionBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(ACTION_STOP_SELF)) {
                tagPresenceCheckScheduler.shutdownNow()
                stopTracking()
                this@NfcService.stopSelf()
                instance = null
                Log.i("nfc", "service stopped")
            }
        }
    }

    private fun isTagStillPresent(): Boolean {
        return try {
            val ndefTag = Ndef.get(tag)
            ndefTag.connect()
            val connected = ndefTag.isConnected
            ndefTag.close()
            connected
        } catch (ex: IOException) {
            Log.d("nfc", "nfc check failed")
            false
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(selfStopReceiver, IntentFilter(ACTION_STOP_SELF))
    }

    override fun onDestroy() {
        unregisterReceiver(selfStopReceiver)
        super.onDestroy()
    }

    private fun getRelevantRecord(messages: Array<NdefMessage>): NdefRecord? {
        val relevantRecords = LinkedList<NdefRecord>()
        for (m in messages) {
            relevantRecords.addAll(m.records.filter { it.toMimeType().equals(getString(R.string.tag_mime), true) })
        }
        if (relevantRecords.size == 0) {
            return null
        }
        if (relevantRecords.size != 1) {
            Log.w("nfc", "More than one relevant NdefRecord is present, choosing first.")
        }
        return relevantRecords[0]
    }
}
