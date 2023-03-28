package com.example.fieldsofinputsnew

import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog.show
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns.EMAIL_ADDRESS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.CheckBox
import android.widget.ProgressBar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
private const val TAG = "TextWatcherTag"

class MainActivity : AppCompatActivity() {
    private lateinit var textInputEditText: TextInputEditText
    private val textWatcher: TextWatcher = object: SimpleTextWatcher(){
        override fun afterTextChanged(s: Editable?) {
            Log.d(TAG,"afterTextChanged $s")
            val input = s.toString()
            if(input.endsWith("@g")){
                Log.d(TAG, "programmatically set text")
                setText("${input}mail.com")
            }
        }
    }
    private fun setText(text: String){
        textInputEditText.removeTextChangedListener(textWatcher)
        textInputEditText.setTextCorrectly(text)
        textInputEditText.addTextChangedListener(textWatcher)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textInputLayout = findViewById<TextInputLayout>(R.id.textInputLayout)
        textInputEditText = textInputLayout.editText as TextInputEditText
        /*textInputEditText.addTextChangedListener(object : SimpleTextWatcher(){
            override fun afterTextChanged(s: Editable?) {
                Log.d("TextWatcherTag","afterTextChanged $s")
                val input = s.toString()
                if(input.endsWith("@g")){
                    Log.d("TextWatcherTag", "programmatically set text")
                    val fullMail = "${input}mail.com"
                    //textInputEditText.setText(fullMail)
                    //textInputEditText.setSelection(fullMail.length)
                    textInputEditText.setTextCorrectly(fullMail)
                }
            }
        })*/
        //textInputEditText.addTextChangedListener(textWatcher)
        //textInputEditText.listenChanges{text->Log.d(TAG,text)}
        textInputEditText.listenChanges{textInputLayout.isErrorEnabled=false}

        val loginButton = findViewById<View>(R.id.loginButton)
        val contentLayout = findViewById<ViewGroup>(R.id.contentLayout)
        val progressBar = findViewById<View>(R.id.progressBar)
        loginButton.setOnClickListener{
            if(EMAIL_ADDRESS.matcher(textInputEditText.text.toString()).matches()){
                /*val imm = this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(textInputEditText.windowToken,0)*/
                hideKeyboard(textInputEditText)
                contentLayout.visibility = View.GONE
                //loginButton.isEnabled=false
                progressBar.visibility=View.VISIBLE
                Snackbar.make(loginButton,"Go to postLogin", Snackbar.LENGTH_LONG).show()
                Handler(Looper.myLooper()!!).postDelayed({
                    contentLayout.visibility = View.VISIBLE
                    progressBar.visibility=View.GONE

                    //val dialog = BottomSheetDialog(this)
                    val dialog = Dialog(this)
                    val view = LayoutInflater.from(this).inflate(R.layout.dialog,contentLayout,false)
                    dialog.setCancelable(false)
                    view.findViewById<View>(R.id.closeButton).setOnClickListener{
                        dialog.dismiss()
                    }
                    dialog.setContentView(view)
                    dialog.show()
                    /*
                    BottomSheetDialog(this).run{
                        setContentView(R.layout.dialog)
                        setCancelable(false)
                        show()
                    }
*/
                },3000)
            }else{
                textInputLayout.isErrorEnabled = true
                textInputLayout.error = getString(R.string.invalid_email_message)
            }
        }

        val checkBox = findViewById<CheckBox>(R.id.checkBox)
        val spannableString = SpannableString(getString(R.string.agreement_full_text))
        //checkBox.text=spannableString

        loginButton.isEnabled=false
        checkBox.setOnCheckedChangeListener{_,isChecked->loginButton.isEnabled=isChecked}
    }
}
fun AppCompatActivity.hideKeyboard(view: View){
    val imm = this.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken,0)
}
private fun TextInputEditText.listenChanges(block: (text: String)->Unit) {
    addTextChangedListener(object : SimpleTextWatcher(){
        override fun afterTextChanged(s: Editable?) {
            block.invoke(s.toString())
        }
    })
}

abstract class SimpleTextWatcher:TextWatcher{
    override fun afterTextChanged(s: Editable?) {

    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }
}

fun TextInputEditText.setTextCorrectly(text: CharSequence){
    setText(text)
    setSelection(text.length)
}