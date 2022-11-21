package com.liflymark.normalschedule.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.liflymark.normalschedule.ui.score_detail.UiControl
import com.liflymark.normalschedule.ui.theme.NorScTheme
import kotlinx.coroutines.launch
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.sign_in_compose.SignUIAll

class GetViewToPng : ComponentActivity() {
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val view = LayoutInflater.from(this).inflate(R.layout.activity_test, null, false)
//        val drawingCacheEnabled = true
//        setContentView(view)
//        view.setDrawingCacheEnabled(drawingCacheEnabled)
//        view.buildDrawingCache(drawingCacheEnabled)
//        view.measure(
//            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
//        );
//        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//        val drawingCache: Bitmap = view.getDrawingCache()
//        val bitmap: Bitmap?
//        bitmap = Bitmap.createBitmap(drawingCache)
//        view.setDrawingCacheEnabled(false)
////        MediaStore.Images.Media.insertImage(contentResolver, bitmap!!, "title", "")
//        saveImage(bitmap,this, "test")
//        lifecycle.coroutineScope.launch {
//            
//        }
//        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent { 
            NorScTheme {
                UiControl()
                val state = rememberScaffoldState()
                Scaffold(
                    scaffoldState = state,
                    content = {
                        it
                        SignUIAll(state){_,_ ->}
                    },
                    topBar = {
                        NormalTopBar(label = "登录")
                    }
                )

            }
        }
    }
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun RequestGrant(
        multiplePermissionsState: MultiplePermissionsState,
        navigateToSettingsScreen: () -> Unit,
        scaffoldState: ScaffoldState,
        hadShowedGrant:@Composable () -> Unit
    ) {
        var doNotShowRationale by rememberSaveable { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        when {
            multiplePermissionsState.allPermissionsGranted -> {
                hadShowedGrant()
            }
            multiplePermissionsState.shouldShowRationale ->
            {
                if (doNotShowRationale) {
                    LaunchedEffect(key1 = Unit, block = {
                        scaffoldState.snackbarHostState.showSnackbar(message = "您已拒绝授予，如需授予请重新打开该页面")
                    })
                } else {
                    var show by rememberSaveable{ mutableStateOf(true)}
                    if (show) {
                        AlertDialog(
                            onDismissRequest = {
                                multiplePermissionsState.launchMultiplePermissionRequest()
                                show = false
                            },
                            confirmButton = {
                                TextButton(onClick = { multiplePermissionsState.launchMultiplePermissionRequest() }) {
                                    Text(text = "授予权限")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar("拒绝将无法使用该功能")
                                    }
                                    show = false
                                    doNotShowRationale = true
                                }) {
                                    Text(text = "拒绝并不再提示")

                                }
                            },
                            title = {
                                Text(text = "应用需要您授予权限，用以导入日历事件")
                            },
                            properties = DialogProperties(
                                dismissOnBackPress = true,
                                dismissOnClickOutside = false
                            )
                        )
                    }
                }
            }
            else -> {
                var show by rememberSaveable{ mutableStateOf(true)}
                if (show){
                    AlertDialog(
                        onDismissRequest = {
                            show = false
                        },
                        confirmButton = {
                            TextButton(onClick = navigateToSettingsScreen ) {
                                Text(text = "去设置授予")
                            }
                        },
                        title = {
                            Text(text = "应用需要您授予权限，用以导入日历事件")
                        },
                        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
                    )
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    private fun getPermissionsText(permissions: List<PermissionState>): String {
        val revokedPermissionsSize = permissions.size
        if (revokedPermissionsSize == 0) return ""

        val textToShow = StringBuilder().apply {
            append("The ")
        }

        for (i in permissions.indices) {
            textToShow.append(permissions[i].permission)
            when {
                revokedPermissionsSize > 1 && i == revokedPermissionsSize - 2 -> {
                    textToShow.append(", and ")
                }
                i == revokedPermissionsSize - 1 -> {
                    textToShow.append(" ")
                }
                else -> {
                    textToShow.append(", ")
                }
            }
        }
        textToShow.append(if (revokedPermissionsSize == 1) "permission is" else "permissions are")
        return textToShow.toString()
    }
    private fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName)
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            // RELATIVE_PATH and IS_PENDING are introduced in API 29.

            val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }
        } else {
            val directory = File(Environment.getExternalStorageDirectory().toString() + separator + folderName)
            // getExternalStorageDirectory is deprecated in API 29

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString() + ".png"
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            if (file.absolutePath != null) {
                val values = contentValues()
                values.put(MediaStore.Images.Media.DATA, file.absolutePath)
                // .DATA is deprecated in API 29
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        }
    }

    private fun contentValues() : ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * 获取一个 View 的缓存视图
     * (前提是这个View已经渲染完成显示在页面上)
     * @param view
     * @return
     */
    fun getCacheBitmapFromView(view: View): Bitmap? {

        return null
    }
}