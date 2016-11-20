;
; Platformer with ladders
;
; Type : single screen
;
;
; The collision used is done by scanning the 9 tiles ontop and around the
; player. In this example the outside of the map is checked and the block
; tiles. The collision was therefore done with rectsoverlap.
;

;
; A system checks flags whenever a player is ontop of a ladder and changes
; the player onladder states. Also it sets the nearest top and nearest 
; bottom coordinates which we can use to exit the ladder climbing state.
; When the player presses up or down he climbs until the ladderbound is
; reached and then sets the usingladder state to false.
;

;
; Graphics used : 
; Created in the program
; Level size : 
; 9*9 squares
; Special :
; Mouse left places player anywhere on the map


Graphics 500,400,32,2
SetBuffer BackBuffer()

Dim map(9,9)

Type fps
	Field fps,counter,timer
End Type
Global fps.fps = New fps

Type player
	Field x#,y#
	Field jump,fall,grav#
	Field onladder,ladderbottom,laddertop,usingladder
End Type
Global player.player = New player

Type mymap
	Field nothing
	Field block
End Type

Type gfx
	Field tiles[5]
	Field player
End Type
Global gfx.gfx = New gfx

player\grav = 0.1

; Initiate the system
inigfx
readlevel
player\fall = True

Color 255,255,255

While KeyDown(1) = False
	Cls
	drawlevel
	drawplayer
	playercontrol
	levelactive
	gravity
	
	If MouseDown(1) = True Then		player\x = MouseX():		player\y = MouseY()
	updatefps
	Text 0,0,fps\fps
	Flip True
Wend
End

Function playeronladder(x,y)
	px = ((player\x+x) / 32) - 1
	py = ((player\y+y) / 32) - 1
	If RectsOverlap(px,py,1,1,0,0,10,10) = False Then Return
	If map(px,py) = 2 Then Return True
	If map(px,py) = 3 Then Return True
End Function

Function levelactive()
	px = (player\x / 32) - 1
	py = (player\y / 32) - 1
	player\onladder = False
	If RectsOverlap(px,py,1,1,0,0,10,10) = False Then Return
	If map(px,py) = 2 Then player\onladder = True
	If map(px,py) = 3 Then player\onladder = True
	If map(px,py) = 2 Then player\laddertop = py*32 : Line 0,py*32,320,py*32
	If py-1 > 0 
		If map(px,py-1) = 3 Then player\laddertop = py*32
	EndIf
	If py < 9 Then
		If map(px,py+1) = 3 Then player\ladderbottom = py*32+32 : Line 0,py*32+96,320,py*32+96
		If map(px,py+1) = 2 Then player\ladderbottom = py*32+32 : Line 0,py*32+96,320,py*32+96
End If

If player\onladder = False Then player\usingladder = False

End Function

Function gravity()

	If player\fall = True Then
		For i=0 To player\grav
		player\y = player\y + .1;player\grav
		If playertilecollision(0,2) = True Or playeronladder(0,2) = True
			player\fall = False
			player\grav = False
			player\y = player\y/32*32	
			player\jump = False	
		End If
		Next
		player\grav = player\grav + .1
	End If
	

	If playertilecollision(0,2) = False And playeronladder(0,2) = False
		If player\usingladder = False
			player\fall = True

		End If
	End If
	
	If player\jump = True Then
		player\y=player\y + player\grav
		player\grav = player\grav + .1
		If player\grav > 0 Then player\jump = False : player\fall = True
	End If

End Function

Function playercontrol()
	If KeyDown(200) = True Then ; cursor up jump
		If player\jump = False And player\fall = False
		If player\onladder = False Then
		If playertilecollision(0,0) = False Then
			player\jump = True
			player\grav = -1.55
		End If
		End If
		If player\onladder = True Then
			If playertilecollision(0,-4) = False Then
				player\y = player\y - 1
				player\usingladder = True
				If player\y+16 < player\laddertop Then 
				player\onladder = False
				player\usingladder = False
				End If
			End If
		End If
		End If
	End If
	If KeyDown(205) = True Then ; cursor right ; right
		If playertilecollision(1,0) = False Then
			player\x = player\x + 1
		End If
	End If
	If KeyDown(203) = True Then ; cursor left ; left
		If playertilecollision(-1,0) = False Then
			player\x = player\x - 1
		End If
	End If
	If KeyDown(208) = True Then
		If playeronladder(0,2) = True Then
			If playertilecollision(0,0) = False
			player\onladder = True
			player\usingladder = True	
			player\y = player\y + 1
			If player\y > player\ladderbottom Then 
			player\onladder = False
			player\usingladder = False
			End If
			End If
		End If
	End If
End Function

Function playertilecollision(x1,y1)
;
; This collision checks a segment of the map against the player
;

