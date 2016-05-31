Graphics3D 800,600,32,2
SetBuffer BackBuffer()

AppTitle "Map generator"

; mapwidthheight
Global mw=100
Global mh=100
;tilewidthheight
Global tw=800/mw
Global th=600/mh
;maxroomsizewh
Global maxroomsize = 10

Dim map(mw,mh)

SeedRnd MilliSecs()

makemap

Global timer=CreateTimer(60)

Global camera = CreateCamera()
PositionEntity camera,40,50,0
RotateEntity camera,40,0,0
Global light = CreateLight()

Type e
	Field e
End Type

make3dmap

While KeyDown(1) = False
	WaitTimer timer
; Reset movement values - otherwise, the cone will not stop!
x#=0
y#=0
z#=0

; Change rotation values depending on the key pressed
If KeyDown( 203 )=True Then x#=-1
If KeyDown( 205 )=True Then x#=1
If KeyDown( 208 )=True Then y#=-1
If KeyDown( 200 )=True Then y#=1
If KeyDown( 44 )=True Then z#=-1
If KeyDown( 30 )=True Then z#=1

	z=.1
	y=.1
	z=.4
	cnt=cnt+1
	If cnt>200 Then
		newmap
		make3dmap
		cnt=0
		PositionEntity camera,40,50,0
	End If

	MoveEntity camera,x,y,z
	
	RenderWorld
	Flip
Wend
End

Function make3dmap()
	For this.e = Each e
		FreeEntity this\e
	Next
	Delete Each e
	For y=0 To mh-1
	For x=0 To mw-1
		If map(x,y) >0
			mye.e = New e
			mye\e = CreateCube()
			ScaleEntity mye\e,.50,.50,.50
			EntityColor mye\e,100,50,0
			If map(x,y)=2
			ScaleEntity mye\e,.5,3,.5
			EntityColor mye\e,200,100,0
			End If
			PositionEntity mye\e,x,0,y
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
	Local total=Rand(20000,150000)
	For i=0 To total
		x = Rand(maxroomsize,mw-maxroomsize)
		y = Rand(maxroomsize,mh-maxroomsize)
		If map(x,y) = 3
			a = Rand(0,4)
			w=Rand(5,maxroomsize)
			h=Rand(5,maxroomsize)
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

Function drawmap()
	For y=0 To mh
	For x=0 To mh
		Select map(x,y)
			Case 0;nothing
			Color 0,0,0
			Case 1;floor
			Color 255,255,255
			Case 2;wall
			Color 100,100,100
			Case 3;door
			Color 255,0,0
		End Select
		Rect x*tw,y*th,tw,th
	Next
	Next
End Function
