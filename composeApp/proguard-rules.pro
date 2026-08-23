# Koin
-keep class org.koin.** { *; }
-keepclassmembers class * {
    @org.koin.core.annotation.KoinInternalApi *;
}

# Kotlin Serialization (si la usas para tu API)
-keepattributes *Annotation*, InnerClasses
-keepnames class kotlinx.serialization.json.** { *; }
-keep class org.pablo.mygas.core.navigation.** { *; }
-keepnames class org.pablo.mygas.core.navigation.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}

# Tus modelos de datos (muy importante para que no cambien los nombres)
-keep class org.pablo.mygas.data.remote.** { *; }
-keep class org.pablo.mygas.data.local.database.models.** { *; }

# Room
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Database class *
