package io.github.natank25.epitechutils.codingStyleWindow;

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.ProjectScope;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class CodingStyleToolWindowFactory implements ToolWindowFactory, DumbAware {
	@Override
	public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
		CodingStyleToolWindowContent toolWindowContent = new CodingStyleToolWindowContent(toolWindow, project);
		Content content = ContentFactory.getInstance().createContent(toolWindowContent.getContentPanel(), "", false);
		toolWindow.getContentManager().addContent(content);
	}
	
	private static class CodingStyleToolWindowContent {
		
		
		public static final String CODING_STYLE_REPORTS_LOG = "coding-style-reports.log";
		private final JPanel contentPanel = new JPanel();
		private final JButton generateReport = new JButton("Generate new Coding Style report");
		private final JButton updateWindow = new JButton("Update current view");
		private final JTextArea result = new JTextArea();
		private final Project project;
		
		public CodingStyleToolWindowContent(ToolWindow toolWindow, Project project) {
			project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkFileListener() {
				@Override
				public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
					VFileEvent event = events.stream().filter(vFileEvent -> vFileEvent.getFile().getName().equals(CODING_STYLE_REPORTS_LOG)).findFirst().orElse(null);
					if (event == null || !event.getFile().exists())
						return;
					updateView();
					BulkFileListener.super.after(events);
				}
			});
			this.project = project;
			contentPanel.setLayout(new BorderLayout(0, 20));
			contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
			contentPanel.add(createPanel(), BorderLayout.PAGE_START);
			updateWindow.addActionListener(actionEvent -> updateView());
			contentPanel.add(updateWindow);
			contentPanel.add(result, BorderLayout.CENTER);
		}
		
		public JPanel getContentPanel() {
			return contentPanel;
		}
		
		@NotNull
		private JPanel createPanel() {
			JPanel reportPanel = new JPanel();
			generateReport.addActionListener(actionEvent -> generateReport());
			reportPanel.add(generateReport);
			return reportPanel;
		}
		
		private void generateReport() {
			var dockerRunConfig = RunManager.getInstance(project).findConfigurationByName("Generate Coding Style Report");
			if (dockerRunConfig == null) return;
			ProgramRunnerUtil.executeConfiguration(dockerRunConfig, DefaultRunExecutor.getRunExecutorInstance());
			VfsUtil.findFile(Path.of(project.getBasePath()), true).refresh(false, true);
		}
		
		@Nullable
		private VirtualFile getCodingStyleReport() {
			return FilenameIndex.getVirtualFilesByName(
					CODING_STYLE_REPORTS_LOG,
					ProjectScope.getProjectScope(project)).stream().findFirst().orElse(null);
		}
		
		private String readCodingStyleReport() {
			return readFileContent(getCodingStyleReport());
		}
		
		private String readFileContent(@Nullable VirtualFile file) {
			if (file != null && file.exists()) {
				try {
					return "Result:\n" + new String(file.contentsToByteArray());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			} else {
				return "File not found or is not readable";
			}
		}
		
		private void updateView() {
			result.setText(readCodingStyleReport());
		}
	}
}