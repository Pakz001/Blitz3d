;
;
Graphics 640,480,32,2
SetBuffer BackBuffer()

Global im = LoadImage("sand.bmp")
If im = 0 Then RuntimeError "error"
Dim c(96,96)
While KeyDown(1) = False
	Cls
	DrawImage im,0,0
	If KeyHit(2) Then convert
	If KeyHit(3) Then writeout
	Flip
Wend
End
Function writeout()
	Local krr$[98]
	For y=0 To 95:
	a$ = a$ + "Data "
	For x=0 To 95
		a$=a$ + c(x,y)
		If x<95 Then a$=a$+ ","
	Next
	a$ = a$ + Chr(13)+chr(10)
	Next
	;
	f = WriteFile("te.txt")	
		WriteLine(f,a$)	
	CloseFile(f)
	;
	ExecFile "notepad te.txt"
End Function
Function convert()
	For y=0 To ImageHeight(im)-1
		For x=0 To ImageWidth(im)-1
			a = ReadPixel(x,y)
			c(x,y) = a
		Next
	Next
	;
	DebugLog "er"
	;
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


