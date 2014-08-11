package de.dhbw.horb.routePlanner.ui;

import java.util.LinkedList;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.controlsfx.dialog.Dialogs;

import de.dhbw.horb.routePlanner.Constants;
import de.dhbw.horb.routePlanner.SupportMethods;
import de.dhbw.horb.routePlanner.data.SettingsManager;

public class RoutePlannerMainController {

    // Reference to the main application.
    private RoutePlannerMainApp routePlannerMainApp;
    private WebEngine webEngine;;
    public String linkEnd = Constants.LINK_LINKEND;
    public String wayString = null;
    public String nodeString = null;
    public String calculationMethod;
    public String evaluationMethod;

    @FXML
    private WebView testWebView;

    @FXML
    private Button closeButton;

    @FXML
    private Button infoButton;

    @FXML
    private Button testButton;

    @FXML
    private ComboBox<String> startComboBox;

    @FXML
    private ComboBox<String> targetComboBox;

    @FXML
    private ComboBox<String> countryComboBox;

    @FXML
    private Button calculateRouteButton;

    @FXML
    private TabPane tabPane;

    @FXML
    private Tab calculatedRouteTab;

    @FXML
    private RadioButton fastestRouteRadio;

    @FXML
    private RadioButton shortestRouteRadio;

    @FXML
    private ToggleGroup calculationMethodToggleGroup;

    @FXML
    private RadioButton dijkstraRouteRadio;

    @FXML
    private RadioButton aStarRouteRadio;

    @FXML
    private ToggleGroup evaluationMethodToggleGroup;

    @FXML
    private Label startLabel;

    @FXML
    private Label destinationLabel;

    @FXML
    private ListView<String> calculatedRouteListView;

    @FXML
    private CheckBox showWaysCheckBox;

    @FXML
    private CheckBox showNodesCheckBox;

    @FXML
    private Button updateDataButton;

    /**
     * The constructor. The constructor is called before the initialize()
     * method.
     */
    public RoutePlannerMainController() {
    }

    @FXML
    void testButtonClicked(ActionEvent event) {
	//webEngine.load(this.getClass().getResource("overpass.html").toExternalForm());
    }

    @FXML
    void calculateRouteButtonClicked(ActionEvent event) {

	String start = startComboBox.getValue();
	String end = targetComboBox.getValue();

	if (start != null && end != null && !start.trim().isEmpty() && !end.trim().isEmpty()) {

	    if (calculationMethodToggleGroup.getSelectedToggle() == null) {

		Dialogs.create().title("Keine Berechnung m�glich!")
			.message("Bitte geben Sie eine Berechnungsmethode an.").showError();

	    } else {

		if (evaluationMethodToggleGroup.getSelectedToggle() == null) {

		    Dialogs.create().title("Keine Berechnung m�glich!")
			    .message("Bitte geben Sie einen Berechnungsalgorithmus an.").showError();

		} else {

		    //tabPane.getTabs().remove(calculatedRouteTab);

		    calculationMethod = null;
		    calculationMethod = getCalculationMethod();

		    evaluationMethod = null;
		    evaluationMethod = getEvaluationMethod();
		    evaluationMethod = "AStern";

		    UIEvaluationInterface.calculateRoute(start, end, calculationMethod, evaluationMethod);
		    webEngine.load(this.getClass().getResource("overpass.html").toExternalForm());

		}

	    }

	} else {

	    Dialogs.create().title("Keine Berechnung m�glich!")
		    .message("Bitte geben Sie sowohl Start als auch Ziel an.").showError();

	}

    }

    /**
     * Method to detect the selected CalculationMethod
     * @return currently selected CalculationMethod as String
     */
    private String getCalculationMethod() {
	String result = null;
	if (fastestRouteRadio.isSelected()) {
	    result = Constants.EVALUATION_CALCULATION_DURATION;
	} else {
	    if (shortestRouteRadio.isSelected()) {
		result = Constants.EVALUATION_CALCULATION_DISTANCE;
	    } else {

	    }
	}
	return result;
    }

    /**
     * Method to detect the selected EvaluationMethod
     * @return currently selected EvaluationMethod as String
     */
    private String getEvaluationMethod() {
	String result = null;
	if (aStarRouteRadio.isSelected()) {
	    result = Constants.EVALUATION_METHOD_ASTAR;
	} else {
	    if (dijkstraRouteRadio.isSelected()) {
		result = Constants.EVALUATION_METHOD_DIJKSTRA;
	    } else {

	    }
	}
	return result;
    }

    @FXML
    void infoButtonClicked(ActionEvent event) {

	routePlannerMainApp.executeStartupTask();

    }

