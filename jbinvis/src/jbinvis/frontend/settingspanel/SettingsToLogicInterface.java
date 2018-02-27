package jbinvis.frontend.settingspanel;

import java.awt.Component;

/**
 * Allows a render logic to listen for changes in a settings panel
 * @author Billy
 */
public interface SettingsToLogicInterface<Panel extends Component> {
    void attachPanel(Panel settingsPanel);
    void detachPanel();
}
