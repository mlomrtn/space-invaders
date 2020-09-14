repl:
	clj --main cljs.main --compile flappy-bird.core --repl

prod:
	clj -m cljs.main --optimizations advanced -c flappy-bird.core

.PHONEY: repl
