package com.example.myapplication.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.myapplication.ui.dashboard.DashboardActivity
import com.example.myapplication.R

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val preference = getSharedPreferences("auth", MODE_PRIVATE)
            preference.edit {
                putString("token", "oauth_access_token")
                putLong("expiry", System.currentTimeMillis() + 60000)
            }
            println(" login "+(System.currentTimeMillis() + 60000))
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }
}