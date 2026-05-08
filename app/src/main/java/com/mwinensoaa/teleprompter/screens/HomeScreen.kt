package com.mwinensoaa.teleprompter.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import com.mwinensoaa.teleprompter.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument

@Composable
fun HomeScreen(
    context: Context,
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

    LaunchedEffect(Unit) {
        showContent = true
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->

        uri?.let {

            selectedFileName = getFileName(context, it)

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

        // =========================
        // Background Glow
        // =========================

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
                    Color(0xFF7C3AED).copy(alpha = 0.2f)
                )
        )

        AnimatedVisibility(
            visible = showContent,
            enter = fadeIn()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),

                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(70.dp))

                // =========================
                // Logo
                // =========================

                Surface(
                    shape = CircleShape,

                    color = Color.White.copy(alpha = 0.08f),

                    tonalElevation = 8.dp,

                    modifier = Modifier.size(120.dp)
                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.MenuBook,

                            contentDescription = null,

                            tint = Color.White,

                            modifier = Modifier.size(58.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = stringResource((R.string.app_name)),

                    style = MaterialTheme.typography.headlineMedium,

                    fontWeight = FontWeight.Bold,

                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text =
                        "Import scripts, read smoothly",

                    style = MaterialTheme.typography.bodyLarge,

                    color = Color.White.copy(alpha = 0.7f),

                    lineHeight = 28.sp,

                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(60.dp))

                // =========================
                // Import Card
                // =========================

                Card(
                    modifier = Modifier.fillMaxWidth(),

                    shape = RoundedCornerShape(32.dp),

                    colors = cardColors(
                        containerColor =
                            Color.White.copy(alpha = 0.08f)
                    ),

                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    )
                ) {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),

                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Surface(
                            shape = CircleShape,

                            color =
                                Color(0xFF2563EB).copy(alpha = 0.15f),

                            modifier = Modifier.size(72.dp)
                        ) {

                            Box(
                                contentAlignment = Alignment.Center
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.Description,

                                    contentDescription = null,

                                    tint = Color(0xFF60A5FA),

                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(22.dp))

                        Text(
                            text = "Import Document",

                            style =
                                MaterialTheme.typography.titleLarge,

                            color = Color.White,

                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text =
                                "Choose a PDF or TXT document from your device",

                            style =
                                MaterialTheme.typography.bodyMedium,

                            color = Color.White.copy(alpha = 0.7f),

                            textAlign = TextAlign.Center,

                            lineHeight = 24.sp
                        )

                        AnimatedVisibility(
                            visible =
                                selectedFileName.isNotEmpty()
                        ) {

                            Column {

                                Spacer(modifier = Modifier.height(24.dp))

                                Surface(
                                    shape =
                                        RoundedCornerShape(16.dp),

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
                                            text = selectedFileName,

                                            color = Color.White,

                                            style =
                                                MaterialTheme.typography.bodyMedium,

                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(30.dp))

                        Button(
                            onClick = {

                                launcher.launch(
                                    arrayOf(
                                        "text/plain",
                                        "application/pdf",
                                        "application/msword",
                                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                                    )
                                )
                            },

                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),

                            shape = RoundedCornerShape(18.dp),

                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor =
                                        Color(0xFF2563EB)
                                )
                        ) {

                            if (isLoading) {

                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),

                                    color = Color.White,

                                    strokeWidth = 2.dp
                                )
                            }
                            else {

                                Icon(
                                    imageVector =
                                        Icons.Default.FolderOpen,

                                    contentDescription = null
                                )

                                Spacer(
                                    modifier = Modifier.width(10.dp)
                                )

                                Text(
                                    text = "Choose File",

                                    fontSize = 17.sp,

                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Built for creators and presenters",

                    color = Color.White.copy(alpha = 0.45f),

                    style = MaterialTheme.typography.bodySmall,

                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
        }
    }
}

/**
 * Extract text from TXT or PDF
 */
fun extractTextFromFile(
context: Context,
uri: Uri,
onResult: (String) -> Unit
) {

    CoroutineScope(Dispatchers.IO).launch {

        PDFBoxResourceLoader.init(context)

        val contentResolver =
            context.contentResolver

        val mimeType =
            contentResolver.getType(uri)

        try {

            val extractedText = when {

                /*
                 * TXT FILE
                 */
                mimeType == "text/plain" -> {

                    val inputStream =
                        contentResolver.openInputStream(uri)

                    inputStream
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        ?: ""
                }

                /*
                 * DOCX FILE
                 */
                mimeType ==
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> {

                    val inputStream =
                        contentResolver.openInputStream(uri)

                    val document =
                        XWPFDocument(inputStream)

                    val text = buildString {

                        // Paragraphs
                        document.paragraphs.forEach {

                            append(it.text)
                            append("\n\n")
                        }

                        // Tables
                        document.tables.forEach { table ->

                            append("\n")

                            table.rows.forEach { row ->

                                row.tableCells.forEach { cell ->

                                    append(cell.text)
                                    append("    ")
                                }

                                append("\n")
                            }

                            append("\n")
                        }
                    }

                    document.close()

                    text
                }

                /*
                 * DOC FILE
                 */
                mimeType == "application/msword" -> {

                    val inputStream =
                        contentResolver.openInputStream(uri)

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

                /*
                 * PDF FILE
                 */
                mimeType == "application/pdf" -> {

                    val inputStream =
                        contentResolver.openInputStream(uri)

                    val document =
                        PDDocument.load(inputStream)

                    val stripper =
                        PDFTextStripper()

                    val text =
                        stripper.getText(document)

                    document.close()

                    text
                }

                /*
                 * UNSUPPORTED
                 */
                else -> {

                    "Unsupported file type"
                }
            }

            /*
             * RETURN RESULT ON UI THREAD
             */
            withContext(Dispatchers.Main) {

                onResult(extractedText)
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

/**
 * Get file name
 */
fun getFileName(
    context: Context,
    uri: Uri
): String {

    var name = "Unknown File"

    val cursor = context.contentResolver.query(
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