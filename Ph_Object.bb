Include "Ph_CollisionBox.bb"


Type Ph_Object
	Field Image
	Field Pos#[1]   ; m
	Field Vel#[1]   ; m/s
	Field Acc#[1]   ; m/s�
	Field Rot#      ; rad
	Field RotVel#   ; rad/s
	Field RotAcc#   ; rad/s�
	Field Mass#     ; kg
	Field RotMass#  ; kg/px�
	Field CollisionBox.Shape
	Field Virtual
End Type

Function Ph_DoTick(Obj.Ph_Object, Time#)
	Local a#[1]
	; Apply Acceleration to Velocity
	
	MultiplyVector(Obj\Acc, Time, a)
	
	AddVector(Obj\Vel,a,Obj\Vel)
	Obj\RotVel=Obj\RotVel+Obj\RotAcc*Time
	
	; Apply Velosity to Posiion
	
	MultiplyVector(Obj\Vel, Time, a)
	
	AddVector(Obj\Pos,a,Obj\Pos)
	Obj\Rot=Obj\Rot+Obj\RotVel*Time
	
    ; reset Acceleration
	Obj\Acc[0] = 0
	Obj\Acc[1] = 0
	Obj\RotAcc = 0
End Function

Function Ph_ApplyForce(Obj.Ph_Object, Force#[1], approach#[1], Relative = True)
	If Relative Then
		RotateVector(Force,Obj\Rot,Force)
		RotateVector(approach,Obj\Rot,approach)
	EndIf
	
	If VectorLenght(Force)=0 Then Return
	If VectorLenght(approach)=0 Then
		Ph_ApplyVelForce(Obj,Force)
	Else
		
		
			
		Local a#[1]
		MultiplyVector(approach,-1,a)
		Local Angle# = RadToDeg(VectorAngle(a,Force))
		Ph_ApplyRotTorque(Obj,Sin(Angle)*VectorLenght(Force)*VectorLenght(approach))
		Local b#[1]
		NormalizeVector(a,b)
		
		MultiplyVector(b, Sin(90-Angle)*VectorLenght(Force),b)
		
		Ph_ApplyVelForce(Obj,b)
	EndIf
	
End Function

Function Ph_ApplyVelForce(Obj.Ph_Object, Force#[1])
	Local a#[1]
	DivideVector(Force,Obj\Mass, a)
	AddVector(Obj\Acc,a,Obj\Acc)
End Function

Function Ph_ApplyRotTorque(Obj.Ph_Object, Torque#)
	Obj\RotAcc=Obj\RotAcc+Torque/Obj\RotMass
End Function

Function Ph_Render(Obj.Ph_Object)   ; 1 m ^= 100px
	Local temp = CopyImage(Obj\Image)
	RotateImage temp, RadToDeg(Obj\Rot)
	DrawImage temp,Obj\Pos[0]*100,Obj\Pos[1]*100
	FreeImage temp
End Function

Function Ph_GetVirtualCopyAfterTime.Ph_Object(obj.Ph_Object,t#)
	Local result.Ph_Object = New Ph_Object
	result\Acc[0] = obj\Acc[0]
	result\Acc[1] = obj\Acc[1]
	result\CollisionBox = obj\CollisionBox
	result\Mass = obj\Mass
	result\Pos[0] = obj\Pos[0]
	result\Pos[1] = obj\Pos[1]
	result\Rot = obj\Rot
	result\RotAcc = obj\RotAcc
	result\RotMass = obj\RotMass
	result\RotVel = obj\RotVel
	result\Vel[0] = obj\Vel[0]
	result\Vel[1] = obj\Vel[1]
	result\Virtual = True
	
	Ph_DoTick(result, t)
	Return result
End Function

;~IDEal Editor Parameters:
;~C#Blitz3D