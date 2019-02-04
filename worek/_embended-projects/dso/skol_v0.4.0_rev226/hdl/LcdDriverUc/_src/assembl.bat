SET file=lcdDrv

KCPSM3.EXE %file%.psm
ECHO Will delete all temp Files after closing this window!
pause
del PASS?.dat
del %file%.dec
del %file%.fmt
del %file%.hex
del %file%.m
del %file%*.mem
del constant.txt
del labels.txt
del %file%.log
del %file%.coe
del %file%.v
