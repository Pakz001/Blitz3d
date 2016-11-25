Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd 2
Global turn = 0
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
	Field islandunit
	Field isseaunit
	Field moves
	Field visible
	Field id,x,y,sx,sy,ex,ey,state$,nextstate$
	Field pickupx,pickupy
	Field dropoffx,dropoffy
	Field boardx,boardy
	Field unboardx,unboardy
	Field cargoid[10]
	Field px[1024]
	Field py[1024]
	Field ploc ; path location
End Type
Global myfont = LoadFont("verdana.ttf",10)
SetFont myfont

this.unit = New unit
this\visible = True
this\id = 1
this\state$ = "movetopickup"
this\x = 10
this\y = 10
this\sx = 10
this\sy = 10
this\ex = 22
this\ey = 22
this\islandunit = True
this.unit = New unit
this\visible= True
this\id = 2
this\state$ = "movetopickup"
this\x = 22
this\y = 14
this\isseaunit = True

makemap()
setislandmap()
makepath()
While KeyDown(1) = False
	Cls
	dounits()
	drawmap()
	drawislandmap()
	drawunits()
	drawlandunitpath()
	drawpickuppoint()
	drawdropoffpoint()
	drawboardingpoints()
	turn = turn + 1
	For t.unit = Each unit
		t\moves = 1
	Next
	Flip
Wend
End

Function drawboardingpoints()
	For this.unit = Each unit
		If this\id = 1 Then
			Color 255,255,0
			Rect this\boardx*tilewidth,this\boardy*tileheight,tilewidth,tileheight,False
			Rect this\unboardx*tilewidth,this\unboardy*tileheight,tilewidth,tileheight,False
		End If
	Next
End Function

Function dounits()
	For this.unit = Each unit
		While this\moves > 0
			Select this\state$
				Case "movetopickup"				
				this\ploc = this\ploc + 1
				this\x = this\px[this\ploc]
				this\y = this\py[this\ploc]
				this\moves = this\moves - 1
				If this\islandunit = True				
				If this\x = this\pickupx And this\y = this\pickupy
					this\state$ = "boardtransport"
				End If				
				Else
				If this\x = this\boardx And this\y = this\boardy
					this\state$ = "wait"
				End If				

				End If
				Case "boardtransport"
					If transport(this) = True Then					
						this\visible = False
						this\ploc = 0
						this\state$ = "onboard"
					Else
						this\moves = 0
					End If
				Case "onboard"
					this\moves=0
				Case "movetodropoff"
				this\ploc = this\ploc + 1
				this\x = this\px[this\ploc]
				this\y = this\py[this\ploc]				
				this\moves = this\moves - 1
				If this\x = this\unboardx And this\y = this\unboardy
					this\state$ = "unboardtransport"
				End If
				Case "unboardtransport"
					this\moves=0
					unloadunit(this\cargoid[0])
					this\state$="wait"
				Case "wait"
				this\moves = 0
				Case "move"
				this\ploc = this\ploc + 1
				this\x = this\px[this\ploc]
				this\y = this\py[this\ploc]
				this\moves = this\moves-1
				If this\x = this\ex And this\y = this\ey
					this\state$ = "wait"
				End If
			End Select
		Wend
	Next
	
End Function

Function unloadunit(id)
	For this.unit = Each unit
		If this\id = id
			this\x = this\dropoffx
			this\y = this\dropoffy
			this\visible = True
			this\moves = 0
			this\sx = this\x
			this\sy = this\y
			sx = this\sx
			sy = this\sy
			ex = this\ex
			ey = this\ey
			findpath()
			cnt=0
			For p.path = Each path
				this\px[cnt] = p\x
				this\py[cnt] = p\y
				cnt=cnt+1
			Next
			this\ploc = 0
			this\state$ = "move"
		End If
	Next
End Function

Function transport(this.unit)
	For that.unit = Each unit
		If that\id = 2
			If that\x = this\boardx And that\y = this\boardy
				that\sx = this\boardx
				that\sy = this\boardy
				that\ex = this\unboardx
				that\ey = this\unboardy
				that\unboardx = this\unboardx
				that\unboardy = this\unboardy
				that\cargoid[0] = 1
				sx = that\sx
				sy = that\sy
				ex = that\ex
				ey = that\ey
				findpath()
				cnt=0
				For p.path = Each path
					that\px[cnt] = p\x
					that\py[cnt] = p\y
					cnt=cnt+1
				Next
				that\ploc = 0
				that\state$ = "movetodropoff"
				Return True
			End If
		End If
	Next
	Return False
End Function

Function makepath()	
	this.unit = First unit
	sx = this\sx
	sy = this\sy
	ex = this\ex
	ey = this\ey
	findpath()
	For that.path = Each path
		this\px[cnt] = that\x
		this\py[cnt] = that\y
		cnt=cnt+1
	Next
	this\ploc = 0
	setpickup(this)
	setdropoff(this)
	settransport(this)
End Function

Function settransport(this.unit)
	For that.unit = Each unit
		If that\id = 2
			that\sx = that\x
			that\sy = that\y
			that\ex = this\boardx
			that\ey = this\boardy
			sx = that\sx
			sy = that\sy
			ex = that\ex
			ey = that\ey
			findpath
			For p.path = Each path
				that\px[cnt] = p\x
				that\py[cnt] = p\y
				cnt=cnt+1
			Next
			that\ploc = 0
		End If
	Next
End Function

Function drawdropoffpoint()
	this.unit = First unit
	Color 255,0,0
	Rect this\dropoffx*tilewidth,this\dropoffy*tileheight,tilewidth,tileheight,False
End Function

Function setdropoff(this.unit)
	While exitloop = False
		x1 = this\px[cnt]
		y1 = this\py[cnt]
		x2 = this\px[cnt+1]
		y2 = this\py[cnt+1]
		If map(x1,y1) = 0 And map(x2,y2) > 0
			this\dropoffx = x2
			this\dropoffy = y2
			this\unboardx = x1
			this\unboardy = y1
			Return
		End If
		cnt=cnt+1
	Wend
End Function

Function drawpickuppoint()
	this.unit = First unit
	Color 255,255,0
	Rect this\pickupx*tilewidth+4,this\pickupy*tileheight+4,tilewidth-8,tileheight-8,False
End Function

Function setpickup(this.unit)
	While exitloop = False
		x1 = this\px[cnt]
		y1 = this\py[cnt]
		x2 = this\px[cnt+1]
		y2 = this\py[cnt+1]
		If map(x1,y1) > 0 And map(x2,y2) = 0
			this\pickupx = x1
			this\pickupy = y1
			this\boardx = x2
			this\boardy = y2
			Return
		End If
		cnt=cnt+1
	Wend
End Function

Function drawlandunitpath()
	this.unit = First unit
	Color 255,0,0
	While exitloop = False
		x = this\px[cnt]
		y = this\py[cnt]
		cnt=cnt+1
		Oval x*tilewidth+3,y*tileheight+3,tilewidth-6,tileheight-6,True
		If x = this\ex And y = this\ey Then exitloop = True
	Wend
End Function


Function drawunits()
	For this.unit = Each unit
		If this\visible = True Then
		If this\islandunit = True Then Color 255,255,255
		If this\isseaunit = True Then Color 255,0,0
		Rect this\x*tilewidth,this\y*tileheight,tilewidth,tileheight,True
		End If
	Next
End Function

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
			Wend
		End If
	Next
	Next
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
	For i=0 To 10
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

Function distance(x1,y1,x2,y2)
	Return Abs(x2-x1)+Abs(y2-y1)
End Function
