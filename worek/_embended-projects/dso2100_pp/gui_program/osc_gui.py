#!/usr/bin/python
# This program  is a "quick & dirty" implementation of the GUI
# for the parallel port controlled HC DSO2100 oscilloscope
# Written by Wojciech M. Zabolotny (wzab@ise.pw.edu.pl)
# 
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

import matplotlib
matplotlib.use('GTK')

from matplotlib.figure import Figure
from matplotlib.axes import Subplot
from matplotlib.backends.backend_gtkagg import FigureCanvasGTKAgg as FigureCanvas
from matplotlib.backends.backend_gtkagg import NavigationToolbar2GTKAgg as NavigationToolbar
from matplotlib.widgets import HorizontalSpanSelector
from matplotlib.numerix import arange, sin, pi
import sys
import threading
import gobject
try:
    import pygtk
    pygtk.require("2.0")
except:
    pass
try:
    import gtk
    import gtk.glade
except:
    sys.exit(1)
    
sens_table=(0x33,0x32,0x31,0x30,0x22,0x21,0x20,0x02,0x01)
sens_mask=0x33
input_table=(0x00,0x04,0x08) #DC, GND, AC
input_mask=0x0c
trig1_table=(0x00,0x40,0x80) #CH1, EXT, CH2
trig1_mask=0xc0
trig2_table=(0x00,0x40,0x80) #Trig AC, TV-V, TV-H
trig2_mask=0xc0

class HelloWorldGTK:
    def __init__(self,o):
        #Set the Glade file
        self.o=o
        self.gladefile = 'dso_gui.glade'
#		self.gladefile = 'pyhelloworld.glade2'
        self.wTree = gtk.glade.XML(self.gladefile) 
