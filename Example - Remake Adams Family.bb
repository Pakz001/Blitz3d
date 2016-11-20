Graphics 640,480,16,2
SetBuffer BackBuffer()

Dim map(270,15)

Type fps
	Field fps,fpstimer,fpscounter
End Type
Global fps.fps = New fps
Type font
	Field money
	Field normal
	Field hud
End Type
Global font.font = New font
font\money = LoadFont("verdana.ttf",44)
font\normal = LoadFont("Verdana.ttf",12)
font\hud = LoadFont("verdana.ttf",32,True)
SetFont font\normal
Type game
	Field back
	Field offx,offy,cx,cy
	; demo
	Field sdir,editor
End Type
Global g.game = New game
g\back = CreateImage(640,480)
Type player
	Field x#,y#,w,h,ow,oh,duckwidth,duckheight
	Field jump,fallspeed#,fall
	Field direction,slide,slidespeed#
	Field state ; stand,duck
	Field money,cheat,lives,hits
	Field blinking,blinkingdelay,blink,blinktimer
End Type
Global p.player = New player

Type ai
	Field x#,y#,velx#,vely#,w,h
	Field kind
	Field firedelay,jumpdelay
	Field jump,fallspeed#
End Type

Type bullet
	Field x#,y#,w,h
	Field state
	Field timer1
	Field velx#,vely#
	Field timeout
End Type

Type rot
	Field ang#
	Field Sine#
	Field angdir,timedelay,cnt
	Field chainx#[3],chainy#[3]
	Field ballx#,bally#
End Type

Type money
	Field x#,y#,fallspeed#
End Type

Type gfx
	Field ball,smallball
End Type
Global gfx.gfx = New gfx

inigfx
inigame


While KeyDown(1) = False
	;Cls
	updateai
	updatebullets
	updaterot
	gravity	
	playeraikill()
	aiplayerkill()
	spikesplayerkill()
	bulletsplayerkill
	rotplayerkill
	playerblinkingeffect()
	updatemoney
	playermoneycollision
	playercontrols
	playerslide
	mapscroll


	SetBuffer ImageBuffer(g\back)
	Cls	
	drawbullets	
	drawmoney
	drawrot	
	drawlevel
	
	drawai
	drawplayer
	SetBuffer BackBuffer()
	DrawBlock g\back,-32,0
	drawhud()
	movemlevel()
	If shiftdown() = True Then g\editor = True Else g\editor = False
	Color 255,0,0
;	Text 0,20,p\x-maprealx()
;	Text 0,0,rectmapcollision(MouseX()+maprealx(),MouseY()+maprealy(),2,2,0,0)
;	Text GraphicsWidth()-128,0,myfps()
;	Text 256,0,rectmapcollision(p\x,p\y,48,80,0,0)
;	Text 400,0,p\fall + " ?: " + p\jump
	
	If MouseDown(1) = True Then p\x = MouseX()+maprealx() : p\y = MouseY()+maprealy()
	Flip	
Wend
End

Function inigame()
	;p\x = 3180
	freegame
	p\x = 90
	p\y = 128+48
	p\w = 48 : p\ow = 48 : p\duckwidth = 48
	p\h = 80 : p\oh = 80 : p\duckheight = 32
	p\hits = 3
	p\lives = 1
	p\blinkingdelay = MilliSecs() + 3000
	p\blinktimer = 50
	p\blink = True

	g\cx = 0;33;;88
	g\offx = 0
	g\offy = 0
	g\editor = False
	readlevel(1)
End Function


Function rotplayerkill()
	If p\blinkingdelay > MilliSecs() Then Return
	If p\cheat = True Then Return
	For this.rot = Each rot		
		If p\state = 1 Then
		If ImageRectOverlap(gfx\ball,this\chainx[3],this\chainy[3],p\x,p\y+(80-32),p\w,p\h) = True
			playerdecreasehit
		End If
		Else
		If ImageRectOverlap(gfx\ball,this\chainx[3],this\chainy[3],p\x,p\y,p\w,p\h) = True
			playerdecreasehit
		End If		
		End If
	Next
End Function

Function updaterot()
	For this.rot = Each rot
		add=0
		this\sine = this\sine + 2
		mth=90*Sin(this\sine)
		For a=0 To 3
			angle=(mth)						
			this\chainx#[a] = this\ballx+((add)*Sin(angle))
			this\chainy#[a] = this\bally+((add)*Cos(angle))
			add=add+32
			If a=2 Then add=add+16
		Next

			
	Next
