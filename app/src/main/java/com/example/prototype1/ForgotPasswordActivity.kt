package com.example.prototype1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class ForgotPasswordActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun onReset(v: View){
        var proceed = true
        if(emailET.text.isNullOrEmpty()){
            emailTIL.error = "Email is required"
            emailTIL.isErrorEnabled = true
            proceed = false
        }
        if(proceed){
            loginProgressLayout.visibility = View.VISIBLE
            firebaseAuth.sendPasswordResetEmail(emailET.text.toString())
                    .addOnCompleteListener(this, OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Reset link sent to your email", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Unable to send reset mail", Toast.LENGTH_LONG).show()
                            startActivity(LoginActivity.newIntent(this))
                            finish()
                        }
                    })
        }
    }

    fun goToLogin(v: View){
        startActivity(LoginActivity.newIntent(this))
        finish()
    }

    fun goToSignUp(v: View){
        startActivity(SignUpActivity.newIntent(this))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
    }

    companion object{
        fun newIntent(context: Context) = Intent(context, ForgotPasswordActivity::class.java)
    }
}