Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()
Global sx,sy,ex,ey
Const mapwidth = 39
Const mapheight = 29
Const tilewidth = 16
Const tileheight = 16
Dim map(mapwidth,mapheight)
Type ol
	Field x,y,f,g,h,px,py
End Type
Type cl
	Field x,y,f,g,h,px,py
End Type
Type path
	Field x,y
End Type

While KeyDown(1) = False
	makemap
	setpathcoordinates()
	findpath
	drawmap
	drawpath
	Flip
	For i=0 To 100
		If KeyDown(1) = True Then End
		Delay 1
	Next
Wend
End

Function findpath()
	Delete Each cl
	Delete Each ol
	Delete Each path
	Local tx,ty,tf,tg,th,tpx,tpy,newx,newy,exitloop,lowestf
	a.ol = New ol
	a\x = sx
	a\y = sy
	While exitloop = False
		If openlistisempty() = True Then exitloop = True
		lowestf = 100000
		For b.ol = Each ol
			If b\f < lowestf
				lowestf = b\f
				tx = b\x
				ty = b\y
				tf = b\f
				tg = b\g
				th = b\h
				tpx = b\px
				tpy = b\py
			End If
		Next
		If tx = ex And ty = ey
			exitloop = True
			c.cl = New cl
			c\x = tx
			c\y = ty
			c\f = tf
			c\g = tg
			c\h = th
			c\px = tpx
			c\py = tpy
			findpathback
		Else
			removefromopenlist(tx,ty)
			d.cl = New cl
			d\x = tx
			d\y = ty
			d\f = tf
			d\g = tg
			d\h = th
			d\px = tpx
			d\py = tpy
			For y=-1 To 1
			For x=-1 To 1
				newx = tx+x
				newy = ty+y
				If newx=>0 And newy=>0 And newx=<mapwidth And newy=<mapheight
				If isonopenlist(newx,newy) = False
				If isonclosedlist(newx,newy) = False
				If map(newx,newy) > 1
					e.ol = New ol
					e\x = newx
					e\y = newy
					e\g = map(newx,newy) + 1
					e\h = distance(newx,newy,ex,ey)
					e\f = e\g+e\h
					e\px = tx
					e\py = ty
				End If
				End If
				End If
				End If
			Next
			Next
		End If
	Wend
End Function

Function drawpath()
	that.path = First path
	x1 = that\x
	y1 = that\y
	For this.path = Each path
		Color 255,255,255
		Line this\x*tilewidth+8,this\y*tileheight+8,x1*tilewidth+8,y1*tileheight+8
		x1 = this\x
		y1 = this\y
;		Oval this\x*tilewidth+8,this\y*tileheight+8,8,8,True
;		Color 255,255,255
;		Oval this\x*tilewidth+10,this\y*tileheight+10,4,4,True
	Next
End Function

Function findpathback()
	Local exitloop = False
	Local x = ex
	Local y = ey
	a.path = New path
	a\x = ex
	a\y = ey
	While exitloop = False
		For this.cl = Each cl
			If this\x = x And this\y = y 
				x = this\px
				y = this\py
				that.path = New path
				that\x = x
				that\y = y
			End If
		Next
		If x = sx And y = sy Then exitloop = True
	Wend
End Function

Function openlistisempty()
	For this.ol = Each ol
		Return False
	Next
	Return True
End Function

Function removefromopenlist(x,y)
	For this.ol = Each ol
		If this\x = x And this\y = y
			Delete this
			Return
		End If
	Next
End Function

Function isonopenlist(x,y)
	For this.ol = Each ol
		If this\x = x And this\y = y Then Return True
	Next
End Function

Function isonclosedlist(x,y)
	For this.cl = Each cl
		If this\x = x And this\y = y Then Return True
	Next
End Function

Function distance(x1,y1,x2,y2)
	Return Abs(x2-x1)+Abs(y2-y1)
End Function

Function setpathcoordinates()
	Local exitloop = False
	While exitloop = False
		sx = Rand(mapwidth)
		sy = Rand(mapheight)
		ex = Rand(mapwidth)
		ey = Rand(mapheight)
		If sx<>ex And sy<>ey
		If map(sx,sy) = 2 And map(ex,ey) = 2
		findpath()
		For this.path = Each path
			exitloop = True
		Next
		End If
		End If
	Wend
End Function

Function drawmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		Select map(x,y)
			Case 1 : Color 0,0,200
			Case 2 : Color 0,100,0
			Case 3 : Color 0,200,0
			Case 4 : Color 100,100,100
		End Select
		Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
		Color 0,0,0
		Rect x*tilewidth,y*tileheight,tilewidth+1,tileheight+1,False
	Next
	Next
	Color 0,0,0
	Oval sx*tilewidth+4,sy*tileheight+4,10,10,True
	Color 0,255,0
	Oval sx*tilewidth+6,sy*tileheight+6,6,6,True
	Color 255,255,255
	Text sx*tilewidth+14,sy*tileheight,"Start"	
	Color 0,0,0
	Oval ex*tilewidth+4,ey*tileheight+4,10,10,True
	Color 255,0,0
	Oval ex*tilewidth+6,ey*tileheight+6,6,6,True
	Color 255,255,255
	Text ex*tilewidth+14,ey*tileheight,"End"		
	Color 0,0,0
	Rect 0,0,400,16,True
	Color 255,255,255
	Text 0,0,"Water"
	Text 100,0,"Gras"
	Text 200,0,"Trees"
	Text 300,0,"Mountains"
	Color 0,0,200
	Rect 80,0,10,10,True
	Color 0,100,0
	Rect 180,0,10,10,True
	Color 0,200,0
	Rect 280,0,10,10,True
	Color 100,100,100
	Rect 380,0,10,10,True
	
End Function

Function makemap()
	For y=0 To mapheight
	For x=0 To mapwidth
		map(x,y) = 0
	Next
	Next
	For i=0 To 45
		x = Rand(mapwidth)
		y = Rand(mapheight)
		For y1=-4 To 4
		For x1=-4 To 4
			x2 = x+x1
			y2 = y+y1
			If x2=>0 And y2=>0 And x2=<mapwidth And y2=<mapheight
				map(x2,y2) = map(x2,y2) + 1
			End If
		Next
		Next
		For y1=-2 To 2
		For x1=-2 To 2
			x2 = x+x1
			y2 = y+y1
			If x2=>0 And y2=>0 And x2=<mapwidth And y2=<mapheight
				map(x2,y2) = map(x2,y2) + 1
			End If
		Next
		Next
		For y1=-2 To 2
		For x1=-2 To 2
			x2 = x+x1
			y2 = y+y1
			If x2=>0 And y2=>0 And x2=<mapwidth And y2=<mapheight
				map(x2,y2) = map(x2,y2) + 1
			End If
		Next
		Next		
	Next
	For y=0 To mapheight
	For x=0 To mapwidth
		If map(x,y) < 3 Then map(x,y) = 1
		If map(x,y) =>3 And map(x,y) < 5 Then map(x,y) = 2
		If map(x,y) =>5 And map(x,y) <9 Then map(x,y) = 3
		If map(x,y) => 9 Then map(x,y) = 4
	Next
	Next
End Function
