package de.dhbw.horb.routePlanner.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;

/**
 * Controller-Klasse f�r das Splash-Window. Stellt Funktionen f�r das Splash zur
 * Verf�gung
 *
 * @author robin
 *
 */
public class StartupMainController {

	public StartupMainController() {

	}

	@FXML
	private ProgressIndicator startupProgressIndicator;
	@FXML
	private ProgressBar startupProgressBar;
	@FXML
	private Label startupLabel;

	/**
	 * Setzt den Wert der ProgressBar
	 * 
	 * @param progress
	 *            Wert der ProgressBar als double
	 */
	public void setProgress(double progress) {
		startupProgressBar.setProgress(progress);
	}

	/**
	 * Setzt den Label-Text
	 * 
	 * @param msg
	 *            Label-Text als String
	 */
	public void setMessage(String msg) {
		startupLabel.setText(msg);
	}

	/**
	 * Gibt ProgressBar zur�ck
	 * 
	 * @return Referenz auf ProgressBar
	 */
	public ProgressBar getProgressBar() {
		return startupProgressBar;
	}

	/**
	 * Gibt Label zur�ck
	 * 
	 * @return Referenz auf Label
	 */
	public Label getLabel() {
		return startupLabel;
	}
}
