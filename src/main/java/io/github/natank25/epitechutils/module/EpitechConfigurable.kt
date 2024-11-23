package io.github.natank25.epitechutils.module;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EpitechConfigurable implements Configurable {
	
	@Override
	public @Nullable JComponent createComponent() {
		return new JLabel("MyEpitechNewJLabel"); //TODO ProjectSettingsPanel
	}
	
	@Override
	public boolean isModified() {
		return false;
	}
	
	@Override
	public void apply() throws ConfigurationException {
	
	}
	
	@Override
	public @NlsContexts.ConfigurableName String getDisplayName() {
		return "Epitech Config";
	}
}
