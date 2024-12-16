package io.github.natank25.epitechutils.module

import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.util.Weighted
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import io.github.natank25.epitechutils.project.EpitechUtilsConfiguration

class EpitechConfigurable internal constructor(private val project: Project) : BoundConfigurable(
    "Epitech"
), Weighted {

    override fun getWeight(): Double {
        return GROUP_WEIGHT.toDouble()
    }

    override fun createPanel(): DialogPanel {
        val settings = EpitechUtilsConfiguration.getInstance(project)
        return panel {
            row("Binary name :") {
                textField().bindText(settings::BINARY_NAME)
                    .addValidationRule("Binary name can not be empty")
                        {field ->  field.text.isEmpty()}
                    .addValidationRule("Binary name can only contains a-z, A-Z, 0-9 _ . - characters.")
                        {field -> !field.text.matches(Regex("^[a-zA-Z0-9_.-]+\$"))}
            }
            row("Project name :"){
                textField().bindText(settings::PROJECT_NAME)
            }
            row {
                button("Add Coding Style Run Configuration") {
                    EpitechDirectoryProjectGenerator.createCodingStyleRunConfiguration(project);
                }
            }
        }
    }

    companion object {
        private const val GROUP_WEIGHT = 45
    }
}
