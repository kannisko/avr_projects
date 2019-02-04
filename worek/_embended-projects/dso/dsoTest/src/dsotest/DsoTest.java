
package dsotest;


public class DsoTest {
          static short datum;
          static short Addr;
	    static pPort lpt;


     public static void main( String args[] )
     {
         lpt = new pPort(0xe800);
         
         lpt.setControl((short)0x20);
         lpt.setControl((short)0x20);
         lpt.setControl((short)0x20);
         lpt.setControl((short)0x20);
         lpt.setControl((short)0x20);
         lpt.setControl((short)0x20);
         
         lpt.setControl((short)0x21);
         
         short resp = lpt.getData();
                  
         lpt.setControl((short)0x20);

    }


    
}
