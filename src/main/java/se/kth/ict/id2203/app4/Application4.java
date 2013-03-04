package se.kth.ict.id2203.app4;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.console.Console;
import se.kth.ict.id2203.console.ConsoleLine;
import se.kth.ict.id2203.pucport.*;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Kompics;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public class Application4 extends ComponentDefinition{
	Positive<Timer> timer = requires(Timer.class);
	Positive<Console> con = requires(Console.class);
	Positive<UniformConsensus> puc = requires(UniformConsensus.class);
	
	private static final Logger logger =
			LoggerFactory.getLogger(Application4.class);

	private Set<Address> neighborSet;
	private Address self;
	
	private List<String> commands;
	private boolean blocking;

	
	private HashMap<Integer, Integer> idValues;
	private int counterP;
	private int counterD;
	private boolean waitFlag;
	private int waitTime;
	
	/**
	 * Instantiates a new application0.
	 */
	public Application4() {
		subscribe(handleInit, control);
		subscribe(handleStart, control);
		subscribe(handleContinue, timer);
		subscribe(handleConsoleInput, con);
		subscribe(handleDecide, puc);
	}
	
	Handler<Application4Init> handleInit = new Handler<Application4Init>() {
		public void handle(Application4Init event) {
			neighborSet = event.getNeighborSet();
			self = event.getSelf();
			commands = new ArrayList<String>(Arrays.asList(event.getCommandScript().split(":")));
            commands.add("$DONE");
	        blocking = false;
	        
	        idValues = new HashMap<Integer, Integer>();
			counterP = 0;
			counterD = 0;
			waitFlag = false;
			waitTime = 0;
		}
	};
	
	Handler<Start> handleStart = new Handler<Start>() {
		public void handle(Start event) {
			doNextCommand();
		}
	};
	
	Handler<ApplicationContinue> handleContinue = new Handler<ApplicationContinue>() {
		public void handle(ApplicationContinue event) {
			logger.info("Woke up from sleep");
			blocking = false;
			doNextCommand();
		}
	};
	
	Handler<ConsoleLine> handleConsoleInput = new Handler<ConsoleLine>() {
		@Override
		public void handle(ConsoleLine event) {
			commands.addAll(Arrays.asList(event.getLine().trim().split(":")));
			doNextCommand();
		}
	};
	
	Handler<UcDecide> handleDecide = new Handler<UcDecide>() {
		public void handle(UcDecide event) {
			idValues.put(event.getId(), event.getValue());
			logger.info("Decided on value {} for id {}", event.getValue(), event.getId());
			counterD += 1;
			if (waitFlag == true)
				doWait(0);
			doNextCommand();
		}
	};
	
	private final void doNextCommand() {
		while (!blocking && !commands.isEmpty()) {
            doCommand(commands.remove(0));
		}
	}

	private void doCommand(String cmd) {
		if (cmd.startsWith("S")) {
			doSleep(Integer.parseInt(cmd.substring(1)));
		}else if (cmd.startsWith("P")) {
			String tmp = cmd.substring(1);
			String arr[] = tmp.split("-");
			doPropose(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
		}else if (cmd.startsWith("D")) {
			doWait(Integer.parseInt(cmd.substring(1)));
		}else if (cmd.startsWith("W")) {
			doSort();
		}else if (cmd.equals("help")) {
			doHelp();
		} else if (cmd.equals("$DONE")) {
			logger.info("DONE ALL OPERATIONS");
		} else {
			logger.info("Bad command: '{}'. Try 'help'", cmd);
		}
	}
	
	private void doPropose(int id, int value) {
		logger.info("Propose value {} for instance ID {} ", value, id);
		counterP += 1;
		trigger(new UcPropose(id, value), puc);
	}
	
	private void doWait(int sec) {
		int thisTime;
		if (sec == 0)
			thisTime = waitTime;
		else
			thisTime = sec;
		if (counterD == counterP) {
			logger.info("Wait for {} sec", sec);
			waitFlag = false;
			waitTime = 0;
			
			ScheduleTimeout st = new ScheduleTimeout(thisTime);
			st.setTimeoutEvent(new ApplicationContinue(st));
			trigger(st, timer);
			blocking = true;
		}
		else {
			logger.info("Waiting for all decisions.. ");
			waitFlag = true;
			waitTime = sec;
		}
		
	}
	
	private void doSort() {
		logger.info("Print sorted values: ");
		for (int i : idValues.keySet()) {
			logger.info("Value {} for instance ID {} ", idValues.get(i), i);
		}
	}
	
	private final void doHelp() {
		logger.info("Available commands: S<n>, help, X");
		logger.info("Bm: broadcasts message 'm'");
		logger.info("Sn: sleeps 'n' milliseconds before the next command");
		logger.info("help: shows this help message");
		logger.info("X: terminates this process");
	}
	private void doSleep(long delay) {
		logger.info("Sleeping {} milliseconds...", delay);

		ScheduleTimeout st = new ScheduleTimeout(delay);
		st.setTimeoutEvent(new ApplicationContinue(st));
		trigger(st, timer);
		
		blocking = true;
	}

	private void doShutdown() {
		System.out.println("2DIE");
		System.out.close();
		System.err.close();
		Kompics.shutdown();
		blocking = true;
	}
}
