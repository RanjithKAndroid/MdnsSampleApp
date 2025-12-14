package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_detail)
        lifecycleScope.launch {
            val info = withContext(Dispatchers.IO) {
                val ip = getPublicIp()
                getIpDetails(ip)
            }
            findViewById<TextView>(R.id.tvIPInfo).text = info
        }
    }

    private fun getPublicIp(): String {
        val url = URL("https://api.ipify.org?format=json")
        val conn = url.openConnection() as HttpURLConnection
        val res = conn.inputStream.bufferedReader().readText()
        return JSONObject(res).getString("ip")
    }
    private fun getIpDetails(ip: String): String {
        val url = URL("https://ipinfo.io/$ip/geo")
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        val response = conn.inputStream.bufferedReader().readText()
        val json = JSONObject(response)

        val ipAddress = json.optString("ip")
        val city = json.optString("city")
        val region = json.optString("region")
        val country = json.optString("country")
        val org = json.optString("org")
        val timezone = json.optString("timezone")

        return """
        IP Address : $ipAddress
        City       : $city
        Region     : $region
        Country    : $country
        ISP        : $org
        Timezone   : $timezone
    """.trimIndent()
    }

}