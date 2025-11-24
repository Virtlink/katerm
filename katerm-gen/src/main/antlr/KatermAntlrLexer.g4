lexer grammar KatermAntlrLexer;

// Keywords
PACKAGE       : 'package';
SORT          : 'sort';
CONS          : 'cons';
TEMPLATE      : 'template';
INT           : 'int';
STRING        : 'string';

// Literals and Identifiers
ID            : [a-zA-Z_][a-zA-Z0-9_]*;
QID           : ID ( '.' ID )*;
INTLIT        : [0-9]+;
STRINGLIT     : STRING_QUOTE_OPEN STRING_PART* STRING_QUOTE_CLOSE
              | STRING3_QUOTE_OPEN STRING3_PART* STRING3_QUOTE_CLOSE
              ;
STRING_QUOTE_OPEN  : '"' -> pushMode(LineString);
STRING3_QUOTE_OPEN : '"""' -> pushMode(MultiLineString);


// Symbols
STAR          : '*';
QUESTIONMARK  : '?';
COLON         : ':';
SEMICOLON     : ';';
EQUALS        : '=';
ANGLE_OPEN    : '<';
ANGLE_CLOSE   : '>';
CURLY_OPEN    : '{';
CURLY_CLOSE   : '}';
PAREN_OPEN    : '(';
PAREN_CLOSE   : ')';
SQUARE_OPEN   : '[';
SQUARE_CLOSE  : ']';
DOT           : '.';
COMMA         : ',';

// Layout
WS            : [\u0020\u00a0\u1680\u2000\u200a\u202f\u205f\u3000\r\n]+ -> channel(HIDDEN);

// Errors
ERRORCHAR     : .;  // Move errors to the parser

mode LineString;

STRING_TEXT       : ~( '\\' | '"' | '\r' | '\n' )+ ;
STRING_ESCAPE_SEQ : '\\' ( 't' | 'n' | 'r' | '"' | '\\' ) ;
STRING_QUOTE_CLOSE : '"' -> popMode;
STRING_PART       : STRING_TEXT | STRING_ESCAPE_SEQ ;

mode MultiLineString;

STRING3_TEXT       : ~( '\\' | '"' )+ ;
STRING3_ESCAPE_SEQ : '\\' ( 't' | 'n' | 'r' | '"' | '\\' ) ;
STRING3_NEWLINE    : '\r'? '\n' -> skip ;
STRING3_QUOTE_CLOSE : '"""' -> popMode;
STRING3_PART       : STRING3_QUOTE_CLOSE | STRING3_TEXT | STRING3_ESCAPE_SEQ | STRING3_NEWLINE ;
