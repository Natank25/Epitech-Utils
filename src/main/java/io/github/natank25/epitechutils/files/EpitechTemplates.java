package io.github.natank25.epitechutils.files;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import io.github.natank25.epitechutils.actions.EpitechNewFileAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Properties;

public class EpitechTemplates {
	public static final String C_FILE = "Epitech C File.c";
	public static final String HEADER_FILE = "Epitech Header File.h";
	public static final String MAKEFILE = "Epitech Makefile.mk";
	public static final String GITIGNORE = ".gitignore";
	public static final String C_TEST_FILE = "Epitech C Test File.c";
	public static final String LIBRARY_MAKEFILE = "Library Makefile.mk";
	
	public static @NotNull PsiElement createFromTemplate(Project project, String fileName, PsiDirectory dir, Templates template, @Nullable Properties properties){
		Properties defaultProperties = EpitechNewFileAction.createProperties(project, fileName);
		if (properties != null)
			defaultProperties.putAll(properties);
		try {
			FileTemplate internalTemplate = FileTemplateManager.getInstance(project).getInternalTemplate(template.toString());
			internalTemplate.setLiveTemplateEnabled(true);
			return FileTemplateUtil.createFromTemplate(
					internalTemplate,
					fileName,
					defaultProperties,
					dir);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static @NotNull PsiElement createFromTemplate(Project project, String fileName, PsiDirectory dir, Templates template){
		return createFromTemplate(project, fileName,dir, template, null);
	}
	
	public static @NotNull PsiElement createFromTemplate(Project project, String fileName, VirtualFile dir, Templates template){
		return createFromTemplate(project, fileName, PsiManager.getInstance(project).findDirectory(dir), template);
	}
	
	public static @NotNull PsiElement createCFileFromTemplate(Project project, String fileName, VirtualFile dir){
		return createFromTemplate(project, fileName, PsiManager.getInstance(project).findDirectory(dir), Templates.C_FILE);
	}
	
	public static @NotNull PsiElement createCFileFromTemplate(Project project, String fileName, VirtualFile dir, Properties properties){
		return createFromTemplate(project, fileName, PsiManager.getInstance(project).findDirectory(dir), Templates.C_FILE, properties);
	}
	
	public static @NotNull PsiElement createHeaderFileFromTemplate(Project project, String fileName, VirtualFile dir){
		return createFromTemplate(project, fileName, PsiManager.getInstance(project).findDirectory(dir), Templates.HEADER_FILE);
	}
	
	public static @NotNull PsiElement createGitignoreFileFromTemplate(Project project, VirtualFile dir, String binary_name){
		Properties gitignorProperties = new Properties();
		gitignorProperties.setProperty("BIN_NAME", binary_name);
		return createFromTemplate(project, ".gitignore", PsiManager.getInstance(project).findDirectory(dir), Templates.GITIGNORE, gitignorProperties);
	}
	
	public static @NotNull PsiElement createMakefileFileFromTemplate(Project project, VirtualFile dir, String binary_name){
		Properties makefileProperties = new Properties();
		makefileProperties.setProperty("BIN_NAME", binary_name);
		PsiElement file = createFromTemplate(project, "Makefile", PsiManager.getInstance(project).findDirectory(dir), Templates.MAKEFILE, makefileProperties);
		((PsiFile) file).setName("Makefile");
		return file;
	}
	
	public static @NotNull PsiElement createLibraryMakefileFileFromTemplate(Project project, VirtualFile dir, String libraryName, String srcFiles){
		Properties makefileProperties = new Properties();
		makefileProperties.setProperty("LIB_NAME", libraryName);
		makefileProperties.setProperty("SRC_FILES", srcFiles);
		PsiElement file = createFromTemplate(project, "Makefile", PsiManager.getInstance(project).findDirectory(dir), Templates.LIBRARY_MAKEFILE, makefileProperties);
		((PsiFile) file).setName("Makefile");
		return file;
	}
	
	public enum Templates{
		C_FILE(EpitechTemplates.C_FILE),
		HEADER_FILE(EpitechTemplates.HEADER_FILE),
		MAKEFILE(EpitechTemplates.MAKEFILE),
		GITIGNORE(EpitechTemplates.GITIGNORE),
		C_TEST_FILE(EpitechTemplates.C_TEST_FILE),
		LIBRARY_MAKEFILE(EpitechTemplates.LIBRARY_MAKEFILE);
		
		private final String filename;
		
		Templates(final String name){
			this.filename = name;
		}
		
		@Override
		public String toString() {
			return this.filename;
		}
	}
}
