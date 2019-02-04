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
 * ControllerGui.java
 *
 * Created on September 7, 2008, 9:26 PM
 */

package SkolController;


import SkolUtil.opCodes;
import SushUtil.*;
import gnu.io.*;
import java.awt.Color; 

import java.util.HashSet;
import java.util.Iterator;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/**
 *
 * @author  sushi
 */
public class ControllerGui extends javax.swing.JFrame 
{

//    private static String newline = System.getProperty("line.separator");
    
    public XYDataset xyDataset;
    private ChartPanel jfreeChartPanel;
    private Boolean isThisFirstTextAreaMsgOutputComponentResizedNotification = true;
    private XYSeries xy_series_ch0;
    private XYSeries xy_series_ch1;
//    private XYSeries xy_series_grid;
    
    /** Creates new form ControllerGui */
    public ControllerGui() 
    {
        initComponents();
        //Rectangle myRectangle = this.getBounds();
        //myRectangle.width = 780;
        //myRectangle.height = 552;
        //this.setBounds(myRectangle);
        



      xy_series_ch0 = new XYSeries("ch0");
      xy_series_ch0.clear();
      xy_series_ch0.add(-5,4);
      xy_series_ch0.add(5, -4);

      xy_series_ch1 = new XYSeries("ch1");
      xy_series_ch1.clear();

      xy_series_ch1.add(-5, 0);
      
//      xy_series_grid = new XYSeries("grid");
//      xy_series_grid.clear();
//      xy_series_grid.add(-5,0);
//      xy_series_grid.add(0, 0);
//      xy_series_grid.add(0, 4);
//      xy_series_grid.add(0, -4);
//      xy_series_grid.add(0, 0);
//      xy_series_grid.add(5, 0);// cross done

      XYSeriesCollection collection = new XYSeriesCollection();
//      collection.addSeries(xy_series_grid);
      collection.addSeries(xy_series_ch0);
      collection.addSeries(xy_series_ch1);


//      xyDataset = new XYSeriesCollection(xy_series_ch0);

        
      
      JFreeChart chart = ChartFactory.createXYLineChart
              ("Skål", "", "",
      collection, PlotOrientation.VERTICAL, false, false, false);
      
      XYPlot plot = (XYPlot) chart.getPlot(); 
      XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer(); 
      renderer.setSeriesFillPaint(0, Color.BLACK);

      

      jfreeChartPanel = new ChartPanel(chart); 
      jfreeChartPanel.setSize(780,608);//790  618
      jfreeChartPanel.setDoubleBuffered(true);
      jPanel1_DataView.add(jfreeChartPanel);

//      XYPlot xyPlot = chart.getXYPlot();
//        xyPlot.setDomainGridlinesVisible(true);
//        xyPlot.setRangeGridlinesVisible(true);
//        xyPlot.
//        xyPlot.setDomainAxes(new ValueAxis[10]{new ValueAxis()})
//        xyPlot.setRangeGridlinePaint(Color.black);
//        xyPlot.setDomainGridlinePaint(Color.RED);

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
      

    }

    public int getOffsetXmaxRange() 
    {
        int aMax = jSlider_offsetXCh0.getMaximum();
        int bMax = jSlider_offsetXCh1.getMaximum();
        int aMin = jSlider_offsetXCh0.getMinimum();
        int bMin = jSlider_offsetXCh1.getMinimum();
        if(aMax != bMax || bMin != bMin)
        {
            logger.logError("getOffsetXmaxRange()  if(aMax != bMax || bMin != bMin)");
            return 0;
        }
        return aMax - aMin;
    }
    
    
    void updateDataCh0() 
    {
        double offsetY ;
        synchronized(Main.OffsetXLockCh0)
        {
            offsetY = Main.OffsetYCh0*Main.positionYOffsetMultiplier;
        }
        synchronized(Main.yValuesCh0)
        {     
            xy_series_ch0.setNotify(false);

            xy_series_ch0.clear();
            xy_series_ch0.add(-5, -4 ); // to get the right scale
            xy_series_ch0.add(-5, 4 ); // to get the right scale
            xy_series_ch0.add(+5, -4 ); // to get the right scale
            xy_series_ch0.add(+5, 4 ); // to get the right scale
            for(int i= 0; i < Main.xCoordinates.length; i++)
            {
                double val = (Main.yValuesCh0[i] - 4.0) +  offsetY;
                if(val < -4.0)
                    val = -4.0;
                else if(val > 4.0)
                    val = 4.0;
                xy_series_ch0.add(Main.xCoordinates[i], val );
            }
            xy_series_ch0.setNotify(true);
            xy_series_ch0.fireSeriesChanged();
        }
    }
    void updateDataCh1() 
    {
        double offsetY ;
        synchronized(Main.OffsetXLockCh1)
        {
            offsetY = Main.OffsetYCh1*Main.positionYOffsetMultiplier;
        }
        synchronized(Main.yValuesCh1)
        {     
            xy_series_ch1.setNotify(false);

            xy_series_ch1.clear();
            xy_series_ch1.add(-5, -4 ); // to get the right scale
            xy_series_ch1.add(-5, 4 ); // to get the right scale
            for(int i= 0; i < Main.xCoordinates.length; i++)
            {
                double val = (Main.yValuesCh1[i] - 4.0)+  offsetY;
                if(val < -4.0)
                    val = -4.0;
                else if(val > 4.0)
                    val = 4.0;
                xy_series_ch1.add(Main.xCoordinates[i], val );
            }
            xy_series_ch1.setNotify(true);
            xy_series_ch1.fireSeriesChanged();
        }
    }
    


    

   
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        buttonGroup6 = new javax.swing.ButtonGroup();
        buttonGroup7 = new javax.swing.ButtonGroup();
        buttonGroup8 = new javax.swing.ButtonGroup();
        jTabbedPane_developer = new javax.swing.JTabbedPane();
        jPanel2_CommunicationView = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel_Rs232 = new javax.swing.JPanel();
        jTextField_serialPortName = new javax.swing.JTextField();
        jButton_detectSerialPorts = new javax.swing.JButton();
        jButton_serialConnect = new javax.swing.JButton();
        jPanel_UDP = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jTextField_devicePort = new javax.swing.JTextField();
        jTextField_deviceHostName = new javax.swing.JTextField();
        jToggleButton_connectUdp = new javax.swing.JToggleButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField_portLocal = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jPanel9_FileReader = new javax.swing.JPanel();
        jTextFieldFileNameToRead = new javax.swing.JTextField();
        jButton_FileReaderStart = new javax.swing.JButton();
        jButton_FileReaderStop = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel13 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jTextFieldFileName = new javax.swing.JTextField();
        jButton_startFileWriter = new javax.swing.JButton();
        jButton1StopFileWriter = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jCheckBox_checkUndersampling = new javax.swing.JCheckBox();
        jPanel3_dataView = new javax.swing.JPanel();
        jPanel1_DataView = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButton1_resetDevice = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jButton_startAquisition = new javax.swing.JButton();
        jButton_aquistion_stop = new javax.swing.JButton();
        jButtonSingleFrameCh0WithTrigger = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jSlider_TriggerLevel = new javax.swing.JSlider();
        jPanel7 = new javax.swing.JPanel();
        jButtonTriggerEdgePos = new javax.swing.JButton();
        jButtonTriggerEdgeNeg = new javax.swing.JButton();
        jButtonTriggerOff = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jComboBox_TimeTividerCh0 = new javax.swing.JComboBox();
        jComboBox_TimeTividerCh1 = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jComboBox_Resolution = new javax.swing.JComboBox();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jComboBoxFileReaderData = new javax.swing.JComboBox();
        jButton1_singleVoltCh0 = new javax.swing.JButton();
        jCheckBox_channel0DisplayOrNot = new javax.swing.JCheckBox();
        jCheckBox_channel1DisplayOrNot = new javax.swing.JCheckBox();
        jPanel_offsetPanel = new javax.swing.JPanel();
        jSlider_offsetYCh1 = new javax.swing.JSlider();
        jSlider_offsetXCh1 = new javax.swing.JSlider();
        jLabel_offsetX = new javax.swing.JLabel();
        jLabel_offsetY = new javax.swing.JLabel();
        jSlider_offsetYCh0 = new javax.swing.JSlider();
        jSlider_offsetXCh0 = new javax.swing.JSlider();
        jPanel11 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jButton_serialSendhexValues_temp = new javax.swing.JButton();
        jTextField_serialDataToSendDebug = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jCheckBox_showSerialDataStream = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jTextField_adcBitCount = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jTextField_adc_lowestVoltage = new javax.swing.JTextField();
        jTextField_adc_highestVoltage = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jTextField_AdcCyclesForSingleDataAquisition = new javax.swing.JTextField();
        jTextField_adcSamplesPerFrame = new javax.swing.JTextField();
        jTextField_systemCLockMhz = new javax.swing.JTextField();
        jButton_generateNewTimePerDivSettingsInLoggingOutput = new javax.swing.JButton();
        jPanel_MySql = new javax.swing.JPanel();
        jButtonStartSqConnection = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField2_dbHost = new javax.swing.JTextField();
        jTextField3_dbPort = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField4_dbUser = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField5_dbPwd = new javax.swing.JTextField();
        jTextField6_db_db = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextPaneMessageOutput = new javax.swing.JTextPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea_debugOutDeveloper = new javax.swing.JTextArea();
        jCheckBox_TimingAnalysis = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaMsgOutput = new javax.swing.JTextArea();

