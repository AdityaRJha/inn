package com.example.prototype1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.prototype1.util.DATA_USERS
import com.example.prototype1.util.User
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.emailET
import kotlinx.android.synthetic.main.activity_sign_up.emailTIL
import kotlinx.android.synthetic.main.activity_sign_up.passwordET
import kotlinx.android.synthetic.main.activity_sign_up.passwordTIL

class SignUpActivity : AppCompatActivity() {

    private val TAG = "SignUpActivity"

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firebaseAuth.currentUser?.uid
        user?.let {
            startActivity(HomeActivity.newIntent(this))
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        setTextChangeListener(usernameET,usernameTIL)
        setTextChangeListener(emailET, emailTIL)
        setTextChangeListener(passwordET, passwordTIL)

        signUpProgressLayout.setOnTouchListener { v :View, event : MotionEvent -> true  }
    }


    private fun setTextChangeListener(et: EditText, til: TextInputLayout) {
        et.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                til.isErrorEnabled = false
            }

        })
    }

    fun onSignUp(v: View){
        var proceed = true
        if (usernameET.text.isNullOrEmpty()){
            usernameTIL.error = "Username is required"
            usernameTIL.isErrorEnabled = true
            proceed = false
        }
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
        if(confirmPasswordET.text.isNullOrEmpty()){
            confirmPasswordTIL.error ="Password is required"
            confirmPasswordTIL.isErrorEnabled = true
            proceed = false
        }
        if(confirmPasswordET.text.toString() != passwordET.text.toString()){
            confirmPasswordTIL.error = "Password doesn't match"
            confirmPasswordTIL.isErrorEnabled = true
            proceed = false
        }
        if(proceed){
            signUpProgressLayout.visibility = View.VISIBLE
            firebaseAuth.createUserWithEmailAndPassword(emailET.text.toString(), passwordET.text.toString())
                    .addOnCompleteListener { task: Task<AuthResult> ->
                        if (!task.isSuccessful) {
                            signUpProgressLayout.visibility = View.GONE
                            Toast.makeText(this@SignUpActivity, "SignUp error: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                        } else {
                            firebaseAuth.currentUser!!.sendEmailVerification().addOnCompleteListener {
                                task -> if(task.isSuccessful) {
                                    Log.d(TAG,"Email Sent, Verify!!")
                                }
                            }
                            Toast.makeText(this@SignUpActivity, "Please check your email for verification", Toast.LENGTH_SHORT).show()
                            val email = emailET.text.toString()
                            val name = usernameET.text.toString()
                            val user = User(email, name, "", arrayListOf(), arrayListOf())
                            firebaseDB.collection(DATA_USERS).document(firebaseAuth.uid!!).set(user)
                            startActivity(LoginActivity.newIntent(this))
                            finish()
                        }
                        signUpProgressLayout.visibility = View.GONE
                    }.addOnFailureListener{e: Exception ->
                        e.printStackTrace()
                        signUpProgressLayout.visibility = View.GONE
                    }
        }

    }

    fun goToLogin(v: View){
        startActivity(LoginActivity.newIntent(this))
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
        fun newIntent(context: Context) = Intent(context, SignUpActivity::class.java)
    }
}


