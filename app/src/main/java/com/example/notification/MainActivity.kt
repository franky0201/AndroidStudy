package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Audio
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // API 33부터 알림 권한 요청을 위한 런처
        val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if(granted) { // 권한이 부여된 경우
                showNotification() // 알림 표시
            } else {
                Toast.makeText(this, "알림 궈한을 허용해주세요!", Toast.LENGTH_SHORT).show()
            }
        }


        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // 특정 버전 이상에서만 권한 요청
                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            } else {
                showNotification()
            }
        }
    }

    private fun showNotification() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder: NotificationCompat.Builder
        val channelId = "one-channel"
        val channelName = "My One Channel"

        // Android 8.0 이상에서만 채널 생성
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "My Channel One Description"
            channel.setShowBadge(true)
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audio = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            channel.setSound(uri, audio)
            channel.enableLights(true)
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            channel.vibrationPattern = longArrayOf(100, 200, 100, 200)

            manager.createNotificationChannel(channel)
        }


        builder = NotificationCompat.Builder(this, channelId)

        builder.setSmallIcon(android.R.drawable.ic_notification_overlay)
        builder.setWhen(System.currentTimeMillis())
        builder.setContentTitle("Title")
        builder.setContentText("message")

        val actionIntent = Intent(this, MainActivity2::class.java)
        val actionPending = PendingIntent.getActivity(
            this, 20, actionIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
        ) // 버전 문제로 프래그 추가
        builder.setContentIntent(actionPending)


        val actionIntent2 = Intent(this, MainActivity2::class.java)
        val actionPending2 = PendingIntent.getActivity(
            this, 21, actionIntent2,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_MUTABLE
        ) // 버전 문제로 프래그 추가
        builder.addAction(
            NotificationCompat.Action.Builder(
                android.R.drawable.stat_notify_more,
                "action",
                actionPending2
            ).build()
        )

        val bigPicture = BitmapFactory.decodeResource(resources, R.drawable.pepe1)
        val style = NotificationCompat.BigPictureStyle()
        style.bigPicture(bigPicture)
        builder.setStyle(style)


        manager.notify(1, builder.build())
    }
}
