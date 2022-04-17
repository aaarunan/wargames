package edu.ntnu.arunang.wargames.gui.controller;

import edu.ntnu.arunang.wargames.Army;
import edu.ntnu.arunang.wargames.fsh.ArmyFSH;
import edu.ntnu.arunang.wargames.gui.ArmySingleton;
import edu.ntnu.arunang.wargames.gui.GUI;
import edu.ntnu.arunang.wargames.gui.GUIFactory;
import edu.ntnu.arunang.wargames.unit.Unit;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;
import java.util.Optional;

/**
 * Controller that handles pages where armies are listed.
 */

public class ListArmyCON {

    private final ArmySingleton armySingleton = ArmySingleton.getInstance();
    @FXML // fx:id="armyContainer"
    private VBox armyContainer; // Value injected by FXMLLoader
    @FXML // fx:id="txtArmyName"
    private Text txtArmyName; // Value injected by FXMLLoader
    @FXML // fx:id="detailsWindow"
    private VBox detailsWindow; // Value injected by FXMLLoader
    @FXML // fx:id="tableUnits"
    private TableView<Unit> tableUnits; // Value injected by FXMLLoader
    @FXML
    private MenuButton menuSort;
    @FXML
    private HBox bottomBar;
    @FXML
    private Text title;
    @FXML
    private HBox armyDetails;

    private Army army;

    /**
     * Deletes an Army from the Singleton and in the stored files in /resources/army folder.
     * The method will show a pop-up warning that the user will delete the army.
     */

    @FXML
    void onDelete() {
        ArmyFSH armyFSH = new ArmyFSH();
        //get the result of the popup
        Optional<ButtonType> result = GUIFactory.createWarning("Are you sure you want to delete army?").showAndWait();

        if (result.get() == ButtonType.CANCEL) {
            return;
        }

        //show error if failed
        if (!armyFSH.deleteArmy(army)) {
            GUIFactory.createWarning("Could not remove army. \n The army file might be in use or does not exist.").show();
            return;
        }

        //refresh page
        armySingleton.removeArmy(army);
        List<Army> armies = armySingleton.getArmies();
        updateArmies(armies);

        detailsWindow.setVisible(false);
    }

    /**
     * Sorts the list of armies by the units in the army.
     */

    @FXML
    void onSortByCount() {
        armyContainer.getChildren().clear();
        updateArmies(armySingleton.getArmiesSortedByCount());
        menuSort.setText("Sort by count");
    }

    /**
     * Sorts the list of armies by the units in the army.
     */

    @FXML
    void onSortByName() {
        armyContainer.getChildren().clear();
        updateArmies(armySingleton.getArmiesSortedByName());
        menuSort.setText("Sort by name");
    }

    /**
     * Initializes the page by getting the armies from the singleton and updating
     * the elements in the gui by calling the update methods in the controller.
     */

    @FXML
    // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        updateHeader();
        detailsWindow.setVisible(false);
        GUIFactory.initUnitTable(tableUnits);
        initBottomBar();
        updateArmies(armySingleton.getArmies());
    }

    /**
     * Updates the details containers when an army has been selected.
     * It creates a detailed gridPane of the army stats, and a tableview of with every unit.
     */

    void updateDetails() {
        if (!detailsWindow.isVisible()) {
            detailsWindow.setVisible(true);
        }

        //update the army fields
        txtArmyName.setText(army.getName());
        armyDetails.getChildren().clear();
        armyDetails.getChildren().add(GUIFactory.createArmyPane(army));
        army.getUnits().forEach(unit -> tableUnits.getItems().add(unit));
    }

    /**
     * Update the list of armies.
     *
     * @param armies list of armies
     */

    void updateArmies(List<Army> armies) {
        //clear the container before adding
        armyContainer.getChildren().clear();
        if (armies == null) {
            return;
        }

        //loop through all the armies

        armies.forEach(army -> {
            Button button = GUIFactory.listButton(army.getName());
            button.setOnAction(buttonEvent -> {
                this.army = army;
                updateDetails();
            });
            armyContainer.getChildren().add(button);
        });
    }

    /**
     * Update the header according to simulate in the singleton.
     */
    void updateHeader() {
        if (armySingleton.isSimulate()) {
            if (armySingleton.getAttacker() == null) {
                title.setText("Choose attacker");
            } else {
                title.setText("Choose defender");
            }
        }
    }

    /**
     * Initialize the bottom bar according to simulate in the singleton
     */

    void initBottomBar() {
        Button btnAction;
        bottomBar.getChildren().clear();

        //Change the buttons to the circumstances
        if (!armySingleton.isSimulate()) {
            btnAction = GUIFactory.createDefaultButton("New army");
            btnAction.setOnAction(event -> GUI.setSceneFromActionEvent(event, "newArmy"));
        } else {
            btnAction = GUIFactory.createDefaultButton("Continue");
            btnAction.setOnAction(event -> {
                if (army == null) {
                    return;
                }
                if (armySingleton.getAttacker() == null) {
                    armySingleton.setAttacker(army);
                } else {
                    armySingleton.setDefender(army);
                    GUI.setSceneFromActionEvent(event, "simulate");
                }
                updateHeader();
                detailsWindow.setVisible(false);
                army = null;
            });
        }

        //create back button
        Button btnBack = GUIFactory.createDefaultButton("Back");
        btnBack.setOnAction(event -> GUI.setSceneFromActionEvent(event, "main"));

        //add the buttons to the bottom bar
        bottomBar.getChildren().add(btnBack);
        bottomBar.getChildren().add(btnAction);
    }
}