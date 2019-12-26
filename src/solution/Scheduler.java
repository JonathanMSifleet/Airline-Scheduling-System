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
import baseclasses.Schedule;
import baseclasses.SchedulerRunner;

public class Scheduler implements IScheduler {

	@Override
	public Schedule generateSchedule(IAircraftDAO aircrafts, ICrewDAO crew, IRouteDAO routes, IPassengerNumbersDAO passengerNumbers, LocalDate startDate, LocalDate endDate) {
		// TODO Auto-generated method stub

		Schedule schedule = new Schedule(routes, startDate, endDate);
		schedule.sort();

		List<FlightInfo> remainingAllocations = new ArrayList<>();
		remainingAllocations = schedule.getRemainingAllocations();

		List<Aircraft> unallocatedAircrafts = new ArrayList<>();
		unallocatedAircrafts = aircrafts.getAllAircraft();

		for (int i = 0; i < remainingAllocations.size(); i++) {

			// gets flight data
			int flightNumber = remainingAllocations.get(i).getFlight().getFlightNumber();
			LocalDate flightDate = remainingAllocations.get(i).getDepartureDateTime().toLocalDate();
			int numPassengers = passengerNumbers.getPassengerNumbersFor(flightNumber, flightDate);

			System.out.println("Flight number: " + flightNumber + ", date: " + flightDate);
			System.out.println("Departure location: " + remainingAllocations.get(i).getFlight().getDepartureAirportCode());
			// System.out.println("Passengers: " + numPassengers);

			// determine smallest valid plane
			Aircraft aircraftToUse = determineSmallestAircraft(aircrafts, numPassengers, remainingAllocations.get(i), schedule);
			System.out.println("Aircraft location: " + aircraftToUse.getStartingPosition());

			System.out.println("Type code: " + aircraftToUse.getTypeCode());

			List<Pilot> potentialPilots = crew.findPilotsByTypeRating(aircraftToUse.getTypeCode());
			// List<Pilot> potentialPilots =
			// crew.findPilotsByHomeBaseAndTypeRating(aircraftToUse.getStartingPosition(),
			// aircraftToUse.getTypeCode());

			System.out.println("Potential pilots:");

			if (potentialPilots.size() == 0) {
				System.out.println("No pilots found in aircraft homebase");
			}

			for (int j = 0; j < potentialPilots.size(); j++) {
				System.out.println((j + 1) + ") " + potentialPilots.get(j).getTypeRatings() + ", home base: " + potentialPilots.get(j).getHomeBase());
			}

			try {
				schedule.allocateAircraftTo(aircraftToUse, remainingAllocations.get(i));
				unallocatedAircrafts.remove(aircraftToUse);
				// schedule.allocateCaptainTo(pilotToUse, remainingAllocations.get(i));
				// System.out.println(pilotToUse); // aircraftToUse.getTailCode());

				System.out.println("Aicraft tailcode: " + aircraftToUse.getTailCode());
				System.out.println();

			} catch (DoubleBookedException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
			}
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

	Aircraft determineSmallestAircraft(IAircraftDAO aircrafts, int numPassengers, FlightInfo thisFlight, Schedule schedule) {

		List<Aircraft> validAircraft = aircrafts.findAircraftBySeats(numPassengers);
		Aircraft aircraftToUse = new Aircraft();

		aircraftToUse.setSeats(10000);
		for (Aircraft curAircraft : validAircraft) {
			if (curAircraft.getSeats() < aircraftToUse.getSeats()) { // && !schedule.hasConflict(curAircraft, thisFlight)) {
				aircraftToUse = curAircraft;
			}
		}

		return aircraftToUse;

	}

}