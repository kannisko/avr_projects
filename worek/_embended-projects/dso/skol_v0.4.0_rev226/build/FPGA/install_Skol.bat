
@echo setMode -bscan                                  > impact_batch_commands.cmd
@echo setCable -port usb21 -baud -1                  >> impact_batch_commands.cmd
@echo addDevice -position 1 -file .\Skol.bit     >> impact_batch_commands.cmd
@echo addDevice -position 2 -part "xcf04s"           >> impact_batch_commands.cmd
@echo ReadIdcode -p 1                                >> impact_batch_commands.cmd
@echo program -p 1                                   >> impact_batch_commands.cmd
@echo quit                                           >> impact_batch_commands.cmd
impact -batch impact_batch_commands.cmd
