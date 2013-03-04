package se.kth.ict.id2203.app2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.console.Console;
import se.kth.ict.id2203.console.ConsoleLine;
import se.kth.ict.id2203.lpbport.PbBroadcast;
import se.kth.ict.id2203.lpbport.PbDeliver;
import se.kth.ict.id2203.lpbport.ProbabilisticBroadcast;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Kompics;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public class Application2 extends ComponentDefinition{
	Positive<Timer> timer = requires(Timer.class);
	Positive<Console> con = requires(Console.class);
	Positive<ProbabilisticBroadcast> lpb = requires(ProbabilisticBroadcast.class);
	
	private static final Logger logger =
			LoggerFactory.getLogger(Application2.class);

	private Set<Address> neighborSet;
	private Address self;
	
	private List<String> commands;
	private boolean blocking;

	/**
	 * Instantiates a new application0.
	 */
	public Application2() {
		subscribe(handleInit, control);
		subscribe(handleStart, control);
		subscribe(handleContinue, timer);
		subscribe(handleConsoleInput, con);
		subscribe(handlePbDeliver, lpb);
	}
	
	Handler<Application2Init> handleInit = new Handler<Application2Init>() {
		public void handle(Application2Init event) {
			neighborSet = event.getNeighborSet();
			self = event.getSelf();
			commands = new ArrayList<String>(Arrays.asList(event.getCommandScript().split(":")));
            commands.add("$DONE");
	        blocking = false;
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
	
	Handler<PbDeliver> handlePbDeliver = new Handler<PbDeliver>() {
		public void handle(PbDeliver event) {
			logger.info("Received a deliver from {} with message {}", event.getSource(), event.getMsg());
			
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
		} else if (cmd.startsWith("B")) {
			doSend(cmd.substring(1));
		} else if (cmd.startsWith("X")) {
			
		} else if (cmd.equals("help")) {
			doHelp();
		} else if (cmd.equals("$DONE")) {
			logger.info("DONE ALL OPERATIONS");
		} else {
			logger.info("Bad command: '{}'. Try 'help'", cmd);
		}
	}

	private final void doSend(String message) {
			logger.info("Sending message {}", message);
			trigger(new PbBroadcast(message), lpb);
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
