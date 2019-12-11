package solution;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import baseclasses.DataLoadingException;
import baseclasses.IPassengerNumbersDAO;

/**
 * The PassengerNumbersDAO is responsible for loading an SQLite database
 * containing forecasts of passenger numbers for flights on dates
 */
public class PassengerNumbersDAO implements IPassengerNumbersDAO {

	Map<String, Integer> numbers = new HashMap<>();

	/**
	 * Loads the passenger numbers data from the specified SQLite database into a
	 * cache for future calls to getPassengerNumbersFor() Multiple calls to this
	 * method are additive, but flight numbers/dates previously cached will be
	 * overwritten The cache can be reset by calling reset()
	 * 
	 * @param p The path of the SQLite database to load data from
	 * @throws DataLoadingException If there is a problem loading from the database
	 */
	@Override
	public void loadPassengerNumbersData(Path p) throws DataLoadingException {
		// TODO Auto-generated method stub
		try {
			Connection c = null;
			// connect to DB:
			c = DriverManager.getConnection("jdbc:sqlite:" + p);

			// run query:
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM PassengerNumbers");

			// fetch results:
			while (rs.next()) {
				numbers.put(rs.getString("FlightNumber") + " " + rs.getString("Date"),
						Integer.parseInt(rs.getString("Passengers")));
			}

			c.close();
			rs.close();
			s.close();
		} catch (Exception e) {
			throw new DataLoadingException();
		}
	}

	/**
	 * Returns the predicted number of passengers for a given flight on a given
	 * date, or -1 if no data available
	 * 
	 * @param flightNumber The flight number of the flight to check for
	 * @param date the date of the flight to check for
	 * @return the predicted number of passengers, or -1 if no data available
	 */
	@Override
	public int getPassengerNumbersFor(int flightNumber, LocalDate date) {
		// TODO Auto-generated method stub

		int results = -1;
		if (numbers.containsKey(flightNumber + " " + date)) {
			results = numbers.get(flightNumber + " " + date);
		}
		return results;
	}

	/**
	 * Returns the number of passenger number entries in the cache
	 * 
	 * @return the number of passenger number entries in the cache
	 */
	@Override
	public int getNumberOfEntries() {
		// TODO Auto-generated method stub
		return numbers.size();
	}

	/**
	 * Removes all data from the DAO, ready to start again if needed
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		numbers.clear();
	}

}
