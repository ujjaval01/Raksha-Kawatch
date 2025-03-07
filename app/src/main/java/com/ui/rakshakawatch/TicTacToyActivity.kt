package com.ui.rakshakawatch

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class TicTacToyActivity : AppCompatActivity() {
    private var currentPlayer = "X"
    private val board = Array(3) { arrayOfNulls<String>(3) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tic_tac_toy)

        val buttons = listOf(
            findViewById<Button>(R.id.btn1), findViewById<Button>(R.id.btn2), findViewById<Button>(R.id.btn3),
            findViewById<Button>(R.id.btn4), findViewById<Button>(R.id.btn5), findViewById<Button>(R.id.btn6),
            findViewById<Button>(R.id.btn7), findViewById<Button>(R.id.btn8), findViewById<Button>(R.id.btn9)
        )

        for (i in buttons.indices) {
            buttons[i].setOnClickListener {
                onButtonClick(it as Button, i / 3, i % 3)
            }
        }
    }

    private fun onButtonClick(button: Button, row: Int, col: Int) {
        if (button.text.isNotEmpty()) return

        button.text = currentPlayer
        board[row][col] = currentPlayer

        if (checkWinner()) {
            Toast.makeText(this, "$currentPlayer wins!", Toast.LENGTH_LONG).show()
            resetBoard()
            return
        }

        currentPlayer = if (currentPlayer == "X") "O" else "X"
    }

    private fun checkWinner(): Boolean {
        for (i in 0..2) {
            if (board[i][0] != null && board[i][0] == board[i][1] && board[i][1] == board[i][2]) return true
            if (board[0][i] != null && board[0][i] == board[1][i] && board[1][i] == board[2][i]) return true
        }
        if (board[0][0] != null && board[0][0] == board[1][1] && board[1][1] == board[2][2]) return true
        if (board[0][2] != null && board[0][2] == board[1][1] && board[1][1] == board[2][0]) return true

        return false
    }

    private fun resetBoard() {
        val buttons = listOf(
            findViewById<Button>(R.id.btn1), findViewById<Button>(R.id.btn2), findViewById<Button>(R.id.btn3),
            findViewById<Button>(R.id.btn4), findViewById<Button>(R.id.btn5), findViewById<Button>(R.id.btn6),
            findViewById<Button>(R.id.btn7), findViewById<Button>(R.id.btn8), findViewById<Button>(R.id.btn9)
        )

        for (button in buttons) {
            button.text = ""
        }

        for (i in board.indices) {
            for (j in board[i].indices) {
                board[i][j] = null
            }
        }
        currentPlayer = "X"
    }
}