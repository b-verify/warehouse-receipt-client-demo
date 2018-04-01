First install the package and its dependencies by running 

`$mvn clean install`

The system should pass the tests and return "BUILD SUCCESS"

Next to start bitcoind and setup the regtest environment run 

`$sh setup_regtest.sh`

and to send funds to the server

`$sh send_funds.sh`

Now everything is setup to start the server

`$mvn exec:java -Dexec.mainClass=org.b_verify.server.BVerifyServerApp`

look at the console output and it will print something like this 

`2018-04-01 13:07:43,513 [org.b_verify.server.BVerifyServerApp.main()] INFO  BVerifyServerApp::main - network=regtest | addr=mgesAzJfaZuhbiF1ZDvoGNRbQBsdzGwVKE | txid=acb5f7ce4075cc57dbf468b268403da8b4683408a4ce585af1a594c0cb32db58`

The server will keep running. Now in a separate terminal window start a client by running 

`$mvn exec:java -Dexec.mainClass=org.b_verify.client.BVerifyClientGui`

The GUI should pop up and you should enter the _txid_ , _addr_ and _network_ from the server output to the client gui and hit the __SYNC__ button. This starts the download of the blockchain and processing. The sync will succeed, but since there are no commitments yet the latest commitment will be blank. 


Things are pretty boring with two clients. Instead open up another client by following the same procedure in a separate window.

To transfer funds from one client to another copy the _client address_ from one client into the transfer section of the other. Enter and amount and hit the _transfer_ button. Now because of a bug it is required to manually generate a block. To do this use the shell script

`$sh mine_block.sh`

This will publish the server commitment, which BOTH clients will read and verify. The latest commitment section should update accordingly.
