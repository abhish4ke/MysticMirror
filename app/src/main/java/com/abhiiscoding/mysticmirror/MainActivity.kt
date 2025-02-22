package com.abhiiscoding.mysticmirror

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.abhiiscoding.mysticmirror.homeScreen.HomeScreen
import com.abhiiscoding.mysticmirror.ui.theme.MysticMirrorTheme

class MainActivity : ComponentActivity() {
    private val homeScreenViewModel: HomeScreenViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            MysticMirrorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colorResource(id = R.color.white)
                ) {
                    NavigationGraph(navController)
                }
            }
        }
    }
    @Composable
    fun NavigationGraph(navController: NavController) {
        NavHost(
            navController = navController as NavHostController,
            startDestination = Screens.HomeScreen.route
        ) {
            composable(Screens.HomeScreen.route) {
                HomeScreen(homeScreenViewModel = homeScreenViewModel)
            }
        }
    }
}

