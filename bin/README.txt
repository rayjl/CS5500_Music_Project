-----------------------------------------------------------

Last Revised: 24 October 2014

-----------------------------------------------------------

Team Name: 
Unicorn

Team Members / Emails:
Alexander Do - chaolun88@gmail.com
John Jacob - jacob.joh@husky.neu.edu
Raymond Li - rayjl@uw.edu

-----------------------------------------------------------

CS5500 Assignment #5
Assigned: Wednesday, 1 October 2014
Due: Wednesday, 15 October 2014

-----------------------------------------------------------

About:
This program is a rapid prototype that can compare two audio
files and returns a match if the files are the same song.
This current version will be able to compare files in the
format / file extension of ".mp3" and ".wav". This prototype
follows he specification provided by the instructor for
Assignment 6 with some of its details listed below.

Dependencies:		AudioMatching.java
                    AudioFile.java
                    ComplexNumber.java
                    FingerPrint.java
                    Format.java
                    FFT.java
                    dam

Permissions:        chmod 755 dam

Compilation and 
Execution:          Prototype's build process will result in 
                    software that can be invoked by cd'ing to 
                    the directory containing the software's 
                    executable and executing a command of one of 
                    the following forms:

                    ./dam -f <pathname> -f <pathname>
                    ./dam -d <pathname> -d <pathname>
                    ./dam -f <pathname> -d <pathname>
                    ./dam -d <pathname> -f <pathname>

                    where <pathname> is a Linux path name. If
                    a <pathname> is preceded by "-f", then the
                    <pathname> must end in ".wav" or ".mp3".
                    If a <pathname> is preceded by "-d" the <pathname>represents a directory which then the software should compare every recording in the directory
                    against the second <pathname>'s recording or
                    every recording if it is a directory.

Note: "dam" abbreviates "detect audio misappropriations".

-----------------------------------------------------------

References:

[1] Avery Li-Chun Wang, "An Industrial-Strength Audio
Search Algorithm". PDF website:
www.ee.columbia.edu/~dpwe/papers/Wang03-shazam.pdf

[2] Cheng Yang, "Music Audio Characteristic Sequence
Indexing For Similarity Retrieval". PDF website:
infolabs.stanford.edu/~yangc/pub/cy-waspaa01.pdf

[3] Richard G. Baldwin, "How and Why Spectral Analysis
Works". Article website:
http://www.developer.com/java/other/article.php/3374611/
Fun-with-Java-How-and-Why-Spectral-Analysis-Works.htm

[4] Richard G. Baldwin, "Understanding the Fast Fourier
Transform". Article website:
http://www.developer.com/java/other/article.php/3457251/
Fun-with-Java-Understanding-the-Fast-Fourier-Transform-FFT
-Algorithm.htm

[5] Columbia University (2006-2007). FFT.java [Software]. 
Source Code Website: 
https://www.ee.columbia.edu/~ronw/code/MEAPsoft
/doc/html/FFT_8java-source.html.
Authorized by William Clinger on October 15, 2014.

[6] "Acoustic Fingerprint". Article Website:
http://en.wikipedia.org/wiki/Acoustic_fingerprint

[7] "Digital Signal Processing". Article Website:
http://en.wikipedia.org/wiki/Digital_signal_processing

[8] "Discrete Fourier Transform". Article Website:
http://en.wikipedia.org/wiki/Discrete_Fourier_transform

[9] "Fast Fourier Transform". Article Website:
http://en.wikipedia.org/wiki/Fast_Fourier_transform

[10] "Frequency Spectrum". Article Website:
http://en.wikipedia.org/wiki/Frequency_spectrum

[11] "Hann Function". Article Website:
http://en.wikipedia.org/wiki/Hann_function

[12] "Window Function". Article Website:
http://en.wikipedia.org/wiki/Window_function

[13] "WAVE PCM Soundfile Format". Documentation Website:
https://ccrma.stanford.edu/courses/422/projects/WaveFormat/

[14] "Next Power of 2". Website:
http://www.geeksforgeeks.org/next-power-of-2/

[15] Japp Haitsma. "A Highly Robust Audio Fingerprinting System". 
Article Website:
http://www.nhchau.com/files/AudioFingerprint-02-FP04-2.pdf

-----------------------------------------------------------


