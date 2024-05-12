package com.example.arrivalcharm.activity

import android.annotation.SuppressLint
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import coil.size.Size
import com.example.arrivalcharm.R
import com.example.arrivalcharm.activity.ui.theme.ArrivalCharmTheme
import com.example.arrivalcharm.db.datastore.DatastoreViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AlarmSettingActivity : ComponentActivity() {

    private val dataStoreViewModel: DatastoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ArrivalCharmTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    Greeting(dataStoreViewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(datastoreViewModel: DatastoreViewModel) {

    var alarmDistanceText by remember {
        mutableStateOf("2")
    }

    Column {
        Row {
            Text("알람 거리 :")
            Spacer(modifier = Modifier.width(10.dp))
            Text(alarmDistanceText)
        }
        Spacer(modifier = Modifier.size(10.dp))
        AlertDialogShow {
            alarmDistanceText = it
            val value = alarmDistanceText.toIntOrNull() ?: return@AlertDialogShow
            datastoreViewModel.putAlarmServiceDistance(value)
        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun AlertDialogShow(onClickConfirm: (String) -> Unit) {
    val showDialog = remember { mutableStateOf(false) }
    var editTextValue by remember { mutableStateOf(TextFieldValue()) }
    val scaffoldState = rememberScaffoldState()
    var titleText by remember {
        mutableStateOf("알람 거리 수정")
    }

    Button(onClick = {
        showDialog.value = true
    }) {
        Text(text = "수정하기")
    }

    if (showDialog.value) {
        val coroutineScope = rememberCoroutineScope()
        Scaffold(scaffoldState = scaffoldState, modifier = Modifier.fillMaxSize()) {
            AlertDialog(
                onDismissRequest = {
                    showDialog.value = false
                    editTextValue = TextFieldValue()
                },
                title = { Text(text = titleText) },
                text = {
                    BasicTextField(value = editTextValue, onValueChange = {
                        editTextValue = it
                    })
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val updateValue = editTextValue.text
                            if (isConvertibleToInt(updateValue)) {
                                onClickConfirm.invoke(updateValue)
                                showDialog.value = false //dismiss
                                editTextValue = TextFieldValue()
                            } else {
                                coroutineScope.launch {
                                    titleText = "숫자 형식을 입력하세요."
                                    delay(2000)
                                    titleText = "알람 거리 수정"
                                }
                            }
                        }) {
                        Text("확인")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog.value = false
                            editTextValue = TextFieldValue()
                        }) {
                        Text("취소")
                    }
                }, modifier = Modifier.background(Color.White)
            )
        }

        SnackbarHost(
            scaffoldState.snackbarHostState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            snackbar = {
                Snackbar(action = {
                    TextButton(onClick = { scaffoldState.snackbarHostState.currentSnackbarData?.dismiss() }) {
                        Text(text = "닫기")
                    }
                }, modifier = Modifier.background(Color.White)) {
                    Text(
                        text = scaffoldState.snackbarHostState.currentSnackbarData?.message
                            ?: "숫자 형태 값을 입력하세요."
                    )
                }
            })
    }
}

private fun isConvertibleToInt(text: String): Boolean {
    return try {
        text.toInt()
        true
    } catch (e: java.lang.NumberFormatException) {
        false
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ArrivalCharmTheme {}
}

@Composable
fun GifImage(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val imageLoader = ImageLoader.Builder(context)
        .components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }
        .build()
    Image(
        painter = rememberAsyncImagePainter(
            ImageRequest.Builder(context).data(data = R.raw.kickingbal).apply(block = {
                size(Size.ORIGINAL)
            }).build(), imageLoader = imageLoader
        ),
        contentDescription = null,
        modifier = modifier.fillMaxWidth(),
        contentScale = ContentScale.Crop
    )
}