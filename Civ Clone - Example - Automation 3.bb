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
Type city
	Field x,y,id
End Type
Type unit	
	Field x,y,id,cityid
	Field px[1024]
	Field py[1024]
	Field loc,endloc
	Field sx,sy,ex,ey
	Field state$	
	Field substate$
	Field movesleft#
	Field waitturns
End Type
Type unavail
	Field x,y
End Type
Type take
	Field x,y
End Type
.start
Global endsim = False
Global layroads = False
Global clearforrest = False
Global buildirrigation = False
Dim roadmap(mapwidth,mapheight)
Dim irrigationmap(mapwidth,mapheight)
Dim impmap(mapwidth,mapheight,2)
turn=0
makemap
createcities(Rand(5,20))
createroadmap()
For y=0 To mapheight
For x=0 To mapwidth
	impmap(x,y,0) = roadmap(x,y)
	roadmap(x,y) = 0
Next
Next
createunits()

While KeyDown(1) = False
	Cls
	drawmap
	drawroads
	drawcities
	drawunits
	drawirrigation
	dounits
	turn=turn+1
	setturnsback
	Text 0,0,turn
	Flip:
	If turn > 200 Then Goto start
Wend
End

Function drawirrigation()
	Color 0,250,0
	For y=0 To mapheight
	For x=0 To mapwidth
		If irrigationmap(x,y) = 1 Then
			Rect x*tilewidth,y*tileheight,tilewidth,tileheight,False
		End If
	Next
	Next
End Function

Function setturnsback()
	For this.unit = Each unit
		this\movesleft = 1
	Next
End Function

