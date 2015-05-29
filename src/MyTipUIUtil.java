/**
 * @author tomerb
 *         on 29/05/15
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import com.intellij.CommonBundle;
import com.intellij.ide.BrowserUtil;
import com.intellij.ide.IdeBundle;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.util.TipAndTrickBean;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationNamesInfo;
import com.intellij.openapi.extensions.PluginDescriptor;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.keymap.impl.DefaultKeymap;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.ObjectUtils;
import com.intellij.util.ResourceUtil;
import com.intellij.util.ui.UIUtil;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MyTipUIUtil {
    @NonNls
    private static final String SHORTCUT_ENTITY = "&shortcut:";

    private MyTipUIUtil() {
    }

    @NotNull
    public static String getPoweredByText(@NotNull TipAndTrickBean tip) {
        PluginDescriptor descriptor = tip.getPluginDescriptor();
        return descriptor instanceof IdeaPluginDescriptor && !"com.intellij".equals(descriptor.getPluginId().getIdString())?((IdeaPluginDescriptor)descriptor).getName():"";
    }

    public static void openTipInBrowser(String tipFileName, JEditorPane browser, Class providerClass) {
        TipAndTrickBean tip = TipAndTrickBean.findByFileName(tipFileName);
        if(tip == null && StringUtil.isNotEmpty(tipFileName)) {
            tip = new TipAndTrickBean();
            tip.fileName = tipFileName;
        }

        openTipInBrowser(tip, browser);
    }

    public static void openTipInBrowser(@Nullable TipAndTrickBean tip, JEditorPane browser) {
        if(tip != null) {
            try {
                PluginDescriptor e = tip.getPluginDescriptor();
                ClassLoader tipLoader = e == null? MyTipUIUtil.class.getClassLoader():(ClassLoader)ObjectUtils.notNull(e.getPluginClassLoader(), MyTipUIUtil.class.getClassLoader());
                URL url = ResourceUtil.getResource(tipLoader, "/tips/", tip.fileName);
                if(url == null) {
                    setCantReadText(browser, tip);
                    return;
                }

                StringBuffer text = new StringBuffer(ResourceUtil.loadText(url));
                updateShortcuts(text);
                updateImages(text, tipLoader);
                String replaced = text.toString().replace("&productName;", ApplicationNamesInfo.getInstance().getFullProductName());
                String major = ApplicationInfo.getInstance().getMajorVersion();
                replaced = replaced.replace("&majorVersion;", major);
                String minor = ApplicationInfo.getInstance().getMinorVersion();
                replaced = replaced.replace("&minorVersion;", minor);
                replaced = replaced.replace("&majorMinorVersion;", major + ("0".equals(minor)?"":"." + minor));
                replaced = replaced.replace("&settingsPath;", CommonBundle.settingsActionPath());
                if(UIUtil.isUnderDarcula()) {
                    replaced = replaced.replace("css/tips.css", "css/tips_darcula.css");
                }

                browser.read(new StringReader(replaced), url);
            } catch (IOException var9) {
                setCantReadText(browser, tip);
            }

        }
    }

    private static void setCantReadText(JEditorPane browser, TipAndTrickBean bean) {
        try {
            String ignored = getPoweredByText(bean);
            String product = ApplicationNamesInfo.getInstance().getFullProductName();
            if(!ignored.isEmpty()) {
                product = product + " and " + ignored + " plugin";
            }

            String message = IdeBundle.message("error.unable.to.read.tip.of.the.day", new Object[]{bean.fileName, product});
            browser.read(new StringReader(message), (Object)null);
        } catch (IOException var5) {
            ;
        }

    }

    private static void updateImages(StringBuffer text, ClassLoader tipLoader) {
        boolean dark = UIUtil.isUnderDarcula();
        boolean retina = UIUtil.isRetina();
        String suffix = "";
        if(retina) {
            suffix = suffix + "@2x";
        }

        if(dark) {
            suffix = suffix + "_dark";
        }

        for(int index = text.indexOf("<img", 0); index != -1; index = text.indexOf("<img", index + 1)) {
            int end = text.indexOf(">", index + 1);
            if(end == -1) {
                return;
            }

            String img = text.substring(index, end + 1).replace('\r', ' ').replace('\n', ' ');
            int srcIndex = img.indexOf("src=");
            int endIndex = img.indexOf(".png", srcIndex);
            if(endIndex != -1) {
                String path = img.substring(srcIndex + 5, endIndex);
                if(!path.endsWith("_dark") && !path.endsWith("@2x")) {
                    path = path + suffix + ".png";
                    URL url = ResourceUtil.getResource(tipLoader, "/tips/", path);
                    if(url != null) {
                        String newImgTag = "<img src=\"" + path + "\" ";
                        if(retina) {
                            try {
                                BufferedImage ignore = ImageIO.read(url.openStream());
                                int w = ignore.getWidth() / 2;
                                int h = ignore.getHeight() / 2;
                                newImgTag = newImgTag + "width=\"" + w + "\" height=\"" + h + "\"";
                            } catch (Exception var16) {
                                newImgTag = newImgTag + "width=\"400\" height=\"200\"";
                            }
                        }

                        newImgTag = newImgTag + "/>";
                        text.replace(index, end + 1, newImgTag);
                    }
                }
            }
        }

    }

    private static void updateShortcuts(StringBuffer text) {
        int lastIndex = 0;

        while(true) {
            lastIndex = text.indexOf("&shortcut:", lastIndex);
            if(lastIndex < 0) {
                return;
            }

            int actionIdStart = lastIndex + "&shortcut:".length();
            int actionIdEnd = text.indexOf(";", actionIdStart);
            if(actionIdEnd < 0) {
                return;
            }

            String actionId = text.substring(actionIdStart, actionIdEnd);
            String shortcutText = getShortcutText(actionId, KeymapManager.getInstance().getActiveKeymap());
            if(shortcutText == null) {
                Keymap defKeymap = KeymapManager.getInstance().getKeymap(DefaultKeymap.getInstance().getDefaultKeymapName());
                if(defKeymap != null) {
                    shortcutText = getShortcutText(actionId, defKeymap);
                    if(shortcutText != null) {
                        shortcutText = shortcutText + " in default keymap";
                    }
                }
            }

            if(shortcutText == null) {
                shortcutText = "<no shortcut for action " + actionId + ">";
            }

            text.replace(lastIndex, actionIdEnd + 1, shortcutText);
            lastIndex += shortcutText.length();
        }
    }

    @Nullable
    private static String getShortcutText(String actionId, Keymap keymap) {
        Shortcut[] arr$ = keymap.getShortcuts(actionId);
        int len$ = arr$.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Shortcut shortcut = arr$[i$];
            if(shortcut instanceof KeyboardShortcut) {
                return KeymapUtil.getShortcutText(shortcut);
            }
        }

        return null;
    }

    @NotNull
    public static JEditorPane createTipBrowser() {
        JEditorPane browser = new JEditorPane();
        browser.setEditable(false);
        browser.setBackground(UIUtil.getTextFieldBackground());
        browser.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == EventType.ACTIVATED) {
                    BrowserUtil.browse(e.getURL());
                }

            }
        });
        URL resource = ResourceUtil.getResource(MyTipUIUtil.class, "/tips/css/", UIUtil.isUnderDarcula()?"tips_darcula.css":"tips.css");
        final StyleSheet styleSheet = UIUtil.loadStyleSheet(resource);
        HTMLEditorKit kit = new HTMLEditorKit() {
            public StyleSheet getStyleSheet() {
                return styleSheet != null?styleSheet:super.getStyleSheet();
            }
        };
        browser.setEditorKit(kit);
        return browser;
    }
}