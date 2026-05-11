

package com.mwinensoaa.promptify.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mwinensoaa.promptify.R
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFParagraph
import org.apache.poi.xwpf.usermodel.XWPFTable

private const val TABLE_START = "[TABLE_START]"
private const val TABLE_END = "[TABLE_END]"

@Composable
fun HomeScreen(
    onTextExtracted: (String) -> Unit
) {

    var selectedFileName by remember {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var showContent by remember {
        mutableStateOf(false)
    }

    var showPasteDialog by remember {
        mutableStateOf(false)
    }

    var pastedText by remember {
        mutableStateOf(TextFieldValue(""))
    }

    val context = LocalContext.current

    val clipboardManager =
        context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager

    LaunchedEffect(Unit) {
        showContent = true
    }

    BackHandler {
        (context as Activity).finish()
    }

    /*
     * FILE PICKER
     */

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->

        uri?.let {

            selectedFileName =
                getFileName(context, it)

            isLoading = true

            extractTextFromFile(
                context = context,
                uri = it
            ) { extractedText ->

                isLoading = false

                onTextExtracted(extractedText)
            }
        }
    }

    /*
     * MAIN SCREEN
     */

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF020617),
                        Color(0xFF0F172A),
                        Color(0xFF111827)
                    )
                )
            )
    ) {

        /*
         * BACKGROUND GLOW
         */

        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-60).dp, y = (-20).dp)
                .blur(120.dp)
                .background(
                    Color(0xFF2563EB).copy(alpha = 0.25f)
                )
        )

        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = 220.dp, y = 700.dp)
                .blur(120.dp)
                .background(
                    Color(0xFF7C3AED).copy(alpha = 0.20f)
                )
        )

        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(
                        rememberScrollState()
                    )
                    .padding(horizontal = 24.dp),

                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(70.dp))

                /*
                 * LOGO
                 */

                Surface(
                    shape = CircleShape,

                    color =
                        Color.White.copy(alpha = 0.08f),

                    tonalElevation = 8.dp,

                    modifier = Modifier.size(120.dp)
                ) {

                    Box(
                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.MenuBook,

                            contentDescription = null,

                            tint = Color.White,

                            modifier =
                                Modifier.size(58.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text =
                        stringResource(R.string.app_name),

                    style =
                        MaterialTheme.typography.headlineMedium,

                    fontWeight =
                        FontWeight.Bold,

                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text =
                        "Import documents or paste copied text",

                    style =
                        MaterialTheme.typography.bodyLarge,

                    color =
                        Color.White.copy(alpha = 0.7f),

                    lineHeight = 28.sp,

                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(60.dp))

                /*
                 * MAIN CARD
                 */

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(32.dp),

                    colors = cardColors(
                        containerColor =
                            Color.White.copy(alpha = 0.08f)
                    ),

                    elevation =
                        CardDefaults.cardElevation(
                            defaultElevation = 10.dp
                        )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        Surface(
                            shape = CircleShape,

                            color =
                                Color(0xFF2563EB)
                                    .copy(alpha = 0.15f),

                            modifier =
                                Modifier.size(72.dp)
                        ) {

                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Description,

                                    contentDescription = null,

                                    tint =
                                        Color(0xFF60A5FA),

                                    modifier =
                                        Modifier.size(34.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        Text(
                            text = "Import Script",

                            style =
                                MaterialTheme.typography.titleLarge,

                            fontWeight =
                                FontWeight.SemiBold,

                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text =
                                "PDF, DOC, DOCX, TXT or pasted text",

                            style =
                                MaterialTheme.typography.bodyMedium,

                            color =
                                Color.White.copy(alpha = 0.7f),

                            textAlign = TextAlign.Center
                        )

                        /*
                         * FILE NAME
                         */

                        AnimatedVisibility(
                            visible =
                                selectedFileName.isNotEmpty()
                        ) {

                            Column {

                                Spacer(
                                    modifier =
                                        Modifier.height(24.dp)
                                )

                                Surface(
                                    shape =
                                        RoundedCornerShape(18.dp),

                                    color =
                                        Color.White.copy(alpha = 0.06f)
                                ) {

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),

                                        verticalAlignment =
                                            Alignment.CenterVertically
                                    ) {

                                        Icon(
                                            imageVector =
                                                Icons.Default.FolderOpen,

                                            contentDescription = null,

                                            tint =
                                                Color(0xFF93C5FD)
                                        )

                                        Spacer(
                                            modifier =
                                                Modifier.width(12.dp)
                                        )

                                        Text(
                                            text =
                                                selectedFileName,

                                            color = Color.White,

                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        /*
                         * PASTE BUTTON
                         */

                        Button(
                            onClick = {

                                val clipData: ClipData? =
                                    clipboardManager.primaryClip

                                val copiedText =
                                    clipData
                                        ?.getItemAt(0)
                                        ?.text
                                        ?.toString()

                                pastedText =
                                    TextFieldValue(
                                        copiedText ?: ""
                                    )

                                clearClipboard(context)

                                showPasteDialog = true
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),

                            shape =
                                RoundedCornerShape(18.dp),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        Color(0xFF7C3AED)
                                )
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.ContentPaste,

                                contentDescription = null
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(10.dp)
                            )

                            Text(
                                text = "Paste Copied Text",

                                fontSize = 17.sp,

                                fontWeight =
                                    FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        /*
                         * FILE BUTTON
                         */

                        Button(
                            onClick = {

                                launcher.launch(
                                    arrayOf(
                                        "text/plain",
                                        "application/pdf",
                                        "text/x-markdown",
                                        "application/msword",
                                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                                    )
                                )
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),

                            shape =
                                RoundedCornerShape(18.dp),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        Color(0xFF2563EB)
                                )
                        ) {

                            if (isLoading) {

                                CircularProgressIndicator(
                                    modifier =
                                        Modifier.size(22.dp),

                                    color = Color.White,

                                    strokeWidth = 2.dp
                                )

                            } else {

                                Icon(
                                    imageVector =
                                        Icons.Default.FolderOpen,

                                    contentDescription = null
                                )

                                Spacer(
                                    modifier =
                                        Modifier.width(10.dp)
                                )

                                Text(
                                    text = "Choose File",

                                    fontSize = 17.sp,

                                    fontWeight =
                                        FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text =
                        "Built for creators and presenters",

                    color =
                        Color.White.copy(alpha = 0.45f),

                    style =
                        MaterialTheme.typography.bodySmall,

                    modifier =
                        Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }

    /*
     * PASTE DIALOG
     */

    if (showPasteDialog) {

        AlertDialog(

            onDismissRequest = {
                showPasteDialog = false
            },

            confirmButton = {

                Button(
                    onClick = {

                        val cleanedText =
                            formatParagraphs(
                                //cleanExtractedText(
                                    pastedText.text
                                //)
                            )

                        showPasteDialog = false

                        onTextExtracted(cleanExtractedText(cleanedText))
                    }
                ) {

                    Text("Use Text")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showPasteDialog = false
                    }
                ) {

                    Text("Cancel")
                }
            },

            title = {

                Text(
                    text = "Paste Script"
                )
            },

            text = {

                OutlinedTextField(
                    value = pastedText,

                    onValueChange = {
                        pastedText = it
                    },

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),

                    placeholder = {

                        Text(
                            "Paste or type your script..."
                        )
                    },

                    shape =
                        RoundedCornerShape(16.dp)
                )
            },

            shape = RoundedCornerShape(24.dp)
        )
    }
}

/*
 * EXTRACT TEXT
 */
fun extractTextFromFile(
    context: Context,
    uri: Uri,
    onResult: (String) -> Unit
) {

    CoroutineScope(Dispatchers.IO).launch {

        try {

            PDFBoxResourceLoader.init(context)

            val contentResolver =
                context.contentResolver

            val mimeType =
                contentResolver.getType(uri)

            val extractedText = when {

                /*
                 * TXT / MD
                 */

                mimeType == "text/plain" ||
                        mimeType == "text/markdown" ||
                        mimeType == "text/x-markdown" -> {

                    val inputStream =
                        contentResolver.openInputStream(uri)

                    inputStream
                        ?.bufferedReader(Charsets.UTF_8)
                        ?.use { it.readText() }
                        ?: ""
                }

                /*
                 * DOCX
                 */

                mimeType ==
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> {

                    val inputStream =
                        contentResolver.openInputStream(uri)

                    if (inputStream == null) {

                        "Unable to open DOCX file"

                    } else {

                        val document =
                            XWPFDocument(inputStream)

                        val text = buildString {

                            /*
                             * BODY ELEMENTS
                             *
                             * This preserves the original order
                             * of paragraphs and tables exactly
                             * as they appear in the document.
                             */

                            document.bodyElements.forEach { element ->

                                when (element) {

                                    /*
                                     * PARAGRAPHS
                                     */

                                    is XWPFParagraph -> {

                                        val paragraphText =
                                            element.text
                                                ?.trim()
                                                .orEmpty()

                                        if (paragraphText.isNotEmpty()) {

                                            append(paragraphText)
                                            append("\n\n")
                                        }
                                    }

                                    /*
                                     * TABLES
                                     */

                                    is XWPFTable -> {

                                        append("\n")
                                        append(TABLE_START)
                                        append("\n\n")

                                        /*
                                         * HEADERS
                                         */

                                        val headers =
                                            element.rows
                                                .firstOrNull()
                                                ?.tableCells
                                                ?.map {
                                                    it.text
                                                        .trim()
                                                        .replace("\n", " ")
                                                }
                                                ?: emptyList()

                                        /*
                                         * ROWS
                                         */

                                        element.rows
                                            .drop(1)
                                            .forEachIndexed { rowIndex, row ->

                                                append("ROW ${rowIndex + 1}\n")
                                                append("─────────\n")

                                                row.tableCells
                                                    .forEachIndexed { cellIndex, cell ->

                                                        val header =
                                                            headers.getOrNull(cellIndex)
                                                                ?: "Column ${cellIndex + 1}"

                                                        val value =
                                                            cell.text
                                                                .trim()
                                                                .replace("\n", " ")

                                                        append(header)
                                                        append(": ")
                                                        append(value)
                                                        append("\n")
                                                    }

                                                append("\n")
                                            }

                                        append(TABLE_END)
                                        append("\n\n")
                                    }
                                }
                            }
                        }

                        document.close()

                        text
                    }
                }

                /*
                 * DOC
                 */

                mimeType == "application/msword" -> {

                    val inputStream =
                        contentResolver.openInputStream(uri)

                    if (inputStream == null) {

                        "Unable to open DOC file"

                    } else {

                        val document =
                            HWPFDocument(inputStream)

                        val extractor =
                            WordExtractor(document)

                        val text =
                            extractor.text

                        extractor.close()
                        document.close()

                        text
                    }
                }

                /*
                 * PDF
                 */

                mimeType == "application/pdf" -> {

                    val inputStream =
                        contentResolver.openInputStream(uri)

                    if (inputStream == null) {

                        "Unable to open PDF file"

                    } else {

                        val document =
                            PDDocument.load(inputStream)

                        val stripper =
                            PDFTextStripper()

                        stripper.sortByPosition = true

                        val text =
                            stripper.getText(document)

                        document.close()

                        text
                    }
                }

                else -> {

                    "Unsupported file type"
                }
            }

            val cleanedText =
                cleanExtractedText(extractedText)

            withContext(Dispatchers.Main) {

                onResult(
                    formatParagraphs(cleanedText)
                )
            }

        } catch (e: Exception) {

            e.printStackTrace()

            withContext(Dispatchers.Main) {

                onResult(
                    "Error reading file: ${e.message}"
                )
            }
        }
    }
}

/*
 * CLEAR CLIPBOARD
 */

fun clearClipboard(context: Context) {

    val clipboardManager =
        context.getSystemService(
            Context.CLIPBOARD_SERVICE
        ) as ClipboardManager

    val emptyClip =
        ClipData.newPlainText("", "")

    clipboardManager.setPrimaryClip(emptyClip)
}

/*
 * FORMAT TEXT
 */

fun formatParagraphs(text: String): String {

    return text

        .replace("\r", "")

        .replace(
            Regex("[ ]{2,}"),
            " "
        )

        .replace(
            Regex("\\n{3,}"),
            "\n\n"
        )

        .trim()
}

/*
 * CLEAN TEXT
 */

fun cleanExtractedText(text: String): String {

    return text

        /*
         * REMOVE INVALID CHARACTERS
         */

        .replace("�", "")
        .replace("\u0000", "")

        /*
         * NORMALIZE WINDOWS LINE BREAKS
         */

        .replace("\r", "")

        /*
         * REMOVE INVISIBLE FORMAT CHARACTERS
         * BUT KEEP NEW LINES
         */

        .replace(
            Regex("[\\p{Cf}]"),
            ""
        )

        /*
         * REMOVE CONTROL CHARACTERS
         * EXCEPT \n AND \t
         */

        .replace(
            Regex("[\\p{Cc}&&[^\\n\\t]]"),
            ""
        )

        /*
         * REMOVE EXCESS SPACES
         */
        .replace(
            Regex("[ ]{2,}"),
            " "
        )
        /*
         * LIMIT HUGE EMPTY SPACING
         */
        .replace(
            Regex("\\n{4,}"),
            "\n\n\n"
        )

        .trim()
}

/*
 * FILE NAME
 */

fun getFileName(
    context: Context,
    uri: Uri
): String {

    var name = "Unknown File"

    val cursor =
        context.contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

    cursor?.use {

        if (it.moveToFirst()) {

            val index =
                it.getColumnIndex(
                    OpenableColumns.DISPLAY_NAME
                )

            if (index >= 0) {

                name = it.getString(index)
            }
        }
    }

    return name
}



