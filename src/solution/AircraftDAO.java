package solution;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import baseclasses.Aircraft;
import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;
import java.util.ArrayList; // import the ArrayList class

/**
 * The AircraftDAO class is responsible for loading aircraft data from CSV files
 * and contains methods to help the system find aircraft when scheduling
 */
public class AircraftDAO implements IAircraftDAO {
	
	ArrayList<Aircraft> globalArrayOfAircraft = new ArrayList<>();
	
	
	
	/**
	 * Loads the aircraft data from the specified file, adding them to the currently loaded aircraft
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 * Initially, this contains some starter code to help you get started in reading the CSV file...
	 */
	@Override
	public void loadAircraftData(Path p) throws DataLoadingException {
		
		Aircraft temp = new Aircraft();		
		ArrayList<Aircraft> arrayListOfAircraft = new ArrayList<>();
		
		try {
			//open the file
			BufferedReader reader = Files.newBufferedReader(p);
			
			//read the file line by line
			String line = "";
			
			//skip the first line of the file - headers
			reader.readLine();
			
			while( (line = reader.readLine()) != null) {
				//each line has fields separated by commas, split into an array of fields
				String[] fields = line.split(",");
				
				//put some of the fields into variables: check which fields are where atop the CSV file itself
				String tailcode = fields[0];
				String type = fields[1];
				String manufacturer = fields[2];
				String model = fields[3];
				int seats = Integer.parseInt(fields[4]);
				int cabinCrewReq = Integer.parseInt(fields[5]);
				String startPos = fields[6];
				
				temp.setTailCode(tailcode);
				temp.setTypeCode(type);
				temp.setManufacturer(manufacturer);
				temp.setModel(model);
				temp.setSeats(seats);
				temp.setCabinCrewRequired(cabinCrewReq);
				temp.setStartingPosition(startPos);
				
				// http://sandbox.kriswelsh.com/advprog/

				//print a line explaining what we've found
				System.out.println("Aircraft: " + tailcode + " is a " + type + " with " + seats + " seats.");
				arrayListOfAircraft.add(temp);
				temp = null;
			}
		}
		
		catch (IOException ioe) {
			//There was a problem reading the file
			throw new DataLoadingException(ioe);
		}
		
		globalArrayOfAircraft = arrayListOfAircraft;

	}
	
	/**
	 * Returns a list of all the loaded Aircraft with at least the specified number of seats
	 * @param seats the number of seats required
	 * @return a List of all the loaded aircraft with at least this many seats
	 */
	@Override
	public List<Aircraft> findAircraftBySeats(int seats) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a list of all the loaded Aircraft that start at the specified airport code
	 * @param startingPosition the three letter airport code of the airport at which the desired aircraft start
	 * @return a List of all the loaded aircraft that start at the specified airport
	 */
	@Override
	public List<Aircraft> findAircraftByStartingPosition(String startingPosition) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the individual Aircraft with the specified tail code.
	 * @param tailCode the tail code for which to search
	 * @return the aircraft with that tail code, or null if not found
	 */
	@Override
	public Aircraft findAircraftByTailCode(String tailCode) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a List of all the loaded Aircraft with the specified type code
	 * @param typeCode the type code of the aircraft you wish to find
	 * @return a List of all the loaded Aircraft with the specified type code
	 */
	@Override
	public List<Aircraft> findAircraftByType(String typeCode) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a List of all the currently loaded aircraft
	 * @return a List of all the currently loaded aircraft
	 */
	@Override
	public List<Aircraft> getAllAircraft() {
		return null;
	}

	/**
	 * Returns the number of aircraft currently loaded 
	 * @return the number of aircraft currently loaded
	 */
	@Override
	public int getNumberOfAircraft() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Unloads all of the aircraft currently loaded, ready to start again if needed
	 */
	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
