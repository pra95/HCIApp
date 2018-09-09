# HCIApp
A Client Android application for HCISystem.

## Overview
1. The whole system is client-server interaction between an Android app and a Java desktop application.

2. The repository contains the source code for the Android application.

- The whole system can be visualized as an interaction between a Computer and a Smartphone.

![alt text](https://github.com/pra95/HCIApp/blob/master/data_flow.png "HCI Flow Chart")

- The block diagram shows two devices with a wireless Communication media. 
- The Smartphone has the HCI application used for taking input from the user with the help of inbuilt sensors in the Smartphone. 
- The data gathered is then sent to the intermediate data buffer. 
- The modules process the data to produce a command list. The command list is then used to perform the desired actions on the target application.