package io.github.natank25.epitechutils.codingStyleWindow;

import com.intellij.execution.ProgramRunnerUtil;
import com.intellij.execution.RunManager;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.WriteAction;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

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
			result.setEditable(false);
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
			result.setText("Generating report...");
			try {
				WriteAction.run(() -> {
					if (getCodingStyleReport() != null)
						getCodingStyleReport().delete(this);
				});
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			ProgramRunnerUtil.executeConfiguration(dockerRunConfig, DefaultRunExecutor.getRunExecutorInstance());
			CodingStyleReportWatcher.waitForCodingStyleReport(project, this, this::updateView);
		}
		
		private void updateReportView(){
			System.out.println("UpdateReportView");
			VfsUtil.findFile(Path.of(project.getBasePath()), true).refresh(false, true);
			result.setText(readCodingStyleReport());
		}
		
		@Nullable
		private VirtualFile getCodingStyleReport() {
			System.out.println("GetReport");
			return ReadAction.compute(() -> FilenameIndex.getVirtualFilesByName(
					CODING_STYLE_REPORTS_LOG,
					ProjectScope.getProjectScope(project)).stream().findFirst().orElse(null));
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
			updateReportView();
		}
		
		public static class CodingStyleReportWatcher {
			public static void waitForCodingStyleReport(Project project, CodingStyleToolWindowContent window, Runnable on_run_config_ready) {
				ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
				AtomicInteger count = new AtomicInteger();
				executor.scheduleWithFixedDelay(() -> {
					System.out.println(count.get() + " upd");
					System.out.println(window.getCodingStyleReport() + " report exists");
					if (count.get() >= 10 || window.getCodingStyleReport() != null) {
						System.out.println("Found file");
						executor.shutdown();
						on_run_config_ready.run();
					}
					count.addAndGet(1);
				}, 500, 200, TimeUnit.MILLISECONDS);
			}
		}
	}
}
