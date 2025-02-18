package com.ui.rakshakawatch

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ui.rakshakawatch.R

class DemoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_demo)

        val firstNum = findViewById<EditText>(R.id.firstNum)
        val secondNum = findViewById<EditText>(R.id.scndNum)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val addRadio = findViewById<RadioButton>(R.id.plus)
        val subtractRadio = findViewById<RadioButton>(R.id.minus)
        val multiplyRadio = findViewById<RadioButton>(R.id.mul)
        val divideRadio = findViewById<RadioButton>(R.id.div)
        val submitBtn = findViewById<Button>(R.id.submitBtn)
        val result = findViewById<EditText>(R.id.result)

        submitBtn.setOnClickListener {
            val num1 = firstNum.text.toString().toDoubleOrNull()
            val num2 = secondNum.text.toString().toDoubleOrNull()

            if (num1 == null || num2 == null) {
                Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedOperation = radioGroup.checkedRadioButtonId
            val output = when (selectedOperation) {
                addRadio.id -> num1 + num2
                subtractRadio.id -> num1 - num2
                multiplyRadio.id -> num1 * num2
                divideRadio.id -> {
                    if (num2 == 0.0) {
                        Toast.makeText(this, "Cannot divide by zero", Toast.LENGTH_SHORT).show()
                        return@setOnClickListener
                    }
                    num1 / num2
                }
                else -> 0.0
            }

            result.setText(output.toString())
        }
    }
}
