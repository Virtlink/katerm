parser grammar KatermAntlrParser;

options {
    tokenVocab = KatermAntlrLexer;
}

unit
  : LANGUAGE ID SEMICOLON rule* EOF
  ;

rule
  : ruleName EQUALS ruleSymbol* SEMICOLON
  ;

ruleName
  : ID                                              # simpleRuleName
  | ID DOT ID                                       # qualifiedRuleName
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