        jFrame1.getContentPane().setLayout(null);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        jTabbedPane_developer.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jTabbedPane_developerComponentResized(evt);
            }
        });

        jPanel2_CommunicationView.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTabbedPane2.setName("UDP"); // NOI18N

        jTextField_serialPortName.setText("COM5");

        jButton_detectSerialPorts.setText("detectSerialPorts");
        jButton_detectSerialPorts.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_detectSerialPortsMouseClicked(evt);
            }
        });

        jButton_serialConnect.setText("Connect");
        jButton_serialConnect.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_serialConnectMouseClicked(evt);
            }
        });
        jButton_serialConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_serialConnectActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel_Rs232Layout = new org.jdesktop.layout.GroupLayout(jPanel_Rs232);
        jPanel_Rs232.setLayout(jPanel_Rs232Layout);
        jPanel_Rs232Layout.setHorizontalGroup(
            jPanel_Rs232Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel_Rs232Layout.createSequentialGroup()
                .add(66, 66, 66)
                .add(jPanel_Rs232Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton_detectSerialPorts, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jButton_serialConnect, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jTextField_serialPortName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
                .add(177, 177, 177))
        );
        jPanel_Rs232Layout.setVerticalGroup(
            jPanel_Rs232Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel_Rs232Layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(jButton_detectSerialPorts)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jTextField_serialPortName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton_serialConnect)
                .addContainerGap(147, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("RS232", jPanel_Rs232);

        jLabel2.setText("Local Port");

        jTextField_devicePort.setText("2000");

        jTextField_deviceHostName.setText("localhost");

        jToggleButton_connectUdp.setText("Connect");
        jToggleButton_connectUdp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jToggleButton_connectUdpMouseClicked(evt);
            }
        });
        jToggleButton_connectUdp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton_connectUdpActionPerformed(evt);
            }
        });

        jLabel4.setText("RemoteHost");

        jLabel3.setText(":");

        jTextField_portLocal.setText("2001");

        jTextArea2.setBackground(new java.awt.Color(212, 208, 200));
        jTextArea2.setColumns(20);
        jTextArea2.setEditable(false);
        jTextArea2.setRows(5);
        jTextArea2.setText("If Using OsciSimulator:\nOnly Aquisition Box Commands are\nworking so far.");
        jScrollPane3.setViewportView(jTextArea2);

        org.jdesktop.layout.GroupLayout jPanel_UDPLayout = new org.jdesktop.layout.GroupLayout(jPanel_UDP);
        jPanel_UDP.setLayout(jPanel_UDPLayout);
        jPanel_UDPLayout.setHorizontalGroup(
            jPanel_UDPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel_UDPLayout.createSequentialGroup()
                .add(73, 73, 73)
                .add(jPanel_UDPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jToggleButton_connectUdp, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel_UDPLayout.createSequentialGroup()
                        .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField_portLocal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel_UDPLayout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jTextField_deviceHostName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(1, 1, 1)
                        .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(1, 1, 1)
                        .add(jTextField_devicePort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
            .add(jPanel_UDPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel_UDPLayout.createSequentialGroup()
                    .add(16, 16, 16)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 307, Short.MAX_VALUE)
                    .add(16, 16, 16)))
        );
        jPanel_UDPLayout.setVerticalGroup(
            jPanel_UDPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel_UDPLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel_UDPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(jTextField_portLocal, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel_UDPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(jTextField_deviceHostName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3)
                    .add(jTextField_devicePort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jToggleButton_connectUdp)
                .addContainerGap(148, Short.MAX_VALUE))
            .add(jPanel_UDPLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                .add(jPanel_UDPLayout.createSequentialGroup()
                    .add(120, 120, 120)
                    .add(jScrollPane3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(60, Short.MAX_VALUE)))
        );

        jTabbedPane2.addTab("UDP", jPanel_UDP);

        jPanel9_FileReader.setBorder(javax.swing.BorderFactory.createTitledBorder("FileReader"));

        jTextFieldFileNameToRead.setText("c:\\1.bytes");
        jTextFieldFileNameToRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldFileNameToReadActionPerformed(evt);
            }
        });

        jButton_FileReaderStart.setText("start");
        jButton_FileReaderStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_FileReaderStartActionPerformed(evt);
            }
        });

        jButton_FileReaderStop.setText("stop");
        jButton_FileReaderStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_FileReaderStopActionPerformed(evt);
            }
        });

        jTextArea1.setBackground(new java.awt.Color(212, 208, 200));
        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setRows(5);
        jTextArea1.setText("Use UP and DOWN keys while selected\nDropDownBox to switch fast\nthrough DataFrames.");
        jScrollPane2.setViewportView(jTextArea1);

        org.jdesktop.layout.GroupLayout jPanel9_FileReaderLayout = new org.jdesktop.layout.GroupLayout(jPanel9_FileReader);
        jPanel9_FileReader.setLayout(jPanel9_FileReaderLayout);
        jPanel9_FileReaderLayout.setHorizontalGroup(
            jPanel9_FileReaderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel9_FileReaderLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel9_FileReaderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jTextFieldFileNameToRead, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel9_FileReaderLayout.createSequentialGroup()
                        .add(jButton_FileReaderStart)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton_FileReaderStop)))
                .addContainerGap())
        );
        jPanel9_FileReaderLayout.setVerticalGroup(
            jPanel9_FileReaderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel9_FileReaderLayout.createSequentialGroup()
                .add(jTextFieldFileNameToRead, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel9_FileReaderLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton_FileReaderStart)
                    .add(jButton_FileReaderStop))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 107, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(54, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("FileReader", jPanel9_FileReader);

        jPanel2_CommunicationView.add(jTabbedPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, 360, 310));

        jLabel13.setText("http://skol.sourceforge.net/");
        jPanel2_CommunicationView.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 330, 300, -1));

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("FileWriter"));

        jTextFieldFileName.setText("c:\\1.bytes");
        jTextFieldFileName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldFileNameActionPerformed(evt);
            }
        });

        jButton_startFileWriter.setText("startWriter");
        jButton_startFileWriter.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_startFileWriterMouseClicked(evt);
            }
        });

        jButton1StopFileWriter.setText("stopWriter");

        jLabel14.setText("Can be used to dump all received data into a file.");

        jLabel22.setText("After that use : FileReader to read from File again");

        org.jdesktop.layout.GroupLayout jPanel8Layout = new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jTextFieldFileName, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                    .add(jPanel8Layout.createSequentialGroup()
                        .add(jButton_startFileWriter)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 89, Short.MAX_VALUE)
                        .add(jButton1StopFileWriter))
                    .add(jLabel14)
                    .add(jLabel22)
                    .add(jLabel23))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .add(jTextFieldFileName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton_startFileWriter)
                    .add(jButton1StopFileWriter))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel14)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel22)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel23)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jPanel2_CommunicationView.add(jPanel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 410, 350, 220));

        jLabel26.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SkolController/icons.png"))); // NOI18N
        jPanel2_CommunicationView.add(jLabel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 40, 340, 300));

        jCheckBox_checkUndersampling.setText("Check for Undersampling");
        jCheckBox_checkUndersampling.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_checkUndersamplingActionPerformed(evt);
            }
        });
        jPanel2_CommunicationView.add(jCheckBox_checkUndersampling, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 530, 240, -1));

        jTabbedPane_developer.addTab("Communication", jPanel2_CommunicationView);

        jPanel1_DataView.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel1_DataView.setLayout(null);

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.gray));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton1_resetDevice.setFont(new java.awt.Font("Arial", 0, 11));
        jButton1_resetDevice.setText("Reset Device");
        jButton1_resetDevice.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1_resetDeviceMouseClicked(evt);
            }
        });
        jButton1_resetDevice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1_resetDeviceActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1_resetDevice, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 550, 140, -1));

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Aquisition"));

        jButton_startAquisition.setFont(new java.awt.Font("Arial", 0, 11));
        jButton_startAquisition.setText("start");
        jButton_startAquisition.setMaximumSize(new java.awt.Dimension(70, 23));
        jButton_startAquisition.setMinimumSize(new java.awt.Dimension(70, 23));
        jButton_startAquisition.setPreferredSize(new java.awt.Dimension(70, 23));
        jButton_startAquisition.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_startAquisitionActionPerformed(evt);
            }
        });
        jButton_startAquisition.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_startAquisitionMouseClicked(evt);
            }
        });

        jButton_aquistion_stop.setFont(new java.awt.Font("Arial", 0, 11));
        jButton_aquistion_stop.setText("stop");
        jButton_aquistion_stop.setDoubleBuffered(true);
        jButton_aquistion_stop.setMaximumSize(new java.awt.Dimension(70, 23));
        jButton_aquistion_stop.setMinimumSize(new java.awt.Dimension(70, 23));
        jButton_aquistion_stop.setPreferredSize(new java.awt.Dimension(70, 23));
        jButton_aquistion_stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_aquistion_stopActionPerformed(evt);
            }
        });
        jButton_aquistion_stop.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_aquistion_stopMouseClicked(evt);
            }
        });

        jButtonSingleFrameCh0WithTrigger.setFont(new java.awt.Font("Arial", 0, 11));
        jButtonSingleFrameCh0WithTrigger.setText("single Shot");
        jButtonSingleFrameCh0WithTrigger.setMaximumSize(new java.awt.Dimension(70, 23));
        jButtonSingleFrameCh0WithTrigger.setMinimumSize(new java.awt.Dimension(70, 23));
        jButtonSingleFrameCh0WithTrigger.setPreferredSize(new java.awt.Dimension(70, 23));
        jButtonSingleFrameCh0WithTrigger.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSingleFrameCh0WithTriggerActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel5Layout = new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jButtonSingleFrameCh0WithTrigger, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, jPanel5Layout.createSequentialGroup()
                        .add(jButton_startAquisition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jButton_aquistion_stop, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(2, 2, 2))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel5Layout.createSequentialGroup()
                .add(jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jButton_startAquisition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton_aquistion_stop, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 6, Short.MAX_VALUE)
                .add(jButtonSingleFrameCh0WithTrigger, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.add(jPanel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 160, 80));

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Trigger (Only CH0)"));
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jPanel6.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        jLabel15.setText("Level");
        jPanel6.add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        jSlider_TriggerLevel.setOrientation(javax.swing.JSlider.VERTICAL);
        jSlider_TriggerLevel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider_TriggerLevelMouseReleased(evt);
            }
        });
        jSlider_TriggerLevel.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider_TriggerLevelStateChanged(evt);
            }
        });
        jSlider_TriggerLevel.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                jSlider_TriggerLevelPropertyChange(evt);
            }
        });
        jPanel6.add(jSlider_TriggerLevel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 30, 30, 100));

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("Mode"));

        jButtonTriggerEdgePos.setFont(new java.awt.Font("Arial", 0, 11));
        jButtonTriggerEdgePos.setText("+");
        jButtonTriggerEdgePos.setDoubleBuffered(true);
        jButtonTriggerEdgePos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonTriggerEdgePosMouseClicked(evt);
            }
        });
        jButtonTriggerEdgePos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTriggerEdgePosActionPerformed(evt);
            }
        });

        jButtonTriggerEdgeNeg.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButtonTriggerEdgeNeg.setText("-");
        jButtonTriggerEdgeNeg.setAlignmentX(4.0F);
        jButtonTriggerEdgeNeg.setAlignmentY(4.0F);
        jButtonTriggerEdgeNeg.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonTriggerEdgeNegMouseClicked(evt);
            }
        });
        jButtonTriggerEdgeNeg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTriggerEdgeNegActionPerformed(evt);
            }
        });

        jButtonTriggerOff.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jButtonTriggerOff.setText("OFF");
        jButtonTriggerOff.setAlignmentX(4.0F);
        jButtonTriggerOff.setAlignmentY(4.0F);
        jButtonTriggerOff.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonTriggerOffMouseClicked(evt);
            }
        });
        jButtonTriggerOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTriggerOffActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel7Layout = new org.jdesktop.layout.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .add(jButtonTriggerEdgePos, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jButtonTriggerOff, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(9, Short.MAX_VALUE)
                .add(jButtonTriggerEdgeNeg, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel7Layout.createSequentialGroup()
                .add(jButtonTriggerEdgePos)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButtonTriggerOff)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButtonTriggerEdgeNeg)
                .addContainerGap())
        );

        jPanel6.add(jPanel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, 90, 110));
        jPanel6.add(jLabel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 160, 20, -1));

        jPanel4.add(jPanel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 160, 140));

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Time per Div"));

        jComboBox_TimeTividerCh0.setMinimumSize(new java.awt.Dimension(80, 27));
        jComboBox_TimeTividerCh0.setPreferredSize(new java.awt.Dimension(80, 22));
        jComboBox_TimeTividerCh0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_TimeTividerCh0ActionPerformed(evt);
            }
        });

        jComboBox_TimeTividerCh1.setMinimumSize(new java.awt.Dimension(80, 27));
        jComboBox_TimeTividerCh1.setPreferredSize(new java.awt.Dimension(80, 22));
        jComboBox_TimeTividerCh1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_TimeTividerCh1ActionPerformed(evt);
            }
        });

        jLabel5.setText("CH1");

        jLabel24.setText("CH0");

        jComboBox_Resolution.setToolTipText("This increases the amount of sample Values per Frame, but the TimePerDiv Values have to be divided by this value.");
        jComboBox_Resolution.setMinimumSize(new java.awt.Dimension(80, 27));
        jComboBox_Resolution.setPreferredSize(new java.awt.Dimension(80, 22));
        jComboBox_Resolution.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox_ResolutionActionPerformed(evt);
            }
        });

        jLabel27.setText("Resolution");
        jLabel27.setToolTipText("This increases the amount of sample Values per Frame, but the TimePerDiv Values have to be divided by this value.");

        jLabel28.setText("Raster = Division/2  !");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(4, 4, 4)
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(jComboBox_TimeTividerCh0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel24))
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(jComboBox_TimeTividerCh1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel5))
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(jComboBox_Resolution, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 69, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jLabel27, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                    .add(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel28)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(8, 8, 8)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBox_TimeTividerCh0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel24))
                .add(3, 3, 3)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBox_TimeTividerCh1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel5))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jComboBox_Resolution, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel27))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel28)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 160, 140));

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("FileReader"));

        jComboBoxFileReaderData.setEnabled(false);
        jComboBoxFileReaderData.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxFileReaderDataActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel10Layout = new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jComboBoxFileReaderData, 0, 148, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel10Layout.createSequentialGroup()
                .add(jComboBoxFileReaderData, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 470, 160, 50));

        jButton1_singleVoltCh0.setFont(new java.awt.Font("Arial", 0, 11));
        jButton1_singleVoltCh0.setText("Volt  Ch0 & Ch1");
        jButton1_singleVoltCh0.setMaximumSize(new java.awt.Dimension(70, 23));
        jButton1_singleVoltCh0.setMinimumSize(new java.awt.Dimension(70, 23));
        jButton1_singleVoltCh0.setPreferredSize(new java.awt.Dimension(70, 23));
        jButton1_singleVoltCh0.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton1_singleVoltCh0MouseClicked(evt);
            }
        });
        jButton1_singleVoltCh0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1_singleVoltCh0ActionPerformed(evt);
            }
        });
        jPanel4.add(jButton1_singleVoltCh0, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 520, 140, -1));

        jCheckBox_channel0DisplayOrNot.setForeground(new java.awt.Color(255, 0, 0));
        jCheckBox_channel0DisplayOrNot.setSelected(true);
        jCheckBox_channel0DisplayOrNot.setText("Ch0");
        jPanel4.add(jCheckBox_channel0DisplayOrNot, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 80, -1));

        jCheckBox_channel1DisplayOrNot.setForeground(new java.awt.Color(0, 0, 255));
        jCheckBox_channel1DisplayOrNot.setText("Ch1");
        jPanel4.add(jCheckBox_channel1DisplayOrNot, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 10, 80, -1));

        jPanel_offsetPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Offset"));
        jPanel_offsetPanel.setToolTipText("Click on Offset Label to Reset Sliders");
        jPanel_offsetPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jPanel_offsetPanelMouseClicked(evt);
            }
        });
        jPanel_offsetPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jSlider_offsetYCh1.setMaximum(400);
        jSlider_offsetYCh1.setMinimum(-400);
        jSlider_offsetYCh1.setToolTipText("Click the Axis Label on the right to center.");
        jSlider_offsetYCh1.setValue(0);
        jSlider_offsetYCh1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider_offsetYCh1MouseReleased(evt);
            }
        });
        jSlider_offsetYCh1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider_offsetYCh1StateChanged(evt);
            }
        });
        jPanel_offsetPanel.add(jSlider_offsetYCh1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 50, 70, -1));

        jSlider_offsetXCh1.setMaximum(500);
        jSlider_offsetXCh1.setMinimum(-500);
        jSlider_offsetXCh1.setSnapToTicks(true);
        jSlider_offsetXCh1.setToolTipText("Click the Axis Label on the right to center.");
        jSlider_offsetXCh1.setValue(0);
        jSlider_offsetXCh1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider_offsetXCh1MouseReleased(evt);
            }
        });
        jSlider_offsetXCh1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider_offsetXCh1StateChanged(evt);
            }
        });
        jPanel_offsetPanel.add(jSlider_offsetXCh1, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 20, 70, -1));

        jLabel_offsetX.setText("X");
        jLabel_offsetX.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_offsetXMouseClicked(evt);
            }
        });
        jPanel_offsetPanel.add(jLabel_offsetX, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 20, -1, -1));

        jLabel_offsetY.setText("Y");
        jLabel_offsetY.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel_offsetYMouseClicked(evt);
            }
        });
        jPanel_offsetPanel.add(jLabel_offsetY, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 50, -1, -1));

        jSlider_offsetYCh0.setMaximum(400);
        jSlider_offsetYCh0.setMinimum(-400);
        jSlider_offsetYCh0.setToolTipText("Click the Axis Label on the right to center.");
        jSlider_offsetYCh0.setValue(0);
        jSlider_offsetYCh0.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider_offsetYCh0MouseReleased(evt);
            }
        });
        jSlider_offsetYCh0.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider_offsetYCh0StateChanged(evt);
            }
        });
        jPanel_offsetPanel.add(jSlider_offsetYCh0, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, 70, -1));

        jSlider_offsetXCh0.setMaximum(500);
        jSlider_offsetXCh0.setMinimum(-500);
        jSlider_offsetXCh0.setSnapToTicks(true);
        jSlider_offsetXCh0.setToolTipText("Click the Axis Label on the right to center.");
        jSlider_offsetXCh0.setValue(0);
        jSlider_offsetXCh0.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jSlider_offsetXCh0MouseReleased(evt);
            }
        });
        jSlider_offsetXCh0.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider_offsetXCh0StateChanged(evt);
            }
        });
        jPanel_offsetPanel.add(jSlider_offsetXCh0, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, 70, -1));

        jPanel4.add(jPanel_offsetPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 390, 160, 80));

        org.jdesktop.layout.GroupLayout jPanel3_dataViewLayout = new org.jdesktop.layout.GroupLayout(jPanel3_dataView);
        jPanel3_dataView.setLayout(jPanel3_dataViewLayout);
        jPanel3_dataViewLayout.setHorizontalGroup(
            jPanel3_dataViewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3_dataViewLayout.createSequentialGroup()
                .add(jPanel1_DataView, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 790, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 181, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel3_dataViewLayout.setVerticalGroup(
            jPanel3_dataViewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3_dataViewLayout.createSequentialGroup()
                .add(jPanel3_dataViewLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jPanel1_DataView, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE)
                    .add(jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 649, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane_developer.addTab("Oscilloscope View", jPanel3_dataView);

        jPanel11.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jButton_serialSendhexValues_temp.setText("Send Hex");
        jButton_serialSendhexValues_temp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton_serialSendhexValues_tempMouseClicked(evt);
            }
        });

        jTextField_serialDataToSendDebug.setText("B0EF0001010000");
        jTextField_serialDataToSendDebug.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_serialDataToSendDebugActionPerformed(evt);
            }
        });

        jLabel1.setText("Serial Debug Functions");

        jCheckBox_showSerialDataStream.setText("show raw Traffic");
        jCheckBox_showSerialDataStream.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_showSerialDataStreamActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(42, 42, 42)
                        .add(jCheckBox_showSerialDataStream))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(30, 30, 30)
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jTextField_serialDataToSendDebug, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 129, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel1))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jButton_serialSendhexValues_temp, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 146, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(225, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(28, 28, 28)
                .add(jCheckBox_showSerialDataStream)
                .add(18, 18, 18)
                .add(jLabel1)
                .add(18, 18, 18)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField_serialDataToSendDebug, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jButton_serialSendhexValues_temp))
                .addContainerGap(150, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("RS232 Debug", jPanel1);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("HARDWARE Settings"));

        jLabel16.setText("ADC bits");

        jTextField_adcBitCount.setEditable(false);
        jTextField_adcBitCount.setText("14");

        jLabel17.setText("ADC lowest Voltage Value  [mv]");

        jLabel18.setText("ADC highest Voltage Value  [mv]");

        jTextField_adc_lowestVoltage.setEditable(false);
        jTextField_adc_lowestVoltage.setText("400");

        jTextField_adc_highestVoltage.setEditable(false);
        jTextField_adc_highestVoltage.setText("2900");
        jTextField_adc_highestVoltage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_adc_highestVoltageActionPerformed(evt);
            }
        });

        jLabel19.setText("System Clock [MHz]");

        jLabel20.setText("ADC Cycles for single Aquisition");

        jLabel21.setText("ADC Samples per Frame");

        jTextField_AdcCyclesForSingleDataAquisition.setEditable(false);
        jTextField_AdcCyclesForSingleDataAquisition.setText("33");
        jTextField_AdcCyclesForSingleDataAquisition.setToolTipText("Cycle count between single points in a Data Frame");

        jTextField_adcSamplesPerFrame.setEditable(false);
        jTextField_adcSamplesPerFrame.setText("152");
        jTextField_adcSamplesPerFrame.setToolTipText("For each Oscilloscope Picture this amount of sample points is taken.");
        jTextField_adcSamplesPerFrame.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_adcSamplesPerFrameActionPerformed(evt);
            }
        });

        jTextField_systemCLockMhz.setEditable(false);
        jTextField_systemCLockMhz.setText("50");

        jButton_generateNewTimePerDivSettingsInLoggingOutput.setText("new TimePerDiv to Logging Output only");
        jButton_generateNewTimePerDivSettingsInLoggingOutput.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_generateNewTimePerDivSettingsInLoggingOutputActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(15, 15, 15)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel16))
                    .add(jLabel17)
                    .add(jLabel20)
                    .add(jLabel21)
                    .add(jLabel18)
                    .add(jLabel19))
                .add(24, 24, 24)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextField_systemCLockMhz, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 56, Short.MAX_VALUE)
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                        .add(jTextField_adcBitCount)
                        .add(jTextField_adcSamplesPerFrame)
                        .add(jTextField_AdcCyclesForSingleDataAquisition)
                        .add(jTextField_adc_lowestVoltage)
                        .add(jTextField_adc_highestVoltage, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)))
                .addContainerGap(246, Short.MAX_VALUE))
            .add(jPanel2Layout.createSequentialGroup()
                .add(jButton_generateNewTimePerDivSettingsInLoggingOutput, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 313, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(234, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(17, 17, 17)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel16)
                    .add(jTextField_adcBitCount, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel17)
                    .add(jTextField_adc_lowestVoltage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jTextField_adc_highestVoltage, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel18))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel20)
                    .add(jTextField_AdcCyclesForSingleDataAquisition, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel21)
                    .add(jTextField_adcSamplesPerFrame, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel19)
                    .add(jTextField_systemCLockMhz, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton_generateNewTimePerDivSettingsInLoggingOutput)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("HW Settings", jPanel2);

        jPanel_MySql.setBorder(javax.swing.BorderFactory.createTitledBorder("MySQL"));

        jButtonStartSqConnection.setText("Connect");
        jButtonStartSqConnection.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButtonStartSqConnectionMouseClicked(evt);
            }
        });

        jLabel8.setText("Host");

        jTextField2_dbHost.setText("localhost");

        jTextField3_dbPort.setText("3306");

        jLabel10.setText("User");

        jLabel11.setText("pwd");

        jTextField6_db_db.setText("oscilloscope");

        jLabel12.setText("db");

        jLabel6.setText(":");

        org.jdesktop.layout.GroupLayout jPanel_MySqlLayout = new org.jdesktop.layout.GroupLayout(jPanel_MySql);
        jPanel_MySql.setLayout(jPanel_MySqlLayout);
        jPanel_MySqlLayout.setHorizontalGroup(
            jPanel_MySqlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel_MySqlLayout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel_MySqlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel_MySqlLayout.createSequentialGroup()
                        .add(jLabel7)
                        .add(506, 506, 506))
                    .add(jPanel_MySqlLayout.createSequentialGroup()
                        .add(jPanel_MySqlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel8)
                            .add(jLabel12))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jPanel_MySqlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jTextField6_db_db, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 94, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jPanel_MySqlLayout.createSequentialGroup()
                                .add(jTextField2_dbHost, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel6)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jTextField3_dbPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(345, Short.MAX_VALUE))
                    .add(jPanel_MySqlLayout.createSequentialGroup()
                        .add(jLabel10)
                        .add(18, 18, 18)
                        .add(jTextField4_dbUser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .add(jPanel_MySqlLayout.createSequentialGroup()
                        .add(jLabel11)
                        .add(18, 18, 18)
                        .add(jTextField5_dbPwd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 94, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(389, Short.MAX_VALUE))
                    .add(jPanel_MySqlLayout.createSequentialGroup()
                        .add(jButtonStartSqConnection)
                        .addContainerGap(431, Short.MAX_VALUE))))
        );
        jPanel_MySqlLayout.setVerticalGroup(
            jPanel_MySqlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel_MySqlLayout.createSequentialGroup()
                .add(jPanel_MySqlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel_MySqlLayout.createSequentialGroup()
                        .add(jPanel_MySqlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jTextField2_dbHost, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel6)
                            .add(jTextField3_dbPort, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(jPanel_MySqlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jTextField6_db_db, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel12)))
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel_MySqlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(jTextField4_dbUser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(jPanel_MySqlLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel11)
                    .add(jTextField5_dbPwd, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(10, 10, 10)
                .add(jLabel7)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jButtonStartSqConnection)
                .add(339, 339, 339))
        );

        jTabbedPane1.addTab("MySQl", jPanel_MySql);

        jPanel11.add(jTabbedPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 580, 330));

        jTextPaneMessageOutput.setContentType("text/html");
        jTextPaneMessageOutput.setFont(new java.awt.Font("Courier New", 0, 11));
        jTextPaneMessageOutput.setText("<html>\r   <head>\r \r   </head>\r   <body>\r     <p style=\"margin-top: 0\">\r       \r  </body></html>   ");
        jScrollPane4.setViewportView(jTextPaneMessageOutput);

        jPanel11.add(jScrollPane4, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 30, 230, 60));

        jTextArea_debugOutDeveloper.setColumns(20);
        jTextArea_debugOutDeveloper.setRows(5);
        jScrollPane5.setViewportView(jTextArea_debugOutDeveloper);

        jPanel11.add(jScrollPane5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 330, 990, 330));

        jCheckBox_TimingAnalysis.setText("TimingAnalysis");
        jCheckBox_TimingAnalysis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox_TimingAnalysisActionPerformed(evt);
            }
        });
        jPanel11.add(jCheckBox_TimingAnalysis, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 100, -1, -1));

        jTabbedPane_developer.addTab("Developer only", jPanel11);

        jTextAreaMsgOutput.setColumns(20);
        jTextAreaMsgOutput.setRows(5);
        jTextAreaMsgOutput.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                jTextAreaMsgOutputComponentResized(evt);
            }
        });
        jScrollPane1.setViewportView(jTextAreaMsgOutput);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane_developer, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 1006, Short.MAX_VALUE)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1006, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jTabbedPane_developer)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 57, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents



private void jButton1_resetDeviceMouseClicked(java.awt.event.MouseEvent evt) {                                                  

}                                                                                                  

private void jButton_startAquisitionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_startAquisitionMouseClicked
}//GEN-LAST:event_jButton_startAquisitionMouseClicked

private void jButton_aquistion_stopMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_aquistion_stopMouseClicked
}//GEN-LAST:event_jButton_aquistion_stopMouseClicked

private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
// TODO add your handling code here:


}//GEN-LAST:event_formMouseClicked


private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
// TODO add your handling code here:
  
    
}//GEN-LAST:event_formComponentResized

private void jTextAreaMsgOutputComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTextAreaMsgOutputComponentResized

}//GEN-LAST:event_jTextAreaMsgOutputComponentResized

private void jButton_detectSerialPortsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_detectSerialPortsMouseClicked

    HashSet h = rs232Thread.getAvailableSerialPorts();
    Iterator It = h.iterator();
    int n=0;
    while (It.hasNext()) 
    {
        CommPortIdentifier commPortIdentifier = (CommPortIdentifier) It.next();
        logger.logInfo(commPortIdentifier.getName());
    }
}//GEN-LAST:event_jButton_detectSerialPortsMouseClicked

private void jButton_serialConnectMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_serialConnectMouseClicked
// TODO add your handling code here:
 
    
}//GEN-LAST:event_jButton_serialConnectMouseClicked