    @FXML
    void closeButtonClicked(ActionEvent event) {
	routePlannerMainApp.primaryStage.close();
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setRoutePlannerMainApp(RoutePlannerMainApp routePlannerMainApp) {
	this.routePlannerMainApp = routePlannerMainApp;
	new AutoCompleteComboBoxListener<>(startComboBox);
	new AutoCompleteComboBoxListener<>(targetComboBox);

	//Settings eintragen
	countryComboBox.setValue(SettingsManager.getValue(Constants.SETTINGS_COUNTRY, "Deutschland"));
	switch (SettingsManager.getValue(Constants.SETTINGS_EVALUATION_METHOD, "Dijkstra")) {
	case Constants.EVALUATION_METHOD_DIJKSTRA:
	    evaluationMethodToggleGroup.selectToggle(dijkstraRouteRadio);
	    break;
	case Constants.EVALUATION_METHOD_ASTAR:
	    evaluationMethodToggleGroup.selectToggle(aStarRouteRadio);
	    break;
	default:
	    break;
	}
	switch (SettingsManager.getValue(Constants.SETTINGS_CALCULATION_METHOD, "Dauer")) {
	case Constants.EVALUATION_CALCULATION_DURATION:
	    calculationMethodToggleGroup.selectToggle(fastestRouteRadio);
	    break;
	case Constants.EVALUATION_CALCULATION_DISTANCE:
	    calculationMethodToggleGroup.selectToggle(shortestRouteRadio);
	    break;
	default:
	    break;
	}
	switch (SettingsManager.getValue(Constants.SETTINGS_SHOW_WAYS, "true")) {
	case "true":
	    showWaysCheckBox.setSelected(true);
	    break;
	case "false":
	    showWaysCheckBox.setSelected(false);
	    break;
	default:
	    break;
	}
	switch (SettingsManager.getValue(Constants.SETTINGS_SHOW_NODES, "true")) {
	case "true":
	    showNodesCheckBox.setSelected(true);
	    break;
	case "false":
	    showNodesCheckBox.setSelected(false);
	    break;
	default:
	    break;
	}

	webEngine = testWebView.getEngine();
	webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
	    @Override
	    public void changed(@SuppressWarnings("rawtypes") ObservableValue ov, State oldState, State newState) {

		if (newState == Worker.State.SUCCEEDED) {
		    LinkedList<String> nodes = new LinkedList<>();
		    nodes.add("30898199");
		    nodes.add("30899103");
		    //generateLinkQueries(UIEvaluationInterface.allWayIDs, nodes);
		    //DEBUG:
		    //System.out.println("Ways: " + wayString);
		    //System.out.println("Nodes: " + nodeString);

		    webEngine.executeScript("init()");
		    generateLinkQuery(UIEvaluationInterface.allWayIDs, "way", "ways", "blue");
		    generateLinkQuery(nodes, "node", "nodes", "red");

		    //TODO Liste aufbauen
		    calculatedRouteListView.setItems(UIEvaluationInterface.allDestinationNodeNames);
		    startLabel.setText(startComboBox.getValue());
		    destinationLabel.setText(targetComboBox.getValue());
		    calculatedRouteTab.getStyleClass().removeAll("hidden");
		    SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
		    selectionModel.select(calculatedRouteTab);

		    wayString = null;
		    nodeString = null;
		    UIEvaluationInterface.allWayIDs = null;
		}

	    }
	});
	evaluationMethodToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
	    public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
		if (evaluationMethodToggleGroup.getSelectedToggle() != null) {
		    SettingsManager.saveSetting(Constants.SETTINGS_EVALUATION_METHOD, getEvaluationMethod());
		}
	    }
	});
	calculationMethodToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
	    public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
		if (calculationMethodToggleGroup.getSelectedToggle() != null) {
		    SettingsManager.saveSetting(Constants.SETTINGS_CALCULATION_METHOD, getCalculationMethod());
		}
	    }
	});
	countryComboBox.getItems().addAll(SupportMethods.commaStrToStrList(Constants.COUNTRY_VERIFIED));
	countryComboBox.valueProperty().addListener(new ChangeListener<String>() {
	    @Override
	    public void changed(@SuppressWarnings("rawtypes") ObservableValue ov, String t, String t1) {
		SettingsManager.saveSetting(Constants.SETTINGS_COUNTRY, countryComboBox.getValue());
	    }
	});
	showWaysCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
	    public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
		SettingsManager.saveSetting(Constants.SETTINGS_SHOW_WAYS, new_val.toString());
		String visibility = null;
		if (new_val == true) {
		    visibility = "block";
		} else {
		    visibility = "none";
		}
		webEngine.executeScript("change_visibility('way', '" + visibility + "')");
	    }
	});
	showNodesCheckBox.selectedProperty().addListener(new ChangeListener<Boolean>() {
	    public void changed(ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) {
		SettingsManager.saveSetting(Constants.SETTINGS_SHOW_NODES, new_val.toString());
		String visibility = null;
		if (new_val == true) {
		    visibility = "block";
		} else {
		    visibility = "none";
		}
		//webEngine.executeScript("change_visibility('node', '" + visibility + "')");
	    }
	});
    }

    private void generateLinkQuery(LinkedList<String> list, String method, String name, String color) {

	String completeLink = Constants.LINK_COMPLETELINK;

	if (list == null || list.isEmpty()) {

	} else {

	    int x = 0;
	    for (String string : list) {
		completeLink += method + "(" + string + ");";
		x++;
		if (x > 99) {
		    webEngine.executeScript("add_layer('" + name + "', '" + completeLink + linkEnd + "', '" + color
			    + "')");
		    System.out.println("regul�r: " + completeLink + linkEnd);
		    x = 0;
		    completeLink = Constants.LINK_COMPLETELINK;
		}
	    }

	    if (completeLink != Constants.LINK_COMPLETELINK) {
		webEngine.executeScript("add_layer('" + name + "', '" + completeLink + linkEnd + "', '" + color + "')");
		System.out.println("nicht leer: " + completeLink + linkEnd);
	    }

	}
    }

    /**
     * Method to disable the "CalculateRoute"-Button
     */
    public void disableCalculateRouteButton() {

	calculateRouteButton.setDisable(true);

    }

    /**
     * Method to enable the "CalculateRoute"-Button
     */
    public void enableCalculateRouteButton() {

	calculateRouteButton.setDisable(false);

    }

    @FXML
    void updateDataButtonClicked(ActionEvent event) {

	//	Thread th = new Thread(RoutePlannerMainApp.task);
	//	th.setDaemon(true);
	//	th.start();

    }

}
