repl:
	clj -m cljs.main --compile space-invaders.core --repl

prod:
	clj -m cljs.main --optimizations advanced -c space-invaders.core

update:
	clj -X:deps find-versions :lib org.clojure/clojurescript
	clj -X:deps find-versions :lib org.clojure/core.async

.PHONEY: repl
