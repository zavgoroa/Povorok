<Recipe> ::= 
	<Recipe Title>
	<Ingredient List>
	<Method>

<Recipe Title> ::= <String>.

<Ingredient List> ::= 
	Нужно купить.
	{<ingredient-name> <initial-value> [<measure>]}
	
<ingredient-name> ::= <String>
<initial-value> ::= <Integer>
<measure> ::= шт		// массив
<measure> ::= г | кг 	// сухие
<measure> ::= мл | л 	// жидкие
// Переменные типизируются: сухие - short, жидкие - char

<Method> ::= 
	Способ приготовления.
	{<Method statement>.}

// Операции с массивом
// обратиться по индексу
<Method statement> ::= <ingredient> в таком же количестве как <ingredient> 

// Присвоение переменной значения
<Method statement> ::= <ingredient> замените на <Arithmetic>

<Arithmetic> ::= <ingredient>
<Arithmetic> ::= <Operation> <Arithmetic> и <Arithmetic>

<Operation> ::= смешанные 					// +
<Operation> ::= взятые в нужном количестве 	// -
<Operation> ::= перемешанные 				// *
<Operation> ::= размешанные 				// /

<Method statement> ::= Возьмите из холодильника <ingredient>	//STDIN -> ingredient
<Method statement> ::= Положите в холодильник <ingredient> 		//ingredient -> STDOUT

<Method statement> := Вымочить <ingredient>	// каст ingredient к жидкому (ASCII)

// Оператор ветвления
<Method statement> := 
	По желанию <Condition>.		// if
		{<Method statement>}	
	[При необходимости 			// else
		{<Method statement>}]	
	Попробуйте на готовность	// endif
	
<Condition> ::= <Cond> <ingredient> и <ingredient>
	
<Cond> := посолите		// !=
<Cond> := поперчите		// ==
<Cond> := остудите		// >
<Cond> := подогрейте	// <


// Петли
<Method statement> := 
	Начинайте <Verb> <ingredient>. 		// начало цикла while ingredient != 0
	{<Method statement>}				// тело цикла
	<Verb> <ingredient> до готовности.	// конец цикла, ingredient--, String - комментарий
	
<Method statement> := 
	Начинайте <Verb>. 					// начало цикла while true
	{<Method statement>}				// тело цикла
	<Verb> до готовности.	// конец цикла, ingredient--, String - комментарий
	
	// Пример:
	// Начинайте резать марковь. [тело цикла] Резать марковь до круглых колечек.  
	// Начинайте курить сигареты. [тело цикла] Курить сигареты до посинения.	
	
<Method statement> := Отложите всё в сторону.	// break

