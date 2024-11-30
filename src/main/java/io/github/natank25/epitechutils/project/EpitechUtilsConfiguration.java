package io.github.natank25.epitechutils.project;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Service(Service.Level.PROJECT)
@State(name = "EpitechUtilsConfiguration", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public final class EpitechUtilsConfiguration implements PersistentStateComponent<EpitechUtilsConfiguration> {
	
	public String BINARY_NAME = null;
	public String PROJECT_NAME = null;
	
	@Override
	public @NotNull EpitechUtilsConfiguration getState() {
		return this;
	}
	
	@Override
	public void loadState(@NotNull EpitechUtilsConfiguration epitechUtilsConfiguration) {
		XmlSerializerUtil.copyBean(epitechUtilsConfiguration, this);
	}
	
	public static EpitechUtilsConfiguration getInstance(@NotNull Project project) {
		return project.getService(EpitechUtilsConfiguration.class);
	}
}
