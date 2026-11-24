# Calculator Server

A Java server and client for doing arithmetic over a network. Clients can discover the server, send addition, subtraction, multiplication and division requests, and receive results. The server also reports usage statistics.

## Run

Requires JDK 21. Compile with `javac -encoding UTF-8 -d out src/*.java`. Start `java -cp out Main 5000`, then run `java -cp out CCSClient 5000` in another terminal.
