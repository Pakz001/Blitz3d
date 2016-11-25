Global turn = 1
Global debugmode = True
Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd 8
Global myfont = LoadFont("verdana.ttf",12)
SetFont myfont
Const mapwidth = 39
Const mapheight = 29
Const tilewidth = 16
Const tileheight = 16
Dim map(mapwidth,mapheight)
Global sx,sy,ex,ey
Type ol
	Field x,y,f,g,h,px,py
End Type
Type cl
	Field x,y,f,g,h,px,py
End Type
Type path
	Field x,y
End Type
Global treeimage = CreateImage(16,16)
Global hillimage = CreateImage(16,16)
Global mountainimage = CreateImage(16,16)
Global irrigationimage = CreateImage(16,16)
Type city
	Field id
	Field x,y,size
End Type
Dim roadmap(mapwidth,mapheight)
Dim irrigationmap(mapwidth,mapheight)
Type cityroadimp
	Field x,y
End Type
Type groadimp
	Field x,y
End Type
Type girriimp
	Field x,y
End Type
Type Gforrestimp
	Field x,y
End Type
Type gmineimp
	Field x,y
End Type 
Type cityirriimp
	Field x,y
End Type
Type cityfield
	Field x,y
End Type
Dim islandmap(mapwidth,mapheight)
Type unit
	Field x,y,id,cityid
	Field homecityx,homecityy
	Field movesleft#,waitturns
	Field px[1024]
	Field py[1024]
	Field state$
	Field substate$
	Field job$
	Field ploc
	Field sx,sy,ex,ey
End Type

makecityfield
makeimages()
makemap()
makeislandmap
createcities(Rand(5,10))
createunits()
;this.unit = New unit
;this\x = 20
;this\y = 20
;this\state$ = "automate"
makeroads()
makecityirriimp()
makeglobalimps()
While KeyDown(1) = False
	Cls
	drawmap(2)
	drawroadmap()
	drawirrigationmap()
	drawunits()	
	drawcities()
	dounits()
	For this.unit = Each unit
		this\movesleft = 1
	Next
	Color 0,0,0
	Rect 0,0,100,15,True
	Color 255,255,255
	Text 0,0,"Turn:"+turn
	turn = turn+1
	Flip
Wend
End