; collide with walls
px = (player\x-32)/32
py = (player\y-32)/32
For y=-1 To 1
For x=-1 To 1
	If RectsOverlap(px+x,py+y,1,1,0,0,10,10) Then
	;Rect (px*32)+(x*32),(py*32)+(y*32),32,32,False
	If map(px+x,py+y) = 1 Then
		If RectsOverlap((player\x)+x1,(player\y)+y1,16,18,(px*32)+(x*32)+32,(py*32)+(y*32)+32,32,32) = True Then
			Return True
		End If
	End If
	End If
Next:Next

; out of level
If RectsOverlap(player\x+x1-16,player\y+16+y1,16,16,32,32,9*32,10*32) = False Then Return True

End Function

Function drawplayer()
	DrawImage gfx\player,player\x,player\y
End Function

Function drawlevel()
	For x=0 To 11
		DrawImage gfx\tiles[1],x*32,y*32
		DrawImage gfx\tiles[1],x*32,y*32+11*32
	Next
	For y=0 To 11
		DrawImage gfx\tiles[1],0*32,y*32
		DrawImage gfx\tiles[1],11*32,y*32
	Next
	For y=0 To 9
	For x=0 To 9
		tmp = map(x,y)
		DrawImage gfx\tiles[map(x,y)],x*32+32,y*32+32
	Next:Next
End Function

Function readlevel()
	Restore level1
	For y=0 To 9
	For x=0 To 9
		Read a
		If a = 8 Then player\x = 32*x+32+1 : player\y = 32*y+32+1
		Select a
			Case 0	
			map(x,y) = a
			Case 1
			map(x,y) = a
			Case 2
			map(x,y) = a
			Case 3
			map(x,y) = a
		End Select
	Next:Next
End Function

.level1
Data 9,0,0,0,0,0,0,0,0,0
Data 1,1,3,1,1,1,1,1,1,1
Data 0,0,2,0,0,0,0,0,0,0
Data 1,1,1,1,1,1,1,3,1,1
Data 0,0,0,0,0,0,0,2,0,0
Data 1,3,1,1,1,1,1,1,1,1
Data 0,2,0,0,0,0,0,0,0,0
Data 0,2,0,0,0,0,0,0,0,0
Data 1,1,1,1,1,1,1,1,3,1
Data 8,0,0,0,0,0,0,0,2,0

Function inigfx()
	gfx\tiles[0] = makeblacktile()
	gfx\tiles[1] = makeblocktile()
	gfx\tiles[2] = makeladdertile()
	gfx\tiles[3] = makeladdertile2()
	gfx\player = makeplayer()
End Function

Function makeplayer()
	Local tmpim = CreateImage(16,16)
	SetBuffer ImageBuffer(tmpim)
	;
	Color 0,255,0
	Oval 0,0,16,16
	Color 255,0,0
	Rect 0,0,16,16,False
	;
	SetBuffer BackBuffer()
	Return tmpim
End Function

Function makeladdertile2() ; with block behind it
	Local tmpim = CreateImage(32,32)
	SetBuffer ImageBuffer(tmpim)
	;
	For y=0 To 32 Step 9
		Color 200,200,0
		Rect 2,2+y,30,5
		Color 255,255,255
		For x1=0 To 1
		For y1=0 To 1
		Rect 4+x1*20,2+y,4,4
		Next:Next
	Next
	;
	SetBuffer BackBuffer()
	Return tmpim
End Function

Function makeladdertile()
	Local tmpim = CreateImage(32,32)
	SetBuffer ImageBuffer(tmpim)
	;
	ClsColor 0,0,0
	Cls
	For y=0 To 32 Step 9
		Color 200,200,0
		Rect 2,2+y,30,5
		Color 255,255,255
		For x1=0 To 1
		For y1=0 To 1
		Rect 4+x1*20,2+y,4,4
		Next:Next
	Next
	;
	SetBuffer BackBuffer()
	Return tmpim
End Function



Function makeblocktile()
	Local tmpim = CreateImage(32,32)
	SetBuffer ImageBuffer(tmpim)
	ClsColor 100,100,100
	Cls
	Color 150,150,150
	Rect 0,0,32,32,False
	SetBuffer BackBuffer()
	Return tmpim
End Function

Function makeblacktile()
	Local tmpim = CreateImage(32,32)
	SetBuffer ImageBuffer(tmpim)
	ClsColor 5,5,5
	Cls
	SetBuffer BackBuffer()
	Return tmpim
End Function

Function updatefps()
	fps\counter = fps\counter + 1
	If fps\timer < MilliSecs() Then
		fps\fps = fps\counter
		fps\counter = 0
		fps\timer = MilliSecs() + 1000
	End If
End Function
