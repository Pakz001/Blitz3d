Graphics 640,480,32,2
SetBuffer BackBuffer()
Global turn = 0
SeedRnd 2
Global sx,sy,ex,ey
Const mapwidth = 39
Const mapheight = 29
Const tilewidth = 16
Const tileheight = 16
Dim map(mapwidth,mapheight)
Dim olmap(mapwidth,mapheight)
Dim clmap(mapwidth,mapheight)
Type path
	Field x,y	
End Type
Type ol 
	Field x,y,f,g,h,px,py
End Type
Type cl
	Field x,y,f,g,h,px,py
End Type
Dim zocmap(mapwidth,mapheight)
Dim zocbrush(4,4)
makezocbrush
Dim linemap(mapwidth,mapheight)
Type unit
	Field id,x,y,p,sx,sy,ex,ey,moves
	Field state$,ploc
	Field px[1024]
	Field py[1024]
End Type

Type city
	Field id,x,y,p,unitbuild
End Type

Type bline
	Field x,y
	Field r ; reachable
	Field s ; stacked count
End Type

Cls
makemap()
makeunits()
makecities()
makezocmap()
makebline()

While KeyDown(1) = False
	Cls
	docities()
	dounits()
	drawmap()
	drawcities()
	drawunits()
	drawzocmap()
	drawbline()
	Color 255,255,255
	Text 0,0,turn
	Flip
	For nt.unit = Each unit
		nt\moves = 1
	Next
	turn = turn + 1
	Delay 500
Wend
End


Function dounits()
	For this.unit = Each unit
		If this\p = 2
			While this\moves > 0
				Select this\state$
					Case "move"
					this\ploc = this\ploc + 1				
					this\x = this\px[this\ploc]
					this\y = this\py[this\ploc]
					this\moves = this\moves - 1
					If this\x = this\ex And this\y = this\ey
						this\state$="fortify"
					End If
					Case "fortify"
					this\moves =0			
				End Select			
			Wend
		End If
	Next
End Function

Function reinforce()
	For this.unit = Each unit
		If this\p = 1 Then cnt1 = cnt1 + 1
		If this\p = 2 Then cnt2 = cnt2 + 1
	Next
	If cnt2 < cnt1 Then Return True
End Function

Function docities()
	For this.city = Each city
		If this\p = 2 And reinforce() = True
			this\unitbuild = this\unitbuild - 1
			If this\unitbuild < 1
				this\unitbuild = 4
				that.unit = New unit
				that\id = newunitid()
				that\p = 2
				that\x = this\x
				that\y = this\y
				that\sx = this\x
				that\sy = this\y
				that\moves = 1
				sc = 100
				x=-1
				y=-1
				For b.bline = Each bline
					If b\r = True
					If b\s < sc
						sc = b\s
						x = b\x
						y = b\y
					End If
					End If
				Next
				that\ex = x
				that\ey = y
				sx = that\sx
				sy = that\sy
				ex = that\ex
				ey = that\ey
	;			DebugLog sx+","+sy+":"+ex+","+ey
				If findpath() = False Then RuntimeError "error in docities function"
				cnt=0
				For p.path = Each path
					that\px[cnt] = p\x
					that\py[cnt] = p\y
					cnt=cnt+1
				Next
				For bl.bline = Each bline
					If bl\x = ex
					If bl\y = ey
						bl\s = bl\s+1
					End If
					End If
				Next
				that\ploc = 0
				that\state$ = "move"
			End If	
		End If
	Next
End Function

Function newunitid()
	cnt = 0
	While exitloop = False
	cnt=cnt+1
	exitloop = True
	For this.unit = Each unit
		If this\id = cnt Then exitloop = False	
	Next
	Wend
	Return cnt	
End Function

Function makebline()
	Delete Each bline
	For y=0 To mapheight
	For x=0 To mapwidth
		x1 = x+1
		y1 = y
		If x1=>0 And y1=>0 And x1<=mapwidth And y1<=mapheight
			If zocmap(x,y) = 0
			If zocmap(x1,y1) = 1
			If map(x,y) > 0
			If map(x1,y1) > 0
			this.bline = New bline
			this\x = x1
			this\y = y1
			End If
			End If
			End If
			End If
		End If
		x1 = x-1
		y1 = y
		If x1=>0 And y1=>0 And x1<=mapwidth And y1<=mapheight
			If zocmap(x,y) = 0
			If zocmap(x1,y1) = 1
			If map(x,y) > 0
			If map(x1,y1) > 0
			this.bline = New bline
			this\x = x1
			this\y = y1
			End If
			End If
			End If
			End If
		End If
		x1 = x
		y1 = y-1
		If x1=>0 And y1=>0 And x1<=mapwidth And y1<=mapheight
			If zocmap(x,y) = 0
			If zocmap(x1,y1) = 1
			If map(x,y) > 0
			If map(x1,y1) > 0
			this.bline = New bline
			this\x = x1
			this\y = y1
			End If
			End If
			End If
			End If
		End If
		x1 = x
		y1 = y+1
		If x1=>0 And y1=>0 And x1<=mapwidth And y1<=mapheight
			If zocmap(x,y) = 0
			If zocmap(x1,y1) = 1
			If map(x,y) > 0
			If map(x1,y1) > 0
			this.bline = New bline
			this\x = x1
			this\y = y1
			End If
			End If
			End If
			End If
		End If
		
	Next
	Next	
	For that.city = Each city
	For this.bline = Each bline
		sx = that\x
		sy = that\y
		ex = this\x
		ey = this\y
		If findpath() = True Then this\r = True
	Next
	Next
