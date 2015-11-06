<Recipe> ::= 
	<Recipe Title>
	[<Comments>]
	<Ingredient List>
	<Method>

<Recipe Title> ::= <String>.
<Comments> ::= <String>.

<Ingredient List> ::= 
	Нужно купить.
	{<ingredient-name> <initial-value> [<measure>]}
	
<ingredient-name> ::= <String>
<initial-value> ::= <Integer>
<measure> ::= г | кг | чл | шт  	// сухие
<measure> ::= мл | л | стл			// жидкие
// Переменные типизируются: сухие - short, жидкие - char

<Method> ::= 
	Способ приготовления.
	{<Method statement>}

<Method statement> := Возьмите из холодильника <ingredient>.	//STDIN -> ingredient
<Method statement> ::= Положите в холодильник <ingredient> .		//ingredient -> STDOUT
<Method statement> ::= Отправте блюдо в холодильник.	// элементов стэка -> STDOUT

<Method statement> := Добавте <ingredient>. 	// push
<Method statement> := Положите <ingredient>. 	// pop
<Method statement> := Почистите <ingredient>. 	// + с top и push в стэк
<Method statement> := Натрите на тёрке <ingredient>. 	// - с top и push в стэк
<Method statement> := Замешайте <ingredient>.	// * с top и push в стэк
<Method statement> := Нарежте <ingredient>. 	// / с top и push в стэк
<Method statement> := Вымочить <ingredient>.	// каст ingredient к жидкому (ASCII)
<Method statement> := Добавте воды.			// каст стэка к жидкому
<Method statement> := Подготовте чистую посуду.	// очистит стэк


// Оператор ветвления
<Method statement> := 
	По желанию <Cond> <Smth>.	// if
		{<Method statement>}	
	[При необходимости 			// else
		{<Method statement>}]	
	Попробуйте на готовность.	// endif
		
<Cond> := посолите		// !=
<Cond> := поперчите		// ==
<Cond> := остудите		// >
<Cond> := подогрейте	// <

<Smth> := <ingredient>
<Smth> := <>			//Если ингредиент не указан, сравнивать с верхушкой стэка


// Петли
<Method statement> := 
	Начинайте <Verb> <ingredient>. 		// начало цикла while ingredient != 0
	{<Method statement>}				// тело цикла
	<Verb> <ingredient> до <String>.	// конец цикла, ingredient--, String - комментарий
	// Пример:
	// Начинайте резать марковь. [тело цикла] Резать марковь до круглых колечек.  
	// Начинайте курить сигареты. [тело цикла] Курить сигареты до посинения.	
	
<Method statement> := Отложите всё в сторону.	// break

