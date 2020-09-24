repl:
	clj -m cljs.main --compile space-invaders.core --repl

prod:
	clj -m cljs.main --optimizations advanced -c space-invaders.core

.PHONEY: repl
