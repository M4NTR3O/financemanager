package com.bignerdranch.android.financemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bignerdranch.android.financemanager.ui.nav.AppNav
import com.bignerdranch.android.financemanager.ui.theme.FinanceManagerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { FinanceManagerTheme { AppNav() } }
    }
}