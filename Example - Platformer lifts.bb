; For Blitz Basic 2d,3d,b+
; Platformer horizontal and vertical elevator example
;
; 1 - Regular tile
; 2 - Vertical elevator
; 3 - Horizontal elevator
;
; Elevator system specs
; 	The elevators will stop once it hits a surrounding tile.
;

;
; When a player is collided with a elevator then
;
; * read the elevators y position and update it with the player
; * update the players position with the x velocity of the hor elevator
;

Graphics 640,480,16,2
SetBuffer BackBuffer()

Dim map(15,10)

Type player
	Field x#,y#
	Field fall,fallspeed#,jump
	Field onelevator,elevatorid
End Type
Global p.player = New player

Type elevator
	Field x#,y#,velx#,vely#
	Field x1,y1,x2,y2
	Field kind ; 0 = vertical , 1 = horizontal
End Type

readlevel(1)
p\x = 9*32 : p\y = 8*32

While KeyDown(1) = False
	Cls	
	drawmap
	drawelevators
	drawplayer
	playercontrols
	gravity
	operateelevator
	playerelevatorsystem
	If MouseDown(1) = True Then p\x = MouseX() : p\y = MouseY()
	Text 0,0,playermapcollision(0,0)
	Text 0,10,playerelevatorcollision(0,0)
	Flip
Wend
End

Function playerelevatorsystem()
	; see if the player collides with the elevator
	If p\onelevator = False Then
		If playerelevatorcollision(0,0) = True Then
			; see which elevator he collided with
			p\elevatorid = whichelevatorcollide()
			p\onelevator = True
			p\jump = False
		End If
	End If
	;
	If p\onelevator = True
		If p\jump = True Then p\onelevator = False		
		Select getelevatortype(p\elevatorid)
			Case 0
			p\y = getelevatory(p\elevatorid) - 16
			Case 1
			p\x = p\x + elevatorxvelocity(p\elevatorid)
			p\y = getelevatory(p\elevatorid) - 16
		End Select
		If playerelevatorcollision(0,1) = False Then 
			p\onelevator = False
			p\fallspeed = 0 
		End If
	End If
	
End Function

Function elevatorxvelocity#(num)
	For this.elevator = Each elevator
		If num = cnt Then Return this\velx
		cnt = cnt + 1
	Next
End Function


Function getelevatortype(num)
	cnt = 0 
	For this.elevator = Each elevator
		If cnt = num Then
			Return this\kind
		End If
		cnt = cnt + 1
	Next
End Function

Function getelevatory(num)
	cnt = 0
	For this.elevator = Each elevator
		If cnt = num Then
			Return this\y
		End If
		cnt = cnt + 1
	Next
End Function

Function operateelevator()
	For this.elevator = Each elevator
		this\x = this\x + this\velx
		this\y = this\y + this\vely
		If this\y < this\y2 Then this\vely = -this\vely
		If this\y > this\y1 Then this\vely = -this\vely
		If this\x > this\x1 Then this\velx = -this\velx
		If this\x < this\x2 Then this\velx = -this\velx
		
	Next
End Function


Function playerelevatorcollision(x,y)
	For this.elevator = Each elevator
		If RectsOverlap(p\x+x,p\y+y,16,16,this\x,this\y,32,8) = True Then
			Return True
		End If
	Next
End Function

Function whichelevatorcollide()
	cnt = 0
	For this.elevator = Each elevator
		If RectsOverlap(p\x,p\y,16,16,this\x,this\y,32,8) = True Then
			Return cnt
		End If		
		cnt=cnt + 1
	Next
End Function

Function drawelevators()
	For this.elevator = Each elevator
		Color 0,0,255
		Rect this\x,this\y,32,8,True
	Next
End Function

Function playercontrols()
	If KeyDown(200) = True And p\jump = False Then ; jump
		p\jump = True
		p\fall = True
		p\fallspeed = -4
	End If
	If KeyDown(205) = True Then ; right
		If playermapcollision(1,0) = False Then
			p\x = p\x + 1
		End If
	End If
	If KeyDown(203) = True Then ; left
		If playermapcollision(-1,0) = False Then
			p\x = p\x - 1
		End If
	End If
End Function

Function gravity()
	If p\jump = True And playermapcollision(0,p\fallspeed) = True Then
		p\fall = True
		p\fallspeed = 0
	End If
	If p\fall = False And playermapcollision(0,1) = False Then
		p\fall = True
		p\fallspeed = 1
	End If
	If p\fall = True Then
		p\y = p\y + p\fallspeed
		p\fallspeed = p\fallspeed + .1
		For i=0 To p\fallspeed + 1
			If playermapcollision(0,i) = True Then
				p\y = p\y+i/32*32
				p\fall = False
				p\jump = False
				Exit
			End If
		Next
	End If
End Function

Function playermapcollision(x,y)
	px = (p\x + x) / 32
	py = (p\y + y) / 32
	;
	For y1=-1 To 1
	For x1=-1 To 1
		If RectsOverlap(px+x1,py+y1,1,1,0,0,15,10) = True Then
			If map(px+x1,py+y1) = 1 Then
				If RectsOverlap(p\x+x,p\y+y,16,16,(px+x1)*32,(py+y1)*32,32,32) = True Then
					Return True
				End If
			End If
		End If
	Next:Next
	If RectsOverlap(p\x+x,p\y+y,16,16,15,15,14*32,9*32) = False Then
		Return True
	End If
End Function

Function drawplayer()
	Color 255,255,0
	Oval p\x,p\y,16,16,True
End Function

Function drawmap()
	Color 255,255,255
	For y=0 To 10-1
	For x=0 To 15-1
		Select map(x,y)
			Case 1
			Rect x*32,y*32,32,32,True
		End Select
	Next:Next
End Function

Function readlevel(level)
	Select level
		Case 1:Restore level1
	End Select
	;
	For y=0 To 10-1
	For x=0 To 15-1
		Read a
		Select a
			Case 1
				map(x,y) = a
			Case 2 ; ver elevator
				this.elevator = New elevator
				this\x = x*32
				this\y = y*32
				this\vely = .5
				this\kind = 0
			Case 3 ; hor elevator
				this.elevator = New elevator
				this\x = x*32
				this\y = y*32
				this\velx = .5
				this\kind = 1
		End Select		
	Next:Next
	setelevatorlimits()
End Function

Function setelevatorlimits()
	; Find the edges for the elevator to stop and return
	;
	; vertical notes : elevators need to be placed at their bottom
	; position. The code scans up to until area edge collision.
	;
	; Horizontal notes : elevators need to be placed at the right
	; position. The code scans left until area edge collision
	;
	For this.elevator = Each elevator
		Select this\kind
			Case 0 ; vertical elevator
				this\x1 = this\x
				this\y1 = this\y
				For y=this\y/32-1 To 0 Step -1
					If map(this\x/32-1,y)  = 1 Or map(this\x/32+1,y) = 1 Then												
						Exit
					End If
				Next
				this\y2 = y*32
			Case 1
				this\x1 = this\x
				this\y1 = this\y
				For x = this\x/32-1 To 0 Step -1
					If map(x-1,this\y/32) = 1 Then
						Exit
					End If
				Next
				this\x2 = x*32
		End Select
	Next
End Function

.level1
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 1,1,1,1,0,0,0,3,1,1,1,1,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,1,1,1
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 1,1,1,1,1,1,1,1,1,1,1,2,1,1,1
