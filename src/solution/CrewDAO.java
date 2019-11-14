package solution;
import java.nio.file.Path;
import java.util.List;

import baseclasses.CabinCrew;
import baseclasses.Crew;
import baseclasses.DataLoadingException;
import baseclasses.ICrewDAO;
import baseclasses.Pilot;

/**
 * The CrewDAO is responsible for loading data from JSON-based crew files 
 * It contains various methods to help the scheduler find the right pilots and cabin crew
 */
public class CrewDAO implements ICrewDAO {

	/**
	 * Loads the crew data from the specified file, adding them to the currently loaded crew
	 * Multiple calls to this function, perhaps on different files, would thus be cumulative
	 * @param p A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause" indicates the underlying exception
	 */
	@Override
	public void loadCrewData(Path p) throws DataLoadingException {
		// TODO Auto-generated method stub

	}
	
	/**
	 * Returns a list of all the cabin crew based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at the airport with the specified airport code
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBase(String airportCode) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find cabin crew for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the cabin crew based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find cabin crew for
	 * @return a list of all the cabin crew currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<CabinCrew> findCabinCrewByTypeRating(String typeCode) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a list of all the pilots based at the airport with the specified airport code
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at the airport with the specified airport code
	 */
	@Override
	public List<Pilot> findPilotsByHomeBase(String airportCode) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 * @param typeCode the type of plane to find pilots for
	 * @param airportCode the three-letter airport code of the airport to check for
	 * @return a list of all the pilots based at a specific airport AND qualified to fly a specific aircraft type
	 */
	@Override
	public List<Pilot> findPilotsByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 * @param typeCode the type of plane to find pilots for
	 * @return a list of all the pilots currently loaded who are qualified to fly the specified type of plane
	 */
	@Override
	public List<Pilot> findPilotsByTypeRating(String typeCode) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a list of all the cabin crew currently loaded
	 * @return a list of all the cabin crew currently loaded
	 */
	@Override
	public List<CabinCrew> getAllCabinCrew() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a list of all the crew, regardless of type
	 * @return a list of all the crew, regardless of type
	 */
	@Override
	public List<Crew> getAllCrew() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns a list of all the pilots currently loaded
	 * @return a list of all the pilots currently loaded
	 */
	@Override
	public List<Pilot> getAllPilots() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfCabinCrew() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Returns the number of pilots currently loaded
	 * @return the number of pilots currently loaded
	 */
	@Override
	public int getNumberOfPilots() {
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
