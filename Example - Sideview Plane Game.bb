Function iniplane()
	Color 0,0,0 
 	SetBuffer ImageBuffer(lvl)
	Color 0,0,11
	Rect 100-128,grond - 164,240,168,True
	Color 255,255,255
	Rect 100,grond+8,30,20,True
	SetBuffer BackBuffer()
	
	p.plane = New plane
	p\x = 100
	p\y = grond
	;
	p\xac# = 0.009 ; plane acceleration
	p\yac# = 0.02  ; plane y acceleration
	p\w=16
	p\h=4
	p\maxx = 2 ; maximum speed
	
	p\airborn_at# = .3  ; .airborn liftoff speed
	p\airborn = True
End Function
;drop it in the
; hat baron v2
Graphics 640,480,16,2
SetBuffer BackBuffer()

Global lvl = CreateImage(640,480)

Global hoogte# = GraphicsHeight()
Global breedte# = GraphicsWidth()
Global grond# = hoogte-68
;RuntimeError grond + " L "+ GraphicsHeight()
Global debug = True
Global vert# = GraphicsHeight()/1.2

Type drops
	Field x#,y#,w#,h#,incx#,incy#
		
End Type
;
Type plane
	Field x#,y#,w,h,xforce#,yforce#
	Field maxx#
	Field airborn_at,airborn
	Field xac#,yac#
End Type

Type grav
	Field g#
End Type

Global grav.grav = New grav
	grav\g# = .03


timer = CreateTimer(30)

SeedRnd MilliSecs()

For y=GraphicsHeight()-128 To GraphicsHeight() Step 64
	For x = 0 To GraphicsWidth() Step 48
		so = Rand(1,2)

		If do=True Then
		
	;	makelevel x ,y
		Else
			If so = True Then
	;		makelevel x,y
			End If
		End If

	Next
	do=True
Next
iniplane()
While KeyDown(1) = False
	WaitTimer(timer)

	Cls	
	DrawBlock lvl,0,0
	drawplane
	controls()
	handleplane
	gravity
	updatedrops
	drawdrops
	Rect GraphicsWidth()/2,GraphicsHeight()-90,16,16,False
	
	For this.plane = Each plane
		If Abs(this\xforce) > this\maxx/3 Then Color 0,255,0
		If Abs(this\xforce) < this\maxx/3 Then Color 255,0,0
		pbar 0,0 ,50,10 ,Abs(this\xforce) ,this\maxx
		pbar 0,10,50,10 ,Abs(this\yforce) ,2
		pbar 0,20,50,10 ,Abs(this\maxx)   ,2
	Next
	
	my.plane = First plane
	If KeyHit(57) = True Then inidrops(my)
	
	Color 255,0,0
	Rect 0,grond,breedte,20,False
	
;	pbar 0,0,110,20,Rand(100),100
	Flip
	
	
Wend
End
;
Function controls()
	this.plane = First plane
	If KeyDown(205) = True ; right cursor 	; speed
		speedup(this\xac)
	End If
	If KeyDown(208) = True

	End If
	If KeyDown(200) = True ; up cursor		; up
		rotateup(-this\yac)
	End If
	If KeyDown(203) = True ; cursor back	; brake
		speedup(-this\xac)
	End If
End Function

Function gravity()
	For this.plane = Each plane

	If this\airborn = True;;;; <airborn
		If this\yforce < 2 Then
			this\yforce = this\yforce + .01
	;		DebugLog "ddd " + this\yforce
		End If
		If this\maxx >  .1 Then this\maxx = this\maxx -.0001
		If this\maxx < -.1 Then this\maxx = this\maxx +.0001
	;		End If
		If RectsOverlap(this\x,this\y,1,1,0,0,GraphicsWidth(),GraphicsHeight()) = False 	Then
			this\x = 100
			this\y = 100
			this\xforce = 0
			this\yforce = 0
		End If
		If this\xforce > 0 Then this\xforce = this\xforce - 0.001
		If this\xforce < 0 Then this\xforce = this\xforce + 0.001


	End If
	
			
	If this\airborn = True And this\y > grond Then 
		this\y = grond
		this\xforce = 0
		this\yforce = 0
		this\airborn = False
	End If
	If this\airborn = False And this\y < grond Then
		this\airborn = True
	End If
	Next
End Function

