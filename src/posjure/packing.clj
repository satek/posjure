(ns posjure.packing
  (:require [clojure.xml :as xml])
  (import [org.jpos.iso ISOMsg ISOException]
          org.jpos.iso.packager.GenericPackager))

;;iso messages
(def directory (clojure.java.io/file "packagers"))

(def files (filter (fn [file] (.contains (.getName file) "xml")) (file-seq directory)))

(defn iso-msg-factory [file]
  (let [packager (GenericPackager. file)]
    (fn [] (let [iso-msg (new ISOMsg)]
             (doto iso-msg (.setPackager packager))
             iso-msg))))

(def packagers
  (into {} (map (fn [file] {(keyword (clojure.string/replace (.getName file) #".xml" ""))
                            (iso-msg-factory (.getAbsolutePath file))})
                files)))

(defn get-new-message [packager]
  (((keyword packager) packagers)))
;;

;; parse packager xml for ids
(defn parse-packager-xml [file-location]
  (let [parsed (xml/parse file-location)]
    (map (fn [item] (:id (:attrs item))) (:content parsed))))

(def packager-ids
  (into {} (map (fn [file] {(keyword (clojure.string/replace (.getName file) #".xml" ""))
                            (parse-packager-xml (.getAbsolutePath file))})
                files)))
;;

(defn extract-fields [packager msg-bytes]
  (let [msg (get-new-message packager)]
    (. msg (unpack msg-bytes))
    (into {}
          (map (fn [field] {(keyword field)(. msg (getValue field))}) ((keyword packager) packager-ids)))))

(defn pack-from-params [msg params]
  (. msg (setMTI (params "MTI")))
  (reduce-kv (fn [m k v] (doto msg (.set k v))) {} (dissoc params "MTI"))
  (.pack msg))

(defn bytes-to-string [bytes]
  (apply str (map char bytes)))

(defn bytes-from-string [byte-string]
  (byte-array (map byte byte-string)))
