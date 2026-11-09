package com.example.mypetshop

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.example.mypetshop.data.session.NavigationSession
import com.example.mypetshop.ui.navigation.AppNavigation
import com.example.mypetshop.ui.theme.MyPetShopTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject lateinit var navigationSession: NavigationSession

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyPetShopTheme {
                AppNavigation(session = navigationSession)
            }
        }
    }
}
