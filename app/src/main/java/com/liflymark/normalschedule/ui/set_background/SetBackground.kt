package com.liflymark.normalschedule.ui.set_background

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.liflymark.normalschedule.R
import com.liflymark.normalschedule.logic.Repository
import com.liflymark.normalschedule.logic.utils.CoilEngine
import com.liflymark.normalschedule.ui.abase.ConorButton
import com.liflymark.normalschedule.ui.abase.ConorOutlineButton
import com.liflymark.normalschedule.ui.exam_arrange.UiControl
import com.liflymark.normalschedule.ui.sign_in_compose.NormalTopBar
import com.liflymark.normalschedule.ui.theme.NorScTheme
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
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
fun Greeting3(name: String) {
    Text(text = "Hello $name!")
}

@Composable
fun DefaultAndUserBack(
    state: ScaffoldState,
    viewModel: DefaultBackgroundViewModel = viewModel(),
) {
    val path = remember{ viewModel.pathState }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        Log.d("Background", activityResult.resultCode.toString())
        if (activityResult.resultCode != RESULT_OK) {
            return@rememberLauncherForActivityResult
        }
        Log.d("BackGround", PictureSelector.obtainMultipleResult(activityResult.data).toString())
        val uri = PictureSelector.obtainMultipleResult(activityResult.data)[0].path
        val backgroundFileName = System.currentTimeMillis().toString() + "backImage"
        val realUri = if (Build.VERSION.SDK_INT < 29) {
            Uri.fromFile(File(uri)).toString()
        } else {
            uri
        }
        context.openFileOutput(backgroundFileName, Context.MODE_PRIVATE).use {
            val inputStream = context.contentResolver.openInputStream(realUri.toUri())
            it.write(inputStream?.readBytes())
        }
        scope.launch {
            val internalUri = Uri.fromFile(File(context.filesDir, backgroundFileName)).toString()
            val lastFile = Repository.loadBackgroundFileName()
            lastFile?.let {
                context.deleteFile(it)
                Log.d("deletBack", it.toString())
            }
            viewModel.userBackgroundUri = internalUri
            viewModel.updateBackground()
//            binding.userImage.load(internalUri, imageLoader)
//            Toasty.success(context, "读取成功，返回看看吧", Toasty.LENGTH_SHORT).show()
            state.snackbarHostState.showSnackbar("读取成功 返回看看吧~")
        }

    }

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
                PictureSelector.create(context as Activity)
                    .openGallery(1)
                    .imageEngine(CoilEngine.create())
                    .isCamera(false)
                    .isGif(true)
                    .selectionMode(PictureConfig.SINGLE)
                    .forResult(launcher)
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
