package de.dhbw.horb.routePlanner.evaluation.dijkstra;

public class ControllerTest {

    public static void main(String[] args) {

	String startnode = "Halle/Pei�en (17)";
	String endnode = "G�nzburg (67)";
	String calcMethod = "Dauer";

	Dijkstra dijkstra = new Dijkstra(startnode, endnode);

	dijkstra.calculateRoute(calcMethod);

    }
}
