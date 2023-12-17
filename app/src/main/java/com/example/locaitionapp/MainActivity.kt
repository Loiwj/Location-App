package com.example.locaitionapp

import android.content.Context
import android.os.Bundle
import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.locaitionapp.ui.theme.LocaitionAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel:LocationViewModel = viewModel()
            LocaitionAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LocationApp(viewModel = viewModel)
                }
            }
        }
    }
}
@Composable
fun LocationApp(viewModel: LocationViewModel){
    val context = LocalContext.current
    val LocationUtils = LocationUtils(context)
    LocationDisplay(LocationUtils = LocationUtils,viewModel =  viewModel, context = context )
}
@Composable
fun LocationDisplay(
    LocationUtils: LocationUtils,
    viewModel:LocationViewModel,
    context: Context
) {
    val location = viewModel.location.value

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = {
            //Check xem co quyền truy cập không
                permissions ->
            if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                && permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
            ) {
                //I have access to location
                LocationUtils.requestLocationUpdates(viewModel = viewModel)
            } else {
                //Ask for permission
                //Hiển thị hộp thoại yêu cầu quyền từ người dùng
                val rationalRequired = ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) || ActivityCompat.shouldShowRequestPermissionRationale(
                    context as MainActivity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                if (rationalRequired) {
                    Toast.makeText(
                        context,
                        "Location permission is required for this feature to work",
                        Toast.LENGTH_LONG
                    )
                        .show()
                } else {
                    Toast.makeText(
                        context,
                        "Location permission is required. Please enable it is in the Android Settings",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if(location != null){
            Text("Address: ${location.latitude} ${location.longitude}" )
        }else {
            Text("Location not available")
        }

        Button(onClick = {
            if (LocationUtils.hasLocationPermission(context)) {
                //Permission already granted update the location
                LocationUtils.requestLocationUpdates(viewModel = viewModel)
            } else {
                //Request permission location
                requestPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        }) {

            Text("Get Location")
        }
    }
}

