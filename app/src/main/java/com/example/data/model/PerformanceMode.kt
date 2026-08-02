package com.example.data.model

enum class PerformanceMode(
    val title: String,
    val description: String,
    val colorHex: Long,
    val targetFps: Int,
    val cpuGovernor: String
) {
    BALANCED(
        title = "Balanced",
        description = "Optimized balance between battery life and gaming performance",
        colorHex = 0xFF00F0FF,
        targetFps = 60,
        cpuGovernor = "schedutil"
    ),
    PERFORMANCE(
        title = "Performance",
        description = "High CPU/RAM priority, smooth framerates for competitive play",
        colorHex = 0xFFFFC700,
        targetFps = 90,
        cpuGovernor = "interactive"
    ),
    ULTRA_PERFORMANCE(
        title = "Ultra Cyber Mode",
        description = "Maximum clock frequency, background process freeze, 120+ FPS target",
        colorHex = 0xFFFF0055,
        targetFps = 120,
        cpuGovernor = "performance"
    ),
    BATTERY_SAVER(
        title = "Eco Saver",
        description = "Reduces thermal footprint and battery drain during extended sessions",
        colorHex = 0xFF00FF66,
        targetFps = 45,
        cpuGovernor = "powersave"
    ),
    CUSTOM(
        title = "Custom Profile",
        description = "User configured per-game settings and custom system limits",
        colorHex = 0xFF9D00FF,
        targetFps = 60,
        cpuGovernor = "user"
    )
}
