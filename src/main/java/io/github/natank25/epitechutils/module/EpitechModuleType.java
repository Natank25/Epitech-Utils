package io.github.natank25.epitechutils.module;

import com.intellij.ide.util.projectWizard.EmptyModuleBuilder;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.ultimate.UltimateVerifier;
import io.github.natank25.epitechutils.Icons;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

import javax.swing.*;

public class EpitechModuleType extends ModuleType<EmptyModuleBuilder> {
	
	public static final @NonNls String ID = "EpitechModule";
	
	public static @NotNull ModuleType<?> getInstance() {
		return ModuleTypeManager.getInstance().findByID(ID);
	}
	
	protected EpitechModuleType() {
		super(ID);
		UltimateVerifier.getInstance();
	}
	
	@Override
	public @NotNull EmptyModuleBuilder createModuleBuilder() {
		return new EmptyModuleBuilder() {
			public ModuleType<?> getModuleType() {
				return ModuleTypeManager.getInstance().findByID(ID);
			}
		};
	}
	
	@Override
	public @NotNull @Nls(capitalization = Nls.Capitalization.Title) String getName() {
		return "Epitech Project";
	}
	
	@Override
	public @NotNull @Nls(capitalization = Nls.Capitalization.Sentence) String getDescription() {
		return "Epitech Project Description";
	}
	
	@Override
	public @NotNull Icon getNodeIcon(boolean isOpened) {
		return Icons.EpitechIcon;
	}
	
	@Override
	public boolean isSupportedRootType(JpsModuleSourceRootType<?> type) {
		return type != JavaSourceRootType.TEST_SOURCE;
	}
}
