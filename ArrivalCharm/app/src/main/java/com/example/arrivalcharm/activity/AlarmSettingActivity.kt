package com.example.arrivalcharm.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting() {

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
        }
    }
}

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
    ArrivalCharmTheme {
        Greeting()
    }
}