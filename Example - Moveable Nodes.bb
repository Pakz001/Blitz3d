;
;
;
;

Graphics 640,480,16,2
SetBuffer BackBuffer()

Type pup
	Field nodex[10]
	Field nodey[10]
	Field active[10]
	Field connectto[10]
End Type
makenewpop

Function makenewpop()
	this.pup = New pup
	For i=0 To 10
		this\nodex[i] = Rand(96) + 100
		this\nodey[i] = Rand(96) + 100
		this\active[i]= False
		this\connectto[i] = -1
	Next	
End Function

Function drawpop()
For this.pup = Each pup
	For i=0 To 10
		Color 32,32,32
		Rect this\nodex[i],this\nodey[i],8,8,True
		Color 255,255,0
		Rect this\nodex[i],this\nodey[i],8,8,False
		If this\active[i] = True Then
			Color 255,0,0
			Rect this\nodex[i],this\nodey[i],8,8,True
		End If	
	Next
Next
End Function

Function deselectnodes()
	For this.pup =Each pup
		For i=0 To 10
			this\active[i] = False
		Next
	Next
End Function

Function popnodecol()
	If MouseDown(2) = True Then
	For this.pup = Each pup
		For i=0 To 10			
			If RectsOverlap(MouseX(),MouseY(),1,1,this\nodex[i],this\nodey[i],8,8) = True Then
				this\active[i] = True : Return
			End If
		Next
	Next	
	End If
	If MouseDown(1) = False Then Return
	deselectnodes
	For this.pup = Each pup
		For i=0 To 10			
			If RectsOverlap(MouseX(),MouseY(),1,1,this\nodex[i],this\nodey[i],8,8) = True Then
				this\active[i] = True : Return
			End If
		Next
	Next
End Function

Function connectto()
	If KeyDown(57) = True Then
		For this.pup = Each pup
			For i = 0 To 10
				If this\active[i] = True
					For ii = 0  To 10
						If i<>ii Then
						If this\active[ii] = True Then
							this\connectto[i ] = ii
							this\connectto[ii] = i
							this\active[i]  = False
							this\active[ii] = False
							Return
						End If
						End If
					Next
				End If
			Next
		Next
	End If
End Function

Function drawconnectto()
	For this.pup = Each pup
		For i=0 To 10
			;
			If this\connectto[i] > -1 Then
				x1 = this\nodex[i]
				y1 = this\nodey[i]
				x2 = this\nodex[this\connectto[i]]
				y2 = this\nodey[this\connectto[i]]
			;
			Line x1+4,y1+4,x2+4,y2+4
			End If
			;
		Next
	Next
End Function

Function movenode()
	;
	For this.pup = Each pup
	For i=0 To 10
		If this\active[i] = True Then
			Exit			
		End If
	Next
	If i>10 Then Return
	;
	If KeyDown(205) = True Then ; right
		this\nodex[i] = this\nodex[i] + 1
	End If
	If KeyDown(203) = True Then ; left
		this\nodex[i] = this\nodex[i] - 1	
	End If	
	If KeyDown(208) = True Then ; down
		this\nodey[i] = this\nodey[i] + 1	
	End If	
	If KeyDown(200) = True Then ; up
		this\nodey[i] = this\nodey[i] - 1	
	End If	
	;
	Next
End Function


While KeyDown(1) = False
	Cls
	;
	movenode
	drawconnectto
	connectto
	drawpop
	popnodecol
	Flip
Wend
End

Function dist#(x1,y1,x2,y2)
	Return Sqr((x1-x2)^2+(y1-y2)^2)
End Function
