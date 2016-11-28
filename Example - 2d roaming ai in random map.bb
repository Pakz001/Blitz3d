Graphics 800,640,0,2
SetBuffer BackBuffer()

SeedRnd MilliSecs()

Global numagents%=20
Global mapwidth%=30
Global mapheight%=30
Global tilewidth%=Int(800/mapwidth)
Global tileheight%=Int(640/mapheight)

Dim map(mapwidth,mapheight) 

SeedRnd MilliSecs()

Type agents
    Field x#,y#
    Field angle%
End Type

makegame


Global timer = CreateTimer(60)


While Not KeyHit(1)
	WaitTimer timer
    Cls
    Color 255,255,255
    ;drawmap
	If KeyHit(57) Then makegame
	If counter>60*20 Then makegame : counter=0
	counter=counter+1
    drawmap()
    update
    draw
    Color 255,255,255
    Text 0,0,"Ray Casting roaming ai example."
    Flip
Wend
End

Function update()
	For ag.agents = Each agents
        ag\x = ag\x + Cos(ag\angle)*3
        ag\y = ag\y + Sin(ag\angle)*3
        ; here we check 64 pixels in the angle we are going into
        ; if there is a wall there then the obstr boolean is set
        Local obstr=False
        For i=0 To 64 
            Local x1=(ag\x)+Cos(ag\angle)*i
            Local y1=(ag\y)+Sin(ag\angle)*i
            Local x2%=x1/tilewidth
            Local y2%=y1/tileheight
            If x2>-1 And y2>-1 And x2<=mapwidth And y2<=mapheight
                If map(x2,y2) > 0 Then 
                    obstr = True
					Exit                    
                End If
            End If
        Next
        ; if obstructed then turn
        If obstr = True
            ag\angle = ag\angle + 16
        End If
        ; random movement
        If Rnd(10)<2 Then ag\angle = ag\angle + Rnd(-15,15)
        ; keep angle in check
        If ag\angle>180 Then ag\angle = -180
        If ag\angle<-180 Then ag\angle = 180
    Next
End Function

Function draw()
    For ag.agents = Each agents
        Color 255,255,255
        Oval ag\x,ag\y,10,10
        Line ag\x+5,ag\y+5,ag\x+Cos(ag\angle)*64,ag\y+Sin(ag\angle)*64
    Next
End Function


;Function drawmap()
;    Color 200,200,200
;    For y=0 To mapheight 
;        For x=0 To mapwidth 
;            If map(y,x) > 0
;                Color map(y,x),200,200 
;                Rect x*tilewidth,y*tileheight,tilewidth,tileheight
;            End If
;        Next
;    Next
;End Function

Function makemap(steps = 100)
    Local aproved = False
    While aproved = False
        For y = 0 To mapheight
        For x = 0 To mapwidth
            map(x,y) = 0
        Next
        Next
        x = mapwidth / 2
        y = mapheight / 2
        steps = Rand(500) + 500
        For i=0 To steps
            nstepf = False
            While nstepf = False
                dir = Rand(8)
                Select dir
                    Case 1 : nx = x - 1 : ny = y - 1
                    Case 2 : ny = y - 1
                    Case 3 : nx = x + 1 : ny = y - 1
                    Case 4 : nx = x - 1
                    Case 5 : nx = x + 1
                    Case 6 : nx = x - 1 : ny = y + 1
                    Case 7 : ny = y + 1
                    Case 8 : nx = x + 1 : ny = y + 1
                End Select
                    If nx < mapwidth-1 And nx > 0 And ny < mapheight-1 And ny > 0 Then
                        x = nx
                        y = ny
                        nstepf = True
                    End If
            Wend
            drawbrush(x,y) 
        Next
        aproved = True
        For y=0 To mapheight
            If map(0,y) = 1 Then aproved = False
        Next
        For y=0 To mapheight
            If map(mapwidth,y) = 1 Then aproved = False
        Next
        For x=0 To mapwidth
            If map(x,0) = 1 Then aproved = False
        Next
        For x=0 To mapwidth
            If map(x,mapheight) = 1 Then aproved = False
        Next
        For y=0 To mapheight
            For x=mapwidth-7 To mapwidth
                If map(x,y) = 1 Then aproved = False
            Next
        Next
        hasone = False
        For y=0 To mapheight
            If map(mapwidth-8,y) = 1 Then hasone = True
        Next
        If hasone = False Then aproved = False
        hasone = False
        For y=0 To mapheight
            If map(3,y) = 1 Then hasone = True
        Next
        If hasone = False Then aproved = False
        hasone = False
        For x=0 To mapwidth
            If map(x,3) = 1 Then hasone = True
        Next
        If hasone = False Then aproved = False
        hasone = False
        For x=0 To mapwidth
            If map(x,mapheight-3) = 1 Then hasone = True
        Next
        If hasone = False Then aproved = False
    Wend
	; turn the 0 into 1(blocked) and
	; 1 into 0 (open)
	; map() is a collision map sort of
	For y = 0 To mapheight
	For x = 0 To mapwidth
		If map(x,y) = 1 Then map(x,y)=0 Else map(x,y)=1
	Next
	Next
End Function 

; here we draw into the map to create the level
Function drawbrush(x,y)
    x1 = x - 1
    y1 = y - 1
    If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
        map(x1,y1) = 1
    End If
    x1 = x
    y1 = y - 1
    If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
        map(x1,y1) = 1
    End If
    x1 = x + 1
    y1 = y - 1
    If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
        map(x1,y1) = 1
    End If
    x1 = x - 1
    y1 = y 
    If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
        map(x1,y1) = 1
    End If
    x1 = x + 1
    y1 = y
    If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
        map(x1,y1) = 1
    End If
    x1 = x - 1
    y1 = y + 1
    If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
        map(x1,y1) = 1
    End If
    x1 = x 
    y1 = y + 1
    If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
        map(x1,y1) = 1
    End If
    x1 = x + 1
    y1 = y + 1
    If x1 > 0 And x1 < mapwidth And y1 > 0 And y1 < mapheight Then
        map(x1,y1) = 1
    End If
End Function


Function drawmap()
    Color 200,200,200
    For y = 0 To mapheight
        For x = 0 To mapwidth
            If map(x,y) > 0 Then
                Color map(x,y),200,200 
                ;Rect x*16,y*16,16,16,True
                Rect x*tilewidth,y*tileheight,tilewidth,tileheight
            End If
        Next
    Next
End Function

Function makegame()
	For y=0 To mapheight
	For x=0 To mapwidth
		map(x,y)=0
	Next
	Next
	Delete Each agents

	Select Rand(1,3)
		Case 1
		mapwidth=30
		mapheight=30
		Case 2
		mapwidth=40
		mapheight=40
		Default
		mapwidth=50
		mapheight=50
	End Select
	Dim map(mapwidth,mapheight)
	makemap()
	numagents = Rand(2,20)
	tilewidth=GraphicsWidth()/mapwidth
	tileheight=GraphicsHeight()/mapheight
	For i=0 To numagents
   		exitloop = False
    	While exitloop = False
        	x=Int(Rnd(mapwidth))
        	y=Int(Rnd(mapheight))
        	If map(x,y) = 0
            	ag = Create((x * tilewidth)+tilewidth/2, (y * tileheight)+tileheight/2 )
            	exitloop = True
        	End If
    	Wend
	Next
End Function

Function Create(x#,y#)
    ag.agents = New agents
    ag\x = x
    ag\y = y
    ag\angle=Rnd(-180,180)
End Function
