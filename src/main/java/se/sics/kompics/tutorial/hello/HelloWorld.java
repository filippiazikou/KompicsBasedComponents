package se.sics.kompics.tutorial.hello;

import se.sics.kompics.PortType;

public class HelloWorld extends PortType {
	{
		indication(World.class); /*outcoming - positive*/
		request(Hello.class);	/*incoming - negative*/
	}
}
