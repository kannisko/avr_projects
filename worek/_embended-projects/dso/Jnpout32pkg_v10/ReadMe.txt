This is a further modified version of Jnpout32.DLL now works as a package, e.g.

     package jnpout32;

The new DLL is named "Jnpout32pkg.DLL" to avoid confusion with the others.
This updated version was provided to me by Holger Hoffman from Germany.

In folder 'ioTest_pkg' there are 4 files (and one sub-folder for package jnpout32):

  jnpout32\ioPort.java - wrapper class for Jnpout32.DLL

  jnpout32\pPort.java  - control functions which interface to the ioPort class

  ioTest.java - A short test program using standard LPT1: address, 0x378

  run.bat  - a batch file to build and run ioTest()


Only 2 Assumptions:

  You have Jnpout32pkg.DLL in your Windows\System32 folder.

  You have the java SDK installed to compile ioTest.


--
13-APR-2005
Douglas Beattie Jr.
<beattidp@ieee.org>
http://www.hytherion.com/beattidp/
