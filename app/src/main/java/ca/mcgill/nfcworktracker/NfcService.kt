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
import android.os.Build
import android.os.IBinder
import android.util.Log
import java.util.*

class NfcService : Service() {

    private val selfStopReceiver = NotificationActionBroadcastReceiver()

    companion object {
        const val SERVICE_NOTIFICATION_CHANNEL_ID = "service-notification-channel"
        const val SERVICE_NOTIFICATION_CHANNEL_NAME = "Tracking Active"
        const val ACTION_STOP_SELF = "ca.mcgill.nfcworktracker.action_stop_self"
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val tag: Tag?
        val messages: Array<NdefMessage>?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
            messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, NdefMessage::class.java)
        } else {
            @Suppress("DEPRECATION") //checked SDK_INT above
            tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            @Suppress("DEPRECATION") //checked SDK_INT above
            @Suppress("UNCHECKED_CAST") //extra should only contain ndef messages
            messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) as Array<NdefMessage>?
        }
        val relevantRecord = if (messages == null) null else getRelevantRecord(messages)
        if (tag == null || messages == null || relevantRecord == null) {
            throw java.lang.RuntimeException("${this::class.qualifiedName} must be started with nfc tag information!")
        }

        //print content of payload in record with relevant mime type
        Log.d("nfc", "started service with tag \"${String(relevantRecord.payload)}\"")

        //start service
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(NotificationChannel(
            SERVICE_NOTIFICATION_CHANNEL_ID, SERVICE_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT))

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

        return START_NOT_STICKY
    }

    private inner class NotificationActionBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(ACTION_STOP_SELF)) {
                this@NfcService.stopSelf()
            }
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