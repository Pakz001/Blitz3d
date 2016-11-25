Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()
Global sx,sy,ex,ey
Const mapwidth = 39
Const mapheight = 29
Const tilewidth = 16
Const tileheight = 16
Dim map(mapwidth,mapheight)
Dim olmap(mapwidth,mapheight)
Dim clmap(mapwidth,mapheight)
Type ol
	Field x,y,f,g,h,px,py
End Type
Type cl
	Field x,y,f,g,h,px,py
End Type
Type path
	Field x,y
End Type

sx = Rand(mapwidth)
sy = Rand(mapheight)


makemap()

While KeyDown(1) = False
	Cls
	ex = MouseX() / tilewidth
	ey = MouseY() / tileheight
	If MouseDown(1) = True Then
		sx = ex
		sy = ey
	End If
	findpath()	
	drawmap()
	drawpath()
	Text 0,0,"Press left mouse button to move red block"
	Text 0,12,"Move the mouse to see a path"
	Flip
Wend
End

Function findpath()
	If sx = ex And sy = ey Then Return False
	Delete Each cl
	Delete Each ol
	Delete Each path
	Dim olmap(mapwidth,mapheight)
	Dim clmap(mapwidth,mapheight)
	Local tx,ty,tf,tg,th,newx,newy,lowestf
	this.ol = New ol
	this\x = sx
	this\y = sy
	olmap(sx,sy) = True
	Repeat
		If openlistisempty() = True Then Return False
		lowestf = 100000
		For this.ol = Each ol
			If this\f < lowestf
				lowestf = this\f
				tx = this\x
				ty = this\y
				tf = this\f
				tg = this\g
				th = this\h
				tpx = this\px
				tpy = this\py
			End If
		Next
		If tx = ex And ty = ey 
			that.cl = New cl
			that\x = tx
			that\y = ty
			that\px = tpx
			that\py = tpy
			findpathback()
			Return True
		Else
			removefromopenlist(tx,ty)
			olmap(tx,ty) = False
			clmap(tx,ty) = True
			that.cl = New cl
			that\x = tx
			that\y = ty
			that\f = tf
			that\g = tg
			that\h = th
			that\px = tpx
			that\py = tpy
			For y=-1 To 1
			For x=-1 To 1
				newx = tx+x
				newy = ty+y
				If newx>=0 And newy>=0 And newx<=mapwidth And newy<=mapheight
				If olmap(newx,newy) = False
				If clmap(newx,newy) = False
				olmap(newx,newy) = True
				this.ol = New ol
				this\x = newx
				this\y = newy
				this\g = map(newx,newy)+1
				this\h = distance(newx,newy,ex,ey)
				this\f = this\g+this\h
				this\px = tx
				this\py = ty
				End If
				End If
				End If
			Next
			Next
		End If
	Forever
End Function

Function findpathback()
	x = ex
	y = ey
	that.path = New path
	that\x = x
	that\y = y
	Repeat
		For this.cl = Each cl
			If this\x = x And this\y = y
				x = this\px
				y = this\py
				that.path = New path
				that\x = x
				that\y = y
				Insert that Before First path
			End If
		Next
		If x = sx And y = sy Then Return
	Forever
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

Function drawpath()
	Color 255,255,0
	For this.path = Each path
		Oval this\x*tilewidth,this\y*tileheight,6,6,False
	Next
End Function

Function distance(x1,y1,x2,y2)
	Return Abs(x2-x1)+Abs(y2-y1)
End Function

Function drawmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		If map(x,y) = 0
			Color 50,50,255
		Else
			Color 50,map(x,y)*16,50
		End If
		Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
	Next
	Next
	Color 255,0,0
	Rect sx*tilewidth,sy*tileheight,tilewidth,tileheight,True
End Function

Function makemap()
	Dim map(mapwidth,mapheight)
	For i=0 To 25
		x1 = Rand(mapwidth)
		y1 = Rand(mapheight)
		For y2=-4 To 4
		For x2=-4 To 4
			x3 = x1+x2
			y3 = y1+y2
			If x3>=0 And y3>=0 And x3<=mapwidth And y3<=mapheight
				map(x3,y3) = map(x3,y3) + 1
			End If
		Next
		Next
		For y2=-2 To 2
		For x2=-2 To 2
			x3 = x1+x2
			y3 = y1+y2
			If x3>=0 And y3>=0 And x3<=mapwidth And y3<=mapheight
				map(x3,y3) = map(x3,y3) + 1
			End If
		Next
		Next
		For y2=-1 To 1
		For x2=-1 To 1
			x3 = x1+x2
			y3 = y1+y2
			If x3>=0 And y3>=0 And x3<=mapwidth And y3<=mapheight
				map(x3,y3) = map(x3,y3) + 1
			End If
		Next
		Next		
	Next
End Function
