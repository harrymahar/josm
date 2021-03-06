// License: GPL. Copyright 2007 by Immanuel Scholz and others
package org.openstreetmap.josm.tools;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;

import javax.swing.JApplet;

import org.openstreetmap.josm.Main;

/**
 * Helper to open platform web browser on different platforms
 *
 * This now delegates the real work to a platform specific class.
 *
 * @author Imi
 */
public class OpenBrowser {

    private static void displayUrlFallback(URI uri) throws IOException {
        if (Main.platform == null)
            throw new IllegalStateException(tr("Failed to open URL. There is currently no platform set. Please set a platform first."));
        Main.platform.openUrl(uri.toString());
    }

    /**
     * @return <code>null</code> for success or a string in case of an error.
     * @throws IllegalStateException thrown if no platform is set to which opening the URL can be dispatched,
     * {@link Main#platform}
     */
    public static String displayUrl(URI uri) {
        if (Main.applet) {
            try {
                JApplet applet = (JApplet) Main.parent;
                applet.getAppletContext().showDocument(uri.toURL());
                return null;
            } catch (MalformedURLException mue) {
                return mue.getMessage();
            }
        }

        if (Desktop.isDesktopSupported()) {
            try {
                try {
                    Desktop.getDesktop().browse(uri);
                } catch (IOException e) {
                    // Workaround for KDE (Desktop API is severely flawed)
                    // see http://bugs.sun.com/view_bug.do?bug_id=6486393
                    Main.warn("Desktop class failed. Platform dependent fall back for open url in browser.");
                    displayUrlFallback(uri);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        } else {
            try {
                Main.warn("Desktop class is not supported. Platform dependent fall back for open url in browser.");
                displayUrlFallback(uri);
            } catch (IOException e) {
                return e.getMessage();
            }
        }
        return null;
    }

    public static String displayUrl(String url) {
        try {
            return displayUrl(new URI(url));
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
