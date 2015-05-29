import com.intellij.ide.util.TipDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAware;

public class MyShowTipsAction extends AnAction implements DumbAware {
  private static MyTipDialog ourTipDialog;

  public void actionPerformed(AnActionEvent e) {
    if (ourTipDialog != null && ourTipDialog.isVisible()) {
      ourTipDialog.dispose();
    }
    ourTipDialog = new MyTipDialog();
    ourTipDialog.show();
  }
}