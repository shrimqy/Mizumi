package com.dokja.mizumi.epub

import android.app.ActivityManager
import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.provider.OpenableColumns
import android.util.Log
import androidx.core.content.ContextCompat
import com.dokja.mizumi.repository.AppFileResolver
import com.dokja.mizumi.repository.AppRepository
import com.dokja.mizumi.util.asSequence
import com.dokja.mizumi.util.tryAsResponse
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

    private val channelId = "Import EPUB"

    override fun onBind(intent: Intent?): IBinder? = null
    private var job: Job? = null

    override fun onDestroy() {
        job?.cancel()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY
        val intentData = IntentData(intent)
        job = CoroutineScope(Dispatchers.IO).launch {
            tryAsResponse {

                startForeground(1, Notification())

                val inputStream = contentResolver.openInputStream(intentData.uri) ?: return@tryAsResponse

                val fileName = contentResolver.query(
                    intentData.uri,
                    arrayOf(OpenableColumns.DISPLAY_NAME),
                    null,
                    null,
                    null,
                    null
                ).asSequence().map { it.getString(0) }.last()
                Log.d("FileExplorer", "Selected FileName: $fileName")
                val epubBook = inputStream.use { epubParser(inputStream = it)}

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