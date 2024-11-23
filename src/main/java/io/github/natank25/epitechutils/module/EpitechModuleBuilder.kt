package io.github.natank25.epitechutils.module

import com.intellij.ide.util.projectWizard.ModuleBuilder
import com.intellij.ide.util.projectWizard.ModuleWizardStep
import com.intellij.ide.util.projectWizard.WizardContext
import com.intellij.openapi.Disposable
import com.intellij.openapi.module.ModuleType
import com.intellij.openapi.roots.ModifiableRootModel
import com.intellij.openapi.util.Disposer
import io.github.natank25.epitechutils.module.EpitechModuleBuilder.EpitechModuleWizardStep
import javax.swing.JComponent

class EpitechModuleBuilder : ModuleBuilder() {
    var configurationData: EpitechProjectSettings? = null
    var forceGitignore: Boolean = false

    override fun getModuleType(): ModuleType<*> {
        return EpitechModuleType.Companion.INSTANCE
    }

    override fun setupRootModel(modifiableRootModel: ModifiableRootModel) {
        createProject(modifiableRootModel)
    }

    override fun getCustomOptionsStep(context: WizardContext?, parentDisposable: Disposable): ModuleWizardStep? {
        val step: ModuleWizardStep = EpitechModuleWizardStep()
        Disposer.register(parentDisposable, Disposable { step.disposeUIResources() })
        return step
    }

    fun createProject(rootModel: ModifiableRootModel?) {
        println("EpitechModuleBuilder.java:38")
    }

    class EpitechModuleWizardStep : ModuleWizardStep() {
        private val peer = EpitechProjectGeneratorPeer()
        override fun getComponent(): JComponent {
            return peer.getComponent()
        }


        override fun updateDataModel() {
            //TODO Update UI
        }
    }
}
