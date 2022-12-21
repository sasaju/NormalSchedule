package com.liflymark.normalschedule.ui.set_background

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.utils.CoilEngine
import com.liflymark.normalschedule.ui.abase.ConorButton
import com.liflymark.normalschedule.ui.abase.ConorOutlineButton
import com.liflymark.normalschedule.ui.exam_arrange.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import kotlinx.coroutines.launch
import java.io.File


class SetBackground : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NorScTheme {
                // A surface container using the 'background' color from the theme
                val state = rememberScaffoldState()
                UiControl()
                Scaffold(
                    scaffoldState = state,
                    topBar = {
                        NormalTopBar(label = "更换背景")
                    },
                    content = {
                        it
                        DefaultAndUserBack(state)
                    }
                )
            }
        }
    }
}


@Composable
fun DefaultAndUserBack(
    state: ScaffoldState,
    viewModel: DefaultBackgroundViewModel = viewModel(),
) {
    val path = remember{ viewModel.pathState }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var requestAndShowDialog by rememberSaveable { mutableStateOf(false) }
    RequestReadImage(
        show = requestAndShowDialog,
        onDismiss = {requestAndShowDialog = false},
        isGranted = {
            PictureSelector.create(context as Activity)
                .openGallery(SelectMimeType.ofImage())
                .setSelectionMode(SelectModeConfig.SINGLE)
                .setImageEngine(CoilEngine())
                .isGif(true)
                .isDisplayCamera(false)
                .forResult(object : OnResultCallbackListener<LocalMedia?> {
                    override fun onResult(result: ArrayList<LocalMedia?>?) {
                        requestAndShowDialog =false
                        val realUri = result?.getOrNull(0)?.path ?: return
                        val backgroundFileName = System.currentTimeMillis().toString() + "backImage"
                        context.openFileOutput(backgroundFileName, Context.MODE_PRIVATE).use {
                            val inputStream = context.contentResolver.openInputStream(realUri.toUri())
                            it.write(inputStream?.readBytes())
                            inputStream?.close()
                        }
                        scope.launch {
                            val internalUri = Uri.fromFile(File(context.filesDir, backgroundFileName)).toString()
                            val lastFile = Repository.loadBackgroundFileName()
                            lastFile?.let {
                                context.deleteFile(it)
                            }
                            viewModel.userBackgroundUri = internalUri
                            viewModel.updateBackground()
                            state.snackbarHostState.showSnackbar("读取成功 返回看看吧~")
                        }
                    }
                    override fun onCancel() {
                        scope.launch{ state.snackbarHostState.showSnackbar("您没有选择任何图片") }
                        requestAndShowDialog = false
                    }
                }
                )
        }
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            ImageCard(
                path = Uri.parse(""),
                text = "默认背景"
            ) {
                scope.launch {
                    val lastFile = Repository.loadBackgroundFileName()
                    lastFile?.let {
                        context.deleteFile(it)
                    }
                    viewModel.userBackgroundUri = "0"
                    viewModel.updateBackground()
                    scope.launch {
                        state.snackbarHostState.showSnackbar("切换成功")
                    }
                }
            }
            path.value?.let {
                ImageCard(
                    path = it,
                    text = "当前背景"
                ) {
                    scope.launch {
                        state.snackbarHostState.showSnackbar("点击下方按钮以更换背景")
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        ConorButton(
            onClick = {
                requestAndShowDialog = true
            },
            text="更换背景"
        )
        Spacer(modifier = Modifier.height(10.dp))
        ConorOutlineButton(
            onClick = {
                scope.launch {
                    val lastFile = Repository.loadBackgroundFileName()
                    lastFile?.let {
                        context.deleteFile(it)
                    }
                    viewModel.userBackgroundUri = "0"
                    viewModel.updateBackground()
                    scope.launch {
                        state.snackbarHostState.showSnackbar("切换成功")
                    }
                }
            },
            text = "切换为默认"
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun RequestReadImage(
    show:Boolean,
    onDismiss:() -> Unit,
    isGranted:() -> Unit
){
    val context = LocalContext.current
    val readPermissionState = rememberPermissionStateMy(
        permission =  if(Build.VERSION.SDK_INT<=32){
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }else{
            android.Manifest.permission.READ_MEDIA_IMAGES
        },
        onCannotRequestPermission = {
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.data = Uri.parse("package:" + context.packageName)
            context.startActivity(intent)
        }
    )
    if (!readPermissionState.status.isGranted && show){
        val buttonText = if (readPermissionState.status.shouldShowRationale){
            "该权限为必要权限，需要授予"
        }else{
            "授予权限"
        }
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text(text = "请授予APP读写权限")},
            text = { Text(text = "需要读取图片的权限以选择图片") },
            buttons = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                   TextButton(
                       onClick = {
                           readPermissionState.launchPermissionRequest()
                       }) {
                       Text(text = buttonText)
                   }
                }
            }
        )
    }
    if (readPermissionState.status.isGranted && show){
        isGranted()
    }
}

@ExperimentalPermissionsApi
@Composable
fun rememberPermissionStateMy(
    permission: String,
    onCannotRequestPermission: () -> Unit = {},
    onPermissionResult: (Boolean) -> Unit = {},
): ExtendedPermissionState {
    val activity = LocalContext.current as Activity

    var currentShouldShowRationale by remember {
        mutableStateOf(activity.shouldShowRationale(permission))
    }

    var atDoubleDenialForPermission by remember {
        mutableStateOf(false)
    }

    val mutablePermissionState = rememberPermissionState(permission) { isGranted ->
        if (!isGranted) {
            val updatedShouldShowRationale = activity.shouldShowRationale(permission)
            if (!currentShouldShowRationale && !updatedShouldShowRationale)
                onCannotRequestPermission()
            else if (currentShouldShowRationale && !updatedShouldShowRationale)
                atDoubleDenialForPermission = false
        }
        onPermissionResult(isGranted)
    }

    return remember(permission) {
        ExtendedPermissionState(
            permission = permission,
            mutablePermissionState = mutablePermissionState,
            onCannotRequestPermission = onCannotRequestPermission,
            atDoubleDenial = atDoubleDenialForPermission,
            onLaunchedPermissionRequest = {
                currentShouldShowRationale = it
            }
        )
    }
}
fun Activity.shouldShowRationale(permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
}
@OptIn(ExperimentalPermissionsApi::class)
@Stable
class ExtendedPermissionState(
    override val permission: String,
    private val mutablePermissionState: PermissionState,
    private val atDoubleDenial: Boolean,
    private val onLaunchedPermissionRequest: (shouldShowRationale: Boolean) -> Unit,
    private val onCannotRequestPermission: () -> Unit
) : PermissionState {
    override val status: PermissionStatus
        get() = mutablePermissionState.status

    override fun launchPermissionRequest() {
        onLaunchedPermissionRequest(mutablePermissionState.status.shouldShowRationale)
        if (atDoubleDenial) onCannotRequestPermission()
        else mutablePermissionState.launchPermissionRequest()
    }
}
@Composable
fun ImageCard(
    path: Uri,
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(300.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = text)
            Image(
                painter = rememberAsyncImagePainter(
                    ImageRequest.Builder(LocalContext.current)
                        .data(data = path)
                        .error(R.drawable.main_background_4)
                        .build()
                ),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}
