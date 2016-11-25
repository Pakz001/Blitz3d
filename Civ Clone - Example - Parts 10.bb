; Another map generator
;
;

Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()
Const mapwidth = 39
Const mapheight = 29
Const tilewidth = 16
Const tileheight = 16
Dim map(mapwidth,mapheight)
Global myfont = LoadFont("verdana.ttf",35)
SetFont myfont

Repeat
	Cls
	makemap
	drawmap
	Color 255,255,255
	Text GraphicsWidth()/2,GraphicsHeight()/2,"Another Civ Clone Map Generator",1,1
	Flip
	For i=0 To 400
		If KeyDown(1) = True Then End
		Delay 1
	Next
Forever
End

Function makemap()
	Dim map(mapwidth,mapheight)
	For i=0 To 55
		x1 = Rand(mapwidth)
		y1 = Rand(mapheight)
		radius = Rand(1,10)
		For y2=-radius To radius
		For x2=-radius To radius
			If (x2*x2 + y2*y2) <= radius*radius+radius*0.8
				x3 = x1+x2
				y3 = y1+y2
				If x3>=0 And y3>=0 And x3<=mapwidth And y3<=mapheight
					map(x3,y3)=map(x3,y3) + 1
				End If
			End If
		Next
		Next
	Next
End Function

Function drawmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		If map(x,y) < 5
			Color 0,0,200
		Else
			Color 0,map(x,y)*8,0
		End If
		Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
	Next
	Next
End Function