Function rotateup(val#)
	For this.plane = Each plane
		If Abs(this\xforce) > this\maxx/2 Then
			this\yforce = this\yforce + val#
		End If		
	Next
End Function

Function speedup(val#)
	For this.plane = Each plane
		this\xforce = this\xforce + val#
		;If Abs(this\xforce) > Abs(this\maxx) Then this\xforce = this\maxx
		If this\xforce < 0
			If this\xforce < -this\maxx Then this\xforce = -this\maxx
		Else
			If this\xforce > this\maxx Then this\xforce = this\maxx
		End If
;		If debug = True Then If Rand(10)=1 Then DebugLog this\xforce
	Next
End Function

Function drawplane()
	For this.plane = Each plane
		temp = CreateImage(4+(Abs(this\xforce*8)),this\h)
		SetBuffer ImageBuffer(temp)
		
		ClsColor 255,255,255  :Cls
		Color 200,40,20
		Rect 0,                    0,ImageWidth(temp),ImageHeight(temp)/5,True
		Rect 0,(ImageHeight(temp)/5),ImageWidth(temp),ImageHeight(temp)/5,True
		Rect 0,(ImageHeight(temp)/5)*2,ImageWidth(temp),ImageHeight(temp)/5,True

		SetBuffer BackBuffer()
		
		If this\xforce>0 RotateImage temp , this\yforce  * 12
		If this\xforce<0 RotateImage temp , -this\yforce * 12
		
		DrawImage temp,this\x,this\y
		FreeImage temp
	Next
End Function
;
Function handleplane()
	For this.plane = Each plane
		this\x = this\x + this\xforce#
		this\y = this\y + this\yforce#
		If this\yforce < -1 Then this\yforce = -1			
	Next
End Function
;
Function pbar(x,y,w,h,s#,max#)
	;
	a# = (w/max)*s
;	Color 255,0,0
	Rect x,y,a,10,True
;	Color 100,100,100
	Rect x,y,w,h,False
	;
End Function



Function makelevel(x1,y)

;	SetBuffer CanvasBuffer(can)
	SetBuffer ImageBuffer(lvl)
;	x1=x1+20
;	y=y-140
	
;	For x=x1 To 320 Step 48
	
	t=CreateImage(64,64)
	SetBuffer ImageBuffer(t)
	Color 255,255,255
	While  i < 320
		a = (Cos(i+Rnd(i)))*22
		b = (Sin(Rand(i)))*22
		a = a ;+ x1
		b = b ;+ y
;		If b > 30 And b<50 Then Color 20,130,250 Else Color 255,255,255
		Color 255,255,255
		
		a1=a+22
		a2=b+Rand(20)
		a3=Rand(13,10)
		a4=Rand(3,20)+ls
		Rect a1,a2,a3,a4
		Oval a1,a2,a3+4,a4+4
		;Rect a+22,b+Rand(30),Rand(13,10),Rand(3,10) + ls
		Oval a+22,b,Rand(13,15),Rand(13,35) + ls
		;
		;
		ls = 0
		If a < 20 Then ls = Rand(15)
		If a > 140 Then ls = Rand(30)
		;
		i=i+10
		;
	Wend
;	Color 100,100,100
;	Rect 0,0,64,64,False
	SetBuffer ImageBuffer(lvl)
	DrawImage t,x1,y

	
		If Rand(5) = True Then
			Color 255,255,255
			Rect x1,y,32,32,True
		End If
		If Rand(5) = True Then
			Color 255,255,255
			Rect x1+32,y,32,32,True
		End If



;	Next

	SetBuffer BackBuffer()
	
End Function

Function inidrops(this.plane)
	;
	that.drops = New drops
	that\x = this\x
	that\y = this\y
	that\incy = this\yforce
	that\incx = this\xforce
	;
End Function
Function updatedrops()
	For this.drops = Each drops	
		this\x = this\x + this\incx
		this\y = this\y + this\incy
		this\incy = this\incy + grav\g
		If this\incx > 0 Then this\incx = this\incx - .01
		If this\incx < 0 Then this\incx = this\incx + .01
		If RectsOverlap(this\x,this\y,this\w,this\h,0,0,breedte,hoogte) = False
			Delete this
		End If
	Next
End Function
Function drawdrops()
	For this.drops = Each drops
		Color 255,255,255
		Oval this\x,this\y,6,6,True
	Next
End Function