End Function

Function drawrot()
	For this.rot = Each rot
		Color 0,0,255
		MidHandle gfx\smallball
		For i=0 To 2
			DrawImage gfx\smallball,this\chainx[i]-maprealx(),this\chainy[i]-maprealy()
		Next
		MidHandle gfx\ball
		DrawImage gfx\ball,this\chainx[3]-maprealx(),this\chainy[3]-maprealy()
	Next
End Function

Function playerblinkingeffect()
	If p\blinkingdelay > MilliSecs() Then
		If p\blinktimer < MilliSecs() Then 
			If p\blink = True Then p\blink = False Else p\blink = True
			p\blinktimer = MilliSecs() + 50
		End If
		Else
		p\blink = True
	End If
End Function

Function drawhud()
	; hits
	cnt = 0
	For y=100 To 0 Step -34
		If p\hits > cnt Then drawlifeimage(32,y)		
		cnt = cnt + 1
	Next
	;
	; lives
	SetFont font\hud
	Color 155,155,155
	Text 10,GraphicsHeight()-128,"Lives"
	Text 32,GraphicsHeight()-108,playerlives()
	; money
	Color 155,155,155
	drawmoneyimage(32,GraphicsHeight()-70)
	SetFont font\hud
	Text 32,GraphicsHeight()-42,playermoney()
	SetFont font\normal
End Function

Function inirot(x,y)
	this.rot = New rot
	this\ang = 90
	this\ballx = x
	this\bally = y+48
End Function

Function playermoneycollision()
	For this.money = Each money
		If RectsOverlap(p\x,p\y,p\w,p\h,this\x,this\y,32,32) = True Then 
			p\money = p\money + 1
		Delete this
	End If
	Next
End Function

Function inimoney(x,y)
	this.money = New money
	this\x = x
	this\y = y
	this\fallspeed = Rnd(-1,0)
End Function

Function updatemoney()
	For this.money = Each money
		this\y = this\y - this\fallspeed
		this\fallspeed = this\fallspeed + .09
		If this\fallspeed > 1 Then 
		this\fallspeed = -1
		this\y = this\y/32*32
		End If
	Next
End Function

Function drawmoney()
	For this.money = Each money
		drawmoneyimage(this\x-maprealx(),this\y-maprealy())
	Next
End Function

Function spikesplayerkill()
	If p\cheat = True Then Return
	If p\blinkingdelay > MilliSecs() Then Return
	x1 = p\x / 32
	y1 = (p\y+64) / 32
	If RectsOverlap(x1,y1,1,1,0,0,170,15) = True Then
		If map(x1,y1) = 2 Then playerdecreasehit	
	End If
End Function

Function playeraikill()
	If p\fall = True Then 
		For this.ai = Each ai
			If RectsOverlap(p\x,p\y,48,80,this\x,this\y,this\w,this\h) = True Then 
			Delete this
			End If
		Next
	End If
End Function

Function playerslide()
	If p\slide = True 
;	p\state = 0
	If p\jump = True Then p\slide = False : Return
	If p\fall = True Then p\slide = False : Return
	Select p\direction
	Case 0
	If playerright() = False
	If pmcollision(p\slidespeed,0) = False Then
		p\x = p\x + p\slidespeed 
		p\slidespeed = p\slidespeed - .1
		Else
		p\x = p\x/32*32
		p\slide = False
		p\slidespeed = 0
	End If
	End If
	Case 1
	If playerleft() = False
	If pmcollision(-p\slidespeed,0) = False
		p\x = p\x - p\slidespeed 
		p\slidespeed = p\slidespeed - .1
		Else
		p\x = p\x/32*32
		p\slide = False
		p\slidespeed = 0
	End If
	End If
	End Select
	End If
	If p\slidespeed < 0 Then p\slide = False
End Function