Function dounits()
	For this.unit = Each unit
		While this\movesleft > 0
			Select this\state$
				Case "automate"
					;find job
					job = False
					If roadsconnectingcitiesleft(this) = True
						this\state$ = "connectcitieswithroads"
						job = True
					End If
					If job = False
						If improvmentsnearcity(this) = True
							this\state$ = "improvearoundcities"
							job = True
						End If
					End If
					If job=False
						If improvmentsleft(this) = True 
							this\state$ = "improve"
							job = True
						End If
					End If
					If job = False
						If unitisinhomecity(this) = False
							this\state$ = "gobacktohomecity"
							job = True
						End If
					End If
					If job = False Then this\movesleft = 0
				Case "improve"
					job = False
					If job = False
						shortest = 100000
						For qw.groadimp = Each groadimp
							If islandmap(this\x,this\y) = islandmap(qw\x,qw\y)
								d = distance(qw\x,qw\y,this\x,this\y)
								If d < shortest						
									shortest = d				
									job = True
									this\job$ = "layroad"
									ex = qw\x
									ey = qw\y
								End If
							End If
						Next
					End If
					If job = True Then
						DebugLog MilliSecs()
						If this\x = ex And this\y = ey Then
							this\state$ = this\job$
							this\ex = ex
							this\ey = ey
							this\sx = this\x
							this\sy = this\y
							this\waitturns = 4
							removegroadimp(ex,ey)
							Else
							sx = this\x
							sy = this\y
							If findpath() = True
								removegroadimp(ex,ey)
								this\sx = sx
								this\sy = sy
								this\ex = ex
								this\ey = ey
								cnt = 0
								For ig.path = Each path
									cnt=cnt+1
								Next
								cnt=cnt-1
								ig.path = Last path
								For i=0 To cnt
									this\px[i] = ig\x
									this\py[i] = ig\y
									ig = Before ig
								Next
								this\ploc = 0
								this\state$ = "move"
							Else
								this\state$="automate"
							End If
						End If
					End If
					If job = False 
						this\state$ = "automate"
					End If
				Case "improvearoundcities"
					shortest = 100000
					For iac.cityirriimp = Each cityirriimp
						d = distance(this\x,this\y,iac\x,iac\y)
						If d < shortest
							shortest = d
							ex = iac\x
							ey = iac\y
						End If
					Next
					If ex = this\x And ey = this\y
						this\sx = this\x
						this\sy = this\y
						this\ex = ex
						this\ey = ey
						this\state$ = "layirrigation"
						this\waitturns = "4"
						removeirrifromlist(ex,ey)
					Else
						removeirrifromlist(ex,ey)
						sx = this\x
						sy = this\y
						If findpath() = True Then
							this\sx = this\x
							this\sy = this\y
							this\ex = ex
							this\ey = ey
							cnt = 0
							For iac2.path = Each path
								cnt=cnt+1
							Next		
							cnt=cnt-1
							iac2.path = Last path
							For i=0 To cnt
								this\px[i] = iac2\x
								this\py[i] = iac2\y
								iac2 = Before iac2
							Next
							this\ploc = 0
							this\state$ = "move"
							this\job$ = "buildirrigation"
						Else
						this\state$="automate"
						End If
					End If
				Case "gobacktohomecity"
					For e.city = Each city
						If e\id = this\cityid
							this\ex = e\x
							this\ey = e\y
							ex = e\x
							ey = e\y
						End If						
					Next	
					sx = this\x
					sy = this\y
					this\sx = this\x
					this\sy = this\y
					If findpath()= True Then
						cnt = 0
						this\ploc = 0
						For c.path = Each path
							cnt = cnt + 1
						Next
						cnt=cnt-1
						gbhc.path = Last path
						For i=0 To cnt
							this\px[i] = gbhc\x
							this\py[i] = gbhc\y
							gbhc = Before gbhc
						Next
						this\state$ = "move"
						this\job$ = "automate"
					Else
						this\state$ = "automate"
					End If			 
				Case "connectcitieswithroads"
					; find closest road for unit
					continue = False
					For that.cityroadimp = Each cityroadimp
						If islandmap(that\x,that\y) = islandmap(this\x,this\y)
							continue = True
						End If
					Next
					If continue = True Then
						shortest = 100000
						For that.cityroadimp = Each cityroadimp
							If islandmap(this\x,this\y) = islandmap(that\x,that\y)
								d = distance(this\x,this\y,that\x,that\y)
								If d<shortest
									shortest = d
									ex = that\x
									ey = that\y
								End If
							End If
						Next
						If this\x = ex And this\y = ey
							this\sx = x
							this\sy = y
							this\ex = x
							this\ey = y
							this\state$ = "layroad"
							this\waitturns = 4
							removecityroadimp(ex,ey)
						Else
							sx = this\x
							sy = this\y
							If findpath() = True
								this\sx = this\x
								this\sy = this\y
								this\ex = ex
								this\ey = ey
								;DebugLog this\sx+","+this\sy+":"+this\ex+","+this\ey
								removecityroadimp(ex,ey)
								cnt = 0
								For a.path = Each path
									cnt = cnt + 1
								Next		
								cnt=cnt-1
								a.path = Last path
								For i=0 To cnt
									this\px[i] = a\x
									this\py[i] = a\y
									a = Before a
								Next
								this\ploc = 0
								this\state$ = "move"
								this\job$ = "layroad"								 		
							Else
								this\state$ = "automate"
							End If
						End If
					Else
						this\state = "automate"
					End If
				Case "move"
					this\ploc = this\ploc + 1
					this\x = this\px[this\ploc]
					this\y = this\py[this\ploc]
					If roadmap(this\x,this\y) = 1
						this\movesleft = this\movesleft - 0.3
					Else
						this\movesleft = this\movesleft - 1
					End If
					If this\x = this\ex And this\y = this\ey
						this\state$ = this\job$
						Select this\job$
							Case "layroad"
							this\waitturns = 4
							Case "buildirrigation"
							this\waitturns = 4
						End Select
					End If
					If debugmode = True Then
						Cls
						drawmap(2)
						drawroadmap()
						drawirrigationmap()						
						drawunits()	
						drawcities()
						Color 0,0,0
						Rect 0,0,100,15,True
						Color 255,255,255
						Text 0,0,"Turn:"+turn
						Flip
						If KeyDown(1) = True Then End
					End If
				Case "layroad"
					this\waitturns = this\waitturns - 1
					this\movesleft = 0
					If this\waitturns =< 0
						this\state$="automate"
						roadmap(this\x,this\y) = 1
					End If
				Case "buildirrigation"
					this\waitturns = this\waitturns - 1
					this\movesleft = 0
					If this\waitturns =< 0
						this\state$ = "automate"
						irrigationmap(this\x,this\y) = 1
					End If
			End Select
		Wend
	Next
