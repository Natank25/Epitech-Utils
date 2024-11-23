package io.github.natank25.epitechutils.module

import com.intellij.ide.util.projectWizard.AbstractNewProjectStep.AbstractCallback
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase
import com.intellij.platform.DirectoryProjectGenerator
import io.ktor.util.caseInsensitiveMap


class EpitechProjectSettingsStep(
    projectGenerator: DirectoryProjectGenerator<EpitechProjectSettings?>,
    callback: AbstractCallback<EpitechProjectSettings>?) :
    ProjectSettingsStepBase<EpitechProjectSettings?>(projectGenerator, callback)
