package io.github.natank25.epitechutils.actions;

import com.intellij.lang.Language;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.jetbrains.lang.makefile.MakefileFile;
import com.jetbrains.lang.makefile.psi.MakefileVariableAssignment;
import io.github.natank25.epitechutils.EpitechNotifications;
import io.github.natank25.epitechutils.files.EpitechTemplates;
import io.github.natank25.epitechutils.project.EpitechUtilsConfiguration;
import kotlin.collections.EmptyList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BuildLibrary extends AnAction {
	
	private static void createLibraryMakefile(Project project, String libName, VirtualFile headerDir) {
		VirtualFile virtualMakefile = VfsUtil.findFile(Path.of(Objects.requireNonNull(project.getBasePath()), "/Makefile"), true);
		if (virtualMakefile == null || !virtualMakefile.exists())
			return;
		MakefileFile makefile = new MakefileFile(Objects.requireNonNull(PsiManager.getInstance(project).findViewProvider(virtualMakefile)));
		MakefileVariableAssignment[] assignments = makefile.findChildrenByClass(MakefileVariableAssignment.class);
		MakefileVariableAssignment srcVariable = Arrays.stream(assignments)
				.filter(makefileVariableAssignment -> makefileVariableAssignment.getVariable().getName().equals("SRC"))
				.findFirst()
				.orElse(null);
		
		if (srcVariable == null || srcVariable.getVariableValue() == null) return;
		
		String srcFiles = srcVariable.getVariableValue().getText();
		ApplicationManager.getApplication().runWriteAction(() -> {
			EpitechTemplates.createLibraryMakefileFileFromTemplate(project, headerDir.getParent(), libName, srcFiles);
		});
	}
	
	@Override
	public void actionPerformed(AnActionEvent e) {
		Project project = e.getProject();
		if (project == null || project.getBasePath() == null)
			return;
		String libName = getLibName(project);
		if (libName == null) {
			EpitechNotifications.sendNotification(project, "Current project is not suitable for creating a library out of it.", NotificationType.WARNING);
			return;
		}
		
		VirtualFile buildDir = VfsUtil.findFile(Path.of(project.getBasePath(), "build"), true);
		AtomicReference<VirtualFile> headerDir = new AtomicReference<>();
		ApplicationManager.getApplication().runWriteAction(() -> {
			try {
				if (buildDir != null && buildDir.exists()) {
					buildDir.delete(this);
				}
				VfsUtil.createDirectories(Path.of(project.getBasePath(), "build", libName).toString());
				headerDir.set(VfsUtil.createDirectories(Path.of(project.getBasePath(), "build", libName, "include").toString()));
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		});
		EpitechNotifications.sendNotification(project, "Creating library \"" + libName + "\".", NotificationType.INFORMATION);
		copyLibrary(project, libName, headerDir.get());
	}
	
	private void copyCodeFiles(Project project, String libName) {
		
		List<VirtualFile> sources = new ArrayList<>();
		List<String> sourcesPath = null;
		try {
			sourcesPath = parseMakefile(project);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		if (sourcesPath == null) {
			return;
		}
		
		sourcesPath.forEach(path -> {
			VirtualFile file = VfsUtil.findFile(Path.of(project.getBasePath(), path), true);
			if (file == null || !file.exists()) {
				System.out.println("Source file \"" + path + "\" could not be found.");
				EpitechNotifications.sendNotification(project, "Source file \"" + path + "\" could not be found.", NotificationType.ERROR);
				return;
			}
			sources.add(file);
		});
		
		sources.forEach(virtualFile -> {
			String relativePath = virtualFile.getPath().replace(project.getBasePath(), "");
			String relativeParentPath = "/build/" + libName + relativePath.replace(virtualFile.getName(), "");
			String formattedRelativeParentPath = relativeParentPath.substring(0, relativeParentPath.length() - 1);
			ApplicationManager.getApplication().runWriteAction(() -> {
				VirtualFile dir;
				try {
					dir = VfsUtil.createDirectoryIfMissing(project.getBasePath() + formattedRelativeParentPath);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				if (dir == null){
					dir = VfsUtil.findFile(Path.of(project.getBasePath() + formattedRelativeParentPath), true);
				}
				try {
					System.out.println("Copying \"" + virtualFile.getPath()  + "\" into \"" + dir.getPath() + "\"");
					VfsUtil.copy(this, virtualFile, dir);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
		});
	}
	
	private void copyHeaderFile(Project project, String libName, VirtualFile headerDir) {
		if (headerDir == null || !headerDir.exists())
			return;
		VirtualFile sourceHeaderDir = VfsUtil.findFile(Path.of(project.getBasePath(), "include"), true);
		
		if (sourceHeaderDir == null || !sourceHeaderDir.exists())
			return;
		ApplicationManager.getApplication().runWriteAction(() -> {
			for (VirtualFile child : sourceHeaderDir.getChildren()) {
				try {
					VfsUtil.copy(this, child, headerDir);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
	
	private void copyLibrary(Project project, String libName, VirtualFile headerDir) {
		copyCodeFiles(project, libName);
		copyHeaderFile(project, libName, headerDir);
		createLibraryMakefile(project, libName, headerDir);
	}
	
	private String getLibName(Project project) {
		String binName = EpitechUtilsConfiguration.getInstance(project).BINARY_NAME;
		if (!binName.endsWith(".a") || !binName.startsWith("lib"))
			return null;
		return binName.replace("lib", "").replace(".a", "");
	}
	
	public List<String> parseMakefile(Project project) throws IOException {
		
		// Locate the Makefile
		VirtualFile virtualMakefile = VfsUtil.findFile(Path.of(Objects.requireNonNull(project.getBasePath()), "/Makefile"), true);
		if (virtualMakefile == null || !virtualMakefile.exists())
			throw new IllegalArgumentException("Makefile could not be found");
		
		// Extract the SRC variable value
		String srcVariable = extractVariable(project, virtualMakefile);
		if (srcVariable == null) throw new IllegalArgumentException("SRC variable not found in Makefile");
		
		// Resolve the paths
		return parseSrcVariable(project, srcVariable);
	}
	
	private List<String> parseSrcVariable(Project project, String srcVariable) {
		ArrayList<String> list = new ArrayList<>();
		List<String> srcList = Arrays.stream(srcVariable.split(" ")).toList();
		List<String> prefixes = new ArrayList<>();
		for (String src : srcList) {
			
			if (src.startsWith("$(addprefix")) {
				String name = src.replace("$(addprefix", "");
				prefixes.add(name.substring(0, name.length() - 1));
				continue;
			}
			String prefix = String.join("", prefixes);
			String path = src.replace(")", "");
			int parenthesisCount = src.length() - src.replace(")", "").length();
			for (int i = 0; i < parenthesisCount; i++)
				prefixes.removeLast();
			if (path.isBlank())
				continue;
			list.add(prefix + path);
		}
		return list;
	}
	
	private String extractVariable(Project project, VirtualFile virtualMakefile) {
		if (virtualMakefile == null || !virtualMakefile.exists())
			return null;
		MakefileFile makefile = new MakefileFile(Objects.requireNonNull(PsiManager.getInstance(project).findViewProvider(virtualMakefile)));
		MakefileVariableAssignment[] assignments = makefile.findChildrenByClass(MakefileVariableAssignment.class);
		MakefileVariableAssignment srcVariable = Arrays.stream(assignments)
				.filter(makefileVariableAssignment -> makefileVariableAssignment.getVariable().getName().equals("SRC"))
				.findFirst()
				.orElse(null);
		
		if (srcVariable == null || srcVariable.getVariableValue() == null) return null;
		String rawVar = srcVariable.getVariableValue().getText();
		return rawVar.replaceAll("\\s", "").replace("\\", " ");
	}
}
