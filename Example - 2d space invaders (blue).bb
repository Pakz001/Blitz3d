;
;
;
;

Global difficulty = 100 ; lower = harder

Graphics 640,480,16,2
SetBuffer BackBuffer()


;
; Scancodes
;
;

Const KEY_NONE				= 0
Const KEY_ESCAPE			= 1
Const KEY_1					= 2
Const KEY_2					= 3
Const KEY_3					= 4
Const KEY_4					= 5
Const KEY_5					= 6
Const KEY_6					= 7
Const KEY_7					= 8
Const KEY_8					= 9
Const KEY_9					= 10
Const KEY_0					= 11
Const KEY_HYPHEN			= 12
Const KEY_EQUAL				= 13
Const KEY_BACKSPACE			= 14
Const KEY_TAB				= 15
Const KEY_Q					= 16
Const KEY_W					= 17
Const KEY_E					= 18
Const KEY_R					= 19
Const KEY_T					= 20
Const KEY_Y					= 21
Const KEY_U					= 22
Const KEY_I					= 23
Const KEY_O					= 24
Const KEY_P					= 25
Const KEY_BRACKET_LEFT		= 26
Const KEY_BRACKET_RIGHT		= 27
Const KEY_ENTER				= 28
Const KEY_CTRL_LEFT			= 29
Const KEY_A					= 30
Const KEY_S					= 31
Const KEY_D					= 32
Const KEY_F					= 33
Const KEY_G					= 34
Const KEY_H					= 35
Const KEY_J					= 36
Const KEY_K					= 37
Const KEY_L					= 38
Const KEY_SEMICOLON			= 39
Const KEY_APOSTROPHE		= 40
Const KEY_GRAVE				= 41
Const KEY_SHIFT_LEFT		= 42
Const KEY_BACKSLASH			= 43
Const KEY_Z					= 44
Const KEY_X					= 45
Const KEY_C					= 46
Const KEY_V					= 47
Const KEY_B					= 48
Const KEY_N					= 49
Const KEY_M					= 50
Const KEY_COMMA				= 51
Const KEY_PERIOD			= 52
Const KEY_SLASH				= 53
Const KEY_SHIFT_RIGHT		= 54
Const KEY_NUMPAD_MULTIPLY	= 55
Const KEY_ALT_LEFT			= 56
Const KEY_SPACE				= 57
Const KEY_CAPS_LOCK			= 58
Const KEY_F1				= 59
Const KEY_F2				= 60
Const KEY_F3				= 61
Const KEY_F4				= 62
Const KEY_F5				= 63
Const KEY_F6				= 64
Const KEY_F7				= 65
Const KEY_F8				= 66
Const KEY_F9				= 67
Const KEY_F10				= 68
Const KEY_NUM_LOCK			= 69
Const KEY_SCROLL_LOCK		= 70
Const KEY_NUMPAD_7			= 71
Const KEY_NUMPAD_8			= 72
Const KEY_NUMPAD_9			= 73
Const KEY_NUMPAD_HYPHEN		= 74
Const KEY_NUMPAD_4			= 75
Const KEY_NUMPAD_5			= 76
Const KEY_NUMPAD_6			= 77
Const KEY_PLUS				= 78
Const KEY_NUMPAD_1			= 79
Const KEY_NUMPAD_2			= 80
Const KEY_NUMPAD_3			= 81
Const KEY_NUMPAD_0			= 82
Const KEY_NUMPAD_PERIOD		= 83
Const KEY_F11				= 87
Const KEY_F12				= 88
Const KEY_F13				= 100
Const KEY_F14				= 101
Const KEY_F15				= 102
Const KEY_NUMPAD_EQUAL		= 141
Const KEY_NUMPAD_ENTER		= 156
Const KEY_CTRL_RIGHT		= 157
Const KEY_NUMPAD_SLASH		= 181
Const KEY_SYS_RQ			= 183
Const KEY_ALT_RIGHT			= 184
Const KEY_PAUSE				= 197
Const KEY_HOME				= 199
Const KEY_ARROW_UP			= 200
Const KEY_PAGE_UP			= 201
Const KEY_ARROW_LEFT		= 203
Const KEY_ARROW_RIGHT		= 205
Const KEY_END				= 207
Const KEY_ARROW_DOWN		= 208
Const KEY_PAGE_DOWN			= 209
Const KEY_INSERT			= 210
Const KEY_DELETE			= 211
Const KEY_WINDOWS_LEFT		= 219
Const KEY_WINDOWS_RIGHT		= 220

