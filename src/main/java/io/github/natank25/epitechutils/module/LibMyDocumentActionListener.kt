package io.github.natank25.epitechutils.module

import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener

class LibMyDocumentActionListener(checkValid: Runnable) : DocumentListener {
    private val checkValid: Runnable

    init {
        this.checkValid = checkValid
    }

    override fun changedUpdate(e: DocumentEvent?) {
        checkValid.run()
    }

    override fun insertUpdate(e: DocumentEvent?) {
        checkValid.run()
    }

    override fun removeUpdate(e: DocumentEvent?) {
        checkValid.run()
    }
}