End Function

Function drawbline()
	For this.bline = Each bline
		Color 200,200,0
		Oval this\x*tilewidth+3,this\y*tileheight+3,tilewidth-6,tileheight-6,False
		Color 255,255,255
		Text this\x*tilewidth+tilewidth/2,this\y*tileheight+tileheight/2,"X",True,True
	Next
End Function

Function makezocmap()
	For this.unit = Each unit
		If this\p = 1
			x1 = this\x
			y1 = this\y
			For y2=0 To 4
			For x2=0 To 4
				If zocbrush(x2,y2) = 1
					zocmap((x1+x2)-2,(y1+y2)-2) = 1
				End If
			Next
			Next
		End If
	Next
End Function

Function drawzocmap()
	Color 0,0,255
	For y=0 To mapheight
	For x=0 To mapwidth
		If zocmap(x,y) = 1
			Oval x*tilewidth,y*tileheight,tilewidth,tileheight,False
		End If
	Next
	Next
End Function

Function makezocbrush()
	Restore zocbrushdata
	For y=0 To 4
	For x=0 To 4
		Read a
		zocbrush(x,y) = a
	Next
	Next
End Function

Function drawcities()
	For this.city = Each city
		Select this\p
			Case 2
			Color 255,255,255
			Rect this\x*tilewidth,this\y*tileheight,tilewidth,tileheight,True
			Color 0,0,0
			Rect this\x*tilewidth,this\y*tileheight,tilewidth,tileheight,False
		End Select
	Next
End Function

Function makecities()
	For i=0 To 2
		cnt = cnt + 1
		this.city = New city
		this\id = cnt
		this\p = 2
		x1 = 5
		y1 = 20	
		this\x = x1+Rand(-5,5)
		this\y = y1+Rand(-5,5)
	Next
End Function

Function makeunits()
	For i=0 To 25
		cnt=cnt+1
		this.unit = New unit
		this\id = cnt
		this\p = 1
		x1 = 10
		y1 = 10
		this\x = x1+Rand(-5,5)
		this\y = y1+Rand(-5,5)
	Next
End Function

Function drawunits()
	For this.unit = Each unit
		Select this\p
			Case 1:Color 200,0,0
			Case 2:Color 200,200,200
		End Select
		Rect this\x*tilewidth,this\y*tileheight,tilewidth,tileheight,True
	Next
End Function

Function drawmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		Color 0,0,200
		If map(x,y) > 0 Then Color 0,map(x,y)*16,0
		Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True		
	Next
	Next
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
		If x3=>0 And y3=>0 And x3<=mapwidth And y3<=mapheight
		map(x3,y3)=map(x3,y3)+1
		End If
		Next
		Next
		For y2=-2 To 2
		For x2=-2 To 2
		x3 = x1+x2
		y3 = y1+y2
		If x3=>0 And y3=>0 And x3<=mapwidth And y3<=mapheight
		map(x3,y3)=map(x3,y3)+1
		End If
		Next
		Next
		For y2=-1 To 1
		For x2=-1 To 1
		x3 = x1+x2
		y3 = y1+y2
		If x3=>0 And y3=>0 And x3<=mapwidth And y3<=mapheight
		map(x3,y3)=map(x3,y3)+1
		End If
		Next
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
				If map(newx,newy) > 0
				If olmap(newx,newy) = False
				If clmap(newx,newy) = False
				cont = True
				If zocmap(newx,newy) = True
					cont = False
					For t.bline = Each bline
						If newx = t\x And newy=t\y
							cont = True
						End If
					Next
				End If
				If cont = True
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


.zocbrushdata
Data 0,1,1,1,0
Data 1,1,1,1,1
Data 1,1,1,1,1
Data 1,1,1,1,1
Data 0,1,1,1,0

