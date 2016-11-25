Graphics 640,480,32,2
SetBuffer BackBuffer()
Global myfont = LoadFont("verdana.ttf",10)
SetFont myfont
SeedRnd 3
Global turn = 0
Global sx,sy,ex,ey
Const tilewidth = 16
Const tileheight = 16
Const mapwidth = 39
Const mapheight = 29
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
	Field id,x,y,state$,waitturns
End Type
Type unit
	Field id,x,y,state$,substate$,ismilunit,isseaunit,issettlerunit
	Field sx,sy,ex,ey,ploc,movesleft
	Field boardx,boardy,unboardx,unboardy
	Field pickupx,pickupy,dropoffx,dropoffy
	Field px[1024]
	Field py[1024]
End Type
Dim zonebrush(4,4)
makezonebrush()
Dim islandmap(mapwidth,mapheight)

makemap()
makeislandmap()
findstartingposition
While KeyDown(1) = False
	Cls
	docities
	dounits()
	drawmap()
	drawislandmap()
	drawcityzones()
	drawcities()
	drawunits()
	Flip
	For n.unit = Each unit
		n\movesleft = 1
	Next
	turn = turn + 1
	Delay 50
Wend
WaitKey
End

Function docities()
	For this.city = Each city
		Select this\state$
			Case "buildsettler"
				this\waitturns = this\waitturns - 1
				If this\waitturns < 1
					that.unit = New unit
					that\id = newunitid()
					that\x = this\x
					that\y = this\y
					that\sx = this\x
					that\sy = this\y
					that\issettlerunit = True
					that\movesleft = 1
					that\state$ = "movetobuildcity"
					If findnextcityplace(that) = True
						sx = that\sx
						sy = that\sy
						ex = that\ex
						ey = that\ey
						;DebugLog sx+","+sy+";"+ex+","+ey
						If findpath() = True
							cnt=0
							For p.path = Each path
								that\px[cnt] = p\x
								that\py[cnt] = p\y
								cnt=cnt+1
							Next
							that\ploc = 0
						Else
							
							RuntimeError "error in docities function"						 
						End If
						this\waitturns = 10
					Else
						this\state$ = "nothing"
						Delete that
					End If
				End If
			Case "nothing"				
		End Select
	Next
End Function

Function dounits()
	For this.unit = Each unit
		If this\movesleft > 0
			Select this\state$
				Case "buildcity"
					nc.city = New city
					nc\x = this\x
					nc\y = this\y
					nc\id = newcityid()
					nc\state$ = "buildsettler"
					nc\waitturns = 10
					Delete this
				Case "movetobuildcity"
					this\ploc = this\ploc + 1
					this\x = this\px[this\ploc]
					this\y = this\py[this\ploc]
					this\movesleft = this\movesleft - 1
					If this\x = this\ex And this\y = this\ey
						this\state$ = "buildcity"
					End If
			End Select
		End If
	Next
End Function

Function findnextcityplace(u.unit)
	cnt = 0
	For that.unit = Each unit
		If that\issettlerunit = True Then cnt=cnt+1
	Next
	If cnt > 10 Then Return False
	cnt = 0
	ant = 5
	While exitloop = False
		cnt=cnt+1
		If cnt>1000 Then Return False
		ant = ant + 1
		exitloop2 = False
		While exitloop2 = False
			x = u\x + Rand(ant)-ant/2
			y = u\y + Rand(ant)-ant/2
			If RectsOverlap(x,y,1,1,0,0,mapwidth,mapheight) = True Then exitloop2 = True
		Wend
		If map(x,y) > 0
			dist = 10000
			For this.city = Each city				
				d = distance(this\x,this\y,x,y)
				If d < dist Then dist = d
			Next
			If dist > 5
				dist = 10000
				For that.unit = Each unit
					If that\state$="movetobuildcity"
						d = distance(that\ex,that\ey,x,y)
						If d<dist Then dist = d
					End If
				Next
			End If
			If dist > 5
				exitloop = True
			End If			
		End If
	Wend
	u\ex = x
	u\ey = y
	Return True
End Function

