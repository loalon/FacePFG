# Computer engineering final project 2017-2018 

# FACIAL RECOGNITION SYSTEM FOR MOBILE DEVICES 

This project seeks the development of a library of functions and a set of applications that facilitate facial recognition tasks. Also, this project pursues that the developed software will be installed in a mobile hardware platform, therefore allowing 
it to be a part of a telepresence robot, accessible by mobile devices via wireless connection and usable as a desktop device. 
The project consists on 4 different parts installed on a Raspberry Pi and an additional one developed for Android devices:

* **FCModule** library of functions developed in Python. The tasks of this library include: image capture,  facial  detection  and  cropping,  communication  with  Microsoft  Azure’s  cloud service  to  send  the  cropped  images  and  recover  of  the  identification  of  the  most probable candidates, face tagging and, finally, the return of the results as text or image. 
Also,  three  applications  that  use  this  library  have  been  developed.  

* **FaceRecon** is an example of the usage of the library and serves as reference for the other applications. 

* **FaceBT** is an application that works in the background and allows the connection of other devices to the Raspberry Pi, allowing the use of the facial recognition functions. 

* **FacePi** python application with a graphic user interface providing the user an easy access to the facial recognition functions. 

* **FacePal** Finally, an Android application named  has been developed that allows the addition of new persons and faces to the facial recognition system. 
##
