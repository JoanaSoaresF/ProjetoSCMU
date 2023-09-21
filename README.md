# ProjetoSCMU
Project developed as part of the Mobile and Ubiquitous Computing Systems course.
In this project we intend to develop a "smart" doorbell. The aim is to install the doorbell at the user's entrance so that they can :
- have their entrance illuminated at night when the doorbell detects movement;
- be notified of the presence of someone at their door, either by approaching or by pressing the doorbell button;
- be able to visualise the person at your door via a photo;
- be able to interact with the person at your door via a small LCD monitor for displaying written messages;
- interact with the doorbell, authorising or not authorising the entry of the person who rang it.

The mobile application should include a series of commands that allow the user to carry out certain actions "on demand". In particular, the user should be able to switch on the entrance light, start capturing images from the camera, unlock/lock the door and show a message on the display.

In this application it should also be possible to access the events (proximity detection, door opening, ...) detected by the system and consult the information collected during the event. The user must also be able to respond promptly to event notifications from the system. 

The system (doorbell) must be able to communicate with a server in order to store data on detected incidents, namely: date, type of incident and image captured. The mobile application would periodically check the occurrences on the server, and should notify the user of a new occurrence. The user can then choose to respond to the notification by sending a message or unlocking the door.
