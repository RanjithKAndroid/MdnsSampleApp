package com.example.myapplication.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.ui.dashboard.DashboardActivity
import com.example.myapplication.util.Utils.isNetworkAvailable

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preference = getSharedPreferences("auth",MODE_PRIVATE)
        val token = preference.getString("token",null)
        val expiry = preference.getLong("expiry",0)
        println(" expiry "+expiry+" currentTimeMillis "+ System.currentTimeMillis())
        if (token!=null && expiry > System.currentTimeMillis()){
            if (isNetworkAvailable(this)){
                startActivity(Intent(this, DashboardActivity::class.java))
            } else {
                preference.edit().clear().apply()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()

    }
}