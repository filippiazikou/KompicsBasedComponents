package se.kth.ict.id2203.console.java;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import se.kth.ict.id2203.console.Console;
import se.kth.ict.id2203.console.ConsoleLine;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Start;

public class JavaConsole extends ComponentDefinition implements Runnable {
	Negative<Console> con = provides(Console.class);

	private final Thread thread;

	public JavaConsole() {
		thread = new Thread(this);
		thread.setDaemon(true);

		subscribe(handleStart, control);
		subscribe(handleOutput, con);
	}

	Handler<Start> handleStart = new Handler<Start>() {
		@Override
		public void handle(Start event) {
			thread.start();
		}
	};

	Handler<ConsoleLine> handleOutput = new Handler<ConsoleLine>() {
		@Override
		public void handle(ConsoleLine event) {
			System.out.println(event.getLine());
		}
	};

	@Override
	public void run() {
		BufferedReader in = new BufferedReader(
				new InputStreamReader(System.in));
		while (true) {
			try {
				String line = in.readLine();
				trigger(new ConsoleLine(line) , con);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
