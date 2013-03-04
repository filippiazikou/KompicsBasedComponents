package se.sics.kompics.tutorial.hello;

import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;

public class Component1 extends ComponentDefinition {

	private Positive<HelloWorld> hwPort = requires(HelloWorld.class);

	public Component1() {
		System.out.println("Component1 created.");
		subscribe(startHandler, control);
		subscribe(worldHandler, hwPort);
	}
	Handler<Start> startHandler = new Handler<Start>() {
		public void handle(Start event) {
			System.out.println("Component1 started. Triggering Hello...");
			trigger(new Hello("Hi there!"), hwPort);
		}
	};
	Handler<World> worldHandler = new Handler<World>() {
		public void handle(World event) {
			System.out.println("Component1 received World event.");
		}
	};
}
