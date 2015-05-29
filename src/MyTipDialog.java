/**
 * @author tomerb
 *         on 25/05/15
 */

import com.intellij.CommonBundle;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.util.TipPanel;
import com.intellij.internal.statistic.UsageTrigger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.wm.ex.WindowManagerEx;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyTipDialog extends DialogWrapper {
    private TipPanel myTipPanel;

    @Nullable
    protected String getDimensionServiceKey() {
        return this.getClass().getName();
    }

    public MyTipDialog() {
        super(WindowManagerEx.getInstanceEx().findVisibleFrame(), true);
        this.initialize();
    }

    public MyTipDialog(@NotNull Window parent) {
        super(parent, true);
        this.initialize();
    }

    private void initialize() {
        this.setModal(false);
        this.setTitle(IdeBundle.message("title.tip.of.the.day", new Object[0]));
        this.setCancelButtonText(CommonBundle.getCloseButtonText());
        this.myTipPanel = new TipPanel();
        this.myTipPanel.nextTip();
        this.setHorizontalStretch(1.33F);
        this.setVerticalStretch(1.25F);
        this.init();
    }

    @NotNull
    protected Action[] createActions() {
        return new Action[]{new MyTipDialog.PreviousTipAction(), new MyTipDialog.NextTipAction(), this.getCancelAction()};
    }

    protected JComponent createCenterPanel() {
        return this.myTipPanel;
    }

    public void dispose() {
        super.dispose();
    }

    public static MyTipDialog createForProject(Project project) {
        Window w = WindowManagerEx.getInstanceEx().suggestParentWindow(project);
        return w == null?new MyTipDialog():new MyTipDialog(w);
    }

    @Nullable
    public JComponent getPreferredFocusedComponent() {
        return this.myPreferredFocusedComponent;
    }

    private class NextTipAction extends AbstractAction {
        public NextTipAction() {
            super(IdeBundle.message("action.next.tip", new Object[0]));
            this.putValue("DefaultAction", Boolean.TRUE);
            this.putValue("FocusedAction", Boolean.TRUE);
        }

        public void actionPerformed(ActionEvent e) {
            MyTipDialog.this.myTipPanel.nextTip();
            UsageTrigger.trigger("tips.of.the.day.next");
        }
    }

    private class PreviousTipAction extends AbstractAction {
        public PreviousTipAction() {
            super(IdeBundle.message("action.previous.tip", new Object[0]));
        }

        public void actionPerformed(ActionEvent e) {
            MyTipDialog.this.myTipPanel.prevTip();
            UsageTrigger.trigger("tips.of.the.day.prev");
        }
    }
}