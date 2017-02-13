Global scale1# = 5
Global scale2# = scale1*2
Global scale3# = 20

Global width = 800
Global height = 600
Graphics3D width,height,32,2
SetBuffer BackBuffer()

AppTitle "Map generator"

; mapwidthheight --- needs a beefy pc for large sizes
Global mw=100
Global mh=100
;tilewidthheight --- for the minimap
Global tw=GraphicsWidth()/mw
Global th=GraphicsHeight()/mh
;min/maxroomsizewh - - generator variables
Global minroomsize = 5
Global maxroomsize = 15

Dim map(mw,mh)
; collisions
Const type_scenery=3
Const type_cam=1

Global alt = 0

Global cam=CreateCamera()
EntityRadius cam,1.5
EntityType cam,type_cam

CameraClsColor cam,71,71,71

Global mousespeed#=0.5
Global camspeed#=0.5
Global camsmoothness#=3


SeedRnd MilliSecs()

Dim entmap(mw,mh,2)

Global timer=CreateTimer(30)

;make the textures
Global texw = 256
Global texh = 256
Dim tex(10)
For ii=0 To 10
tex(ii) = CreateTexture(texw,texh)
SetBuffer TextureBuffer(tex(ii))
ClsColor 200,200,200
Cls
num=1000
If ii=2 Then num=3000
For i=0 To num
x = Rand(0,texw)
y = Rand(0,texh)
c = Rand(255)
Color c,c,c
Oval x,y,Rand(1,5),Rand(1,5)
Next
Next
SetBuffer BackBuffer()


remakelevel()
placeplayer

Global minmap = CreateImage(200,200)
makeminimap

Color 255,255,255
While KeyDown(1) = False
	WaitTimer timer	
	If KeyHit(2)=True
		remakelevel
		makeminimap		
		placeplayer

	End If
	UpdateWorld
	RenderWorld
	gameinput
	Text 0,0,"Press 1 - new level - mouse to turn - rmb/cursors move."
	DrawBlock minmap,width-200,0
	Color 255,255,0

	px = EntityX(cam)/(mw/20)
	py = EntityZ(cam)/(mh/20)
	Rect (width-200+(200-px)-5),(200-py)-5,10,10,False
	Flip
Wend
End

Function remakelevel()
	For y=0 To mh
	For x=0 To mw		
		FreeEntity entmap(x,y,0)		
		FreeEntity entmap(x,y,1)
		entmap(x,y,0) = 0
		entmap(x,y,1) = 0
		map(x,y) = 0
	Next
	Next
	makemap()
	makeents
	Collisions type_cam,type_scenery,2,3
End Function

;
; Here the cube map is made
;
;

Function makeents()
	For y=0 To mh
	For x=0 To mw
		If map(x,y) = 1
			entmap(x,y,0) = CreateCube()
			ScaleEntity entmap(x,y,0),scale1,1,scale1
			PositionEntity entmap(x,y,0),x*scale2,0,y*scale2
			EntityColor entmap(x,y,0),155,100,0
			EntityType entmap(x,y,0),type_scenery
			EntityTexture entmap(x,y,0),tex(0)
			
			entmap(x,y,1) = CreateCube()
			ScaleEntity entmap(x,y,1),scale1,1,scale1
			PositionEntity entmap(x,y,1),x*scale2,scale3,y*scale2
			EntityColor entmap(x,y,1),155,100,0
			EntityType entmap(x,y,1),type_scenery
			EntityTexture entmap(x,y,1),tex(1)

		End If
		If map(x,y) = 2
			entmap(x,y,0) = CreateCube()
			ScaleEntity entmap(x,y,0),scale1,scale3,scale1
			PositionEntity entmap(x,y,0),x*scale2,0,y*scale2
			EntityColor entmap(x,y,0),255,100,0
			EntityType entmap(x,y,0),type_scenery
			EntityTexture entmap(x,y,0),tex(2)
			
		End If
		If map(x,y) = 3
			entmap(x,y,0) = CreateCube()
			ScaleEntity entmap(x,y,0),scale1,1,scale1
			PositionEntity entmap(x,y,0),x*scale2,0,y*scale2
			EntityColor entmap(x,y,0),55,100,0
			EntityType entmap(x,y,0),type_scenery
			EntityTexture entmap(x,y,0),tex(3)


			entmap(x,y,1) = CreateCube()
			ScaleEntity entmap(x,y,1),scale1,1,scale1
			PositionEntity entmap(x,y,1),x*scale2,scale3,y*scale2
			EntityColor entmap(x,y,1),55,100,0
			EntityType entmap(x,y,1),type_scenery
			EntityTexture entmap(x,y,1),tex(4)

		End If
		If map(x,y) = 4 ; lava
			entmap(x,y,0) = CreateCube()
			ScaleEntity entmap(x,y,0),scale1,1,scale1
			PositionEntity entmap(x,y,0),x*scale2,0,y*scale2
			EntityColor entmap(x,y,0),155,10,1
			EntityType entmap(x,y,0),type_scenery
			EntityTexture entmap(x,y,0),tex(5)

			entmap(x,y,1) = CreateCube()
			ScaleEntity entmap(x,y,1),scale1,1,scale1
			PositionEntity entmap(x,y,1),x*scale2,scale3,y*scale2
			EntityColor entmap(x,y,1),155,100,0
			EntityType entmap(x,y,1),type_scenery
			EntityTexture entmap(x,y,1),tex(1)

		End If
		
	Next
	Next
End Function

Function newmap()
	For y=0 To mh
	For x=0 To mw
		map(x,y)=0
	Next
	Next
	makemap
