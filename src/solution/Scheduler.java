package solution;
import java.time.LocalDate;

import baseclasses.IAircraftDAO;
import baseclasses.ICrewDAO;
import baseclasses.IPassengerNumbersDAO;
import baseclasses.IRouteDAO;
import baseclasses.IScheduler;
import baseclasses.Schedule;
import baseclasses.SchedulerRunner;

public class Scheduler implements IScheduler {

	@Override
	public Schedule generateSchedule(IAircraftDAO arg0, ICrewDAO arg1, IRouteDAO arg2, IPassengerNumbersDAO arg3, LocalDate arg4, LocalDate arg5) {
		// TODO Auto-generated method stub
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

}
