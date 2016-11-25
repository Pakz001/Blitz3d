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
Type unit
	Field x,y,sx,sy,ex,ey,id,ploc
	Field px[1024]
	Field py[1024]
	Field state$,moves#,visible
End Type
Type city
	Field id,x,y,size
End Type
Dim roadmap(mapwidth,mapheight)
Dim citybrush(4,4)
Global turn

makemap()
makecities()
makecitybrush()
makeroadmap()
makeunits()
;setcoordinates()
;findpath()
;drawmap()
;drawpath()
While KeyDown(1) = False
	Cls
	dounits()
	gameloop()
	For this.unit = Each unit
		this\moves = 1
	Next
	turn = turn + 1
	Flip
Wend
End

Function gameloop()	
	drawmap()
	drawroads()
	drawcities()
	drawunits()
	Color 255,255,255
	Text 0,0,"Turn :"+turn
End Function

Function dounits()
	For this.unit = Each unit
		While this\moves > 0
			Select this\state$
				Case "findnewpos"
					Repeat
						x = Rand(mapwidth)
						y = Rand(mapheight)
						If map(x,y) > 0 Then Exit
					Forever
					this\sx = this\x
					this\sy = this\y
					this\ex = x
					this\ey = y
					sx = this\sx
					sy = this\sy
					ex = this\ex
					ey = this\ey
					If findpath() = True
						cnt = 0
						For that.path = Each path
							this\px[cnt] = that\x
							this\py[cnt] = that\y
							cnt=cnt + 1
						Next
						this\ploc = 0
						this\state$ = "move"
					End If
				Case "move"
					this\ploc = this\ploc + 1
					ox = this\x
					oy = this\y
					this\x = this\px[this\ploc]
					this\y = this\py[this\ploc]
					x1 = ox*tilewidth
					y1 = oy*tileheight
					x2 = this\x*tilewidth
					y2 = this\y*tileheight
					exitloop = False
					this\visible = False
					While exitloop = False
						Cls
						gameloop()
						For i=0 To 1
						If x1 > x2 Then x1 = x1 - 1
						If x1 < x2 Then x1 = x1 + 1
						If y1 > y2 Then y1 = y1 - 1
						If y1 < y2 Then y1 = y1 + 1
						If x1 = x2 And y1 = y2 Then exitloop = True
						Next
						Color 255,0,0
						drawunit(x1,y1)
						Flip
						If KeyDown(1) = True Then End
						Delay 1
					Wend				
					this\visible = True	
					If roadmap(this\x,this\y) = True
						this\moves = this\moves	 - .3
						Else
						this\moves = this\moves - 1
					End If
					If this\x = this\ex And this\y = this\ey
						this\state$ = "findnewpos"
					End If
			End Select
		Wend
	Next
End Function

Function makeunits()
	For i=0 To 15
		Repeat
			x = Rand(mapwidth)
			y = Rand(mapheight)
			If map(x,y) > 0
				Exit
			End If
		Forever	
		this.unit = New unit
		this\x = x
		this\y = y
		this\id = i
		this\moves = 2
		this\state$ = "findnewpos"
		this\visible = True
	Next
End Function

Function drawunit(x,y)
	Color 0,0,0
	Rect x,y,tilewidth,tileheight,True
	Color 255,0,0
	Rect x+1,y+1,tilewidth-2,tileheight-2,True
	Color 255,255,255
	Text x+tilewidth/2,y+tileheight/2,"U",True,True
End Function

Function drawunits()
	For this.unit = Each unit
		If this\visible = True 
			x = this\x * tilewidth
			y = this\y * tileheight
			drawunit(x,y)
		End If
	Next
End Function

Function makeroadmap()
	For this.city = Each city
	For that.city = Each city
		sx = this\x
		sy = this\y
		ex = that\x
		ey = that\y
		If findpath() = True
			For thus.path = Each path
				roadmap(thus\x,thus\y) = True
			Next
		End If
		For y=0 To 4
		For x=0 To 4
			If this\x - 2 + x =>0 And this\y-2+y=>0 And this\x-2+x<=mapwidth And this\y-2+y<=mapheight
			If citybrush(x,y) = 1
			If map(this\x-2+x,this\y-2+y) > 0
				roadmap(this\x-2+x,this\y-2+y) = True
			End If
			End If
			End If
		Next
		Next
		
	Next
	Next