Function mapscroll()
	x = p\x - maprealx()
	;
	If playerright() = False And playerleft() = False
	If p\slide = True Then
	Select p\direction 
	Case 0
	For i=0 To p\slidespeed-1
	scrolllevel(0)
	Next
	Case 1
	For i=0 To p\slidespeed-1
	scrolllevel(1)
	Next

	End Select
	End If
	End If
	;
	If x+40> GraphicsWidth() / 2 And playerright() = True Then
		If g\cx < 70*2-20
		scrolllevel(0):scrolllevel(0):scrolllevel(0):scrolllevel(0)
		End If
	End If
	If x-40< GraphicsWidth() / 2 And playerleft() = True Then
		If g\cx > 0
			scrolllevel(1):scrolllevel(1):scrolllevel(1):scrolllevel(1)
		End If
	End If
End Function



Function playercontrols()
;	p\state=0
	p\w = p\ow
	p\h = p\oh
	If playerright() = True Then
	If pmcollision(4,0) = False Then
		p\direction  = 0
		p\slide = True
		p\slidespeed = 2
		p\x=p\x+4
	End If
	End If
	If playerleft() = True Then
	If pmcollision(-4,0) = False Then
		p\direction = 1
		p\slide = True
		p\slidespeed = 2
		
		p\x=p\x-4
	End If
	EndIf
	If KeyDown(200) = True Or KeyDown(57) = True
	If p\fall = False And p\jump = False Then
		p\jump = True
		p\fallspeed = -11.5
	End If
	End If
	If playerduck() = True Then
	If p\jump = False And p\fall = False Then
		p\state = 1
		p\w = p\duckwidth
		p\h = p\duckheight
	End If
	Else
		p\state = 0
	End If
End Function

Function gravity()
	If p\jump = True And pmcollision(0,p\fallspeed) = True Then
		p\fall = True
		p\jump = False
		p\fallspeed = 2
	End If
	If pmcollision(0,2) = False And p\fall = False And p\jump = False Then 
		p\fall = True
		p\jump = True
		p\fallspeed = 2
	End If
	If p\jump = True Or p\fall = True Then
		If p\fallspeed > 0 Then p\fall = True
		p\fallspeed = p\fallspeed + .4
		p\y = p\y + p\fallspeed
		For i=1 To p\fallspeed
		If pmcollision(0,i) = True Then
			While pmcollision(0,i) = True 
			p\y=p\y - 1
			Wend
			p\fall = False
			p\jump = False
			Exit
		End If
		Next
	EndIf
End Function

Function bulletsplayerkill()
	If p\cheat = True Then Return
	If p\blinkingdelay > MilliSecs() Then Return
	For this.bullet = Each bullet
		Select p\state
		Case 0
		If RectsOverlap(p\x,p\y,p\w,p\h,this\x,this\y,this\w,this\h) = True Then
			playerdecreasehit
		End If
		Case 1
		If RectsOverlap(p\x,p\y,p\w,p\h,this\x,this\y+(80-32),this\w,this\h) = True Then
			playerdecreasehit
		End If
		End Select
	Next
End Function

Function updatebullets()
	For this.bullet = Each bullet
		Select this\state
			Case 0 
			If this\timer1 + 1500 < MilliSecs() Then this\state = 1
			Case 1
			this\velx = this\velx + .2
			If this\velx > 5 Then this\state = 2
			Case 2
		End Select
		this\x = this\x + this\velx
		this\y = this\y + this\vely
		If this\timeout < MilliSecs() Then Delete this
	Next
End Function

Function drawbullets()
	For this.bullet = Each bullet
		Color 255,255,0
		Oval this\x-maprealx(),this\y-maprealy(),48,48,True
	Next
End Function

Function inibullet(x,y,dir)
	this.bullet = New bullet
	this\x = x
	this\y = y
	this\w = 48
	this\h = 48
	this\timer1 = MilliSecs()
	this\timeout = MilliSecs() + 3800
	If dir = 0 Then this\velx = 5
	If dir = 1 Then this\velx = -5
End Function

Function aiplayerkill()
	If p\blinkingdelay > MilliSecs() Then Return
		For this.ai = Each ai
		If RectsOverlap(p\x,p\y,p\w,p\h,this\x,this\y,this\w,this\h) = True Then
			playerdecreasehit()
		End If
	Next
End Function

