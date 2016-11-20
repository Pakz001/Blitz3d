;
; Hero like platformer (jetpack)
;
; The map is 4 directional scrolling
; the player can plant explosives 'space' and blow up doors
; use the cursors to thrust and move
;
;


Const mapwidth = 128
Const mapheight = 128
Dim map(mapwidth,mapheight)

Type explosives
	Field x,y
	Field timeout
End Type
Type explosion
	Field x,y,velx#,vely#,timeout
End Type

Type gfx
	Field tile[32]
	Field player
	Field mapbuffer
End Type
Global gfx.gfx = New gfx

Type mymap
	Field x,y,cx,cy
	Field offx,offy
End Type
Global mymap.mymap = New mymap

Type player
	Field state
	Field x#,y#
	Field w,h
	Field incx#,incy#
	Field thrust
End Type
Global player.player = New player

Graphics 640,480,32,2
SetBuffer BackBuffer()
inigfx
For x=0 To 5
For y=0 To 5
insertprefab(Rand(2),x*12+3,y*12+3)
Next:Next
ClsColor 0,0,0

player\x = 128
player\y = 128-32
player\w = 10
player\h = 16
mymap\cy = 7
mymap\offy = -16
While KeyDown(1) = False
	Cls
	drawmap		
	If KeyDown(42) = False Then updatemap
	updateplayer
	updateexplosives
	updateexplosion
	playercontrols
;
	If KeyHit(57) = True Then playerdropexplosive
;
	If MouseDown(1) = True Then player\x = MouseX() : player\y = MouseY() 
	Text 0,0,playermapcollision(0,0)
	If MouseX() > 630 Then mymap\x = mymap\x + 1
	Flip
Wend
End

Function iniexplosion(x,y)
	For i=0 To 10
		this.explosion = New explosion
		this\x = x
		this\y = y
		this\velx = Rand(-3,3)
		this\vely = Rand(-3,3)
		this\timeout = MilliSecs() + 1000 + Rand(1000)
	Next
End Function

Function updateexplosion()
	For this.explosion = Each explosion
		this\x = this\x + this\velx
		this\y = this\y + this\vely
		If this\timeout < MilliSecs() Then Delete this
	Next
End Function

Function drawexplosion()
	For this.explosion = Each explosion
		Color 255,255,0
		Oval this\x,this\y,4,4
	Next
End Function

Function updateexplosives()
	For this.explosives = Each explosives
		If this\timeout < MilliSecs() Then 
		iniexplosion(this\x,this\y) 
		destroydoors(this\x,this\y)
		Delete this
		End If
	Next
End Function

Function drawexplosives()
	For this.explosives = Each explosives
		Color 150,0,0
		Rect this\x,this\y,5,7
	Next
End Function

Function playerdropexplosive()
	this.explosives = New explosives
	this\x = player\x
	this\y = player\y+10
	this\timeout = MilliSecs() + 3000
End Function

Function handleworld(x,y)
	For this.explosives = Each explosives
		this\x = this\x + x
		this\y = this\y + y
	Next
End Function

Function destroydoors(x,y)
	x = x  / 32 + 1
	y = y  / 32 
	For x1=-2 To 2
	For y1=-1 To 1
		If map(x+mymap\cx+x1,y+mymap\cy+y1) = 2 Then
		map(x+mymap\cx+x1,y+mymap\cy+y1) = 0
		End If
	Next:Next
End Function

Function updatemap()
	If player\y > 480-200 
		scrolldown() 
		handleworld(0,-1)
	End If
	While player\x > 640-200 
		scrollright()
		handleworld(-1,0)		
	Wend
	If mymap\cx > 0 
		While player\x < 200
		scrollleft() 
		handleworld(1,0)
		Wend
	End If
	If mymap\cy > 0 Then
		While player\y < 200 
		scrollup 
		handleworld(0,1)
		Wend
	End If
End Function

Function scrollup()
	mymap\offy = mymap\offy + 1
	player\y = player\y + 1
	If mymap\offy > 31 Then
		mymap\cy = mymap\cy - 1
		mymap\offy = 0
	End If
End Function

Function scrollleft()
	mymap\offx = mymap\offx + 1
	player\x = player\x + 1
	If mymap\offx > 31 Then
		mymap\cx = mymap\cx - 1
		mymap\offx = 0
	End If
End Function

Function scrollright()
	mymap\offx = mymap\offx - 1
	player\x = player\x - 1
	If mymap\offx < -31 Then
		mymap\cx = mymap\cx + 1
		mymap\offx = 0
	End If
End Function

Function scrolldown()
	mymap\offy = mymap\offy - 1
	player\y = player\y - 1
	If mymap\offy < -31 Then 
		mymap\cy = mymap\cy + 1
		mymap\offy = 0
	End If
End Function

Function updateplayer()
; Rocket thruster

