;
; Dissintergrating tiles
;

Graphics 640,480,16,2
SetBuffer BackBuffer()

Dim map(15,10)

Type disstile ; open list
	Field x,y,timeout
End Type

Type player
	Field x,y
	Field fall,jump
	Field fallspeed#
End Type
Global p.player = New player

readlevel(1)

While KeyDown(1) = False
	Cls
	If MouseDown(1) = True Then p\x = MouseX() : p\y = MouseY()
	drawmap
	drawplayer
	gravity
	moveplayer
	disstiles ;(dissapearing / dissolving tiles)
	Text 0,0,disstilecollision() ; collide with such a tile? = 1
	Flip
Wend
End

Function disstiles()
	If disstilecollision() = True Then		
		px = (p\x)/32
		py = (p\y/32)+1
		If map(px,py) = 2 Then
			this.disstile = New disstile
			map(px,py) = 1
			this\x = px
			this\y = py
			this\timeout = MilliSecs() + 1000 ; time to dissapear in millisecond (1000 = 1 second)
		End If
	End If
	;
	For this.disstile = Each disstile
		If this\timeout < MilliSecs() Then 
			map(this\x,this\y) = 0
		End If
	Next
End Function

Function disstilecollision()
	px = (p\x) / 32
	py = (p\y) / 32
	For x=-1 To 1
	For y=-1 To 1
		If RectsOverlap(px+x,py+y,1,1,0,0,15,10) = True Then
			Rect px*32+x*32,py*32+y*32,32,32,False
			If map(px+x,py+y) = 2 Then 
				If RectsOverlap(p\x,p\y+1,16,16,px*32+x*32,py*32+y*32,32,32) = True Then Return True
			End If
		EndIf
	Next:Next
End Function

Function drawplayer()
	Color 255,255,0
	Oval p\x,p\y,16,16,True
End Function

Function moveplayer()
	If KeyDown(205) = True Then
		If playermapcollision(1,0) = False Then
		p\x = p\x + 1
		End If
	End If
	If KeyDown(203) = True Then
		If playermapcollision(-1,0) = False Then
		p\x = p\x - 1
		End If
	End If
	If KeyDown(57) = True Then
		If p\jump = False Then
			p\jump = True
			p\fallspeed = -4
			p\fall = True
		End If
	End If
End Function

Function drawmap()
	For x=0 To 15-1
	For y=0 To 10-1
		Select map(x,y)
		Case 1
		Color 255,255,255:Rect x*32,y*32,32,32,True
		Case 2
		Color 155,155,155 : Rect x*32,y*32,32,32,True
	End Select
	Next:Next
End Function

Function playermapcollision(x1,y1)
	px = (p\x+x1) / 32
	py = (p\y+y1) / 32
	For x=-1 To 1
	For y=-1 To 1
		If RectsOverlap(px+x,py+y,1,1,0,0,15,10) = True Then
			Rect px*32+x*32,py*32+y*32,32,32,False
			If map(px+x,py+y) = 1 Or map(px+x,py+y) = 2 Then 
				If RectsOverlap(p\x+x1,p\y+y1,16,16,px*32+x*32,py*32+y*32,32,32) = True Then Return True
			End If
		EndIf
	Next:Next
	If RectsOverlap(p\x+x1,p\y+y1,16,16,15,15,14*32,9*32) = False Then
		Return True
	End If
End Function

Function gravity()
	;
	;
	If playermapcollision(0,p\fallspeed) = True And p\jump = True Then
		p\fallspeed = 0
		p\y = p\y/32*32
	End If
	;
	If playermapcollision(0,1) = False And p\fall = False
		p\fall = True 
		p\fallspeed = 1
	End If
	;
	If p\fall = True Then
		p\y = p\y + p\fallspeed
		p\fallspeed = p\fallspeed + .1
		For i=0 To p\fallspeed
			If playermapcollision(0,i) = True Then
				p\y = p\y+i/32*32
				p\fall = False
				p\jump = False
				Exit
			End If
		Next
	End If
End Function

Function readlevel(level)
	Select level
		Case 1
		Restore level1
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
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,1,1
Data 0,0,0,0,1,1,1,1,1,1,1,2,2,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,1,1,1,2,2,1,1,1,1,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,1,1,1,2,2,2,1,1,1,1,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