Dim Keynames$(220)
For n = 0 To 220
	Keynames(n)= "Unknown"
Next
	Keynames(1)= "Escape"
	Keynames(2)= "1"
	Keynames(3)= "2"
	Keynames(4)= "3"
	Keynames(5)= "4"
	Keynames(6)= "5"
	Keynames(7)= "6"
	Keynames(8)= "7"
	Keynames(9)= "8"
	Keynames(10)= "9"
	Keynames(11)= "0"
	Keynames(12)= "-"
	Keynames(13)= "="
	Keynames(14)= "Backspace"
	Keynames(15)= "Tab"
	Keynames(16)= "Q"
	Keynames(17)= "W"
	Keynames(18)= "E"
	Keynames(19)= "R"
	Keynames(20)= "T"
	Keynames(21)= "Y"
	Keynames(22)= "U"
	Keynames(23)= "I"
	Keynames(24)= "O"
	Keynames(25)= "P"
	Keynames(26)= "["
	Keynames(27)= "]"
	Keynames(28)= "Return"
	Keynames(29)= "Left Ctrl"
	Keynames(30)= "A"
	Keynames(31)= "S"
	Keynames(32)= "D"
	Keynames(33)= "F"
	Keynames(34)= "G"
	Keynames(35)= "H"
	Keynames(36)= "J"
	Keynames(37)= "K"
	Keynames(38)= "L"
	Keynames(39)= ";"
	Keynames(40)= "'"
	Keynames(41)= "#";UK Keyboard
	Keynames(42)= "Left Shift"
	Keynames(43)= "\"
	Keynames(44)= "Z"
	Keynames(45)= "X"
	Keynames(46)= "C"
	Keynames(47)= "V"
	Keynames(48)= "B"
	Keynames(49)= "N"
	Keynames(50)= "M"
	Keynames(51)= ","
	Keynames(52)= "."
	Keynames(53)= "/"
	Keynames(54)= "Right Shift"
	Keynames(55)= "Numpad *"
	Keynames(56)= "Left Alt"
	Keynames(57)= "Space"
	Keynames(58)= "Caps Lock"
	Keynames(59)= "F1"
	Keynames(60)= "F2"
	Keynames(61)= "F3"
	Keynames(62)= "F4"
	Keynames(63)= "F5"
	Keynames(64)= "F6"
	Keynames(65)= "F7"
	Keynames(66)= "F8"
	Keynames(67)= "F9"
	Keynames(68)= "F10"
	Keynames(69)= "Num Lock"
	Keynames(70)= "Scroll Lock"
	Keynames(71)= "Numpad 7"
	Keynames(72)= "Numpad 8"
	Keynames(73)= "Numpad 9"
	Keynames(74)= "Numpad -"
	Keynames(75)= "Numpad 4"
	Keynames(76)= "Numpad 5"
	Keynames(77)= "Numpad 6"
	Keynames(78)= "Numpad +"
	Keynames(79)= "Numpad 1"
	Keynames(80)= "Numpad 2"
	Keynames(81)= "Numpad 3"
	Keynames(82)= "Numpad 0"
	Keynames(83)= "Numpad ."

	Keynames(87)= "F11"
	Keynames(88)= "F12"

	Keynames(100)= "F13"
	Keynames(101)= "F14"
	Keynames(102)= "F15"
	
	Keynames(141)= "Numpad ="
	
	Keynames(156)= "Numpad Enter"
	Keynames(157)= "Right Ctrl"
	
	Keynames(181)= "Numpad /"
	
	Keynames(183)= "Sys RQ"
	Keynames(184)= "Right Alt"
	
	Keynames(197)= "Pause"
	
	Keynames(199)= "Home"
	Keynames(200)= "Up Arrow"
	Keynames(201)= "Page Up"
	
	Keynames(203)= "Left Arrow"
	
	Keynames(205)= "Right Arrow"
	
	Keynames(207)= "End"
	Keynames(208)= "Down Arrow"
	Keynames(209)= "Page Down"
	Keynames(210)= "Insert"
	Keynames(211)= "Delete"
	
	Keynames(219)= "Left Windows"
	Keynames(220)= "Right Windows"



;Include "scancodes.bb"
;Include "fps.bb"
;Include "graphics_manipulation.bb"

; fps

