# Parser Generator

Generator of top-down recursive parsers for [LL(1)](https://en.wikipedia.org/wiki/LL_grammar) grammars.

### Usage
To use this app, you must provide three arguments in the command line: `<grammar-file>`, `<package-name>` and `<location>`.
`<grammar-file>` is a path to file with your grammar, and `<package-name>`, `<location>` are for names of Java package and location where to generate files respectively.

Grammar-files have the following syntax: 

* `@ <CODE>` - inserts code into all nodes as a public mutable field. For example, we can define integer attribute as follows: `@int val = 0;`
* `# <CODE>` - Same as `@`, but inserts code into the parser. Can be used to create deriving attributes and global contexts.
* `<TOKEN> = <STRING> | <REGEX>` - defines a token. A token name must start with a capital letter. The token can be defined either with string or regular expression. For example, `NUM = "[0-9]+;"` defines a number token and `PLUS = '+';` defines token for "plus" symbol. Note that regex rule requires `"` symbols and string rule requires `'`.
* `<NTERM> = <RULE_1> | ... | <RULE_N>` - defines a grammar rule for non-terminal. Non-terminals must start with a lowercase letter. It is allowed to define epsilon rules with following syntax: `a := | smth`. You can insert Java code with `{code}` blocks after any entity in rule -- either terminal or non-terminal, and after epsilons. This code will be directly inserted into generated parser and will be called after parsing of respective entity. Of course, these blocks are not mandatory, and it is allowed to access children + fields defined via `@` and `#`.

All lines in grammar file should end with a semicolon. The initial non-terminal must have the `main` name. Grammar file will be parsed via ANTLR4.

Before generating, app will check if provided grammar is LL(1). If not so, it will throw an exception.

After running `parse` method, it will return `MainNode` object representing parse tree. 

#### Examples:
* `grammar_math.txt` - attribute calculator. To maintain infix-left order, it uses an accumulator defined in the global field. The result of an expression may be accessed with `val` field. 
* `grammar_cpp.txt` - very simplified grammar for C and C++ variable declaration (no arrays, templates, function pointers, etc.). It prints current non-terminal and children information after parsing (`{}` blocks can be removed to disable printing).

Generated code can be found in `examples` directory.

#### Known issues:
* First of all, it's difficult to generate efficient lexer. In this app, the generated lexer uses naive strategy, consuming input by 1 symbol and running check on all rules, which result in huge complexity. 

* This app does not attempt to fix the grammar to make it LL(1) (i.e. removing left recursion). 
