;
; Roaming ai which raycasts
;  conversion by RustyKristy and R.v.Etten
;
;

Graphics 640,480,32,2
SetBuffer BackBuffer()

SeedRnd MilliSecs()

Const numagents%=50
Global mapwidth%=10
Global mapheight%=10
Global tilewidth%=640/mapwidth
Global tileheight%=480/mapheight

Dim map(10,10) 

Type agents
    Field x#,y#
    Field angle%
End Type

For y = 0 to 9
    For x = 0 To 9
        Read num ; Get the next data value in the data stack
        map(y,x) = num
    Next
Next

For i=0 to numagents
    exitloop = False
    While exitloop = False
        x=Int(Rnd(9))
        y=Int(Rnd(9))
        If map(y,x) = 0
            ag = Create((x * tilewidth)+tilewidth/2, (y * tileheight)+tileheight/2 )
            exitloop = True
        End If
    Wend
Next

Function Create(x#,y#)
    ag.agents = New agents
    ag\x = x
    ag\y = y
    ag\angle=Rnd(-180,180)
End Function

Global timer = CreateTimer(60)

While Not KeyHit(1)
	WaitTimer timer
    Cls
    Color 255,255,255
    drawmap
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
            If x2>-1 And y2>-1 And x2<mapwidth And y2<mapheight
                If map(y2,x2) > 0 Then 
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


Function drawmap()
    Color 200,200,200
    For y=0 To mapheight 
        For x=0 To mapwidth 
            If map(y,x) > 0
                Color map(y,x),200,200 
                Rect x*tilewidth,y*tileheight,tilewidth,tileheight
            End If
        Next
    Next
End Function


.level
Data 1,1,1,1,1,1,1,1,1,1
Data 1,0,0,0,0,0,0,0,0,1
Data 1,0,0,0,0,0,0,0,0,1
Data 1,0,0,0,0,0,1,1,1,1
Data 1,0,0,1,0,0,0,0,0,1
Data 1,0,0,1,0,0,0,0,0,1
Data 1,1,1,1,0,0,0,0,0,1
Data 1,0,0,0,0,0,0,1,0,1
Data 1,0,0,0,0,0,0,1,0,1
Data 1,1,1,1,1,1,1,1,1,1
