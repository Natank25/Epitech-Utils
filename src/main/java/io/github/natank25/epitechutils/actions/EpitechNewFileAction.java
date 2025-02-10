package io.github.natank25.epitechutils.actions;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.externalSystem.importing.ImportSpec;
import com.intellij.openapi.externalSystem.importing.ImportSpecBuilder;
import com.intellij.openapi.externalSystem.util.ExternalSystemUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.jetbrains.cidr.cpp.makefile.MakefileUtil;
import com.jetbrains.cidr.cpp.makefile.project.resolver.MkProjectResolverPolicy;
import com.jetbrains.lang.makefile.MakefileFile;
import com.jetbrains.lang.makefile.psi.MakefileVariableAssignment;
import io.github.natank25.epitechutils.files.EpitechTemplates;
import io.github.natank25.epitechutils.icons.EpitechUtilsIcons;
import io.github.natank25.epitechutils.project.EpitechUtilsConfiguration;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;

public class EpitechNewFileAction extends CreateFileFromTemplateAction {
	public static String getRelativePathFromProjectRoot(PsiFile psiFile, Project project) {
		VirtualFile virtualFile = psiFile.getVirtualFile();
		if (virtualFile == null) return null;
		
		String projectBasePath = project.getBasePath();
		if (projectBasePath == null) return null;
		
		String absoluteFilePath = virtualFile.getPath();
		if (absoluteFilePath.startsWith(projectBasePath))
			return absoluteFilePath.substring(projectBasePath.length() + 1);
		else return null;
	}
	
	public static void addFileInMakefile(Project project, PsiFile file, @NotNull String variable) {
		VirtualFile virtualMakefile = VfsUtil.findFile(Path.of(Objects.requireNonNull(project.getBasePath()), "/Makefile"), true);
		if (virtualMakefile == null || !virtualMakefile.exists())
			return;
		
		MakefileFile makefile = new MakefileFile(Objects.requireNonNull(PsiManager.getInstance(project).findViewProvider(virtualMakefile)));
		
		MakefileVariableAssignment[] assignments = makefile.findChildrenByClass(MakefileVariableAssignment.class);
		
		MakefileVariableAssignment targetAssignment = Arrays.stream(assignments)
				.filter(makefileVariableAssignment -> makefileVariableAssignment.getVariable().getName().equals(variable))
				.findFirst()
				.orElse(null);
		
		if (targetAssignment == null) return;
		
		String relativePath = getRelativePathFromProjectRoot(file, project);
		if (relativePath == null) return;
		relativePath = relativePath.replace("src/", "");
		Document document = makefile.getFileDocument();
		
		PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(document);
		
		if (targetAssignment.getVariableValue() == null)
			return;
		String currentValue = targetAssignment.getVariableValue().getText();
		
		String separator;
		if (currentValue.isBlank()) {
			separator = "\t";
			currentValue = variable + " =";
		} else
			separator = "\n" + (variable.equals("SRC") ? "\t\t" : "\t\t\t");
		String substring = currentValue.substring(0, currentValue.length() - 2 - (variable.equals("SRC") ? 2 : 3));
		String updatedValue = substring + separator + relativePath + "\t\\\n\t" + (variable.equals("SRC") ? "\t" : "\t\t") + ')';
		String updatedContent = document.getText().replace(currentValue, updatedValue);
		WriteCommandAction.runWriteCommandAction(project, () -> document.setText(updatedContent));
		PsiDocumentManager.getInstance(project).commitDocument(document);
	}
	
	public static void addCFileInMakefile(Project project, PsiFile file) {
		addFileInMakefile(project, file, "SRC");
	}
	
	public static void addTestFileInMakefile(Project project, PsiFile file) {
		addFileInMakefile(project, file, "TESTS_SRC");
	}
	
	public static Properties createProperties(Project project) {
		Properties properties = FileTemplateManager.getInstance(project).getDefaultProperties();
		EpitechUtilsConfiguration configuration = EpitechUtilsConfiguration.getInstance(project);
		properties.setProperty("EPITECH_PROJECT_NAME", configuration.PROJECT_NAME);
		properties.setProperty("BIN_NAME", configuration.BINARY_NAME);
		return properties;
	}
	
