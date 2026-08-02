package com.example.data.repository

import com.example.data.model.SystemStats
import com.example.utils.SystemMonitorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class SystemMonitorRepository(
    private val monitorManager: SystemMonitorManager
) {
    val systemStats: StateFlow<SystemStats> = monitorManager.systemStats

    fun startMonitoring(scope: CoroutineScope) {
        monitorManager.startMonitoring(scope)
    }

    fun stopMonitoring() {
        monitorManager.stopMonitoring()
    }
}