Function dounits()
	While unitsmovesleft() = True
	For this.unit = Each unit
		Select this\state$
			Case "idle"
				done=False
				this\movesleft = 0
				If layroads = False Then
					this\state$ = "layroads"
					this\substate$ = "findroadtolay"
					done = True
				End If
				If done = False
					If clearforrest = False
						this\state$ = "removetrees"
						this\substate$ = "findtree"
						done = True
					End If
				End If
				If done=False
					If buildirrigation = False
						this\state$ = "buildirrigation"
						this\substate$ = "findirrigation"
						done = True
					End If
				End If				
			Case "buildirrigation"
				Select this\substate$
					Case "findirrigation"
						If setclosestirrigation(this) = False
							this\state$ = "movehome"
							this\substate$ = "planpath"
						Else
							sx = this\x
							sy = this\y
							this\sx = sx
							this\sy = sy
							this\ex = ex
							this\ey = ey
							findpath
							cnt = 0
							For that.path = Each path
								cnt=cnt+1
							Next
							this\endloc = cnt
							this\loc = 0
							that.path = Last path
							For i=1 To cnt
								this\px[i] = that\x
								this\py[i] = that\y
								that = Before that		
							Next			
							this\substate$ = "onroute"								
						End If
					Case "onroute"
						If this\movesleft > .3						
							If this\x = this\ex And this\y = this\ey
								this\substate$ = "birrigation"
								this\waitturns = 4
								this\movesleft = this\movesleft - 1	
							Else
								If roadmap(this\px[loc+1],this\py[loc+1]) = True Then
									this\movesleft = this\movesleft - .3
									Else
									this\movesleft = this\movesleft - 1
								End If					
								this\loc = this\loc + 1
								this\x = this\px[this\loc]
								this\y = this\py[this\loc]
							End If
						Else
							this\movesleft = 0
						End If
					Case "birrigation"
						this\waitturns = this\waitturns - 1
						this\movesleft = this\movesleft - 1
						If this\waitturns = 0 Then
						irrigationmap(this\x,this\y) = True
						this\state$ = "buildirrigation"
						this\substate="findirrigation"
						End If											
				End Select				
			Case "layroads"
				Select this\substate$
					Case "findroadtolay"
						If setpathclosestroadimp(this) = False Then
							this\state$ = "movehome"
							this\substate$ = "planpath"
							layroads = True
							Else
							sx = this\x
							sy = this\y
							this\sx = sx
							this\sy = sy
							this\ex = ex
							this\ey = ey
							findpath
							cnt = 0
							For that.path = Each path
								cnt=cnt+1
							Next				
							this\endloc = cnt
							this\loc = 0
							that.path = Last path
							For i=1 To cnt
								this\px[i] = that\x
								this\py[i] = that\y
								that = Before that		
							Next			
							this\substate$ = "onroute"							
						End If
					Case "onroute"
						If this\movesleft > .3						
							If this\x = this\ex And this\y = this\ey
								this\substate$ = "placeroad"
								this\waitturns = 4
								this\movesleft = this\movesleft - 1	
							Else
								If roadmap(this\px[loc+1],this\py[loc+1]) = True Then
									this\movesleft = this\movesleft - .3
									Else
									this\movesleft = this\movesleft - 1
								End If					
								this\loc = this\loc + 1
								this\x = this\px[this\loc]
								this\y = this\py[this\loc]
							End If
						Else
							this\movesleft = 0
						End If
					Case "placeroad"
						this\waitturns = this\waitturns - 1
						this\movesleft = this\movesleft - 1
						If this\waitturns = 0 Then
						roadmap(this\x,this\y) = True
						this\state$ = "layroads"
						this\substate="findroadtolay"
						End If
						
				End Select
			Case "movehome"
				Select this\substate$
					Case "planpath"
						sethomecitypath(this\cityid)
						sx = this\x
						sy = this\y
						this\sx = sx
						this\sy = sy
						this\ex = ex
						this\ey = ey
						findpath
						cnt = 0
						For that.path = Each path
							cnt=cnt+1
						Next
						this\endloc = cnt
						this\loc = 0
						that.path = Last path
						For i=1 To cnt
							this\px[i] = that\x
							this\py[i] = that\y
							that = Before that		
						Next			
						this\substate$ = "onroute"
					Case "onroute"
						If this\movesleft > .3						
							If this\x = this\ex And this\y = this\ey
								this\substate$ = "nothing"
								this\movesleft = this\movesleft - 1	
							Else
								If roadmap(this\px[loc+1],this\py[loc+1]) = True Then
									this\movesleft = this\movesleft - .3
									Else
									this\movesleft = this\movesleft - 1
								End If					
								this\loc = this\loc + 1
								this\x = this\px[this\loc]
								this\y = this\py[this\loc]
							End If
						Else
							this\movesleft = 0
						End If
					Case "nothing"
						this\state$ = "idle"
											
				End Select			
			Case "removetrees"
				Select this\substate$
					Case "findtree"
					If setclosesttree(this\cityid) = False 
						clearforrest = True
						this\state$ = "movehome"
						this\substate$ = "planpath"
						Else
						this\state$ = "removingtree"
						sx = this\x
						sy = this\y
						this\sx = sx
						this\sy = sy
						this\ex = ex
						this\ey = ey
						findpath
						cnt = 0
						For that.path = Each path
							cnt=cnt+1
						Next
						this\endloc = cnt
						this\loc = 0
						that.path = Last path
						For i=1 To cnt
							this\px[i] = that\x
							this\py[i] = that\y
							that = Before that		
						Next			
						;RuntimeError this\ex+","+this\ey+":"+this\px[this\endloc]+","+this\py[this\endloc]					
						this\substate$ = "onroute"
					End If
				End Select
			Case "removingtree"
				Select this\substate$
					Case "onroute"
						If this\movesleft > .3						
							If this\x = this\ex And this\y = this\ey
								this\substate$ = "removingtree"
								this\waitturns = 4
								this\movesleft = this\movesleft - 1	
							Else
								If roadmap(this\px[loc+1],this\py[loc+1]) = True Then
									this\movesleft = this\movesleft - .3
									Else
									this\movesleft = this\movesleft - 1
								End If					
								this\loc = this\loc + 1
								this\x = this\px[this\loc]
								this\y = this\py[this\loc]
							End If
						Else
							this\movesleft = 0
						End If
					Case "removingtree"
						this\waitturns = this\waitturns - 1
						this\movesleft = this\movesleft - 1
						If this\waitturns = 0 Then
						map(this\x,this\y) = 2
						this\state$ = "removetrees"
						this\substate="findtree"
						End If
				End Select
		End Select
	Next
	Wend
End Function

