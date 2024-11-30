package io.github.natank25.epitechutils.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ex.ActionUtil;
import com.intellij.openapi.externalSystem.action.RefreshExternalProjectAction;
import com.intellij.openapi.externalSystem.importing.ImportSpec;
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder;
import com.intellij.openapi.externalSystem.model.ProjectSystemId;
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.jetbrains.cidr.cpp.makefile.MakefileManager;
import com.jetbrains.cidr.cpp.makefile.MakefileUtil;
import com.jetbrains.cidr.cpp.makefile.MakefileWorkspace;
import com.jetbrains.cidr.cpp.makefile.MakefileWorkspaceProvider;
import com.jetbrains.cidr.cpp.makefile.actions.MakefileRefreshProjectAction;
import com.jetbrains.cidr.cpp.makefile.project.MakefileAutoImportAware;
import com.jetbrains.cidr.cpp.makefile.settings.MakefileProjectSettings;
import com.jetbrains.cidr.cpp.makefile.settings.MakefileSettings;
import com.jetbrains.cidr.cpp.makefile.settings.MakefileSettingsState;
import io.github.natank25.epitechutils.icons.EpitechUtilsIcons;
import io.github.natank25.epitechutils.files.EpitechTemplates;
import org.intellij.lang.xpath.xslt.util.NameValidator;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.awt.event.InputEvent;
import java.util.Objects;
import java.util.Properties;

public class EpitechNewFileAction extends CreateFileFromTemplateAction {
	@Override
	protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.@NotNull Builder builder) {
		
		builder.setTitle("Epitech File");
		builder.addKind("C file", EpitechUtilsIcons.EpitechIcon_150x150, EpitechTemplates.C_FILE);
		builder.addKind("Header file", EpitechUtilsIcons.EpitechIcon_150x150, EpitechTemplates.HEADER_FILE);
		builder.addKind("Makefile", EpitechUtilsIcons.EpitechIcon_150x150, EpitechTemplates.MAKEFILE);
	}
	
	@Override
	protected @NlsContexts.Command String getActionName(PsiDirectory directory, @NonNls @NotNull String newName, @NonNls String templateName) {
		return "Create Epitech " + newName;
	}
	
	private PsiFile createMakefile(PsiDirectory dir){
		return EpitechTemplates.createMakefileFileFromTemplate(dir.getProject(), dir.getVirtualFile(), "").getContainingFile();
	}
	
	@Override
	protected PsiFile createFile(String name, String templateName, PsiDirectory dir) {
		if (Objects.equals(templateName, EpitechTemplates.MAKEFILE))
			return createMakefile(dir);
		return super.createFile(name, templateName, dir);
	}
	
	
	public static Properties createProperties(Project project, String filename){
		Properties properties = FileTemplateManager.getInstance(project).getDefaultProperties();
		properties.setProperty("FILE_NAME", filename);
		return properties;
	}
}
