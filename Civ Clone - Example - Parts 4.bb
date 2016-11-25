Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd 2
Global sx,sy,ex,ey
Const mapwidth = 39
Const mapheight = 29
Const tilewidth = 16
Const tileheight = 16
Dim map(mapwidth,mapheight)
Dim olmap(mapwidth,mapheight)
Dim clmap(mapwidth,mapheight)
Dim islandmap(mapwidth,mapheight)
Type ol
	Field x,y,f,g,h,px,py
End Type
Type cl
	Field x,y,f,g,h,px,py
End Type
Type path
	Field x,y
End Type
Type unit
	Field id,x,y,sx,sy,ex,ey,state,nextstate
End Type
Global myfont = LoadFont("verdana.ttf",10)
SetFont myfont

makemap()
setislandmap()
drawmap()
drawislandmap()
Flip
WaitKey
End


Function setislandmap()
	Delete Each ol
	Delete Each cl
	Dim olmap(mapwidth,mapheight)
	Dim clmap(mapwidth,mapheight)
	For y=0 To mapheight
	For x=0 To mapwidth
		If map(x,y) > 0 And islandmap(x,y) = 0
			cnt=cnt+1
			islandmap(x,y) = cnt
			this.ol = New ol
			this\x = x
			this\y = y
			While openlistisempty() = False
				this.ol = First ol
				tx = this\x
				ty = this\y
				removefromopenlist(tx,ty)
				olmap(tx,ty) = False
				clmap(tx,ty) = True
				For y1=-1 To 1
				For x1=-1 To 1
					newx = tx+x1
					newy = ty+y1
					If newx>=0 And newy>=0 And newx<=mapwidth And newy<=mapheight
					If map(newx,newy) > 0
					If olmap(newx,newy) = False
					If clmap(newx,newy) = False
						olmap(newx,newy)= True
						this.ol = New ol
						this\x = newx
						this\y = newy
						islandmap(Newx,newy) = cnt
					End If
					End If
					End If
					End If
				Next
				Next
				Cls
				If KeyDown(1) = True Then End
				drawmap
				drawislandmap
				Flip
			Wend
		End If
	Next
	Next
End Function

Function removefromopenlist(x,y)
	For this.ol = Each ol
		If this\x = x And this\y = y
			Delete this
			Return
		End If
	Next
End Function

Function openlistisempty()
	For this.ol = Each ol
		Return False
	Next
	Return True
End Function

Function drawislandmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		Color 255,255,255
		Text x*tilewidth+tilewidth/2,y*tileheight+tileheight/2,islandmap(x,y),True,True
	Next
	Next
End Function

Function drawmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		Select map(x,y)
			Case 0
			Color 0,0,200
			Default 
			Color 0,map(x,y)*16,0
		End Select
		Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
	Next
	Next
End Function

Function makemap()
	Dim map(mapwidth,mapheight)
	For i=0 To 13
		x1 = Rand(mapwidth)
		y1 = Rand(mapheight)
		For y2=-4 To 4
		For x2=-4 To 4
			x3 = x1+x2
			y3 = y1+y2
			If x3>=0 And y3=>0 And x3<= mapwidth And y3<=mapheight
				map(x3,y3) = map(x3,y3) + 1
			End If 
		Next
		Next
		For y2=-2 To 2
		For x2=-2 To 2
			x3 = x1+x2
			y3 = y1+y2
			If x3>=0 And y3=>0 And x3<= mapwidth And y3<=mapheight
				map(x3,y3) = map(x3,y3) + 1
			End If 
		Next
		Next
		For y2=-1 To 1
		For x2=-1 To 1
			x3 = x1+x2
			y3 = y1+y2
			If x3>=0 And y3=>0 And x3<= mapwidth And y3<=mapheight
				map(x3,y3) = map(x3,y3) + 1
			End If 
		Next
		Next
		
	Next
End Function
