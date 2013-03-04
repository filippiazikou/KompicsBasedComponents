package se.kth.ict.id2203.app1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.kth.ict.id2203.application.Application0;
import se.kth.ict.id2203.application.Application0Init;
import se.kth.ict.id2203.application.ApplicationContinue;
import se.kth.ict.id2203.application.Pp2pMessage;
import se.kth.ict.id2203.console.Console;
import se.kth.ict.id2203.console.ConsoleLine;
import se.kth.ict.id2203.flp2p.FairLossPointToPointLink;
import se.kth.ict.id2203.pfdport.PerfectFailureDetector;
import se.kth.ict.id2203.pfdport.PfdCrash;
import se.kth.ict.id2203.pp2p.PerfectPointToPointLink;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Kompics;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public class Application1a extends ComponentDefinition{
	Positive<Timer> timer = requires(Timer.class);
	Positive<Console> con = requires(Console.class);
	Positive<PerfectFailureDetector> pfd = requires(PerfectFailureDetector.class);
	
	private static final Logger logger =
			LoggerFactory.getLogger(Application1a.class);

	private Set<Address> neighborSet;
	private Address self;
	private long gama;
	private long delta;
	
	private List<String> commands;
	private boolean blocking;

	
	/**
	 * Instantiates a new application0.
	 */
	public Application1a() {
		subscribe(handleInit, control);
		subscribe(handleStart, control);
		subscribe(handleContinue, timer);
		subscribe(handleConsoleInput, con);
		subscribe(handlePfdCrash, pfd);
	}
	
	Handler<Application1aInit> handleInit = new Handler<Application1aInit>() {
		public void handle(Application1aInit event) {
			neighborSet = event.getNeighborSet();
			self = event.getSelf();
			gama = event.getGama();
			delta = event.getDelta();
			
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
	
	Handler<PfdCrash> handlePfdCrash = new Handler<PfdCrash>() {
		public void handle(PfdCrash event) {
			logger.info("Received a crash from {}", event.getSource());
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
		} else if (cmd.startsWith("X")) {
			doShutdown();
		} else if (cmd.equals("help")) {
			doHelp();
		} else if (cmd.equals("$DONE")) {
			logger.info("DONE ALL OPERATIONS");
		} else {
			logger.info("Bad command: '{}'. Try 'help'", cmd);
		}
	}

	private final void doHelp() {
		logger.info("Available commands: S<n>, help, X");
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
