package solution;

import baseclasses.CabinCrew;
import baseclasses.Crew;
import baseclasses.DataLoadingException;
import baseclasses.ICrewDAO;
import baseclasses.Pilot;
import baseclasses.Pilot.Rank;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.json.*;

/** The CrewDAO is responsible for loading data from JSON-based crew files It
 * contains various methods to help the scheduler find the right pilots and
 * cabin crew */
public class CrewDAO implements ICrewDAO {

	ArrayList<CabinCrew> globalArrayOfCabinCrew = new ArrayList<>();
	ArrayList<Pilot> globalArrayOfPilots = new ArrayList<>();
	ArrayList<Crew> globalArrayOfAllCrew = new ArrayList<>();

	/** Loads the crew data from the specified file, adding them to the currently
	 * loaded crew Multiple calls to this function, perhaps on different files,
	 * would thus be cumulative
	 * 
	 * @param  p                    A Path pointing to the file from which data could be loaded
	 * @throws DataLoadingException if anything goes wrong. The exception's "cause"
	 *                              indicates the underlying exception */
	@Override
	public void loadCrewData(Path p) throws DataLoadingException {
		// TODO Auto-generated method stub

		Pilot tempPilot = new Pilot();
		CabinCrew tempCabinCrew = new CabinCrew();

		try {

			// read the JSON into a String
			BufferedReader br = Files.newBufferedReader(p);

			String json = "";
			String line = "";
			while ((line = br.readLine()) != null) {
				json = json + line;
			}

			// turns j
			JSONObject root = new JSONObject(json);

			// splits JSON root array into array of pilots and cabin ctew
			JSONArray pilots = root.getJSONArray("pilots");
			JSONArray cabinCrew = root.getJSONArray("cabincrew");

			try {
				for (int i = 0; i < pilots.length(); i++) {

					JSONObject pilot = pilots.getJSONObject(i);

					tempPilot.setForename(pilot.get("forename").toString());
					tempPilot.setSurname(pilot.get("surname").toString());
					tempPilot.setRank(Rank.valueOf(pilot.get("rank").toString().toUpperCase()));
					tempPilot.setHomeBase(pilot.get("homebase").toString());

					JSONArray pilotTypeRating = pilot.getJSONArray("typeRatings");
					for (int j = 0; j < pilotTypeRating.length(); j++) {
						tempPilot.setQualifiedFor(pilotTypeRating.get(j).toString());
					}

					// System.out.println(tempPilot.getHomeBase());

					globalArrayOfPilots.add(tempPilot);
					tempPilot = new Pilot();
				}
			} catch (Exception e) {
				throw new DataLoadingException();
			}

			for (int k = 0; k < cabinCrew.length(); k++) {
				JSONObject cabcrew = cabinCrew.getJSONObject(k);
				JSONArray qualifiedFor = cabcrew.getJSONArray("typeRatings");
				try {
					tempCabinCrew.setForename(cabcrew.get("forename").toString());
					tempCabinCrew.setSurname(cabcrew.get("surname").toString());
					tempCabinCrew.setHomeBase(cabcrew.get("homebase").toString());

					for (int j = 0; j < qualifiedFor.length(); j++) {
						tempCabinCrew.setQualifiedFor(qualifiedFor.get(j).toString());
					}

					globalArrayOfCabinCrew.add(tempCabinCrew);
					tempCabinCrew = new CabinCrew();
				} catch (Exception e) {
					throw new DataLoadingException();
				}
			}

			globalArrayOfAllCrew.addAll(globalArrayOfCabinCrew);
			globalArrayOfAllCrew.addAll(globalArrayOfPilots);
		} catch (Exception e) {
			throw new DataLoadingException();
		}

	}

	/** Returns a list of all the cabin crew based at the airport with the specified
	 * airport code
	 * 
	 * @param  airportCode the three-letter airport code of the airport to check for
	 * @return             a list of all the cabin crew based at the airport with the specified
	 *                     airport code */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBase(String airportCode) {
		// TODO Auto-generated method stub

		ArrayList<CabinCrew> tempList = new ArrayList<>();

		for (int i = 0; i < globalArrayOfCabinCrew.size(); i++) {
			if (globalArrayOfCabinCrew.get(i).getHomeBase().equals(airportCode)) {
				tempList.add(globalArrayOfCabinCrew.get(i));
			}
		}

		return tempList;
	}

	/** Returns a list of all the cabin crew based at a specific airport AND
	 * qualified to fly a specific aircraft type
	 * 
	 * @param  typeCode    the type of plane to find cabin crew for
	 * @param  airportCode the three-letter airport code of the airport to check for
	 * @return             a list of all the cabin crew based at a specific airport AND
	 *                     qualified to fly a specific aircraft type */
	@Override
	public List<CabinCrew> findCabinCrewByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		// TODO Auto-generated method stub
		ArrayList<CabinCrew> tempList = new ArrayList<>();

