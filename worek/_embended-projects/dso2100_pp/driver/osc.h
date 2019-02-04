/*
	Copyright  (C) 2006 Piotr Mikolajczuk
        some changes introduced in 2007 by Wojciech M. Zabolotny

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

#ifndef _OSC_H
#define _OSC_H

#include<linux/cdev.h>
#include<linux/parport.h>
#include"osc_user.h"

#define OSC_DEF_MAJOR_NR 62
#define OSC_DEV_COUNT 2 	       
#define OSC_PORT_NR 0x378

struct osc_struct{
	struct pardevice *pardev;
	struct cdev cdev;
	int is_parport_claimed;
	int osc_io_mode;
};

static void osc_attach (struct parport *port);
static void osc_detach (struct parport *port);

static int osc_open(struct inode *inode, struct file *filp); 
static int osc_release(struct inode *inode, struct file *filp); 
static ssize_t osc_read(struct file *filp, char *buf, size_t count, loff_t *f_pos); 
static ssize_t osc_write(struct file *filp, const char *buf, size_t count, loff_t *f_pos); 
static int osc_ioctl(struct inode *inode, struct file *filp, unsigned int cmd, unsigned long arg);
static void osc_exit(void);
static int osc_init(void);

#endif /* _OSC_H */
