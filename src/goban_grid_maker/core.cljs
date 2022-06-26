(ns goban-grid-maker.core
  (:require
    [re-com.core :as com :refer [h-box input-text slider checkbox v-box single-dropdown]]
    [reagent.core :as r]
    [reagent.dom :as dom]))


(def hoshi-choices [{:id 1 :label "no"}
                    {:id 2 :label "in corners"}
                    {:id 3 :label "in corners and tengen"}
                    {:id 4 :label "all"}])


(defn app []
  (let [size (r/atom "9")
        coords? (r/atom false)
        hoshi-placement (r/atom false)]
    (fn []
      [v-box
       :gap "10px"
       :children [[h-box
                         :children [[:label "Size of board"]
                                    [slider {:model size
                                             :min 2
                                             :max 23
                                             :width "20rem"
                                             :on-change #(reset! size (str %))}]
                                    [input-text {:model size
                                                 #_#_:width "60px"
                                                 :on-change #(reset! size (str %))
                                                 #_#_:change-on-blur? false}]]]

                        [h-box
                         :children [[:label "Show coordinates"]

                                    [checkbox {:model coords?
                                               #_#_:width "60px"
                                               :on-change #(reset! coords? %)
                                               #_#_:change-on-blur? false}]]]

                        [h-box
                         :children [[:label "Show hoshi"]
                                    [single-dropdown {:choices hoshi-choices
                                                      :model hoshi-placement
                                                      #_#_:width "60px"
                                                      :on-change #(reset! hoshi-placement %)
                                                      #_#_:change-on-blur? false}]]]]]
      [:form#inputForm
         [:fieldset
          [:legend "Standard options"]
          [:p [:label {:for "s"} "Size of board"] [:select#s {:name "s"}
                                                   [:option "9"]
                                                   [:option "13"]
                                                   [:option "19"]]]
          [:p [:label {:for "sC"} "Show coordinates"] [:select#sC {:name "sC"}
                                                       [:option {:value "true"} "yes"]
                                                       [:option {:selected "selected" :value "false"} "no"]]]
          [:p [:label#sHl {:for "sH"} "Show hoshi"] [:select#sH {:name "sH"}
                                                     [:option {:value "no"} "no"]
                                                     [:option {:selected "selected" :value "corners"} "in corners"]
                                                     [:option {:value "tengen"} "in corners and tengen"]
                                                     [:option {:value "all"} "all"]]]
          [:div {:style "clear:both"}]]
         [:fieldset
          [:legend "Advanced options"]
          [:p [:label {:for "lT"} "Thickness of lines in mm"] [:select#lT {:name "lT"}
                                                               [:option "1"]
                                                               [:option "2"]
                                                               [:option "3"]]]
          [:p [:label {:for "hD"} "Diameter of hoshi in mm"] [:select#hD {:name "hD"}
                                                              [:option "2"]
                                                              [:option {:selected "selected"} "3"]
                                                              [:option "4"]]]
          [:div {:style "clear:both"}]]
         [:fieldset
          [:legend "Expert options"]
          [:p [:label {:for "dH"} "Horizontal distance of lines in mm"] [:input {:name "dH" :type "text" :size "5" :maxlength "6" :value "21.944"}]]
          [:p [:label {:for "dV"} "Vertical distance of lines in mm"] [:input {:name "dV" :type "text" :size "5" :maxlength "6" :value "23.166"}]]
          [:div {:style "clear:both"}]]
         [:p [:input#submit {:type "submit" :value "Submit"}]]]

     )))


(dom/render [app] (.getElementById js/document "app"))






#_(comment

  (def xml-header "<?xml version=\"1.0\" encoding=\"iso-8859-1\" standalone=\"no\"?>
<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" \"http://www.w3.org/TR/SVG/DTD/svg10.dtd\">")
  (def font-size 8)
  (def text-style (str "fill: #000; font-size:" font-size "mm; font-family:Arial; text-align:right;"))





  (def hoshis {9 [2 4 6]
               13 [3 6 9]
               19 [3 9 15]})

  (def hoshi-types {"corners" [[0 0] [0 2] [2 0] [2 2]]
                    "tengen" [[0 0] [0 2] [2 0] [2 2] [1 1]]
                    "all" [[0 0] [0 2] [2 0] [2 2] [1 1] [0 1] [2 1] [1 0] [1 2]]})

  (def coords ["A" "B" "C" "D" "E" "F" "G" "H" "J" "K" "L" "M" "N" "O" "P" "Q" "R" "S" "T"])







  (defn draw-circle [x y {:keys [space-x space-y offset-x offset-y hoshi-radius]}]
    (let [cx (+ (* x space-x) offset-x)
          cy (+ (* y space-y) offset-y)]
      (format "<circle cx=\"%smm\" cy=\"%smm\" r=\"%smm\" style=\"fill: #000\" />\n" cx cy hoshi-radius)))


  (defn mm [val]
    (str val "mm"))




  (defn draw-lines-x [{:keys [lines line-thickness space-x space-y offset-x offset-y]}]
    (let [x (+
              (* (dec lines) space-x)
              offset-x)]
      (for [i (range 1 (dec lines))
            :let [y (+ (* i space-y) offset-y)]]
        [:line {:x1 (mm offset-x)
                :y1 (mm y)
                :x2 (mm x)
                :y2 (mm y)
                :stroke "black"
                :stroke-width (mm line-thickness)}])))

  (defn draw-lines-y [{:keys [lines line-thickness space-x space-y offset-x offset-y]}]
    (let [y (+
              (* (dec lines) space-y)
              offset-y)]
      (for [i (range 1 (dec lines))
            :let [x (+ (* i space-x) offset-x)]]
        [:line {:x1 (mm x)
                :y1 (mm offset-y)
                :x2 (mm x)
                :y2 (mm y)
                :stroke "black"
                :stroke-width (mm line-thickness)}])))



  (defn draw-hoshis [{:keys [hoshi-diameter lines where-hoshi] :as config}]
    (when (> hoshi-diameter 0)
      (let [hos (hoshis lines)
            hot (hoshi-types where-hoshi)]
        (for [[x y] hot]
          (draw-circle (nth hos x) (nth hos y) config)))))



  (defn draw-coords [{:keys [show-coords lines space-x space-y offset-x offset-y font-size]}]
    (when show-coords
      ;; if($showCoords){
      ;;   for($i=0; $i < $lines; $i++){
      ;;     echo "<text x=\"".($i*$spaceX+$offsetX-($fontSize/3))."mm\" y=\"".(($lines-1)*$spaceY+$offsetY+$fontSize)."mm\" style=\"$textStyle\">$coords[$i]</text>\n";
      ;;   }
      ;;   for($i=$lines-1; $i >= 0; $i--){
      ;;     echo "<text x=\"".(($lines-1)*$spaceX+$offsetX+$fontSize*4/3)."mm\" y=\"".($i*$spaceY+$offsetY+($fontSize/3))."mm\" style=\"$textStyle"."text-anchor:end\">".($lines-$i)."</text>\n";
      ;;   }
      ;; }
      (concat
        (for [i (range lines)]
          [:text {:x (mm (-
                           (+
                             (* i space-x)
                             offset-x)
                           (float (/ font-size 3))))
                  :y (mm (+
                           (* (dec lines) space-y)
                           offset-y
                           font-size))
                  :style text-style}
           (coords i)])

        (for [i (range lines)]
          [:text {:x (mm (+
                           (* (dec lines) space-x)
                           offset-x
                           (float (* font-size 4/3))))
                  :y (mm (+
                           (* i space-y)
                           offset-y
                           (float (/ font-size 3))))
                  :style (str text-style "text-anchor:end")}
           (- lines i)]))))





  (defn create-grid [{:keys [lines space-x space-y svg-width svg-height offset-x offset-y line-thickness] :as config}]
    (let [width (* (dec lines) space-x)
          height (* (dec lines) space-y)]
      (html [:svg {:width (mm svg-width)
                   :height (mm svg-height)
                   :xmlns "http://www.w3.org/2000/svg"}
             [:rect {:x (mm offset-x)
                     :y (mm offset-y)
                     :width (mm width)
                     :height (mm height)
                     :style (format "fill: #FFF; stroke: #000; stroke-width: %smm" line-thickness)}]
             (draw-lines-x config)
             (draw-lines-y config)
             (draw-hoshis config)
             (draw-coords config)])))










  ;; http://localhost:31250/www/go/goban-grid?s=19&lT=2&hD=4&sC=true&dH=21.944&dV=23.166&sH=all
  ;; http://localhost:31250/www/go/goban-grid?s=13&lT=2&hD=5&sC=false&dH=21.944&dV=23.166&sH=corners
  ;; http://localhost:31250/www/go/goban-grid?s=13&lT=2&hD=5&sC=true&dH=21.944&dV=23.166&sH=corners
  ;; http://clojure.gungfu.de/www/go/goban-grid?s=13&lT=2&hD=5&sC=true&dH=21.944&dV=23.166&sH=corners

  (defpage "/www/go/goban-grid" {:keys [s lT hD sC dH dV sH]}
    (let [lines (Integer/parseInt s)
          line-thickness lT
          where-hoshi sH
          hoshi-diameter (if (= "no" where-hoshi) 0 (Integer/parseInt hD))
          show-coords (= sC "true")

          space-x (Float/parseFloat dH)                     ; 21.944
          space-y (Float/parseFloat dV)                     ; 23.166

          offset-x (if show-coords (+ 2 font-size) 2)
          offset-y (if show-coords (+ 2 font-size) 2)
          hoshi-radius (float (/ hoshi-diameter 2))
          svg-width (+
                      (* (dec lines) space-x)
                      (* 2 offset-x)
                      (if show-coords font-size 0))         ; ($lines-1) * $spaceX+ 2*$offsetX + ($showCoords ? $fontSize : 0) ;
          svg-height (+
                       (* (dec lines) space-y)
                       (* 2 offset-y))                      ;($lines-1) * $spaceY+ 2*$offsetY ;
          config {:lines lines
                  :line-thickness line-thickness
                  :where-hoshi where-hoshi
                  :hoshi-diameter hoshi-diameter
                  :show-coords show-coords
                  :font-size font-size
                  :space-x space-x
                  :space-y space-y
                  :offset-x offset-x
                  :offset-y offset-y
                  :hoshi-radius hoshi-radius
                  :svg-width svg-width
                  :svg-height svg-height}
          body (create-grid config)]
      {:status 200
       :headers {"Content-type" "image/svg+xml"}
       :body body}
      )
    ))
