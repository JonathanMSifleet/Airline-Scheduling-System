package solution;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import baseclasses.Aircraft;
import baseclasses.CabinCrew;
import baseclasses.DoubleBookedException;
import baseclasses.FlightInfo;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;
import baseclasses.InvalidAllocationException;
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

		List<FlightInfo> remainingAllocations = schedule.getRemainingAllocations();

		for (int i = 0; i < remainingAllocations.size(); i++) {

			List<Aircraft> unallocatedAircrafts = aircrafts.getAllAircraft();
			unallocatedAircrafts.remove(aircrafts.findAircraftByTailCode("A320"));

			List<Pilot> unallocatedPilots = crew.getAllPilots();

			List<CabinCrew> unallocatedCabinCrew = crew.getAllCabinCrew();

			// gets flight data
			int flightNumber = remainingAllocations.get(i).getFlight().getFlightNumber();
			LocalDate flightDate = remainingAllocations.get(i).getDepartureDateTime().toLocalDate();
			int numPassengers = passengerNumbers.getPassengerNumbersFor(flightNumber, flightDate);

			System.out.println("Flight number: " + flightNumber + ", date: " + flightDate);
			System.out.println(
					"Departure location: " + remainingAllocations.get(i).getFlight().getDepartureAirportCode());
			/////////////////////

			// determine smallest valid plane
			Aircraft aircraftToUse = determineSmallestAircraft(aircrafts, aircraftToRemove, numPassengers,
					remainingAllocations.get(i), schedule);
			System.out.println("Aircraft location: " + aircraftToUse.getStartingPosition());

			System.out.println("Type code: " + aircraftToUse.getTypeCode());
			/////////////////////////////////////

			System.out.println(
					"Number of passengers: " + numPassengers + ", number of seats: " + aircraftToUse.getSeats());

			// get captain:
			Pilot captainToUse = determineCaptain(crew, aircraftToUse, unallocatedPilots);
			/////////////////////

			unallocatedPilots.remove(captainToUse);

			// get first officer:
			Pilot firstOfficerToUse = determineFirstOfficer(crew, aircraftToUse, unallocatedPilots);
			////////////////

			// get list of cabin crew:
			int numCrew = aircraftToUse.getCabinCrewRequired();
			System.out.println("Number of crew required: " + numCrew);

			List<CabinCrew> validCabinCrew = crew.findCabinCrewByTypeRating(aircraftToUse.getTypeCode());
			List<CabinCrew> cabinCrewToUse = new ArrayList<>();

			List<CabinCrew> suitableCrew = intersectCC(validCabinCrew, unallocatedCabinCrew);

			for (int k = 0; k < numCrew; k++) {
				cabinCrewToUse.add(suitableCrew.get(k));
			}
			///////////////

			try {
				schedule.allocateAircraftTo(aircraftToUse, remainingAllocations.get(i));
				unallocatedAircrafts.remove(aircraftToUse);

				schedule.allocateCaptainTo(captainToUse, remainingAllocations.get(i));
				System.out.println("Captain to use: " + captainToUse.getSurname());

				schedule.allocateFirstOfficerTo(firstOfficerToUse, remainingAllocations.get(i));
				System.out.println("FO to use: " + firstOfficerToUse.getSurname());
				System.out.println("Cabin crew to use: ");

				for (int j = 0; j < cabinCrewToUse.size(); j++) { // curCrew : cabinCrewToUse) {
					schedule.allocateCabinCrewTo(cabinCrewToUse.get(j), remainingAllocations.get(i));
					System.out.print(cabinCrewToUse.get(j).getSurname() + ", ");
				}

				System.out.println();

				schedule.completeAllocationFor(remainingAllocations.get(i));
				System.out.println("Flight allocated");

			} catch (DoubleBookedException | InvalidAllocationException e) {
				// TODO Auto-generated catch block

				// System.out.println(e.toString());

				e.printStackTrace();
			}
			System.out.println("----------");

		}

		return schedule;
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
		 * List<Pilot> suitableCaptains = intersectPilots(validPilots, allCaptains);
		 * 
		 * try { return suitableCaptains.get(0); } catch (Exception e) {
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
		 * List<Pilot> suitableFOs = intersectFOs(validPilots, allCaptains);
		 * 
		 * try { return suitableFOs.get(0); } catch (Exception e) {
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

		for (Pilot curPilot : validPilots) {
			if (allCaptains.contains(curPilot)) {
				intersectList.add(curPilot);
			}
		}

		return intersectList;

	}

	List<Pilot> intersectFOs(List<Pilot> validPilots, List<Pilot> allFOs) {

		List<Pilot> intersectList = new ArrayList<>();

		for (Pilot curFO : validPilots) {
			if (allFOs.contains(curFO)) {
				intersectList.add(curFO);
			}
		}

		return intersectList;

	}

	List<CabinCrew> intersectCC(List<CabinCrew> validCabinCrew, List<CabinCrew> unallocatedCabinCrew) {

		List<CabinCrew> intersectList = new ArrayList<>();

		for (CabinCrew curCabinCrew : unallocatedCabinCrew) {
			if (validCabinCrew.contains(curCabinCrew)) {
				intersectList.add(curCabinCrew);
			}
		}

		return intersectList;

	}
}