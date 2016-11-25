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

Repeat
	Cls
	makemap()
	setcoordinates()
	findpath()
	drawmap()
	drawpath()
	Flip
	If KeyDown(1) = True Then End
Forever

Function findpath()
	If sx=ex And sy=ey Then Return False
	Delete Each ol
	Delete Each cl
	Delete Each path
	Dim olmap(mapwidth,mapheight)
	Dim clmap(mapwidth,mapheight)
	Local tx,ty,tf,tg,th,tpx,tpy,newx,newy,lowestf
	a.ol = New ol
	a\x = sx
	a\y = sy
	olmap(sx,sy) = True
	Repeat
		If openlistisempty() = True Then Return False
		lowestf = 100000
		For a.ol = Each ol
			If a\f < lowestf
				lowestf = a\f
				tx = a\x
				ty = a\y
				tf = a\f
				tg = a\g
				th = a\h
				tpx = a\px
				tpy = a\py
			End If
		Next
		If tx = ex And ty = ey
			b.cl = New cl
			b\x = tx
			b\y = ty
			b\f = tf
			b\g = tg
			b\h = th
			b\px = tpx
			b\py = tpy
			findpathback()
			Return True
		Else
			removefromopenlist(tx,ty)
			olmap(tx,ty) = False
			clmap(tx,ty) = True
			b.cl = New cl
			b\x = tx
			b\y = ty
			b\f = tf
			b\g = tg
			b\h = th
			b\px = tpx
			b\py = tpy
			For y=-1 To 1
			For x=-1 To 1
			newx = tx+x
			newy = ty+y
			If newx>=0 And newy>=0 And newx<=mapwidth And newy<=mapheight
			If olmap(newx,newy) = False
			If clmap(newx,newy) = False
				a.ol = New ol
				a\x = newx
				a\y = newy
				a\g = map(newx,newy)+1
				a\h = distance(newx,newy,ex,ey)
				a\f = a\g+a\h
				a\px = tx
				a\py = ty
				olmap(newx,newy) = True
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
	While exitloop = False
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
		If x = sx And y = sy Then exitloop = True
	Wend
End Function

Function drawpath()
	For this.path = Each path
		x = this\x*tilewidth
		y = this\y*tileheight
		Color 155,155,155
		Oval x,y,tilewidth,tileheight,True
		Color 255,255,255
		Text x,y,cnt
		cnt=cnt+1
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

Function distance(x1,y1,x2,y2)
	Return Abs(x2-x1)+Abs(y2-y1)
End Function

Function setcoordinates()
	While exitloop = False
		sx = Rand(mapwidth)
		sy = Rand(mapheight)
		ex = Rand(mapwidth)
		ey = Rand(mapheight)
		If sx<>ex And sy<>ey Then exitloop = True
	Wend
End Function

Function drawmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		Color map(x,y)*8,map(x,y)*8,map(x,y)*8
		Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
	Next
	Next
	Color 255,255,255
	Rect sx*tilewidth-2,sy*tileheight-2,tilewidth+4,tileheight+4,False
	Rect ex*tilewidth-2,ey*tileheight-2,tilewidth+4,tileheight+4,False
End Function

Function makemap()
	Dim map(mapwidth,mapheight)
	For i=0 To 45
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
