package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class DashboardActivity : AppCompatActivity() {

    private lateinit var nsdManager: NsdManager
    private lateinit var database: AppDatabase
    private lateinit var deviceAdapter: DeviceAdapter
    private val discoveryListeners = mutableListOf<NsdManager.DiscoveryListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        database = AppDatabase.getInstance(this)
        deviceAdapter = DeviceAdapter(mutableListOf()) {
            startActivity(Intent(this, DetailActivity::class.java))
        }

        findViewById<RecyclerView>(R.id.rvDevices).apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = this@DashboardActivity.deviceAdapter
        }

        loadDeviceFromDB()

        //discoverDevicesList()

    }

    private fun loadDeviceFromDB() {
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                database.deviceDao().updateAllDevicesStatus("OFFLINE")
                database.deviceDao().getAllDevicesList()
            }.also {
                deviceAdapter.updateList(it)
            }
        }
    }

    private fun discoverDevicesList(){
        nsdManager = getSystemService(NSD_SERVICE) as NsdManager
        nsdManager.discoverServices("_http._tcp.", NsdManager.PROTOCOL_DNS_SD, discoveryListener)
    }

    override fun onStart() {
        super.onStart()

        nsdManager = getSystemService(NSD_SERVICE) as NsdManager
        val serviceTypes = listOf(
            "_http._tcp.",
            "_googlecast._tcp.",
            "_airplay._tcp.",
            "_ipp._tcp."
        )
        /*nsdManager.discoverServices(
            "_http._tcp.",
            NsdManager.PROTOCOL_DNS_SD,
            discoveryListener
        )*/
        /*nsdManager.discoverServices(
            "_readyformdns._tcp.",
            NsdManager.PROTOCOL_DNS_SD,
            discoveryListener
        )*/
        serviceTypes.forEach { type ->
            val listener = createDiscoveryListener(type)
            discoveryListeners.add(listener)

            nsdManager.discoverServices(
                type,
                NsdManager.PROTOCOL_DNS_SD,
                listener
            )
        }

    }


    override fun onStop() {
        super.onStop()

        try {
            nsdManager.stopServiceDiscovery(discoveryListener)
        } catch (e: Exception) {
            // Safe guard
        }
    }

    private fun createDiscoveryListener(type: String): NsdManager.DiscoveryListener {
        return object : NsdManager.DiscoveryListener {

            override fun onDiscoveryStarted(serviceType: String) {
                Log.d("mDNS", "Started: $type")
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                Log.d("mDNS", "Found [$type]: ${serviceInfo.serviceName}")
                resolveServiceCompat(serviceInfo)
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                Log.d("mDNS", "Lost [$type]: ${serviceInfo.serviceName}")
            }

            override fun onDiscoveryStopped(serviceType: String) {}
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {}
            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {}
        }
    }

    private val discoveryListener = object : NsdManager.DiscoveryListener {
        override fun onDiscoveryStarted(p0: String?) {
            println(" started ")
        }

        override fun onDiscoveryStopped(p0: String?) {
            println(" stopped ")
        }

        override fun onServiceFound(nsdServiceInfo: NsdServiceInfo) {
            println(" found ")
            resolveServiceCompat(nsdServiceInfo)
            //nsdManager.resolveService(nsdServiceInfo, Executors.newSingleThreadExecutor() ,resolveListener)
        }

        override fun onServiceLost(p0: NsdServiceInfo?) {
            println(" lost ")
        }

        override fun onStartDiscoveryFailed(p0: String?, p1: Int) {
            println(" failed ")
        }

        override fun onStopDiscoveryFailed(p0: String?, p1: Int) {
            println(" stop failed ")
        }

    }

    private fun resolveServiceCompat(serviceInfo: NsdServiceInfo) {
        val executor = Executors.newSingleThreadExecutor()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            nsdManager.registerServiceInfoCallback(
                serviceInfo,
                executor,
                object : NsdManager.ServiceInfoCallback {

                    override fun onServiceInfoCallbackRegistrationFailed(p0: Int) {
                        println(" register failed ")
                    }

                    override fun onServiceInfoCallbackUnregistered() {
                        println(" unregistered ")
                    }

                    override fun onServiceLost() {
                        println(" lost ")
                    }

                    override fun onServiceUpdated(nsdServiceInfo: NsdServiceInfo) {
                        println(" updated ")
                        val addresses = nsdServiceInfo.hostAddresses
                        println(" addresses $addresses")
                        val ipAddress = addresses.firstOrNull()?.hostAddress?:return
                        //val port = nsdServiceInfo.port

                        val deviceEntity = DeviceEntity(deviceIPAddress = ipAddress,
                            deviceName = nsdServiceInfo.serviceName,
                            deviceStatus = "ONLINE")
                        lifecycleScope.launch {
                            val updated = withContext(Dispatchers.IO) {
                                database.deviceDao().insertDevices(deviceEntity)
                                database.deviceDao().getAllDevicesList()
                            }
                            deviceAdapter.updateList(updated)
                        }
                    }
                }
            )
        } else {
            nsdManager.resolveService(
                serviceInfo,
                executor,
                object : NsdManager.ResolveListener {

                    override fun onServiceResolved(nsdServiceInfo: NsdServiceInfo) {
                        println(" host "+nsdServiceInfo.host.toString())
                        println(" resolved "+nsdServiceInfo.host.hostAddress.toString())
                        val ipAddress = nsdServiceInfo.host?.hostAddress?:return
                        val port = nsdServiceInfo.port

                        val deviceEntity = DeviceEntity(deviceIPAddress = ipAddress,
                            deviceName = nsdServiceInfo.serviceName,
                            deviceStatus = "ONLINE")
                        lifecycleScope.launch {
                            val updated = withContext(Dispatchers.IO) {
                                database.deviceDao().insertDevices(deviceEntity)
                                database.deviceDao().getAllDevicesList()
                            }
                            deviceAdapter.updateList(updated)
                        }
                    }

                    override fun onResolveFailed(info: NsdServiceInfo, errorCode: Int) {
                        println(" resolve failed ")
                    }
                }
            )
        }
    }


}