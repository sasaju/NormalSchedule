package com.liflymark.normalschedule.ui.set_background

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
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
import com.liflymark.normalschedule.ui.theme.NorScTheme
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.launch
import java.io.File

class SetBackground : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NorScTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    DefaultAndUserBack()
                }
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
    viewModel: DefaultBackgroundViewModel = viewModel(),
){
    val path = viewModel.pathState.observeAsState()
    var selectedDefault  by rememberSaveable{ mutableStateOf(false)}
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode != RESULT_OK) {
            return@rememberLauncherForActivityResult
        }
        Log.d("BackGround", PictureSelector.obtainMultipleResult(activityResult.data).toString())
        val uri = PictureSelector.obtainMultipleResult(activityResult.data)[0].path
        val backgroundFileName = System.currentTimeMillis().toString()+"backImage"
        val realUri = if(Build.VERSION.SDK_INT <29){ Uri.fromFile(File(uri)).toString() }else{ uri }
        context.openFileOutput(backgroundFileName, Context.MODE_PRIVATE).use {
            val inputStream = context.contentResolver.openInputStream(realUri.toUri())
            it.write(inputStream?.readBytes())
        }
        scope.launch {
            val internalUri = Uri.fromFile(File(context.filesDir,backgroundFileName)).toString()
            val lastFile = Repository.loadBackgroundFileName()
            lastFile?.let {
                context.deleteFile(it)
                Log.d("deletBack",it.toString())
            }
            viewModel.userBackgroundUri = internalUri
            viewModel.updateBackground()
//            binding.userImage.load(internalUri, imageLoader)
            Toasty.success(context, "读取成功，返回看看吧", Toasty.LENGTH_SHORT).show()
        }

    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        ImageCard(
            selected = selectedDefault,
            path = Uri.parse(""),
            text = "默认背景图"
        ) {
            selectedDefault = true
        }
        path.value?.let{
            Box(modifier = Modifier
                .width(134.dp)
                .height(233.dp),
                contentAlignment = Alignment.Center
            ) {
                ImageCard(
                    selected = !selectedDefault,
                    path = it,
                    text = "用户自定义背景"
                ) {
                    selectedDefault = false
                }
                TextButton(onClick = {
                    PictureSelector.create(context as Activity)
                        .openGallery(1)
                        .imageEngine(CoilEngine.create())
                        .isCamera(false)
                        .isGif(true)
                        .selectionMode(PictureConfig.SINGLE)
                        .forResult(PictureConfig.CHOOSE_REQUEST)
                }) {
                    Text(text = "更换背景")
                }
            }
        }
    }

}

@Composable
fun ImageCard(
    selected: Boolean,
    path:Uri,
    text:String,
    onClick:() -> Unit
){
    Card(
        modifier = Modifier
            .width(134.dp)
            .height(333.dp)
            .clickable { onClick() },
        shape = MaterialTheme.shapes.medium
    ) {
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
        if (selected){
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(20.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview3() {
    NorScTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            DefaultAndUserBack()
        }
    }
}