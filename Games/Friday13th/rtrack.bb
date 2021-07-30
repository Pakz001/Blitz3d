;
;
; Blitz +
;
;
Graphics 800,600,32,2
;Graphics 1024,768,32,1
SetBuffer BackBuffer()
HidePointer()
;
Type p
	Field mw,mh,tw,th,gw,gh,scale,timer
	Field mcx,mcy,mpx#,mpy#,gamespeed
	Field hdelay,vdelay,ml,mu,mr,md,msx#,msy#
	Field tileeditcursor,brushsize
	Field mmapcx,mmapcy,mapredraw
End Type
Global tex = CreateImage(96,96)
Dim datex(96,96)
p.p = New p
p\mw = 100:p\mh  = 100:p\scale = 49:p\mapredraw = True
p\gw = GraphicsWidth():p\gh = GraphicsHeight()
p\tw = p\gw / 20:p\th = p\gh / 15
p\timer = 10:p\brushsize = 1
p\msx = 1:p\msy = 1:p\gamespeed = 5
Dim map(p\mw,p\mh)
For x=0 To p\mw : For y=0 To p\mh : map(x,y) = 9 : Next:Next
Dim blockbuffer(15,15,15)
Global tempim
Dim mpb(16,16)
Dim mpb2(16,16)
Dim mappieces(128,96)
Const nummappieces = 16
Global memused$
readtex1:readblocks():readmap
Global fps,fpstimer,fpscounter
Global md = 1
Const nummaptiles = 11
For i=9 To 96:p\scale = i:buildblocks:Next
memused$ = "Vidmemused : " + (TotalVidMem()-AvailVidMem())
p\scale = 32:timer = CreateTimer(60)
Global mbuffer = CreateImage(GraphicsWidth(),GraphicsHeight(),1,2)
;RuntimeError memused$
While  we <> $803
	we = WaitEvent()
	Select we
	Case $4001
		If KeyDown(1) = True Then we = $803
		Cls
		thefps:	zooming	
		For i=0 To p\gamespeed : mapcontrols : Next
		;
		If p\mapredraw = True		
			SetBuffer ImageBuffer(mbuffer):Cls
			Viewport 0,32,GraphicsWidth(),GraphicsHeight()-(32*2):drawmap(0,33):Viewport 0,0,GraphicsWidth(),GraphicsHeight()
			SetBuffer BackBuffer()
			p\mapredraw = False
		End If		
		DrawBlock mbuffer,0,0
		;
		If KeyHit(59) Then tileedit
		Color 240,240,240
		Oval MouseX(),MouseY(),51,51,False		
		Text 400,0,fps : Text 420,0,p\scale
		Text 200,0,AvailVidMem()
		Text 0,0,p\mcx + " : " + p\mpx + " : " + p\msx
		Text 0,20,p\mcy + " : " + p\mpy + " : " + p\msy
		Text 0,GraphicsHeight()-20,memused$
		Flip False
	End Select
Wend
Function thefps()
	fpscounter = fpscounter + 	1
	If fpstimer < MilliSecs() Then
		fps = fpscounter
		fpscounter = 0
		fpstimer = MilliSecs() + 1000
	End If
	Return fps
End Function
End

Function zooming()
p.p = First p
	If p\timer < MilliSecs() And (KeyDown(13) Or KeyDown(12))
		p\mapredraw = True
		If KeyDown(13) md = 1
		If KeyDown(12) md = -1		
		p\timer = MilliSecs() + (1000/30)
		p\scale = p\scale + md 
		If p\scale > 95 Then p\scale = 95
		If p\scale < 10 Then p\scale = 10
		;
		If KeyDown(54) Then rval = rval - 1 Else rval = rval + 1
		If KeyDown(42) Then rval = rval + 1
		If rval > 360 Then rval = 0
	End If
End Function
Function mapcontrols()
	p.p = First p
	omx = p\mcx:omy = p\mcy
	opx = p\mpx:opy = p\mpy
	If KeyDown(203) ; l
		ml = True : p\ml = True : p\mr = False :p\msx = 1: p\hdelay = MilliSecs()+1500
		p\mapredraw = True
	End If
	If KeyDown(205) ; r
		mr = True : p\mr = True : p\ml = False :p\msx = 1: p\hdelay = MilliSecs()+1500
		p\mapredraw = True
	End If
	If KeyDown(200) ; up
		mu = True : p\mu = True : p\md = False:p\msy = 1 : p\vdelay = MilliSecs()+1500
		p\mapredraw = True
	End If
	If KeyDown(208) ; down
		md = True : p\md = True : p\mu = False:p\msy = 1 : p\vdelay = MilliSecs()+1500
		p\mapredraw = True
	End If	
	;
	
	If p\mu = True Or p\md = True
		If p\vdelay > MilliSecs() Then
			p\mapredraw = True
			If p\mu = True Then p\mpy = p\mpy - p\msy
			If p\md = True Then p\mpy = p\mpy + p\msy
			If p\msy > 0 Then p\msy = p\msy - ((1.0/90)/p\gamespeed) Else p\msy = 0
		Else
			p\mu = False
			p\md = False
		End If
	End If
	If p\ml = True Or p\mr = True
		If p\hdelay > MilliSecs() Then
			p\mapredraw = True
			If p\ml = True Then p\mpx = p\mpx - p\msx
			If p\mr = True Then p\mpx = p\mpx + p\msx
			If p\msx > 0 Then p\msx = p\msx - ((1.0/90)/p\gamespeed) : Else p\msx = 0
		Else
			p\ml = False
			p\mr = False
		End If
	End If
	;
	If p\mpx > p\scale Then		
		p\mcx = p\mcx + 1
		p\mpx = 0
	End If
	If p\mpy > p\scale Then
		p\mcy = p\mcy + 1
		p\mpy = 0
	End If
	If p\mpx < 0 Then
		If p\mcx > 0 Then
			p\mcx = p\mcx - 1
			p\mpx = p\scale
			Else
			p\mpx = opx
		End If
	End If
	If p\mpy < 0 Then
		If p\mcy > 0 Then
			p\mcy = p\mcy - 1
			p\mpy = p\scale
			Else
			p\mpy = opy
		End If
	End If

	;
	If p\mcx < 0 Then p\mcx = omx
	If p\mcx > p\mw Then p\mcx = p\mw
	If p\mcy < 0 Then p\mcy = omy
	If p\mcy > p\mh Then p\mcy = opy
	
	;
End Function
;
Function drawmap(x1,y1)
	p.p = First p
	mcx = p\mcx
	mcy = p\mcy
	Color 100,100,100
	;For y=p\mcy To 100-1:For x=p\mcx To 100-1
	r = ((GraphicsWidth()/p\scale))+p\scale
	b = (GraphicsHeight()/p\scale)
	For y=0 To b:For x=0 To r
		osx = ((x*p\scale)-p\mpx)+x1
		osy = ((y*p\scale)-p\mpy)+y1
		mx = x+p\mcx:my = y+p\mcy
		If mx=<100 And my =< 100 Then
		Select map(mx,my)
			Case 1
				;Rect x Shl 5,y Shl 5,32,32
				DrawBlock mappieces(p\scale,0), osx,osy
			Case 2
				DrawBlock mappieces(p\scale,1), osx,osy
			Case 3
				DrawBlock mappieces(p\scale,2), osx,osy
			Case 4
				DrawBlock mappieces(p\scale,3), osx,osy
			Case 5
				DrawBlock mappieces(p\scale,4), osx,osy
			Case 6
				DrawBlock mappieces(p\scale,5), osx,osy
			Case 9
				DrawBlock mappieces(p\scale,9), osx,osy
			Default
				DrawBlock mappieces(p\scale,map(mx,my)),osx,osy
		End Select
		End If
	Next:Next
End Function
Function readmap()
	Restore lvl1
	For y=0 To 15-1
		For x=0 To 20-1
			Read a
			map(x,y) = a
		Next
	Next
End Function
Function buildblocks()
p.p = First p
p\tw = p\scale:p\th = p\scale
makeblock(0)
makeblock(1)
makeblock(2)
makeblock(3)
makeblock(4)
makeblock(5)
makeblock(9)

makeblock2(10)



;For i=1 To 64-9
;makeblock(0,i+10)
;Next

End Function
Function readblocks()
	Restore tr1:For y=0 To 15:For x=0 To 15:Read a:blockbuffer(0,x,y) = a:Next:Next:
	Restore tr2:For y=0 To 15:For x=0 To 15:Read a:blockbuffer(1,x,y) = a:Next:Next:
	Restore tr3:For y=0 To 15:For x=0 To 15:Read a:blockbuffer(2,x,y) = a:Next:Next:
	Restore tr4:For y=0 To 15:For x=0 To 15:Read a:blockbuffer(3,x,y) = a:Next:Next:
	Restore tr5:For y=0 To 15:For x=0 To 15:Read a:blockbuffer(4,x,y) = a:Next:Next:
	Restore tr6:For y=0 To 15:For x=0 To 15:Read a:blockbuffer(5,x,y) = a:Next:Next:
	Restore grass1:For y=0 To 15:For x=0 To 15:Read a:blockbuffer(9,x,y) = a:Next:Next
	Restore tree1:For y=0 To 15:For x=0 To 15:Read a:blockbuffer(10,x,y) = a:Next:Next
End Function
Function makeblock(num,tmp=0)

	p.p = First p
	;If mappieces(num) > 0 Then FreeImage mappieces(num)
	tempim = CreateImage(p\tw,p\th,1,2)	
	SetBuffer ImageBuffer(tempim)	
	tw#  = p\tw :	th# =  p\th 
	ox# = tw / 16 : oy# = th  / 16
	For y=0 To 15:ny = y*oy
	For x=0 To 15:nx = x*ox
		Select blockbuffer(num,x,y)
			Case 0 : Color 150,150,150	:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),2):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 3 : Color 160,160,160	:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),2):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 5 : Color 190,190,190	:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),2):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 6 : Color 70,70,70			:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),2):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 4 : Color 140,140,140	:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),2):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 1 : Color 50,50,50			:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),2):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 2 : Color 200,200,200	:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),2):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 7 : Color 21,160,0			:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),1):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 8 : Color 20,180,0			:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),1):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
		End Select
			
	Next:Next
	If tmp = 0 Then
		mappieces(p\scale,num) = CopyImage(tempim)
		Else
		mappieces(p\scale,tmp) = CopyImage(tempim)
	End If

	SetBuffer BackBuffer()	
	FreeImage(tempim)		
End Function

Function makeblock2(num)
	p.p = First p
	;If mappieces(num) > 0 Then FreeImage mappieces(num)
	tempim = CreateImage(p\tw,p\th,1,2)	
	SetBuffer ImageBuffer(tempim)	
	tw#  = p\tw :	th# =  p\th 
	ox# = tw / 16 : oy# = th  / 16
	For y=0 To 15:ny = y*oy
	For x=0 To 15:nx = x*ox
		Select blockbuffer(num,x,y)
			Case 0 : Color 150,150,150	:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),2):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 5 : Color 190,190,190	:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),2):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 6 : Color 70,70,70			:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),2):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 4 : Color 140,140,140	:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),2):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 1 : Color 10,60,10			:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),2):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 2 : Color 10,40,10			:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),2):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 3 : Color 30,90,30		:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),2):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 7 : Color 21,160,0			:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),1):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 8 : Color 20,180,0			:Rect nx,ny, ox+1,oy+1,True :a=blend(x,y,ColorRed(),ColorGreen(),ColorBlue(),1):Color getr(a),getg(a),getb(a):Rect nx,ny, ox+1,oy+1,True
			Case 9 : Color 4,5,5				:Rect nx,ny, ox+1,oy+1,True 
		End Select
			
	Next:Next	
	mappieces(p\scale,num) = CopyImage(tempim)
	SetBuffer BackBuffer()
	FreeImage(tempim)		
End Function


Function setcolor(a)
	
	Color getr(a),getg(a),getb(a)
