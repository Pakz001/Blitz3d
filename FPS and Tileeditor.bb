; Left mouse and right mouse to edit
; space to switch displays (fps and edit)

; edit mode func
Global numnodes = 1000
Dim node(numnodes,100)
Global editx
Global edity



Graphics3D 640,480
SetBuffer BackBuffer()
Global minimapim = CreateImage(100,100)

Global cam = CreateCamera()
MoveEntity cam,0,0,-10
mousespeed#=0.5
cameraspeed#=0.1
camerasmoothness#=3

Global mode = 2
Global fpsmode = 1
Global edit = 2

Global thewall
Global thefloor
Global theceiling

Dim mapon(numnodes)
Dim mapent(numnodes)

thewall = CreateCube()
HideEntity thewall
ScaleEntity thewall,0.5,0.5,0.05 

updateminimap()

While KeyDown(1) = False

	Select mode
		Case edit
		If KeyHit(57) Then 
			mode = 1
			make3dlevel
		End If
		Cls
		DrawBlock minimapim,GraphicsWidth()-100,0
		Text 0,0,editx
		editfunc
		Flip
		Case fpsmode
		If KeyHit(57) Then mode = 2
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
		Flip
	End Select
Wend

End

Function CurveValue#(newvalue#,oldvalue#,increments )
If increments>1 oldvalue#=oldvalue#-(oldvalue#-newvalue#)/increments
If increments<=1 oldvalue=newvalue
Return oldvalue#
End Function

Function editfunc()
	If KeyDown(203) Then editx=editx-1:updateminimap()
	If KeyDown(205) Then editx=editx+1:updateminimap()
	If KeyDown(200) Then edity=edity-1:updateminimap()
	If KeyDown(208) Then edity=edity+1:updateminimap()
	drawnodes	
	editnodes
End Function

Function editnodes()

	If MouseDown(1) = True
		If nodeundermouse() = False Then
			Local f=freenumnode()
			node(f,0) = 1
			node(f,1) = (MouseX()/32)+editx
			node(f,2) = (MouseY()/32)+edity
			updateminimap()			
		End If
	End If
	If MouseHit(2) = True
		If nodeundermouse() = True
			node(undermousenode(),0) = node(undermousenode(),0) + 1
			If node(undermousenode(),0) = 3
				node(undermousenode(),0) = 1
			End If
		End If
	End If
End Function

Function undermousenode()
	Local x=editx+(MouseX()/32)
	Local y=edity+(MouseY()/32)
	For i=0 To numnodes
		If node(i,0) > 0
		If node(i,1) = x
		If node(i,2) = y
			Return i
		End If
		End If
		End If
	Next
End Function

Function nodeundermouse()
	Local x=editx+(MouseX()/32)
	Local y=edity+(MouseY()/32)
	For i=0 To numnodes
		If node(i,0) > 0
		If node(i,1) = x
		If node(i,2) = y
			Return True
		End If
		End If
		End If
	Next
End Function

Function freenumnode()
	For i=0 To numnodes
		If node(i,0) = 0 Then Return i
	Next

End Function

Function drawnodes()
	For i=0 To numnodes
		If node(i,0) > 0
		If node(i,1) > editx
		If node(i,2) > edity
		If node(i,1) < editx+(GraphicsWidth()/32)
		If node(i,2) < edity+(GraphicsHeight()/32)
			drawnode(i)
		End If
		End If
		End If
		End If
		End If
	Next	
End Function

Function drawnode(n)
	Local x = node(n,1)*32
	Local y = node(n,2)*32
	x=x-editx*32
	y=y-edity*32
	Color 255,255,255
	Rect x,y,32,32,True
	Select node(n,0)
		Case 1
			Color 50,50,50
			Rect x,y+10,32,10
		Case 2
			Color 50,50,50
			Rect x+10,y,10,32
	End Select
End Function

Function make3dlevel()
	For i=0 To numnodes
		If mapon(i) = True Then FreeEntity mapent(i)		
	Next
	For i=0 To numnodes
		If node(i,0) > 0
			mapent(i) = CopyEntity(thewall)
			PositionEntity mapent(i),node(i,1),0,node(i,2)
			If node(i,0) = 2 
				RotateEntity mapent(i),0,90,0
			End If
			mapon(i) = True
		End If
	Next
End Function


Function updateminimap()
	SetBuffer ImageBuffer(minimapim)
	ClsColor 0,0,0
	Cls
	Color 100,100,100
	Rect 0,0,99,99,False
	Color 255,255,255
	For i=0 To numnodes
	If node(i,0)>0
		x = (node(i,1)/2)+50
		y = (node(i,2)/2)+50
		Plot x,y
	EndIf
	Next
	Color 255,255,0
	x = editx/2+50
	y = edity/2+50
	Rect x,y,10,10,False
	SetBuffer BackBuffer()
End Function

