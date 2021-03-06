/*--
-- Copyright (C) [2008] [Schuster Andreas]
-- 
-- This program is free software; you can redistribute it and/or modify it 
-- under the terms of the GNU General Public License as published by the Free 
-- Software Foundation; either version 3 of the License, or (at your option) 
-- any later version.
-- 
-- This program is distributed in the hope that it will be useful, but WITHOUT 
-- ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS 
-- FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
-- 
-- You should have received a copy of the GNU General Public License along with 
-- this program; if not, see <http://www.gnu.org/licenses/>.
-- */
/*
 * gui.java
 *
 * Created on September 7, 2008, 9:26 PM
 */

package OsciDummy;

import SushUtil.logger;
import java.awt.Rectangle;
import java.net.DatagramSocket;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author  sushi
 */
public class gui extends javax.swing.JFrame 
{

    private static String newline = System.getProperty("line.separator");
    static DatagramSocket datagrammSocket;
    
    /** Creates new form gui */
    public gui() 
    {
        initComponents();
        
        if(System.getProperty("os.name").contains("Windows"))
        {
            try 
            {
              logger.logDebug("Running on Windows Platform");
              //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
              UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
              //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
              SwingUtilities.updateComponentTreeUI(this);
              this.pack();
            } 
            catch(Exception e) 
            {
                logger.logError("Error setting native LAF: " + e);
            }
        }
        Rectangle myRectangle = this.getBounds();
        myRectangle.width = 550;
        myRectangle.height = 250;
        this.setBounds(myRectangle);
        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaMsgOutput = new javax.swing.JTextArea();
        jSlider1_ZigZag = new javax.swing.JSlider();
        jSlider2_sin = new javax.swing.JSlider();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Osci Dummy");

        jTextAreaMsgOutput.setColumns(20);
        jTextAreaMsgOutput.setFont(new java.awt.Font("Arial", 0, 10));
        jTextAreaMsgOutput.setRows(5);
        jScrollPane1.setViewportView(jTextAreaMsgOutput);

        jSlider1_ZigZag.setOrientation(javax.swing.JSlider.VERTICAL);

        jSlider2_sin.setOrientation(javax.swing.JSlider.VERTICAL);

        jLabel1.setText("sin");

        jLabel2.setText("zigZag");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jSlider2_sin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(jSlider1_ZigZag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(18, 18, 18)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 588, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel1)
                            .add(jLabel2))
                        .add(5, 5, 5)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jSlider1_ZigZag, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 181, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jSlider2_sin, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 181, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
    * @param args the command line arguments
    */
    public static void mainOld(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new gui().setVisible(true);
            }
        });
    }
    
    public void writeMessage(String msg)
    {
        jTextAreaMsgOutput.append(msg + newline);
        jTextAreaMsgOutput.setCaretPosition( jTextAreaMsgOutput.getText().length() );        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JSlider jSlider1_ZigZag;
    public javax.swing.JSlider jSlider2_sin;
    public javax.swing.JTextArea jTextAreaMsgOutput;
    // End of variables declaration//GEN-END:variables

}
