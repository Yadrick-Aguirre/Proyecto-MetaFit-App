package ac.cr.utn.mundofit_app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.concurrent.TimeUnit

class LegRaiseActivity : AppCompatActivity() {

   /* override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_leg_raise)

        findViewById<ImageView>(R.id.gifLegRaise).setImageResource(R.drawable.leg_raise)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
    }
}*/
   private lateinit var tvTimer: TextView
    private lateinit var tvConsejo: TextView
    private lateinit var btnIniciar: Button
    private lateinit var btnPausar: Button
    private lateinit var btnReiniciar: Button
    private lateinit var btnBack: ImageButton

    private var tiempoInicio: Long = 0
    private var tiempoPausado: Long = 0
    private var isRunning = false

    private val handler = Handler(Looper.getMainLooper())
    private val runnableTimer = object : Runnable {
        override fun run() {
            if (isRunning) {
                val millis = System.currentTimeMillis() - tiempoInicio + tiempoPausado
                actualizarCronometro(millis)
                handler.postDelayed(this, 100)
            }
        }
    }

    private val consejos = listOf(
        "Mantén la espalda baja pegada al suelo en todo momento",
        "Eleva las piernas de forma controlada, sin balancearlas",
        "Contrae el abdomen antes de levantar las piernas",
        "Baja las piernas lentamente para mayor efectividad",
        "Evita arquear la espalda al descender",
        "Mantén las piernas juntas durante el movimiento",
        "Respira: sube exhalando, baja inhalando",
        "Si es difícil, flexiona ligeramente las rodillas",
        "Concéntrate en el abdomen, no en la velocidad",
        "¡Excelente ejercicio para fortalecer el core!"
    )

    private var indiceConsejo = 0

    private val runnableConsejo = object : Runnable {
        override fun run() {
            if (isRunning) {
                tvConsejo.text = consejos[indiceConsejo]
                indiceConsejo = (indiceConsejo + 1) % consejos.size
                handler.postDelayed(this, 10000) // Cambia cada 10 segundos
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_knee_push_up)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // VINCULAR VISTAS (AGREGA ESTOS IDs EN TU LAYOUT SOBRE EL GIF)
        tvTimer = findViewById(R.id.tvTimer)
        tvConsejo = findViewById(R.id.tvConsejo)
        btnIniciar = findViewById(R.id.btnIniciar)
        btnPausar = findViewById(R.id.btnPausar)
        btnReiniciar = findViewById(R.id.btnReiniciar)
        btnBack = findViewById(R.id.btnBack)

        // CONSEJO INICIAL
        tvConsejo.text = consejos[0]
        tvTimer.text = "00:00:00"

        btnIniciar.setOnClickListener {
            if (!isRunning) {
                isRunning = true
                tiempoInicio = System.currentTimeMillis()
                handler.post(runnableTimer)
                handler.post(runnableConsejo)
                btnIniciar.isEnabled = false
                btnPausar.isEnabled = true
                btnPausar.text = "Pausar"
            }
        }

        btnPausar.setOnClickListener {
            if (isRunning) {
                isRunning = false
                tiempoPausado += System.currentTimeMillis() - tiempoInicio
                handler.removeCallbacks(runnableTimer)
                handler.removeCallbacks(runnableConsejo)
                btnPausar.text = "Reanudar"
            } else {
                isRunning = true
                tiempoInicio = System.currentTimeMillis()
                handler.post(runnableTimer)
                handler.post(runnableConsejo)
                btnPausar.text = "Pausar"
            }
        }

        btnReiniciar.setOnClickListener {
            isRunning = false
            tiempoPausado = 0
            handler.removeCallbacks(runnableTimer)
            handler.removeCallbacks(runnableConsejo)
            tvTimer.text = "00:00:00"
            indiceConsejo = 0
            tvConsejo.text = consejos[0]
            btnIniciar.isEnabled = true
            btnPausar.isEnabled = false
            btnPausar.text = "Pausar"
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun actualizarCronometro(millis: Long) {
        val horas = TimeUnit.MILLISECONDS.toHours(millis)
        val minutos = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
        val segundos = TimeUnit.MILLISECONDS.toSeconds(millis) % 60

        tvTimer.text = String.format("%02d:%02d:%02d", horas, minutos, segundos)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnableTimer)
        handler.removeCallbacks(runnableConsejo)
    }
}