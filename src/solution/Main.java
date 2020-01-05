package solution;

import java.nio.file.Paths;
import java.time.LocalDate;
import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;

/** This class allows you to run the code in your classes yourself, for testing
 * and development */
public class Main {

	public static void main(String[] args) {

		IAircraftDAO aircrafts = new AircraftDAO();

		try {
			//aircrafts.loadAircraftData(Paths.get("./data/schedule_aircraft.csv"));
			 aircrafts.loadAircraftData(Paths.get("./data/aircraft.csv"));

			System.out.println("Number of aircraft " + aircrafts.getNumberOfAircraft());
		} catch (DataLoadingException dle) {
			System.err.println("Error loading aircraft data");
			dle.printStackTrace();
		}

		ICrewDAO crew = new CrewDAO();

		try {
			//crew.loadCrewData(Paths.get("./data/schedule_crew.json"));
			 crew.loadCrewData(Paths.get("./data/crew.json"));

			System.out.println("Number of crew " + crew.getNumberOfCabinCrew());
			System.out.println("Number of pilots: " + crew.getNumberOfPilots());
		} catch (DataLoadingException dle) {
			System.err.println("Error loading crew data");
			dle.printStackTrace();
		}

		IPassengerNumbersDAO passengers = new PassengerNumbersDAO();

		try {
			//passengers.loadPassengerNumbersData(Paths.get("./data/schedule_passengers.db"));
			 passengers.loadPassengerNumbersData(Paths.get("./data/passengernumbers.db"));

			System.out.println("Sets of passengers " + passengers.getNumberOfEntries());
		} catch (DataLoadingException dle) {
			System.err.println("Error loading passenger data");
			dle.printStackTrace();
		}

		IRouteDAO routes = new RouteDAO();

		try {
			//routes.loadRouteData(Paths.get("./data/schedule_routes.xml"));
			 routes.loadRouteData(Paths.get("./data/routes.xml"));

			System.out.println("Number of routes: " + routes.getNumberOfRoutes());
		} catch (DataLoadingException dle) {
			System.err.println("Error loading route data");
			dle.printStackTrace();
		}

		LocalDate startDate = LocalDate.parse("2020-07-01");
		LocalDate endDate = LocalDate.parse("2020-07-08");

		IScheduler scheduler = new Scheduler();
		try {
			scheduler.generateSchedule(aircrafts, crew, routes, passengers, startDate, endDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