End Function

Function drawroads()
	Color 100,50,0
	For y1=0 To mapheight
	For x1=0 To mapwidth
	For y2=-1 To 1
	For x2=-1 To 1
		x3 = x1+x2
		y3 = y1+y2
		If x3>=0 And y3>=0 And x3<=mapwidth And y3<=mapheight
			If roadmap(x1,y1) = True
			If roadmap(x3,y3) = True 
				Line ((x3*tilewidth)+tilewidth/2),((y3*tileheight)+tileheight/2),((x1*tilewidth)+tilewidth/2),((y1*tileheight)+tileheight/2)
				Line ((x3*tilewidth)+tilewidth/2)+1,((y3*tileheight)+tileheight/2),((x1*tilewidth)+tilewidth/2)+1,((y1*tileheight)+tileheight/2)				
			End If
			End If
		End If
	Next
	Next
	Next
	Next
End Function

Function drawcities()
	For this.city = Each city
		x = this\x*tilewidth
		y = this\y*tileheight
		Color 0,0,0
		Rect x,y,tilewidth,tileheight,True
		Color 255,255,255
		Rect x+1,y+1,tilewidth-2,tileheight-2,True
		Color 0,0,0
		Text x+tilewidth/2,y+tileheight/2,"C",True,True
	Next
End Function


Function makecities()
	For i = 0 To 4
		exitloop = False
		While exitloop = False
		x = Rand(mapwidth)
		y = Rand(mapheight)
		dist = 1000
		For this.city = Each city
			d = distance(this\x,this\y,x,y)
			If d<dist
				dist = d
			End If
		Next
		If dist > 5 
		If map(x,y) > 0
			exitloop = True
		End If
		End If
		Wend
		this.city = New city
		this\id = i
		this\x = x
		this\y = y
		this\size = Rand(4,9)
	Next
End Function


Function findpath()
	If sx = ex And sy = ey Then Return False
	Delete Each ol
	Delete Each cl
	Delete Each path
	For y=0 To mapheight
	For x=0 To mapwidth
		olmap(x,y) = False
		clmap(x,y) = False
	Next
	Next
	Local tx,ty,tf,tg,th,tpx,tpy,newx,newy,lowestf
	a.ol = New ol
	a\x = sx
	a\y = sy
	olmap(sx,sy) = True
	Repeat
		If openlistisempty() = True Then Return False
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
			c.cl = New cl
			c\x = tx
			c\y = ty
			c\px = tpx
			c\py = tpy
			findpathback()
			Return True
		Else
			removefromopenlist(tx,ty)
			olmap(tx,ty) = False
			d.cl = New cl
			d\x = tx
			d\y = ty
			d\px = tpx
			d\py = tpy
			clmap(tx,ty) = True
			
			For y=-1 To 1
			For x=-1 To 1
				newx = tx+x
				newy = ty+y
				If newx>=0 And newy>=0 And newx<=mapwidth And newy<=mapheight
				If clmap(newx,newy) = False
				If olmap(newx,newy) = False
				If map(newx,newy)>0
					clmap(newx,newy) = True
					e.ol = New ol
					e\x = newx
					e\y = newy
					If roadmap(newx,newy) = True 
						e\g = 1
					Else
						e\g = map(newx,newy) + 1
					End If
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

Function drawpath()
	For this.path = Each path
		Color 155,155,155
		Oval this\x*tilewidth,this\y*tileheight,tilewidth,tileheight,True
		Color 255,255,255
		Text this\x*tilewidth,this\y*tileheight,cnt
		cnt=cnt+1
	Next
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

Function distance(x1,y1,x2,y2)
	Return Abs(x2-x1)+Abs(y2-y1)
End Function

Function drawmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		Select map(x,y)
		Case 0
		Color 0,0,200
		Default
		Color 0,map(x,y)*8,0
		End Select
		Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
	Next
	Next
End Function

Function makemap()
	For y=0 To mapheight
	For x=0 To mapwidth
		map(x,y) = 0
	Next
	Next
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

Function makecitybrush()
	Restore citybrushdata
	For y=0 To 4
	For x=0 To 4
		Read a
		citybrush(x,y) = a
	Next
	Next
End Function

.citybrushdata
Data 0,1,1,1,0
Data 1,1,1,1,1
Data 1,1,1,1,1
Data 1,1,1,1,1
Data 0,1,1,1,0

