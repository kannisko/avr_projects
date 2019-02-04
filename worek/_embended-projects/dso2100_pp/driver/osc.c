/*
  Copyright  (C) 2006 Piotr Mikolajczuk
  Some corrections introduced in 2007 by Wojciech M. Zabolotny

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/



#include <linux/init.h>
//#include <linux/config.h>
#include <linux/module.h>
#include <linux/delay.h>
#include <linux/kernel.h> /* printk() */
#include <linux/slab.h> /* kmalloc() */
#include <linux/fs.h> /* everything... */
#include <linux/errno.h> /* error codes */
#include <linux/types.h> /* size_t */
#include <linux/proc_fs.h>
#include<linux/device.h>
#include<linux/cdev.h>
#include <linux/fcntl.h> /* O_ACCMODE */
#include <linux/ioport.h>
#include <asm/system.h> /* cli(), *_flags */
#include <asm/uaccess.h> /* copy_from/to_user */


#include"osc.h"
#define DEVICE_NAME "dso"
void cleanup_osc( void );

static struct osc_struct osc_dev[OSC_DEV_COUNT] ={[0 ... (OSC_DEV_COUNT-1)]={
    .pardev = NULL,
    .is_parport_claimed = 0,
    .osc_io_mode = OSC_IO_STATUS
  }};

static struct file_operations osc_fops = { 
  .owner = THIS_MODULE,
  .read = osc_read,
  .write = osc_write,
  .ioctl = osc_ioctl,
  .open = osc_open,
  .release = osc_release
};

static struct parport_driver osc_par_drv = {
  .name = "osc",
  .attach = osc_attach,
  .detach = osc_detach,	
};

dev_t osc_devno =  MKDEV(0,0);
struct cdev * osc_cdev = NULL;
static struct class *class_dso2100 = NULL;
static unsigned char parport_registered = 0;

static void osc_attach (struct parport *port)
{
  int port_num=port->portnum;
  if(port_num >= OSC_DEV_COUNT) return; // Do not attach to parport devices over OSC_DEV_COUNT
  osc_dev[port_num].pardev = parport_register_device (port, "osc", NULL, NULL, NULL, 0,&osc_dev[port_num]);
	
  if(osc_dev[port_num].pardev == NULL)
    {
      /*error*/
      printk(KERN_ERR "osc: parport_register_device() error\n");
      return;
    }
	
  printk(KERN_DEBUG "osc: using %s\n",port->name);
  return;
}

static void osc_detach (struct parport *port)
{
  int port_num=port->portnum;
  if(port_num >= OSC_DEV_COUNT) return; // Do not attach to parport devices over OSC_DEV_COUNT
  if (osc_dev[port_num].pardev)
    parport_unregister_device(osc_dev[port_num].pardev);
  osc_dev[port_num].pardev=0; 
  return;
}

static int __init osc_init(void) 
{	
  int err;
  int i;
  printk(KERN_INFO "osc: inserting osc module\n"); 	
  /* Create the class describing our device - udev support */
  class_dso2100 = class_create(THIS_MODULE, "dso2100");
  if (IS_ERR(class_dso2100)) {
    printk(KERN_ERR "Error creating rs_class.\n");
    err=PTR_ERR(class_dso2100);
    goto err1;
  }
  /* Allocate the device number */
  err=alloc_chrdev_region(&osc_devno, 0, OSC_DEV_COUNT, DEVICE_NAME);
  if(err) {
    printk ("<1>Alocation of the device number for %s failed\n",
            DEVICE_NAME);
    goto err1; 
  };
  /*register the parport driver*/
  err = parport_register_driver(&osc_par_drv);
  if(err)
    {
      printk(KERN_ERR "osc: error %d can't register driver with parport\n",err);
      goto err1;
    } else {
    parport_registered = 1;
  }		
  /* Allocate the character device structure */
  osc_cdev = cdev_alloc( );
  osc_cdev->ops = &osc_fops;
  osc_cdev->owner = THIS_MODULE;
  /* Add the character device */
  err=cdev_add(osc_cdev, osc_devno, OSC_DEV_COUNT);
  if(err) {
    printk ("<1>Registration of the device number for %s failed\n",
            DEVICE_NAME);
    goto err1;
  };
  for(i=0;i<OSC_DEV_COUNT;i++) {
    device_create(class_dso2100,NULL,MKDEV(MAJOR(osc_devno),i),"dso%d",i);
  }
  printk ("<1>%s The major device number is %d.\n",
	  "Registeration is a success.",
	  MAJOR(osc_devno));
  return 0; /* success */
 err1:
  cleanup_osc();
  return err;
}

