lexer grammar KatermAntlrLexer;

// Keywords
LANGUAGE      : 'language';
STRING        : 'string';
INT           : 'int';

// Literals and Identifiers
ID            : [a-zA-Z_][a-zA-Z0-9_]*;
INTLIT        : [0-9]+;
STRINGLIT     : '"' ( StringEscapeSeq | ~( '\\' | '"' | '\r' | '\n' ) )* '"';
fragment StringEscapeSeq : '\\' ( 't' | 'n' | 'r' | '"' | '\\' );

// Symbols
STAR          : '*';
QUESTIONMARK  : '?';
COLON         : ':';
SEMICOLON     : ';';
EQUALS        : '=';
ANGLE_OPEN    : '<';
ANGLE_CLOSE   : '>';
DOT           : '.';

// Layout
WS            : [\u0020\u00a0\u1680\u2000\u200a\u202f\u205f\u3000\r\n]+ -> channel(HIDDEN);

// Errors
ERRORCHAR     : .;  // Move errors to the parser
