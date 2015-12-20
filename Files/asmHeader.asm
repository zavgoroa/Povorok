;=============================================================================
        function segment
		assume cs:function,ds:data,ss:astack

		;опенапюгнбюмхе ярпнйх б вхякн (оепедювю оюпюлерпю вепег ярей, пегскэрюр б юу)
		;ХГ БНЯЭЛЕПХВМНИ Б ДЕЯЪРХВМСЧ
	        s_to_int proc far
			push bp
                        mov bp,sp

			xor ax,ax
			mov si,[bp+6]
			mov al,ds:[si]
    			cmp al,'-'
    			jnz iter
    			mov di,1
    			inc si
iter:
    			xor ax,ax
    			mov bx,10
repeat:
    			mov cl,ds:[si]
    			cmp cl,'$'
    			jz end_iter

    			sub cl,'0'
    			mul bx
    			add ax,cx
    			inc si
    			jmp repeat
end_iter:
    			cmp di,1
    			jnz out_pr
    			neg ax
out_pr:
			pop bp
	        	retf 2
		s_to_int endp

		;опенапюгнбюмхе вхякю б ярпнйс (оепедювю оюпюлерпю вепег AX, пегскэрюр б оепелеммни)
		;ХГ ДЕЯЪРХВМНИ Б БНЯЭЛЕПХВМСЧ
		int_to_s proc far
    			mov si,offset buf

   			test ax,ax
   			jns abs_n

			mov dx,'-'
   			mov ds:[si],dx
			inc si
   			neg ax
abs_n:
    			xor cx,cx
    			mov bx,10
alloc:
   		 	xor dx,dx
    			div bx
    			push dx
    			inc cx

    			test ax,ax
    			jnz alloc
out_m:
    			pop dx
    			add dl,'0'
    			mov ds:[si],dx
			inc si
			loop out_m

            mov dl,0Dh
            mov ds:[si],dl
            inc si
            mov dl,0Ah
            mov ds:[si],dl
            inc si
			mov dl,'$'
			mov ds:[si],dl

              		retf
		int_to_s endp
	function ends

;=============================================================================
	code segment
		assume cs:code,ds:data,ss:astack

		;опнжедспю бшбндю ярпнйх мю щйпюм (оюпюлерп б DX)
	        out_str proc near
			push ax
			mov ah,09h
			int 21h
			pop ax
		        ret
		out_str endp

		;нямнбмюъ опнжедспю опнцпюллш
		main proc far

		push ds
        sub ax,ax
        push ax

        mov ax,data
        mov ds,ax