/* cleanup, after init failure, or before module removal */
void cleanup_osc( void )
{
  int i;
  if(parport_registered) {
    parport_unregister_driver(&osc_par_drv);
  }
  for(i=0;i<OSC_DEV_COUNT;i++) {
    if(osc_dev[i].is_parport_claimed) 
      {
	parport_release(osc_dev[i].pardev);
	osc_dev[i].is_parport_claimed = 0;
      }
  }
  for(i=0;i<OSC_DEV_COUNT;i++) {
    if(osc_dev[i].pardev)
      {
	parport_unregister_device(osc_dev[i].pardev);
	osc_dev[i].pardev=0;
      }
  }
  /* Remove the device from the class */
  if(osc_devno && class_dso2100) {
    for (i=0;i<OSC_DEV_COUNT;i++) 
      device_destroy(class_dso2100,MKDEV(MAJOR(osc_devno),i));
  }
  if(osc_cdev) cdev_del(osc_cdev);
  osc_cdev=NULL;
  /* Free the device number */
  unregister_chrdev_region(osc_devno, OSC_DEV_COUNT);
  /* Unregister the class */
  if(class_dso2100) {
    class_destroy(class_dso2100);
    class_dso2100=NULL;
  }

}
static void __exit osc_exit(void) {
  cleanup_osc();	
  printk(KERN_INFO "osc: removing osc module\n");
}

static int osc_verify_connecion(struct parport *port)
{
	
  unsigned char byte;
  unsigned char control_byte;
			
	
  byte = 0;	
	
  control_byte = 0x00;
  port->ops->write_control(port, control_byte);	
	
  control_byte = 0x01;	
  port->ops->data_reverse(port);
	
  port->ops->write_control(port, control_byte);			
  byte = port->ops->read_data(port);
  //printk(KERN_DEBUG "Verify connection result:  %X \n",byte);
	
	
  //port->ops->data_reverse(port);
  control_byte = 0x00;
  port->ops->write_control(port, control_byte);	
	
  if(byte == 0x55) /* OK */
    return 0;
  else
    return -1;
	
}

unsigned char osc_load_ch1_data(struct parport *port)
{
  unsigned char control_byte;
  unsigned char byte;
	
  port->ops->data_reverse(port);
  control_byte = 0x0A;
  port->ops->write_control(port, control_byte);
	
  control_byte = 0x0B;
  port->ops->write_control(port, control_byte);
  ndelay(1000);	
  byte = port->ops->read_data(port);
	
  //printk(KERN_DEBUG "osc: loaded byte:  %X \n", byte);
	
  control_byte = 0x0A;
  port->ops->write_control(port, control_byte);
	
  return byte;
}

unsigned char osc_load_ch2_data(struct parport *port)
{
  unsigned char control_byte;
  unsigned char byte;
	
  port->ops->data_reverse(port);
  control_byte = 0x08;
  port->ops->write_control(port, control_byte);
	
  control_byte = 0x09;
  port->ops->write_control(port, control_byte);
  ndelay(1000);	
  byte = port->ops->read_data(port);
	
  //printk(KERN_DEBUG "osc: loaded byte:  %X \n", byte);
	
  control_byte = 0x08;
  port->ops->write_control(port, control_byte);
	
  return byte;

}

static int osc_send_command(struct parport *port, unsigned char byte)
{
  unsigned char control_byte;
  //unsigned char debug_byte;	
	
  port->ops->data_forward(port);
  control_byte = 0x06;
  port->ops->write_control(port, control_byte);
	
	
  /* send command byte to DSO*/	
	
  port->ops->write_data(port, byte);
  //port->ops->write_data(port, byte);
  //port->ops->write_data(port, byte);
	
	
  //debug_byte = port->ops->read_data(port);	
	
  //printk(KERN_DEBUG "osc: sended byte:  %X \n",debug_byte);
	
  control_byte = 0x06;
  port->ops->write_control(port, control_byte);
	
  control_byte = 0x07;
  port->ops->write_control(port, control_byte);
  ndelay(1000);	
  control_byte = 0x06;
  port->ops->write_control(port, control_byte);	
	
  port->ops->data_reverse(port);	
	
  control_byte = 0x06; //was 06 wzab
  port->ops->write_control(port, control_byte);	
	
  control_byte = 0x04; //was 04 wzab
  port->ops->write_control(port, control_byte);
	
  return 0;
}