Function setclosestirrigation(this.unit)
	Delete Each unavail
	For that.unit = Each unit
		If that\state$ = "buildirrigation"
			thot.unavail = New unavail
			thot\x = that\ex
			thot\y = that\ey		
		End If
	Next
	lowest = 100000
	goahead = False
	For y=0 To mapheight
	For x=0 To mapwidth
		If map(x,y) = 2
		If Not irrigationmap(x,y) = True
		If onunavail(x,y) = False
			d = distance(x,y,this\x,this\y)
			If d<lowest Then
				lowest = d
				ex = x
				ey = y
				For a.city = Each city
					If this\cityid = a\id
						If distance(ex,ey,a\x,a\y) < 6 Then goahead = True
					End If
				Next
			End If
		End If
		End If
		End If
	Next
	Next
	If goahead = False Then Return False Else Return True
End Function

Function onunavail(x,y)
	For this.unavail = Each unavail
		If this\x = x And this\y = y Then Return True
	Next
	Return False
End Function

Function setpathclosestroadimp(this.unit)
	doit = False
	For y=0 To mapheight
	For x=0 To mapwidth
		If impmap(x,y,0) = True Then doit = True
	Next
	Next
	If doit = False Then Return False
	lowest = 100000
	For y=0 To mapheight
	For x=0 To mapwidth
		If impmap(x,y,0) = True
			d = distance(x,y,this\x,this\y)			
			If d<lowest
				ex = x
				ey = y
				sx = this\x
				sy = this\y		
				If findpath() = True Then lowest=d
			End If
		End If
	Next
	Next
	impmap(ex,ey,0) = 0
	Return True
End Function

Function sethomecitypath(cityid)
	For this.city = Each city
		If this\id = cityid Then
			ex = this\x
			ey = this\y
			Return
		End If
	Next
End Function

Function setclosesttree(cityid)
	Delete Each unavail
	For this.unit = Each unit
		If this\state$ = "removingtree"
			a.unavail = New unavail
			a\x = this\ex
			a\y = this\ey
		End If
	Next
	For that.city = Each city
		If that\id = cityid
			lowest = 100000
			foundtree = False
			For y=0 To mapheight
			For x=0 To mapwidth
				If map(x,y) = 3 Then
					foundtree = True
					notdo=False
					For b.unavail = Each unavail
						If b\x = x And b\y = y
							notdo = True
						End If
					Next
					If notdo = False
						d = distance(x,y,that\x,that\y)
						If d<lowest
							lowest=d							
							ex = x
							ey = y
						End If
					End If
				End If
			Next
			Next
		End If
	Next
	If lowest>6 Then foundtree = False
	If foundtree = False Then Return False Else Return True
End Function

Function unitsmovesleft()
	For this.unit = Each unit
		If this\movesleft > 0 Then Return True
	Next
	Return False
End Function

Function drawunits()
	Color 50,50,50
	For this.unit = Each unit
		Rect this\x*tilewidth,this\y*tileheight,tilewidth,tileheight,True
	Next
End Function

Function createunits()
	Delete Each unit
	For this.city = Each city
		For i=1 To 3
			that.unit = New unit
			that\id = num
			that\cityid = this\id
			that\x = this\x
			that\y = this\y
			that\state$ = "idle"
			that\substate$ = ""
			that\movesleft# = 1
			that\sx = that\x
			that\sy = that\y
			num=num+1
		Next
	Next
End Function

Function drawroads()
	Color 100,50,0
	For y=0 To mapheight
	For x=0 To mapwidth
	For y1=-1 To 1
	For x1=-1 To 1
		If x+x1=>0 And y+y1=>0 And x+x1=<mapwidth And y+y1=<mapheight		
		If roadmap(x,y) = True And roadmap(x+x1,y+y1) = True
			Line (x+x1)*tilewidth+8,(y+y1)*tileheight+8,x*tilewidth+8,y*tileheight+8
			Line (x+x1)*tilewidth+9,(y+y1)*tileheight+8,x*tilewidth+9,y*tileheight+8
		End If
		End If
	Next
	Next
	Next
	Next
End Function


Function createroadmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		roadmap(x,y) = False
	Next
	Next
	For this.city = Each city
	For that.city = Each city
		If this\x <> that\x And this\y <> that\y
		sx = this\x
		sy = this\y
		ex = that\x
		ey = that\y
		findpath
		For a.path = Each path
			roadmap(a\x,a\y) = True
		Next
		End If
	Next
	Next
