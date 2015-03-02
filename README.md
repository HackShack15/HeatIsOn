# HeatIsOn
The Heat is On! Android controlled emissivity heater.
This is the code for the android portion of the project. The app communicates via bluetooth with an Arduino board, which turns the
fan on and off, depending on the current temperature in relation to the 'goal' temperature. The Arduino also sends the current
temp back to the phone as an unsigned byte via the same bluetooth connection. Built for UB Hacking Fall 2014, very simple demo.
