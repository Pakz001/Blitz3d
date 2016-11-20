; Platformer with lasers

Graphics 640,480,16,2
SetBuffer BackBuffer()

Dim map(15,10)

Type player
	Field x#,y#
	Field fall,fallspeed#,jump
	Field direction
	Field firedelay
End Type
Global p.player = New player

Type laser
	Field x#,y#,velx#,vely#
End Type

readlevel(1)

p\x = 0 : p\y = 32*7 : p\direction = 1

While KeyDown(1) = False
	Cls
	If MouseDown(1) = True Then p\x = MouseX() : p\y = MouseY()
	drawlevel
	drawplayer
	drawlasers
	updatelasers
	lasermapcollision
	gravity
	playercontrols
	Text 0,0,playermapcollision(0,0)
	Flip
Wend
End

Function inilaser(x,y,dir)
	this.laser = New laser
	this\x = x
	this\y = y
	this\velx = 3.5*dir
	this.laser = New laser
	this\x = x
	this\y = y
	this\velx = 3.5*dir
	this\vely = -.2
	this.laser = New laser
	this\x = x
	this\y = y
	this\velx = 3.5*dir
	this\vely = .2
End Function
Function updatelasers()
	For this.laser = Each laser
		this\x = this\x + this\velx
		this\y = this\y + this\vely
	Next
End Function
Function drawlasers()
	For this.laser = Each laser
		Color 255,0,0
		Oval this\x,this\y,8,8,True
	Next
End Function

Function lasermapcollision()
	For this.laser = Each laser
	px = (this\x) / 32
	py = (this\y) / 32
	For y1 = -1 To 1
	For x1 = -1 To 1
		If RectsOverlap(px+x1,py+y1,1,1,0,0,15,10) = True Then
			If map(px+x1,py+y1) = 1 Then
				If RectsOverlap(this\x+x,this\y+y,8,8,px*32+x1*32,py*32+y1*32,32,32) = True Then 
				delout = True
				End If
			End If
		End If
	Next:Next
	If RectsOverlap(this\x+x,this\y+y,8,8,0,0,15*32,10*32) = False Then
		delout = True
	End If
	If delout = True Then Delete this : delout = False
	Next
End Function


Function playercontrols()
	If KeyDown(205) = True Then ; right
		p\direction = 1
		If playermapcollision(1,0) = False Then
			p\x = p\x + 1
		End If
	End If
	If KeyDown(203) = True Then ; left
		p\direction = 2
		If playermapcollision(-1,0) = False Then
			p\x = p\x - 1
		End If
	End If
	If KeyDown(200) = True And p\jump = False Then
		If playermapcollision(0,-1) = False Then
			p\jump = True : p\fall = True
			p\fallspeed = - 4
		End If
	End If
	If KeyDown(57) = True ; space = fire
		If p\firedelay < MilliSecs() Then
			Select p\direction
			Case 1 ; right
			inilaser(p\x,p\y,1)
			Case 2 ; left
			inilaser(p\x,p\y,-1)
			End Select
			p\firedelay = MilliSecs() + 200
		End If
	End If
End Function

Function gravity()
	If playermapcollision(0,p\fallspeed) = True And p\jump = True Then
		p\y = p\y/32*32
		p\fallspeed = 0
	End If
	If playermapcollision(0,1) = False And p\fall = False Then
		p\fall = True
		p\fallspeed = 1
	End If
	If p\fall = True Then
		p\y=p\y + p\fallspeed
		p\fallspeed = p\fallspeed + .1
		For i=0 To p\fallspeed+1
			If playermapcollision(0,i) = True Then
				p\fall = False
				p\jump = False
				p\y = p\y+i/32*32
				Exit
			End If
		Next
	End If
End Function 

Function playermapcollision(x,y)
	Local px = (p\x + x) / 32
	Local py = (p\y + y) / 32
	For y1 = -1 To 1
	For x1 = -1 To 1
		If RectsOverlap(px+x1,py+y1,1,1,0,0,15,10) = True Then
			If map(px+x1,py+y1) = 1 Then
				If RectsOverlap(p\x+x,p\y+y,16,16,px*32+x1*32,py*32+y1*32,32,32) = True Then Return True
			End If
		End If
	Next:Next

	If RectsOverlap(p\x+x,p\y+y,17,16,16,0,14*32,9*32) = False Then
		Return True
	End If
End Function

Function drawplayer()
	Color 255,255,0
	Oval p\x,p\y,16,16,True
End Function

Function drawlevel()
	For y=0 To 10-1
	For x=0 To 15-1
		Select map(x,y)
			Case 1
				Color 255,255,255
				Rect x*32,y*32,32,32,True	
		End Select
	Next:Next
End Function

Function readlevel(level)
	Select level
		Case 1:Restore level1
	End Select
	For y=0 To 10-1
	For x=0 To 15-1
		Read a
		map(x,y) = a
	Next:Next
End Function

.level1
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,1,1,1,1,1,1,1,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,1,1,1,1,1,1
Data 0,0,0,0,0,0,1,1,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,1,1,1,1,1,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