End Function

Function removegroadimp(x,y)
	For this.groadimp = Each groadimp
		If this\x = x And this\y = y Then
			Delete this
			Return
		End If
	Next
End Function

Function makeglobalimps()
	For y=0 To mapheight
	For x=0 To mapwidth
		If nearbycity(x,y) = True Then
		Select map(x,y)
			Case 1
			a.groadimp = New groadimp
			a\x = x
			a\y = y
			Case 2
			a.groadimp = New groadimp
			a\x = x
			a\y = y
			Case 3
			a.groadimp = New groadimp
			a\x = x
			a\y = y			
			Case 4
			a.groadimp = New groadimp
			a\x = x
			a\y = y
		End Select
	EndIf
	Next
	Next
End Function

Function nearbycity(x,y)
	For this.city = Each city
		If distance(x,y,this\x,this\y) < 6 Then Return True
	Next
End Function


Function improvmentsleft(this.unit)
	For a.girriimp = Each girriimp
		If islandmap(a\x,a\y) = islandmap(this\x,this\y)
			Return True
		End If
	Next
	For b.gforrestimp = Each gforrestimp
		If islandmap(b\x,b\y) = islandmap(this\x,this\y)
			Return True
		End If		
	Next
	For c.groadimp = Each groadimp
		If islandmap(c\x,c\y) = islandmap(this\x,this\y)
			Return True
		End If		
	Next
	For d.gmineimp = Each gmineimp
		If islandmap(d\x,d\y) = islandmap(this\x,this\y)
			Return True
		End If		
	Next		
End Function

Function removeirrifromlist(x,y)
	For this.cityirriimp = Each cityirriimp
		If this\x = x And this\y = y 
			Delete this
			Return
		End If
	Next
End Function

Function improvmentsnearcity(this.unit)
	For that.cityirriimp = Each cityirriimp
		If islandmap(this\x,this\y) = islandmap(that\x,that\y)
			Return True
		End If
	Next
End Function

Function unitisinhomecity(this.unit)
	For that.city = Each city
		If this\cityid = that\id
		If this\x = that\x And this\y = that\y Then Return True
		End If
	Next
End Function

Function removecityroadimp(x,y)
	For this.cityroadimp = Each cityroadimp
		If this\x = x And this\y = y
			Delete this
			Return
		End If
	Next
End Function

Function roadsconnectingcitiesleft(this.unit)
	For that.cityroadimp = Each cityroadimp
		If islandmap(this\x,this\y) = islandmap(that\x,that\y) Then Return True
	Next
End Function

Function makecityirriimp()
	For this.city = Each city	
		For that.cityfield = Each cityfield
			x = this\x+that\x
			y = this\y+that\y
			If x=>0 And y=>0 And x=<mapwidth And y=<mapheight
			If islandmap(x,y) = islandmap(this\x,this\y)
			If map(x,y) = 1
				a.cityirriimp = New cityirriimp
				a\x = x
				a\y = y
			End If
			End If
			End If
		Next
	Next
End Function

Function drawunits()
	For this.unit = Each unit
		Color 255,0,0
		Rect this\x*tilewidth,this\y*tileheight,16,16,True
		x = this\x*tilewidth+8
		y = this\y*tileheight+8
		Color 255,255,255
		Select this\state$
			Case "layroad"
			Text x,y,"R",True,True
			Case "buildirrigation"
			Text x,y,"I",True,True
		End Select
	Next