private void jTextField_serialDataToSendDebugActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_serialDataToSendDebugActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jTextField_serialDataToSendDebugActionPerformed

private void jButton_serialSendhexValues_tempMouseClicked(java.awt.event.MouseEvent evt) {                                                              
        
    try
    {
        Main.sendDeviceRawDataByte(hexUtil.stringToByte(jTextField_serialDataToSendDebug.getText()));
    }
    catch (Exception ex) 
    {
        logger.logError("jButton_serialSendhexValuesMouseClicked:", ex);
    }
}                                                                                                                                                                             

private void jToggleButton_connectUdpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton_connectUdpActionPerformed
    
    if(Main.connected)
    {
       Main.connected = false;
       jToggleButton_connectUdp.setText("Connect");
       Main.communicationThreadStop();
       jButton_serialConnect.setEnabled(true);
    }
    else
    {
        if(!Main.communicationThreadStart(Main.communicationType.udp))
            return;
        
        Main.connected = true;
        jToggleButton_connectUdp.setText("Disconnect");
        jButton_serialConnect.setEnabled(false);
    }
}//GEN-LAST:event_jToggleButton_connectUdpActionPerformed

private void jToggleButton_connectUdpMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jToggleButton_connectUdpMouseClicked
// TODO add your handling code here:
    
    
   
}//GEN-LAST:event_jToggleButton_connectUdpMouseClicked

private void jButtonStartSqConnectionMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonStartSqConnectionMouseClicked


    Main.communicationStartSqlServer(jTextField2_dbHost.getText(), jTextField3_dbPort.getText(), jTextField6_db_db.getText(), 			jTextField4_dbUser.getText(), jTextField5_dbPwd.getText());
}//GEN-LAST:event_jButtonStartSqConnectionMouseClicked

private void jButton_serialConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_serialConnectActionPerformed
    
    if(Main.connected)
    {
        Main.connected = false;
       jButton_serialConnect.setText("Connect");
       Main.communicationThreadStop();
       jToggleButton_connectUdp.setEnabled(true);
    }
    else
    {
        if(!Main.communicationThreadStart(Main.communicationType.serial))
            return;
        
        Main.connected = true;
        jButton_serialConnect.setText("Disconnect");
        
        jToggleButton_connectUdp.setEnabled(false);
    }

}//GEN-LAST:event_jButton_serialConnectActionPerformed

private void jButtonSingleFrameCh0WithTriggerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSingleFrameCh0WithTriggerActionPerformed
    if(!Main.connected)
    {
        logger.logInfo("Not connected!");
        return;
    }
    Main.getDeviceSingleFrameCh0withTrigger();
    
}//GEN-LAST:event_jButtonSingleFrameCh0WithTriggerActionPerformed

