   @i
   M=0
   @R2
   M=0
(LOOP)
   @i
   D=M
   @R0
   D=D-M
   @END
   D;JEQ
   @R1
   D=M
   @R2
   M=M+D
   @i
   M=M+1 //i++
   @LOOP
   0;JMP
(END)
   @END
   0;JMP