End Function

Function makemap()
	map(mw/2,mh/2) = 3
	Local total=(mw*mh)*30; Rand(20000,150000)
	For i=0 To total
		x = Rand(maxroomsize+2,mw-(maxroomsize+2))
		y = Rand(maxroomsize+2,mh-(maxroomsize+2))
		If map(x,y) = 3
			a = Rand(0,4)
			w=Rand(minroomsize,maxroomsize)
			h=Rand(minroomsize,maxroomsize)
			Select a
				Case 0;nroom
				If fits(x-w/2,y-h,w,h-1) = True
					mr(x,y-h,x+w/2,y-h/2,x,y,x-w/2,y-h/2)
				EndIf
				Case 1;eroom
				If fits(x+1,y-h/2,w,h) = True
					mr(x+w/2,y-h/2,x+w,y,x+w/2,y+h/2,x,y)
				EndIf
				Case 2;sroom
				If fits(x-w/2,y+1,w,h) = True
					mr(x,y,x+w/2,y+h/2,x,y+h,x-w/2,y+h/2)
				EndIf
				Case 3;wroom
				If fits(x-w-1,y-h/2,w,h) = True
					mr(x-w/2,y-h/2,x,y,x-w/2,y+h/2,x-w,y)
				EndIf
			End Select
		End If
	Next
	; here we remove left over doors
	For y=2 To mh-2
	For x=2 To mw-2
		If map(x,y) = 3
			; if into darkness then remove
			If map(x-1,y) = 0 Or map(x+1,y) = 0
				map(x,y) = 2
			End If
			If map(x,y-1) = 0 Or map(x,y+1) = 0
				map(x,y) = 2
			End If
			cnt=0
			; every door if blocked remove
			For y1=y-1 To y+1
			For x1=x-1 To x+1
			If map(x1,y1) = 2 Then cnt=cnt+1
			Next
			Next
			If cnt>2 Then map(x,y)=2
		End If
	Next
	Next
	
End Function

; makeroom
Function mr(x1,y1,x2,y2,x3,y3,x4,y4)
	Local myw = x2-x1
	Local myh = y3-y1
	For y5=y1 To y3
	For x5=x4 To x2
		map(x5,y5) = 1
	Next
	Next
	For y5=y1 To y3
		map(x4,y5) = 2
		map(x2,y5) = 2		
	Next
	For x5=x4 To x2
		map(x5,y1) = 2
		map(x5,y3) = 2
	Next
	map(x1,y1) = 3
	map(x2,y2) = 3
	map(x3,y3) = 3
	map(x4,y4) = 3

	If Rand(2) = 1
	If Rand(2) = 1
		For y5 = y1+2 To y3-2
		For x5 = x4+2 To x2-2
			map(x5,y5) = 4
		Next
		Next	
	Else
		For y5 = y1+3 To y3-3
		For x5 = x4+3 To x2-3
			map(x5,y5) = 2
		Next
		Next
	End If	
	End If

End Function

; Is there anything in the map
Function fits(x,y,w,h)
	; if outside
	If x<0 Or y<0 Or x+w>mw Or y+h>mh Then Return False	
	; if inside
	For y1=y To y+h
	For x1=x To x+w
		If map(x1,y1)>0 Then Return False
	Next
	Next
	Return True
End Function

Function gameinput()
	;cam controls
	mx#=CurveValue(MouseXSpeed()*mousespeed,mx,camsmoothness)
	my#=CurveValue(MouseYSpeed()*mousespeed,my,camsmoothness)
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	pitch#=EntityPitch(cam)
	yaw#=EntityYaw(cam)
	pitch=pitch+my
	yaw=yaw-mx
	If pitch>89 pitch=89
	If pitch<-89 pitch=-89
	RotateEntity cam,0,yaw,0
	TurnEntity cam,pitch,0,0
	
	cx#=(KeyDown(32)-KeyDown(30))*camspeed
	cz#=(KeyDown(17)-KeyDown(31))*camspeed
	If MouseDown(2) = True Then cz=camspeed

	; Gravity
	TranslateEntity cam,0,-1,0
	MoveEntity cam,cx,0,cz

Return

End Function

Function placeplayer()
	Local exitloop = False
	Local cnt2=0
	While exitloop = False
		x = Rand(0,mw)
		y = Rand(0,mh)
		cnt=0
		For y1=-3 To 3
		For x1=-3 To 3
		If x+x1>0 And x+x1<mw And y+y1>0 And y+y1<mh
		If map(x+x1,y+y1) = 1 
			cnt=cnt+1
		End If
		End If
		Next
		Next
		If cnt>48
			exitloop = True
		End If
		cnt2=cnt2+1
		If cnt2>10000 Then remakelevel : cnt2=0
	Wend
	PositionEntity cam,x*scale2,8,y*scale2
	ResetEntity cam
End Function

Function makeminimap()
	SetBuffer ImageBuffer(minmap)
	ClsColor 0,0,0
	Cls
	For y=0 To mh
	For x=0 To mw
		If map(x,y)>0
		If map(x,y) = 1
			Color 150,150,150
		End If
		If map(x,y) = 2
			Color 250,250,250
		End If
		If map(x,y) = 3
			Color 50,50,50
		End If
		
		Plot 200-(x*200/mw),200-(y*200/mh)
		End If
	Next
	Next
	SetBuffer BackBuffer()
End Function

Function CurveValue#(newvalue#,oldvalue#,increments )
If increments>1 oldvalue#=oldvalue#-(oldvalue#-newvalue#)/increments
If increments<=1 oldvalue=newvalue
Return oldvalue#
End Function
