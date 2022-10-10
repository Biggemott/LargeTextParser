package com.biggemot.largetextparser.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.biggemot.largetextparser.R
import com.biggemot.largetextparser.domain.ParseResult
import com.biggemot.largetextparser.domain.ParserRepo
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import javax.inject.Inject


class ParserRepoImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val parserApi: ParserApi,
) : ParserRepo {

    companion object {
        private const val LINE_CHAR_LIMIT = 1024L * 1024L
        private const val CLIPBOARD_STRINGS_SEPARATOR = "\n"
        private const val LOG_FILE_NAME = "results.log"
    }

    private val logFile: File by lazy {
        File(context.filesDir, LOG_FILE_NAME).also {
            if (!it.exists()) it.createNewFile()
        }
    }

    private val logScope = CoroutineScope(Dispatchers.IO)
    private var logChannel = newLogChannel()

    override fun getFileStream(url: String): Flow<ParseResult> {
        return flow {
            try {
                val response = parserApi.loadFile(url)
                Timber.d("response successful=${response.isSuccessful}")
                response.body()?.source()?.let { source ->
                    while (!source.exhausted()) {
                        emit(ParseResult.Data(source.readUtf8LineStrict(LINE_CHAR_LIMIT)))
                    }
                    emit(ParseResult.Finish)
                } ?: run { emit(ParseResult.Error) }
            } catch (e: IOException) {
                Timber.e(e, "exception")
                emit(ParseResult.Error)
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun copyToClipboard(strings: List<String>) {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(
            context.getString(R.string.clipboard_copy_label),
            strings.joinToString(CLIPBOARD_STRINGS_SEPARATOR)
        )
        clipboardManager.setPrimaryClip(clipData)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun appendLogLine(line: String) {
        logChannel.trySend(
            logScope.launch {
                try {
                    OutputStreamWriter(FileOutputStream(logFile, true), Charsets.UTF_8).apply {
                        appendLine(line)
                        close()
                    }
                } catch (e: IOException) {
                    Timber.e(e, "writeLog")
                }
            }
        )
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override fun clearLog() {
        logChannel.cancel()
        logChannel = newLogChannel().apply {
            trySend(
                logScope.launch {
                    try {
                        OutputStreamWriter(FileOutputStream(logFile, false), Charsets.UTF_8).close()
                    } catch (e: IOException) {
                        Timber.e(e, "clearLog")
                    }
                }
            )
        }
    }

    // using channel to control log calls order
    private fun newLogChannel(): Channel<Job> = Channel<Job>(capacity = Channel.UNLIMITED).apply {
        logScope.launch {
            consumeEach { it.join() }
        }
    }

}