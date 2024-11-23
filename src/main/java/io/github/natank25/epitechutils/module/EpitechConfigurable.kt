package io.github.natank25.epitechutils.module

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import com.intellij.openapi.util.NlsContexts.ConfigurableName
import javax.swing.JComponent
import javax.swing.JLabel

class EpitechConfigurable : Configurable {
    override fun createComponent(): JComponent? {
        return JLabel("MyEpitechNewJLabel") //TODO ProjectSettingsPanel
    }

    override fun isModified(): Boolean {
        return false
    }

    @Throws(ConfigurationException::class)
    override fun apply() {
    }

    override fun getDisplayName(): @ConfigurableName String {
        return "Epitech Config"
    }
}
