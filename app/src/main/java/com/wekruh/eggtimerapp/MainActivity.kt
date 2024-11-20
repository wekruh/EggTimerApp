package com.wekruh.eggtimerapp

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AsyncPlayer
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.wekruh.eggtimerapp.databinding.ActivityMainBinding

const val  CHANNEL_ID = "chanelId"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var progressBar: ProgressBar
    private var activeTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        progressBar = findViewById(R.id.progressBar)
    }

    private fun sendNotification(title: String, message: String) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
                return
            }
            notify(1, builder.build())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Notification Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun visibleOpr1(view: View) {
        binding.setTimerBtn.visibility = View.INVISIBLE
        binding.stopTimerBtn.visibility = View.INVISIBLE
        binding.appNameText.visibility = View.INVISIBLE
        binding.changeBtn.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        binding.progressBarBackGround.visibility = View.INVISIBLE
        binding.timerText.visibility = View.INVISIBLE
        binding.boiledEgg.visibility = View.VISIBLE
        binding.eggTypeText.visibility = View.VISIBLE
        binding.btn2min.visibility = View.VISIBLE
        binding.btn4min.visibility = View.VISIBLE
        binding.btn6min.visibility = View.VISIBLE
        binding.btn8min.visibility = View.VISIBLE
        binding.btn10min.visibility = View.VISIBLE
        binding.btn15min.visibility = View.VISIBLE
    }

    fun visibleOpr2(view: View) {
        binding.setTimerBtn.visibility = View.VISIBLE
        binding.appNameText.visibility = View.VISIBLE
        binding.stopTimerBtn.visibility = View.INVISIBLE
        binding.boiledEgg.visibility = View.INVISIBLE
        binding.eggTypeText.visibility = View.INVISIBLE
        binding.btn2min.visibility = View.INVISIBLE
        binding.btn4min.visibility = View.INVISIBLE
        binding.btn6min.visibility = View.INVISIBLE
        binding.btn8min.visibility = View.INVISIBLE
        binding.btn10min.visibility = View.INVISIBLE
        binding.btn15min.visibility = View.INVISIBLE
        binding.timerText.visibility = View.INVISIBLE
        binding.progressBar.visibility = View.INVISIBLE
        binding.progressBarBackGround.visibility = View.INVISIBLE
        binding.changeBtn.visibility = View.INVISIBLE
    }

    fun visibleOpr3(view: View) {
        binding.setTimerBtn.visibility = View.INVISIBLE
        binding.boiledEgg.visibility = View.INVISIBLE
        binding.stopTimerBtn.visibility = View.INVISIBLE
        binding.eggTypeText.visibility = View.INVISIBLE
        binding.btn2min.visibility = View.INVISIBLE
        binding.btn4min.visibility = View.INVISIBLE
        binding.btn6min.visibility = View.INVISIBLE
        binding.btn8min.visibility = View.INVISIBLE
        binding.btn10min.visibility = View.INVISIBLE
        binding.btn15min.visibility = View.INVISIBLE
        binding.timerText.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
        binding.progressBarBackGround.visibility = View.VISIBLE
        binding.changeBtn.visibility = View.VISIBLE
        binding.stopTimerBtn.visibility = View.VISIBLE
    }

    fun makeProgressBarInvisible(progressBar: ProgressBar) {
        binding.progressBar.visibility = View.INVISIBLE
        binding.progressBarBackGround.visibility = View.INVISIBLE
    }

    fun startAlarm(view: View) {
        if (!this::mediaPlayer.isInitialized) {
            mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound)
        }
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.seekTo(0)
        }
        mediaPlayer.start()
    }

    fun startCountdownTimer(
        durationInMillis: Long,
        intervalInMillis: Long,
        onTick: (remainingTimeInMillis: Long) -> Unit,
        onFinish: () -> Unit
    ): CountDownTimer {
        val timer = object : CountDownTimer(durationInMillis, intervalInMillis) {
            override fun onTick(millisUntilFinished: Long) {
                onTick(millisUntilFinished)
            }

            override fun onFinish() {
                onFinish()
                sendNotification("EggTimer", "Your egg is ready!")
            }
        }.start()

        activeTimer = timer
        return timer
    }

    fun setTimer(view: View) {
        visibleOpr1(view)
    }

    fun btn2minC(view: View) {
        visibleOpr3(view)

        val totalDurationInMillis = 120000L
        progressBar.max = (totalDurationInMillis / 1000).toInt()

        val timer = startCountdownTimer(
            durationInMillis = totalDurationInMillis,
            intervalInMillis = 1000,
            onTick = { remainingTime ->
                val minutes = (remainingTime / 1000) / 60
                val seconds = (remainingTime / 1000) % 60
                val timeFormatted = String.format("%02d:%02d", minutes, seconds)
                binding.timerText.text = timeFormatted

                val elapsedTime = (totalDurationInMillis - remainingTime) / 1000
                progressBar.progress = elapsedTime.toInt()
            },
            onFinish = {
                startAlarm(view)
                sendNotification("Egg Timer", "Your egg is ready!")
                makeProgressBarInvisible(progressBar)
                binding.timerText.text = "Time's Up!"
                progressBar.progress = progressBar.max
            }
        )
    }

    fun btn4minC(view: View) {
        visibleOpr3(view)

        val totalDurationInMillis = 240000L
        progressBar.max = (totalDurationInMillis / 1000).toInt()

        val timer = startCountdownTimer(
            durationInMillis = totalDurationInMillis,
            intervalInMillis = 1000,
            onTick = { remainingTime ->
                val minutes = (remainingTime / 1000) / 60
                val seconds = (remainingTime / 1000) % 60
                val timeFormatted = String.format("%02d:%02d", minutes, seconds)
                binding.timerText.text = timeFormatted

                val elapsedTime = (totalDurationInMillis - remainingTime) / 1000
                progressBar.progress = elapsedTime.toInt()
            },
            onFinish = {
                startAlarm(view)
                sendNotification("EggTimer", "Your egg is ready!")
                makeProgressBarInvisible(progressBar)
                binding.timerText.text = "Time's Up!"
                progressBar.progress = progressBar.max
            }
        )
    }

    fun btn6minC(view: View) {
        visibleOpr3(view)

        val totalDurationInMillis = 360000L
        progressBar.max = (totalDurationInMillis / 1000).toInt()

        val timer = startCountdownTimer(
            durationInMillis = totalDurationInMillis,
            intervalInMillis = 1000,
            onTick = { remainingTime ->
                val minutes = (remainingTime / 1000) / 60
                val seconds = (remainingTime / 1000) % 60
                val timeFormatted = String.format("%02d:%02d", minutes, seconds)
                binding.timerText.text = timeFormatted

                val elapsedTime = (totalDurationInMillis - remainingTime) / 1000
                progressBar.progress = elapsedTime.toInt()
            },
            onFinish = {
                startAlarm(view)
                sendNotification("EggTimer", "Your egg is ready!")
                makeProgressBarInvisible(progressBar)
                binding.timerText.text = "Time's Up!"
                progressBar.progress = progressBar.max
            }
        )
    }

    fun btn8minC(view: View) {
        visibleOpr3(view)

        val totalDurationInMillis = 480000L
        progressBar.max = (totalDurationInMillis / 1000).toInt()

        val timer = startCountdownTimer(
            durationInMillis = totalDurationInMillis,
            intervalInMillis = 1000,
            onTick = { remainingTime ->
                val minutes = (remainingTime / 1000) / 60
                val seconds = (remainingTime / 1000) % 60
                val timeFormatted = String.format("%02d:%02d", minutes, seconds)
                binding.timerText.text = timeFormatted

                val elapsedTime = (totalDurationInMillis - remainingTime) / 1000
                progressBar.progress = elapsedTime.toInt()
            },
            onFinish = {
                startAlarm(view)
                sendNotification("EggTimer", "Your egg is ready!")
                makeProgressBarInvisible(progressBar)
                binding.timerText.text = "Time's Up!"
                progressBar.progress = progressBar.max
            }
        )
    }

    fun btn10minC(view: View) {
        visibleOpr3(view)

        val totalDurationInMillis = 600000L
        progressBar.max = (totalDurationInMillis / 1000).toInt()

        val timer = startCountdownTimer(
            durationInMillis = totalDurationInMillis,
            intervalInMillis = 1000,
            onTick = { remainingTime ->
                val minutes = (remainingTime / 1000) / 60
                val seconds = (remainingTime / 1000) % 60
                val timeFormatted = String.format("%02d:%02d", minutes, seconds)
                binding.timerText.text = timeFormatted

                val elapsedTime = (totalDurationInMillis - remainingTime) / 1000
                progressBar.progress = elapsedTime.toInt()
            },
            onFinish = {
                startAlarm(view)
                sendNotification("EggTimer", "Your egg is ready!")
                makeProgressBarInvisible(progressBar)
                binding.timerText.text = "Time's Up!"
                progressBar.progress = progressBar.max
            }
        )
    }

    fun btn15minC(view: View) {
        visibleOpr3(view)

        val totalDurationInMillis = 900000L
        progressBar.max = (totalDurationInMillis / 1000).toInt()

        val timer = startCountdownTimer(
            durationInMillis = totalDurationInMillis,
            intervalInMillis = 1000,
            onTick = { remainingTime ->
                val minutes = (remainingTime / 1000) / 60
                val seconds = (remainingTime / 1000) % 60
                val timeFormatted = String.format("%02d:%02d", minutes, seconds)
                binding.timerText.text = timeFormatted

                val elapsedTime = (totalDurationInMillis - remainingTime) / 1000
                progressBar.progress = elapsedTime.toInt()
            },
            onFinish = {
                startAlarm(view)
                sendNotification("EggTimer", "Your egg is ready!")
                makeProgressBarInvisible(progressBar)
                binding.timerText.text = "Time's Up!"
                progressBar.progress = progressBar.max
            }
        )
    }

    fun changeTimer(view: View) {
        activeTimer?.cancel()

        visibleOpr1(view)
    }

    fun stopTimer(view: View) {
        activeTimer?.cancel()
        visibleOpr2(view)
    }
}