Type myfps
	Field timer,counter,fps
End Type

Global myfps.myfps = New myfps


Type backdrop
	Field bmap
	Field bmap2
End Type
Global backdrop.backdrop = New backdrop


Dim enemywave(32,32)
Type wave
	Field x#,y#
	Field w,h
	Field maxleft
	Field dir ; direction of wave
End Type

Global wave.wave = New wave

Type player
	Field bmap
	Field x,y
	Field firedelay
	Field lives
End Type

Type concrete
	Field bmap
	Field x,y
End Type

Type sprites
	Field laser1
	Field concrete1
	Field alien1
End Type

Global sprites.sprites = New sprites
inilasersprites

Type lasers
	Field x#,y#
	Field speedy#
End Type

Type alienlasers
	Field x#,y#
	Field speedy#
End Type

Type particles
	Field x#,y#
	Field mx#,my#
	Field ux#,uy#
	Field timeout
End Type

Global player.player = New player
player\lives = 3

inibackdrop
iniconcretesprites
inialiensprites
iniconcrete
iniwave(1)
iniplayer

While KeyDown(1) = False
	Cls
	DrawBlock backdrop\bmap,0,0
	updatefps
	updatelasers
	updatewave
	updatealienlasers
	playerlaserconcretecollision
	alienlaserconcretecollision
	alienlaserplayercollision
	mousealiencollision
	laseraliencollision
	alienfirelaser
	userinput
	drawconcrete
	drawlasers
	drawalienlasers
	drawplayer
	drawwave
	drawparticles
	updateparticles
	wavealive
	gameinfo
	If MouseHit(2) = True Then iniexplosion(MouseX(),MouseY())
	Color 255,255,255
	Text 320,GraphicsHeight()-32,"Sync Rate : " + myfps\fps
	Flip
Wend
End

Function inibackdrop()
	backdrop\bmap = CreateImage(GraphicsWidth(),GraphicsHeight())
	SetBuffer ImageBuffer(backdrop\bmap)
	ClsColor 0,0,100
	Cls
	shadeareaup(backdrop\bmap,0,0,GraphicsWidth(),GraphicsHeight(),120)
	;
	noisefilter(backdrop\bmap)
	mosiacarea(backdrop\bmap,0,0,640,480,16,16)
	SetBuffer BackBuffer()

End Function

Function gameinfo()
	Text 0,GraphicsHeight()-32,"Lives : " + player\lives
End Function 

Function gamerules()
	player\lives = player\lives - 1
	player\x = GraphicsWidth()/2
	For this.alienlasers = Each alienlasers
		Delete this
	Next
	If player\lives = 0 Then
		Delay(1000)
		iniwave(1)
		player\x = GraphicsWidth()/2
		player\lives = 3
	End If
End Function

Function alienlaserplayercollision()
	For this.alienlasers = Each alienlasers
		If RectsOverlap(player\x,player\y,16,16,this\x,this\y,8,8) = True Then
			If ImagesOverlap(player\bmap,player\x,player\y,sprites\laser1,this\x,this\y) = True Then
			;iniwave(1)
			gamerules()
			End If
		End If
	Next
End Function

Function wavealive()
	For x=0 To wave\w
	For y=0 To wave\h
		If enemywave(x,y) = True Then Return
	Next:Next
	iniwave(1)
	iniconcrete
	If difficulty > 30 Then difficulty = difficulty - 5
End Function

Function drawparticles()
	For this.particles = Each particles
		Rect this\x,this\y,3,3
	Next
End Function

Function updateparticles()

	For this.particles = Each particles
		this\x = this\x + this\mx
		this\y = this\y + this\my
	
		this\my = this\my + this\uy
	
		If this\timeout < MilliSecs() Then Delete this
	Next

End Function