Function makeislandmap()
	Dim olmap(mapwidth,mapheight)
	Dim clmap(mapwidth,mapheight)
	Delete Each ol
	Delete Each cl
	For y=0 To mapheight
	For x=0 To mapwidth
	If map(x,y) > 0 And islandmap(x,y) = 0
	cnt=cnt+1
	this.ol = New ol
	islandmap(x,y) = cnt
	this\x = x
	this\y = y
	olmap(x,y) = True
	While openlistisempty() = False
		this.ol = First ol
		tx = this\x
		ty = this\y
		removefromopenlist(this\x,this\y)
		olmap(tx,ty) = False
		clmap(tx,ty) = True
		that.cl = New cl
		that\x = tx
		that\y = ty
		For y1=-1 To 1
		For x1=-1 To 1
			newx =tx+x1
			newy =ty+y1
			If newx>=0 And newy>=0 And newx<=mapwidth And newy<=mapheight
				If olmap(newx,newy) = False
				If clmap(newx,newy) = False
				If map(newx,newy) > 0
				If islandmap(newx,newy) = 0
					olmap(newx,newy) = True
					this.ol = New ol
					this\x = newx
					this\y = newy
					islandmap(newx,newy) = cnt
				End If
				End If
				End If
				End If
			End If
		Next
		Next
	Wend
	End If
	Next
	Next
End Function

Function drawislandmap()
	Color 255,255,255
	For y=0 To mapheight
	For x=0 To mapwidth
		Text x*tilewidth,y*tileheight,islandmap(x,y)
	Next
	Next
End Function

Function newunitid()
	While exitloop = False
		exitloop = True
		For this.unit = Each unit
			If this\id = cnt Then exitloop = False
		Next
		cnt=cnt+1
	Wend
	Return cnt
End Function

Function newcityid()
	While exitloop = False
		exitloop = True
		For this.city = Each city
			If this\id = cnt Then exitloop = False
		Next
		cnt=cnt+1
	Wend
	Return cnt
End Function

Function drawcityzones()
	Color 200,200,200
	For this.city = Each city
		For y=0 To 4
		For x=0 To 4
			If zonebrush(x,y) = 1
				x1 = x-2+this\x
				y1 = y-2+this\y
				Oval x1*tilewidth,y1*tileheight,tilewidth,tileheight,False
			End If
		Next
		Next
	Next
End Function

Function findstartingposition()
	this.unit = New unit
	While exitloop = False
		x = Rand(mapwidth)
		y = Rand(mapheight)
		If map(x,y) > 0 Then exitloop = True
	Wend
	this\id = 0
	this\x = x
	this\y = y
	this\issettlerunit = True
	this\state$ = "buildcity"
End Function

Function drawunits()
	For this.unit = Each unit
		x = this\x*tilewidth
		y = this\y*tileheight
		Color 200,200,200
		Rect x,y,tilewidth,tileheight,True
		Color 0,0,0
		If this\isseaunit = True
			Text x+tilewidth/2,y+tileheight/2,"N",True,True
		End If
		If this\issettlerunit = True
			Text x+tilewidth/2,y+tileheight/2,"S",True,True
		End If
		If this\ismilunit = True
			Text x+tilewidth/2,y+tileheight/2,"M",True,True
		End If
		Rect x,y,tilewidth,tileheight,False
		Rect x+1,y+1,tilewidth-2,tileheight-2,False
	Next
End Function

Function drawcities()
	For this.city = Each city
		x = this\x*tilewidth
		y = this\y*tileheight
		Color 255,255,255
		Rect x,y,tilewidth,tileheight,True
		Color 0,0,0
		Rect x,y,tilewidth,tileheight,False
	Next
End Function

Function findpath()
	If sx = ex And sy=ey Then Return False
	Delete Each cl
	Delete Each ol
	Delete Each path
	Dim olmap(mapwidth,mapheight)
	Dim clmap(mapwidth,mapheight)
	Local tx,ty,tf,tg,th,tpx,tpy,newx,newy,lowestf
	olmap(sx,sy) = True
	this.ol = New ol
	this\x = sx
	this\y = sy
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
			clmap(tx,ty) = True
			olmap(tx,ty) = False
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
		Color 0,map(x,y)*16,0
		If map(x,y) = 0 Then Color 0,0,200		
		Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
	Next
	Next
End Function

Function makemap()
	Dim map(mapwidth,mapheight)
	For i=0 To 15
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

Function makezonebrush()
	Restore zonedata
	For y=0 To 4
	For x=0 To 4
		Read a
		zonebrush(x,y) = a
	Next
	Next
End Function

.zonedata
Data 0,0,1,0,0
Data 0,1,1,1,0
Data 1,1,1,1,1
Data 0,1,1,1,0
Data 0,0,1,0,0

