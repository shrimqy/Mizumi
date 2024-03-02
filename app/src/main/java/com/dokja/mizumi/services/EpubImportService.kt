package com.dokja.mizumi.services

import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.dokja.mizumi.R
import com.dokja.mizumi.epub.epubParser
import com.dokja.mizumi.repository.AppFileResolver
import com.dokja.mizumi.repository.AppRepository
import com.dokja.mizumi.utils.NotificationsCenter
import com.dokja.mizumi.utils.asSequence
import com.dokja.mizumi.utils.removeProgressBar
import com.dokja.mizumi.utils.text
import com.dokja.mizumi.utils.title
import com.dokja.mizumi.utils.tryAsResponse
import dagger.hilt.android.AndroidEntryPoint
import epubImporter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.KProperty

@AndroidEntryPoint
class EpubImportService : Service() {
    @Inject
    lateinit var appRepository: AppRepository

    @Inject
    lateinit var notificationsCenter: NotificationsCenter

    @Inject
    lateinit var appFileResolver: AppFileResolver

    private class IntentData : Intent {
        var uri by Extra_Uri()

        constructor(intent: Intent) : super(intent)
        constructor(ctx: Context, uri: Uri) : super(ctx, EpubImportService::class.java) {
            this.uri = uri
        }
    }

    companion object {
        fun start(ctx: Context, uri: Uri) {
            if (!isRunning(ctx))
                ContextCompat.startForegroundService(ctx, IntentData(ctx, uri))
        }

        private fun isRunning(context: Context): Boolean =
            context.isServiceRunning(EpubImportService::class.java)
    }



    override fun onBind(intent: Intent?): IBinder? = null

    private lateinit var notificationBuilder: NotificationCompat.Builder
    private var job: Job? = null

    private val channelName by lazy { getString(R.string.notification_channel_name_import_epub) }
    private val channelId = "Import EPUB"
    private val notificationId = channelId.hashCode()


    override fun onCreate() {
        super.onCreate()
        notificationBuilder = notificationsCenter.showNotification(
            notificationId = notificationId,
            channelId = channelId,
            channelName = channelName,
        )
        startForeground(notificationId, notificationBuilder.build())
    }

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY
        val intentData = IntentData(intent)
        job = CoroutineScope(Dispatchers.IO).launch {
            tryAsResponse {

                notificationsCenter.modifyNotification(
                    notificationBuilder,
                    notificationId = notificationId
                ) {
                    title = getString(R.string.import_epub)
                    foregroundServiceBehavior = NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
                    setProgress(100, 0, true)
                }

                val inputStream = contentResolver.openInputStream(intentData.uri)
                if (inputStream == null) {
                    notificationsCenter.showNotification(
                        channelName = channelName,
                        channelId = channelId,
                        notificationId = "Import EPUB failure".hashCode()
                    ) {
                        text = getString(R.string.failed_get_file)
                        removeProgressBar()
                    }
                    return@tryAsResponse
                }

                val fileName = contentResolver.query(
                    intentData.uri,
                    arrayOf(OpenableColumns.DISPLAY_NAME),
                    null,
                    null,
                    null,
                    null
                ).asSequence().map { it.getString(0) }.last()

                Log.d("FileExplorer", "Selected FileName: $fileName")
                val epubBook = inputStream.use { epubParser(inputStream = it) }

                epubImporter(
                    storageFolderName = fileName,
                    appFileResolver = appFileResolver,
                    appRepository = appRepository,
                    epub = epubBook,
                    addToLibrary = true
                )
            }
            stopSelf(startId)
        }
        return START_NOT_STICKY
    }
}

fun Context.isServiceRunning(serviceClass: Class<*>): Boolean {
    val className = serviceClass.name
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    return manager.getRunningServices(Integer.MAX_VALUE)
        .any { className == it.service.className }
}

class Extra_Uri {
    operator fun getValue(thisRef: Intent, property: KProperty<*>) =
        thisRef.extras!!.get(property.name) as Uri

    operator fun setValue(thisRef: Intent, property: KProperty<*>, value: Uri) =
        thisRef.putExtra(property.name, value)
}