Function iniexplosion(x#,y#)
	For i=0 To 15
		this.particles = New particles
		this\x = x
		this\y = y
		this\mx = Rnd(8)-4
		this\my = Rnd(8)-4
		this\ux = Rnd(.5)
		this\uy = Rnd(.5)
		this\timeout = MilliSecs() + 300+Rand(300)
	Next
End Function


Function alienfirelaser()
	If Rand(difficulty) = 1 Then 
	x = Rand(wave\w)
	If wavexalive(x) = True Then
		this.alienlasers = New alienlasers
		this\x = x*32+wave\x+8
		this\y = (wavexbottom(x)*32)+wave\y+16
		this\speedy = 1.5
		End If
	End If
End Function

Function alienlaserconcretecollision()
	For this.alienlasers = Each alienlasers		
		If this\y> GraphicsHeight()-256 Then		
			For that.concrete = Each concrete
				If RectsOverlap(this\x,this\y,8,8,that\x,that\y,32,32) Then
					If ImagesCollide(sprites\laser1,this\x,this\y,0,that\bmap,that\x,that\y,0) = True Then
						SetBuffer ImageBuffer(that\bmap)
	;					DebugLog "x : " + (this\x-that\x)
	;					DebugLog "y : " + (this\y-that\y)
						x = this\x - that\x
						y = this\y - that\y
						Color 0,0,0
						Oval x,y,9,9,True
						Deleteflag = True				
						SetBuffer BackBuffer()
					End If
				EndIf
			Next
			If deleteflag = True Then Delete this : deleteflag = False
		End If		
	Next
End Function

Function updatealienlasers()
	For this.alienlasers = Each alienlasers
		this\y = this\y + this\speedy
		If this\y > GraphicsHeight() + 32 Then Delete this
	Next
End Function

Function drawalienlasers()
	For this.alienlasers = Each alienlasers
		DrawImage sprites\laser1,this\x,this\y
	Next
End Function

Function wavexbottom(x)
	For y=wave\h To 0 Step -1
		If enemywave(x,y) = True Then Return y
	Next
End Function

Function wavexalive(x)
	For y=0 To wave\h
		If enemywave(x,y) = True Then Return True
	Next
End Function



Function laseraliencollision()
	For this.lasers = Each lasers
	x = (this\x-wave\x)/32
	y = (this\y-wave\y)/32
	If RectsOverlap(x,y,1,1,0,0,wave\w+1,wave\h+1) Then
		If enemywave(x,y) = True Then
	
		If ImagesOverlap(sprites\laser1,this\x,this\y,sprites\alien1,x*32+wave\x,y*32+wave\y) = True
			enemywave(x,y) = False
			iniexplosion(this\x,this\y)	
			Delete this
		End If
		
		End If
	End If
	Next
End Function

Function mousealiencollision()
	If MouseHit(1) = True Then
	x = (MouseX()-wave\x)/32
	y = (MouseY()-wave\y)/32
	If x<0 Or y<0 Then Return
	If enemywave(x,y) = True Then enemywave(x,y) = False
		adjustwave
		;If RectsOverlap(MouseX(),MouseY(),1,1,x,y,16,16)
	End If
	;Text 120,0,":x : " + x
End Function

Function adjustwave()
For x=0 To wave\w	
	For y=0 To wave\h
		If enemywave(x,y) = True Then Return
	Next
	;
	wave\maxleft = (-x*32)-32
	;
Next
End Function

Function drawwave()
	For y=0 To wave\h
	For x=0 To wave\w
		If enemywave(x,y) = True Then
		DrawImage sprites\alien1,wave\x+x*32,y*32
		EndIf
	Next:Next
End Function

Function updatewave(gamespeed = 2)
	For i=0 To gamespeed
		Select wave\dir
			Case 0
				wave\x = wave\x + .5
			Case 1
				wave\x = wave\x - .5

		End Select
		If wave\x+(wave\w*32) > GraphicsWidth()-32 Then 
			wave\dir = 1
		End If
		If wave\x < wave\maxleft Then 
		wave\dir = 0
		End If
	Next
End Function

Function iniwave(num)
	Select num
	Case 1
		wave\w = 10
		wave\h = 5
		wave\maxleft = 0
		wave\x = GraphicsWidth()/2-5*32
		For x=0 To 10
		For y=0 To 5
		enemywave(x,y) = True
		Next:Next
	End Select
End Function

Function playerlaserconcretecollision()
	For this.lasers = Each lasers		
		If this\y> GraphicsHeight()-256 Then		
			For that.concrete = Each concrete
				If RectsOverlap(this\x,this\y,8,8,that\x,that\y,32,32) Then
					If ImagesCollide(sprites\laser1,this\x,this\y,0,that\bmap,that\x,that\y,0) = True Then
						SetBuffer ImageBuffer(that\bmap)
	;					DebugLog "x : " + (this\x-that\x)
	;					DebugLog "y : " + (this\y-that\y)
						x = this\x - that\x
						y = this\y - that\y
						Color 0,0,0
						Oval x,y-2,9,9,True
						Deleteflag = True				
						SetBuffer BackBuffer()
					End If
				EndIf
			Next
			If deleteflag = True Then Delete this
		End If		
	Next
End Function

Function userinput(gamespeed = 2)
	oldx = player\x
	oldy = player\y
	For i=0 To gamespeed
		If KeyDown(key_arrow_left) Then
			player\x = player\x - 1
		End If
		If KeyDown(key_arrow_right) Then
			player\x = player\x + 1
		End If
		If KeyDown(key_space) Then 
			playerfirelaser
		End If
	Next
End Function

Function playerfirelaser()
	If player\firedelay > MilliSecs() Then Return
	inilaser(player\x+4,player\y-4)
	player\firedelay = MilliSecs() + 350
End Function


Function updatelasers(gamespeed = 2)
	For i=0 To gamespeed
		For this.lasers = Each lasers
			this\y = this\y + this\speedy
			deleteout = False
			If this\y<-64 Then deleteout = True 
			If this\y>GraphicsHeight()+32 Then deleteout = True
			If deleteout = True Then Delete this

		Next		
	Next
End Function

Function drawlasers()
	For this.lasers = Each lasers
		DrawImage sprites\laser1,this\x,this\y
	Next
End Function


Function inilaser(x,y)
	this.lasers = New lasers
	this\x = x
	this\y = y
	this\speedy = -1
End Function

Function drawconcrete()
	For this.concrete = Each concrete
		DrawImage this\bmap,this\x,this\y
	Next
End Function

Function iniconcrete()
	For x=16 To GraphicsWidth() - 32 Step 64
		this.concrete = New concrete
		this\bmap = CopyImage(sprites\concrete1)
		this\x = x
		this\y = GraphicsHeight() - 110
	Next
End Function

Function inilasersprites()
	sprites\laser1 = CreateImage(8,8)
	SetBuffer ImageBuffer(sprites\laser1)
	Color 255,255,255
	Rect 2,0,4,8,True
	SetBuffer BackBuffer()
End Function


Function drawplayer()
	DrawImage player\bmap,player\x,player\y
End Function

Function iniplayer()
	player\x = GraphicsWidth() / 2
	player\y = GraphicsHeight() - 64
	player\bmap = CreateImage(16,16)
	createplayerimage()
End Function

Function inialiensprites()
	sprites\alien1 = CreateImage(16,16)
	SetBuffer ImageBuffer(sprites\alien1)
	Restore aliensprite1
	Color 255,255,255
	For y=0 To 7
	For x=0 To 7
		Read a
		If a = 1 Then 
			Rect x*2,y*2,2,2,True
		End If
	Next:Next
	SetBuffer BackBuffer()
End Function

Function iniconcretesprites()
	sprites\concrete1 = CreateImage(32,32)
	SetBuffer ImageBuffer(sprites\concrete1)
	Restore concrete1
	Color 255,255,255
	For y=0 To 7
	For x=0 To 7
		Read a
		If a = 1 Then
			Rect x*4,y*4,4,4
		End If
	Next:Next
	SetBuffer BackBuffer()
End Function

Function createplayerimage()
	SetBuffer ImageBuffer(player\bmap)
	ClsColor 0,0,0
	Color 255,255,255
	Restore player_sprite
	For y=0 To 7
	For x=0 To 7
		Read a
		If a = 1 Then
			Rect x*2,y*2,2,2
		End If
	Next:Next
	SetBuffer BackBuffer()
End Function

.concrete1
Data 0,0,0,0,0,0,0,0
Data 0,1,1,0,0,1,1,0
Data 1,1,1,1,1,1,1,1
Data 1,1,1,1,1,1,1,1
Data 1,1,1,1,1,1,1,1
Data 1,1,1,1,1,1,1,1
Data 1,1,1,1,1,1,1,1
Data 1,1,1,1,1,1,1,1
.player_sprite
Data 0,0,0,1,1,0,0,0
Data 0,0,0,1,1,0,0,0
Data 0,0,1,1,1,1,0,0
Data 0,0,1,1,1,1,0,0
Data 0,0,1,1,1,1,0,0
Data 0,1,1,1,1,1,1,0
Data 0,1,1,1,1,1,1,0
Data 0,1,1,1,1,1,1,0
.aliensprite1
Data 0,0,1,1,1,1,0,0
Data 0,1,1,1,1,1,1,0
Data 0,1,1,1,1,1,1,0
Data 1,1,1,1,1,1,1,1
Data 1,1,1,1,1,1,1,1
Data 1,1,0,0,0,0,1,1
Data 0,1,1,0,0,1,1,0
Data 0,0,1,0,0,1,0,0

;Standard functions for converting colour to RGB values, for WritePixelFast and ReadPixelFast
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

Function clipval(in)
	If in>255 Then Return 255
	If in<0 Then Return 0
	Return in
End Function

Function updatefps()
	myfps\counter = myfps\counter + 1
	If myfps\timer < MilliSecs() Then
		myfps\fps = myfps\counter
		myfps\counter = 0
		myfps\timer = MilliSecs() + 1000
	End If
End Function

;
; Graphics manipulation
;
Function scratchdownarea(im,x,y,w,h)
	SetBuffer ImageBuffer(im)
	LockBuffer ImageBuffer(im)
	;
	For x1=0 To ImageWidth(im)
	rgb = ReadPixelFast(x1,y)
	For y1=y To y+(h-3+Rand(1,3))
	If cnt=4 Then rgb = ReadPixelFast(x1,y1) : cnt=1
	cnt=cnt+1
	If RectsOverlap(x1,y1,1,1,x,y,w,h)
	WritePixelFast x1,y1,rgb
	End If
	Next
	Next
	;
	UnlockBuffer ImageBuffer(im)
	SetBuffer BackBuffer()
	Return im
End Function



Function mosiacarea(im,x,y,w,h,sx,sy)
	SetBuffer ImageBuffer(im)
	LockBuffer ImageBuffer(im)
	While y1<ImageHeight(im) - sy
	While x1<ImageWidth(im) ;- sx
	If RectsOverlap(x1,y1,1,1,x,y,w,h)
		rgb = ReadPixelFast(x1,y1)
		For x2=x1+2 To x1+(sx-2)
		For y2=y1+2 To y1+(sy-2)
			If RectsOverlap(x2,y2,1,1,x,y,w,h)				
				WritePixelFast(x2,y2,rgb)
			End If
		Next
		Next
	End If
	x1=x1+sx
	Wend
	x1=0
	y1=y1+sy
	Wend
	UnlockBuffer(ImageBuffer(im))
	SetBuffer BackBuffer()
	Return im
End Function

Function colorizearea(im,x,y,w,h,r1#=100,g1#=100,b1#=100)
	SetBuffer ImageBuffer(im)
	LockBuffer(ImageBuffer(im))
	For y1=0 To ImageHeight(im)
		For	x1=0 To ImageWidth(im)
			;
			If RectsOverlap(x1,y1,1,1,x,y,w,h) = True
				rgb = ReadPixelFast(x1,y1)
				r# = getr(rgb)
				g# = getg(rgb)
				b# = getb(rgb)
				;
				r2 = clipval(r+r1)
				g2 = clipval(g+g1)
				b2 = clipval(b+b1)
				;
				rgb2 = getrgb(r2,g2,b2)
				WritePixelFast x1,y1,rgb2
			End If
			;

		Next
	Next
	UnlockBuffer(ImageBuffer(im))
	SetBuffer BackBuffer()
	Return im
End Function
Function shadearearightdown(im,x,y,w,h,col# = 100)
	Local cy# = col
	SetBuffer ImageBuffer(im)
	LockBuffer ImageBuffer(im)
	;
	For x1=0 To w
	For y1=0 To h
		If RectsOverlap(x1,y1,1,1,x,y,w,h)
			rgb = ReadPixelFast(x1,y1)
			;
			r# = getr(rgb)
			g# = getg(rgb)
			b# = getb(rgb)
			;
			r1# = clipval(r/100*cy)
			g1# = clipval(g/100*cy)
			b1# = clipval(b/100*cy)
			;
			WritePixelFast x1,y1,getrgb(r1,g1,b1)		
		End If
		;
	Next
		If x1>x 
			cy#=cy#-(100/w)
		End If
	Next
	;
	UnlockBuffer ImageBuffer(im)
	SetBuffer BackBuffer()
	Return im
End Function



Function shadearealeftup(im,x,y,w,h,col# = 100)
	Local cy# = col
	SetBuffer ImageBuffer(im)
	LockBuffer ImageBuffer(im)
	;
	For x1=0 To 32
	For y1=0 To 32
		If RectsOverlap(x1,y1,1,1,x,y,w,h)
			rgb = ReadPixelFast(x1,y1)
			;
			r# = getr(rgb)
			g# = getg(rgb)
			b# = getb(rgb)
			;
			r1# = clipval(r/100*cy)
			g1# = clipval(g/100*cy)
			b1# = clipval(b/100*cy)
			;
			WritePixelFast x1,y1,getrgb(r1,g1,b1)		
		End If
		;
	Next
		If x1>x
			cy#=cy#+(100/w)
		End If
	Next
	;
	UnlockBuffer ImageBuffer(im)
	SetBuffer BackBuffer()
	Return im
End Function

Function shadeareaup(im,x#,y#,w#,h#,col#=50)
Local cy# = col
SetBuffer ImageBuffer(im)
LockBuffer(ImageBuffer(im))
For y1=0 To ImageHeight(im)
	For	x1=0 To ImageWidth(im)
	If RectsOverlap(x1,y1,1,1,x,y,w,h) = True Then
	rgb = ReadPixelFast(x1,y1)

	r# = getr(rgb)
	g# = getg(rgb)
	b# = getb(rgb)

	r = clipval((r/100)*cy)
	g = clipval((g/100)*cy)
	b = clipval((b/100)*cy)

	rgb2 = getrgb(r,g,b)

	WritePixelFast x1,y1,rgb2

	End If
	Next
	If y1>y And y1<y+h Then
	cy#=cy#+(col/h)
	End If
Next
UnlockBuffer(ImageBuffer(im))
SetBuffer BackBuffer()
Return im
End Function


Function shadeareadown(im,x#,y#,w#,h#,col#=50)
Local cy# = col
SetBuffer ImageBuffer(im)
LockBuffer(ImageBuffer(im))
For y1=0 To ImageHeight(im)
	For	x1=0 To ImageWidth(im)
	If RectsOverlap(x1,y1,1,1,x,y,w,h) = True Then
		rgb = ReadPixelFast(x1,y1)

		r# = getr(rgb)
		g# = getg(rgb)
		b# = getb(rgb)

		r = clipval((r/100)*cy)
		g = clipval((g/100)*cy)
		b = clipval((b/100)*cy)

		rgb2 = getrgb(r,g,b)

		WritePixelFast x1,y1,rgb2

		End If
	Next
	If y1>y And y1<y+h Then
		cy#=cy#-col/h
	End If
Next
UnlockBuffer(ImageBuffer(im))
SetBuffer BackBuffer()
Return im
End Function


Function noisefilter(im)
	SetBuffer ImageBuffer(im)
	w = ImageWidth(im)
	h = ImageHeight(im)
	LockBuffer ImageBuffer(im)

	For x=0 To w-1
	For y=0 To h-1
		rgb = ReadPixelFast(x,y)
		r# = getr(rgb)
		g# = getg(rgb)
		b# = getb(rgb)
		;
		r = clipval(r/100*Rand(90,110))
		g = clipval(g/100*Rand(90,110))
		b = clipval(b/100*Rand(90,110))
		;
		rgb2 = getrgb(r,g,b)
		;	
		WritePixelFast x,y,rgb2
	Next:Next
	UnlockBuffer ImageBuffer(im)
	SetBuffer BackBuffer()
	Return im
End Function

Function noise(im,x,y)
	LockBuffer ImageBuffer(im)
	rgb = ReadPixelFast(x,y,ImageBuffer(im))
	r# = getr(rgb)
	g# = getg(rgb)
	b# = getb(rgb)
	;
	r = clipval(r/100*Rand(90,110))
	g = clipval(g/100*Rand(90,110))
	b = clipval(b/100*Rand(90,110))
	;
	rgb2 = getrgb(r,g,b)
	;	
	WritePixelFast x,y,rgb2,ImageBuffer(im)
	UnlockBuffer ImageBuffer(im)
End Function


Function brighten(im,x,y,val)
	LockBuffer ImageBuffer(im)
	rgb = ReadPixelFast(x,y,ImageBuffer(im))
	r# = getr(rgb)
	g# = getg(rgb)
	b# = getb(rgb)
	;
	r =clipval((r / 100) * val)
	g =clipval((g / 100) * val)
	b =clipval((b / 100) * val)
	;
	rgb2 = getrgb(r,g,b)
	;
	WritePixelFast x,y,rgb2,ImageBuffer(im)
	UnlockBuffer ImageBuffer(im)
End Function
