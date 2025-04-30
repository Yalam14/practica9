package guzman.jesus.practica9

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {
    private lateinit var userRef: com.google.firebase.database.DatabaseReference
    private lateinit var listTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa Firebase con URL específica
        val database = FirebaseDatabase.getInstance("https://practica9-68267-default-rtdb.firebaseio.com/")
        userRef = database.getReference("Users")

        // Referencias a las vistas
        listTextView = findViewById(R.id.list_textView)
        val btnSave: Button = findViewById(R.id.save_button)
        val etName: EditText = findViewById(R.id.et_name)
        val etLastName: EditText = findViewById(R.id.et_lastName)
        val etAge: EditText = findViewById(R.id.et_age)

        btnSave.setOnClickListener { saveUser() }

        // Listener para actualizar la lista
        userRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                try {
                    val user = snapshot.getValue(User::class.java)
                    user?.let { updateUserList(it) }
                } catch (e: Exception) {
                    Log.e("FirebaseError", "Error al leer usuario: ${e.message}")
                    snapshot.ref.removeValue() // Elimina datos corruptos
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error de Firebase: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUser() {
        val name = findViewById<EditText>(R.id.et_name).text.toString().trim()
        val lastName = findViewById<EditText>(R.id.et_lastName).text.toString().trim()
        val ageStr = findViewById<EditText>(R.id.et_age).text.toString().trim()

        if (name.isEmpty() || lastName.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val age = try {
            ageStr.toLong() // Intenta convertir a número
        } catch (e: NumberFormatException) {
            ageStr // Si falla, guarda como String
        }

        val user = User(name, lastName, age)

        userRef.push().setValue(user)
            .addOnSuccessListener {
                Toast.makeText(this, "Usuario guardado correctamente", Toast.LENGTH_SHORT).show()
                clearFields()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateUserList(user: User) {
        val currentText = listTextView.text.toString()
        listTextView.text = currentText + user.toString()
    }

    private fun clearFields() {
        findViewById<EditText>(R.id.et_name).text.clear()
        findViewById<EditText>(R.id.et_lastName).text.clear()
        findViewById<EditText>(R.id.et_age).text.clear()
    }
}