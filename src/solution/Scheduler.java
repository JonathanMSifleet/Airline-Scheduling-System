package solution;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import baseclasses.Aircraft;
import baseclasses.DoubleBookedException;
import baseclasses.FlightInfo;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;
import baseclasses.Pilot;
import baseclasses.Pilot.Rank;
import baseclasses.Schedule;
import baseclasses.SchedulerRunner;

public class Scheduler implements IScheduler {

	@Override
	public Schedule generateSchedule(IAircraftDAO aircrafts, ICrewDAO crew, IRouteDAO routes,
			IPassengerNumbersDAO passengerNumbers, LocalDate startDate, LocalDate endDate) {
		// TODO Auto-generated method stub

		// creates lists:
		List<Aircraft> aircraftToRemoveList = aircrafts.findAircraftByType("A320");
		Aircraft aircraftToRemove = aircraftToRemoveList.get(0);

		Schedule schedule = new Schedule(routes, startDate, endDate);
		schedule.sort();

		List<FlightInfo> remainingAllocations = new ArrayList<>();
		remainingAllocations = schedule.getRemainingAllocations();

		List<Aircraft> unallocatedAircrafts = new ArrayList<>();
		unallocatedAircrafts = aircrafts.getAllAircraft();
		unallocatedAircrafts.remove(aircrafts.findAircraftByTailCode("A320"));

		List<Pilot> unallocatedPilots = new ArrayList<>();
		unallocatedPilots = crew.getAllPilots();

		///////////////

		for (FlightInfo flight : remainingAllocations) {

			// gets flight data
			int flightNumber = flight.getFlight().getFlightNumber();
			LocalDate flightDate = flight.getDepartureDateTime().toLocalDate();
			int numPassengers = passengerNumbers.getPassengerNumbersFor(flightNumber, flightDate);

			System.out.println("Flight number: " + flightNumber + ", date: " + flightDate);
			System.out.println("Departure location: " + flight.getFlight().getDepartureAirportCode());
			/////////////////////

			// determine smallest valid plane
			Aircraft aircraftToUse = determineSmallestAircraft(aircrafts, aircraftToRemove, numPassengers, flight,
					schedule);
			System.out.println("Aircraft location: " + aircraftToUse.getStartingPosition());

			System.out.println("Type code: " + aircraftToUse.getTypeCode());
			/////////////////////////////////////

			// get captain:
			Pilot captainToUse = determineCaptain(crew, aircraftToUse, unallocatedPilots);
			/////////////////////

			unallocatedPilots.remove(captainToUse);

			// get first officer:
			Pilot firstOfficerToUse = determineFirstOfficer(crew, aircraftToUse, unallocatedPilots);
			////////////////
			
			// get list of cabin crew:
			//List<CabinCrew>
			//

			try {
				schedule.allocateAircraftTo(aircraftToUse, flight);
				unallocatedAircrafts.remove(aircraftToUse);

				schedule.allocateCaptainTo(captainToUse, flight);
				System.out.println("Captain to use: " + captainToUse.getSurname());

				schedule.allocateFirstOfficerTo(firstOfficerToUse, flight);
				System.out.println("FO to use: " + firstOfficerToUse.getSurname());

				// to do: schedule.allocateCabinCrewTo(crew, flight);

			} catch (DoubleBookedException e) {
				// TODO Auto-generated catch block

				System.out.println(e.toString());

				// e.printStackTrace();
			}

			System.out.println("----------");

		}

		return null;
	}

	@Override
	public void setSchedulerRunner(SchedulerRunner arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	Aircraft determineSmallestAircraft(IAircraftDAO aircrafts, Aircraft aircraftToRemove, int numPassengers,
			FlightInfo thisFlight, Schedule schedule) {

		List<Aircraft> validAircraft = aircrafts.findAircraftBySeats(numPassengers);
		validAircraft.remove(aircraftToRemove);

		Aircraft aircraftToUse = new Aircraft();

		aircraftToUse.setSeats(10000);
		for (Aircraft curAircraft : validAircraft) {
			if (curAircraft.getSeats() < aircraftToUse.getSeats()) {
				aircraftToUse = curAircraft;
			}
		}

		return aircraftToUse;

	}

	Pilot determineCaptain(ICrewDAO crew, Aircraft aircraftToUse, List<Pilot> unallocatedPilots) {
		// get captain
		List<Pilot> validPilots = new ArrayList<>();
		validPilots = crew.findPilotsByTypeRating(aircraftToUse.getTypeCode());

		return validPilots.get(0);

		/*
		 * List<Pilot> allCaptains = getListOfCaptains(unallocatedPilots);
		 * 
		 * List<Pilot> potentialCaptains = intersectPilots(validPilots, allCaptains);
		 * 
		 * try { return potentialCaptains.get(0); } catch (Exception e) {
		 * System.out.println("No captains found"); return null; }
		 */
	}

	Pilot determineFirstOfficer(ICrewDAO crew, Aircraft aircraftToUse, List<Pilot> unallocatedPilots) {
		// get captain
		List<Pilot> validPilots = new ArrayList<>();
		validPilots = crew.findPilotsByTypeRating(aircraftToUse.getTypeCode());

		return validPilots.get(0);

		/*
		 * List<Pilot> allFOs = getListOfFirstOfficers(unallocatedPilots);
		 * 
		 * List<Pilot> potentialFOs = intersectFOs(validPilots, allCaptains);
		 * 
		 * try { return potentialFOs.get(0); } catch (Exception e) {
		 * System.out.println("No FOs found"); return null; }
		 */
	}

	List<Pilot> getListOfFirstOfficers(List<Pilot> unallocatedPilots) {

		List<Pilot> listOfFirstOfficers = new ArrayList<>();

		for (Pilot curPilot : unallocatedPilots) {
			if (curPilot.getRank() == Rank.FIRST_OFFICER) {
				listOfFirstOfficers.add(curPilot);
			}
		}

		return listOfFirstOfficers;
	}

	List<Pilot> getListOfCaptains(List<Pilot> unallocatedPilots) {

		List<Pilot> listOfCaptains = new ArrayList<>();

		for (Pilot curPilot : unallocatedPilots) {
			if (curPilot.getRank() == Rank.CAPTAIN) {
				listOfCaptains.add(curPilot);
			}
		}

		return listOfCaptains;
	}

	List<Pilot> intersectPilots(List<Pilot> validPilots, List<Pilot> allCaptains) {

		List<Pilot> intersectList = new ArrayList<>();

		for (Pilot curObject : validPilots) {
			if (allCaptains.contains(curObject)) {
				intersectList.add(curObject);
			}
		}

		return intersectList;

	}

	List<Pilot> intersectFOs(List<Pilot> validPilots, List<Pilot> allFOs) {

		List<Pilot> intersectList = new ArrayList<>();

		for (Pilot curObject : validPilots) {
			if (allFOs.contains(curObject)) {
				intersectList.add(curObject);
			}
		}

		return intersectList;

	}
}