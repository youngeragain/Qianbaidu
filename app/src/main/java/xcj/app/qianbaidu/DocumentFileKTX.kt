package xcj.app.qianbaidu

import android.content.Context
import androidx.documentfile.provider.DocumentFile
import java.io.BufferedWriter

fun DocumentFile.creteFileInFolder(
    context: Context,
    mimeType: String,
    name: String,
    writer: BufferedWriter.() -> Unit
) {
    if (!isDirectory) {
        return
    }
    val documentFile = createFile(mimeType, name)
    if (documentFile == null) {
        return
    }
    context.contentResolver
        .openOutputStream(documentFile.uri)
        ?.bufferedWriter()
        ?.use(writer)
}