open module wargames {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires java.desktop;

    exports edu.ntnu.arunang.wargames;
    exports edu.ntnu.arunang.wargames.gui;
    exports edu.ntnu.arunang.wargames.unit;
    exports edu.ntnu.arunang.wargames.gui.controller;
    exports edu.ntnu.arunang.wargames.gui.factory;
    exports edu.ntnu.arunang.wargames.battle;
    exports edu.ntnu.arunang.wargames.event;
}