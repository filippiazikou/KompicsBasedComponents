package se.sics.kompics.tutorial.hello;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Start;

public class Component2 extends ComponentDefinition {
	
	private Negative<HelloWorld> hwPort = provides(HelloWorld.class);
	
	public Component2() {
		System.out.println("Component2 created.");
		subscribe(startHandler, control);
		subscribe(helloHandler, hwPort);
	}
	Handler<Start> startHandler = new Handler<Start>() {
		public void handle(Start event) {
			System.out.println("Component2 started.");
		}
	};
	Handler<Hello> helloHandler = new Handler<Hello>() {
		public void handle(Hello event) {
			System.out.println("Component2 received Hello event with message: "
					+ event.getMessage());
			trigger(new World(), hwPort);
		}
	};
}