End Function

Function drawirrigationmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		If irrigationmap(x,y) = 1 Then
			;Color 50,255,20
			;Oval x*tilewidth+4,y*tileheight+4,16-8,16-8,True
			DrawImage irrigationimage,x*tilewidth,y*tileheight
		End If
	Next
	Next
End Function

Function createunits()
	For this.city = Each city
		num = Rand(2,5)
		For i=1 To num
			that.unit = New unit
			that\x = this\x
			that\y = this\y
			that\homecityx = this\x
			that\homecityy = this\y
			that\id = unitnum
			that\cityid = this\id
			that\movesleft = 1
			that\state$ = "automate"					 
		Next
	Next
End Function

Function makecityfield()
	Restore cityf
	For y=-2 To 2
	For x=-2 To 2
		Read a
		If a = 1 Then
			this.cityfield = New cityfield
			this\x = x
			this\y = y
		End If
	Next
	Next	
End Function

Function makeislandmap()
	For y=0 To mapheight
	For x=0 To mapwidth
		islandmap(x,y) = 0
	Next
	Next
	Delete Each ol
	Delete Each cl
	currentisland = 0
	For y=0 To mapheight
	For x=0 To mapwidth
		If map(x,y) > 1
			If islandmap(x,y) = 0
				currentisland = currentisland + 1
				exitloop = False
				a.ol = New ol
				a\x = x
				a\y = y
				islandmap(a\x,a\y) = currentisland
				While exitloop = False
					If openlistisempty() = True Then exitloop = True
					If exitloop = False
					c.ol = First ol
					x2=c\x
					y2=c\y
					removefromopenlist(x2,y2)
					d.cl = New cl
					d\x = x2
					d\y = y2
					For y1=-1 To 1
					For x1=-1 To 1
						If x2+x1=>0 And y2+y1=>0 And x2+x1=<mapwidth And y2+y1=<mapheight
						If isonopenlist(x2+x1,y2+y1) = False
						If isonclosedlist(x2+x1,y2+y1) = False
						If map(x2+x1,y2+y1) =>1
							b.ol = New ol
							b\x = x2+x1
							b\y = y2+y1
							islandmap(x2+x1,y2+y1) = currentisland
						End If					
						End If
						End If
						End If
					Next
					Next
					End If
				Wend
			End If
		End If		
	Next
	Next
End Function

Function drawroadmap()
	Color 200,200,0
	For y=0 To mapheight
	For x=0 To mapwidth
		For y1=-1 To 1
		For x1=-1 To 1
			nx = x+x1
			ny = y+y1
			If nx=>0 And ny=>0 And nx=<mapwidth And ny=<mapheight
				If roadmap(x,y) = True And roadmap(nx,ny) = True Then
				Line nx*tilewidth+8,ny*tileheight+8,x*tilewidth+8,y*tileheight+8
				End If
			End If
		Next
		Next
	Next
	Next
	
End Function

Function makeroads()
	For this.city = Each city
	For that.city = Each city
	If Not this\id = that\id
		sx = this\x
		sy = this\y
		ex = that\x
		ey = that\y
		DebugLog sx+","+sy+","+ex+","+ey
		findpath
		For a.path = Each path
			roadmap(a\x,a\y) = 1
		Next
	End If
	Next
	Next
	For y=0 To mapheight
	For x=0 To mapwidth
		If roadmap(x,y) = True And cityontile(x,y) = False Then
			z.cityroadimp = New cityroadimp
			z\x = x
			z\y = y
		End If
		roadmap(x,y) = False
	Next
	Next
	For this.city = Each city
		roadmap(this\x,this\y) = True
	Next
End Function

Function cityontile(x,y)
	For this.city = Each city
		If this\x = x And this\y = y Then Return True
	Next
End Function

