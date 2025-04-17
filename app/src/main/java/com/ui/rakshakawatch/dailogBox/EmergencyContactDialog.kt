package com.ui.rakshakawatch.dailogBox

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import com.ui.rakshakawatch.R

class EmergencyContactDialog(
    context: Context,
    private val contactId: String? = null,
    private val existingName: String? = null,
    private val existingPhone: String? = null,
    private val onSave: (String, String) -> Unit,
    private val onDelete: (() -> Unit)? = null
) {

    private val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_contact, null)
    private val nameEditText: EditText = dialogView.findViewById(R.id.emgName)
    private val phoneEditText: EditText = dialogView.findViewById(R.id.emgPhone)
    private val btnSave: Button = dialogView.findViewById(R.id.btnSave)
    private val btnCancel: Button = dialogView.findViewById(R.id.btnCancel)

    init {
        // Set existing values if provided (for editing)
        existingName?.let { nameEditText.setText(it) }
        existingPhone?.let { phoneEditText.setText(it) }

        val builder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setCancelable(true)

        val dialog = builder.create()

        btnSave.setOnClickListener {
            val name = nameEditText.text.toString()
            val phone = phoneEditText.text.toString()
            onSave(name, phone)
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            if (contactId != null && onDelete != null) {
                onDelete?.let { it1 -> it1() }
            }
            dialog.dismiss()
        }

        dialog.show()
    }
}