private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
// TODO add your handling code here:
    Main.sendDeviceRawDataByte(new byte[]{116,1,2,3,4,5,6});
}//GEN-LAST:event_jButton2ActionPerformed

private void jButtonTriggerEdgePosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTriggerEdgePosActionPerformed
    if(!Main.connected)
    {
        logger.logInfo("Not connected!");
        return;
    }
    Main.setDeviceTriggerPosEdge();
}//GEN-LAST:event_jButtonTriggerEdgePosActionPerformed

private void jButtonTriggerEdgeNegActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTriggerEdgeNegActionPerformed
    if(!Main.connected)
    {
        logger.logInfo("Not connected!");
        return;
    }
    Main.setDeviceTriggerNegEdge();
}//GEN-LAST:event_jButtonTriggerEdgeNegActionPerformed

private void jSlider_TriggerLevelPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_jSlider_TriggerLevelPropertyChange

   
}//GEN-LAST:event_jSlider_TriggerLevelPropertyChange

private void jSlider_TriggerLevelStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_TriggerLevelStateChanged
 

}//GEN-LAST:event_jSlider_TriggerLevelStateChanged

private void jSlider_TriggerLevelMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_TriggerLevelMouseReleased
    if(!Main.connected)
    {
        logger.logInfo("Not connected!");
        return;
    }
    Main.setDeviceTriggerLevel(jSlider_TriggerLevel.getValue());
}//GEN-LAST:event_jSlider_TriggerLevelMouseReleased

private void jButtonTriggerEdgePosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTriggerEdgePosMouseClicked

}//GEN-LAST:event_jButtonTriggerEdgePosMouseClicked

private void jButtonTriggerEdgeNegMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTriggerEdgeNegMouseClicked

}//GEN-LAST:event_jButtonTriggerEdgeNegMouseClicked

private void jTextField_adc_highestVoltageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_adc_highestVoltageActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jTextField_adc_highestVoltageActionPerformed

private void jTextField_adcSamplesPerFrameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_adcSamplesPerFrameActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jTextField_adcSamplesPerFrameActionPerformed

