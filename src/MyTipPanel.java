/**
 * @author tomerb
 * on 29/05/15
 */

import com.intellij.icons.AllIcons.General;
import com.intellij.ide.GeneralSettings;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.util.TipAndTrickBean;
import com.intellij.ide.util.TipUIUtil;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;

public class MyTipPanel extends JPanel {
    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 200;
    private final JEditorPane myBrowser;
    private final JLabel myPoweredByLabel;
    private final List<TipAndTrickBean> myTips = ContainerUtil.newArrayList();

    public MyTipPanel() {
        this.setLayout(new BorderLayout());
        JLabel jlabel = new JLabel(General.Tip);
        jlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        JLabel label1 = new JLabel(IdeBundle.message("label.did.you.know", new Object[0]));
        Font font = label1.getFont();
        label1.setFont(font.deriveFont(0, (float) (font.getSize() + 4)));
        JPanel jpanel = new JPanel();
        jpanel.setLayout(new BorderLayout());
        jpanel.add(jlabel, "West");
        jpanel.add(label1, "Center");
        jpanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        this.add(jpanel, "North");
        this.myBrowser = TipUIUtil.createTipBrowser();
        JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(this.myBrowser);
        this.add(scrollPane, "Center");
        JPanel southPanel = new JPanel(new BorderLayout());
        JCheckBox showOnStartCheckBox = new JCheckBox(IdeBundle.message("checkbox.show.tips.on.startup", new Object[0]), true);
        showOnStartCheckBox.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        final GeneralSettings settings = GeneralSettings.getInstance();
        showOnStartCheckBox.setSelected(settings.isShowTipsOnStartup());
        showOnStartCheckBox.addItemListener(new ItemListener() {
            public void itemStateChanged(@NotNull ItemEvent e) {
                settings.setShowTipsOnStartup(e.getStateChange() == 1);
            }
        });
        southPanel.add(showOnStartCheckBox, "West");
        this.myPoweredByLabel = new JBLabel();
        this.myPoweredByLabel.setHorizontalAlignment(4);
        this.myPoweredByLabel.setForeground(SimpleTextAttributes.GRAY_ITALIC_ATTRIBUTES.getFgColor());
        southPanel.add(this.myPoweredByLabel, "East");
        this.add(southPanel, "South");
        Collections.addAll(this.myTips, Extensions.getExtensions(TipAndTrickBean.EP_NAME));
    }

    public Dimension getPreferredSize() {
        return new Dimension(400, 200);
    }

    public void prevTip() {
        if (this.myTips.size() == 0) {
            this.myBrowser.setText(IdeBundle.message("error.tips.not.found", new Object[]{ApplicationNamesInfo.getInstance().getFullProductName()}));
        } else {
            GeneralSettings settings = GeneralSettings.getInstance();
            int lastTip = settings.getLastTip();
            --lastTip;
            TipAndTrickBean tip;
            if (lastTip <= 0) {
                tip = (TipAndTrickBean) this.myTips.get(this.myTips.size() - 1);
                lastTip = this.myTips.size();
            } else {
                tip = (TipAndTrickBean) this.myTips.get(lastTip - 1);
            }

            this.setTip(tip, lastTip, this.myBrowser, settings);
        }
    }

    private void setTip(TipAndTrickBean tip, int lastTip, JEditorPane browser, GeneralSettings settings) {
        MyTipUIUtil.openTipInBrowser(tip, browser);
        this.myPoweredByLabel.setText(TipUIUtil.getPoweredByText(tip));
        settings.setLastTip(lastTip);
    }

    public void nextTip() {
        if (this.myTips.size() == 0) {
            this.myBrowser.setText(IdeBundle.message("error.tips.not.found", new Object[]{ApplicationNamesInfo.getInstance().getFullProductName()}));
        } else {
            GeneralSettings settings = GeneralSettings.getInstance();
            int lastTip = settings.getLastTip();
            ++lastTip;
            TipAndTrickBean tip;
            if (lastTip - 1 >= this.myTips.size()) {
                tip = (TipAndTrickBean) this.myTips.get(0);
                lastTip = 1;
            } else {
                tip = (TipAndTrickBean) this.myTips.get(lastTip - 1);
            }

            this.setTip(tip, lastTip, this.myBrowser, settings);
        }
    }
}
