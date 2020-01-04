package solution;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import baseclasses.Aircraft;
import baseclasses.CabinCrew;
import baseclasses.FlightInfo;
import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;
import baseclasses.Pilot;
import baseclasses.Pilot.Rank;
import baseclasses.QualityScoreCalculator;
import baseclasses.Schedule;
import baseclasses.SchedulerRunner;

public class Scheduler implements IScheduler {

	@Override
	public Schedule generateSchedule(IAircraftDAO aircrafts, ICrewDAO crew, IRouteDAO routes, IPassengerNumbersDAO passengerNumbers, LocalDate startDate, LocalDate endDate) {
		// TODO Auto-generated method stub

		Schedule schedule = new Schedule(routes, startDate, endDate);

		schedule.sort();

		List<FlightInfo> remainingAllocations = schedule.getRemainingAllocations();
		List<FlightInfo> allAllocations = new ArrayList<>();
		allAllocations.addAll(remainingAllocations);
		int numAllocations = allAllocations.size();
		int validAllocations = numAllocations;

		List<Pilot> unallocatedPilots = crew.getAllPilots();
		List<CabinCrew> unallocatedCabinCrew = crew.getAllCabinCrew();

		String curDay = "Wed";

		String lastTailCode = "";
		Pilot lastCaptain = new Pilot();
		Pilot lastFO = new Pilot();
		List<CabinCrew> lastCabinCrew = new ArrayList<>();

		for (int i = 0; i < numAllocations; i++) {

			if (!allAllocations.get(i).getFlight().getDayOfWeek().equals(curDay)) {
				curDay = allAllocations.get(i).getFlight().getDayOfWeek();
				System.out.println("It's a new day...");
				System.out.println("----------");
			}

			if (i % 4 == 0) {
				unallocatedPilots.clear();
				unallocatedPilots = crew.getAllPilots();
				unallocatedCabinCrew.clear();
				unallocatedCabinCrew = crew.getAllCabinCrew();
			}

			// gets flight data
			int flightNumber = allAllocations.get(i).getFlight().getFlightNumber();
			LocalDate flightDate = allAllocations.get(i).getDepartureDateTime().toLocalDate();
			int numPassengers = passengerNumbers.getPassengerNumbersFor(flightNumber, flightDate);

			Aircraft aircraftToUse = determineSmallestAircraft(aircrafts, numPassengers, allAllocations.get(i), schedule, lastTailCode);
			lastTailCode = aircraftToUse.getTailCode();

			Pilot captainToUse = determineCaptain(crew, aircraftToUse, unallocatedPilots, lastCaptain, lastFO);
			unallocatedPilots.remove(captainToUse);
			lastCaptain = captainToUse;

			Pilot firstOfficerToUse = determineFirstOfficer(crew, aircraftToUse, unallocatedPilots, captainToUse, lastFO);
			unallocatedPilots.remove(firstOfficerToUse);
			lastFO = firstOfficerToUse;

			List<CabinCrew> cabinCrewToUse = determineSuitableCabinCrew(crew, aircraftToUse, unallocatedCabinCrew, lastCabinCrew, i);
			unallocatedCabinCrew.removeAll(cabinCrewToUse);

			if (i != 0) {
				lastCabinCrew.addAll(cabinCrewToUse);
			}

			printFlightTelemetry(allAllocations, i, flightNumber, flightDate, aircraftToUse, numPassengers, captainToUse, firstOfficerToUse, cabinCrewToUse);

			try {
				schedule.allocateAircraftTo(aircraftToUse, allAllocations.get(i));
				schedule.allocateCaptainTo(captainToUse, allAllocations.get(i));
				schedule.allocateFirstOfficerTo(firstOfficerToUse, allAllocations.get(i));

				for (int j = 0; j < cabinCrewToUse.size(); j++) {
					schedule.allocateCabinCrewTo(cabinCrewToUse.get(j), allAllocations.get(i));
				}

				System.out.println();

				if (schedule.isValid(allAllocations.get(i))) {
					System.out.println("Flight allocated");
				} else {
					System.out.println("Flight not valid");
				}

				schedule.completeAllocationFor(allAllocations.get(i));

			} catch (Exception e) {
				// TODO Auto-generated catch block
				validAllocations--;
				e.printStackTrace();
			}
			System.out.println("---------");
		}
		System.out.println("Valid allocations: " + validAllocations);

		if (validAllocations == 32) {
			QualityScoreCalculator score = new QualityScoreCalculator(aircrafts, crew, passengerNumbers, schedule);
			System.out.println("Score:" + score.calculateQualityScore());

			String[] describeScore = score.describeQualityScore();

			for (String curLine : describeScore) {
				System.out.println(curLine);
			}

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

	void printFlightTelemetry(List<FlightInfo> allAllocations, int i, int flightNumber, LocalDate flightDate, Aircraft aircraftToUse, int numPassengers, Pilot captainToUse, Pilot firstOfficerToUse, List<CabinCrew> cabinCrewToUse) {
		System.out.println((i + 1) + ") Flight number: " + flightNumber + ", date: " + flightDate);
		System.out.println("Departure location: " + allAllocations.get(i).getFlight().getDepartureAirportCode());
		System.out.println("Aircraft location: " + aircraftToUse.getStartingPosition());
		System.out.println("Type code: " + aircraftToUse.getTypeCode());
		System.out.println("Tail code: " + aircraftToUse.getTailCode());
		System.out.println("Number of passengers: " + numPassengers + ", number of seats: " + aircraftToUse.getSeats());
		System.out.println("Number of crew required: " + aircraftToUse.getCabinCrewRequired());
		System.out.println("Captain to use: " + captainToUse.getSurname());
		System.out.println("FO to use: " + firstOfficerToUse.getSurname());
		System.out.println("Cabin crew to use: ");
		for (CabinCrew curCrew : cabinCrewToUse) {
			System.out.print(curCrew.getSurname() + ", ");
		}
	}

	Aircraft determineSmallestAircraft(IAircraftDAO aircrafts, int numPassengers, FlightInfo thisFlight, Schedule schedule, String lastTailCode) {

		List<Aircraft> validAircraft = aircrafts.findAircraftBySeats(numPassengers);

		Aircraft aircraftToUse = new Aircraft();

		aircraftToUse.setSeats(10000);
		for (Aircraft curAircraft : validAircraft) {
			if (curAircraft.getSeats() < aircraftToUse.getSeats() && (!curAircraft.getTailCode().equals(lastTailCode))) {
				aircraftToUse = curAircraft;
			}
		}

		return aircraftToUse;

	}

	Pilot determineCaptain(ICrewDAO crew, Aircraft aircraftToUse, List<Pilot> unallocatedPilots, Pilot lastCaptain, Pilot lastFO) {
		List<Pilot> validPilots = new ArrayList<>();
		validPilots = crew.findPilotsByTypeRating(aircraftToUse.getTypeCode());

		List<Pilot> allCaptains = getListOfCaptains(unallocatedPilots);
		List<Pilot> suitableCaptains = intersectPilots(validPilots, allCaptains);

		Collections.shuffle(suitableCaptains);
		Collections.shuffle(unallocatedPilots);
		// lastCaptain

		int i = 0;
		do {
			try {
				if (suitableCaptains.get(i) != lastCaptain) {
					return suitableCaptains.get(i);
				}
			} catch (Exception e) {
				if (unallocatedPilots.get(i) != lastCaptain) {
					return unallocatedPilots.get(i);
				}
				i++;
			}
		} while (true);

		/*
		 * try {
		 * return suitableCaptains.get(0);
		 * } catch (Exception e) {
		 * return unallocatedPilots.get(0);
		 * }
		 */

	}

	Pilot determineFirstOfficer(ICrewDAO crew, Aircraft aircraftToUse, List<Pilot> unallocatedPilots, Pilot captainToUse, Pilot lastFO) {
		List<Pilot> validPilots = new ArrayList<>();
		validPilots = crew.findPilotsByTypeRating(aircraftToUse.getTypeCode());

		List<Pilot> allFOs = getListOfFirstOfficers(unallocatedPilots);
		List<Pilot> suitableFOs = intersectFOs(validPilots, allFOs);

		Collections.shuffle(suitableFOs);
		Collections.shuffle(unallocatedPilots);

		int i = 0;
		do {
			try {
				if (suitableFOs.get(i) != captainToUse) {
					return suitableFOs.get(i);
				}
			} catch (Exception e) {
				int j = 0;
				do {
					if (unallocatedPilots.get(j) != captainToUse) {
						return unallocatedPilots.get(j);
					}
					j++;
				} while (true);
			}
			i++;
		} while (true);
	}

	List<CabinCrew> determineSuitableCabinCrew(ICrewDAO crew, Aircraft aircraftToUse, List<CabinCrew> unallocatedCabinCrew, List<CabinCrew> lastCabinCrew, int i) {
		List<CabinCrew> validCabinCrew = crew.findCabinCrewByTypeRating(aircraftToUse.getTypeCode());

		List<CabinCrew> suitableCrew = intersectCC(validCabinCrew, unallocatedCabinCrew);
		List<CabinCrew> cabinCrewToUse = new ArrayList<>();

		if (i != 0) {
			suitableCrew.removeAll(lastCabinCrew);
		}

		Collections.shuffle(suitableCrew);

		try {
			for (int j = 0; j < aircraftToUse.getCabinCrewRequired(); j++) {
				cabinCrewToUse.add(suitableCrew.get(j));
			}
		} catch (Exception e) {
			cabinCrewToUse.removeAll(cabinCrewToUse);
			for (int j = 0; j < aircraftToUse.getCabinCrewRequired(); j++) {
				cabinCrewToUse.add(unallocatedCabinCrew.get(j));
			}
		}

		return cabinCrewToUse;
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