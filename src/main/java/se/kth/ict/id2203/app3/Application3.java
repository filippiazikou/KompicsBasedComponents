package se.kth.ict.id2203.app3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.ict.id2203.console.Console;
import se.kth.ict.id2203.console.ConsoleLine;
import se.kth.ict.id2203.riwcport.*;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Kompics;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.address.Address;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

public class Application3 extends ComponentDefinition{
	Positive<Timer> timer = requires(Timer.class);
	Positive<Console> con = requires(Console.class);
	Positive<ReadImposeWriteConsult> riwc = requires(ReadImposeWriteConsult.class);
	
	private static final Logger logger =
			LoggerFactory.getLogger(Application3.class);

	private Set<Address> neighborSet;
	private Address self;
	
	private List<String> commands;
	private boolean blocking;

	/**
	 * Instantiates a new application0.
	 */
	public Application3() {
		subscribe(handleInit, control);
		subscribe(handleStart, control);
		subscribe(handleContinue, timer);
		subscribe(handleConsoleInput, con);
		subscribe(handleReadMessageReturn, riwc);
		subscribe(handleWriteMessageReturn, riwc);
	}
	
	Handler<Application3Init> handleInit = new Handler<Application3Init>() {
		public void handle(Application3Init event) {
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
	
	Handler<ReadMessageReturn> handleReadMessageReturn = new Handler<ReadMessageReturn>() {
		public void handle(ReadMessageReturn event) {
			logger.info("Received a read for register {} with value {}", event.getRegister(), event.getValue());
			blocking = false;	
			doNextCommand();
		}
	};
	
	Handler<WriteMessageReturn> handleWriteMessageReturn = new Handler<WriteMessageReturn>() {
		public void handle(WriteMessageReturn event) {
			logger.info("Received a write from {} for register {}", event.getSource(), event.getRegister());
			blocking = false;
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
		} else if (cmd.startsWith("W")) {
			doWrite(cmd.substring(1));
		} else if (cmd.startsWith("R")) {
			doRead();
		}else if (cmd.equals("help")) {
			doHelp();
		} else if (cmd.equals("$DONE")) {
			logger.info("DONE ALL OPERATIONS");
		} else {
			logger.info("Bad command: '{}'. Try 'help'", cmd);
		}
	}

	private final void doWrite(String valStr) {
		
		int val = Integer.parseInt(valStr);
			logger.info("Writting value {}", val);
			trigger(new WriteMessage(self, 0, val), riwc);
			blocking = true;
	}
	
	private final void doRead() {
			logger.info("Reading value from 0 reg");
			trigger(new ReadMessage(self, 0), riwc);
			blocking = true;
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
