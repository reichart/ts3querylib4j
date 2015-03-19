#ts3querylib4j

A pure Java library to administrate a TeamSpeak 3 Voice over IP server via the server query interface.

This library strives to be
  * object-oriented
  * simple to use
  * extensible

## Concept ##

All commands sent and responses received via the query server protocol are handled as objects. The library takes care of parsing responses and (un)escaping string values according to the Teamspeak 3 Query Server documentation.

Where possible, responses are converted into (lists of) simple POJOs (Plain Old Java Objects) with meaningful getters, setters and methods. This way an application can use this library without ever having to deal with the low-level server query protocol.

For advanced or (yet) unimplemented functionality, you can always reach down to the lower level of the protocol and handle commands and responses yourself.

## A few quick Examples ##

High-level object-oriented usage:

```
VirtualServer server = ... ;

for (Message message : vserver.getMessages()) {
	System.out.println(message.getId() + ": " + message.getSubject());
	message.setFlag(true); // set read
}
```

Low-level command/response usage with conversion of server reply into Java object:

```
public Message get(final int messageID) {
	return send(command("messageget").with("msgid", messageID)).as(Message.class);
}
```