unsigned char osc_receive_status(struct parport *port)
{
  unsigned char byte;
  unsigned char control_byte;			
	
  byte = 0;
	
  port->ops->data_reverse(port);	
  control_byte = 0x02; //was 02
  port->ops->write_control(port, control_byte);	
	
	
  control_byte = 0x02; //was 02
  port->ops->write_control(port, control_byte);
	
	
  control_byte = 0x03; //was 03
  port->ops->write_control(port, control_byte);
	
	
  control_byte = 0x03; //was 03
  port->ops->write_control(port, control_byte);
  ndelay(1000);	
  byte = port->ops->read_data(port);
	
	
  control_byte = 0x02; //was 02
  port->ops->write_control(port, control_byte);	
	
  control_byte = 0x02; //was 02
  port->ops->write_control(port, control_byte);	
	

  control_byte = 0x04; //was 04
  port->ops->write_control(port, control_byte);	
	
  return byte;
}

static int osc_open(struct inode *inode, struct file *filp) 
{
	
  struct osc_struct *dev;
  struct parport *port;
  int nrdev;	
  //dev = container_of(inode->i_cdev, struct osc_struct, cdev);
  nrdev=MINOR(inode->i_rdev);
  dev = &osc_dev[nrdev];
  filp->private_data = dev;
	
	
  /* claim port */
  if(dev->pardev == NULL)
    {
      /*error*/
      printk(KERN_ERR "osc: parport_device not registered - can't claim port\n");
      return -1;
    }
  else if(!dev->is_parport_claimed)
    {
      parport_claim_or_block(dev->pardev);
      dev->is_parport_claimed = 1;
      printk(KERN_DEBUG "osc: port claimed\n");
			
      /*
	mode = parport_negotiate(dev->pardev->port, IEEE1284_MODE_BYTE);
	printk(KERN_DEBUG "osc: mode negotiation: %d\n", mode);
      */			
			
      /* verify connection with DSO */
			
      port = dev->pardev->port;
			
      if(osc_verify_connecion(port))
	{
	  printk(KERN_ERR "osc: can't find DSO\n");
	  return -1;
	}
      else
	printk(KERN_DEBUG "osc: connection verified\n");

    }
	
  return 0;
}

static int osc_release(struct inode *inode, struct file *filp) 
{

  struct osc_struct *dev;
	
  dev = filp->private_data;
	
  if(dev->is_parport_claimed)
    {
      parport_release(dev->pardev);
      dev->is_parport_claimed = 0;
      printk(KERN_DEBUG "osc: port released\n");		
    }
  return 0; 
}

static int osc_ioctl(struct inode *inode, struct file *filp, unsigned int cmd, unsigned long arg)
{
  struct osc_struct *dev;	
  dev = filp->private_data;
  if((cmd == OSC_IO_CH1) || (cmd == OSC_IO_CH2) || (cmd ==OSC_IO_STATUS))
    {
      //printk(KERN_DEBUG "osc: ioctl %x\n", (int) cmd);
      dev->osc_io_mode = cmd;
      return 0;
    }
  else
    {
      printk(KERN_DEBUG "osc: wrong IO mode %x\n", (int) cmd);		
      return -1;
    }	
}

static ssize_t osc_read(struct file *filp, char *buf, size_t count, loff_t *f_pos) 
{

  /* Buffer to read the device */
  unsigned char byte;
  int err;
  int i;

  struct osc_struct *dev;	
  //printk(KERN_DEBUG "osc read %x\n", (int) count);
  dev =filp->private_data;
  for (i=0;i<count;i++) {
    switch(dev->osc_io_mode)
      {
      case OSC_IO_STATUS:
	byte = osc_receive_status(dev->pardev->port);
	break;
		
      case OSC_IO_CH1:
	byte = osc_load_ch1_data(dev->pardev->port);
	break;
			
      case OSC_IO_CH2:
	byte = osc_load_ch2_data(dev->pardev->port);
	break;
			
      default:
	printk(KERN_DEBUG "osc: wrong IO mode\n");		
	break;
      }	
	
    /* copy data to user space */
    err = copy_to_user(buf+i,&byte,1);
    if(err)
      return err;	
  }
  return count;
}

/* writes command bytes from user space*/
static ssize_t osc_write( struct file *filp, const char *buf, size_t count, loff_t *f_pos) {

	
  const char *tmp;
  /* Buffer writing to the device */	
	
  unsigned char byte;
  int err;
  int i;
	
  struct osc_struct *dev;	
  //printk(KERN_DEBUG "osc write %x\n", (int) count);
  dev =filp->private_data;
  for(i=0;i<count;i++) {
    /* reading next byte from user space buffer */
    tmp=buf+i;
    err = copy_from_user(&byte, tmp,1);
    if(err)
      return err;	
  
    osc_send_command(dev->pardev->port,  byte);
  }

  return count; 
}


module_init(osc_init);
module_exit(osc_exit);

MODULE_LICENSE("GPL");
MODULE_AUTHOR("Piotr Mikolajczuk & Wojciech Zabolotny");
