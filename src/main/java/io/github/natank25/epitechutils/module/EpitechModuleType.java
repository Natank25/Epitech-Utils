package io.github.natank25.epitechutils.module;

import com.intellij.openapi.module.ModuleType;
import io.github.natank25.epitechutils.Icons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class EpitechModuleType extends ModuleType<EpitechModuleBuilder> {
	
	public static final EpitechModuleType INSTANCE = new EpitechModuleType();
	
	protected EpitechModuleType() {
		super("EpitechModule");
	}
	
	@Override
	public @NotNull EpitechModuleBuilder createModuleBuilder() {
		return new EpitechModuleBuilder();
	}
	
	@Override
	public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getName() {
		return "Epitech Project";
	}
	
	@Override
	public @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String getDescription() {
		return "Epitech project module";
	}
	
	@Override
	public @NotNull Icon getNodeIcon(boolean isOpened) {
		return Icons.EpitechLogo.EpitechIcon_150x150;
	}
	
	
}
