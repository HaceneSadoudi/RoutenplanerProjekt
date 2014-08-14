package de.dhbw.horb.routePlanner.evaluation.aStar;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import de.dhbw.horb.routePlanner.Constants;
import de.dhbw.horb.routePlanner.SupportMethods;
import de.dhbw.horb.routePlanner.data.StAXMapGraphDataParser;

/**
 * Berechnung der Route mit dem AStar Algorithmus. Da auf Heuristik verzichtet wurde handelt es sich mehr um das
 * aufbauen eines minimalen Spannbaums mit Kantenbewertung.
 */
public class AStar {
    /**
     * enum f�r die Unterscheidung zwischen offener und geschlossener Liste.
     */
    private enum ListType {
	open, closed
    }

    private Map<String, List<Map<String, String>>> routes;
    private Map<String, List<String>> nodeMap;

    private Map<String, String> openEdgesPredecessor;
    private Map<String, Map<String, String>> openEdgesRoute;
    private Map<String, Double> openEdgesWeight;
    private Map<String, String> closedEdgesPredecessor;
    private Map<String, Map<String, String>> closedEdgesRoute;
    private Map<String, Double> closedEdgesWeight;
    private HashSet<String> departureIDs;
    private HashSet<String> destinationIDs;
    private String calculateMethod;

    /**
     * AStar Routenberechnung
     * 
     * @param departure Name des Anfangs
     * @param destination Name des Ziels
     */
    public AStar(String departure, String destination) {
	openEdgesPredecessor = new HashMap<String, String>();
	openEdgesRoute = new HashMap<String, Map<String, String>>();
	openEdgesWeight = new HashMap<String, Double>();
	closedEdgesPredecessor = new HashMap<String, String>();
	closedEdgesRoute = new HashMap<String, Map<String, String>>();
	closedEdgesWeight = new HashMap<String, Double>();
	try {
	    routes = StAXMapGraphDataParser.getRouteXMLMap();
	    nodeMap = StAXMapGraphDataParser.getNodeXMLMap();
	} catch (FileNotFoundException | XMLStreamException e) {
	    e.printStackTrace();
	}

	departureIDs = new HashSet<String>();
	List<String> depIDs = nodeMap.get(departure);
	if (depIDs == null || depIDs.isEmpty())
	    return;

	for (String id : depIDs) {
	    departureIDs.add(id);
	}

	destinationIDs = new HashSet<String>();
	List<String> desIDs = nodeMap.get(destination);
	if (desIDs == null || desIDs.isEmpty())
	    return;

	for (String id : desIDs) {
	    destinationIDs.add(id);
	}
    }

    /**
     * Start der Routenberechnung
     * 
     * @param calculateMethod Berechnungsmethode (Zeit oder Strecke)
     * @return Liste mit Wegen der richtigen Route
     */
    public List<Map<String, String>> calculateWay(String calculateMethod) {
	if (destinationIDs == null || departureIDs == null || destinationIDs.isEmpty() || departureIDs.isEmpty()) {
	    System.err.println("No IDs for departure or destination.");
	    return null;
	}

	this.calculateMethod = calculateMethod;
	for (String depID : departureIDs) {
	    closedEdgesPredecessor.put(depID, depID);
	    closedEdgesRoute.put(depID, null);
	    closedEdgesWeight.put(depID, 0.0);
	    addNeighbourToOpenList(depID);
	}
	findDestination();

	String destinationID = null;
	Double wCache = null;
	for (String desID : destinationIDs) {
	    Double w = getWeightBack(desID);
	    if (w != null && (wCache == null || w < wCache)) {
		wCache = w;
		destinationID = desID;
	    }
	}

	if (destinationID == null) {
	    System.err.println("AStar couldn't find a way to destination.");
	    return null;
	}

	return getWays(destinationID);
    }

    /**
     * Erzeugt eine Liste von Wegen (Von Blatt zu Wurzel).
     * 
     * @param destination Das Ziel
     * @return Liste der Wege von Ziel zum Anfang
     */
    private List<Map<String, String>> getWays(String destination) {
	List<Map<String, String>> nodes = new LinkedList<Map<String, String>>();
	Map<String, String> mp = closedEdgesRoute.get(destination);

	if (mp != null) {
	    String newDestination = closedEdgesPredecessor.get(destination);

	    if (newDestination != null) {

		if (departureIDs.contains(newDestination)) {
		    nodes.add(mp);
		    return nodes;
		} else {
		    nodes = getWays(newDestination);
		    nodes.add(mp);
		}
	    }
	}

	if (nodes != null && !nodes.isEmpty())
	    return nodes;
	return null;
    }

    /**
     * Gibt das Gewicht zur�ck von einem Ziel bis zum Anfang (Von Blatt zu Wurzel).
     * 
     * @param destination Das Ziel
     * @return Gewicht (Zeit oder Strecke)
     */
    private Double getWeightBack(String destination) {
	Double w = null;
	String newDestination = closedEdgesPredecessor.get(destination);
	Double newWeight = closedEdgesWeight.get(destination);

	if (newDestination != null && newWeight != null) {

	    if (departureIDs.contains(newDestination))
		return newWeight;

	    w = getWeightBack(newDestination) + newWeight;
	}
	return w;
    }

    /**
     * Rekursives untersuchen der offenen Kanten bis das Ziel erreicht ist.
     */
    private void findDestination() {
	String smallest = getSmallest();
	if (smallest != null) {
	    Map<String, String> mp = openEdgesRoute.get(smallest);

	    removeEdge(ListType.open, mp);
	    addEdge(ListType.closed, mp);
	    addNeighbourToOpenList(mp.get(Constants.NEW_ROUTE_DESTINATIONNODEID));

	    if (!openEdgesRoute.isEmpty())
		findDestination();
	}
    }

