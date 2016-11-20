;
;
;

Graphics 640,480,16,2
SetBuffer BackBuffer()


Type talkballoon
	Field x#,y#,w#,h#
	Field txt$
	Field style
	Field tw,th
End Type

Function newtalkballoon(x,y,w,h,in$,style)
this.talkballoon = New talkballoon
this\x = x
this\y = y
this\w = w
this\h = h
this\style = style
this\txt$ = in$
this\tw = StringWidth(in$)
this\th = StringHeight(in$)
End Function

Function drawtalkballoons()
Local im
For this.talkballoon = Each talkballoon
im = CreateImage(this\w+wm,this\h+hm)
SetBuffer ImageBuffer(im)





Color 200,200,200

Local vx#[10]
Local vy#[10]
Local vw#[10]
Local vh#[10]

vx[0] = 0
vy[0] = 0
vw[0] = 10
vh[0] = 10

vx[1] = this\w-10
vy[1] = 0
vw[1] = 10
vh[1] = 10

vx[2] = 0
vy[2] = this\h-10
vw[2] = 10
vh[2] = 10

vx[3] = this\w-10
vy[3] = this\h-10
vw[3] = 10
vh[3] = 10

Color 200,200,200
For i=0 To 3
	Oval vx[i],vy[i],vw[i],vh[i],True
Next
Color 200,200,200
For i=0 To 3
	Oval vx[i]+2,vy[i]+2,vw[i]-4,vh[i]-4,True
Next

;Oval 0			,0			,10,10,True
;Oval this\w-10	,0			,10,10,True
;Oval 0			,this\h-10	,10,10,True
;Oval this\w-10	,this\h-10	,10,10,True


;Color 0,255,0

vx[0] = 4
vy[0] = 0
vw[0] = this\w-8
vh[0] = 8
vx[1] = 4
vy[1] = this\h-8
vw[1] = this\w-8
vh[1] = this\h
vx[2] = 0
vy[2] = 4
vw[2] = 8
vh[2] = this\h-8
vx[3] = this\w-8
vy[3] = 4
vw[3] = 8
vh[3] = this\h-8

Color 200,200,200
For i=0 To 3
	Rect vx[i],vy[i],vw[i],vh[i],True
Next



;Rect 4,0,this\w-8,8,True
;Rect 4,this\h-8,this\w-8,this\h,True
;Rect 0,4,8,this\h-8,True
;Rect this\w-8,4,8,this\h-8,True


;Rect 4,this\h-8,this\w-8,4,True


Select this\style
	Case 0
	Color 255,255,255
	Rect 3,3,this\w-6,this\h-6,True
	Case 1
	Color 255,255,255
	Rect 4,4,this\w-8,this\h-8,True
	Case 2
	Color 255,255,255
	Rect 4,4,this\w-8,this\h-8,True
End Select


SetBuffer BackBuffer()
DrawImage im,this\x,this\y
FreeImage im
Next
End Function

For i=0 To 20
	newtalkballoon(Rand(350),Rand(350),Rand(100,200),Rand(100,200),"hey hallo",0)
Next
While KeyDown(1) = False
	Cls
	drawtalkballoons()
	Flip
Wend
End
