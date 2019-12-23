package solution;

import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import java.io.*;

import baseclasses.DataLoadingException;
import baseclasses.IRouteDAO;
import baseclasses.Route;

/**
 * The RouteDAO parses XML files of route information, each route specifying
 * where the airline flies from, to, and on which day of the week
 */
public class RouteDAO implements IRouteDAO {

	ArrayList<Route> arrayOfRoutes = new ArrayList<>();

	/**
	 * Loads the route data from the specified file, adding them to the currently
	 * loaded routes Multiple calls to this function, perhaps on different files,
	 * would thus be cumulative
	 * 
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause"
	 *                              indicates the underlying exception
	 */
	@Override
	public void loadRouteData(Path arg0) throws DataLoadingException {
		// TODO Auto-generated method stub

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

		try {
			// load the XML
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(arg0.toString());

			Element root = doc.getDocumentElement();
			NodeList routeNodes = root.getElementsByTagName("Route");

			for (int i = 0; i < routeNodes.getLength(); i++) {
				Node route = routeNodes.item(i);
				Route temp = new Route();

				try {

					Element routeData = (Element) route;

					temp.setFlightNumber(Integer.parseInt(routeData.getElementsByTagName("FlightNumber").item(0).getTextContent()));
					temp.setDayOfWeek(routeData.getElementsByTagName("DayOfWeek").item(0).getTextContent());
					temp.setDepartureTime(LocalTime.parse(routeData.getElementsByTagName("DepartureTime").item(0).getTextContent(), formatter));
					temp.setDepartureAirportCode(routeData.getElementsByTagName("DepartureAirportCode").item(0).getTextContent());
					temp.setDepartureAirport(routeData.getElementsByTagName("DepartureAirport").item(0).getTextContent());
					temp.setArrivalTime(LocalTime.parse(routeData.getElementsByTagName("ArrivalTime").item(0).getTextContent()));
					temp.setArrivalAirport(routeData.getElementsByTagName("ArrivalAirport").item(0).getTextContent());
					temp.setArrivalAirportCode(routeData.getElementsByTagName("ArrivalAirportCode").item(0).getTextContent());
					temp.setDuration(java.time.Duration.parse(routeData.getElementsByTagName("Duration").item(0).getTextContent()));

				} catch (Exception e) {
					throw new DataLoadingException();
				}
				
				// add route to array of routes
				arrayOfRoutes.add(temp);
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.println("Error opening XML file: " + e);
			throw new DataLoadingException();
		}
	}

	/**
	 * Finds all flights that depart on the specified day of the week
	 * 
	 * @param dayOfWeek A three letter day of the week, e.g. "Tue"
	 * @return A list of all routes that depart on this day
	 */
	@Override
	public List<Route> findRoutesByDayOfWeek(String dayOfWeek) {
		// TODO Auto-generated method stub

		ArrayList<Route> temp = new ArrayList<>();

		for (int i = 0; i < arrayOfRoutes.size(); i++) {
			if (arrayOfRoutes.get(i).getDayOfWeek().equals(dayOfWeek)) {
				temp.add(arrayOfRoutes.get(i));
			}
		}
		return temp;

	}

	/**
	 * Finds all of the flights that depart from a specific airport on a specific
	 * day of the week
	 * 
	 * @param airportCode the three letter code of the airport to search for, e.g.
	 *                    "MAN"
	 * @param dayOfWeek   the three letter day of the week code to search for, e.g.
	 *                    "Tue"
	 * @return A list of all routes from that airport on that day
	 */
	@Override
	public List<Route> findRoutesByDepartureAirportAndDay(String airportCode, String dayOfWeek) {
		// TODO Auto-generated method stub

		ArrayList<Route> temp = new ArrayList<>();

		for (int i = 0; i < arrayOfRoutes.size(); i++) {
			if (arrayOfRoutes.get(i).getDepartureAirportCode().equals(airportCode) && arrayOfRoutes.get(i).getDayOfWeek().equals(dayOfWeek)) {
				temp.add(arrayOfRoutes.get(i));
			}
		}

		return temp;
	}

	/**
	 * Finds all of the flights that depart from a specific airport
	 * 
	 * @param airportCode the three letter code of the airport to search for, e.g.
	 *                    "MAN"
	 * @return A list of all of the routes departing the specified airport
	 */
	@Override
	public List<Route> findRoutesDepartingAirport(String airportCode) {
		// TODO Auto-generated method stub

		ArrayList<Route> temp = new ArrayList<>();

		for (int i = 0; i < arrayOfRoutes.size(); i++) {
			if (arrayOfRoutes.get(i).getDepartureAirportCode().equals(airportCode)) {
				temp.add(arrayOfRoutes.get(i));
			}
		}

		return temp;
	}

	/**
	 * Finds all of the flights that depart on the specified date
	 * 
	 * @param date the date to search for
	 * @return A list of all routes that depart on this date
	 */
	@Override
	public List<Route> findRoutesbyDate(LocalDate date) {
		// TODO Auto-generated method stub

		ArrayList<Route> temp = new ArrayList<>();

		for (int i = 0; i < arrayOfRoutes.size(); i++) {

			DayOfWeek day = null;

			try {

				switch (arrayOfRoutes.get(i).getDayOfWeek()) {
				case "Sun":
					day = DayOfWeek.SUNDAY;
					break;
				case "Mon":
					day = DayOfWeek.MONDAY;
					break;

				case "Tue":
					day = DayOfWeek.TUESDAY;
					break;

				case "Wed":
					day = DayOfWeek.WEDNESDAY;
					break;

				case "Thu":
					day = DayOfWeek.THURSDAY;
					break;

				case "Fri":
					day = DayOfWeek.FRIDAY;
					break;

				case "Sat":
					day = DayOfWeek.SATURDAY;
					break;
				default:
					throw new DataLoadingException();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (day == date.getDayOfWeek()) {
				temp.add(arrayOfRoutes.get(i));
			}
		}

		return temp;

	}

	/**
	 * Returns The full list of all currently loaded routes
	 * 
	 * @return The full list of all currently loaded routes
	 */
	@Override
	public List<Route> getAllRoutes() {
		// TODO Auto-generated method stub
		return arrayOfRoutes;
	}

	/**
	 * Returns The number of routes currently loaded
	 * 
	 * @return The number of routes currently loaded
	 */
	@Override
	public int getNumberOfRoutes() {
		// TODO Auto-generated method stub
		return arrayOfRoutes.size();
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		arrayOfRoutes = new ArrayList<Route>();
	}
}
