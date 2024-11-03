package io.github.natank25.epitechutils.module;



import com.intellij.ide.util.projectWizard.AbstractNewProjectStep;
import com.intellij.ide.util.projectWizard.ProjectSettingsStepBase;
import com.intellij.platform.DirectoryProjectGenerator;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;

public class EpitechProjectSettingsStep extends ProjectSettingsStepBase<EpitechProjectSettings> {
    public EpitechProjectSettingsStep(DirectoryProjectGenerator<EpitechProjectSettings> projectGenerator) {
        super(projectGenerator, new AbstractNewProjectStep.AbstractCallback<>());
    }
    
    
}
