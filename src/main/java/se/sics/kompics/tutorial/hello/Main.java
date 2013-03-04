package se.sics.kompics.tutorial.hello;

import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Kompics;

public class Main extends ComponentDefinition {

	private Component component1, component2;

	public Main() {
		System.out.println("Main created.");
		component1 = create(Component1.class);
		component2 = create(Component2.class);
		connect(component1.required(HelloWorld.class), 
				component2.provided(HelloWorld.class));
	}
	public static void main(String[] args) {
		Kompics.createAndStart(Main.class, 1);
		Kompics.shutdown();
	}
}
