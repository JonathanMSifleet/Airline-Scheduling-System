package solution;

import java.nio.file.Paths;

import baseclasses.DataLoadingException;
import baseclasses.IAircraftDAO;

/**
 * This class allows you to run the code in your classes yourself, for testing and development
 */
public class Main {

	public static void main(String[] args) {	
		IAircraftDAO aircraft = new AircraftDAO();
		
		try {
			aircraft.loadAircraftData(Paths.get("./data/aircraft.csv"));
		}
		catch (DataLoadingException dle) {
			System.err.println("Error loading aircraft data");
			dle.printStackTrace();
		}
	}

}
