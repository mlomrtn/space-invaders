repl:
	clj --main cljs.main --compile space-invaders.core --repl

prod: clean
	clj -M -m cljs.main --optimizations advanced -c space-invaders.core

install: prod
	cp -R index.html out ../mlomrtn.github.io

update:
	clj -X:deps find-versions :lib org.clojure/clojurescript
	clj -X:deps find-versions :lib org.clojure/core.async

clean:
	rm -rf out
	rm -f *~ src/space_invaders/*~

.PHONEY: repl prod update clean
