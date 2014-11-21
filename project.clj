(defproject posjure "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [ring/ring-core "1.3.1"]
                 [ring/ring-devel "1.3.1"]
                 [ring/ring-json "0.3.1"]
                 [org.immutant/web "2.0.0-alpha2"]
                 [org.immutant/caching "2.0.0-alpha2"]
                 [org.immutant/messaging "2.0.0-alpha2"]
                 [org.immutant/scheduling "2.0.0-alpha2"]
                 [org.jpos/jpos "1.9.8"]
                 [compojure "1.2.0"]
                 [org.clojure/tools.cli "0.3.1"]]
  :main posjure.core
  :aot [posjure.core])
