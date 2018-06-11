#!/bin/sh
sleep 20
hciconfig hci0 up
hciconfig hci0 sspmode 1
hciconfig hci0 piscan
sudo bluetooth-agent 1234