If player\thrust = False
	If playermapcollision(0,12) = False 
	 player\thrust=True : player\incy=1
	End If
End If

If player\thrust = True Then

If playermapcollision(0,-5) = True Then
	player\incy = 1
End If

If player\incy < 0 Then player\incy = player\incy + .1 
If player\incy > 0 Then player\incy = player\incy + .01
player\y = player\y + player\incy

For i=0 To player\incy
If playermapcollision(0,i) = True Then
player\thrust = False
player\y = player\y/32*32-1
End If
Next
End If
End Function



Function playercontrols()
	If KeyDown(200) = True Then ; up
		player\incy = -1.5
		player\thrust = True		
	End If
	If KeyDown(203) = True Then ; left
		If playermapcollision(-1,0) = False Then
			player\x = player\x - 1
		End If
	End If
	If KeyDown(205) = True Then ; right
		If playermapcollision(1,0) = False Then
			player\x = player\x + 1
		End If
	End If
End Function

Function playermapcollision(x,y)
px = player\x / 32 
py = player\y / 32 
For x1=-1 To 3
For y1=-1 To 3
If RectsOverlap(px+x1,py+y1,1,1,0,0,mapwidth,mapheight) = True
If map(px+x1+mymap\cx,py+y1+mymap\cy) > 0 Then
;Rect px*32+mymap\offx+x1*32-32,py*32+mymap\offy+y1*32-32,32,32,False
If RectsOverlap(player\x+x,player\y+y,player\w,player\h,(px*32+x1*32)-32+mymap\offx,(py*32+y1*32)-32+mymap\offy,32,32) = True Then
;Rect px*32+mymap\offx+x1*32-32,py*32+mymap\offy+y1*32-32,32,32,False
Return True
End If
End If
End If
Next:Next
End Function

Function drawplayer()
	DrawImage gfx\player,player\x,player\y
End Function

Function drawmap()
	SetBuffer ImageBuffer(gfx\mapbuffer)
	Cls
	For y1 = 0 To 480+32 Step 32
	For x1 = 0 To 640+32 Step 32
		If RectsOverlap(1/32+mymap\cx,y1/32+mymap\cy,1,1,0,0,mapwidth,mapheight) = True Then
		If map(x1/32+mymap\cx,y1/32+mymap\cy) > 0 Then
			DrawImage gfx\tile[map(x1/32+mymap\cx,y1/32+mymap\cy)],x1+mymap\offx-32,y1+mymap\offy-32
		End If
		EndIf
	Next:Next	
	drawexplosion
	drawplayer
	drawexplosives
	SetBuffer BackBuffer()
	DrawBlock gfx\mapbuffer,0,0
End Function

Function inigfx()
	gfx\tile[1] = makeblocktile()
	gfx\tile[2] = makedoortile()
	gfx\player = makeplayerimage()
	gfx\mapbuffer = CreateImage(800,600)
End Function

Function makeplayerimage()
	Local tmpim = CreateImage(10,16)
	SetBuffer ImageBuffer(tmpim)
	ClsColor 0,0,0
	Cls
	Color 200,200,0
	Rect 0,0,10,16,True
	SetBuffer BackBuffer()
	Return tmpim

End Function

Function makedoortile()
	Local tmpim = CreateImage(32,32)
	SetBuffer ImageBuffer(tmpim)
	ClsColor 0,70,70
	Cls
	Color 100,100,100
	Rect 0,0,32,32,False
	SetBuffer BackBuffer()
	Return tmpim
End Function

Function makeblocktile()
	Local tmpim = CreateImage(32,32)
	SetBuffer ImageBuffer(tmpim)
	ClsColor 150,150,150
	Cls
	Color 200,200,200
	Rect 0,0,32,32,False
	SetBuffer BackBuffer()
	Return tmpim
End Function


Function insertprefab(prefab,x,y)
	Select prefab
		Case 1
		Restore prefab1
		Case 2
		Restore prefab2
	End Select
	For y1=0 To 7
	For x1=0 To 7
		Read a
		map(x1+x,y1+y) = a
	Next:Next
End Function


.prefab1
Data 1,1,1,1,1,1,1,1
Data 1,0,0,0,0,0,0,0
Data 1,0,0,0,0,0,0,0
Data 1,0,0,0,0,0,0,0
Data 1,1,1,1,1,1,0,0
Data 1,0,0,0,0,0,0,0
Data 1,0,0,0,0,0,0,0
Data 1,1,1,1,1,1,1,1
.prefab2
Data 1,1,1,1,1,1,1,1
Data 1,1,1,1,1,1,1,1
Data 1,1,1,1,1,1,1,1
Data 0,0,2,0,0,0,0,1
Data 1,1,1,1,0,0,0,1
Data 1,0,0,0,0,0,0,1
Data 1,0,0,0,0,0,0,1
Data 1,1,1,1,1,1,1,1
