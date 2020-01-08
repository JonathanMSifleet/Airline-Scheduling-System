package solution;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import baseclasses.QualityScoreCalculator;
import baseclasses.Schedule;
import baseclasses.SchedulerRunner;

public class Scheduler implements IScheduler {

	boolean isCompetition = false;

	@Override
	public Schedule generateSchedule(IAircraftDAO aircrafts, ICrewDAO crew, IRouteDAO routes,
			IPassengerNumbersDAO passengerNumbers, LocalDate startDate, LocalDate endDate) {
		// TODO Auto-generated method stub

		Schedule schedule = new Schedule(routes, startDate, endDate);

		schedule.sort();

		List<FlightInfo> remainingAllocations = schedule.getRemainingAllocations();
		List<FlightInfo> allAllocations = new ArrayList<>();
		allAllocations.addAll(remainingAllocations);
		int validAllocations = allAllocations.size();

		List<Pilot> unallocatedPilots = crew.getAllPilots();
		List<CabinCrew> unallocatedCabinCrew = crew.getAllCabinCrew();

		Aircraft lastPlane = new Aircraft();
		List<Aircraft> listOfLastPlanes = new ArrayList<>();
		Pilot lastCaptain = new Pilot();
		Pilot lastFO = new Pilot();
		List<CabinCrew> lastCabinCrew = new ArrayList<>();

		int moduloNum;
		int allocationsToMake = -1;

		if (aircrafts.getNumberOfAircraft() == 4) {
			moduloNum = 4;
			allocationsToMake = 32;
		} else {
			isCompetition = true;
			moduloNum = 48;
			allocationsToMake = 6687;
		}

		int i = 0;

		for (FlightInfo curFlight : allAllocations) {
//		for (int i = 0; i < allAllocations.size(); i++) {

			if (isCompetition) {
				if (i % moduloNum == 0) {
					listOfLastPlanes.clear();
				}
			}

			if (i % moduloNum == 0) {
				unallocatedPilots.clear();
				unallocatedPilots = crew.getAllPilots();
				unallocatedCabinCrew.clear();
				unallocatedCabinCrew = crew.getAllCabinCrew();
				System.out.println("Availability reset");
				System.out.println("---------");
			}

			// gets flight data
			int flightNumber = curFlight.getFlight().getFlightNumber();
			LocalDate flightDate = curFlight.getDepartureDateTime().toLocalDate();
			int numPassengers = passengerNumbers.getPassengerNumbersFor(flightNumber, flightDate);

			System.out.println((i + 1) + ") Flight number: " + flightNumber + ", date: " + flightDate);
			System.out.println("Departure location: " + curFlight.getFlight().getDepartureAirportCode());

			Aircraft aircraftToUse = determineSmallestAircraft(aircrafts, numPassengers, curFlight, schedule, lastPlane,
					listOfLastPlanes);
			lastPlane = aircraftToUse;
			if (isCompetition) {
				listOfLastPlanes.add(lastPlane);
			}

			System.out.println("Aircraft location: " + aircraftToUse.getStartingPosition());
			System.out.println("Type code: " + aircraftToUse.getTypeCode());
			System.out.println("Tail code: " + aircraftToUse.getTailCode());
			System.out.println(
					"Number of passengers: " + numPassengers + ", number of seats: " + aircraftToUse.getSeats());
			System.out.println("Number of crew required: " + aircraftToUse.getCabinCrewRequired());

			Pilot captainToUse = determineCaptain(crew, aircraftToUse, unallocatedPilots, lastCaptain, lastFO);
			unallocatedPilots.remove(captainToUse);
			lastCaptain = captainToUse;
			System.out.println("Captain to use: " + captainToUse.getSurname());

			Pilot firstOfficerToUse = determineFirstOfficer(crew, aircraftToUse, unallocatedPilots, captainToUse,
					lastFO, lastCaptain);
			unallocatedPilots.remove(firstOfficerToUse);
			lastFO = firstOfficerToUse;
			System.out.println("FO to use: " + firstOfficerToUse.getSurname());

			List<CabinCrew> cabinCrewToUse = determineSuitableCabinCrew(crew, aircraftToUse, unallocatedCabinCrew,
					lastCabinCrew, i);
			unallocatedCabinCrew.removeAll(cabinCrewToUse);
			System.out.println("Cabin crew to use: ");
			for (CabinCrew curCrew : cabinCrewToUse) {
				System.out.print(curCrew.getSurname() + ", ");
			}
			if (i != 0) {
				lastCabinCrew.addAll(cabinCrewToUse);
			}

			try {

				allocateCrewAndCabin(schedule, crew, aircrafts, curFlight, cabinCrewToUse, aircraftToUse, captainToUse,
						firstOfficerToUse, numPassengers, i);

				System.out.println();

				try {
					schedule.completeAllocationFor(curFlight);
				} catch (InvalidAllocationException iae) {
					while (true) {
						try {
							allocateCrewAndCabin(schedule, crew, aircrafts, curFlight, cabinCrewToUse, aircraftToUse,
									captainToUse, firstOfficerToUse, numPassengers, i);
							schedule.completeAllocationFor(curFlight);
							break;
						} catch (Exception e1) {
						}
					}
				}

				if (schedule.isValid(curFlight)) {
					System.out.println("Flight allocated");
				} else {
					validAllocations--;
					System.out.println("Flight not valid");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("---------");
			i++;
		}

		if (isCompetition) {
			System.out.println(
					"Allocations : " + validAllocations + ", remaining " + (allocationsToMake - validAllocations));
		} else {
			System.out.println("Valid allocations: " + validAllocations);

		}

		QualityScoreCalculator score = new QualityScoreCalculator(aircrafts, crew, passengerNumbers, schedule);
		System.out.println("Score:" + score.calculateQualityScore());

		String[] describeScore = score.describeQualityScore();

		for (String curLine : describeScore) {
			System.out.println(curLine);
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

	void allocateCrewAndCabin(Schedule schedule, ICrewDAO crew, IAircraftDAO aircrafts, FlightInfo curFlight,
			List<CabinCrew> cabinCrewToUse, Aircraft aircraftToUse, Pilot captainToUse, Pilot firstOfficerToUse,
			int numPassengers, int i) {

		try {
			schedule.allocateAircraftTo(aircraftToUse, curFlight);
		} catch (DoubleBookedException e) {
			while (true) {
				try {
					schedule.allocateAircraftTo(selectRandomAircraft(aircrafts, numPassengers), curFlight);
					break;
				} catch (Exception e1) {
				}
			}
		}

		try {
			schedule.allocateCaptainTo(captainToUse, curFlight);
		} catch (DoubleBookedException e) {
			while (true) {
				try {
					schedule.allocateCaptainTo(selectRandomPilot(crew), curFlight);
					break;
				} catch (Exception e1) {
				}
			}
		}

		try {
			schedule.allocateFirstOfficerTo(firstOfficerToUse, curFlight);
		} catch (DoubleBookedException e) {
			while (true) {
				try {
					schedule.allocateFirstOfficerTo(selectRandomPilot(crew), curFlight);
					break;
				} catch (Exception e1) {
				}
			}
		}

		for (int j = 0; j < cabinCrewToUse.size(); j++) {
			try {
				schedule.allocateCabinCrewTo(cabinCrewToUse.get(j), curFlight);
			} catch (DoubleBookedException dbe) {
				while (true) {
					try {
						schedule.allocateCabinCrewTo(selectRandomCC(crew), curFlight);
						break;
					} catch (DoubleBookedException dbe1) {

					}
				}
			}
		}
	}

	Pilot selectRandomPilot(ICrewDAO crew) {

		List<Pilot> validCaptains = crew.getAllPilots();

		Random rand = new Random();

		return validCaptains.get(rand.nextInt(validCaptains.size()));

	}

	Aircraft selectRandomAircraft(IAircraftDAO aircrafts, int numPassengers) {

		List<Aircraft> validAircraft = aircrafts.getAllAircraft();

		Random rand = new Random();

		return validAircraft.get(rand.nextInt(validAircraft.size()));

	}

	CabinCrew selectRandomCC(ICrewDAO crew) {

		List<CabinCrew> validCC = crew.getAllCabinCrew();

		Random rand = new Random();

		return validCC.get(rand.nextInt(validCC.size()));

	}

	Aircraft determineSmallestAircraft(IAircraftDAO aircrafts, int numPassengers, FlightInfo thisFlight,
			Schedule schedule, Aircraft lastPlane, List<Aircraft> listOfLastPlanes) {

		List<Aircraft> validAircraft = aircrafts.findAircraftBySeats(numPassengers);

		Aircraft aircraftToUse = new Aircraft();

		aircraftToUse.setSeats(10000);
		for (Aircraft curAircraft : validAircraft) {
			if (!isCompetition) {
				if (curAircraft.getSeats() < aircraftToUse.getSeats() && curAircraft != lastPlane) {
					aircraftToUse = curAircraft;
				}
			} else {
				if (curAircraft.getSeats() < aircraftToUse.getSeats() && (!listOfLastPlanes.contains(curAircraft))) {
					aircraftToUse = curAircraft;
				}
			}
		}

		if (aircraftToUse.getSeats() == 10000) {
			// no suitable aircraft found :(
			for (Aircraft curAircraft : validAircraft) {
				if (curAircraft.getSeats() < aircraftToUse.getSeats()) {
					aircraftToUse = curAircraft;
				}
			}
		}

		return aircraftToUse;

	}

	Pilot determineCaptain(ICrewDAO crew, Aircraft aircraftToUse, List<Pilot> unallocatedPilots, Pilot lastCaptain,
			Pilot lastFO) {
		List<Pilot> validPilots = new ArrayList<>();
		validPilots = crew.findPilotsByTypeRating(aircraftToUse.getTypeCode());

		List<Pilot> allCaptains = getListOfCaptains(unallocatedPilots);
		List<Pilot> suitableCaptains = intersectPilots(validPilots, allCaptains);

		for (Pilot curCaptain : suitableCaptains) {
			if (curCaptain != lastFO && curCaptain != lastCaptain) {
				return curCaptain;
			}
		}

		// System.out.println("No suitable captains found, now trying all captains");
		for (Pilot curCaptain : unallocatedPilots) {
			if (curCaptain != lastFO && curCaptain != lastCaptain) {
				return curCaptain;
			}
		}

		System.out.println("Null captain");
		return null;

	}

	Pilot determineFirstOfficer(ICrewDAO crew, Aircraft aircraftToUse, List<Pilot> unallocatedPilots,
			Pilot captainToUse, Pilot lastFO, Pilot lastCaptain) {
		List<Pilot> validPilots = new ArrayList<>();
		validPilots = crew.findPilotsByTypeRating(aircraftToUse.getTypeCode());

		List<Pilot> allFOs = getListOfFirstOfficers(unallocatedPilots);
		List<Pilot> suitableFOs = intersectFOs(validPilots, allFOs);

		for (Pilot curFO : suitableFOs) {
			if (curFO != captainToUse && curFO != lastFO && curFO != lastCaptain) {
				return curFO;
			}
		}

		// System.out.println("No suitable FOs found, now trying all FOs");
		for (Pilot curFO : unallocatedPilots) {
			if (curFO != captainToUse && curFO != lastFO && curFO != lastCaptain) {
				return curFO;
			}
		}

		System.out.println("Null FO");
		return null;
	}

	List<CabinCrew> determineSuitableCabinCrew(ICrewDAO crew, Aircraft aircraftToUse,
			List<CabinCrew> unallocatedCabinCrew, List<CabinCrew> lastCabinCrew, int i) {
		List<CabinCrew> validCabinCrew = crew.findCabinCrewByTypeRating(aircraftToUse.getTypeCode());

		List<CabinCrew> suitableCrew = intersectCC(validCabinCrew, unallocatedCabinCrew);
		List<CabinCrew> cabinCrewToUse = new ArrayList<>();

		if (i != 0) {
			suitableCrew.removeAll(lastCabinCrew);
		}

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