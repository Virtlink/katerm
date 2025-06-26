parser grammar KatermAntlrParser;

options {
    tokenVocab = KatermAntlrLexer;
}

unit
  : PACKAGE QID SEMICOLON decl* EOF
  ;

decl
  : sortDecl
  | ruleDecl
  ;

sortDecl
  : SORT ID CURLY_OPEN decl* CURLY_CLOSE
  ;

ruleDecl
  : ID EQUALS ruleSymbol* SEMICOLON
  ;

ruleSymbol
  : STRINGLIT                                       # literalSymbol
  | ANGLE_OPEN ID COLON typeSpec ANGLE_CLOSE        # namedSymbol
  ;

typeSpec
  : type STAR                                       # starTypeSpec
  | type                                            # simpleTypeSpec
  ;

type
  : ID                                              # refType
  | INT                                             # intType
  | STRING                                          # stringType
  ;