Function findpath()
	Delete Each ol
	Delete Each cl
	Delete Each path	
	If sx=ex And sy=ey Then Return False
	Local tx,ty,tf,tg,th,newx,newy,lowestf,exitloop
	a.ol = New ol
	a\x = sx
	a\y = sy
	While exitloop = False
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
		If tx = ex And ty = ey ; path found
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
			If isonclosedlist(newx,newy) = False
			If isonopenlist(newx,newy) = False
			If map(newx,newy) > 0
			e.ol = New ol
			e\x = newx
			e\y = newy
			If roadmap(newx,newy) = 1 
			e\g = 1
			Else
			e\g = map(newx,newy)*3
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

Function drawpath()
	Color 255,255,0
	For this.path = Each path
		Oval this\x*tilewidth,this\y*tileheight,4,4,True
	Next
End Function

Function createcities(num)
	Delete Each city
	For i=1 To num
		this.city = New city
		this\id = i
		this\size = Rand(1,10)
		exitloop = False
		While exitloop = False And KeyDown(1) = False
			x = Rand(mapwidth)
			y = Rand(mapheight)
			If map(x,y) = 1
			sdist = 1000
			For that.city = Each city
				d = distance(that\x,that\y,x,y)
				If d<sdist Then sdist = d
			Next
			If sdist>5 Then
				this\x=x
				this\y=y
				roadmap(x,y) = 1
				exitloop = True
			End If
			End If
		Wend
	Next
End Function

Function drawcities()
	For this.city = Each city
		Color 0,0,0
		Rect this\x*tilewidth,this\y*tileheight,tilewidth,tileheight,False
		Color 255,255,255
		Rect this\x*tilewidth+1,this\y*tileheight+1,tilewidth-2,tileheight-2,True
		Color 100,100,100
		Text this\x*tilewidth+8,this\y*tileheight+8,this\size,True,True
	Next
End Function

Function drawmap(mode = 1)
	If mode = 1
	For y=0 To mapheight
	For x=0 To mapwidth
		Rect x*tilewidth,y*tileheight,tilewidth,tileheight,False
		Text x*tilewidth,y*tileheight,map(x,y)
	Next
	Next
	End If
	If mode = 2 Or mode = 3
	For y=0 To mapheight
	For x=0 To mapwidth
		Select map(x,y)
			Case 0;water
			Color 0,0,200
			Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
			Case 1;gras
			Color 0,100,0
			Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True
			Case 2;tree
			Color 0,100,0
			Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True						
			DrawImage treeimage,x*tilewidth,y*tileheight
			Case 3;hills
			Color 0,100,0
			Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True						
			DrawImage hillimage,x*tilewidth,y*tileheight
			Case 4;mountain
			Color 0,100,0
			Rect x*tilewidth,y*tileheight,tilewidth,tileheight,True						
			DrawImage mountainimage,x*tilewidth,y*tileheight			
		End Select
			;Color 0,0,0
			;Rect x*tilewidth,y*tileheight,tilewidth+1,tileheight+1,False
			If mode = 3
			Color 255,255,255
			Text x*tilewidth+12,y*tileheight+12,islandmap(x,y),True,True
			End If
	Next
	Next
	End If
End Function

Function makemap()
	For y=0 To mapheight
	For x=0 To mapwidth
	map(x,y)=0
	Next
	Next
	For i=0 To 30
		x = Rand(mapwidth)
		y = Rand(mapheight)
		For y1=-4 To 4
		For x1=-4 To 4
			If x+x1=>0 And y+y1=>0 And x+x1=<mapwidth And y+y1=<mapheight
				map(x+x1,y+y1)=map(x+x1,y+y1)+1
			End If
		Next
		Next
		For y1=-2 To 2
		For x1=-4 To 4
			If x+x1=>0 And y+y1=>0 And x+x1=<mapwidth And y+y1=<mapheight
				map(x+x1,y+y1)=map(x+x1,y+y1)+1
			End If
		Next
		Next
		For y1=-1 To 1
		For x1=-4 To 4		
			If x+x1=>0 And y+y1=>0 And x+x1=<mapwidth And y+y1=<mapheight
				map(x+x1,y+y1)=map(x+x1,y+y1)+1
			End If
		Next
		Next
	Next
	For y=0 To mapheight
	For x=0 To mapwidth
		If map(x,y) = 1 Then map(x,y) = 1;gras
		If map(x,y) = 2 Then map(x,y) = 1;gras
		If map(x,y) = 3 Then map(x,y) = 1;gras
		If map(x,y) = 4 Then map(x,y) = 1;gras
		If map(x,y) =>1 And Rand(12) = 1 Then map(x,y) = 2
		If map(x,y) =>1 And Rand(22) = 1 Then map(x,y) = 3
		If map(x,y) = 5 Then map(x,y) = 2;trees
		If map(x,y) = 6 Then map(x,y) = 2;trees
		If map(x,y) = 7 Then map(x,y) = 2;trees		
		If map(x,y) = 8 Then map(x,y) = 3;hills
		If map(x,y) = 9 Then map(x,y) = 3;hills
		If map(x,y) > 9 Then map(x,y) = 4;mountains			
	Next
	Next	
