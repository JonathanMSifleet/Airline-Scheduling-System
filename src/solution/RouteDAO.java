package solution;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
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

		try {
			// load the XML
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = db.parse(arg0.toString());

			Element root = doc.getDocumentElement(); // get root element

			// get a list of the child nodes:
			NodeList children = root.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node c = children.item(i);
				if (c.getNodeName().equals("Route")) {
					// we found a <Route> element, lets find its title
					NodeList grandchildren = c.getChildNodes();
					for (int j = 0; j < grandchildren.getLength(); j++) {
						Node d = grandchildren.item(j);
						if (d.getNodeName().contentEquals("FlightNumber")) {
							System.out.println(d.getChildNodes().item(0).getNodeValue());
						}
					}
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.println("Error opening XML file: " + e);
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
		return null;
	}

	/**
	 * Finds all of the flights that depart from a specific airport on a specific
	 * day of the week
	 * 
	 * @param airportCode the three letter code of the airport to search for, e.g.
	 *                    "MAN"
	 * @param dayOfWeek   the three letter day of the week code to searh for, e.g.
	 *                    "Tue"
	 * @return A list of all routes from that airport on that day
	 */
	@Override
	public List<Route> findRoutesByDepartureAirportAndDay(String airportCode, String dayOfWeek) {
		// TODO Auto-generated method stub
		return null;
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
		return null;
	}

	/**
	 * Finds all of the flights that depart on the specified date
	 * 
	 * @param date the date to search for
	 * @return A list of all routes that dpeart on this date
	 */
	@Override
	public List<Route> findRoutesbyDate(LocalDate date) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns The full list of all currently loaded routes
	 * 
	 * @return The full list of all currently loaded routes
	 */
	@Override
	public List<Route> getAllRoutes() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns The number of routes currently loaded
	 * 
	 * @return The number of routes currently loaded
	 */
	@Override
	public int getNumberOfRoutes() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Unloads all of the crew currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
