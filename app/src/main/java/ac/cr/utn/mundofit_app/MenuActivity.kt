package ac.cr.utn.mundofit_app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val sp = getSharedPreferences("UserData", MODE_PRIVATE)
        val name = sp.getString("user_name", "Usuario") ?: ""
        val lastnames = sp.getString("user_lastnames", "") ?: ""
        findViewById<TextView>(R.id.textNameUser).text = "¡Hola, $name $lastnames!"


        findViewById<ImageButton>(R.id.imageButton2).setOnClickListener {
            startActivity(Intent(this, LegRaiseActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton3).setOnClickListener {
            startActivity(Intent(this, PushUpActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton4).setOnClickListener {
            startActivity(Intent(this, PlankActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton5).setOnClickListener {
            startActivity(Intent(this, SidePlankActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton6).setOnClickListener {
            startActivity(Intent(this, OpenPlankActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton7).setOnClickListener {
            startActivity(Intent(this, KneePushUpActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton8).setOnClickListener {
            startActivity(Intent(this, DynamicPlankActivity::class.java))
        }

        findViewById<ImageButton>(R.id.imageButton9).setOnClickListener {
            startActivity(Intent(this, RussianTwistActivity::class.java))
        }
    }
}