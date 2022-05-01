package edu.ntnu.arunang.wargames.gui.controller;

import edu.ntnu.arunang.wargames.Army;
import edu.ntnu.arunang.wargames.fsh.ArmyFSH;
import edu.ntnu.arunang.wargames.gui.ArmySingleton;
import edu.ntnu.arunang.wargames.gui.GUI;
import edu.ntnu.arunang.wargames.gui.decorator.TextDecorator;
import edu.ntnu.arunang.wargames.gui.factory.AlertFactory;
import edu.ntnu.arunang.wargames.gui.factory.ButtonFactory;
import edu.ntnu.arunang.wargames.gui.factory.ContainerFactory;
import edu.ntnu.arunang.wargames.gui.factory.NavbarFactory;
import edu.ntnu.arunang.wargames.unit.Unit;
import edu.ntnu.arunang.wargames.unit.UnitFactory;
import edu.ntnu.arunang.wargames.unit.UnitType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the newArmy page.
 */

public class NewArmyCON {
    @FXML
    private TextField fieldArmyName;
    @FXML
    private TableView<Unit> tableUnits;
    @FXML
    private Text txtArmyName;
    @FXML
    private TextField fieldHealthPoints;
    @FXML
    private TextField fieldCount;
    @FXML
    private TextField fieldName;
    @FXML
    private MenuButton menuUnitType;
    @FXML
    private VBox vBoxAdd;
    @FXML
    private Text txtErrorMsg;
    @FXML
    private HBox armyContainer;
    @FXML
    private BorderPane borderPane;

    private final Army army = new Army("Army name");
    private final ArmySingleton armySingleton = ArmySingleton.getInstance();

    /**
     * Changes the name of the army when typed in the army name textfield, and updates
     * the detail container accordingly.
     */

    @FXML
    void onArmyName() {
        txtErrorMsg.setText("");
        //throw error if name is empty
        try {
            army.setName(fieldArmyName.getText());
        } catch (IllegalArgumentException e) {
            txtErrorMsg.setText(e.getMessage());
        }
        txtArmyName.setText(army.getName());
    }

    /**
     * Cancel creation of new army. And redirect to the listArmy page.
     *
     * @param event triggering event from button
     */

    @FXML
    void onCancel(ActionEvent event) {
        GUI.setSceneFromActionEvent(event, "listArmy");
    }

    /**
     * Saves the army to the /resources/army folder.
     * If the file already exists, the user will be warned.
     *
     * @param event triggering event.
     */

    @FXML
    void onSave(ActionEvent event) {
        ArmyFSH armyFSH = new ArmyFSH();

        txtErrorMsg.setText("");
        if (txtArmyName.getText().isBlank()) {
            txtErrorMsg.setText("Choose an army name");
        }

        //checks if file exists
        if (armyFSH.fileExists(new File(ArmyFSH.getPath(army.getName())))) {
            Alert alert = AlertFactory.createWarning(String.format("Army '%s' already exists, do you want to override it?", army.getName()));
            Optional<ButtonType> result = alert.showAndWait();

            //cancels the process if the user declines
            if (result.get() == ButtonType.CANCEL) {
                return;
            }
        }
        Army prevArmy;
        try {
            prevArmy = armyFSH.loadFromFile(new File(ArmyFSH.getPath(army.getName())));
        } catch (IOException e) {
            AlertFactory.createError("An unexpected error occured!\n" + e.getMessage()).show();
            return;
        }

        //Checks if the army can be written
        try {
            armyFSH.writeArmy(army);
        } catch (IOException e) {
            AlertFactory.createError("Could not overwrite file. File might be in use... \n " + e.getMessage()).show();
        } catch (Exception e) {
            AlertFactory.createError("Un unexpected exception occurred... \n " + e.getMessage()).show();
            return;
        }

        //removes the army from the singleton if successful, and adds the new army
        armySingleton.removeArmy(prevArmy);
        armySingleton.addArmy(army);
        GUI.setSceneFromActionEvent(event, "listArmy");
    }

    /**
     * Add units to an army. This also updates the gui accordingly.
     */

    @FXML
    void onAdd() {
        txtErrorMsg.setText("");
        ArrayList<Unit> units = new ArrayList<>();

        //check if the user has choosen a unittype
        if (menuUnitType.getText().equals("UnitType")) {
            txtErrorMsg.setText("Choose an Unittype.");
            return;
        }

        //try to create a unit from the user input
        try {
            units = (ArrayList<Unit>) UnitFactory.constructUnitsFromString(menuUnitType.getId(), fieldName.getText(), Integer.parseInt(fieldHealthPoints.getText()), Integer.parseInt(fieldCount.getText()));
            army.add(units);
            units.forEach(unit -> tableUnits.getItems().add(unit));
        } catch (IllegalArgumentException e) {
            txtErrorMsg.setText(e.getMessage());
        } catch (Exception e) {
            AlertFactory.createError("Unexpected error occured: \n" + e.getMessage()).show();
        }

        Button btnDelete = new Button("Delete");

        Unit target = units.get(0);

        //Add a card to the history container
        ContainerFactory.ListCardBuilder listCardBuilder = new ContainerFactory.ListCardBuilder();
        VBox vBox = listCardBuilder.add("Type: " + target.getClass().getSimpleName()).add("Name: " + target.getName()).add("Health points: " + target.getHealthPoints()).add("Count: " + units.size()).build();

        ArrayList<Unit> finalUnits = units;
        btnDelete.setOnAction(event -> {
            onDeleteUnits(event, finalUnits, vBox);
        });

        //add all the elements and update the page
        vBox.getChildren().add(btnDelete);
        vBoxAdd.getChildren().add(vBox);
        repaintDetails();
    }

    void onDeleteUnits(ActionEvent event, List<Unit> units, VBox vBox) {
        for (Unit unit : units) {
            army.remove(unit);
            vBoxAdd.getChildren().remove(vBox);
            tableUnits.getItems().remove(unit);
            repaintDetails();
        }
    }

    /**
     * Initializes the page. Initializes number only fields and the tableview.
     */

    @FXML
    void initialize() {
        initMenuUnitType();
        ButtonFactory.initNumberOnlyTextField(fieldCount);
        ButtonFactory.initNumberOnlyTextField(fieldHealthPoints);
        ContainerFactory.initTableViewUnits(tableUnits);
        initBottomBar();
        repaintDetails();
    }

    /**
     * Update the stats of the army.
     */

    void repaintDetails() {
        armyContainer.getChildren().clear();
        armyContainer.getChildren().add(ContainerFactory.createArmyPane(army));
    }

    /**
     * Intializes the menuButton for unitType. Loops through the UnitType enum.
     */

    void initMenuUnitType() {
        for (UnitType type : UnitType.values()) {
            MenuItem menuItem = ButtonFactory.createMenuItem(type.toString());
            menuItem.setOnAction(event -> {
                menuUnitType.setText(type.toString());
                menuUnitType.setId(type.toString());
            });
            menuUnitType.getItems().add(menuItem);
        }
    }

    /**
     * Initializes the bottombar.
     */

    void initBottomBar() {
        HBox hBox = NavbarFactory.createBottomBar();
        Button back = ButtonFactory.createDefaultButton("Cancel");
        back.setOnAction(event -> GUI.setSceneFromActionEvent(event, "listArmy"));
        Button save = ButtonFactory.createDefaultButton("Save");
        save.setOnAction(this::onSave);
        hBox.getChildren().addAll(back, save);
        borderPane.setBottom(hBox);
    }
}
