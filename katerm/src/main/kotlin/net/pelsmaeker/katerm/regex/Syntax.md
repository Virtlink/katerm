# Terms

- Int
- Real
- String
- Appl
- List
- Placeholder?
- Var
- ListVar
- Option

# Regex

- Star
- Plus
- Question mark
- Or
- Parentheses

# Mix

    `LEX`* (`MOD`<$x>)? | ( `LEX`* (`MOD`<`X`>)? )*
    `a`* (`b``c`)? | ((`a``b``c`)*`d`?)*

    LEX* (MOD<$x>)? | ( LEX* (MOD<X>)? )*
    a* (bc)? | ((abc)*d?)*

    LEX* $$x MOD<X>
    a* $$x bc

    [ LEX MOD<X> ]
    [ LEX, MOD(X) ]
    LEX MOD<X>