    /**
     * Hinzuf�gen der Nachbarn zu einer ID
     * 
     * @param id Knoten ID
     */
    private void addNeighbourToOpenList(String id) {
	List<Map<String, String>> r = routes.get(id);
	if (r == null || r.isEmpty()) {
	    return;
	}
	for (Map<String, String> edge : r) {

	    if (edge == null)
		continue;

	    if (closedEdgesPredecessor.containsKey(edge.get(Constants.NEW_ROUTE_DESTINATIONNODEID)))
		continue;

	    if (openEdgesPredecessor.containsKey(edge.get(Constants.NEW_ROUTE_DESTINATIONNODEID))) {
		Double newWeight = getWeight(edge);
		Double newWeightBack = getWeightBack(edge.get(Constants.NEW_ROUTE_DEPARTURENODEID));

		if (newWeightBack == null || newWeight == null)
		    continue;

		Double oldWeight = openEdgesWeight.get(edge.get(Constants.NEW_ROUTE_DESTINATIONNODEID));
		Double oldWeightBack = getWeightBack(openEdgesPredecessor.get(edge
		        .get(Constants.NEW_ROUTE_DESTINATIONNODEID)));

		if (oldWeightBack == null || oldWeight == null)
		    System.err.println("addNeighbourToOpenList() OldWeight Fehler");

		if ((newWeight + newWeightBack) > (oldWeight + oldWeightBack))
		    continue;

		removeEdge(ListType.open, openEdgesRoute.get(edge.get(Constants.NEW_ROUTE_DESTINATIONNODEID)));
	    }
	    addEdge(ListType.open, edge);
	}
    }

    /**
     * Das Gewicht entsprechend der Berechnungsmethode zur�ckgeben.
     * 
     * @param edge Die Kante
     * @return Das Gewicht (Zeit oder Strecke)
     */
    private Double getWeight(Map<String, String> edge) {
	String w;
	switch (calculateMethod) {
	case Constants.NEW_ROUTE_DISTANCE:
	    w = edge.get(Constants.NEW_ROUTE_DISTANCE);
	    break;
	case Constants.NEW_ROUTE_DURATION:
	    w = edge.get(Constants.NEW_ROUTE_DURATION);
	    break;
	default:
	    w = edge.get(Constants.NEW_ROUTE_DURATION);
	}
	if (!SupportMethods.isNumeric(w))
	    return null;
	return Double.valueOf(w);
    }

    /**
     * F�ge eine Kante zur geschlossenen oder offenen Liste hinzu
     * 
     * @param t Listen Art (offen oder geschlossen)
     * @param edge Die Kante
     */
    private void addEdge(ListType t, Map<String, String> edge) {
	if (edge == null || edge.isEmpty())
	    return;
	String destinationID = edge.get(Constants.NEW_ROUTE_DESTINATIONNODEID);

	Double weight = getWeight(edge);

	if (weight == null || destinationID == null)
	    return;

	switch (t) {
	case open:
	    openEdgesRoute.put(destinationID, edge);
	    openEdgesPredecessor.put(destinationID, edge.get(Constants.NEW_ROUTE_DEPARTURENODEID));
	    openEdgesWeight.put(destinationID, weight);
	    break;

	case closed:
	    closedEdgesRoute.put(destinationID, edge);
	    closedEdgesPredecessor.put(destinationID, edge.get(Constants.NEW_ROUTE_DEPARTURENODEID));
	    closedEdgesWeight.put(destinationID, weight);
	    break;

	default:
	    break;
	}
    }

    /**
     * gibt die mit gesamt Weg kleinste Kante zur�ck.
     * 
     * @return Die Kante
     */
    private String getSmallest() {
	String key = null;
	Double value = null;

	for (Map.Entry<String, Double> entry : openEdgesWeight.entrySet()) {
	    String iKey = entry.getKey();
	    Double iValue = entry.getValue();

	    if (iKey == null || iValue == null)
		continue;

	    Double wb = getWeightBack(openEdgesPredecessor.get(iKey));
	    if (wb == null)
		continue;
	    Double newValue = wb + iValue;

	    if (value == null || newValue <= value) {
		key = iKey;
		value = newValue;
	    }
	}
	return key;
    }

    /**
     * L�sche eine Kante aus einer Liste.
     * 
     * @param t Listen Art (offen oder geschlossen)
     * @param edge Die Kante
     * @return
     */
    private Map<String, String> removeEdge(ListType t, Map<String, String> edge) {
	if (edge == null || edge.isEmpty())
	    return null;
	String destinationID = edge.get(Constants.NEW_ROUTE_DESTINATIONNODEID);

	switch (t) {
	case open:
	    if (openEdgesRoute == null || openEdgesRoute.isEmpty() || !openEdgesRoute.containsKey(destinationID))
		return null;

	    openEdgesPredecessor.remove(destinationID);
	    openEdgesWeight.remove(destinationID);
	    return openEdgesRoute.remove(destinationID);

	case closed:
	    if (closedEdgesRoute == null || closedEdgesRoute.isEmpty() || !closedEdgesRoute.containsKey(destinationID))
		return null;

	    closedEdgesPredecessor.remove(destinationID);
	    closedEdgesWeight.remove(destinationID);
	    return closedEdgesRoute.remove(destinationID);

	default:
	    return null;
	}
    }
}
