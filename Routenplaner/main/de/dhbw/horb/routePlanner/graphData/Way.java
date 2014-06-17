package de.dhbw.horb.routePlanner.graphData;

import java.util.List;

/**
 * Klasse die eine Kante beschreibt.
 * 
 * @author Sebastian
 */
public class Way {

	private Long id;
	private List<Long> nodeIDs;

	// private List<String, Object> characteristics;

	/**
	 * Konstruktor der Kanten Klasse.
	 * 
	 * @param source
	 *            Der Anfang einer Kante.
	 * @param target
	 *            Das Ende einer Kante.
	 * @param identifier
	 *            Eine Straßenbezeichnung.
	 * @param length
	 *            Die Länge der Kante.
	 */
	public Way(Long id, List<Long> nodeIDs, List<String> characteristics) {

	}

}