End Function

Function makeimages()
	SetBuffer ImageBuffer(treeimage)
	Restore tree
	Color 0,200,0
	For y=0 To 15
	For x=0 To 15
	Read a
	If a = 1 Then Plot x,y
	Next
	Next
	SetBuffer ImageBuffer(hillimage)
	Color 200,100,0
	For y=0 To 15
	For x=0 To 15
	Read a
	If a = 1 Then Plot x,y
	Next
	Next
	SetBuffer ImageBuffer(mountainimage)
	Color 200,200,200
	For y=0 To 15
	For x=0 To 15
	Read a
	If a = 1 Then Plot x,y
	Next
	Next
	SetBuffer ImageBuffer(irrigationimage)
	Color 200,200,20
	For y=0 To 15
	For x=0 To 15
	Read a
	If a = 1 Then Plot x,y
	Next
	Next
	SetBuffer BackBuffer()
End Function

Function distance(x1,y1,x2,y2)
	Return Abs(x2-x1)+Abs(y2-y1)
End Function

.tree
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,1,1,0,0,0,0,1,1,0,0,0,0,0,0
Data 0,1,1,1,1,0,1,1,1,1,1,0,0,0,0,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0
Data 0,0,1,1,1,1,1,1,1,1,1,1,1,1,0,0
Data 0,0,1,1,1,0,0,0,0,1,1,1,1,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
.hill
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,1,1,1,1,1,1,1,1,1,0,0,0
Data 0,0,0,0,1,1,1,1,1,1,1,1,1,0,0,0
Data 0,0,0,1,1,1,1,1,1,1,1,1,1,1,0,0
Data 0,0,0,1,1,1,1,1,1,1,1,1,1,1,0,0
Data 0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0
Data 0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
.mountain
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0
Data 0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0
Data 0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0
Data 0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0
Data 0,0,0,1,1,1,1,1,1,1,1,1,0,0,0,0
Data 0,0,1,1,1,1,1,1,1,1,1,1,1,0,0,0
Data 0,0,1,1,1,1,1,1,1,1,1,1,1,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
.irrigation
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0
Data 0,0,0,0,0,1,0,0,1,0,0,0,1,0,0,0
Data 0,0,0,1,0,0,1,0,0,1,0,0,0,1,0,0
Data 0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0
Data 0,0,1,0,0,0,0,0,1,0,0,1,0,0,1,0
Data 0,0,0,1,0,0,1,0,0,0,0,0,1,0,0,0
Data 0,0,0,0,1,0,0,1,0,0,1,0,0,1,0,0
Data 0,0,1,0,0,1,0,0,1,0,0,1,0,0,0,0
Data 0,0,0,1,0,0,0,0,0,0,0,0,1,0,0,0
Data 0,1,0,0,0,0,0,1,0,0,1,0,0,0,0,0
Data 0,0,1,0,0,1,0,0,1,0,0,1,0,0,0,0
Data 0,0,0,1,0,0,1,0,0,0,0,0,1,0,0,0
Data 0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,0
Data 0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0



.cityf
Data 0,0,1,0,0
Data 0,1,1,1,0
Data 1,1,0,1,1
Data 0,1,1,1,0
Data 0,0,1,0,0
