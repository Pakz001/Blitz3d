; Turning a cube into a wall
; use walls to create maps :)

Graphics3D 640,480
SetBuffer BackBuffer()

Global ang
Global wall

wall = CreateCube()
ScaleEntity wall,1,1,0.05 
PositionEntity wall,x,y,0
ang=0

cam = CreateCamera()
MoveEntity cam, 0,0,-4


While KeyDown(1) = False
	ang=ang+1
	If ang>359 Then ang=0
	RotateEntity wall,0,ang,0
	RenderWorld
	Flip
Wend

End