End Function
Function blend(x,y,ar#,ag#,ab#,met)
	Select met
		Case 1		
			a  = datex(x,y):r# = getr(a):g#=getg(a):b#=getb(a)
			nr# = (ar + r) /2:ng# = (ag + g) /2:nb# = (ab + b) /2 ; 50/50 blend
		Case 2	
			a  = datex(x,y):r# = getr(a)/4:g#=getg(a)/4:b#=getb(a)/4
			nr# = (ar + r) :ng# = (ag + g) :nb# = (ab + b) 
	End Select
	Return getrgb(nr,ng,nb)
End Function
Function flipmpb()
	x2 = 0:y2 = 0
	For y = 0 To 15
		For x = 15 To 0 Step -1
			mpb2(x2,y2) = mpb(x,y)
			x2=x2 + 1
		Next
		x2 = 0
		y2=y2+1
	Next
	f = WriteFile("tit.txt")
	WriteLine f,".tr4"
	For y=0 To 15:For x=0 To 15
		a$=a$ + mpb2(x,y)
		If x<15 Then a$=a$+ " , "
	Next
	WriteLine f,"Data " + a$
	a$ = ""
	Next
;
	x2=0:y2=0
	For y = 15 To 0 Step -1
		For x = 15 To 0 Step -1
			mpb2(x2,y2) = mpb(x,y)
			x2=x2 + 1
		Next
		x2 = 0
		y2=y2+1
	Next
	f = WriteFile("tit.txt")
	WriteLine f,".tr5"
	For y=0 To 15:For x=0 To 15
		a$=a$ + mpb2(x,y)
		If x<15 Then a$=a$+ " , "
	Next
	WriteLine f,"Data " + a$
	a$ = ""
	Next

	x2=0:y2=0
	For y = 15 To 0 Step -1
		For x = 0 To 15
			mpb2(x2,y2) = mpb(x,y)
			x2=x2 + 1
		Next
		x2 = 0
		y2=y2+1
	Next
	f = WriteFile("tit.txt")
	WriteLine f,".tr5"
	For y=0 To 15:For x=0 To 15
		a$=a$ + mpb2(x,y)
		If x<15 Then a$=a$+ " , "
	Next
	WriteLine f,"Data " + a$
	a$ = ""
	Next
	
	CloseFile(f)
	ExecFile("notepad tit.txt")
End Function
Function tileedit()
	Viewport 0,0,GraphicsWidth(),GraphicsHeight()
	
	p.p = First p
	p\mapredraw = True
	;Local tb = CreateImage(GraphicsWidth(),GraphicsHeight())
	;GrabImage tb,0,0
	;tb = dimimage(tb)
	dimtset(0,1)
	dimtset(-1,4)
	While we<> $803
		we = WaitEvent()
		Select we
		Case $4001
			If KeyHit(1) Then we=$803
				Cls
				If p\mapredraw = True Then
				;DebugLog "ok"
				SetBuffer ImageBuffer(mbuffer):Cls
				Viewport 0,32,GraphicsWidth(),GraphicsHeight()-64
				drawmap(0,0)
				Viewport 0,32,640,480				
				p\scale = p\scale - 1 : drawmap(0,0) : p\scale = p\scale + 1											
				p\mapredraw = False
				SetBuffer BackBuffer()				
				End If
				;
				DrawBlock mbuffer,0,0
				If KeyHit(2) Then p\brushsize = 0
				If KeyHit(3) Then p\brushsize = 1
				If KeyHit(4) Then p\brushsize = 2
				If KeyHit(5) Then p\brushsize = 3
				;
				If KeyDown(205) Then p\mmapcx = p\mmapcx + 1:p\mapredraw = True
				If KeyDown(203) Then p\mmapcx = p\mmapcx - 1:p\mapredraw = True
				If KeyDown(200) Then p\mmapcy = p\mmapcy - 1:p\mapredraw = True
				If KeyDown(208) Then p\mmapcy = p\mmapcy + 1:p\mapredraw = True
				;
				;
				drawminimap(2+4,32+4)
				draweditpanel(0,16*32,32+4)
				;
				Color 140,140,140 : Rect 0,32,640,480,False
				Color 140,140,140:Rect MouseX(),MouseY(),16,16,True
				Color 210,210,210:Rect MouseX(),MouseY(),13,13,True
				Color 10,10,10:Rect MouseX(),MouseY(),16*p\brushsize,16*p\brushsize,False
				If mappieces(32,p\tileeditcursor) > 0 DrawImage mappieces(32,p\tileeditcursor),MouseX()+6,MouseY()+6				
				;
				Flip
			End Select
	Wend
	p\scale = p\scale - 1
	buildblocks
	p\scale = p\scale + 1
	buildblocks
	p\mapredraw=True
	FlushKeys()
End Function
Function draweditpanel(num,x1,y1)
	p.p = First p
	For x=0 To 3:For y=0 To 5
		If mappieces(32,num) > 0 Then
			dx = (x*34)+x1
			dy = (y*34)+y1
			If RectsOverlap(MouseX(),MouseY(),1,1,dx,dy,32,32) = True And MouseDown(1) = True Then p\tileeditcursor = num
			DrawImage mappieces(32,num),dx,dy
		End If
		If num < nummappieces Then num = num + 1 Else Return
	Next:Next
End Function
Function drawminimap(x1,y1)
	p.p = First p	
	Viewport 0,0,GraphicsWidth(),GraphicsHeight()
	tw = 640 / 34
	th = 480 / 34
	
	For y=p\mmapcy To p\mmapcy+32:For x=p\mmapcx To p\mmapcx+32
		;
		If RectsOverlap(x,y,1,1,0,0,p\mw,p\mh) = True Then
		dx =((x-p\mmapcx)*th)+x1
		dy =((y-p\mmapcy)*th)+y1
		tx = x-p\mmapcx
		ty = y-p\mmapcy
		If RectsOverlap(MouseX(),MouseY(),1,1,dx,dy,th,th) And MouseDown(1) = True Then
			p\mapredraw = True
			For xx = x To x+p\brushsize : For yy = y To y+p\brushsize
				If RectsOverlap(xx,yy,1,1,0,0,p\mw,p\mh) = True Then
					t  = p\tileeditcursor
					If t<7 Then t=t+1
					map(xx,yy) = t
				End If
			Next:Next
		End If
		If RectsOverlap(MouseX(),MouseY(),1,1,dx,dy,th,th) And MouseDown(2) = True Then
			p\mapredraw = True
			t = map(tx,ty)
			If t<8 Then t = t - 1
			If t>-1 Then p\tileeditcursor = t
			
		End If
		;
		draw = True	
		t = map(x,y)
		If t<8 Then t=t-1
		Color 50,50,50
		Rect dx,dy,th,th,False
		If t>-1 Then
			DrawImage mappieces(th+1,t),dx,dy
		End If
		End If
	Next:Next
End Function
Function dimimage(im)
Local krpim = CreateImage(ImageWidth(im),ImageHeight(im))
krpim = CopyImage(im)
SetBuffer ImageBuffer(krpim)
LockBuffer ImageBuffer(krpim)
For x=0 To (ImageWidth(krpim)-2) 
For y=0 To (ImageHeight(krpim)-2)
	a = ReadPixelFast(x,y,ImageBuffer(krpim))
	rp# = getr(a):	gp# = getg(a):	bp# = getb(a)
	If RectsOverlap(x,y,1,1,0,32,640,480) = False
		If y Mod 5 = 1 Then
			r# = (rp/100)*20
			g# = (gp/100)*20
			b#= (bp/100)*20
		ElseIf y Mod 5 = 0 Then
			r# = (rp/100)*80
			g# = (gp/100)*80
			b#= (bp/100)*80
		Else
			r# = (rp/100)*40
			g# = (gp/100)*40
			b#= (bp/100)*40
		End If
		If r<0 Then r=0 : If g<0 Then g=0:If b<0 Then b=0
		
	Else
		If y Mod 5 = 1 Then
		r# = (rp/100)*10
		g# = (gp/100)*10
		b#= (bp/100)*10
		Else
		r# = (rp/100)*5
		g# = (gp/100)*5
		b#= (bp/100)*5
		End If
		
	End If
	WritePixelFast x,y,getrgb(Floor(r),Floor(g),Floor(b)),ImageBuffer(krpim)
Next
Next
UnlockBuffer ImageBuffer(krpim)
SetBuffer BackBuffer()
Return krpim
FreeImage krpim
End Function

Function dimtset(ts,dp#)
p.p = First p
ots = p\scale
p\scale = p\scale + ts
For i=0 To nummaptiles
	If mappieces(p\scale,i) > 0 Then
		LockBuffer ImageBuffer(mappieces(p\scale,i))
		cnt = 0
		For y=0 To (ImageHeight(mappieces(p\scale,i)))-1
		For x=0 To (ImageWidth(mappieces(p\scale,i)))-1		
			a = ReadPixelFast(x,y,ImageBuffer(mappieces(p\scale,i)))
			rp# = getr(a):	gp# = getg(a):	bp# = getb(a)
			If cnt = 4
				r# = (rp/100)*(20/dp)
				g# = (gp/100)*(20/dp)
				b#= (bp/100)*(20/dp)
			ElseIf cnt = 5
				r# = (rp/100)*(40/dp)
				g# = (gp/100)*(40/dp)
				b#= (bp/100)*(40/dp)
				cnt = 0
			Else
				r# = (rp/100)*(33/dp)
				g# = (gp/100)*(33/dp)
				b#= (bp/100)*(33/dp)
			End If
			cnt = cnt + 1
			WritePixelFast x,y,getrgb(Floor(r),Floor(g),Floor(b)),ImageBuffer(mappieces(p\scale,i))
		Next:Next
		UnlockBuffer ImageBuffer(mappieces(p\scale,i))
	End If
Next
SetBuffer BackBuffer()
p\scale = ots
Return krpim
End Function

Function GetRGB(r,g,b)
	Return b Or (g Shl 8) Or (r Shl 16)
End Function
Function GetR(rgb)
    Return rgb Shr 16 And %11111111
End Function
Function GetG(rgb)
	Return rgb Shr 8 And %11111111
End Function
Function GetB(rgb)
	Return rgb And %11111111
End Function
Function rotatex(x#,y#,angle#,w#,h#)
	Return (((Cos(angle)*(x-w/2.5) )  + Sin(angle) * (y-h/2.5)))
End Function
Function rotatey(x#,y#,angle#,w#,h#)
	Return (( (-Sin(angle) * (x-w/2.5) ) + Cos(angle)	*(y-h/2.5)))
End Function
Function rotatex2(x#,y#,angle#,w#,h#)
	Return (((Cos(angle)*(x-(w/2)) )  + Sin(angle) * (y-h/2))) + w/2
End Function
Function rotatey2(x#,y#,angle#,w#,h#)
	Return (( (-Sin(angle) * (x-w/2) ) + Cos(angle)	*(y-h/2)))+h/2
End Function

Function readtex1()
SetBuffer ImageBuffer(tex)
Restore tex1
For y=0 To 95:For x=0 To 95
	Read a
	WritePixel x,y,a
	datex(x,y) = a
Next:Next
SetBuffer BackBuffer()
End Function

Function flipstring$(in$)
For i=Len(in$) To 1 Step -1
	a$ = a$ + Mid(in$,i,1)
Next
Return a$
End Function
.lvl1
Data 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9
Data 9 , 3 , 1 , 1 , 4 , 9 , 9 , 9 , 9 , 9 , 3 , 1 , 1 , 4 , 9 , 9 , 3 , 1 , 4 , 9
Data 9 , 2 , 9 , 9 , 2 , 9 , 9 , 9 , 3 , 1 , 5 , 9 , 9 , 2 , 9 , 9 , 2 , 9 , 2 , 9
Data 9 , 2 , 9 , 9 , 6 , 1 , 1 , 1 , 5 , 9 , 9 , 9 , 9 , 6 , 4 , 9 , 2 , 9 , 2 , 9
Data 9 , 2 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 3 , 1 , 4 , 9 , 9 , 2 , 9 , 2 , 9 , 2 , 9
Data 9 , 2 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 2 , 9 , 2 , 9 , 3 , 5 , 9 , 2 , 9 , 2 , 9
Data 9 , 2 , 9 , 3 , 1 , 1 , 1 , 1 , 1 , 5 , 9 , 2 , 9 , 2 , 9 , 9 , 2 , 9 , 2 , 9
Data 9 , 2 , 9 , 2 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 2 , 9 , 6 , 1 , 1 , 5 , 9 , 2 , 9
Data 9 , 6 , 1 , 5 , 3 , 1 , 1 , 1 , 4 , 9 , 9 , 2 , 9 , 9 , 9 , 9 , 9 , 9 , 2 , 9
Data 9 , 9 , 9 , 3 , 5 , 9 , 9 , 9 , 2 , 9 , 9 , 6 , 1 , 1 , 1 , 1 , 4 , 9 , 2 , 9
Data 9 , 9 , 9 , 6 , 1 , 4 , 9 , 9 , 2 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 2 , 9 , 2 , 9
Data 9 , 3 , 1 , 1 , 1 , 5 , 9 , 9 , 6 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 5 , 9 , 2 , 9
Data 9 , 2 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 2 , 9
Data 9 , 6 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 5 , 9
Data 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9 , 9

.tr1
Data 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1
Data 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 0 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 0 , 0 , 4 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 0 , 0 , 0 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 0 , 0 , 0 , 0 , 0 , 0 , 4 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 3 , 0 , 0 , 0 , 0
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 4 , 0 , 0 , 0 , 0
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2 , 2
Data 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1 , 1

.tr2
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1

.tr3
Data 8 , 8 , 8 , 8 , 8 , 8 , 1 , 1 , 1 ,1 , 1 , 1 , 1 , 1 , 1 , 1
Data 8 , 8 , 8 , 8 , 8 , 1 , 1 , 1 , 1 , 1 , 2 , 2 , 2 , 2 , 2 , 2
Data 8 , 8 , 8 , 8 , 1 , 1 , 1 , 2 , 2 , 2 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 8 , 8 , 1 , 2 , 2 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 8 , 1 , 2 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 8 , 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 1 , 2 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 1 , 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1

.tr4
Data 1 , 1 , 1 , 1 , 1 , 1 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8
Data 2 , 2 , 2 , 2 , 2 , 2 , 1 , 1 , 1 , 8 , 8 , 8 , 8 , 8 , 8 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 2 , 2 , 2 , 1 , 1 , 8 , 8 , 8 , 8 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 2 , 1 , 8 , 8 , 8 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1 , 8 , 8 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1 , 8 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1 , 8 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1

.tr5
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1 , 8 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1 , 8 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1 , 8 , 8 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 2 , 1 , 8 , 8 , 8 , 8
Data 0 , 0 , 0 , 0 , 0 , 0 , 2 , 2 , 2 , 1 , 1 , 8 , 8 , 8 , 8 , 8
Data 2 , 2 , 2 , 2 , 2 , 2 , 1 , 1 , 1 , 8 , 8 , 8 , 8 , 8 , 8 , 8
Data 1 , 1 , 1 , 1 , 1 , 1 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8

.tr6
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2 , 1
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 2
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 8 , 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 8 , 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 8 , 8 , 1 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 8 , 8 , 8 , 1 , 2 , 2 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 8 , 8 , 8 , 8 , 1 , 1 , 2 , 2 , 2 , 0 , 0 , 0 , 0 , 0 , 0
Data 8 , 8 , 8 , 8 , 8 , 8 , 8 , 1 , 1 , 1 , 2 , 2 , 2 , 2 , 2 , 2
Data 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 1 , 1 , 1 , 1 , 1 , 1

.grass1
Data 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8
Data 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8
Data 8 , 7 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8
Data 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 7 , 8 , 8 , 8 , 7 , 8
Data 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8
Data 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8
Data 8 , 8 , 8 , 7 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8
Data 8 , 8 , 8 , 8 , 8 , 8 , 7 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8
Data 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8
Data 8 , 8 , 7 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8
Data 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 7 , 8 , 8 , 8 , 8
Data 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8
Data 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 7
Data 8 , 8 , 8 , 7 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8
Data 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 7 , 8
Data 8 , 8 , 8 , 8 , 8 , 8 , 7 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8 , 8

.tree1
Data 8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8
Data 8,8,8,8,8,3,3,3,3,3,1,8,8,8,8,8
Data 8,8,8,3,3,3,1,1,1,1,1,2,2,8,8,8
Data 8,8,3,3,1,1,1,1,1,2,1,2,2,2,8,8
Data 8,8,3,1,1,1,1,2,1,2,1,2,2,2,8,8
Data 8,8,3,1,1,1,2,1,2,1,1,2,2,2,8,8
Data 8,8,1,1,1,2,1,1,1,1,2,2,2,2,2,8
Data 8,8,1,2,2,1,1,1,2,2,2,2,2,2,2,8
Data 8,8,1,2,1,1,1,3,2,2,2,3,2,2,2,8
Data 8,8,1,2,1,1,2,2,2,2,2,2,9,2,2,8
Data 8,8,1,1,2,2,2,2,9,2,2,2,2,2,9,8
Data 8,8,2,2,2,2,2,2,2,2,2,2,2,9,8,8
Data 8,8,2,2,2,2,9,2,2,2,2,2,9,8,8,8
Data 8,8,8,2,2,2,2,2,2,2,2,9,8,8,8,8
Data 8,8,8,8,9,9,9,9,9,9,9,8,8,8,8,8
Data 8,8,8,8,8,8,8,8,8,8,8,8,8,8,8,8

.tex1
Data -12770274,-10011342,-11193815,-10011342,-10339792,-12113373,-12376287,-11128022,-9748684,-10930900,-12310495,-11587801,-11062485,-11062485,-8829125,-9354441,-10274256,-10930900,-13295846,-13098725,-10536913,-11850715,-11719387,-12244958,-11193815,-12244958,-11719130,-12770274,-11390936,-11193814,-9617098,-11653594,-10865364,-10930900,-11719387,-11193815,-11719387,-13230053,-10208463,-10405585,-11719130,-14084076,-11982044,-10536913,-12244958,-13755625,-13689833,-13689833,-13032932,-11719387,-11653594,-11982044,-11128022,-10930900,-10668243,-11062485,-10996693,-10339792,-10602450,-10930900,-10536913,-10536913,-7712445,-10011342,-8303553,-7646652,-7843773,-7843773,-10799572,-9682891,-10668242,-11587801,-12244958,-11128022,-13164261,-11325144,-10799572,-10668243,-12770274,-12770274,-10799572,-11784923,-12178910,-11653594,-10865364,-9814220,-11325144,-10602450,-11128022,-10602450,-11850715,-11587801,-11259607,-11193814,-11522265,-10142670
Data -11128022,-10405585,-11193815,-11982044,-12507616,-10077134,-12704482,-11653594,-9814220,-11522265,-10274256,-11653594,-9617098,-13032932,-9617355,-12638945,-12113373,-11390936,-11850715,-11062485,-12638945,-12704482,-12441824,-11587801,-12047581,-12836067,-9617098,-13295590,-11193814,-10668243,-9682891,-10339792,-12244958,-9288904,-11587801,-11719387,-10930900,-11784923,-13295590,-11456472,-12836067,-12047581,-13558504,-11719130,-11390936,-11719387,-9617098,-11062485,-12244958,-12310495,-12178910,-13164261,-13230053,-10668242,-10734035,-12244702,-11325144,-10471121,-12376287,-11653594,-12770274,-10208463,-8632003,-9682891,-9223112,-7712445,-12047581,-9354441,-9617098,-9157575,-10471121,-10734035,-9880013,-9485770,-10142670,-12441824,-11259607,-10734035,-11128022,-11850715,-11784923,-12047581,-10865364,-12573153,-10339792,-10930900,-10471121,-10536913,-12178910,-11325144,-12310495,-11456472,-13492968,-12310495,-11522265,-10471121
Data -12047581,-11325144,-11719387,-9617098,-10668243,-10996693,-11193814,-9420233,-11719387,-9485770,-11850715,-12244958,-10668242,-10339792,-11719387,-11719130,-13624296,-12047581,-10865364,-12047581,-11719387,-12310495,-11982044,-10668243,-13624296,-12507616,-12704482,-12638945,-11587801,-11522265,-11193815,-11325144,-12704482,-10471121,-11325144,-10077134,-10668243,-10865364,-11128022,-11522265,-12901603,-10930900,-12836067,-11653594,-12113373,-10602450,-8763332,-8894661,-9288904,-10011342,-10274256,-10274256,-10930900,-10930900,-10471121,-10142927,-9617355,-10996693,-12178910,-10996693,-12047581,-11193815,-11062485,-10405585,-9223112,-8500418,-10668243,-9617098,-10668243,-10668242,-10142927,-9223112,-10208463,-9880013,-11062485,-10668243,-11456472,-11850715,-10865364,-10668242,-10865364,-12047581,-10471121,-13492968,-10405585,-10536913,-11982044,-11653594,-11456472,-10602450,-11128022,-11850715,-12047581,-10274256,-11719387,-11193815
Data -10930900,-12638945,-12244702,-12047581,-10536913,-11587801,-12376287,-10536913,-12178910,-10471121,-12178910,-10405585,-12901603,-10077134,-11784923,-12573153,-13755625,-12376287,-12244702,-13230053,-12376287,-11193814,-14149612,-12573153,-12704482,-13821162,-13821162,-11390936,-11062485,-12441824,-10405585,-10405585,-10602450,-9814220,-11456472,-13295590,-10274256,-11390936,-12244958,-11193815,-11062485,-12770274,-12376287,-10996693,-11193815,-10339792,-9682891,-12376287,-10405585,-11719130,-10668243,-11390936,-10274256,-10471121,-11390936,-10142927,-9945805,-11193814,-11850715,-11916252,-11193814,-11784923,-10142670,-8106432,-7449531,-9420233,-10142927,-10011342,-11456472,-10339792,-10602450,-9617098,-10536913,-10208463,-9091783,-10208463,-9617098,-11128022,-10274256,-11325144,-10142670,-10274256,-11259607,-10077134,-13098725,-12638945,-10274256,-11456472,-12310495,-11062485,-10339792,-12178910,-11982044,-11653594,-12244958,-12376287
Data -12244958,-10930900,-12376287,-11062485,-12178910,-10142670,-10734035,-12441824,-12573153,-12507616,-10668243,-11587801,-12310495,-12441824,-12310495,-12310495,-11982044,-9945805,-10865364,-10668242,-10405585,-13295846,-11982044,-11982044,-11522265,-11390936,-11587801,-10799572,-12836067,-12507616,-9617355,-10405585,-11719130,-10930900,-10865364,-10011342,-11128022,-10799572,-12244958,-8829125,-12244702,-13492968,-11390936,-12836067,-11128022,-8960454,-10142670,-10668242,-12573153,-11587801,-11719387,-10339792,-10668243,-11193814,-11982044,-12376287,-11193815,-11916252,-11587801,-9748684,-11719387,-12113373,-10799572,-9025990,-8369089,-9945805,-9091783,-10339792,-10930900,-10471121,-10274256,-9682891,-8960454,-11325144,-9880013,-10274256,-11784923,-9420233,-11653594,-10536913,-10799572,-10274256,-9288904,-10734035,-10668243,-13230053,-11719130,-11456472,-11653594,-11193815,-12507616,-11719130,-11128022,-12376287,-12244958,-11128022
Data -10799572,-12836067,-11325144,-12310495,-12376287,-10865364,-12573153,-11784923,-10339792,-10668243,-11128022,-12441824,-12113373,-13886955,-10996693,-11850715,-13032932,-11719130,-11193814,-11719130,-11982044,-13295590,-12244958,-11850715,-12573153,-10142670,-12441824,-11325144,-11522265,-11850715,-11522265,-11193815,-11916252,-11719387,-9814220,-10077134,-12441824,-13558504,-12376287,-9420233,-10865364,-13821162,-12441824,-10142670,-13032932,-12507616,-13230053,-9157575,-12310495,-13098725,-12836067,-11259607,-10668242,-10602450,-11456472,-11719387,-12836067,-10405585,-10668242,-11062485,-11784923,-11128022,-9026247,-9354441,-10668242,-11259607,-8697540,-11456472,-12638945,-9880013,-10734035,-9880013,-9485770,-9617355,-9682891,-9814220,-9617355,-10471121,-11719130,-11587801,-10471121,-11719130,-10471121,-11850715,-11522265,-11653594,-10536913,-11719387,-11719130,-11653594,-10668242,-12638945,-10668242,-12244702,-11587801,-12770274
Data -11719387,-10536913,-11719130,-9945805,-10339792,-10930900,-9682891,-12178910,-11916252,-10142670,-10734035,-12047581,-12376287,-10930900,-11719130,-10471121,-11850715,-12376287,-11062485,-11916252,-11522265,-8500675,-11719387,-11456472,-11784923,-11062485,-10668242,-10602450,-11587801,-9748684,-12113373,-11193815,-12047581,-12310495,-11916252,-13098725,-12047581,-14412270,-11456472,-11850715,-13032932,-14280941,-11982044,-10930900,-9157575,-10865364,-11259607,-10602450,-10536913,-10668243,-11653594,-12901603,-11325144,-12244702,-11128022,-8500675,-8960454,-11719387,-9617355,-10602450,-9682891,-9617355,-9354441,-9026247,-10865364,-10734035,-9551562,-10011342,-10996693,-9485770,-7975102,-9420233,-9354441,-9288904,-9945805,-9880013,-11456472,-11193815,-10536913,-10405585,-10865364,-12244958,-10668242,-10274256,-10339792,-11719387,-10799572,-10536913,-10471121,-11587801,-11062485,-10602450,-11456472,-11587801,-11653594,-10668242
Data -12244702,-11784923,-13032932,-11982044,-12441824,-13361639,-10142670,-11522265,-11982044,-11587801,-11193814,-11850715,-12376287,-12770274,-11784923,-9682891,-10602450,-10339792,-13032932,-11456472,-10142670,-12310495,-11390936,-11719130,-12441824,-11193815,-10930900,-10734035,-11325144,-11456472,-11982044,-11653594,-9945805,-12047581,-10339792,-10274256,-10536913,-9025990,-10274256,-10668242,-8894661,-11850715,-11719130,-9157575,-9880013,-11719130,-11916252,-11719130,-12507616,-12113373,-13689833,-13295590,-12113373,-11128022,-11587801,-12244702,-10930900,-11850715,-12310495,-12047581,-11325144,-10602450,-11193814,-9617098,-10339792,-10142927,-10077134,-12507616,-9617355,-9880013,-8500675,-11062485,-10142670,-10471121,-10208463,-10536913,-10602450,-11719387,-10405585,-9026247,-9617355,-10930900,-11653594,-12638945,-10799572,-11325144,-10996693,-12178910,-11587801,-11259607,-11522265,-12178910,-11193814,-12376287,-12901603,-11719387
Data -11522265,-12441824,-10799572,-12704482,-11850715,-11062485,-11390936,-10471121,-12770274,-11784923,-9748684,-12573153,-11719130,-11325144,-12441824,-11916252,-10996693,-12573153,-10799572,-11259607,-13361639,-11390936,-12178910,-12638945,-12770274,-11128022,-12441824,-9945805,-12178910,-11259607,-10668243,-9945805,-11259607,-11916252,-10602450,-11653594,-11587801,-12113373,-9354441,-9485770,-9682891,-8829125,-9091783,-8697540,-11193815,-12310495,-12376287,-13952747,-14412270,-14149612,-11522265,-10996693,-9026247,-11062485,-12113373,-10536913,-9157575,-10011342,-10077134,-9551562,-10142927,-11653594,-10142927,-11062485,-11587801,-10208463,-11193815,-12178910,-9485770,-8500418,-9288904,-10734035,-10274256,-11193815,-11259607,-10274256,-11259607,-11193814,-10668243,-10471121,-11719387,-10274256,-12441824,-11850715,-10668242,-11653594,-10208463,-10734035,-12178910,-10536913,-11719387,-12178910,-12376287,-12244702,-11062485,-12310495
Data -11982044,-13032932,-10668243,-11128022,-12638945,-12573153,-11982044,-13295846,-11390936,-12244958,-11193814,-11719387,-10602450,-12244958,-10274256,-10208463,-9617355,-10471121,-12901603,-12770274,-11390936,-12113373,-13032932,-10734035,-11325144,-12836067,-10602450,-13098725,-10865364,-9748684,-11456472,-9354441,-11850715,-11259607,-12310495,-12047581,-13230053,-10799572,-10799572,-10734035,-12113373,-13032932,-11916252,-10668243,-11062485,-11325144,-10471121,-10471121,-9157575,-9551562,-11062485,-10405585,-9945805,-11522265,-11062485,-9945805,-12310495,-10668242,-9288904,-10471121,-11456472,-10011342,-10142927,-11325144,-12178910,-10996693,-9682891,-11522265,-10930900,-8500675,-9748684,-10011342,-10274256,-11982044,-11522265,-11390936,-11456472,-10668243,-11456472,-12178910,-12113373,-10996693,-11259607,-10996693,-10142670,-12178910,-12113373,-12770274,-11719130,-10734035,-11390936,-10536913,-11259607,-11390936,-10471121,-12967396
Data -10734035,-10996693,-12047581,-12244958,-11456472,-12310495,-12967396,-11982044,-11982044,-11062485,-11128022,-12770274,-11719387,-10799572,-11719387,-12244958,-11587801,-12901603,-10865364,-11850715,-13361639,-13361639,-11390936,-11719387,-11784923,-12310495,-12310495,-12376287,-9945805,-11193815,-11390936,-12507616,-11325144,-9945805,-11259607,-12244958,-10930900,-10996693,-11522265,-10471121,-10536913,-10471121,-11062485,-9880013,-12770274,-9682891,-11259607,-12770274,-9617355,-9880013,-9617098,-11982044,-10602450,-13032932,-11719387,-10930900,-10734035,-9420233,-9288904,-10011342,-9025990,-9814220,-11193815,-12507616,-11325144,-11193815,-11325144,-9880013,-10405585,-9814220,-9354441,-10799572,-12244702,-11719130,-11259607,-11390936,-11390936,-10668242,-11128022,-12507616,-11325144,-12507616,-9682891,-11456472,-10339792,-10865364,-11916252,-12310495,-12047581,-12178910,-11916252,-12573153,-10865364,-11193815,-11982044,-10930900
Data -10930900,-12244958,-11193815,-11653594,-12901603,-12441824,-12047581,-11128022,-11259607,-11784923,-11982044,-11850715,-9814220,-9748684,-10405585,-12770274,-11456472,-12244702,-10339792,-13230053,-12770274,-13689833,-12178910,-12770274,-12113373,-11259607,-12638945,-13886955,-9945805,-12507616,-10142927,-13295590,-12573153,-12573153,-11325144,-10668242,-9748684,-9157575,-10142670,-10668243,-10996693,-9420233,-9551562,-11522265,-9814220,-8894661,-11193814,-12178910,-11982044,-13492968,-11062485,-14412527,-14018284,-12704482,-13492968,-13427175,-12836067,-12573153,-12178910,-12244702,-11719387,-11587801,-12047581,-11982044,-11784923,-10471121,-11982044,-10734035,-11325144,-10011342,-10865364,-11259607,-11982044,-12376287,-9617355,-11784923,-11850715,-11259607,-12967396,-11128022,-11456472,-12638945,-11719130,-9420233,-9748684,-11522265,-11062485,-10536913,-12113373,-10668243,-11653594,-11916252,-11916252,-12310495,-11587801,-12244702
Data -12113373,-11784923,-12178910,-12638945,-11128022,-12376287,-10799572,-10339792,-11522265,-9880013,-10668243,-11982044,-10865364,-10865364,-11916252,-10339792,-11390936,-12244958,-10274256,-12376287,-12573153,-10536913,-12967396,-10602450,-10668242,-10471121,-11719387,-10799572,-9157575,-11193814,-12507616,-13295590,-13164261,-12376287,-11259607,-10734035,-9945805,-13098725,-10536913,-10668243,-13492968,-11719130,-13427175,-13492968,-13230053,-12047581,-14412270,-12376287,-14412270,-13689833,-13689833,-13098725,-12638945,-14215405,-13427175,-13952747,-11719387,-12901603,-12376287,-11784923,-11719387,-12244958,-11982044,-11390936,-10405585,-11784923,-10274256,-12770274,-9617098,-10668243,-10930900,-10602450,-10734035,-12770274,-12047581,-10405585,-9617355,-11128022,-13295590,-12376287,-11982044,-11784923,-12047581,-11719130,-10996693,-9026247,-12047581,-11259607,-11982044,-11193814,-10799572,-11193815,-11784923,-11982044,-12244958,-11850715
Data -11850715,-11456472,-12638945,-11522265,-11193814,-11719387,-13164261,-12047581,-10011342,-12573153,-12901603,-9223112,-10734035,-11653594,-10405585,-11784923,-11193814,-12770274,-12704482,-11982044,-13427175,-12376287,-12967396,-11916252,-11784923,-13361639,-11916252,-10668243,-10536913,-10996693,-12244958,-12047581,-10996693,-13295846,-11719387,-10668243,-10799572,-12376287,-11784923,-10799572,-11784923,-10142670,-9223112,-10142927,-11587801,-14215405,-13295590,-10602450,-12113373,-12244958,-13098725,-12573153,-10865364,-13821162,-13295590,-12441824,-12178910,-11982044,-12638945,-13032932,-12836067,-12901603,-12310495,-10602450,-11062485,-10668242,-9682891,-10077134,-9945805,-10799572,-10865364,-10077134,-10668243,-11982044,-12507616,-13098725,-12704482,-12704482,-13295590,-13032932,-11916252,-12704482,-11259607,-12770274,-12967396,-11587801,-11850715,-10996693,-10930900,-10734035,-11653594,-12244702,-12573153,-12441824,-12573153,-11325144
Data -11719387,-12638945,-12244958,-11128022,-11456472,-12244958,-11719387,-10799572,-10668242,-10865364,-11587801,-10799572,-11982044,-12178910,-11784923,-12507616,-12967396,-12244702,-12244702,-11653594,-11522265,-12376287,-13821418,-12507616,-12441824,-10471121,-13230053,-13689833,-12244958,-11850715,-10142927,-11259607,-11193815,-13295590,-11522265,-12178910,-11916252,-11587801,-10208463,-10930900,-10011342,-11850715,-9354441,-10077134,-10077134,-11982044,-9026247,-11325144,-11193814,-11062485,-13032932,-13230053,-13427175,-11522265,-13295846,-12770274,-13032932,-12244958,-12967396,-12573153,-12967396,-12376287,-11982044,-9682891,-8500675,-10799572,-10602450,-9025990,-10865364,-8960454,-10996693,-10142670,-10405585,-10274256,-10536913,-12507616,-12901603,-12638945,-12770274,-13164261,-11653594,-12244702,-12310495,-12178910,-12770274,-12704482,-10734035,-12967396,-11259607,-11653594,-12244702,-10930900,-12507616,-11719130,-12244958,-12573153
Data -11850715,-11325144,-11390936,-12770274,-11784923,-11850715,-11916252,-10142927,-11982044,-11128022,-10799572,-11916252,-10077134,-11259607,-11193814,-11982044,-12113373,-12770274,-12507616,-13230053,-11916252,-12244958,-11982044,-12244958,-11916252,-11193815,-10734035,-10865364,-12113373,-12244958,-11062485,-10734035,-10536913,-12310495,-12704482,-10339792,-13164261,-11193815,-10930900,-14018284,-11062485,-10208463,-8500418,-8303553,-11128022,-10011342,-11325144,-14149612,-11916252,-10668242,-12967396,-13361639,-13821418,-12441824,-13032932,-11982044,-12047581,-11982044,-11259607,-12836067,-12507616,-11916252,-13164261,-10734035,-9617098,-10077134,-12047581,-11456472,-11259607,-10142927,-10536913,-12638945,-11916252,-11193815,-11719387,-12376287,-11062485,-11850715,-12967396,-11916252,-12441824,-12770274,-11193814,-11062485,-12113373,-13032932,-12704482,-11587801,-11128022,-12573153,-11850715,-11259607,-11259607,-12770274,-11653594,-11390936
Data -12638945,-11784923,-11653594,-11456472,-11587801,-11916252,-12244958,-13098725,-10996693,-10930900,-12310495,-12310495,-10799572,-10996693,-11193815,-10536913,-11916252,-10668242,-9880013,-12244958,-12113373,-12507616,-7909310,-11982044,-9223112,-12047581,-12836067,-13164261,-12967396,-13164261,-11916252,-12770274,-11916252,-11653594,-9617355,-11128022,-10339792,-11653594,-13295590,-11653594,-11193815,-9157575,-8632003,-8960454,-11193815,-11128022,-12047581,-12638945,-11719130,-11522265,-11390936,-9420233,-10996693,-13886955,-11456472,-13492968,-11850715,-13230053,-11456472,-12638945,-10734035,-12507616,-12113373,-9617355,-12836067,-11062485,-10734035,-11850715,-11719130,-11784923,-11325144,-12244702,-12178910,-12441824,-10142670,-10602450,-11719387,-11062485,-11128022,-11653594,-12047581,-11916252,-12901603,-12376287,-12770274,-12770274,-12507616,-11653594,-9814220,-10142927,-12901603,-10734035,-10471121,-11390936,-12047581,-11916252
Data -12310495,-12441824,-11062485,-10471121,-11719130,-11587801,-11259607,-10996693,-11193814,-12047581,-12047581,-11522265,-12310495,-12770274,-11587801,-12770274,-10405585,-10799572,-11390936,-12507616,-12704482,-13492968,-11719387,-10602450,-9880013,-12573153,-12113373,-11390936,-12441824,-12376287,-13361639,-13821418,-12244958,-11325144,-12310495,-13689833,-12507616,-13098725,-11719387,-9682891,-10536913,-13952747,-11522265,-13164261,-10865364,-13558504,-11982044,-10799572,-10536913,-11128022,-9945805,-9026247,-10142927,-11850715,-11719130,-13689833,-12573153,-12901603,-13295846,-12573153,-12178910,-11916252,-10668243,-9814220,-10405585,-9617355,-10602450,-10734035,-11916252,-11916252,-12704482,-12244958,-12573153,-12047581,-10668242,-10405585,-10668243,-12244958,-11916252,-11784923,-11850715,-12507616,-12244958,-12244958,-12638945,-12836067,-12310495,-11916252,-10142927,-12244702,-12441824,-12507616,-10471121,-10339792,-11193815,-12441824
Data -11719130,-12310495,-10668242,-10996693,-10799572,-10668243,-11193815,-11456472,-12507616,-10930900,-11653594,-12244702,-10930900,-11784923,-9157575,-11653594,-12573153,-12770274,-11193815,-11587801,-10536913,-13295846,-10668243,-12770274,-11390936,-10930900,-11128022,-11850715,-12901603,-12244958,-11062485,-13164261,-11456472,-12047581,-13032932,-12244958,-13032932,-12638945,-13230053,-11916252,-13821418,-13689833,-8369089,-9091783,-12310495,-10799572,-11982044,-12573153,-11719130,-11784923,-12113373,-12310495,-10734035,-10668243,-11587801,-11719387,-11522265,-11784923,-11193815,-11719130,-10602450,-11193815,-9682891,-11719387,-11128022,-10536913,-10602450,-10799572,-10799572,-12638945,-12441824,-11653594,-11587801,-12770274,-12376287,-10602450,-11916252,-11653594,-12178910,-11850715,-12507616,-12573153,-12573153,-11259607,-12244702,-11784923,-10208463,-11850715,-9814220,-10142670,-11916252,-10668243,-10077134,-11193815,-10734035,-12310495
Data -11850715,-11390936,-11653594,-10799572,-10930900,-10668243,-10865364,-11719130,-11325144,-11587801,-11719387,-11587801,-10077134,-11916252,-13952747,-12178910,-12310495,-13755625,-8960454,-12244702,-13624296,-10734035,-12244702,-10602450,-11128022,-13164261,-9880013,-10668243,-12310495,-11784923,-11982044,-11325144,-10339792,-10996693,-10930900,-12836067,-12770274,-13098725,-13492968,-10865364,-12967396,-10339792,-9223112,-11259607,-13755625,-10668242,-13361639,-13230053,-13427175,-10799572,-11193814,-11522265,-10930900,-11325144,-10274256,-9288904,-9354441,-10536913,-11719387,-10142670,-11653594,-10471121,-11719130,-11719387,-10930900,-10734035,-12047581,-10865364,-12113373,-12310495,-12770274,-13230053,-13361639,-12441824,-12901603,-12507616,-11719387,-12244958,-12310495,-11325144,-11325144,-11982044,-12244702,-12310495,-12310495,-12770274,-13492968,-12770274,-10011342,-9157575,-10734035,-10536913,-10471121,-10602450,-12113373,-11456472
Data -11128022,-12047581,-10536913,-10471121,-10865364,-10471121,-12376287,-11653594,-10471121,-11719387,-12770274,-12113373,-10142927,-11522265,-10274256,-12113373,-7449530,-13558504,-11982044,-13295590,-12967396,-12178910,-12638945,-10668242,-11390936,-10996693,-11325144,-10996693,-11719130,-12638945,-11259607,-13295590,-10405585,-12113373,-10865364,-13886955,-13492968,-12573153,-8763332,-13164261,-10471121,-10602450,-13821418,-12836067,-11259607,-10077134,-11653594,-12770274,-11390936,-11587801,-10405585,-9682891,-12178910,-12113373,-10865364,-10142670,-10142927,-12244958,-11325144,-9617098,-10996693,-12638945,-10734035,-11982044,-11916252,-9682891,-10799572,-13295590,-11456472,-11784923,-12573153,-13295590,-12113373,-11456472,-12441824,-13361639,-11719130,-10208463,-12573153,-12967396,-11719130,-10142927,-10865364,-11325144,-12047581,-12244702,-12178910,-11982044,-11784923,-9420233,-10865364,-10142670,-11456472,-10339792,-11193814,-12047581
Data -10668243,-11062485,-12507616,-11390936,-10930900,-10930900,-11325144,-12376287,-13624296,-11719130,-11719130,-9551562,-10668243,-11325144,-11719387,-10011342,-12573153,-11456472,-13295846,-12770274,-11784923,-12376287,-10865364,-11062485,-10668243,-11193815,-13821418,-12376287,-11719130,-12638945,-14149612,-13624296,-12770274,-13032932,-11719130,-11784923,-8697540,-12507616,-8763332,-12704482,-13624296,-12836067,-12967396,-10668243,-12573153,-10996693,-11128022,-12244958,-11456472,-10274256,-13098725,-13624296,-12244958,-11128022,-11784923,-10734035,-12770274,-13230053,-12178910,-7843773,-11259607,-10668243,-9880013,-9485770,-10142670,-10930900,-11456472,-10865364,-12310495,-12441824,-11062485,-10996693,-13427175,-13032932,-10930900,-11784923,-12244958,-11850715,-10602450,-12244958,-13427175,-13295590,-12047581,-10536913,-11916252,-10471121,-12310495,-11390936,-11587801,-11850715,-11653594,-10799572,-11719387,-11390936,-12376287,-11193814
Data -10339792,-10142927,-10668242,-10077134,-10734035,-10602450,-12638945,-11259607,-13032932,-10536913,-9223112,-9945805,-11916252,-14149612,-11653594,-10799572,-13624296,-12638945,-13295590,-12770274,-13230053,-12244958,-11916252,-10668243,-12901603,-10865364,-12967396,-12836067,-11259607,-12244958,-14084076,-14412527,-12441824,-11390936,-12967396,-9748684,-13361639,-13164261,-11916252,-10930900,-11982044,-9880013,-12244702,-11653594,-10142927,-11784923,-9814220,-12441824,-14543855,-14346734,-13821162,-12967396,-13755625,-10865364,-10734035,-9223112,-11982044,-11390936,-13361639,-8369089,-8500418,-8040895,-9748684,-10471121,-9091783,-9748684,-10077134,-11587801,-11259607,-12901603,-11982044,-11784923,-12770274,-12901603,-13361639,-11456472,-10930900,-11850715,-12507616,-11259607,-12638945,-12310495,-12113373,-10536913,-10142670,-10668242,-11653594,-10011342,-11653594,-12244958,-11982044,-12310495,-11719387,-11193814,-10799572,-10405585
Data -10602450,-10339792,-12244702,-10536913,-11982044,-11062485,-12704482,-10471121,-12770274,-11522265,-11259607,-11719130,-12376287,-12113373,-10668243,-12836067,-12836067,-13295846,-13624296,-13164261,-11456472,-13295590,-11916252,-9617098,-12704482,-11587801,-13032932,-13427175,-13295590,-13164261,-13295846,-10274256,-10077134,-13821418,-13886955,-12901603,-13295846,-12178910,-11587801,-11390936,-12441824,-11719130,-12113373,-13624296,-12901603,-13098725,-12573153,-13492968,-13689833,-12507616,-13164261,-14740977,-13295846,-11456472,-11587801,-12441824,-9551562,-10011342,-11784923,-10471121,-12310495,-11653594,-9617355,-10142927,-10077134,-10865364,-11390936,-10536913,-11390936,-12507616,-13164261,-12244702,-11587801,-11719130,-12638945,-10865364,-11325144,-11062485,-10668242,-12047581,-11390936,-12573153,-12244958,-11325144,-10668243,-10799572,-10734035,-9617355,-11719387,-12770274,-12704482,-12244958,-12310495,-11522265,-11916252,-10471121
Data -10930900,-11259607,-11193815,-12704482,-12704482,-10668242,-13295846,-11325144,-9682891,-11325144,-9880013,-11719130,-11522265,-13295590,-13361639,-10865364,-12310495,-13361639,-12441824,-10930900,-11325144,-11325144,-11916252,-12704482,-12836067,-11719387,-13886955,-11193814,-12244702,-11193814,-13164261,-10799572,-12836067,-14084076,-14346734,-10339792,-13624296,-11916252,-12507616,-10011342,-13492968,-12244958,-12704482,-12113373,-12178910,-11850715,-10996693,-12770274,-14346734,-12770274,-12507616,-12770274,-13295846,-10996693,-8960454,-9748684,-11456472,-8566211,-7121080,-10142670,-8172224,-7515323,-8763332,-7580860,-8960454,-9354441,-11128022,-11062485,-11719387,-10536913,-9814220,-12770274,-12441824,-12376287,-10930900,-10208463,-12770274,-11653594,-11193815,-11193814,-12638945,-11325144,-12047581,-12441824,-10339792,-11259607,-10208463,-11062485,-11719387,-12770274,-12901603,-11587801,-11719130,-11390936,-10996693,-11062485
Data -10865364,-10602450,-10668243,-10799572,-12178910,-13230053,-12573153,-12770274,-11193814,-11982044,-12901603,-13230053,-13164261,-13230053,-13295846,-12836067,-12967396,-12770274,-13624296,-11128022,-14412270,-10471121,-11916252,-12113373,-11325144,-13295846,-12704482,-12113373,-11128022,-13821162,-11719130,-12901603,-13952747,-14280941,-12573153,-11784923,-11390936,-11719387,-13427175,-12573153,-13032932,-12376287,-12638945,-11719387,-10274256,-8829125,-10011342,-10865364,-11587801,-12967396,-13755625,-12178910,-11062485,-10142927,-11259607,-10274256,-10734035,-9945805,-8303553,-8040895,-9025990,-8040895,-7777981,-8040895,-8434882,-8566211,-10471121,-10536913,-10930900,-12770274,-11850715,-11062485,-12376287,-12836067,-12047581,-10208463,-10865364,-11390936,-10142670,-9880013,-11062485,-12244702,-12178910,-12376287,-10668243,-10668242,-10734035,-12638945,-12310495,-12507616,-12901603,-12113373,-11850715,-11456472,-10996693,-10602450
Data -12113373,-10274256,-9814220,-10142670,-10339792,-13098725,-12836067,-13295590,-11982044,-10996693,-13032932,-12113373,-12310495,-13427175,-13230053,-12573153,-12441824,-13492968,-12113373,-11916252,-13427175,-11982044,-13032932,-14149612,-12507616,-10471121,-10799572,-14018284,-11916252,-10930900,-11653594,-11719387,-14084076,-11784923,-13624296,-10142927,-11456472,-12047581,-13755625,-10142670,-13295846,-11193815,-12441824,-12901603,-11259607,-10799572,-11653594,-10734035,-11062485,-11719387,-12244702,-12178910,-11916252,-12310495,-10996693,-11719387,-10471121,-8303553,-8106432,-7121080,-9748684,-7909310,-8369089,-8106432,-8632003,-9617098,-12244702,-10996693,-10536913,-11259607,-12113373,-11193815,-11193814,-12376287,-12178910,-10865364,-9880013,-9354441,-10799572,-9880013,-9617098,-11982044,-12244958,-12573153,-10405585,-11193814,-12310495,-12113373,-12244958,-12244958,-12836067,-12770274,-11522265,-10536913,-10536913,-10405585
Data -10142927,-9748684,-9814220,-11390936,-10142670,-13624296,-11456472,-12836067,-13032932,-11522265,-12836067,-11062485,-12770274,-12770274,-12507616,-12441824,-13558504,-13164261,-13295590,-14084076,-11259607,-12310495,-11522265,-14149612,-11719387,-10930900,-11653594,-10471121,-12178910,-11916252,-13755625,-13098725,-13821418,-10668242,-12573153,-12638945,-11653594,-13361639,-13558504,-10799572,-12573153,-10668243,-11850715,-11062485,-12441824,-12244958,-12244958,-12244702,-11784923,-12244958,-12244702,-10734035,-9223112,-8894661,-11587801,-8237760,-8369089,-10011342,-10339792,-8763332,-7975102,-7843773,-8500418,-10077134,-10536913,-11719130,-11784923,-9617098,-10208463,-11784923,-11128022,-11062485,-10930900,-11259607,-12836067,-12770274,-11259607,-9354441,-8960454,-8894661,-10996693,-9945805,-12113373,-12113373,-12178910,-12244702,-13492968,-12113373,-11587801,-13230053,-10930900,-11982044,-10668243,-11784923,-10208463,-9945805
Data -10208463,-11259607,-12376287,-11456472,-12836067,-11719387,-12967396,-12704482,-12244958,-12573153,-13361639,-9880013,-12441824,-12573153,-14084076,-13492968,-12573153,-12836067,-12638945,-10996693,-10602450,-12573153,-11456472,-11719387,-12244702,-12967396,-12770274,-11982044,-11062485,-12836067,-14149612,-12901603,-13427175,-12310495,-13427175,-12310495,-13558504,-13164261,-13295846,-12770274,-12244958,-13427175,-13755625,-13427175,-11259607,-12704482,-11784923,-9880013,-10405585,-10142670,-10077134,-10339792,-10142670,-11784923,-10734035,-11587801,-11456472,-11390936,-10077134,-11062485,-8040895,-10208463,-10734035,-10602450,-11193815,-12244958,-11719387,-10142670,-10405585,-12770274,-12836067,-12901603,-10668243,-11128022,-11259607,-11653594,-10668243,-10011342,-8566211,-11259607,-10274256,-10668243,-11259607,-12244958,-12310495,-12244958,-11982044,-11719387,-12178910,-11128022,-10930900,-10274256,-12178910,-10602450,-12047581,-11062485
Data -11850715,-12244958,-12836067,-11522265,-11325144,-12967396,-13427175,-12901603,-12770274,-13164261,-13295590,-12310495,-11719130,-12704482,-14346734,-13821162,-12507616,-11784923,-13098725,-12244958,-14149612,-12770274,-13164261,-13689833,-11259607,-12573153,-13624296,-9485770,-11193815,-11456472,-12244702,-13689833,-12244958,-13492968,-13230053,-12573153,-13821162,-13821162,-12638945,-12178910,-12770274,-11784923,-12178910,-13098725,-12047581,-12901603,-11390936,-10602450,-10208463,-10668243,-10142927,-10142927,-10471121,-10799572,-11916252,-11719387,-11719130,-12507616,-10602450,-11916252,-10668242,-11128022,-9880013,-11193814,-11325144,-12244702,-11522265,-11325144,-10077134,-12376287,-12507616,-12638945,-10274256,-11522265,-10668242,-11325144,-11522265,-10799572,-10142927,-10011342,-9945805,-9880013,-11850715,-12310495,-12047581,-12244958,-11916252,-11784923,-12178910,-10274256,-10602450,-10602450,-11522265,-11062485,-12441824,-12244958
Data -11653594,-11982044,-11982044,-9551562,-11128022,-12047581,-11325144,-12441824,-12113373,-13624296,-13361639,-13427175,-13755625,-13952747,-13689833,-11982044,-12573153,-12770274,-9880013,-12244702,-13492968,-12770274,-12507616,-11390936,-11522265,-10602450,-11653594,-9420233,-10799572,-10208463,-10799572,-12901603,-14280941,-13689833,-14215405,-13164261,-13624296,-13295590,-10536913,-12770274,-14412527,-12770274,-11062485,-13558504,-13164261,-12573153,-9617355,-12310495,-11128022,-12244958,-12310495,-11390936,-12770274,-11390936,-11587801,-12573153,-12310495,-12704482,-11325144,-11522265,-11587801,-10799572,-9814220,-11850715,-12376287,-10930900,-10405585,-12770274,-11456472,-10799572,-11916252,-13098725,-11522265,-10405585,-11456472,-10668242,-12113373,-9551562,-10339792,-9880013,-10142670,-10734035,-12113373,-11784923,-12113373,-11719130,-11522265,-11259607,-11653594,-10799572,-8566211,-8763332,-10471121,-10142670,-11522265,-11719130
Data -9682891,-11128022,-10602450,-10930900,-13361639,-11456472,-12244702,-12901603,-12441824,-13821162,-12836067,-12967396,-13755625,-13361639,-11522265,-11653594,-13295846,-12573153,-11128022,-12244702,-13098725,-12638945,-11784923,-13098725,-13624296,-12178910,-11653594,-9945805,-11587801,-11587801,-11719387,-13032932,-14543855,-13689833,-13164261,-13755625,-13164261,-12244958,-12836067,-13295846,-13427175,-13689833,-12770274,-10405585,-10405585,-12047581,-10077134,-9814220,-10471121,-12638945,-13361639,-12047581,-11587801,-9485770,-11390936,-12441824,-12770274,-11390936,-11719130,-12441824,-12441824,-11522265,-10274256,-10930900,-10799572,-12901603,-12441824,-11193815,-12310495,-12244702,-11784923,-12113373,-11390936,-11128022,-9420233,-11259607,-8894661,-11259607,-10011342,-9682891,-11259607,-12704482,-12376287,-12638945,-11128022,-11062485,-11062485,-10996693,-11193815,-9880013,-7843773,-8500675,-10668242,-10930900,-10405585,-11193815
Data -11325144,-11390936,-9551562,-8829125,-11719130,-11456472,-13032932,-12704482,-12901603,-12244958,-13821162,-13886955,-13164261,-11982044,-10668243,-12310495,-11916252,-13427175,-12967396,-11784923,-12770274,-13295846,-11784923,-12704482,-9814220,-11784923,-11522265,-10930900,-11193814,-9945805,-11850715,-12441824,-14609648,-13689833,-13361639,-11325144,-13230053,-11982044,-12047581,-13689833,-13427175,-12770274,-13558504,-12573153,-13098725,-10799572,-10077134,-11850715,-11982044,-12836067,-12770274,-10011342,-9945805,-9551562,-11390936,-11456472,-11916252,-12507616,-12770274,-13230053,-13295846,-12770274,-9485770,-10930900,-10339792,-12178910,-13032932,-12901603,-10996693,-11653594,-12901603,-12770274,-10602450,-11456472,-11456472,-10339792,-11062485,-12178910,-10142670,-11719387,-12441824,-12244958,-12376287,-12836067,-10996693,-10339792,-10734035,-10668242,-11193815,-10536913,-9091783,-11259607,-11193814,-10996693,-9617098,-11587801
Data -10274256,-10668242,-11456472,-9880013,-10011342,-12310495,-13361639,-13492968,-13427175,-12770274,-13295846,-12836067,-11916252,-10865364,-11784923,-11587801,-11062485,-13361639,-12244958,-12704482,-11982044,-11719130,-11390936,-10339792,-10471121,-12573153,-12047581,-10536913,-8697540,-10602450,-13295846,-14412270,-11587801,-13032932,-12638945,-13952747,-11982044,-11390936,-13624296,-12113373,-12770274,-12113373,-13558504,-13295846,-13361639,-10668242,-11850715,-11719130,-9420233,-10799572,-11522265,-10930900,-11784923,-12047581,-9617355,-10930900,-12573153,-11719387,-10471121,-13032932,-12113373,-11719130,-12244958,-12244958,-11587801,-11587801,-11982044,-10734035,-10668242,-10668242,-10602450,-11062485,-9223112,-10274256,-12967396,-11062485,-10668243,-9223112,-11850715,-12376287,-12638945,-12441824,-12638945,-12836067,-11982044,-11062485,-10339792,-9945805,-10734035,-11850715,-9748684,-12507616,-11128022,-10734035,-11982044,-10799572
Data -9617098,-8960454,-9617098,-10142670,-9091783,-13361639,-13821162,-12507616,-12310495,-11456472,-12836067,-11719387,-11062485,-11128022,-11193815,-12770274,-13164261,-13886955,-13427175,-11982044,-12704482,-11390936,-12638945,-12310495,-11850715,-12376287,-12047581,-10077134,-10142927,-13886955,-12244958,-11390936,-12638945,-13361639,-12770274,-11193815,-10602450,-12770274,-12507616,-12178910,-9748684,-12507616,-11916252,-14149612,-10996693,-11916252,-11784923,-12244702,-10208463,-12113373,-10734035,-9748684,-10142927,-10536913,-10536913,-11062485,-11193815,-10471121,-12770274,-12573153,-12047581,-11719387,-11193814,-11259607,-10668242,-11522265,-9748684,-10208463,-13098725,-10799572,-9354441,-9945805,-10668242,-11719387,-10734035,-11325144,-12770274,-10668243,-10339792,-12376287,-9945805,-12770274,-12244958,-11193814,-12441824,-10536913,-10799572,-10077134,-12507616,-11325144,-10077134,-10471121,-9945805,-10602450,-10405585,-9288904
Data -10142927,-10536913,-9814220,-10668243,-9748684,-12507616,-13624296,-12244958,-12441824,-11259607,-13689833,-12376287,-11259607,-11193814,-11653594,-14215405,-12244702,-11850715,-13952747,-12770274,-12244958,-13821162,-10668242,-10471121,-9485770,-10339792,-10799572,-9814220,-11522265,-11128022,-11719387,-10996693,-13230053,-11916252,-12178910,-12244702,-11259607,-12310495,-12244958,-12901603,-11653594,-12507616,-11719130,-10668242,-11916252,-12178910,-12244702,-7975102,-6989751,-8369089,-9485770,-9223112,-8763332,-11522265,-10536913,-10011342,-9551562,-11128022,-11784923,-12178910,-12310495,-11784923,-10471121,-10930900,-10734035,-12836067,-12113373,-11193814,-12441824,-9485770,-9157575,-8763332,-9617098,-10208463,-9026247,-9617098,-11916252,-13821418,-10930900,-12770274,-10930900,-12376287,-12704482,-10668243,-12507616,-11522265,-11522265,-10274256,-11850715,-12638945,-10668242,-10471121,-10077134,-10930900,-9880013,-10536913
Data -11653594,-10930900,-11259607,-12441824,-9617355,-12376287,-13032932,-13821418,-12770274,-12244958,-12770274,-11784923,-11128022,-11719130,-13098725,-13886955,-12967396,-14149612,-13689833,-13427175,-10142670,-12376287,-14675184,-11062485,-10996693,-11850715,-10602450,-11653594,-11719387,-13821162,-12638945,-13689833,-12244702,-13032932,-11456472,-11062485,-10471121,-11062485,-11850715,-12178910,-12967396,-11719387,-13032932,-11719387,-10734035,-11587801,-9157575,-10996693,-11719130,-11193814,-9091783,-10536913,-11916252,-10734035,-11259607,-11390936,-10339792,-10930900,-9617098,-11587801,-11653594,-12244958,-10865364,-10471121,-10405585,-10142670,-10734035,-10930900,-10471121,-10405585,-9617355,-9025990,-9945805,-10274256,-10930900,-10339792,-10930900,-11784923,-13689833,-11719387,-13361639,-11784923,-11719130,-11259607,-10536913,-12704482,-11456472,-12244958,-10471121,-11325144,-12507616,-12704482,-10471121,-11916252,-11128022,-11062485
Data -11653594,-10865364,-9091783,-13558504,-12178910,-13952747,-13821162,-13821162,-12901603,-13821162,-11982044,-11587801,-13164261,-13032932,-13098725,-12376287,-12638945,-12704482,-13427175,-12244702,-11062485,-12901603,-12770274,-11784923,-10011342,-10405585,-10536913,-12441824,-13295590,-11522265,-12178910,-11719130,-12047581,-13295590,-11982044,-13032932,-12770274,-12638945,-13361639,-11653594,-12638945,-13164261,-10734035,-11193814,-12901603,-10996693,-9288904,-6792630,-7909310,-8500418,-12770274,-12441824,-11916252,-10865364,-12113373,-11456472,-10668243,-10536913,-12047581,-12178910,-10142670,-9420233,-11193814,-10536913,-10536913,-10865364,-9945805,-9288904,-9814220,-9026247,-9682891,-8960454,-10142670,-10274256,-11784923,-12770274,-12244958,-10865364,-12507616,-14149612,-9551562,-12244702,-10339792,-11653594,-9223112,-10011342,-10405585,-12704482,-11587801,-12244702,-11193815,-11193814,-12376287,-11587801,-9551562,-10668243
Data -12704482,-10339792,-8040895,-13624296,-13624296,-14412270,-13558504,-12507616,-13361639,-11719387,-11390936,-12441824,-13295590,-13098725,-12441824,-12047581,-11916252,-14543855,-12901603,-10405585,-12901603,-13755625,-13492968,-13558504,-9748684,-10668242,-10930900,-13361639,-13492968,-12310495,-12178910,-12901603,-12638945,-12836067,-13295590,-13098725,-14609648,-13755625,-12244702,-12704482,-13098725,-13886955,-12113373,-12441824,-13952747,-12770274,-10208463,-8303553,-9026247,-8829125,-8894661,-11325144,-11653594,-10274256,-12507616,-10602450,-9814220,-11193814,-13098725,-12704482,-12047581,-11916252,-11784923,-11062485,-10668242,-10142670,-10011342,-9420233,-9288904,-9617098,-11390936,-9551562,-11390936,-10471121,-12441824,-12573153,-12704482,-12441824,-12507616,-14149612,-11128022,-10865364,-10142670,-10668243,-8369089,-10602450,-10077134,-12573153,-12836067,-11916252,-11784923,-11062485,-13098725,-11982044,-8500675,-10208463
Data -11062485,-12244702,-11982044,-13295590,-13755625,-13689833,-11719130,-11587801,-11916252,-13558504,-12441824,-11784923,-12244958,-12507616,-13624296,-14412270,-13689833,-12770274,-11325144,-12376287,-10668242,-11456472,-12507616,-11784923,-10996693,-9945805,-9814220,-11719387,-14149612,-12770274,-11653594,-13558504,-14084076,-10930900,-12178910,-9157575,-13230053,-12244958,-12507616,-13492968,-14346734,-14675184,-9748684,-9682891,-10142670,-10077134,-10799572,-12638945,-12638945,-11193814,-10734035,-11390936,-10536913,-10865364,-11259607,-12310495,-8763332,-9814220,-10208463,-11719387,-12441824,-12310495,-12113373,-11719130,-11259607,-8829125,-10077134,-11587801,-9025990,-10668242,-9288904,-10339792,-12047581,-13886955,-11128022,-12967396,-10142670,-13098725,-10208463,-12770274,-13624296,-10668242,-11719387,-8894661,-11784923,-13821418,-10142670,-11062485,-11916252,-9617355,-11390936,-11719130,-12507616,-12113373,-11325144,-12113373
Data -9617355,-11982044,-10865364,-13755625,-13427175,-11916252,-11850715,-11522265,-12244702,-13032932,-13164261,-13230053,-11719130,-11784923,-12770274,-13230053,-11325144,-12244958,-12770274,-10996693,-12113373,-10077134,-12244958,-11982044,-11193814,-10142927,-10011342,-12836067,-13558504,-13295590,-12638945,-13098725,-11982044,-13492968,-12047581,-10734035,-13821418,-14478319,-14478319,-14478319,-14149612,-14018284,-13295846,-11390936,-11982044,-11982044,-11193815,-10471121,-9420233,-9748684,-10142670,-10536913,-8566211,-10471121,-10405585,-11653594,-12244702,-11719387,-10930900,-10668243,-11325144,-12244702,-13361639,-9945805,-11390936,-11587801,-10405585,-13164261,-11390936,-9485770,-10734035,-11390936,-11062485,-12704482,-11390936,-13230053,-12178910,-11062485,-11193814,-9617098,-14478319,-12113373,-10865364,-9420233,-9682891,-11128022,-10142927,-10471121,-10077134,-11325144,-10602450,-11850715,-12573153,-13361639,-11062485,-11653594
Data -9748684,-11193814,-11062485,-13689833,-13952747,-11128022,-12047581,-11850715,-11850715,-14609648,-12770274,-12770274,-11850715,-11719130,-12376287,-13295846,-11850715,-11653594,-11259607,-11916252,-13230053,-10405585,-11653594,-9748684,-10668242,-10602450,-11325144,-12441824,-12310495,-11916252,-9682891,-8632003,-13032932,-13032932,-11587801,-10077134,-14018284,-10471121,-13295846,-13558504,-13427175,-13164261,-13689833,-11719130,-10930900,-11850715,-12047581,-11325144,-9617355,-10077134,-10011342,-9354441,-9354441,-10339792,-10405585,-9880013,-11193814,-11784923,-11784923,-12704482,-11456472,-11193815,-12901603,-10339792,-11456472,-12310495,-11193814,-11062485,-11325144,-10536913,-11850715,-12507616,-12376287,-12507616,-13821418,-10208463,-9617355,-13164261,-13558504,-10668243,-13755625,-13295590,-11982044,-10734035,-10668243,-9682891,-11719387,-9814220,-10865364,-11587801,-11193815,-11325144,-11916252,-12836067,-11259607,-10996693
Data -12047581,-10930900,-11916252,-12047581,-11719130,-11587801,-11850715,-12310495,-12113373,-13427175,-12441824,-13427175,-11259607,-11982044,-12638945,-13295846,-10865364,-13755625,-10471121,-11522265,-10602450,-10405585,-10471121,-9485770,-12441824,-12310495,-12113373,-13821162,-14149612,-13295590,-10077134,-11325144,-12441824,-12178910,-11850715,-11062485,-10405585,-9288904,-11784923,-13164261,-12704482,-8303553,-12638945,-12507616,-11784923,-11456472,-10734035,-10996693,-10799572,-11719387,-12310495,-9748684,-9617098,-8960454,-9945805,-10405585,-11259607,-11784923,-9945805,-9551562,-13164261,-13164261,-12441824,-8237760,-9814220,-11456472,-13689833,-12967396,-10339792,-13295590,-10865364,-11719130,-11850715,-9617355,-12441824,-13755625,-11719387,-12770274,-14412270,-11587801,-10799572,-14346734,-14412270,-12441824,-10077134,-11259607,-11587801,-10339792,-10996693,-12507616,-9945805,-9617355,-11062485,-12770274,-10799572,-10734035
Data -11850715,-13098725,-12901603,-11522265,-11259607,-11784923,-12967396,-13821418,-14412270,-12047581,-12310495,-12113373,-12573153,-12441824,-11587801,-13821162,-13164261,-12704482,-12244958,-11128022,-9485770,-9748684,-9354441,-13098725,-11456472,-11587801,-12704482,-12967396,-12836067,-12178910,-12441824,-10077134,-12244702,-13295846,-12047581,-9617355,-10799572,-9551562,-9748684,-11325144,-11719387,-7777981,-10142927,-11456472,-11193815,-11456472,-11653594,-9420233,-10274256,-10536913,-11916252,-10930900,-9814220,-11259607,-9880013,-8040895,-9617355,-10011342,-11587801,-12376287,-10274256,-9223112,-10668242,-9945805,-9682891,-11456472,-11522265,-12244702,-12836067,-12770274,-13230053,-11653594,-11916252,-10274256,-11456472,-11982044,-14543855,-14412527,-13032932,-13032932,-12310495,-14084076,-14412270,-13755625,-12967396,-11653594,-10142670,-11916252,-10142927,-10536913,-12836067,-10471121,-10536913,-11719387,-12441824,-12901603
Data -12244958,-12047581,-12507616,-13624296,-11390936,-11719130,-13098725,-12638945,-13689833,-11719130,-11719130,-12244958,-12901603,-12638945,-12770274,-13821418,-12770274,-12047581,-11784923,-11719130,-9814220,-9025990,-10471121,-10799572,-10208463,-13164261,-13492968,-10602450,-12113373,-11719130,-11259607,-12836067,-8500675,-12113373,-11916252,-9617098,-13821418,-11522265,-13032932,-10536913,-10930900,-9288904,-12113373,-12704482,-10077134,-9945805,-10602450,-11522265,-10142927,-11325144,-10799572,-11916252,-10930900,-10208463,-8697540,-7646652,-10602450,-11259607,-12244958,-10536913,-11653594,-11390936,-10536913,-12113373,-11719130,-9880013,-11982044,-12178910,-12573153,-10471121,-13230053,-11062485,-10930900,-13164261,-11719387,-10208463,-11653594,-14346734,-11719130,-13624296,-12638945,-14872306,-12770274,-13886955,-11193814,-11784923,-10668243,-11456472,-7515323,-11325144,-11982044,-9617355,-11916252,-12507616,-12376287,-12113373
Data -11719387,-11390936,-14280941,-11522265,-11719387,-12967396,-13558504,-13164261,-13558504,-14543855,-11719387,-13624296,-11916252,-13295590,-13624296,-14740977,-12704482,-12310495,-10799572,-12178910,-12836067,-12507616,-11062485,-11193814,-11259607,-13492968,-14543855,-11916252,-9814220,-11916252,-10930900,-11982044,-13952747,-10208463,-12047581,-14346734,-12704482,-12376287,-12967396,-11193814,-9617098,-12836067,-12310495,-11982044,-12573153,-12507616,-11719387,-12310495,-12047581,-12638945,-10339792,-12244702,-12836067,-11522265,-12704482,-10471121,-10471121,-8172224,-10405585,-10668243,-9617355,-10734035,-11784923,-11325144,-11062485,-11062485,-11456472,-13032932,-10668242,-11719387,-11128022,-13295846,-10339792,-12573153,-9945805,-10339792,-9748684,-13689833,-12441824,-13427175,-12770274,-12113373,-12638945,-13886955,-12047581,-11850715,-11784923,-9814220,-9551562,-10668243,-9617098,-13032932,-12310495,-12113373,-13295590,-11653594
Data -10996693,-14937842,-12770274,-12507616,-12770274,-13230053,-13295590,-13098725,-13886955,-14478319,-14018284,-12244958,-14084076,-13952747,-11850715,-12638945,-12901603,-12113373,-10668242,-11916252,-10799572,-12836067,-12178910,-10799572,-14215405,-10668243,-11719387,-11982044,-9682891,-12967396,-12113373,-11587801,-12836067,-14478319,-12244702,-13295590,-12178910,-11982044,-12047581,-11916252,-13689833,-11456472,-13952747,-11916252,-13164261,-12244958,-12178910,-10274256,-13689833,-12967396,-9617098,-11522265,-10865364,-11850715,-11259607,-10996693,-12507616,-12244958,-9157575,-8237760,-9025990,-9617098,-11062485,-11719130,-11390936,-11850715,-11522265,-14346734,-10077134,-10668242,-12967396,-13952747,-11193815,-12244958,-10734035,-12244958,-10142670,-11522265,-13624296,-13361639,-14149612,-11850715,-13295590,-14609648,-12376287,-11784923,-12770274,-13361639,-10339792,-10668243,-7383738,-11916252,-11982044,-12244702,-12573153,-14412527
Data -12967396,-13689833,-12770274,-12638945,-12310495,-12836067,-14346734,-14018284,-13492968,-13689833,-12901603,-13821418,-14018284,-13952747,-12244702,-11128022,-11587801,-10602450,-9814220,-14215405,-11193814,-11193815,-11456472,-12178910,-13361639,-12441824,-10405585,-13361639,-10536913,-12770274,-13098725,-12967396,-10865364,-13032932,-13295590,-13032932,-12770274,-13821162,-13492968,-11784923,-12178910,-10930900,-12441824,-13427175,-12376287,-11456472,-12704482,-12901603,-12507616,-11850715,-10930900,-12047581,-12244958,-11719130,-12113373,-11587801,-11916252,-11719387,-10668243,-11325144,-12244958,-10536913,-11653594,-10602450,-12376287,-12441824,-12638945,-14412270,-11193815,-12770274,-13361639,-11522265,-13361639,-12244958,-11259607,-13361639,-11522265,-10339792,-13755625,-14280941,-13952747,-12113373,-13755625,-13558504,-13295846,-12244702,-12376287,-12113373,-12704482,-10930900,-10142927,-13821418,-12770274,-12638945,-12836067,-13230053
Data -12178910,-13295590,-13821418,-12310495,-11522265,-11982044,-14412270,-14149612,-13952747,-13755625,-13558504,-14478319,-13427175,-12704482,-12573153,-10405585,-12113373,-11982044,-13427175,-12376287,-11522265,-10602450,-12441824,-13295590,-13295590,-12770274,-10865364,-12901603,-13689833,-11259607,-12244958,-12573153,-13098725,-11916252,-12573153,-13558504,-13295590,-13032932,-13032932,-12704482,-11719130,-11982044,-12113373,-10405585,-13295846,-9091783,-11193814,-12507616,-12244702,-11522265,-10536913,-12573153,-12047581,-9223112,-10602450,-12310495,-11784923,-11456472,-9091783,-10536913,-12244958,-11850715,-11259607,-10602450,-10865364,-13755625,-13098725,-12901603,-13886955,-13492968,-13427175,-10930900,-13164261,-8829125,-11325144,-13952747,-13295590,-12836067,-13624296,-13689833,-12901603,-12836067,-13624296,-13821162,-13295590,-12704482,-11259607,-11850715,-11784923,-9157575,-13952747,-13492968,-11982044,-12770274,-13821162,-12901603
Data -12836067,-13230053,-13361639,-11916252,-12310495,-12573153,-13032932,-13295846,-13427175,-14543855,-14084076,-12113373,-12901603,-11193815,-10930900,-10668243,-10142927,-11982044,-11390936,-10405585,-12047581,-13755625,-13230053,-11719130,-11456472,-12113373,-12901603,-13098725,-13558504,-12507616,-11587801,-11982044,-13624296,-11719387,-11784923,-13427175,-12704482,-10865364,-10668243,-10734035,-10208463,-11259607,-11916252,-10405585,-9682891,-12836067,-10274256,-10208463,-11062485,-12704482,-11128022,-10996693,-9223112,-10668243,-11916252,-10799572,-13821418,-10274256,-9354441,-12836067,-11193815,-12244958,-11522265,-10996693,-10142670,-13427175,-13755625,-14346734,-14806513,-12441824,-13361639,-10996693,-13624296,-8303553,-9814220,-12310495,-13164261,-13032932,-13098725,-13821162,-13492968,-13821418,-14018284,-12507616,-12770274,-12507616,-13755625,-8697540,-11916252,-8960454,-11850715,-13295590,-13558504,-12178910,-13098725,-13230053
Data -12836067,-12836067,-12244958,-12704482,-14280941,-12376287,-13821418,-14149612,-13821162,-13624296,-14412527,-13361639,-11719387,-9682891,-9617098,-12113373,-10734035,-10799572,-11916252,-11719387,-11916252,-12967396,-11193815,-12441824,-11128022,-11522265,-11982044,-12638945,-9551562,-12901603,-12376287,-10602450,-11587801,-10668242,-10668242,-13821418,-12113373,-11850715,-9880013,-14018284,-12770274,-11587801,-10011342,-8237760,-11325144,-10405585,-9223112,-11062485,-10734035,-11719387,-10668243,-10471121,-11259607,-12244702,-11193814,-11916252,-13361639,-11325144,-9682891,-12244702,-10734035,-11390936,-11784923,-11128022,-11390936,-13361639,-14346734,-14084076,-14346734,-14149612,-13821162,-12573153,-13295846,-9420233,-10536913,-11653594,-13230053,-13295846,-12441824,-13821162,-13427175,-13821418,-13886955,-13164261,-13230053,-12113373,-12441824,-9354441,-10077134,-10339792,-13427175,-12967396,-13098725,-12638945,-12047581,-12836067
Data -11982044,-12901603,-10274256,-11916252,-13032932,-12638945,-13427175,-13624296,-14412270,-11193815,-13164261,-11325144,-8369089,-10996693,-10865364,-11784923,-11456472,-11128022,-11522265,-11193815,-10602450,-14280941,-13492968,-10405585,-13755625,-12770274,-12113373,-12573153,-12638945,-12047581,-11719387,-13821418,-12836067,-12178910,-13427175,-10077134,-13098725,-11653594,-11719130,-10799572,-12244702,-11062485,-9485770,-8697540,-9091783,-9748684,-9025990,-9880013,-10996693,-11522265,-11522265,-12310495,-12113373,-12573153,-13427175,-11259607,-10274256,-9682891,-9223112,-13032932,-11719387,-13098725,-10865364,-10077134,-10602450,-13558504,-12441824,-13689833,-13689833,-14346734,-14412527,-12441824,-12573153,-8697540,-9814220,-12967396,-14149612,-14149612,-12770274,-13886955,-12376287,-12967396,-13886955,-12638945,-12573153,-11128022,-11587801,-12310495,-13624296,-12901603,-14675184,-11325144,-12770274,-12047581,-11325144,-13032932
Data -12047581,-13624296,-11850715,-12704482,-12441824,-12836067,-10930900,-13821162,-13492968,-12376287,-13098725,-10602450,-11193814,-11062485,-9814220,-10142927,-12244702,-10996693,-11522265,-11390936,-14872306,-13558504,-12244958,-11522265,-11850715,-12244958,-13295590,-11982044,-10865364,-11193814,-13427175,-12376287,-12638945,-12376287,-8829125,-11522265,-11193815,-12310495,-11390936,-11916252,-11259607,-13032932,-13032932,-12704482,-9485770,-12178910,-11850715,-10471121,-11587801,-11784923,-10734035,-12376287,-11982044,-10405585,-13295590,-13558504,-12836067,-11456472,-11982044,-10536913,-14872306,-11193814,-10471121,-10077134,-12704482,-13492968,-13755625,-13032932,-14149612,-14412270,-14149612,-14018284,-12770274,-11325144,-9880013,-12770274,-13492968,-13295590,-13164261,-12638945,-12244702,-12441824,-11719387,-13361639,-12376287,-12047581,-11719130,-11390936,-12507616,-12770274,-11784923,-11522265,-12310495,-11719387,-12113373,-13624296
Data -12704482,-14806513,-12967396,-12638945,-11062485,-12113373,-13427175,-14675184,-12704482,-11784923,-11653594,-10142927,-11653594,-9617098,-10142927,-10142670,-12113373,-9814220,-12770274,-12704482,-13755625,-11325144,-11325144,-10274256,-10996693,-10208463,-12836067,-11456472,-12113373,-13558504,-12244958,-13295590,-12901603,-9814220,-12047581,-11522265,-10536913,-10930900,-12573153,-9420233,-10011342,-12770274,-13689833,-12770274,-11916252,-12573153,-12441824,-11193815,-11456472,-11193815,-11982044,-11390936,-9880013,-11587801,-11062485,-11062485,-13689833,-14280941,-15003635,-11062485,-14018284,-12178910,-11653594,-12376287,-13361639,-14346734,-14675184,-14280941,-14084076,-14609648,-14412270,-13952747,-14346734,-10865364,-10602450,-12638945,-13821418,-13164261,-11325144,-12967396,-13164261,-12770274,-12376287,-12901603,-11325144,-11982044,-12967396,-12376287,-12507616,-14084076,-11784923,-11916252,-12178910,-11982044,-12573153,-14478319
Data -12507616,-13492968,-12836067,-11653594,-10734035,-12244958,-10996693,-13295846,-13164261,-9485770,-10471121,-11193815,-12573153,-9814220,-9354441,-10077134,-11916252,-14018284,-14609648,-14412527,-13164261,-12047581,-12047581,-11587801,-11128022,-13689833,-11719130,-13230053,-12047581,-13295846,-12967396,-11719130,-11916252,-12244702,-11719387,-12638945,-13886955,-12310495,-11916252,-10865364,-11390936,-13164261,-12704482,-11587801,-10734035,-12310495,-11390936,-10471121,-11456472,-12244958,-11916252,-12376287,-13164261,-10865364,-11128022,-10865364,-13361639,-14412270,-14412527,-11719387,-12967396,-13689833,-13295846,-12967396,-14084076,-14543855,-14412527,-14280941,-13886955,-13821162,-13886955,-14084076,-13295590,-12704482,-10734035,-13164261,-12047581,-11719387,-12770274,-13492968,-13755625,-13492968,-12507616,-13098725,-12770274,-11719387,-12376287,-12441824,-12113373,-12967396,-11587801,-12113373,-12441824,-13164261,-13295590,-13361639
Data -12507616,-12901603,-12441824,-12244702,-12244958,-13032932,-12573153,-11850715,-9945805,-11522265,-9682891,-10996693,-12244958,-9617098,-9091783,-11259607,-9880013,-11850715,-13295590,-14346734,-9551562,-13821418,-11522265,-10668242,-12507616,-13558504,-11784923,-12376287,-12047581,-12836067,-13098725,-11850715,-11587801,-13821418,-10011342,-11916252,-11587801,-10142927,-11456472,-11390936,-12441824,-11193815,-11982044,-11456472,-11325144,-11456472,-13689833,-9814220,-8697540,-7909310,-11587801,-10734035,-9617098,-9945805,-11193815,-10668242,-14543855,-14675184,-13821418,-10799572,-12178910,-13098725,-11982044,-12178910,-12573153,-14084076,-14675184,-14872306,-14280941,-12441824,-11325144,-12376287,-12638945,-12770274,-12113373,-13032932,-11719387,-10996693,-11259607,-12113373,-12376287,-12901603,-12770274,-13295846,-12047581,-11982044,-12244702,-13689833,-12113373,-11259607,-12770274,-13098725,-12441824,-12573153,-12770274,-12836067
Data -13492968,-11193815,-11719387,-12310495,-11325144,-11916252,-10734035,-10865364,-11587801,-9354441,-11653594,-11522265,-13624296,-10011342,-9288904,-10208463,-11653594,-10930900,-12573153,-12573153,-10077134,-10996693,-11587801,-11784923,-12244702,-14084076,-12507616,-12507616,-12113373,-14478319,-11325144,-13295590,-12376287,-12770274,-12376287,-12901603,-10865364,-12967396,-12178910,-10405585,-11193814,-10930900,-12310495,-12770274,-11719130,-13755625,-13558504,-9682891,-7777981,-7909310,-8763332,-10339792,-11390936,-11128022,-11522265,-12244702,-10865364,-11325144,-10536913,-11850715,-12836067,-13492968,-10865364,-11850715,-10996693,-13689833,-13886955,-14280941,-14215405,-11062485,-10602450,-11653594,-12376287,-12901603,-12967396,-13230053,-13098725,-12967396,-11784923,-12310495,-12113373,-13164261,-11193815,-12376287,-12376287,-12178910,-12113373,-12704482,-12573153,-12967396,-13164261,-12836067,-12573153,-12376287,-11916252,-11653594
Data -12376287,-11719387,-11850715,-12770274,-12047581,-11390936,-12047581,-12047581,-9617098,-12441824,-12178910,-11719130,-10865364,-12244958,-11193814,-12113373,-11653594,-12573153,-10734035,-10274256,-11587801,-12047581,-11193815,-11719387,-11916252,-12836067,-12441824,-11719130,-12901603,-11719130,-11390936,-11916252,-10930900,-13230053,-11784923,-12441824,-11456472,-10405585,-10799572,-12770274,-11062485,-11982044,-11719130,-12113373,-10339792,-9814220,-9551562,-8040895,-8237760,-8434882,-10142927,-11062485,-11128022,-12047581,-10865364,-13755625,-12770274,-12310495,-12573153,-13821162,-13755625,-13755625,-13952747,-12244958,-10405585,-9551562,-14412270,-14280941,-12178910,-10536913,-10405585,-13755625,-13098725,-12704482,-12441824,-12638945,-12244702,-11784923,-12310495,-11982044,-12244958,-13558504,-12573153,-13295590,-13164261,-12573153,-12770274,-12901603,-13886955,-13821162,-13295590,-12244958,-12901603,-12507616,-11982044,-11653594
Data -13689833,-10996693,-11128022,-12770274,-11982044,-12770274,-13295846,-12047581,-11719387,-10602450,-10668242,-11653594,-11062485,-11325144,-11850715,-12836067,-13755625,-13427175,-11456472,-10339792,-9945805,-12310495,-10274256,-10274256,-11128022,-12770274,-12573153,-11325144,-11982044,-11916252,-9748684,-11719387,-13230053,-12573153,-12507616,-11456472,-11193814,-11062485,-11587801,-12178910,-10799572,-12441824,-12770274,-11916252,-11653594,-13361639,-9814220,-8697540,-11193814,-9288904,-9025990,-11193814,-10930900,-11653594,-10339792,-11522265,-13295846,-13098725,-13755625,-14149612,-14018284,-14346734,-13886955,-9223112,-10011342,-10142927,-10930900,-11193815,-11653594,-10734035,-13624296,-14084076,-14149612,-14346734,-10734035,-13689833,-11193814,-10930900,-12178910,-13821162,-11193815,-12638945,-14280941,-12178910,-13886955,-13821162,-13427175,-13689833,-13295846,-12770274,-11653594,-11587801,-11653594,-11587801,-11916252,-10930900
Data -11916252,-13624296,-11522265,-11522265,-11325144,-12901603,-11916252,-11522265,-9420233,-10930900,-11390936,-10536913,-12638945,-11916252,-11128022,-13821418,-13821162,-11128022,-12244702,-10536913,-9551562,-11784923,-11325144,-10471121,-11587801,-12770274,-11982044,-10996693,-10799572,-11128022,-10536913,-11259607,-10142927,-10734035,-10668243,-11062485,-9617098,-10668243,-10799572,-11982044,-12047581,-11193814,-12704482,-12178910,-12704482,-11850715,-9223112,-9420233,-11456472,-8632003,-9880013,-12376287,-12244958,-13821162,-12704482,-12507616,-11850715,-12244702,-14084076,-14346734,-14018284,-14084076,-12770274,-10668242,-10865364,-11587801,-11587801,-9945805,-10734035,-12244702,-14215405,-11653594,-11193814,-11587801,-13032932,-11982044,-11850715,-11719387,-12441824,-13558504,-12244702,-14412527,-13821418,-13492968,-13098725,-14215405,-14018284,-13689833,-13952747,-11784923,-12376287,-11982044,-10668243,-12310495,-12047581,-13295846
Data -13295846,-10274256,-10405585,-12113373,-11456472,-12638945,-11719387,-12113373,-12244702,-11456472,-10668243,-9880013,-13558504,-12901603,-11784923,-13886955,-11719130,-14018284,-12901603,-11325144,-12178910,-10405585,-11719387,-13098725,-11719387,-9091783,-11456472,-13098725,-9288904,-11259607,-12244702,-12770274,-11982044,-11982044,-10339792,-8960454,-11982044,-10142670,-10274256,-12047581,-12244958,-12244958,-10602450,-11522265,-12507616,-12244958,-11784923,-9880013,-11522265,-11259607,-12113373,-12836067,-12770274,-13361639,-12310495,-13689833,-11719387,-11916252,-12507616,-14478319,-15003635,-13624296,-14872306,-12770274,-12376287,-13230053,-11325144,-8894661,-12244958,-12047581,-12704482,-9288904,-11325144,-11719130,-11719130,-10799572,-13164261,-13689833,-12507616,-13164261,-13032932,-12376287,-13689833,-14543855,-13295846,-13295846,-13230053,-13558504,-12770274,-11719130,-11325144,-11982044,-11390936,-11587801,-11128022,-10668243
Data -13558504,-9288904,-12047581,-12178910,-12638945,-12770274,-12244958,-11522265,-12047581,-12244958,-11128022,-14412527,-12441824,-12310495,-11325144,-13821418,-13295846,-11522265,-12573153,-11062485,-11325144,-10602450,-12047581,-12901603,-12441824,-12310495,-12310495,-9880013,-9814220,-12770274,-11850715,-10668242,-11193815,-11193815,-12836067,-11719130,-10471121,-12836067,-10142927,-10865364,-10471121,-12836067,-11719130,-10930900,-10471121,-11522265,-11522265,-12047581,-11062485,-12967396,-13164261,-12441824,-12507616,-13624296,-10996693,-9354441,-12244958,-11653594,-10208463,-10996693,-11259607,-10668243,-14346734,-14412270,-12704482,-9420233,-12113373,-12113373,-11653594,-13098725,-12836067,-10142927,-11850715,-11522265,-12441824,-13624296,-11062485,-12901603,-14018284,-13821162,-14215405,-13032932,-12704482,-13427175,-13755625,-14018284,-13952747,-12770274,-11719130,-12770274,-12244702,-12836067,-12376287,-12376287,-12244958,-9617098
Data -11719387,-9223112,-12310495,-13295846,-13295590,-12836067,-12638945,-12770274,-12244958,-9485770,-12967396,-12244702,-11587801,-13098725,-13427175,-10734035,-10865364,-13295590,-11916252,-12836067,-11916252,-13361639,-12244958,-12770274,-10668243,-13886955,-11653594,-12047581,-8763332,-12901603,-11522265,-11916252,-12244702,-9880013,-12967396,-11522265,-11719130,-13492968,-11587801,-10602450,-10865364,-12178910,-10668242,-11390936,-11456472,-10734035,-10865364,-12638945,-13295846,-13821162,-13032932,-11916252,-11719387,-13427175,-13755625,-14149612,-12047581,-8960454,-10142927,-9420233,-13032932,-11719387,-12244702,-14149612,-12770274,-10668242,-12376287,-11587801,-11653594,-12638945,-10668243,-11784923,-10799572,-13230053,-13558504,-14412527,-13032932,-12901603,-13427175,-13098725,-13755625,-13689833,-13032932,-13821162,-12836067,-12507616,-11982044,-11784923,-12113373,-11653594,-12244702,-13427175,-12573153,-12836067,-12310495,-9288904
Data -12310495,-11850715,-12638945,-12836067,-12441824,-11522265,-12967396,-13755625,-12441824,-13492968,-13689833,-10536913,-9945805,-10930900,-13821162,-11325144,-11982044,-11456472,-11982044,-11193814,-12047581,-10996693,-12836067,-11193815,-12638945,-11850715,-11062485,-10668243,-10536913,-12573153,-10339792,-11128022,-11916252,-10142670,-11719130,-11653594,-12573153,-11193815,-11259607,-11390936,-10602450,-11128022,-14280941,-13032932,-11522265,-11193814,-10734035,-14280941,-12441824,-13558504,-13098725,-11916252,-12178910,-13952747,-13230053,-12113373,-14280941,-12770274,-9880013,-10208463,-12967396,-10274256,-12113373,-12770274,-9485770,-13952747,-11916252,-10734035,-12638945,-11587801,-11325144,-11325144,-11128022,-13032932,-13492968,-13295590,-13295590,-13098725,-13952747,-12901603,-13032932,-13492968,-13689833,-12376287,-12901603,-12967396,-11325144,-11259607,-12441824,-12967396,-12244958,-12770274,-12376287,-12441824,-11982044,-11522265
Data -11784923,-12310495,-12507616,-13755625,-13164261,-11982044,-13295846,-11653594,-13952747,-12967396,-11587801,-11128022,-13098725,-10208463,-10996693,-12901603,-11850715,-12113373,-11456472,-12836067,-13098725,-11916252,-12507616,-12441824,-11719387,-10536913,-12244702,-10668243,-11587801,-11982044,-10142670,-10011342,-11587801,-11193814,-10734035,-12244702,-10668243,-10734035,-11193815,-11982044,-13032932,-11653594,-12441824,-11982044,-9026247,-12441824,-12178910,-11259607,-11916252,-12244702,-12967396,-11850715,-13361639,-12178910,-11522265,-12047581,-11784923,-11325144,-11784923,-13886955,-10668243,-10471121,-9617098,-13361639,-13821162,-10471121,-11653594,-10077134,-13558504,-11193815,-12113373,-11325144,-13689833,-14215405,-11784923,-13886955,-14412270,-12770274,-12573153,-13361639,-13624296,-13361639,-13755625,-13032932,-12704482,-11719130,-12244958,-11522265,-11456472,-11719387,-15134964,-13032932,-12573153,-12310495,-11916252,-12376287
Data -12770274,-12901603,-12113373,-13230053,-12836067,-13164261,-13032932,-11719387,-13821162,-10471121,-10208463,-11982044,-9880013,-10668242,-11522265,-10996693,-11916252,-11193815,-11850715,-12704482,-12770274,-10930900,-12244702,-11193814,-12244958,-11522265,-12507616,-10405585,-11193815,-11456472,-12244702,-11719387,-11850715,-11390936,-10405585,-10996693,-11259607,-12638945,-12507616,-13164261,-11193815,-13492968,-10339792,-9880013,-12638945,-11719387,-13492968,-11916252,-10405585,-10208463,-12310495,-12967396,-11916252,-11193814,-14084076,-13164261,-11193814,-9026247,-12638945,-13886955,-13427175,-10405585,-9026247,-10799572,-11916252,-8434882,-10996693,-12244702,-13689833,-12704482,-13886955,-12441824,-13821162,-14280941,-14412270,-13032932,-13624296,-13295846,-12310495,-12704482,-13295846,-13230053,-12770274,-12573153,-12310495,-11719130,-12704482,-13427175,-13164261,-13821162,-14675184,-12638945,-12178910,-11522265,-11916252,-12770274
Data -12441824,-13821418,-11719130,-12507616,-12244702,-12244958,-13098725,-13295590,-14018284,-12770274,-12310495,-11062485,-9880013,-10734035,-10996693,-14149612,-12178910,-11719130,-12113373,-10339792,-11719387,-11653594,-12244958,-10405585,-10208463,-11587801,-10405585,-11325144,-11719130,-10668242,-10208463,-11325144,-12178910,-10865364,-11522265,-11062485,-11916252,-12770274,-12704482,-11128022,-11456472,-11062485,-11522265,-12967396,-12967396,-12704482,-11784923,-10734035,-10339792,-12901603,-13164261,-12770274,-12573153,-10865364,-13492968,-13295590,-10405585,-11916252,-10142670,-12704482,-13098725,-10996693,-9026247,-10011342,-9617355,-10799572,-10405585,-13164261,-12638945,-11784923,-13427175,-11456472,-11062485,-11719387,-13230053,-13821418,-13952747,-14018284,-13821418,-13295846,-14478319,-13098725,-12967396,-11653594,-11193814,-12178910,-11850715,-11522265,-12244702,-12310495,-13624296,-13032932,-10602450,-11850715,-11522265,-13492968
Data -13164261,-12638945,-11325144,-12967396,-12244958,-12573153,-12244702,-11719130,-13821162,-12638945,-10339792,-11062485,-10799572,-11719387,-10799572,-11587801,-10602450,-12770274,-10142927,-10339792,-10668242,-10996693,-10668242,-11653594,-11653594,-12573153,-10274256,-11653594,-11325144,-11193815,-10339792,-11587801,-12113373,-12507616,-11916252,-10865364,-12047581,-13624296,-10865364,-13821162,-13427175,-11719130,-12704482,-12967396,-12178910,-11850715,-11259607,-12638945,-12901603,-12244958,-11456472,-10142670,-12573153,-12113373,-12770274,-12113373,-10536913,-10142670,-10668243,-11062485,-10668242,-11062485,-12047581,-12244702,-10405585,-9945805,-11193815,-12638945,-13098725,-13755625,-13821162,-11587801,-11719130,-12770274,-11587801,-13689833,-13821418,-14215405,-13098725,-14084076,-12901603,-11916252,-13098725,-12244702,-11719387,-12901603,-12770274,-13492968,-12376287,-12573153,-11784923,-11982044,-12441824,-13098725,-11719130,-12638945
Data -12178910,-13689833,-12704482,-12507616,-11390936,-12310495,-13492968,-13427175,-13032932,-12178910,-10471121,-10865364,-11259607,-11587801,-12178910,-11916252,-11719130,-12178910,-10208463,-10930900,-12047581,-12376287,-10536913,-10471121,-11784923,-10339792,-10668243,-11850715,-9814220,-11062485,-10274256,-11587801,-11390936,-11982044,-12244702,-12376287,-12178910,-11193815,-12704482,-12704482,-11193815,-11719387,-13098725,-12113373,-11653594,-11653594,-10536913,-13230053,-11719130,-12638945,-11193815,-11390936,-13624296,-13295846,-11982044,-13295590,-9945805,-9814220,-10668243,-11062485,-13295590,-12244958,-12704482,-13295846,-12441824,-11784923,-13230053,-12376287,-12047581,-13492968,-12376287,-10274256,-12047581,-13361639,-12770274,-13492968,-14084076,-14084076,-12178910,-13492968,-13558504,-13098725,-12967396,-11456472,-12441824,-13164261,-11719130,-11456472,-11653594,-11719387,-11325144,-9025990,-10208463,-12178910,-12704482,-13689833
Data -13427175,-12770274,-14018284,-13295590,-12376287,-12178910,-13624296,-13952747,-13755625,-13098725,-12376287,-11193814,-11916252,-10668243,-11325144,-12507616,-13492968,-10142927,-12244702,-11062485,-10405585,-11193814,-11062485,-11390936,-9617098,-11193815,-12901603,-13361639,-11784923,-11193814,-10339792,-10865364,-11128022,-11522265,-11193814,-13295846,-11587801,-10602450,-12376287,-11193814,-11259607,-11719387,-12967396,-12770274,-10208463,-12770274,-13164261,-13098725,-14346734,-13624296,-11062485,-13361639,-12704482,-13886955,-13098725,-11784923,-9880013,-10405585,-9223112,-10142927,-10339792,-10142927,-11719130,-10471121,-11390936,-12967396,-12178910,-12901603,-11653594,-11325144,-7843773,-11522265,-13821418,-13689833,-13098725,-13755625,-13755625,-12638945,-12376287,-12770274,-12244958,-13558504,-13295846,-10142670,-12967396,-12244702,-11850715,-13295590,-12967396,-11719130,-11982044,-10142927,-10930900,-12704482,-13558504,-12836067
Data -12704482,-14675184,-13164261,-12244958,-12047581,-13492968,-14280941,-12244958,-12244958,-12901603,-13230053,-12244702,-12967396,-12441824,-11916252,-12770274,-12376287,-13295846,-11390936,-11128022,-9025990,-12244958,-11653594,-11325144,-11259607,-12441824,-10536913,-11850715,-10077134,-12113373,-11982044,-10471121,-11390936,-10339792,-9945805,-13492968,-13164261,-10405585,-10339792,-11850715,-13361639,-10405585,-11653594,-12047581,-12507616,-13492968,-13492968,-13492968,-12638945,-12310495,-10668243,-10208463,-9551562,-11587801,-12573153,-13032932,-11193814,-10865364,-10602450,-10865364,-11193814,-9026247,-12178910,-10930900,-12244702,-12244702,-13427175,-11522265,-11456472,-11062485,-10208463,-13952747,-14280941,-14018284,-12441824,-13361639,-12901603,-11062485,-12836067,-12967396,-13098725,-12573153,-12113373,-12441824,-11719387,-12113373,-12244702,-11259607,-12638945,-12441824,-12178910,-11719387,-11193815,-11390936,-13032932,-14609648
Data -11653594,-13427175,-13164261,-13427175,-10471121,-12507616,-14149612,-12967396,-12638945,-13361639,-13230053,-10274256,-11719130,-11325144,-12244958,-11784923,-13624296,-13164261,-12113373,-10668242,-12178910,-13624296,-12047581,-9945805,-10274256,-9223112,-11390936,-9880013,-10208463,-11128022,-11456472,-10930900,-11522265,-12376287,-12178910,-11062485,-11456472,-11062485,-11193814,-12573153,-13230053,-11062485,-12113373,-14280941,-12770274,-14018284,-13032932,-13361639,-10996693,-11916252,-10208463,-12310495,-11522265,-12507616,-10996693,-9354441,-11259607,-8763332,-9091783,-12113373,-9354441,-9945805,-11653594,-11325144,-10799572,-12376287,-11653594,-9354441,-12376287,-9617098,-13952747,-14675184,-14018284,-12704482,-12638945,-13295846,-13492968,-10142927,-12507616,-13295590,-12178910,-13295590,-12770274,-11193814,-13032932,-11522265,-12047581,-9354441,-13295590,-12244958,-11193815,-9945805,-10734035,-12310495,-13230053,-13624296
Data -13230053,-13821162,-13689833,-12047581,-13558504,-12178910,-13689833,-13492968,-12836067,-13098725,-12441824,-11653594,-12113373,-11128022,-12376287,-13295590,-12901603,-11916252,-12770274,-11456472,-9748684,-10734035,-10405585,-12047581,-12113373,-12047581,-12507616,-11193814,-11916252,-11982044,-11587801,-11850715,-12638945,-12310495,-11390936,-10996693,-12836067,-11193815,-9288904,-12244702,-12376287,-11916252,-11916252,-13295846,-10339792,-11587801,-9880013,-9617355,-10668242,-9091783,-10471121,-10799572,-9288904,-10405585,-11719387,-10208463,-13755625,-11325144,-9223112,-11522265,-11456472,-11719387,-11128022,-11784923,-12178910,-13755625,-10602450,-9354441,-11193815,-14084076,-13952747,-14280941,-12178910,-12573153,-11653594,-12967396,-13098725,-13361639,-13098725,-13361639,-12507616,-12244958,-11916252,-10930900,-12573153,-12507616,-11916252,-11916252,-12178910,-12704482,-11193814,-11128022,-12573153,-12178910,-13755625,-13755625
Data -13558504,-14609648,-14018284,-12376287,-13886955,-12704482,-12573153,-13032932,-11982044,-12638945,-12836067,-10668242,-12967396,-10799572,-11850715,-12704482,-12113373,-12244702,-10274256,-12507616,-13821418,-11587801,-9682891,-13164261,-10077134,-11522265,-13164261,-11128022,-11390936,-11982044,-12901603,-12967396,-12967396,-10208463,-11719387,-11916252,-9354441,-12901603,-12704482,-13164261,-14084076,-12967396,-13558504,-13558504,-14084076,-13624296,-13427175,-12770274,-9354441,-10996693,-9682891,-11456472,-13492968,-11982044,-10536913,-12770274,-12178910,-13492968,-10865364,-11719130,-10930900,-10799572,-11719387,-13821162,-12770274,-10142927,-10142927,-11982044,-11719130,-13821418,-13558504,-13361639,-12244702,-12507616,-13164261,-12770274,-13098725,-13558504,-12244702,-11719387,-11784923,-11522265,-11719130,-12244702,-13230053,-12507616,-11456472,-13230053,-11784923,-12376287,-11653594,-12310495,-13361639,-13295590,-13295590,-14412527
Data -13295590,-12638945,-13755625,-13427175,-13295846,-12770274,-11916252,-11982044,-13230053,-10865364,-11916252,-11062485,-11522265,-11390936,-12573153,-12573153,-11325144,-9682891,-11719387,-11916252,-12244702,-11456472,-11062485,-11587801,-10339792,-11522265,-13295590,-10865364,-11259607,-11390936,-12310495,-11325144,-12244958,-10734035,-14215405,-11916252,-10405585,-11390936,-10865364,-10668242,-12836067,-12310495,-13164261,-13624296,-14215405,-13821418,-14280941,-13755625,-8632003,-9617355,-10142927,-10339792,-11587801,-12441824,-11587801,-11719130,-10668243,-11456472,-10405585,-11456472,-11390936,-9485770,-12244958,-13098725,-13558504,-11916252,-9091783,-11325144,-12047581,-12113373,-12178910,-11719130,-12638945,-12178910,-12901603,-12441824,-12901603,-13558504,-11784923,-11390936,-11522265,-11128022,-11456472,-11719130,-11916252,-11982044,-13689833,-12704482,-12901603,-13295846,-12376287,-12704482,-13755625,-13361639,-13492968,-12573153
Data -13295590,-12901603,-14018284,-14018284,-12507616,-12573153,-12113373,-14346734,-13427175,-10668243,-12967396,-11193814,-12376287,-11916252,-10471121,-11587801,-9682891,-12573153,-11784923,-13558504,-11193814,-11916252,-11456472,-11259607,-10339792,-12047581,-11325144,-10405585,-10077134,-10668242,-11653594,-12244702,-11719130,-10668243,-11456472,-11128022,-11390936,-10865364,-10996693,-11653594,-11982044,-12770274,-13164261,-13558504,-13230053,-15397622,-13624296,-11193814,-10734035,-12047581,-12441824,-11587801,-12376287,-9617355,-13689833,-12376287,-14412270,-13821162,-12244958,-12704482,-12573153,-10865364,-12441824,-13821418,-12507616,-9288904,-11390936,-13492968,-11456472,-12244702,-10602450,-11128022,-11850715,-11719130,-12178910,-12770274,-12770274,-12113373,-11653594,-11522265,-11587801,-11719130,-12441824,-11522265,-12573153,-12507616,-12770274,-13427175,-12770274,-13295590,-11587801,-12770274,-13558504,-13755625,-13952747,-13098725
Data -13821418,-13164261,-13821162,-12244702,-12507616,-12704482,-14806513,-11193814,-12770274,-11587801,-11784923,-11587801,-10734035,-11850715,-11062485,-12244702,-12573153,-11522265,-11062485,-11653594,-11982044,-11456472,-12113373,-11916252,-11062485,-12178910,-11193814,-10274256,-9223112,-11456472,-12113373,-12376287,-11982044,-13164261,-13492968,-11193814,-12770274,-9748684,-12573153,-12638945,-12770274,-11325144,-13755625,-14018284,-13821418,-12770274,-11587801,-11982044,-10011342,-11456472,-11325144,-10930900,-10471121,-10142927,-12376287,-14280941,-13558504,-12178910,-13558504,-12441824,-13492968,-12047581,-14149612,-11719130,-9091783,-9354441,-12441824,-11522265,-12376287,-10339792,-12310495,-12638945,-13295590,-12967396,-12441824,-12310495,-11259607,-11784923,-12244702,-11193815,-11128022,-11916252,-12967396,-12441824,-11982044,-12047581,-14280941,-11719387,-12441824,-12638945,-14346734,-11719387,-12967396,-12967396,-13755625,-13098725
Data -14084076,-12967396,-13295590,-14215405,-13821162,-12244958,-13098725,-14018284,-10011342,-11587801,-12638945,-11587801,-11784923,-10208463,-10405585,-9880013,-11456472,-10274256,-12704482,-10339792,-10668242,-10930900,-12704482,-9880013,-12310495,-11193815,-11653594,-9485770,-9880013,-11193814,-11982044,-14478319,-13886955,-12113373,-13427175,-12573153,-13361639,-12507616,-12967396,-12376287,-13492968,-12704482,-12573153,-12704482,-14084076,-12178910,-11325144,-9420233,-12836067,-12244702,-11784923,-10996693,-9617355,-11128022,-13755625,-13755625,-12836067,-12310495,-12638945,-12507616,-12901603,-13230053,-11982044,-11193815,-9026247,-10668243,-11982044,-12310495,-11850715,-12244958,-13032932,-12967396,-12901603,-12441824,-12441824,-12441824,-11390936,-11062485,-10865364,-11193814,-11916252,-11522265,-11653594,-12901603,-12113373,-13032932,-12244958,-12770274,-11259607,-10799572,-13624296,-13361639,-14018284,-13295846,-13164261,-12836067
Data -13689833,-12770274,-13689833,-12376287,-13230053,-12244702,-12638945,-13558504,-12573153,-11653594,-12376287,-9880013,-11193814,-11916252,-11325144,-9945805,-10799572,-10471121,-9354441,-10602450,-8894661,-10471121,-10734035,-11719387,-12441824,-11784923,-12113373,-12770274,-11325144,-11456472,-10865364,-12770274,-14018284,-11522265,-11719130,-10142670,-11587801,-12573153,-12507616,-12901603,-12376287,-12441824,-12507616,-12836067,-12376287,-11390936,-12376287,-12244958,-10208463,-11916252,-12704482,-10668242,-10799572,-13295846,-13295846,-12113373,-12441824,-11456472,-10339792,-11325144,-10339792,-11062485,-10471121,-9617098,-9814220,-11916252,-13886955,-10734035,-10208463,-9025990,-12310495,-13295590,-11916252,-12967396,-11587801,-10602450,-11522265,-10602450,-9157575,-9814220,-12244702,-12310495,-11587801,-13558504,-12244958,-12573153,-11784923,-10142927,-11193814,-12376287,-13295846,-13427175,-12836067,-12507616,-13624296,-12770274
Data -14149612,-13821418,-9945805,-12704482,-13755625,-13230053,-12507616,-12901603,-12638945,-13492968,-12770274,-10077134,-12704482,-12967396,-10471121,-10077134,-11653594,-11784923,-9551562,-11916252,-10142670,-13295846,-11522265,-11719130,-12770274,-12967396,-12244958,-10734035,-9748684,-12967396,-11916252,-12113373,-10602450,-13032932,-14018284,-13032932,-12244702,-13032932,-10734035,-12244958,-10930900,-10799572,-11522265,-13427175,-10602450,-13032932,-12178910,-11850715,-11850715,-11259607,-10405585,-12178910,-13427175,-14346734,-12507616,-11587801,-11325144,-10668242,-9026247,-10996693,-10142670,-10799572,-11259607,-10142927,-11916252,-12441824,-9880013,-12376287,-11325144,-11456472,-11916252,-13755625,-12573153,-11653594,-10339792,-11390936,-11259607,-12178910,-10536913,-12770274,-12113373,-10930900,-12178910,-12310495,-12901603,-10996693,-11916252,-12178910,-13230053,-13164261,-12573153,-10734035,-13558504,-13230053,-11456472,-13821162
Data -13821162,-11522265,-12638945,-12047581,-13295846,-13492968,-10799572,-12770274,-11522265,-12244958,-10734035,-11719387,-12507616,-12967396,-12244702,-12178910,-10734035,-10208463,-10668243,-10668243,-11259607,-11193814,-13032932,-11850715,-12638945,-13098725,-12113373,-13230053,-12770274,-11719130,-13164261,-12901603,-10930900,-9945805,-11982044,-13689833,-12638945,-11193814,-11982044,-12507616,-13164261,-12770274,-13164261,-14412270,-10668242,-9880013,-11982044,-10536913,-11522265,-10208463,-11062485,-12507616,-12967396,-13295846,-13361639,-13624296,-9814220,-8697540,-8369089,-9748684,-11193814,-10799572,-10668242,-8960454,-10142927,-10339792,-12047581,-11653594,-9814220,-12244958,-12836067,-12244702,-12704482,-11062485,-10930900,-10405585,-10208463,-10930900,-11719387,-12376287,-11653594,-12441824,-11325144,-13230053,-13821162,-13689833,-11390936,-11719387,-13230053,-12573153,-12441824,-12836067,-13624296,-12770274,-13230053,-11653594
Data -12573153,-11259607,-12376287,-12310495,-13492968,-10930900,-13098725,-12376287,-11916252,-12704482,-12113373,-11587801,-12704482,-10208463,-10602450,-11719130,-11325144,-11193815,-12244702,-9026247,-10799572,-12113373,-10339792,-11062485,-11062485,-11193814,-11193815,-11062485,-11325144,-12770274,-12770274,-12770274,-9025990,-12376287,-13098725,-11456472,-13098725,-11587801,-12310495,-10668243,-12047581,-10799572,-14412270,-14740977,-12507616,-10011342,-9748684,-11259607,-9617355,-11456472,-12573153,-12901603,-12638945,-11916252,-14938099,-11522265,-10536913,-11325144,-10734035,-10602450,-12770274,-12507616,-11784923,-11456472,-11259607,-10405585,-9288904,-11193814,-11916252,-12441824,-12310495,-12178910,-11193814,-11850715,-11062485,-10865364,-11325144,-12376287,-13624296,-12441824,-10602450,-11850715,-13098725,-11193815,-9617098,-10536913,-12244702,-13098725,-13295846,-13164261,-11850715,-12047581,-13558504,-11522265,-12770274,-11587801
Data -10668243,-10996693,-12113373,-11982044,-12704482,-13821162,-12770274,-13295590,-9026247,-12770274,-10799572,-12113373,-10668243,-9354441,-9420233,-11128022,-11916252,-11325144,-11522265,-12047581,-12113373,-11390936,-11850715,-9682891,-9814220,-11456472,-11850715,-12836067,-11193815,-11456472,-11719387,-10011342,-10405585,-12113373,-13164261,-12244702,-11390936,-10734035,-12770274,-11850715,-12967396,-13952747,-12113373,-12244958,-8763332,-9551562,-9420233,-9485770,-9814220,-10471121,-11193815,-11062485,-11522265,-12770274,-10602450,-11062485,-10536913,-12244958,-7909310,-11390936,-11719387,-10668242,-11719387,-12638945,-11259607,-11193815,-11719130,-12441824,-13230053,-11850715,-10734035,-11193815,-11850715,-10996693,-12376287,-11982044,-10602450,-11259607,-10602450,-11193815,-10602450,-12507616,-11587801,-11522265,-12310495,-11653594,-14412270,-13032932,-14018284,-13295846,-13821162,-13886955,-12967396,-12704482,-12113373,-11325144
Data -10996693,-13032932,-13164261,-11325144,-11259607,-12047581,-12770274,-11850715,-11456472,-11390936,-11193815,-13295590,-9682891,-9945805,-9354441,-11587801,-12244958,-11193814,-10208463,-10668242,-10668242,-9617355,-11325144,-12967396,-10602450,-11062485,-12441824,-12770274,-11587801,-11587801,-11128022,-11128022,-10274256,-11719387,-12573153,-11850715,-9617355,-8303553,-11193814,-12638945,-14346734,-14149612,-11719387,-11587801,-12113373,-11128022,-9945805,-8434882,-9682891,-10471121,-12967396,-12573153,-13032932,-10668243,-11850715,-9814220,-10865364,-11719387,-10339792,-12113373,-10734035,-11062485,-11784923,-11259607,-11719387,-10536913,-12047581,-13624296,-12507616,-11587801,-12507616,-11522265,-10865364,-11719387,-10865364,-11719387,-12441824,-10602450,-11062485,-11916252,-12507616,-12638945,-11719387,-13361639,-10930900,-12770274,-13295846,-12967396,-13361639,-13558504,-11784923,-11653594,-13295846,-12770274,-13295590,-13098725
Data -12836067,-12836067,-13098725,-12507616,-12507616,-13164261,-12441824,-13624296,-13032932,-11982044,-13164261,-10865364,-10339792,-10077134,-9748684,-11259607,-11390936,-12178910,-10142927,-10865364,-11850715,-12310495,-12770274,-12507616,-13558504,-13098725,-12507616,-11390936,-12704482,-10996693,-11456472,-12178910,-12901603,-11850715,-12901603,-11719130,-9880013,-10471121,-13492968,-11916252,-11390936,-13230053,-12573153,-11653594,-9354441,-10339792,-11587801,-9288904,-13952747,-13427175,-12770274,-13295590,-12113373,-9091783,-9354441,-12441824,-9880013,-9617355,-10077134,-9617098,-10668243,-10930900,-11193814,-11522265,-10668243,-12770274,-13492968,-12770274,-10865364,-10142670,-11259607,-11390936,-13427175,-12573153,-12638945,-12113373,-10471121,-8106432,-9748684,-9682891,-12638945,-12178910,-11653594,-10865364,-11259607,-13098725,-13361639,-13755625,-11456472,-12770274,-13558504,-12244702,-13492968,-13032932,-13230053,-12704482
Data -12244702,-11587801,-10142927,-10996693,-12770274,-12310495,-11259607,-14018284,-13230053,-12113373,-12178910,-10734035,-12244702,-9420233,-10930900,-11325144,-10865364,-11062485,-12244958,-11325144,-10734035,-12967396,-13886955,-12244702,-12113373,-10668242,-11850715,-9617355,-10471121,-13032932,-9551562,-9354441,-12770274,-14346734,-13295846,-9420233,-11850715,-10536913,-10536913,-12376287,-11062485,-10077134,-10011342,-8894661,-11982044,-10734035,-10930900,-10142670,-9617355,-12178910,-11719387,-12310495,-13821418,-10865364,-11390936,-8434882,-10602450,-8894661,-12113373,-9748684,-10602450,-11850715,-10274256,-12376287,-13295590,-12507616,-10208463,-11719130,-10668243,-11193814,-10471121,-11522265,-8566211,-8632003,-7909310,-10471121,-11456472,-9945805,-11784923,-8894661,-11784923,-12113373,-12310495,-12047581,-14018284,-12441824,-12244958,-12836067,-13295846,-13098725,-11390936,-13689833,-11653594,-10930900,-10405585,-11587801
Data -13427175,-12244702,-12244702,-12178910,-11850715,-12244958,-10536913,-12967396,-11784923,-13427175,-11982044,-11784923,-12441824,-10996693,-10799572,-11587801,-10865364,-10142670,-11062485,-11456472,-11062485,-12310495,-13164261,-11850715,-11653594,-11587801,-11062485,-11325144,-9025990,-12244958,-13230053,-12507616,-14215405,-14346734,-11193815,-12638945,-11982044,-10668243,-9157575,-12244958,-11193814,-12770274,-13164261,-11653594,-9748684,-10142670,-9814220,-10799572,-13164261,-13427175,-13755625,-13689833,-12770274,-11719130,-11850715,-9485770,-10996693,-9420233,-11587801,-11390936,-11850715,-10865364,-12901603,-13821162,-13886955,-13295846,-12178910,-12113373,-10668242,-11062485,-10208463,-7646652,-7843773,-9880013,-11193814,-12770274,-12441824,-11128022,-11456472,-8763332,-11784923,-12244702,-11916252,-10734035,-12704482,-12967396,-12507616,-13164261,-13164261,-12770274,-12901603,-13361639,-12244958,-11653594,-12244702,-11850715
Data -11784923,-12638945,-10799572,-12376287,-10734035,-11719130,-13361639,-13032932,-14149612,-9814220,-12178910,-12836067,-12178910,-12638945,-11522265,-10602450,-11325144,-11193815,-11062485,-11390936,-11653594,-12244958,-11128022,-11522265,-12113373,-10471121,-11522265,-11128022,-10077134,-11719130,-11784923,-13361639,-12113373,-10142670,-12573153,-11522265,-12638945,-12573153,-9551562,-12836067,-11784923,-11390936,-11325144,-11522265,-11982044,-11850715,-9157575,-9880013,-11259607,-9880013,-14084076,-11719387,-12573153,-8829125,-11062485,-13821418,-10734035,-9682891,-10405585,-12901603,-8894661,-10865364,-11128022,-11193814,-10011342,-8960454,-10668242,-10077134,-10405585,-10734035,-9026247,-7712445,-11259607,-12770274,-12244702,-9288904,-10734035,-10208463,-10208463,-11653594,-12244958,-12704482,-10602450,-11719387,-11719387,-11982044,-13032932,-14149612,-13295590,-10996693,-13886955,-12244702,-11653594,-11456472,-11325144,-12638945
Data -12113373,-11128022,-11784923,-12770274,-10865364,-12967396,-10011342,-10930900,-13032932,-12441824,-12770274,-12901603,-12244702,-11522265,-11784923,-10668242,-10734035,-12310495,-12178910,-12047581,-13098725,-11390936,-12244958,-10405585,-12770274,-12573153,-11062485,-10471121,-10142927,-10077134,-10734035,-10930900,-13755625,-13295846,-12376287,-11193814,-10208463,-10142670,-9420233,-11325144,-10077134,-11325144,-11325144,-10536913,-10536913,-11193814,-11062485,-10930900,-11784923,-12770274,-11193815,-12638945,-12836067,-10602450,-12113373,-10930900,-10536913,-10734035,-12244958,-12113373,-11259607,-12244702,-11193815,-10274256,-9880013,-11062485,-11784923,-11062485,-11653594,-9814220,-10471121,-11719130,-10142927,-8434882,-9682891,-10668243,-10142927,-10142927,-12113373,-11982044,-12967396,-11456472,-10142670,-13230053,-11850715,-12244702,-12376287,-13032932,-11850715,-11456472,-12901603,-12310495,-11128022,-13164261,-11982044,-11456472
Data -13295846,-12836067,-10471121,-12901603,-12638945,-11587801,-9354441,-12113373,-12901603,-11062485,-12310495,-12573153,-10208463,-11719130,-12441824,-11653594,-12244958,-11193815,-11193815,-11916252,-12770274,-10208463,-13032932,-12770274,-12507616,-13624296,-12638945,-13755625,-10996693,-12573153,-11982044,-12178910,-11456472,-13361639,-12573153,-10930900,-12244958,-10930900,-11522265,-9617098,-9091783,-11062485,-11390936,-11193815,-11259607,-10668242,-13952747,-11193814,-10077134,-12441824,-12836067,-14215405,-13164261,-10471121,-11653594,-10077134,-10142927,-11325144,-11325144,-11719387,-11916252,-11850715,-10011342,-11259607,-10602450,-10668242,-11128022,-10668242,-11719130,-12770274,-11850715,-11259607,-11850715,-10274256,-11719130,-10142927,-9157575,-11916252,-13821418,-13295590,-13098725,-10339792,-11456472,-13032932,-11587801,-13230053,-13164261,-13230053,-11587801,-12441824,-12573153,-11653594,-11653594,-12770274,-11325144,-12836067
Data -13558504,-12441824,-10865364,-11982044,-11850715,-11325144,-12638945,-12244958,-9814220,-12901603,-11587801,-11982044,-11719387,-11128022,-12573153,-11784923,-11062485,-11390936,-12047581,-11982044,-13230053,-10799572,-11784923,-12310495,-13821418,-13098725,-10996693,-11719130,-11916252,-12573153,-12836067,-11325144,-10734035,-13098725,-13164261,-12047581,-11522265,-11982044,-10405585,-9945805,-10865364,-10799572,-10274256,-11128022,-12770274,-11784923,-12573153,-12901603,-13427175,-12836067,-11916252,-10208463,-11784923,-10077134,-11653594,-11456472,-11522265,-11653594,-11850715,-10668243,-10734035,-11193814,-10668242,-10865364,-9748684,-11325144,-10471121,-9682891,-11522265,-11193815,-11719130,-10602450,-10996693,-11653594,-10471121,-11916252,-10865364,-11193814,-11456472,-12770274,-12178910,-12113373,-12770274,-12310495,-12047581,-10142670,-12441824,-11456472,-12376287,-11390936,-11325144,-11193814,-11193814,-11128022,-11325144,-12376287
Data -11325144,-11587801,-12047581,-11522265,-10668243,-11653594,-11456472,-10668243,-11390936,-11653594,-11916252,-10865364,-13230053,-10799572,-10668243,-12573153,-12178910,-11522265,-12376287,-12770274,-10799572,-13098725,-12441824,-12244958,-12638945,-12770274,-11719387,-10668242,-12178910,-12178910,-12310495,-12507616,-11587801,-11390936,-13032932,-11916252,-11193814,-10142927,-10930900,-11653594,-11128022,-10274256,-10930900,-9880013,-10799572,-11325144,-12441824,-12441824,-11850715,-11522265,-9880013,-11916252,-10405585,-10208463,-11325144,-10865364,-10142670,-12047581,-11653594,-10799572,-11456472,-11653594,-10668242,-9617098,-9288904,-10142927,-8500418,-9485770,-11390936,-11325144,-12244958,-11390936,-11522265,-10536913,-10471121,-10208463,-10668243,-11193814,-11719387,-12310495,-11653594,-11653594,-12770274,-11653594,-13164261,-11719387,-12310495,-12770274,-11193815,-10996693,-11193814,-11128022,-12047581,-12113373,-12376287,-11587801
Data -10865364,-11128022,-12047581,-10865364,-12113373,-12441824,-12836067,-12178910,-11456472,-10339792,-11653594,-10734035,-11587801,-11062485,-12770274,-12113373,-12967396,-11193814,-11128022,-12178910,-11916252,-12704482,-12113373,-11062485,-12573153,-13098725,-11982044,-11850715,-11390936,-11719130,-10865364,-12244958,-12310495,-10799572,-12967396,-11982044,-10405585,-9617098,-10274256,-12901603,-11259607,-11325144,-11259607,-10799572,-10668242,-10668243,-9748684,-10799572,-10602450,-12113373,-10274256,-11719130,-12376287,-10930900,-11325144,-11653594,-10274256,-11062485,-10405585,-11784923,-11390936,-10405585,-11456472,-10011342,-9091783,-10011342,-9157575,-8829125,-9617355,-10668243,-11522265,-9157575,-10602450,-10339792,-10668243,-11982044,-11982044,-12113373,-11784923,-11850715,-11390936,-10142670,-11193814,-12441824,-12178910,-12836067,-11719387,-11719130,-9682891,-10208463,-11128022,-11850715,-11784923,-11390936,-11653594,-11259607
Data -11653594,-10668243,-10865364,-11719130,-12310495,-10536913,-12704482,-12310495,-10602450,-11456472,-10405585,-11653594,-10339792,-12507616,-9682891,-12967396,-12178910,-11653594,-11719387,-10930900,-12244958,-12770274,-12178910,-11653594,-12047581,-13098725,-9880013,-12244958,-11587801,-10602450,-10471121,-11653594,-12178910,-10142927,-11522265,-11784923,-11193815,-11193814,-12901603,-10734035,-12967396,-12047581,-12441824,-11719130,-11128022,-11653594,-10405585,-11390936,-12704482,-12113373,-11719130,-12704482,-13427175,-10734035,-11193814,-12573153,-11193815,-10536913,-12376287,-10734035,-11587801,-10274256,-9091783,-10142670,-9223112,-7975102,-11193814,-9354441,-9485770,-8763332,-9682891,-10734035,-10142670,-9617355,-10142927,-12573153,-11456472,-10799572,-11259607,-11719387,-11719130,-11916252,-11259607,-12770274,-11062485,-10865364,-10668243,-10930900,-11916252,-11259607,-11719130,-11653594,-13361639,-12310495,-11193815,-10734035
Data -12638945,-10208463,-10996693,-10142927,-10668243,-12178910,-12178910,-11259607,-10077134,-11128022,-12047581,-11522265,-11062485,-11128022,-9288904,-9748684,-10471121,-11193815,-13361639,-13032932,-10668243,-11719130,-11784923,-12244958,-11193815,-12310495,-11719387,-12441824,-11259607,-11325144,-9748684,-11325144,-11062485,-10930900,-11784923,-11456472,-11587801,-12967396,-10536913,-10668242,-11719387,-13689833,-11784923,-10536913,-12310495,-13689833,-13558504,-13230053,-12836067,-11784923,-11456472,-11982044,-11062485,-11128022,-10930900,-11259607,-10996693,-10142927,-10668242,-11062485,-10668243,-10734035,-8172224,-10208463,-8303553,-7777981,-7843773,-7975103,-10536913,-9551562,-10602450,-11522265,-12047581,-10996693,-13230053,-11456472,-10865364,-10799572,-12770274,-12704482,-10996693,-11719387,-12047581,-11587801,-10996693,-10142927,-11193814,-10668242,-11259607,-10865364,-11719130,-11653594,-11325144,-11193814,-11259607,-10339792
Data -11916252,-10405585,-12836067,-11128022,-12638945,-11193814,-11259607,-11587801,-10668243,-12178910,-11193815,-11193815,-9945805,-11719130,-8763332,-10996693,-10734035,-12244958,-11719387,-10799572,-9485770,-9945805,-12244958,-12310495,-12836067,-11193815,-11193814,-10602450,-10734035,-10996693,-10274256,-11325144,-10077134,-10668243,-10930900,-11850715,-12901603,-11653594,-13098725,-12507616,-12178910,-12836067,-10142670,-10865364,-13558504,-12178910,-13098725,-10865364,-10668243,-10602450,-12638945,-9945805,-10405585,-12573153,-10996693,-13361639,-10405585,-11390936,-10011342,-10471121,-9682891,-10274256,-11982044,-11325144,-8500418,-8566211,-9551562,-9354441,-10142670,-10734035,-10405585,-11325144,-11062485,-8566211,-12244958,-11193815,-9814220,-12967396,-13361639,-12507616,-12770274,-11193815,-11062485,-11719387,-11259607,-10339792,-10536913,-11719387,-12178910,-11456472,-11522265,-11128022,-11719387,-11193814,-12244958,-10471121
