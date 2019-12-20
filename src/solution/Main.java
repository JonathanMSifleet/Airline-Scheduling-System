package solution;

import java.nio.file.Paths;

import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;

/**
 * This class allows you to run the code in your classes yourself, for testing
 * and development
 */
public class Main {

	public static void main(String[] args) {
		/*
		 * IAircraftDAO aircraft = new AircraftDAO();
		 * 
		 * try { aircraft.loadAircraftData(Paths.get("./data/aircraft.csv")); } catch
		 * (DataLoadingException dle) {
		 * System.err.println("Error loading aircraft data"); dle.printStackTrace(); }
		 * 
		 * ICrewDAO crew = new CrewDAO();
		 * 
		 * try { crew.loadCrewData(Paths.get("./data/crew.json")); } catch
		 * (DataLoadingException dle) { System.err.println("Error loading crew data");
		 * dle.printStackTrace(); }
		 * 
		 * IPassengerNumbersDAO passengers = new PassengerNumbersDAO();
		 * 
		 * try {
		 * passengers.loadPassengerNumbersData(Paths.get("./data/passengernumbers.db"));
		 * } catch (DataLoadingException dle) {
		 * System.err.println("Error loading passenger data"); dle.printStackTrace(); }
		 */

		IRouteDAO routes = new RouteDAO();

		try {
			routes.loadRouteData(Paths.get("./data/routes.xml"));
		} catch (DataLoadingException dle) {
			System.err.println("Error loading route data");
			dle.printStackTrace();
		}

	}

}
