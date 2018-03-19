#!/bin/bash

# remove any previous folder
rm -rf regtest-data

# create a folder to store the private blockchain 
mkdir regtest-data

# start bitcoind in the right folder
bitcoind -regtest -addresstype=legacy -datadir=regtest-data/ -daemon 