Function updateai()
	For this.ai = Each ai
		Select this\kind
		Case 0
		If this\jumpdelay < MilliSecs() Then
			this\jump = True
			this\fallspeed = -4
			this\jumpdelay = MilliSecs() + 4800*2
		End If
		If this\firedelay < MilliSecs() Then
			inibullet(this\x,this\y,1)
			this\firedelay = MilliSecs() + 4800
		End If
		jumpai(this)
		Case 1
		this\x = this\x + this\velx
		If rectmapcollision(this\x,this\y,48,64,this\velx,0) = True Then 
			this\velx = - this\velx
		End If
		End Select
	Next
End Function
Function jumpai(this.ai)
	If this\jump = False Then Return
	this\y = this\y + this\fallspeed
	this\fallspeed = this\fallspeed + .1
	For i=0 To this\fallspeed
	If rectmapcollision(this\x,this\y,48,48,0,i) = True Then
		this\y = this\y/32*32-4
		this\jump = False
		Exit
	End If
	Next
End Function

Function drawai()
	For this.ai = Each ai
		Select this\kind
		Case 0
		Color 255,0,0
		Rect this\x-maprealx(),this\y-maprealy(),48,48,True
		Case 1
		Color 255,0,0
		Rect this\x-maprealx(),this\y-maprealy(),48,64,True
		End Select
	Next
End Function

Function iniai(x,y,kind)
	this.ai = New ai
	this\x = x
	this\y = y	
	Select kind
	Case 0
	this\w = 48
	this\h = 48
	this\kind = 0
	this\firedelay = MilliSecs() + 200
	this\jumpdelay = MilliSecs() + 4800
	Case 1
	this\w = 48
	this\h = 64
	this\kind = 1
	this\velx = -1.7
	End Select
End Function

Function movemlevel()
	If g\editor = True Then 
		If KeyDown(205) = True Then scrolllevel(0):scrolllevel(0):scrolllevel(0):scrolllevel(0)
		If KeyDown(203) = True Then scrolllevel(1):scrolllevel(1):scrolllevel(1):scrolllevel(1)
	End If
End Function

Function scrolllevel(num)
	Select num
		Case 0 ; right
		g\offx = g\offx - 1
		If g\offx < 0 Then g\offx = 31 : g\cx = g\cx + 1
		Case 1	
		g\offx = g\offx + 1
		If g\offx > 31 Then g\offx = 0 : g\cx = g\cx - 1
	End Select
End Function

Function drawplayer()
	If p\blink= False Then Return
	Select p\state
		Case 0 ; stand
			Color 0,0,255
			Rect p\x-maprealx(),p\y-maprealy(),p\ow,p\oh,True
		Case 1 ; duck
			Color 0,0,255
			Rect p\x-maprealx(),p\y+48-maprealy(),p\duckwidth,p\duckheight
	End Select
End Function

Function drawlevel()
	For y=0 To 14
	For x=0 To 20
		If RectsOverlap((g\cx)+x,(g\cy)+y,1,1,0,0,160,15) = True Then
		Select map(x+g\cx,y+g\cy)
			Case 1
			Color 255,255,255
			Rect (x*32)+g\offx,(y*32)+g\offy,32,32,True
			Case 2
			x1 = (x*32)+g\offx
			y1 = (y*32)+g\offy
			Color 255,255,255
			Line x1,y1+32,x1+16,y1
			Line x1+16,y1,x1+32,y1+32
		End Select

		End If
	Next:Next
End Function

Function readlevel(level)
	;
	Restore level1suba
	For y=0 To 14
	For x=0 To 69
		Read a
		Select a
		Case 1
		map(x,y) = a
		Case 2 ; ai - shooter
		iniai(x*32,y*32-(48-32),0)
		Case 3 ; ai - walker
		iniai(x*32,y*32-32,1)
		Case 4 ; rotating thing
		inirot(x*32,y*32-32)
		Case 5 ; spikes
		map(x,y) = 2
		Case 6 ; money
		inimoney(x*32,y*32)
		End Select
	Next:Next
	Restore level1subb
	For y=0 To 14
	For x=0 To 69
		x1 = x + 69
		y1 = y 
		Read a
		Select a
		Case 1
		map(x1,y1) = a
		Case 2 ; ai - shooter
		iniai(x1*32,y1*32-(48-32),0)
		Case 3 ; ai - walker
		iniai(x1*32,y1*32-32,1)
		Case 4 ; rotating thing
		inirot(x1*32+16,y1*32-32)
		Case 5 ; spikes
		map(x1,y1) = 2
		Case 6 ; money
		inimoney(x1*32,y1*32-32)
		End Select
	Next:Next

