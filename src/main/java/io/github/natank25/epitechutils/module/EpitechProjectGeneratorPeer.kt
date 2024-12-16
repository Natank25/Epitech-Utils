package io.github.natank25.epitechutils.module

import com.intellij.ide.util.projectWizard.SettingsStep
import com.intellij.openapi.Disposable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.Disposer
import com.intellij.platform.ProjectGeneratorPeer
import com.intellij.ui.dsl.builder.COLUMNS_LARGE
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.columns
import com.intellij.ui.dsl.builder.panel
import javax.swing.JComponent
import javax.swing.JTextField

class EpitechProjectGeneratorPeer : ProjectGeneratorPeer<EpitechProjectSettings>, Disposable {
    private val settings = EpitechProjectSettings()
    lateinit var panel: DialogPanel
    private var checkValid: Runnable = Runnable{validate()};

    override fun getComponent(myLocationField: TextFieldWithBrowseButton, checkValid: Runnable): JComponent {
        this.checkValid = checkValid;
        panel = panel {
            row("Github url:") {
                textField()
                    .bindText(settings::gitRepo)
                    .columns(COLUMNS_LARGE)
            }
            row("Binary Name :"){
                textField()
                    .bindText(settings::binName)
                    .onChanged {
                        checkValid()
                    }
            }
            row("Project Name :"){
                textField()
                    .bindText(settings::projectName)
                    .onChanged {
                        checkValid()
                    }
            }
        }
        checkValid()
        return panel
    }


    private fun checkValid() {
        panel.apply()
        checkValid.run()
    }

    override fun buildUI(settingsStep: SettingsStep) {
    }

    override fun getSettings(): EpitechProjectSettings {
        return settings
    }

    override fun validate(): ValidationInfo? {
        return settings.validate()
    }

    override fun isBackgroundJobRunning(): Boolean {
        return false
    }

    override fun dispose() {
    }
}
