package dsotest;

import jnpout32.ioPort;


public class pPort
{
   ioPort pp;                   // wrapper class for 'Jnpout32.dll'
                               // with methods:
                               //    int Out32(int port, int value);
                               //    int Inp32(int port);
   short base;
   short status;
   short control;

   public pPort(int baseAddres)
   {
	  pp = new ioPort();
          base = (short)baseAddres;
          status = (short)(baseAddres+1);
          control = (short)(baseAddres+2);
          
          pp.Out32((short)(baseAddres+0x402),(short)0x20);
   }
   
   public void setData(short data){
       pp.Out32(base, data);
   }
   public short getData(){
       return pp.Inp32(base);
   }
   public void setStatus(short sts){
       pp.Out32(status, sts);
   }
public void setControl(short ctrl){
       pp.Out32(control, ctrl);
   }

}

