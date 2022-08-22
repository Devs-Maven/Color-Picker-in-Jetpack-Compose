package com.example.myapplication

import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.github.skydoves.colorpicker.compose.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme(){
                HomeScreen()
            }
        }
    }
}

@Composable
fun HomeScreen() {

    val controller = rememberColorPickerController()

    var color by remember{mutableStateOf(Color(0xFFFFFFFF))}

    val sysUiController = rememberSystemUiController()
    sysUiController.setSystemBarsColor(color)

    Scaffold(
        Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {Text("Color Picker")},
                backgroundColor = color
            )
        }
    ){
        Column(
            Modifier.fillMaxSize(),
            Arrangement.Center,
            Alignment.CenterHorizontally
        ){

            // ColorPicker
            HsvColorPicker(
                modifier = Modifier.fillMaxWidth(0.65f)
                    .fillMaxHeight(0.38f),
                controller = controller,
                onColorChanged = {
                    color = controller.selectedColor.value
                },
            )

            // ColorPicker from Image
            val context = LocalContext.current
            val photoPicker =
                rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    if(uri != null) {
                        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            ImageDecoder.decodeBitmap(
                                ImageDecoder.createSource(
                                    context.contentResolver,
                                    uri
                                )
                            )
                        } else {
                            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        }
                        controller.setPaletteImageBitmap(bitmap.asImageBitmap())
                    }
            }

            // Button to select image
            OutlinedButton(
                modifier = Modifier.height(45.dp),
                onClick = { photoPicker.launch("image/*") }
            ){Text("Select Image")}
            Spacer(modifier = Modifier.height(10.dp))

            ImageColorPicker(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .fillMaxHeight(0.38f),
                controller = controller,
                paletteImageBitmap = ImageBitmap
                    .imageResource(R.drawable.jetpack_image),
                paletteContentScale = PaletteContentScale.FIT,
                onColorChanged = {
                    color = controller.selectedColor.value
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Slider for Opacity
            AlphaSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(35.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(6.dp)),
                controller = controller,
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Slider for Darkness/Brightness
            BrightnessSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .height(35.dp)
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(6.dp)),
                controller = controller,
            )
        }
    }
}