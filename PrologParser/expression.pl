lookup(K, [(K, V) | _], V).
lookup(K, [_ | T], V) :- lookup(K, T, V).

variable(Name, variable(Name)).
const(Value, const(Value)).

op_add(A, B, operation(op_add, A, B)).
op_subtract(A, B, operation(op_subtract, A, B)).
op_negate(A, B, operation(op_negate, A)).
op_multiply(A, B, operation(op_multiply, A, B)).
op_divide(A, B, operation(op_divide, A, B)).

op_sinh(A, B, operation(op_sinh, A)).
op_cosh(A, B, operation(op_cosh, A)).

operation(op_add, A, B, R) :- R is A + B.
operation(op_subtract, A, B, R) :- R is A - B.
operation(op_negate, A, R) :- R is -A.
operation(op_multiply, A, B, R) :- R is A * B.
operation(op_divide, A, B, R) :- R is A / B.

operation(op_sinh, A, R) :- R is (exp(A) - exp(-A)) / 2.
operation(op_cosh, A, R) :- R is (exp(A) + exp(-A)) / 2.

evaluate(const(V), _, V).
evaluate(variable(Name), Vars, R) :- atom_chars(Name, [H | _]), lookup(H, Vars, R).
evaluate(operation(Op, A), Vars, R) :-
    evaluate(A, Vars, AV),
    operation(Op, AV, R).

evaluate(operation(Op, A, B), Vars, R) :-
    evaluate(A, Vars, AV),
    evaluate(B, Vars, BV),
    operation(Op, AV, BV, R).

nonvar(V, _) :- var(V).
nonvar(V, T) :- nonvar(V), call(T).

:- load_library('alice.tuprolog.lib.DCGLibrary').

expr_p(variable(Name)) --> 
	{nonvar(Name, atom_chars(Name, Chars))},
	symbol_p(Chars), 
	{Chars = [_ | _], atom_chars(Name, Chars)}.
	%{print(Name), member(Name, [x, y, z]) }.

symbol_p([]) --> [].
symbol_p([H | T]) -->
  {member(H, ['x', 'y', 'z', 'X', 'Y', 'Z'])},
  [H],
  symbol_p(T).

expr_p(const(Value)) -->
  {nonvar(Value, number_chars(Value, Chars))},
  digits_p(Chars),
  {Chars = [_, _| _], number_chars(Value, Chars)}.

digits_p([]) --> [].
digits_p([H | T]) -->
  {member(H, ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '-'])},
  [H],
  digits_p(T).

ws(false) --> [].

op_p(op_add) --> ['+'].
op_p(op_subtract) --> ['-'].
op_p(op_negate) --> {atom_chars(negate, Chars)}, Chars.
op_p(op_cosh) --> {atom_chars(cosh, Chars)}, Chars.
op_p(op_sinh) --> {atom_chars(sinh, Chars)}, Chars.
op_p(op_multiply) --> ['*'].
op_p(op_divide) --> ['/'].

ws(true) --> [].
ws(true) --> [' '], ws(true).

expr_p(operation(Op, A, B)) -->
		['('], ws_expr(A), [' '],
		op_p(Op),
		[' '], ws_expr(B), [')'].

expr_p(operation(Op, A)) --> {var(Op) -> R = true; R = false}, op_p(Op), ws(R), ['('], ws_expr(A), [')'].

ws_expr(E) --> {(var(E) -> R = true; R = false)}, ws(R), expr_p(E), ws(R).

infix_str(E, A) :- ground(E), phrase(ws_expr(E), C), atom_chars(A, C).
infix_str(E, A) :-   atom(A), atom_chars(A, C), phrase(ws_expr(E), C).
