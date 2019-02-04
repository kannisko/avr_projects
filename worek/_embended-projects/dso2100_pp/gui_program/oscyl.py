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
import fcntl
import array as arr
import threading
import time
import sys
#import osc_gui
class oscyl_parport:
	OSC_IO_STATUS=2
	OSC_IO_CH1=0
	OSC_IO_CH2=1
	def __init__(self):
		self.vlock=threading.Lock()
		self.modified=False
		self.linia=[ [], [], [], [] ]
		self.quit=False
		self.auto_trig=1
		#self.fn="/dev/osc"
		self.fn=sys.argv[1]
		# The assignments below just create the fields in the class instance
		# The initial values are in fact set in the init_gui function
		# in the osc_gui module
		self.ack_chn=0x03; #Acq CH1 (01,02 = CH1,CH2)
		self.smp_rate=0x02; #01,02,03,04,05 .. = 100,50,25,20,10 .. MSa/s)
		self.rd2_step=0x02; #readout2 step + 1 (02,03 ..,0b)
		self.rd1_step=0x02; #readout1 step + 1 (02,03 ..,0b)
		self.ch1_trig=0x3b; # set CH1 AC, 5 V/Div, Trig CH1
							#(00,04,08 = DC,GND,AC)
							#(01,02,20,21,22,30,31,32,33 = 10m,20m,50m,0.1,0.2,0.5,1,2,5 V/Div)
							#(00,40,80 = Trig CH1,EXT,CH2)
		self.ch2_trig=0x3b; #set CH2 AC, 5 V/Div, Trig AC
							#(00,40,80 = Trig AC,TV-V,TV-H)
		self.rd2_shift=0x01; #readout2 shift (01,02,03,04,05 .. = 0,5,10,15,20 ..)
		self.trig_slope=0x01; #set Trig Slope pos (01,02 = neg,pos)
		self.ch1_v_offset=0x66;
		self.ch2_v_offset=0x5d;
		self.trig_level=0x80;
		self.ch1_offset=0xaa;
		self.ch2_offset=0x89;
		self.ch1_gain=0x7c;
		ch2_offset=0x89;
		self.ch2_gain=0xba;
	def open(self):
		self.fd=open(self.fn,"r+",0)
	def get_status_byte(self):
		fcntl.ioctl(self.fd,self.OSC_IO_STATUS," ")
		return ord(self.fd.read(1))
	def ver(self,val):
		res=self.get_status_byte()
		if res != val:
			raise("setup error expected:"+hex(val)+" received:"+hex(res))
	def send_command_byte(self,b):
		self.fd.write(chr(b))
	def download_data(self,chn):
		if chn==1:
			fcntl.ioctl(self.fd,self.OSC_IO_CH1," ")
		else: 
			fcntl.ioctl(self.fd,self.OSC_IO_CH2," ")
	def setup_parameters(self):
		while True:
			stb=o.get_status_byte()
			if stb == 0x0f:
				break
			#print hex(stb)
			o.send_command_byte(0xff)
			stb=o.get_status_byte()
			if stb == 0x0f:
				break
			#print ":"+hex(stb)
			o.send_command_byte(0x04)
		#We verify if the readout 2 does not try to reach beyond the 
		#5000 samples range!!!
		if ((self.rd2_shift-1)*5+500)*self.rd2_step > 5000:
			self.rd2_shift=int((5000/self.rd2_step-500)/5)+1
			if self.rd2_shift < 1:
				self.rd2_shift = 1
			if self.rd2_shift > 255:
				self.rd2_shift = 255
		self.vlock.acquire()
		self.ver(0x0f)
		self.send_command_byte(self.ack_chn);
		self.ver(0x0f)
		self.send_command_byte(self.ack_chn);
		self.ver(0x05)
		self.send_command_byte(self.smp_rate);
		self.ver(0x05)
		self.send_command_byte(self.smp_rate);
		self.ver(0x08)
		self.send_command_byte(self.rd2_step);
		self.ver(0x08)
		self.send_command_byte(self.rd2_step);
		self.ver(0x09)
		self.send_command_byte(self.rd1_step);
		self.ver(0x09)
		self.send_command_byte(self.rd1_step);
		self.ver(0x06)
		self.send_command_byte(self.ch1_trig);
		self.ver(0x06)
		self.send_command_byte(self.ch1_trig);
		self.ver(0x07)
		self.send_command_byte(self.ch2_trig);
		self.ver(0x07)
		self.send_command_byte(self.ch2_trig);
		self.ver(0x0a)
		self.send_command_byte(self.rd2_shift);
		self.ver(0x0a)
		self.send_command_byte(self.rd2_shift);
		self.ver(0x0b)
		self.send_command_byte(self.trig_slope);
		self.ver(0x0b)
		self.send_command_byte(self.trig_slope);
		self.ver(0x0c)
		self.send_command_byte(self.ch1_v_offset);
		self.ver(0x0c)
		self.send_command_byte(self.ch1_v_offset);
		self.ver(0x0d)
		self.send_command_byte(self.ch2_v_offset);
		self.ver(0x0d)
		self.send_command_byte(self.ch2_v_offset);
		self.ver(0x0e)
		self.send_command_byte(self.trig_level);
		self.ver(0x0e)
		self.send_command_byte(self.trig_level);
		self.ver(0x10)
		self.send_command_byte(self.ch1_offset);
		self.ver(0x10)
		self.send_command_byte(self.ch1_offset);
		self.ver(0x11)
		self.send_command_byte(self.ch1_gain);
		self.ver(0x11)
		self.send_command_byte(self.ch1_gain);
		self.ver(0x12)
		self.send_command_byte(self.ch2_offset);
		self.ver(0x12)
		self.send_command_byte(self.ch2_offset);
		self.ver(0x13)
		self.send_command_byte(self.ch2_gain);
		self.ver(0x13)
		self.send_command_byte(self.ch2_gain);
		self.ver(0x01)
		self.vlock.release()
		return
	
	def norm_acq(self):
		self.send_command_byte(0x02)
		print "wait for OSC CPU"
		while True:
			stb=self.get_status_byte()
			if stb==0x21:
				break;
			elif stb!=0x00:
				raise("unexpected status: "+hex(stb)+" should be 0x00 or 0x21")
		print "wait for trigger"
		while True:
			self.send_command_byte(0x99)
			stb=self.get_status_byte()
			if stb==0x00:
				break
			else:
				print "wait for trigger: "+hex(stb)
		print "wait for data"
		while True:
			if self.get_status_byte()==0x03:
				break
		self.download_data(2)
		res=self.fd.read(16384)
		return arr.array('B',res)
		
	def auto_acq(self,chn):
		#print "wait for OSC CPU"
		self.ack_chn=chn
		self.send_command_byte(0x02)
		while True:
			stb=self.get_status_byte()
			if stb==0x21:
				break;
			elif stb!=0x00:
				raise("unexpected status: "+hex(stb)+" should be 0x00 or 0x21")
		#print "wait for trigger"
		i=1
		while True:
			self.send_command_byte(0x99)
			stb=self.get_status_byte()
			i=i+1
			if stb!=0x21:
				break
			else:
				if i%1000==0 and self.auto_trig!=0:
					self.send_command_byte(0xfe)
				#print "wait for trigger: "+hex(stb)
		#print "wait for data"
		while True:
			if self.get_status_byte()==0x03:
				break
		self.download_data(chn)
		res=self.fd.read(1003)
		res=arr.array('B',res)
		#Check the "magic" bytes
		if res[1000]!=0x01 or res[1001]!=0xfe or res[1002]!=0x80:
			print "Data readout error!!!\n"+hex(res[1000])+","+hex(res[1001])+","+hex(res[1002])		
		#self.send_command_byte(0xff)
		return res
		
		
		
def start_gui():
	#Start the GUI thread
	import osc_gui
	gui=osc_gui.gui(o)
	gui.start()
	return gui
	

def contin():
	global o
	while True:
		o.setup_parameters()
		w=o.auto_acq(1)
		o.vlock.acquire()
		o.linia[0]=w[0:500]
		o.linia[1]=w[500:1000]
		o.vlock.release()
		o.setup_parameters()
		w=o.auto_acq(2)
		o.vlock.acquire()
		o.linia[2]=w[0:500]
		o.linia[3]=w[500:1000]
		o.modified=True
		o.vlock.release()
		#o.linia.draw()
		if o.quit:
			return
global o
o=oscyl_parport()
g=start_gui()
print "GUI started!!!"
#time.sleep(2)
print "acquisition started!!!"
o.open()
o.setup_parameters()
w=o.auto_acq(2)
o.setup_parameters()
w2=o.auto_acq(1)
o.setup_parameters()
w3=o.auto_acq(1)

contin()
a.run()
g.join()
#print "GUI started!!!"
#w=o.auto_acq()
#w=o.auto_acq()