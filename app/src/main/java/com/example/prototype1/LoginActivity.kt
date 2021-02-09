    package com.example.prototype1

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser?.uid
        user?.let {
            startActivity(HomeActivity.newIntent(this))
            finish()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        setTextChangeListener(emailET, emailTIL)
        setTextChangeListener(passwordET, passwordTIL)

        loginProgressLayout.setOnTouchListener { v :View, event :MotionEvent  -> true  }

    }

    private fun setTextChangeListener(et: EditText, til: TextInputLayout) {
        et.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                til.isErrorEnabled = false
            }

        })
    }



    fun onLogin(v: View){
        var proceed = true
        if(emailET.text.isNullOrEmpty()){
            emailTIL.error = "Email is required"
            emailTIL.isErrorEnabled = true
            proceed = false
        }
        if(passwordET.text.isNullOrEmpty()){
            passwordTIL.error ="Password is required"
            passwordTIL.isErrorEnabled = true
            proceed = false
        }
        if(proceed){
            loginProgressLayout.visibility = View.VISIBLE
            firebaseAuth.signInWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString())
                .addOnCompleteListener { task: Task<AuthResult> ->
                    if (!task.isSuccessful) {
                        loginProgressLayout.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, "Login error: Either the username or password is wrong.", Toast.LENGTH_SHORT).show()
                    }else{

                        Toast.makeText(this, "Successfully Logged in", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener{e: Exception ->
                    e.printStackTrace()
                    loginProgressLayout.visibility = View.GONE
                }

        }
    }

    fun goToSignUp(v: View){
        startActivity(SignUpActivity.newIntent(this))
        finish()
    }

    fun goToForgotPassword(v: View){
        startActivity(ForgotPasswordActivity.newIntent(this))
        Toast.makeText(this@LoginActivity, "Don't worry your password would be restored.", Toast.LENGTH_SHORT).show()
        finish()
    }

    override fun onStart() {
        super.onStart()
        firebaseAuth.addAuthStateListener { firebaseAuthListener }
    }

    override fun onStop() {
        super.onStop()
        firebaseAuth.removeAuthStateListener { firebaseAuthListener }
    }

    companion object{
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}