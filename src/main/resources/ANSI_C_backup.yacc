ID
CONSTANT

ADD
SUB
MUL
DIV
MOD

ASN
ADDA
SUBA
MULA
DIVA
MODA

INC
DEC

SEMI

IF
WHILE

LBRACE
RBRACE
LPARE
RPARE

OR
AND
NOT
EQU
NEQ
BTHAN
STHAN

INT
BYTE
CHAR
STRING
BOOLEAN
%%
statement_list
%%

statement_list : statement | statement_list statement ;

statement : expression_statement | iteration_statement | selection_statement | compound_statement ;

expression_statement : expression SEMI ; 
expression_statement : SEMI ; 

expression : assignment_expression ; 

assignment_expression : conditional_expression ; 
assignment_expression : unary_expression assignment_operator assignment_expression ; 

assignment_operator : ASN ; 
assignment_operator : ADDA ; 
assignment_operator : SUBA ; 
assignment_operator : MULA ; 
assignment_operator : DIVA ; 
assignment_operator : MODA ; 

conditional_expression : logical_or_expression ; 

logical_or_expression : logical_and_expression ; 
logical_or_expression : logical_or_expression OR logical_and_expression ; 

logical_and_expression : equality_expression ; 
logical_and_expression : logical_and_expression AND equality_expression ; 

equality_expression : relational_expression ; 
equality_expression : equality_expression EQU relational_expression ; 
equality_expression : equality_expression NEQ relational_expression ; 

relational_expression : additive_expression ; 
relational_expression : relational_expression STHAN additive_expression ; 
relational_expression : relational_expression BTHAN additive_expression ; 

additive_expression : multplicative_expression ; 
additive_expression : additive_expression ADD multplicative_expression ; 
additive_expression : additive_expression SUB multplicative_expression ; 

multplicative_expression : unary_expression ; 
multplicative_expression : multplicative_expression MUL unary_expression ; 
multplicative_expression : multplicative_expression DIV unary_expression ; 
multplicative_expression : multplicative_expression MOD unary_expression ; 

unary_expression : postfix_expression ; 
unary_expression : INC unary_expression ; 
unary_expression : DEC unary_expression ; 
unary_expression : unary_operator unary_expression ; 

postfix_expression : primary_expression ; 
postfix_expression : primary_expression INC ; 
postfix_expression : primary_expression DEC ; 

primary_expression : ID ; 
primary_expression : CONSTANT ; 
primary_expression : LPARE expression RPARE ; 

unary_operator : NOT ; 

iteration_statement : WHILE LPARE expression RPARE statement ; 

selection_statement : IF LPARE expression RPARE statement ; 

compound_statement : LBRACE RBRACE ; 
compound_statement : LBRACE statement_list RBRACE ; 
compound_statement : LBRACE declaration_list RBRACE ; 
compound_statement : LBRACE declaration_list statement_list RBRACE ; 

declaration : type_specifier ID SEMI ; 

type_specifier : INT ; 
type_specifier : BYTE ; 
type_specifier : CHAR ; 
type_specifier : STRING ; 
type_specifier : BOOLEAN ; 

declaration_list : declaration ; 
declaration_list : declaration_list declaration ; 
