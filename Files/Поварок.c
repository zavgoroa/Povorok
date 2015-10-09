<Recipe> ::= 
	<Recipe Title>
	[<Comments>]
	<Ingredient List>
	<Method>

<Recipe Title> ::= <String>.
<Comments> ::= <String>.

<Ingredient List> ::= 
	Ингредиенты.
	{<ingredient-name> <initial-value> [<measure>]}
	
<ingredient-name> ::= <String>
<initial-value> ::= <Integer>
<measure> ::= г | кг | ч.л. | шт.  	// сухие
<measure> ::= мл | л | ст.л.		// жидкие
// Переменные типизируются: сухие - int, жидкие - char

<Method> ::= 
	Способ приготовления.
	<Method statement>

<Method statement> := Возьмите из холодильника <ingredient>.	//STDIN -> ingredient
<Method statement> ::= Положите в холодильник <ingredient> .		//ingredient -> STDOUT
<Method statement> ::= Отправте блюдо в холодильник часа на <Integer>.	// Integer элементов стэка -> STDOUT

<Method statement> := Добавте <ingredient>. 	// push
<Method statement> := Положите <ingredient>. 	// pop
<Method statement> := Почистите <ingredient>. 	// + с top и push в стэк
<Method statement> := Натрите на тёрке <ingredient>. 	// - с top и push в стэк
<Method statement> := Замешайте <ingredient>.	// * с top и push в стэк
<Method statement> := Нарежте <ingredient>. 	// / с top и push в стэк
<Method statement> := Вымочить <ingredient>.	// каст ingredient к жидкому (ASCII)
<Method statement> := Добавте воды.			// каст стэка к жидкому
<Method statement> := Подготовте чистую посуду.	// очистит стэк

// Петли
<Method statement> := 
	Начинайте <Verb> <ingredient>. 		// начало цикла while ingredient != 0
	{<Method statement>}				// тело цикла
	<Verb> <ingredient> до <String>.	// конец цикла, ingredient--, String - комментарий
	// Пример:
	// Начинайте резать марковь. [тело цикла] Резать марковь до круглых колечек.  
	// Начинайте курить сигареты. [тело цикла] Курить сигареты до посинения.	
	
<Method statement> := Отложите всё в сторону.	// break

 


	


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	