	@Override
	protected void buildDialog(@NotNull Project project, @NotNull PsiDirectory directory, CreateFileFromTemplateDialog.@NotNull Builder builder) {
		builder.setTitle("Epitech File");

		String dirPathFromRoot = directory.getVirtualFile().getPath().replace(project.getBasePath(), "");
		if (dirPathFromRoot.contains("tests")){
			builder.addKind("C test file", EpitechUtilsIcons.EpitechIcon_150x150, EpitechTemplates.C_TEST_FILE, new InputValidator() {
				@Override
				public boolean checkInput(@NlsSafe String s) {
					return s.startsWith("tests_");
				}

				@Override
				public boolean canClose(@NlsSafe String s) {
					return this.checkInput(s);
				}
			});
			builder.addKind("C file", EpitechUtilsIcons.EpitechIcon_150x150, EpitechTemplates.C_FILE);
			builder.addKind("Header file", EpitechUtilsIcons.EpitechIcon_150x150, EpitechTemplates.HEADER_FILE);
		} else if (dirPathFromRoot.contains("include")){
			builder.addKind("Header file", EpitechUtilsIcons.EpitechIcon_150x150, EpitechTemplates.HEADER_FILE);
			builder.addKind("C file", EpitechUtilsIcons.EpitechIcon_150x150, EpitechTemplates.C_FILE);
			builder.addKind("C test file", EpitechUtilsIcons.EpitechIcon_150x150, EpitechTemplates.C_TEST_FILE, new InputValidator() {
				@Override
				public boolean checkInput(@NlsSafe String s) {
					return s.startsWith("tests_");
				}
				
				@Override
				public boolean canClose(@NlsSafe String s) {
					return this.checkInput(s);
				}
			});
		} else {
			builder.addKind("C file", EpitechUtilsIcons.EpitechIcon_150x150, EpitechTemplates.C_FILE);
			builder.addKind("Header file", EpitechUtilsIcons.EpitechIcon_150x150, EpitechTemplates.HEADER_FILE);
			builder.addKind("C test file", EpitechUtilsIcons.EpitechIcon_150x150, EpitechTemplates.C_TEST_FILE, new InputValidator() {
				@Override
				public boolean checkInput(@NlsSafe String s) {
					return s.startsWith("tests_");
				}

				@Override
				public boolean canClose(@NlsSafe String s) {
					return this.checkInput(s);
				}
			});

		}
		builder.addKind("Makefile", EpitechUtilsIcons.EpitechIcon_150x150, EpitechTemplates.MAKEFILE);
		builder.addKind("Gitignore", EpitechUtilsIcons.EpitechIcon_150x150, EpitechTemplates.GITIGNORE);
	}
	
	@Override
	protected PsiFile createFile(String name, String templateName, PsiDirectory dir) {
		Project project = dir.getProject();
		
		ImportSpec importSpec = (new ImportSpecBuilder(project, MakefileUtil.ID)).projectResolverPolicy((new MkProjectResolverPolicy(false))).build();
		ExternalSystemUtil.refreshProject(project.getBasePath(), importSpec);
		
		if (Objects.equals(templateName, EpitechTemplates.MAKEFILE))
			return createMakefile(dir);
		if (Objects.equals(templateName, EpitechTemplates.C_FILE)) {
			Properties properties = new Properties();/*
			CreateNewFunctionDialog createNewFunctionDialog = new CreateNewFunctionDialog(project);
			properties.setProperty("body", createNewFunctionDialog.getFunction().toString());*/
			PsiFile file = EpitechTemplates.createCFileFromTemplate(project, name, dir.getVirtualFile(), properties).getContainingFile();
			addCFileInMakefile(project, file);
			return file;
		}
		if (Objects.equals(templateName, EpitechTemplates.C_TEST_FILE)){
			PsiFile file = EpitechTemplates.createCTestFileFromTemplate(project, name, dir.getVirtualFile(), new Properties()).getContainingFile();
			addTestFileInMakefile(project, file);
			return file;
		}
		return super.createFile(name, templateName, dir);
	}
	
	
	@Override
	protected @NlsContexts.Command String getActionName(PsiDirectory directory, @NonNls @NotNull String newName, @NonNls String templateName) {
		return "Create Epitech " + newName;
	}
	
	private PsiFile createMakefile(PsiDirectory dir) {
		return EpitechTemplates.createMakefileFileFromTemplate(dir.getProject(), dir.getVirtualFile(), "").getContainingFile();
	}
}