private void jComboBox_TimeTividerCh0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_TimeTividerCh0ActionPerformed
    
    if(!Main.connected)
    {
        logger.logInfo("Not connected!");
        return;
    }
    JComboBox jcmbType = (JComboBox) evt.getSource();
    String cmbType = (String) jcmbType.getSelectedItem();
    
    Main.setDeviceTimeDivider(cmbType, 0);
}//GEN-LAST:event_jComboBox_TimeTividerCh0ActionPerformed

private void jTextFieldFileNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldFileNameActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jTextFieldFileNameActionPerformed

private void jButton_startFileWriterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton_startFileWriterMouseClicked

    Main.fileWriterStart(jTextFieldFileName.getText());
}//GEN-LAST:event_jButton_startFileWriterMouseClicked

private void jTextFieldFileNameToReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldFileNameToReadActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_jTextFieldFileNameToReadActionPerformed

private void jButton_FileReaderStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_FileReaderStartActionPerformed
 
    if(Main.connected)
    {
       logger.logError("Already some Communication running");
    }
    else
    {
        Main.communicationStartFileReaderThread(jTextFieldFileNameToRead.getText());
    }
}//GEN-LAST:event_jButton_FileReaderStartActionPerformed

private void jButton_FileReaderStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_FileReaderStopActionPerformed

    Main.communicationStopFileReader();
}//GEN-LAST:event_jButton_FileReaderStopActionPerformed

private void jComboBoxFileReaderDataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxFileReaderDataActionPerformed

    if(!Main.connected)
    {
        return;
    }
    JComboBox jcmbType = (JComboBox) evt.getSource();
    Integer selectedVal = Integer.valueOf((String) jcmbType.getSelectedItem());
    
    Main.communicationFileReaderReadLine(selectedVal);
}//GEN-LAST:event_jComboBoxFileReaderDataActionPerformed

private void jButton_startAquisitionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_startAquisitionActionPerformed
    if(!Main.connected)
    {
        logger.logInfo("Not connected!");
        return;
    }
    Main.doAquisition();
}//GEN-LAST:event_jButton_startAquisitionActionPerformed

private void jButton_aquistion_stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_aquistion_stopActionPerformed
    if(!Main.connected)
    {
        logger.logInfo("Not connected!");
        return;
    }
    Main.stopAquisition();
}//GEN-LAST:event_jButton_aquistion_stopActionPerformed

private void jButton1_resetDeviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1_resetDeviceActionPerformed
    if(!Main.connected)
    {
        logger.logInfo("ERROR: not connected!");
        return;
    }
    jSlider_TriggerLevel.setValue(50);
    jComboBox_TimeTividerCh0.setSelectedIndex(0);
    jComboBox_TimeTividerCh1.setSelectedIndex(0);
    jComboBox_Resolution.setSelectedIndex(0);
    Main.sendDeviceRawDataByte(opCodes.RESET);
    logger.logInfo("Device Reset sent."); 
}//GEN-LAST:event_jButton1_resetDeviceActionPerformed

private void jTabbedPane_developerComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane_developerComponentResized
    if(!isThisFirstTextAreaMsgOutputComponentResizedNotification)
        jfreeChartPanel.setSize(jPanel1_DataView.getBounds().width,jPanel1_DataView.getBounds().height);
    else
        isThisFirstTextAreaMsgOutputComponentResizedNotification = false;
   
}//GEN-LAST:event_jTabbedPane_developerComponentResized

private void jButton_generateNewTimePerDivSettingsInLoggingOutputActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_generateNewTimePerDivSettingsInLoggingOutputActionPerformed

    Main.generateNewHardwareTimePerDivSettingsInLoggingOutput();
}//GEN-LAST:event_jButton_generateNewTimePerDivSettingsInLoggingOutputActionPerformed

private void jComboBox_TimeTividerCh1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_TimeTividerCh1ActionPerformed
    if(!Main.connected)
    {
        logger.logInfo("Not connected!");
        return;
    }
    JComboBox jcmbType = (JComboBox) evt.getSource();
    String cmbType = (String) jcmbType.getSelectedItem();
    
    Main.setDeviceTimeDivider(cmbType, 1);
}//GEN-LAST:event_jComboBox_TimeTividerCh1ActionPerformed

private void jButton1_singleVoltCh0MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton1_singleVoltCh0MouseClicked
// TODO add your handling code here:
}//GEN-LAST:event_jButton1_singleVoltCh0MouseClicked

private void jButton1_singleVoltCh0ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1_singleVoltCh0ActionPerformed
    if(!Main.connected)
    {
        logger.logInfo("Not connected!");
        return;
    }
    Main.getDeviceSingleVolt_ch0();
}//GEN-LAST:event_jButton1_singleVoltCh0ActionPerformed

private void jButtonTriggerOffMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButtonTriggerOffMouseClicked
// TODO add your handling code here:
}//GEN-LAST:event_jButtonTriggerOffMouseClicked

private void jButtonTriggerOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTriggerOffActionPerformed

    Main.setDeviceTriggerOff();
}//GEN-LAST:event_jButtonTriggerOffActionPerformed

private void jComboBox_ResolutionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox_ResolutionActionPerformed
    if(!Main.connected)
    {
        logger.logInfo("Not connected!");
        return;
    }
    JComboBox jcmbType = (JComboBox) evt.getSource();
    String cmbType = (String) jcmbType.getSelectedItem();

    Main.setDeviceSamplesPerFrameMode(cmbType);
}//GEN-LAST:event_jComboBox_ResolutionActionPerformed

private void jCheckBox_checkUndersamplingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_checkUndersamplingActionPerformed

    Main.checkUndersampling = jCheckBox_checkUndersampling.isSelected();
}//GEN-LAST:event_jCheckBox_checkUndersamplingActionPerformed

private void jCheckBox_showSerialDataStreamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_showSerialDataStreamActionPerformed

    Main.showSerialDataStream = jCheckBox_showSerialDataStream.isSelected();
}//GEN-LAST:event_jCheckBox_showSerialDataStreamActionPerformed

private void jCheckBox_TimingAnalysisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox_TimingAnalysisActionPerformed

    Main.timingAnalysis = jCheckBox_TimingAnalysis.isSelected();
}//GEN-LAST:event_jCheckBox_TimingAnalysisActionPerformed

private void jSlider_offsetXCh1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_offsetXCh1MouseReleased

    Main.OffsetXCh1 = (double)jSlider_offsetXCh1.getValue();
}//GEN-LAST:event_jSlider_offsetXCh1MouseReleased

private void jSlider_offsetYCh1MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_offsetYCh1MouseReleased
}//GEN-LAST:event_jSlider_offsetYCh1MouseReleased

private void jSlider_offsetYCh1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_offsetYCh1StateChanged
    synchronized(Main.OffsetYLockCh1)
    {
        Main.OffsetYCh1 = (double)jSlider_offsetYCh1.getValue()/100;
    }
}//GEN-LAST:event_jSlider_offsetYCh1StateChanged

private void jSlider_offsetXCh1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_offsetXCh1StateChanged
    synchronized(Main.OffsetXLockCh1)
    {
        Main.OffsetXCh1 = (double)jSlider_offsetXCh1.getValue();
    }
}//GEN-LAST:event_jSlider_offsetXCh1StateChanged

private void jLabel_offsetYMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_offsetYMouseClicked

    jSlider_offsetYCh0.setValue(0);
    jSlider_offsetYCh1.setValue(0);
}//GEN-LAST:event_jLabel_offsetYMouseClicked

