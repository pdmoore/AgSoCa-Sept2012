import static org.junit.Assert.*;

import java.util.Observable;
import java.util.Observer;

import junit.framework.Assert;

import org.junit.Test;
import org.omg.CosNaming.IstringHelper;


public class PomodoroSessionTest {

	@Test
	public void WHEN_SessionIsCreated_THEN_itHas25Minutes() {
		
		PomodoroSession mediator = new PomodoroSession();

		assertEquals(25 * 60 * 1000, mediator.timeRemaining());
	}

	@Test
	public void WHEN_SessionIsStartedANDItTicks_THEN_timeHasElapsed() throws Exception {
		
		PomodoroSession session = new PomodoroSession();
		int initialTime = session.timeRemaining();
		
		session.start();
		session.tick();
		
		assertTrue("Time should be decrementing after we start",
				session.timeRemaining() < initialTime);
	}

	
	@Test
	public void WHEN_SessionRunsOut_THEN_DoneIsSignaled() throws Exception {
		
		PomodoroSession session = new PomodoroSession();

		DoneObserver doneObserver = new DoneObserver();
		session.addObserver(doneObserver);
		
		session.start();
		for (int i = 25 * 60 - 1; i > 0; i--) {
			session.tick();
		}
		
		assertFalse(doneObserver.receivedDone);
		
		session.tick();
		assertTrue(doneObserver.receivedDone);
	}

	@Test
	public void GIVEN_SessionStarted_WHEN_TickHappens_THEN_OneSecondHasElapsed() throws Exception {
		
		PomodoroSession session = new PomodoroSession();
				
		session.start();
		
		session.tick();
		session.tick();
		
		assertEquals(25 * 60 * 1000 - 2000, session.timeRemaining());
	}
	
	@Test
	public void WHEN_SessionIsTicked_THEN_TickEventOccurs() throws Exception {

		PomodoroSession session = new PomodoroSession();

		TickObserver tickObserver = new TickObserver();
		session.addObserver(tickObserver);

		session.start();
		session.tick();

		assertEquals(1, tickObserver.count);
	}

	public static class TickObserver implements Observer {

		public int count;

		@Override
		public void update(Observable o, Object arg) {
			count++;
		}

	}
	
	
	public static class DoneObserver implements Observer {
		public boolean receivedDone;

		@Override
		public void update(Observable o, Object arg) {
			receivedDone = true;
		}
	}


	
	
	class PomodoroSession extends Observable {
		
		private int timeRemaining = 25 * 60 * 1000;

		// this stinks - why "int"???
		public int timeRemaining() {
			return timeRemaining;
		}

		public void tick() {
			timeRemaining -= 1000;
			
			if (timeRemaining == 0) {
				notifyDone();
			} else 
			{
				notifyTick();
			}
		}

		public void start() {
		
			
		}

		private void notifyDone() {

			timeRemaining = 0;

			setChanged();
			notifyObservers();
		}

		private void notifyTick() {

			setChanged();
			notifyObservers();
		}

	}
}
