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
Type city
	Field id,x,y
End Type
Dim roadmap(mapwidth,mapheight)

makemap()
makecities()
makeroads
;findpath()
drawmap()
drawcities()
drawroads
Flip
WaitKey
End

Function makeroads()
	For this.city = Each city
	For that.city = Each city
		sx = this\x
		sy = this\y
		ex = that\x
		ey = that\y
		findpath()
		For a.path = Each path
			roadmap(a\x,a\y) = True
		Next
	Next
	Next
End Function

Function drawroads()
	Color 200,200,200
	For y=0 To mapheight
	For x=0 To mapwidth
		If roadmap(x,y) = True
			Oval x*tilewidth+3,y*tileheight+3,tilewidth-6,tileheight-6,True
		End If
	Next
	Next
End Function

Function drawcities()
	Color 255,255,255
	For this.city = Each city
		Rect this\x*tilewidth,this\y*tileheight,tilewidth,tileheight,True
	Next
End Function

Function makecities()
	While exitloop = False
		x = Rand(mapwidth)
		y = Rand(mapheight)
		ld = 100000
		For this.city = Each city
			If distance(this\x,this\y,x,y) < ld 
				ld = distance(this\x,this\y,x,y)
			End If
		Next
		If ld > 5
			this.city = New city
			this\id = n
			this\x = x
			this\y = y
			n=n+1
			If n=10 Then exitloop = True
		End If
	Wend
End Function

Function findpath()
	If sx = ex And sy = ey Then Return False
	Dim olmap(mapwidth,mapheight)
	Dim clmap(mapwidth,mapheight)
	Delete Each path
	Delete Each cl
	Delete Each ol
	Local tx,ty,tf,tg,th,tpx,tpy,newx,newy,lowestf
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
				clmap(tx,ty) = True
				that.cl = New cl
				that\x = tx
				that\y = ty
				that\f = tf
				that\g = tg
				that\h = th
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
					If newx>=0 And newy=>0 And newx<=mapwidth And newy<=mapheight
					If olmap(newx,newy) = False
					If clmap(newx,newy) = False
						olmap(newx,newy) = True
						this.ol = New ol
						this\x = newx
						this\y = newy
						If roadmap(newx,newy) = True Then
							this\g = 1
							Else
							this\g = map(newx,newy)+1
						End If
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

Function drawmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		Color map(x,y)*8,map(x,y)*8,map(x,y)*8
		Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
	Next
	Next
End Function

Function makemap()
	Dim map(mapwidth,mapheight)
	For i=0 To 24
		x1 = Rand(mapwidth)
		y1 = Rand(mapheight)
		For y2=-4 To 4
		For x2=-4 To 4
			x3 = x1+x2
			y3 = y1+y2
			If x3=>0 And y3=>0 And x3<=mapwidth And y3=<mapheight
				map(x3,y3) = map(x3,y3) + 1
			End If
		Next
		Next
		For y2=-2 To 2
		For x2=-2 To 2
			x3 = x1+x2
			y3 = y1+y2
			If x3=>0 And y3=>0 And x3<=mapwidth And y3=<mapheight
				map(x3,y3) = map(x3,y3) + 1
			End If
		Next
		Next
		For y2=-1 To 1
		For x2=-1 To 1
			x3 = x1+x2
			y3 = y1+y2
			If x3=>0 And y3=>0 And x3<=mapwidth And y3=<mapheight
				map(x3,y3) = map(x3,y3) + 1
			End If
		Next
		Next
		
	Next
End Function

