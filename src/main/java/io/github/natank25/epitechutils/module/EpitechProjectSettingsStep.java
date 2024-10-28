package io.github.natank25.epitechutils.module;



import com.intellij.ide.util.projectWizard.AbstractNewProjectStep;
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase;
import com.intellij.platform.DirectoryProjectGenerator;

public class EpitechProjectSettingsStep extends ProjectSettingsStepBase<EpitechProjectSettings> {
    public EpitechProjectSettingsStep(DirectoryProjectGenerator<EpitechProjectSettings> projectGenerator) {
        super(projectGenerator, new AbstractNewProjectStep.AbstractCallback<>());
    }
    
    
}