End Function

.level1suba
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,6,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,6,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,3,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,5,5,5,5,1,1,1,1,1,1,1,1,1,1,1,5,5,5,5,1,1,1,1,1,1,1,0,0,0,0
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
.level1subb
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1
Data 0,1,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,0,0,0,1,1,1,1,1,0,0,0,0,1
Data 0,1,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,0,0,0,0,1,1,1,0,0,0,0,0,1
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,1,0,0,0,0,0,0,4,0,0,0,0,0,0,1
Data 0,1,0,6,6,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,4,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 0,1,0,6,6,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,6,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,6,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,0,0,1
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,5,5,5,5,1,1,1,1,1,5,5,5,5,1,1,1,1,1,1,1,1,1,1,1,1,1,6,6,6,1
Data 0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1



Function maprealx()
	a = g\cx * 32
	b = g\offx
	Return a-b
End Function
Function maprealy()
	a = g\cy * 32
	b = g\offy
	Return a-b
End Function

Function rectmapcollision(x,y,w,h,offx,offy)
	px = (x) / 32
	py = (y) / 32
	For y1 = -3 To 4
	For x1 = -3 To 3
		If RectsOverlap(px+x1,py+y1,1,1,0,0,70*2,15) = True Then
			If map(px+x1,py+y1) = 1 Then
				;Color 0,250,0
				;Rect (px+x1)*32-maprealx(),(py+y1)*32-maprealy(),32,32,False
				If RectsOverlap(x+offx,y+offy,w,h,(px+x1)*32,(py+y1)*32,32,32) = True Then
					Return True
				End If
			End If
		End If
	Next:Next
End Function

Function pmcollision(offx,offy)
	Select p\state
		Case 0:Return rectmapcollision(p\x,p\y,48,80,offx,offy)
		Case 1:Return rectmapcollision(p\x,p\y,48,80,offx,offy)
	End Select
End Function

Function myfps()
	fps\fpscounter = fps\fpscounter + 1
	If fps\fpstimer < MilliSecs() Then
		fps\fpstimer = MilliSecs() + 1000
		fps\fps = fps\fpscounter
		fps\fpscounter = 0
	End If
	Return fps\fps
End Function

Function shiftdown()
	If KeyDown(42) = True Then Return True
	If KeyDown(54) = True Then Return True
End Function 
Function playerright()
	If KeyDown(205) = True Then Return True
End Function
Function playerleft()
	If KeyDown(203) = True Then Return True
End Function
Function playerduck()
	If KeyDown(208) = True Then Return True
End Function

Function playermoney$()
	If Len(p\money) = 1 Then Return "0"+p\money Else Return p\money
End Function
Function playerlives$()
	If Len(p\lives) = 1 Then Return "0"+p\lives Else Return p\lives
End Function

Function drawmoneyimage(x,y)
	Color 0,0,0
	Oval x-1,y-1,34,34,True
	Color 255,255,0
	Oval x,y,32,32,True
	Color 0,0,0
	SetFont font\money
	Text x+5,y-8,"$"
	SetFont font\normal
End Function

Function drawlifeimage(x,y)
	Color 255,0,0
	Oval x,y,32,32
	Color 5,5,5
	SetFont font\money
	Text x+3,y-8,"H"
	SetFont font\normal
End Function

Function playerdecreasehit()
	p\hits = p\hits - 1
	p\blinkingdelay = MilliSecs() + 3000
	If p\hits = 0 Then
		p\hits = 3
		p\lives = p\lives - 1
		If p\lives = 0 Then inigame
	End If
End Function

Function inigfx()
	gfx\ball = CreateImage(64,64)
	SetBuffer ImageBuffer(gfx\ball)
	Color 255,0,0
	Oval 0,0,64,64,True
	gfx\smallball = CreateImage(32,32)
	SetBuffer ImageBuffer(gfx\smallball)
	Color 255,90,0
	Oval 0,0,32,32,True
	SetBuffer BackBuffer()
End Function

Function freegame()
	For a.ai = Each ai
		Delete a
	Next
	For b.bullet = Each bullet
		Delete b
	Next
	For c.rot = Each rot
		Delete c
	Next
	For d.money = Each money
		Delete d
	Next
End Function

