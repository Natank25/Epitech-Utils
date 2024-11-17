package io.github.natank25.epitechutils.module;

import com.intellij.ide.util.projectWizard.ModuleBuilder;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EpitechModuleBuilder extends ModuleBuilder {
	public @Nullable EpitechProjectSettings configurationData = null;
	public boolean forceGitignore = false;
	
	@Override
	public ModuleType<?> getModuleType() {
		return EpitechModuleType.INSTANCE;
	}
	
	@Override
	public void setupRootModel(@NotNull ModifiableRootModel modifiableRootModel) {
		createProject(modifiableRootModel);
	}
	
	@Override
	public @Nullable ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
		ModuleWizardStep step = new EpitechModuleWizardStep();
		Disposer.register(parentDisposable, step::disposeUIResources);
		return step;
	}
	
	public void createProject(ModifiableRootModel rootModel) {
		System.out.println("EpitechModuleBuilder.java:38");
	}
	
	public class EpitechModuleWizardStep extends ModuleWizardStep {
	
		private final EpitechProjectGeneratorPeer peer = new EpitechProjectGeneratorPeer();
		@Override
		public JComponent getComponent() {
			return peer.getComponent();
		}
		
		
		@Override
		public void updateDataModel() {
			//TODO Update UI
		}
	}
}
