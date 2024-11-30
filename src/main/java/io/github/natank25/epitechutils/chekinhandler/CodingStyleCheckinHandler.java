package io.github.natank25.epitechutils.chekinhandler;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;

import java.io.IOException;
import java.nio.file.Path;

public class CodingStyleCheckinHandler extends CheckinHandler {
	private final Project project;
	private final CheckinProjectPanel panel;
	
	public CodingStyleCheckinHandler(Project project, CheckinProjectPanel panel) {
		this.project = project;
		this.panel = panel;
	}
	
	@Override
	public ReturnResult beforeCheckin() {
		VirtualFile coding_style_report_file = VfsUtil.findFile(Path.of(project.getBasePath(), "coding-style-reports.log"), true);
		if (coding_style_report_file == null || !coding_style_report_file.exists()) {
			Messages.showErrorDialog(project, "The coding style log file does not exists, please execute BANANA to continue", "Commit Blocked");
			return ReturnResult.CANCEL;
		}
		ApplicationManager.getApplication().runWriteAction(() -> {
			try {
				coding_style_report_file.delete(this);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});
		return super.beforeCheckin();
	}
}
