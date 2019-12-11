package solution;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;

import baseclasses.DataLoadingException;
import baseclasses.IPassengerNumbersDAO;
import java.sql.*;

/**
 * The PassengerNumbersDAO is responsible for loading an SQLite database
 * containing forecasts of passenger numbers for flights on dates
 */
public class PassengerNumbersDAO implements IPassengerNumbersDAO {

	ArrayList<String> arrayOfPassengers = new ArrayList<>();

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
				String tempString = rs.getString("Date") + "," + rs.getString("FlightNumber") + "," +  rs.getString("Passengers");
				//System.out.println(tempString);

				arrayOfPassengers.add(tempString);
			}
		} catch (SQLException e) {
			e.printStackTrace();
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

		// [do]

		return 0;
	}

	/**
	 * Returns the number of passenger number entries in the cache
	 * 
	 * @return the number of passenger number entries in the cache
	 */
	@Override
	public int getNumberOfEntries() {
		// TODO Auto-generated method stub
		return arrayOfPassengers.size();
	}

	/**
	 * Removes all data from the DAO, ready to start again if needed
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		arrayOfPassengers = null;
	}

}
