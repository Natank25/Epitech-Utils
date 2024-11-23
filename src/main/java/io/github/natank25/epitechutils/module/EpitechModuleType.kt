package io.github.natank25.epitechutils.module

import com.intellij.openapi.module.ModuleType
import io.github.natank25.epitechutils.Icons.EpitechLogo
import org.jetbrains.annotations.Nls
import javax.swing.Icon

class EpitechModuleType protected constructor() : ModuleType<EpitechModuleBuilder?>("EpitechModule") {
    override fun createModuleBuilder(): EpitechModuleBuilder {
        return EpitechModuleBuilder()
    }

    override fun getName(): @Nls(capitalization = Nls.Capitalization.Title) String {
        return "Epitech Project"
    }

    override fun getDescription(): @Nls(capitalization = Nls.Capitalization.Sentence) String {
        return "Epitech project module"
    }

    override fun getNodeIcon(isOpened: Boolean): Icon {
        return EpitechLogo.EpitechIcon_150x150
    }


    companion object {
        val INSTANCE: EpitechModuleType = EpitechModuleType()
    }
}
