parser grammar KatermAntlrParser;

options {
    tokenVocab = KatermAntlrLexer;
}

unit
  : PACKAGE QID SEMICOLON decl* EOF
  ;

decl
  : sortDecl
  | consDecl
  | templateDecl
  ;

sortDecl
  : SORT ID (PAREN_OPEN varSpecList? PAREN_CLOSE)? (COLON typeList)? SEMICOLON
  ;

consDecl
  : CONS ID (PAREN_OPEN varSpecList? PAREN_CLOSE)? (COLON typeList)? SEMICOLON
  ;

templateDecl
  : TEMPLATE ID EQUALS STRINGLIT SEMICOLON
  ;

varSpecList
  : varSpec (COMMA varSpecList?)?
  ;

varSpec
  : ID COLON typeSpec
  ;

typeSpec
  : type STAR                                       # starTypeSpec
  | type                                            # simpleTypeSpec
  ;

typeList
  : type (COMMA typeList?)?
  ;

type
  : ID                                              # refType
  | INT                                             # intType
  | STRING                                          # stringType
  ;