#		self.wTree.signal_autoconnect(dic)
        self.wTree.signal_autoconnect(self)
        self.figure = Figure(figsize=(8,6), dpi=72)
        self.axis = self.figure.add_subplot(111)

        w=arange(0,500)*256.0/499.0;
        #Now we create four lines, which will be used
        #to present four plots:
            # channel A - readout 1
            # channel A - readout 2
            # channel B - readout 1
            # channel B - readout 2
        self.linia_A1, =self.axis.plot(w)
        self.linia_A2, =self.axis.plot(w)
        self.linia_B1, =self.axis.plot(w)
        self.linia_B2, =self.axis.plot(w)
        self.init_gui()
        self.axis.set_xlabel('sample number')
        self.axis.set_ylabel('sample value')

        self.canvas = FigureCanvas(self.figure) # a gtk.DrawingArea
        self.canvas.show()
        self.canvas.set_size_request(600, 400)
        self.canvas.set_events(
            gtk.gdk.BUTTON_PRESS_MASK      |
            gtk.gdk.KEY_PRESS_MASK      |
            gtk.gdk.KEY_RELEASE_MASK
            )
        self.canvas.set_flags(gtk.HAS_FOCUS|gtk.CAN_FOCUS)
        self.canvas.grab_focus()
        self.wTree.get_widget('plotbox').pack_start(self.canvas, True, True)
        self.wTree.get_widget('plotbox').show()
        gobject.timeout_add(200,lambda:self.mydraw())
    def init_gui(self):
        #Initialize all ComboBox widgets
        combo_list=(
        ('A_coupl',2), #AC
        ('B_coupl',2), #AC
        ('A_sens',0), # 5V/div
        ('B_sens',0), # 5V/div
        ('Sampling',8), # 1MSmp/s
        ('trig_run_mode',1), #AUTO
        ('trig_mode',0), #AC
        ('trig_source',0), #Chan A
        ('trig_slope',0), # Negative slope
        )
        for i in combo_list:
            self.wTree.get_widget(i[0]).set_active(i[1])
        #Initialize all Scale widgets
        scale_list=(
        ('rd1_step',1),
        ('rd2_step',10),
        ('rd2_shift',10),
        ('chA_voffs_scale',128),
        ('chA_offs_scale',120),
        ('chA_gain_scale',68),
        ('chB_voffs_scale',128),
        ('chB_offs_scale',120),
        ('chB_gain_scale',68),
        )
        for i in scale_list:
            self.wTree.get_widget(i[0]).get_adjustment().set_value(i[1])
        #Initialize all CheckBox widgets
        chbox_list=(
        ('chA_rd1_disp',True),
        ('chA_rd2_disp',False),
        ('chB_rd1_disp',True),
        ('chB_rd2_disp',False),
        )
        for i in chbox_list:        
            #Dirty trick: to make sure, that the toggled method is triggered
            #we set the button first to True, then to False, and finally
            # to the correct value
            bt=self.wTree.get_widget(i[0])
            bt.set_active(i[1])
            bt.emit("toggled")
    def on_WindowMain_destroy(self,p):
        self.o.vlock.acquire()
        self.o.quit=True
        self.o.vlock.release()
        gtk.main_quit()
    def mydraw(self):
        if self.o.quit:
            gtk.main_quit()
        if self.o.modified:
            #print "Akuku!!\n"
            self.o.vlock.acquire()
            #if self.o.modified:
            self.linia_A1.set_ydata(self.o.linia[0])
            self.linia_A2.set_ydata(self.o.linia[1])
            self.linia_B1.set_ydata(self.o.linia[2])
            self.linia_B2.set_ydata(self.o.linia[3])
            self.o.modified=False
            self.o.vlock.release()
            self.canvas.draw()
        return True
            
    def on_btnHelloWorld_clicked(self, widget):
        print "Hello world"
    def set_A_sens(self,p):
        n=p.get_active()
        self.o.vlock.acquire()
        self.o.ch1_trig=(self.o.ch1_trig & 0xcc) | sens_table[n]
        self.o.vlock.release()
        #print "Set channel a to:"+str(p.get_active())
        #print(dir(p))
        #help(p)
    def set_B_sens(self,p):
        n=p.get_active()
        self.o.vlock.acquire()
        self.o.ch2_trig=(self.o.ch2_trig & 0xcc) | sens_table[n]
        self.o.vlock.release()
        #print "Set channel a to:"+str(p.get_active())
        #print(dir(p))
        #help(p)
    def set_sampling(self,p):
        n=p.get_active()
        self.o.vlock.acquire()
        self.o.smp_rate=n+1
        self.o.vlock.release()
        #print "Set channel a to:"+str(p.get_active())
        #print(dir(p))
        #help(p)
    
    def chA_voffs_set(self,p):
        n=p.get_adjustment().get_value()
        self.o.vlock.acquire()
        self.o.ch1_v_offset=n
        self.o.vlock.release()
        #print "Set channel a to:"+str(p.get_active())
        #print(dir(p))
        #help(p)
    def chB_voffs_set(self,p):
        n=p.get_adjustment().get_value()
        self.o.vlock.acquire()
        self.o.ch2_v_offset=n
        self.o.vlock.release()
        #print "Set channel a to:"+str(p.get_active())
        #print(dir(p))
        #help(p)
    def chA_offs_set(self,p):
        n=p.get_adjustment().get_value()
        self.o.vlock.acquire()
        self.o.ch1_offset=n
        self.o.vlock.release()
        #print "Set channel a to:"+str(p.get_active())
        #print(dir(p))
        #help(p)
    def chB_offs_set(self,p):
        n=p.get_adjustment().get_value()
        self.o.vlock.acquire()
        self.o.ch2_offset=n
        self.o.vlock.release()
        #print "Set channel a to:"+str(p.get_active())
        #print(dir(p))
        #help(p)
    def chA_gain_set(self,p):
        n=p.get_adjustment().get_value()
        self.o.vlock.acquire()
        self.o.ch1_gain=n
        self.o.vlock.release()
        #print "Set channel a to:"+str(p.get_active())
        #print(dir(p))
        #help(p)
    def chB_gain_set(self,p):
        n=p.get_adjustment().get_value()
        self.o.vlock.acquire()
        self.o.ch2_gain=n
        self.o.vlock.release()
        #print "Set channel a to:"+str(p.get_active())
        #print(dir(p))
        #help(p)
    def rd1_step_set(self,p):
        n=p.get_adjustment().get_value()
        self.o.vlock.acquire()
        self.o.rd1_step=n+1
        self.o.vlock.release()
    def rd2_step_set(self,p):
        n=p.get_adjustment().get_value()
        self.o.vlock.acquire()
        self.o.rd2_step=n+1
        self.o.vlock.release()
    def rd2_shift_set(self,p):
        n=p.get_adjustment().get_value()
        self.o.vlock.acquire()
        self.o.rd2_shift=n+1
        self.o.vlock.release()
    def on_trig_run_mode_changed(self,p):
        n=p.get_active()
        self.o.vlock.acquire()
        self.o.auto_trig=n
        self.o.vlock.release()
    def on_trig_mode_changed(self,p):
        n=p.get_active()
        self.o.vlock.acquire()
        #The line below requires the special ordering of options
        #Otherwise, a lookup table will be needed
        self.o.ch2_trig=(self.o.ch2_trig & 0x3f) | (n * 0x40)
        self.o.vlock.release()
    def on_trig_source_changed(self,p):
        n=p.get_active()
        self.o.vlock.acquire()
        #The line below requires the special ordering of options
        #Otherwise, a lookup table will be needed
        self.o.ch1_trig=(self.o.ch1_trig & 0x3f) | (n*0x40)
        self.o.vlock.release()
    def on_trig_slope_changed(self,p):
        n=p.get_active()
        self.o.vlock.acquire()
        #The line below requires the special ordering of options
        #Otherwise, a lookup table will be needed
        self.o.trig_slope=n+1
        self.o.vlock.release()
    def on_A_coupl_expose_event(self,p,e):
        print "Configure!!!"+str(p)+" "+str(e)
        p.set_active(2)
    def on_A_coupl_changed(self,p):
        n=p.get_active()
        self.o.vlock.acquire()
        #The line below requires the special ordering of options
        #Otherwise, a lookup table will be needed
        self.o.ch1_trig=(self.o.ch1_trig & 0xf3) | (n*0x04)
        self.o.vlock.release()
    def on_B_coupl_changed(self,p):
        n=p.get_active()
        self.o.vlock.acquire()
        #The line below requires the special ordering of options
        #Otherwise, a lookup table will be needed
        self.o.ch2_trig=(self.o.ch2_trig & 0xf3) | (n*0x04)
        self.o.vlock.release()
    def on_chB_rd1_disp_toggled(self,p):
        n=p.get_active()
        self.linia_B1.set_visible(n)
    def on_chB_rd2_disp_toggled(self,p):
        n=p.get_active()
        self.linia_B2.set_visible(n)
    def on_chA_rd1_disp_toggled(self,p):
        n=p.get_active()
        self.linia_A1.set_visible(n)
    def on_chA_rd2_disp_toggled(self,p):
        n=p.get_active()
        self.linia_A2.set_visible(n)
if __name__ == "__main__":
    hwg = HellowWorldGTK()
    gtk.main()

class gui(threading.Thread):
    def __init__(self,o):
        threading.Thread.__init__(self)
        self.hwg=HelloWorldGTK(o)
        self.o=o
    def flush_events():
        # Updates bounding boxes, among others.                                  
        while gtk.events_pending():                                              
           gtk.main_iteration()                                                 
    def run(self):
        gtk.gdk.threads_init()
        gtk.main()
        gtk.gdk.threads_leave()
        #while True:
            #self.o.vlock.acquire()
            #if self.o.modified:
                #self.linia.set_ydata(self.o.linia)
                #self.hwg.canvas.draw()
            #self.o.vlock.release()
            #self.flush_events()       
