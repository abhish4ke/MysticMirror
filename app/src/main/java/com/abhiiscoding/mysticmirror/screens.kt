package com.abhiiscoding.mysticmirror

sealed class Screens(val route: String) {
    object HomeScreen : Screens("HomeScreen")
}