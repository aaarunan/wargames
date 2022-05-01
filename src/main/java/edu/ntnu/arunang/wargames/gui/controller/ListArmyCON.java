package edu.ntnu.arunang.wargames.gui.controller;

import edu.ntnu.arunang.wargames.Army;
import edu.ntnu.arunang.wargames.fsh.ArmyFSH;
import edu.ntnu.arunang.wargames.fsh.FileFormatException;
import edu.ntnu.arunang.wargames.gui.ArmySingleton;
import edu.ntnu.arunang.wargames.gui.GUI;
import edu.ntnu.arunang.wargames.gui.decorator.ButtonDecorator;
import edu.ntnu.arunang.wargames.gui.decorator.TextDecorator;
import edu.ntnu.arunang.wargames.gui.factory.*;
import edu.ntnu.arunang.wargames.unit.Unit;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

/**
 * Controller for pages where armies are listed.
 */

public class ListArmyCON {
    @FXML
    private VBox armyContainer;
    @FXML
    private Text txtArmyName;
    @FXML
    private VBox detailsWindow;
    @FXML
    private TableView<Unit> tableUnits;
    @FXML
    private Text title;
    @FXML
    private HBox armyDetails;
    @FXML
    private BorderPane borderPane;

    private Army army;
    private final ArmySingleton armySingleton = ArmySingleton.getInstance();

    private Text errorMsg;
    private HBox bottomBar;
    private Button btnPressedArmy;
    private boolean isAttacker;

    /**
     * Updates the details container. If the window is disabled it will be set to enabled.
     * It creates a detailed gridPane of the army stats, and a tableview of with every unit.
     */

    void repaintDetails() {
        if (!detailsWindow.isVisible()) {
            detailsWindow.setVisible(true);
        }

        //update the army fields
        txtArmyName.setText(army.getName());
        armyDetails.getChildren().clear();
        armyDetails.getChildren().add(ContainerFactory.createArmyPane(army));
        army.getUnits().forEach(unit -> tableUnits.getItems().add(unit));
    }

    /**
     * Update the army list.
     *
     * @param armyFiles list of armies
     */

    void repaintArmies(File[] armyFiles) {
        //clear the container before adding
        armyContainer.getChildren().clear();
        if (armyFiles == null) {
            return;
        }
        ArmyFSH armyFSH = new ArmyFSH();

        //loop through all the armies
        for (File file : armyFiles) {
            Button button = ButtonFactory.listButton(file.getName().substring(0, file.getName().lastIndexOf('.')));
            button.setOnAction(buttonEvent -> {
                try {
                    this.army = armyFSH.loadFromFile(file);
                } catch (IOException e) {
                    AlertFactory.createError("Army could not be loaded...\n" + e.getMessage());
                } catch (FileFormatException e) {
                    AlertFactory.createError("Army is wrongly formatted! \n" + e.getMessage());
                }
                updatePressed(button);
                repaintDetails();
            });
            armyContainer.getChildren().add(button);
        }
    }

    /**
     * Changes the status when an army is pressed.
     * When the army is selected it will be highlighted, and the previous
     * selected army will be set to default styling.
     *
     * @param button army that was pressed.
     */

    void updatePressed(Button button) {
        if (btnPressedArmy != null && !isAttacker) {
            ButtonDecorator.makeListElementDefault(btnPressedArmy);
        }

        if (!army.equals(armySingleton.getAttacker())) {
            ButtonDecorator.makeListElementHighlighted(button);
        }

        btnPressedArmy = button;
        isAttacker = army.equals(armySingleton.getAttacker());
    }

    /**
     * Update the header. This is according to whether simulation has been choosen
     * and the attacker is not null.
     */

    void repaintHeader() {
        if (armySingleton.isSimulate()) {
            if (armySingleton.getAttacker() == null) {
                title.setText("Choose attacker");
            } else {
                title.setText("Choose defender");
            }
        }
    }

    /**
     * Initialize the bottom bar. Buttons will be added according to whether simulation has been
     * choosen.
     */

    void initBottomBar() {
        HBox bottomBar = NavbarFactory.createBottomBar();
        Button btnAction;

        //Change the buttons to the circumstances
        if (!armySingleton.isSimulate()) {
            btnAction = ButtonFactory.createDefaultButton("New army");
            btnAction.setOnAction(event -> GUI.setSceneFromActionEvent(event, "newArmy"));
        } else {
            btnAction = ButtonFactory.createDefaultButton("Continue");
            btnAction.setOnAction(this::onContinue);
        }

        //initialize error message
        errorMsg = TextFactory.createSmallText("Info: army can be double-clicked");

        //create back button
        Button btnBack = ButtonFactory.createDefaultButton("Back");
        btnBack.setOnAction(event -> GUI.setSceneFromActionEvent(event, "main"));

        //add the buttons to the bottom bar
        bottomBar.getChildren().addAll(errorMsg, btnBack, btnAction);
        borderPane.setBottom(bottomBar);
    }

    /**
     * Method called when the user presses the continue button. It sets the attacking army
     * and defending army.
     *
     * @param event triggered by continue button
     */

    void onContinue(ActionEvent event) {
        if (army == null) {
            TextDecorator.makeErrorText(errorMsg);
            errorMsg.setText("Choose an army");
            return;
        }

        if (armySingleton.getAttacker() == null) {
            armySingleton.setAttacker(army);
            ButtonDecorator.makeListElementActive(btnPressedArmy);
            btnPressedArmy.setText("(Attacker) " + btnPressedArmy.getText());
            btnPressedArmy = null;
        } else {
            armySingleton.setDefender(army);
            GUI.setSceneFromActionEvent(event, "simulate");
        }
        repaintHeader();
        detailsWindow.setVisible(false);
        army = null;
    }

    /**
     * Deletes an Army from the Singleton and in the stored files in /resources/army folder.
     * The method will show a pop-up warning that the user must confirm.
     */

    @FXML
    void onDelete() {
        ArmyFSH armyFSH = new ArmyFSH();
        //get the result of the popup
        Optional<ButtonType> result = AlertFactory.createWarning("Are you sure you want to delete army?").showAndWait();

        if (result.get() == ButtonType.CANCEL) {
            return;
        }

        //show error if failed
        if (!armyFSH.deleteArmy(army)) {
            AlertFactory.createWarning("Could not remove army. \n The army file might be in use or does not exist.").show();
            return;
        }

        //refresh page
        armySingleton.removeArmy(army);
        File[] armyFiles = armyFSH.getAllArmyFiles();
        repaintArmies(armyFiles);

        //hides the detailswindow
        detailsWindow.setVisible(false);
    }

    /**
     * Initializes the page. It repaints the header and the army int the singleton.
     * Lastly in initializes the bottom bar.
     */

    @FXML
    void initialize() {
        repaintHeader();
        detailsWindow.setVisible(false);
        ContainerFactory.initTableViewUnits(tableUnits);
        initBottomBar();
        ArmyFSH armyFSH = new ArmyFSH();
        repaintArmies(armyFSH.getAllArmyFiles());
    }
}