		for (int i = 0; i < globalArrayOfCabinCrew.size(); i++) {
			if (globalArrayOfCabinCrew.get(i).getHomeBase().equals(airportCode) && globalArrayOfCabinCrew.get(i).isQualifiedFor(typeCode)) {
				tempList.add(globalArrayOfCabinCrew.get(i));
			}
		}
		return tempList;
	}

	/** Returns a list of all the cabin crew currently loaded who are qualified to
	 * fly the specified type of plane
	 * 
	 * @param  typeCode the type of plane to find cabin crew for
	 * @return          a list of all the cabin crew currently loaded who are qualified to
	 *                  fly the specified type of plane */
	@Override
	public List<CabinCrew> findCabinCrewByTypeRating(String typeCode) {
		// TODO Auto-generated method stub

		ArrayList<CabinCrew> tempList = new ArrayList<>();

		for (int i = 0; i < globalArrayOfCabinCrew.size(); i++) {
			if (globalArrayOfCabinCrew.get(i).getTypeRatings().contains(typeCode)) {
				tempList.add(globalArrayOfCabinCrew.get(i));
			}
		}
		return tempList;
	}

	/** Returns a list of all the pilots based at the airport with the specified
	 * airport code
	 * 
	 * @param  airportCode the three-letter airport code of the airport to check for
	 * @return             a list of all the pilots based at the airport with the specified
	 *                     airport code */
	@Override
	public List<Pilot> findPilotsByHomeBase(String airportCode) {
		// TODO Auto-generated method stub
		ArrayList<Pilot> tempList = new ArrayList<>();

		for (int i = 0; i < globalArrayOfPilots.size(); i++) {
			if (globalArrayOfPilots.get(i).getHomeBase().contains(airportCode)) {
				tempList.add(globalArrayOfPilots.get(i));
			}
		}
		return tempList;
	}

	/** Returns a list of all the pilots based at a specific airport AND qualified to
	 * fly a specific aircraft type
	 * 
	 * @param  typeCode    the type of plane to find pilots for
	 * @param  airportCode the three-letter airport code of the airport to check for
	 * @return             a list of all the pilots based at a specific airport AND qualified to
	 *                     fly a specific aircraft type */
	@Override
	public List<Pilot> findPilotsByHomeBaseAndTypeRating(String typeCode, String airportCode) {
		// TODO Auto-generated method stub
		ArrayList<Pilot> tempList = new ArrayList<>();

		for (int i = 0; i < globalArrayOfPilots.size(); i++) {
			if (globalArrayOfPilots.get(i).getHomeBase().equals(airportCode) && globalArrayOfPilots.get(i).isQualifiedFor(typeCode)) {
				tempList.add(globalArrayOfPilots.get(i));
			}
		}
		return tempList;
	}

	/** Returns a list of all the pilots currently loaded who are qualified to fly
	 * the specified type of plane
	 * 
	 * @param  typeCode the type of plane to find pilots for
	 * @return          a list of all the pilots currently loaded who are qualified to fly
	 *                  the specified type of plane */
	@Override
	public List<Pilot> findPilotsByTypeRating(String typeCode) {
		// TODO Auto-generated method stub

		ArrayList<Pilot> tempList = new ArrayList<>();
		for (int i = 0; i < globalArrayOfPilots.size(); i++) {
			if (globalArrayOfPilots.get(i).getTypeRatings().contains(typeCode)) {
				tempList.add(globalArrayOfPilots.get(i));
			}
		}
		return tempList;
	}

	/** Returns a list of all the cabin crew currently loaded
	 * 
	 * @return a list of all the cabin crew currently loaded */
	@Override
	public List<CabinCrew> getAllCabinCrew() {
		// TODO Auto-generated method stub

		List<CabinCrew> tempAllCabinCrew = new ArrayList<>();

		tempAllCabinCrew.addAll(globalArrayOfCabinCrew);

		return tempAllCabinCrew;
	}

	/** Returns a list of all the crew, regardless of type
	 * 
	 * @return a list of all the crew, regardless of type */
	@Override
	public List<Crew> getAllCrew() {
		// TODO Auto-generated method stub

		List<Crew> tempAll = new ArrayList<>();

		tempAll.addAll(globalArrayOfAllCrew);

		return tempAll;
	}

	/** Returns a list of all the pilots currently loaded
	 * 
	 * @return a list of all the pilots currently loaded */
	@Override
	public List<Pilot> getAllPilots() {
		// TODO Auto-generated method stub

		List<Pilot> tempAllPilots = new ArrayList<>();

		tempAllPilots.addAll(globalArrayOfPilots);

		return tempAllPilots;

	}

	@Override
	public int getNumberOfCabinCrew() {
		// TODO Auto-generated method stub
		return globalArrayOfCabinCrew.size();
	}

	/** Returns the number of pilots currently loaded
	 * 
	 * @return the number of pilots currently loaded */
	@Override
	public int getNumberOfPilots() {
		// TODO Auto-generated method stub
		return globalArrayOfPilots.size();
	}

	/** Unloads all of the crew currently loaded, ready to start again if needed */
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		globalArrayOfAllCrew.clear();
		globalArrayOfCabinCrew.clear();
		globalArrayOfPilots.clear();
	}
}