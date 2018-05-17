First install the package and its dependencies by running 

`$mvn clean install`

The system should pass the tests and return "BUILD SUCCESS"

Now navigate to the directory where the b-verify/server-demo project is located and run 

`$./run_server.sh ./run_alice.sh ./run_bob.sh`

in order to start up the server and two mock depositors, Alice and Bob.

Then open the b-verify/desktop-client-demo in Eclipse and run the BVerifyClientDemo.java file located in 
src/main/java/org.b_verify.client as a Java Application.

The desktop client configuration GUI should pop up and then enter the host and port number of the server.
You can connect to a remote server or the default one that is started locally by running the ./run_server.sh script.
The host number for the default is 127.0.0.1 and the port number is 50051. Once entered, hit the START SYNC button.
This starts the downloading and processing of commitments from the server.

When the sync suceeds, the desktop client GUI will open. You can now fill out the PROCESS NEW RECEIPT fields and issue a
new receipt request to either of the mock depositors, Alice and Bob. You can specify which depositor by writing in 'Alice' or
'Bob' in the DEPOSITOR field of the section. Once Alice and Bob approves the new receipt request, which these mock depositors 
do automatically, the new receipt will be confirmed and added to the ALL RECEIPTS table.

If there is a DYMO M10 scale available, you can weigh items which automatically fills out and cryptographically signs the WEIGHT 
field of the PROCESS NEW RECEIPT section. This demonstrates the application's ability to integrate with IoT devices. A 
similar IoT process for filling out the VOLUME and HUMIDITY would ideally be implemented in a real-world implementation of 
this application.

The mock depositors are also able to request receipt transfers on the mobile client application. Once a transfer receipt is 
requested and confirmed by the two involved depositors, the desktop client will check the commitment and proof from the 
server reflecting the transferred receipt ownership and then the ALL RECEIPTS table will be updated to show the new owner 
of the receipt.

After issue receipt requests and transfer receipt requests are updated, these actions will be batched by the server and 
then commitments will be published by the server. Information about the latest server commitment will be reflected in the 
LAST VERIFIED COMMITMENT section of the desktop client GUI.

NOTE: In order to run the desktop-client-demo project in Eclipse, the server-demo project must be included in the build path.
Otherwise, a jar of the server-demo project may be included as a dependency in the pom.xml file. The desktop-client-demo project
could also be run as a jar on the command line.
