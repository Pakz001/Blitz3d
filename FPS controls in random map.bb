Graphics3D 800,600,0,2
SetBuffer BackBuffer()

SeedRnd MilliSecs()

Global mapwidth = 30
Global mapheight = 30
Dim map(mapwidth,mapheight)



alt = 0

cam=CreateCamera()

cameraclscolor cam,71,71,71

mousespeed#=0.1
cameraspeed#=0.5
camerasmoothness#=3

hidepointer

light=CreateLight()
RotateEntity light,90,0,0

movemouse 320,240

Type block
	Field e
End Type

p = CreatePlane()

EntityColor p,71,71,71
EntityFX p, 1

Global wall = CreateCube()

ScaleEntity wall,.4,4,.4

	makemap()
	Cls
	drawmap()

FreeEntity wall



While not KeyHit(1)
	
	Color 255,255,255

	;Camera controls
	mx#=CurveValue(MouseXSpeed()*mousespeed,mx,camerasmoothness)
	my#=CurveValue(MouseYSpeed()*mousespeed,my,camerasmoothness)
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	pitch#=EntityPitch(cam)
	yaw#=EntityYaw(cam)
	pitch=pitch+my
	yaw=yaw-mx
	If pitch>89 pitch=89
	If pitch<-89 pitch=-89
	RotateEntity cam,0,yaw,0
	TurnEntity cam,pitch,0,0
	
	cx#=(KeyDown(32)-KeyDown(30))*cameraspeed
	cz#=(KeyDown(17)-KeyDown(31))*cameraspeed
	MoveEntity cam,cx,0,cz



	RenderWorld
	Text 0,0,"WASD and mouse"
	Flip 1
Wend
End

Function makemap(steps = 100)
	Local aproved = False
	While aproved = False
		For y = 0 To mapheight
		For x = 0 To mapwidth
			map(x,y) = 0
		Next
		Next
		x = mapwidth / 2
		y = mapheight / 2
		steps = Rand(500) + 500
		For i=0 To steps
			nstepf = False
			While nstepf = False
				dir = Rand(8)
				Select dir
					Case 1 : nx = x - 1 : ny = y - 1
					Case 2 : ny = y - 1
					Case 3 : nx = x + 1 : ny = y - 1
 					Case 4 : nx = x - 1
					Case 5 : nx = x + 1
					Case 6 : nx = x - 1 : ny = y + 1
					Case 7 : ny = y + 1
					Case 8 : nx = x + 1 : ny = y + 1
				End Select
					If nx < mapwidth And nx > 0 And ny < mapheight And ny > 0 Then
						x = nx
						y = ny
						nstepf = True
					End If
			Wend
			drawbrush(x,y) 
		Next
		aproved = True
		For y=0 To mapheight
			If map(0,y) = 1 Then aproved = False
		Next
		For y=0 To mapheight
			If map(mapwidth,y) = 1 Then aproved = False
		Next
		For x=0 To mapwidth
			If map(x,0) = 1 Then aproved = False
		Next
		For x=0 To mapwidth
			If map(x,mapheight) = 1 Then aproved = False
		Next
		For y=0 To mapheight
			For x=mapwidth-7 To mapwidth
				If map(x,y) = 1 Then aproved = False
			Next
		Next
		hasone = False
		For y=0 To mapheight
			If map(mapwidth-8,y) = 1 Then hasone = True
		Next
		If hasone = False Then aproved = False
		hasone = False
		For y=0 To mapheight
			If map(3,y) = 1 Then hasone = True
		Next
		If hasone = False Then aproved = False
		hasone = False
		For x=0 To mapwidth
			If map(x,3) = 1 Then hasone = True
		Next
		If hasone = False Then aproved = False
		hasone = False
		For x=0 To mapwidth
			If map(x,mapheight-3) = 1 Then hasone = True
		Next
		If hasone = False Then aproved = False
	Wend
	; turn the 0 into 1(blocked) and
	; 1 into 0 (open)
	; map() is a collision map sort of
	For y = 0 To mapheight
	For x = 0 To mapwidth
		If map(x,y) = 1 Then map(x,y)=0 Else map(x,y)=1
	Next
	Next
End Function 

Function drawbrush(x,y)
	x1 = x - 1
	y1 = y - 1
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
	x1 = x
	y1 = y - 1
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
	x1 = x + 1
	y1 = y - 1
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
	x1 = x - 1
	y1 = y 
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
	x1 = x + 1
	y1 = y
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
	x1 = x - 1
	y1 = y + 1
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
	x1 = x 
	y1 = y + 1
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
	x1 = x + 1
	y1 = y + 1
	If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
		map(x1,y1) = 1
	End If
End Function

Function drawmap()
	Color 255,0,0
	For y = 0 To mapheight
		For x = 0 To mapwidth
			If map(x,y) = 1 Then

				t.block=New block
				t\e=CopyEntity(wall)
				EntityAlpha t\e,0.5
				PositionEntity t\e,x,1,y
				if alt=0 then
					EntityColor t\e,255,0,0
					alt=1
				else
					EntityColor t\e,0,255,0
					alt=0
				EndIf	

				;Rect x*16,y*16,16,16,True
			End If
		Next
	Next
End Function



Function CurveValue#(newvalue#,oldvalue#,increments )
If increments>1 oldvalue#=oldvalue#-(oldvalue#-newvalue#)/increments
If increments<=1 oldvalue=newvalue
Return oldvalue#
End Function

