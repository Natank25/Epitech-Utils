package io.github.natank25.epitechutils.chekinhandler;

import com.intellij.openapi.vcs.CheckinProjectPanel;
import com.intellij.openapi.vcs.changes.CommitContext;
import com.intellij.openapi.vcs.checkin.CheckinHandler;
import com.intellij.openapi.vcs.checkin.CheckinHandlerFactory;
import org.jetbrains.annotations.NotNull;

public class CodingStyleCheckinHandlerFactory extends CheckinHandlerFactory {
	@Override
	public @NotNull CheckinHandler createHandler(@NotNull CheckinProjectPanel checkinProjectPanel, @NotNull CommitContext commitContext) {
		return new CodingStyleCheckinHandler(checkinProjectPanel.getProject(), checkinProjectPanel);
	}
}
