package io.github.natank25.epitechutils.module;

import com.intellij.ide.util.projectWizard.SettingsStep;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.platform.ProjectGeneratorPeer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EpitechProjectGeneratorPeer implements ProjectGeneratorPeer<EpitechProjectSettings> {
	private final EpitechNewProjectPanel epitechNewProjectPanel;
	private volatile JComponent component;
	
	public EpitechProjectGeneratorPeer() {
		epitechNewProjectPanel = new EpitechNewProjectPanel(true);
	}
	
	@Override
	public @NotNull JComponent getComponent() {
		if (component == null) {
			synchronized (this) {
				if (component == null) {
					return component = epitechNewProjectPanel.createPanel();
				}
			}
		}
		return component;
	}
	
	@Override
	public void buildUI(@NotNull SettingsStep settingsStep) {
	
	}
	
	@Override
	public @NotNull EpitechProjectSettings getSettings() {
		return null;
	}
	
	@Override
	public @Nullable ValidationInfo validate() {
		return null;
	}
	
	@Override
	public boolean isBackgroundJobRunning() {
		return false;
	}
}
