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
	Field x,y,px,py
End Type
Type path
	Field x,y
End Type
Const sealevel = 5
Type city
	Field id,x,y,size
End Type
Dim fow(mapwidth,mapheight)

Repeat
	Cls
	makemap()
	makecities()
	updatefow() ; update fog of war array
	drawmap()
	drawcities()
	Flip
	For i=0 To 500
		If KeyDown(1) = True Then End
		Delay 1
	Next
Forever
End

Function updatefow()
	Local radius = 3
	Dim fow(mapwidth,mapheight)
	For this.city = Each city
		x1 = this\x
		y1 = this\y		
		For y2=-radius To radius
		For x2=-radius To radius
			If ((y2*y2)+(x2*x2)) <= radius*radius+radius*0.8
				x3 = x1+x2
				y3 = y1+y2
				If x3>=0 And y3>=0 And x3<=mapwidth And y3<=mapheight
					fow(x3,y3) = True
				End If
			End If
		Next
		Next
	Next
End Function

Function makecities()
	Delete Each city
	Repeat
		cont = True
		cnt = 0
		While cont = True
			cont = False
			onland = False
			While onland = False
				x = Rand(mapwidth)
				y = Rand(mapheight)
				If map(x,y) >= sealevel Then onland = True
			Wend
			For this.city = Each city
				d = distance(this\x,this\y,x,y)
				If d < 4 Then cont = True
			Next
			If cont = True Then cnt = cnt + 1
			If cnt > 50 Then Return
		Wend
		this.city = New city
		this\id = newcityid()
		this\x = x
		this\y = y
	Forever
End Function

Function newcityid()
	Local cnt = 0
	Local taken = False
	Repeat
		taken = False
		For this.city = Each city
			If this\id = cnt Then taken = True
		Next
		If taken = False
			Return cnt
		End If
		cnt = cnt + 1
	Forever
End Function

Function drawcities()
	For this.city = Each city
		If fow(this\x,this\y) = True 
			x = this\x*tilewidth
			y = this\y*tileheight
			Color 0,0,0
			Rect x,y,tilewidth,tileheight,True
			Color 255,255,255
			Rect x+1,y+1,tilewidth-2,tileheight-2,True
		End If
	Next
End Function

Function findpath()
	If sx = ex And sy = ey Then Return False
	Dim olmap(mapwidth,mapheight)
	Dim clmap(mapwidth,mapheight)
	Delete Each ol
	Delete Each cl
	Delete Each path
	this.ol = New ol
	this\x = sx
	this\y = sy
	Local tx,ty,tf,tg,th,tpx,tpy,newx,newy,lowestf
	olmap(sx,sy) = True
	Repeat
		If openlistisempty() = True Then Return False
		lowestf = 1000000
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
			that\px = tpx
			that\py = tpy
			For y=-1 To 1
			For x=-1 To 1
				newx = tx+x
				newy = ty+y
				If newx>=0 And newy>=0 And newx<=mapwidth And newy<=mapheight
				If olmap(newx,newy) = False
				If clmap(newx,newy) = False
				If map(newx,newy) => sealevel
					olmap(newx,newy) = True
					this.ol = New ol
					this\x = newx
					this\y = newy
					this\g = map(newx,newy) + 1
					this\h = distance(newx,newy,ex,ey)
					this\f = this\g+this\h
					this\px = tx
					this\py = ty
				End If
				End If
				End If
				End If
			Next
			Next
		End If
	Forever
End Function

Function drawpath()	
	Color 255,255,0
	For this.path = Each path
		Oval this\x*tilewidth,this\y*tileheight,tilewidth,tileheight,False
	Next
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

Function distance(x1,y1,x2,y2)
	Return Abs(x2-x1)+Abs(y2-y1)
End Function

Function drawmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		If fow(x,y) = True
			If map(x,y) < sealevel
				Color 0,0,100+map(x,y)*20
			Else
				Color 0,map(x,y)*8,0
			End If
			Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
		End If
	Next
	Next
End Function

Function makemap()
	Dim map(mapwidth,mapheight)
	While lowest < 13
		x1 = Rand(mapwidth)
		y1 = Rand(mapheight)
		radius = Rand(3,6)
		For y2=-radius To radius
		For x2=-radius To radius
			If ((x2*x2)+(y2*y2)) <= radius*radius+radius*0.8
				x3 = x1+x2
				y3 = y1+y2
				If x3=>0 And y3>=0 And x3=<mapwidth And y3=<mapheight
					map(x3,y3)=map(x3,y3)+1
					If map(x3,y3) > lowest Then lowest = map(x3,y3)
				End If
			End If
		Next
		Next
	Wend
End Function



