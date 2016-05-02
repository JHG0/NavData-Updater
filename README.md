# NavData Updater
_by Josh Glottmann_

This software automatically updates NavData for vSTARS and vERAM. 

The NavData is provided by [Casey Diers](http://www.myfsim.com/sectorfilecreation/vSTARSDump.php). 

__[Download](https://github.com/JHG0/NavData-Updater/blob/master/NavData%20Updater.jar?raw=true)__

#### NavDataExceptions.txt
This is an optional file that you can create in either `%appdata%\vSTARS\NavData` or `C:\Users\Username\AppData\Local\vERAM` (_the vSTARS directory is read first_).

The file contains additions and deletions to the NavData. 

##### Example Exceptions
`WAYPT` The waypoint `WAYPT` is deleted. 

`ARPT|Airport` The waypoint `ARPT` is deleted only if it is of type `Airport` (_types listed below_).

`WAY*` Any waypoint starting with `WAY` is deleted. 

`ARP*|Airport` Any waypoint starting with `ARP` and of type `Airport` is deleted. 

`AIRMN|32.5238111|-85.772963` The waypoint `AIRMN` is added at latitude `32.5238111`, longitude `-85.772963`.

__Valid Types:__ `Airport`, `Intersection`, `VOR`, `NDB`