private void jLabel_offsetXMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel_offsetXMouseClicked
    jSlider_offsetXCh0.setValue(0);                                     
    jSlider_offsetXCh1.setValue(0);
}//GEN-LAST:event_jLabel_offsetXMouseClicked

private void jSlider_offsetYCh0MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_offsetYCh0MouseReleased
// TODO add your handling code here:
}//GEN-LAST:event_jSlider_offsetYCh0MouseReleased

private void jSlider_offsetYCh0StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_offsetYCh0StateChanged
    synchronized(Main.OffsetYLockCh0)
    {
        Main.OffsetYCh0 = (double)jSlider_offsetYCh0.getValue()/100;
    }
}//GEN-LAST:event_jSlider_offsetYCh0StateChanged

private void jSlider_offsetXCh0MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jSlider_offsetXCh0MouseReleased
// TODO add your handling code here:
}//GEN-LAST:event_jSlider_offsetXCh0MouseReleased

private void jSlider_offsetXCh0StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider_offsetXCh0StateChanged
    synchronized(Main.OffsetXLockCh0)
    {
        Main.OffsetXCh0 = (double)jSlider_offsetXCh0.getValue();
    }
}//GEN-LAST:event_jSlider_offsetXCh0StateChanged

private void jPanel_offsetPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel_offsetPanelMouseClicked
    jSlider_offsetYCh0.setValue(0);
    jSlider_offsetYCh1.setValue(0);
    jSlider_offsetXCh0.setValue(0);                                     
    jSlider_offsetXCh1.setValue(0);
}//GEN-LAST:event_jPanel_offsetPanelMouseClicked



    /**
    * @param args the command line arguments
    */
    public static void mainOld() 
    {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ControllerGui().setVisible(true);
            }
        });
    }
    
    public int getLocalPort()
    {
        return Integer.parseInt(jTextField_portLocal.getText());
    }
    public int getRemotePort()
    {
        return Integer.parseInt(jTextField_devicePort.getText());
    }
    public String getRemoteHost()
    {
        return jTextField_deviceHostName.getText();
    }
    public String getSerialPortName()
    {
        return jTextField_serialPortName.getText();
    }
    public void setSerialPortName(String newPort)
    {
        jTextField_serialPortName.setText(newPort);
    }
    public void disableUdpPanel()
    {
        jPanel_UDP.setVisible(false);
    }
    public void disableMySqlPanel()
    {
        jPanel_MySql.setVisible(false);
    }
    public void disableRS232Panel()
    {
        jPanel_Rs232.setVisible(false);
    }
    public int getAdcBitValue()
    {
        return Integer.valueOf(jTextField_adcBitCount.getText());
    }
    public int getAdcLowestVoltageValue()
    {
        return Integer.valueOf(jTextField_adc_lowestVoltage.getText());
    }
    public int getAdcHighestVoltagetValue()
    {
        return Integer.valueOf(jTextField_adc_highestVoltage.getText());
    }
    public int getAdcCyclesForSingleDAQValue()
    {
        return Integer.valueOf(jTextField_AdcCyclesForSingleDataAquisition.getText());
    }
    public int getAdcSamplesPerFrameValue()
    {
        return Integer.valueOf(jTextField_adcSamplesPerFrame.getText());
    }
    public int getSystemClockMhzValue()
    {
        return Integer.valueOf(jTextField_systemCLockMhz.getText());
    }
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.ButtonGroup buttonGroup7;
    private javax.swing.ButtonGroup buttonGroup8;
    private javax.swing.JButton jButton1StopFileWriter;
    private javax.swing.JButton jButton1_resetDevice;
    private javax.swing.JButton jButton1_singleVoltCh0;
    private javax.swing.JButton jButtonSingleFrameCh0WithTrigger;
    private javax.swing.JButton jButtonStartSqConnection;
    private javax.swing.JButton jButtonTriggerEdgeNeg;
    private javax.swing.JButton jButtonTriggerEdgePos;
    private javax.swing.JButton jButtonTriggerOff;
    private javax.swing.JButton jButton_FileReaderStart;
    private javax.swing.JButton jButton_FileReaderStop;
    private javax.swing.JButton jButton_aquistion_stop;
    private javax.swing.JButton jButton_detectSerialPorts;
    private javax.swing.JButton jButton_generateNewTimePerDivSettingsInLoggingOutput;
    private javax.swing.JButton jButton_serialConnect;
    private javax.swing.JButton jButton_serialSendhexValues_temp;
    private javax.swing.JButton jButton_startAquisition;
    private javax.swing.JButton jButton_startFileWriter;
    public javax.swing.JCheckBox jCheckBox_TimingAnalysis;
    public javax.swing.JCheckBox jCheckBox_channel0DisplayOrNot;
    public javax.swing.JCheckBox jCheckBox_channel1DisplayOrNot;
    private javax.swing.JCheckBox jCheckBox_checkUndersampling;
    public javax.swing.JCheckBox jCheckBox_showSerialDataStream;
    public javax.swing.JComboBox jComboBoxFileReaderData;
    public javax.swing.JComboBox jComboBox_Resolution;
    public javax.swing.JComboBox jComboBox_TimeTividerCh0;
    public javax.swing.JComboBox jComboBox_TimeTividerCh1;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_offsetX;
    private javax.swing.JLabel jLabel_offsetY;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel1_DataView;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel2_CommunicationView;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel3_dataView;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9_FileReader;
    private javax.swing.JPanel jPanel_MySql;
    private javax.swing.JPanel jPanel_Rs232;
    private javax.swing.JPanel jPanel_UDP;
    private javax.swing.JPanel jPanel_offsetPanel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JSlider jSlider_TriggerLevel;
    private javax.swing.JSlider jSlider_offsetXCh0;
    private javax.swing.JSlider jSlider_offsetXCh1;
    private javax.swing.JSlider jSlider_offsetYCh0;
    private javax.swing.JSlider jSlider_offsetYCh1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane_developer;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    public javax.swing.JTextArea jTextAreaMsgOutput;
    public javax.swing.JTextArea jTextArea_debugOutDeveloper;
    private javax.swing.JTextField jTextField2_dbHost;
    private javax.swing.JTextField jTextField3_dbPort;
    private javax.swing.JTextField jTextField4_dbUser;
    private javax.swing.JTextField jTextField5_dbPwd;
    private javax.swing.JTextField jTextField6_db_db;
    private javax.swing.JTextField jTextFieldFileName;
    private javax.swing.JTextField jTextFieldFileNameToRead;
    private javax.swing.JTextField jTextField_AdcCyclesForSingleDataAquisition;
    public javax.swing.JTextField jTextField_adcBitCount;
    private javax.swing.JTextField jTextField_adcSamplesPerFrame;
    private javax.swing.JTextField jTextField_adc_highestVoltage;
    private javax.swing.JTextField jTextField_adc_lowestVoltage;
    private javax.swing.JTextField jTextField_deviceHostName;
    private javax.swing.JTextField jTextField_devicePort;
    private javax.swing.JTextField jTextField_portLocal;
    private javax.swing.JTextField jTextField_serialDataToSendDebug;
    private javax.swing.JTextField jTextField_serialPortName;
    private javax.swing.JTextField jTextField_systemCLockMhz;
    public javax.swing.JTextPane jTextPaneMessageOutput;
    private javax.swing.JToggleButton jToggleButton_connectUdp;
    // End of variables declaration//GEN-END:variables

}
