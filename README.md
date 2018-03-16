To check out how rmi works 

first compile the code `mvn clean compile`

then from the target/classes directory 

start the rmi registry 

`rmiregistry &`

now spin up a server:

`java org.b_verify.BVerifyServerApp`

and in two separate terminals also spin up client apps

`java org.b_verify.BVerifyClientApp "HENRY" "BIN" 0`

and 

`java org.b_verify.BVerifyClientApp "BIN" "HENRY" 1`



