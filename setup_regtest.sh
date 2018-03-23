#!/bin/bash

# kill any previous setup
killall bitcoind
# killall rmiregistry

# remove any previous folder
rm -rf regtest-data
rm -rf server
rm -rf client-data-*

# create a folder to store the private blockchain 
mkdir regtest-data

# start bitcoind in the right folder
bitcoind -regtest -addresstype=legacy -datadir=regtest-data/ -daemon 

# start java rmi 
#cd target/classes
#rmiregistry & 
