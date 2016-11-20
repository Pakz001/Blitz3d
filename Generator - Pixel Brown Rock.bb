Graphics 640,480,16,2
SetBuffer BackBuffer()

Global blokgroottex = 32
Global blokgroottey = 32

Dim mapske(64,64)

Dim blokskes(64,64)
For x=0 To 64
For y=0 To 64
	blokskes(x,y) = CreateImage(32,32)
	SetBuffer ImageBuffer(blokskes(x,y))
	;ClsColor Rand(240,250),Rand(101,150),Rand(10,30)
	;ClsColor Rand(245,258),Rand(141,146),Rand(10,30)
	;Cls
	For x1=0 To 32 Step 8
	For y1=0 To 32 Step 8
		If Rand(7) = 1 Then
		Color Rand(240,250),Rand(101,150),Rand(10,30)
		Rect x1,y1,8,8,True
		End If
	Next:Next
	;
	For x1=0 To 32 Step 4
	For y1=0 To 32 Step 4
		If Rand(2) = 1 Then
		Color Rand(244,248),Rand(121,130),Rand(15,25)
		Rect x1,y1,4,4,True
		End If
	Next:Next
	;
	SetBuffer BackBuffer()	
Next:Next

;
;
While KeyDown(1) = False
	Cls
	tekenveldske()
;	ontplofblokske(Rand(0,32),Rand(0,32),Rand(0,32),Rand(0,32))
	Color 255,255,255:Text 0,400,AvailVidMem()
	Flip
Wend
End
;
;
Function ontplofblokske(x,y,x1,y1)
	Local neu = MilliSecs()
	
	;
	;
;	SetBuffer ImageBuffer(k\blokske)
;	Color 0,0,0
;	Oval x1-6,y1-6,12,12,True
;	SetBuffer BackBuffer()
;	mapske(x,y) = neu
	;
	kaaske = CreateImage(blokgroottex*2,blokgroottey*2)
	SetBuffer ImageBuffer(kaaske)
		;DrawBlock 	
	SetBuffer BackBuffer()
	;			
End Function
;
;
Function tekenveldske()
	;
	For x=0 To 10
	For y=0 To 10
	;
	;
	DrawImage blokskes(x,y),x*32,y*32
	;
	;
	Next:Next
	;
End Function
;
;
