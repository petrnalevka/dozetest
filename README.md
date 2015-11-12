# dozetest
Minimal Android app which shows that sensor gathering in Doze mode does not work when having a partial wake lock in a foreground service

Android just became a new category of documentation "semi-documented", you have to gather your knowledge on how Android works on G+ post.
Here is one of them https://plus.google.com/109720416927515295704/posts/Vph4KjPZ4LN.
+Dianne Hackborn posted about foreground services being able to gather sensor data during doze when holding a partial wake lock.

This project is a minimal implementation of that case which does not work at least on Build MRA58N, Nexus 5.

I did run the test over night and below are the data. 

DOZE.INFO: [01:18:12.640, main]: Data 11.747253
.. log entry every minute..
DOZE.INFO: [02:30:12.987, main]: Data 19.627228
.. gap ..
DOZE.INFO: [03:20:40.661, main]: Data 90.62346
.. log entry every minute ..
DOZE.INFO: [03:24:40.691, main]: Data 7.68367
.. gap ..
DOZE.INFO: [04:59:22.820, main]: Data 21.719559
.. log entry every minute ..
DOZE.INFO: [05:08:22.848, main]: Dat a 7.2907867
.. gap .. until 8:20

To come up with an implementation quickly I used our internal logging class which does in-memory logging and sending logs, but I will get rid of this dependency.﻿