End Function

Function createcities(num)
	Delete Each city
	For i=1 To num
		exitloop = False
		domistake = False
		While exitloop = False
			If KeyDown(1) = True Then End
			x = Rand(mapwidth)
			y = Rand(mapheight)
			If map(x,y) > 1 And (Not map(x,y) = 4)
				dist = 100000
				For that.city = Each city
					d2 = distance(x,y,that\x,that\y)
					If d2 < dist Then dist = d2
				Next
				If dist > 3 Then
					For that.city = Each city
						If that\x = x And that\y = y Then domistake = True
					Next
					If domistake = False Then
						exitloop = True
						this.city = New city
						this\x = x
						this\y = y
						this\id = i
					End If
				End If
			End If
		Wend
	Next
End Function

Function drawcities()
	Color 255,255,255
	For this.city = Each city
		Rect this\x*tilewidth,this\y*tileheight,tilewidth,tileheight,True
	Next
End Function

Function findpath()
	If sx = ex And sy = ey Then Return
	Delete Each ol
	Delete Each cl
	Delete Each path
	Local tx,ty,tf,tg,th,tpx,tpy,newx,newy,lowestf,exitloop
	a.ol = New ol
	a\x = sx
	a\y = sy
	While exitloop = False
		If openlistisempty() = True Then exitloop = True : pathnotfound = True
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
					If roadmap(newx,newy) = True Then
					e\g = tg 
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
	Wend
	If pathnotfound = True Then Return False
	Return True
End Function

Function findpathback()
	Local exitloop = False
	Local x = ex
	Local y = ey
	that.path = New path
	that\x = ex
	that\y = ey
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

Function distance(x1,y1,x2,y2)
	Return Abs(x2-x1)+Abs(y2-y1)
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

Function isonclosedlist(x,y)
	For this.cl = Each cl
		If this\x = x And this\y = y Then Return True
	Next
End Function

Function isonopenlist(x,y)
	For this.ol = Each ol
		If this\x = x And this\y = y Then Return True
	Next
End Function

Function drawpath()
	Local cnt=0
	For this.path = Each path
		cnt=cnt+1
	Next
	If cnt = 0 Then Return
	Color 255,255,255
	this.path = First path
	x1 = this\x
	y1 = this\y
	For that.path = Each path
		Line that\x*tilewidth+8,that\y*tileheight+8,x1*tilewidth+8,y1*tileheight+8
		x1 = that\x
		y1 = that\y
	Next
End Function

Function setcoordinates()
	Local exitloop = False
	While exitloop = False
		sx = Rand(mapwidth)
		sy = Rand(mapheight)
		ex = Rand(mapwidth)
		ey = Rand(mapheight)
		If sx<>ex And sy<>ey
		If map(sx,sy) > 1 And map(ex,ey) > 1
			exitloop = True
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
	Next
	Next
;	Color 30,255,20
;	Oval sx*tilewidth,sy*tileheight,tilewidth,tileheight,True
;	Color 255,20,10
;	Oval ex*tilewidth,ey*tileheight,tilewidth,tileheight,True
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
			If x+x1=>0 And y+y1=>0 And x+x1=<mapwidth And y+y1=<mapheight
				map(x+x1,y+y1) = map(x+x1,y+y1) + 1
			End If
		Next
		Next
		For y1=-2 To 2
		For x1=-2 To 2
			If x+x1=>0 And y+y1=>0 And x+x1=<mapwidth And y+y1=<mapheight
				map(x+x1,y+y1) = map(x+x1,y+y1) + 1
			End If
		Next
		Next
		For y1=-1 To 1
		For x1=-1 To 1
			If x+x1=>0 And y+y1=>0 And x+x1=<mapwidth And y+y1=<mapheight
				map(x+x1,y+y1) = map(x+x1,y+y1) + 1
			End If
		Next
		Next
	Next
	For y=0 To mapheight
	For x=0 To mapwidth
		If map(x,y) < 3 Then map(x,y) = 1
		If map(x,y) => 3 And map(x,y) < 5 Then map(x,y) = 2
		If map(x,y) => 5 And map(x,y) < 8 Then map(x,y) = 3
		If map(x,y) => 8 Then map(x,y) = 4
	Next
	Next
End Function
