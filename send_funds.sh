#!/bin/bash

ADDRESS_TO_FUND=mgesAzJfaZuhbiF1ZDvoGNRbQBsdzGwVKE

# generate 101 blocks to spend a coinbase tx 
bitcoin-cli -regtest -datadir=regtest-data/ generate 101

# send 25 bitcoins to address (funding tx)
bitcoin-cli -regtest -datadir=regtest-data/ sendtoaddress $ADDRESS_TO_FUND 25

# give the funding tx one confirmation 
bitcoin-cli -regtest -datadir=regtest-data/ generate 1

