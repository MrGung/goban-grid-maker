(ns goban-grid-maker.core
  (:require
    [re-com.core :as com :refer [h-box input-text slider checkbox v-box single-dropdown]]
    [reagent.core :as r]
    [reagent.dom :as dom]))


(def hoshi-choices [{:id "no" :label "no"}
                    {:id "corners" :label "in corners"}
                    {:id "tengen" :label "in corners and tengen"}
                    {:id "all" :label "all"}])
(defn mm [val]
  (str val "mm"))



(defn draw-lines-x [lines line-thickness space-x space-y offset-x offset-y]
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
(defn draw-lines-y [lines line-thickness space-x space-y offset-x offset-y]
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

(def font-size 8)
(def text-style (str "fill: #000; font-size:" font-size "mm; font-family:Arial; text-align:right;"))
(def coords ["A" "B" "C" "D" "E" "F" "G" "H" "J" "K" "L" "M" "N" "O" "P" "Q" "R" "S" "T"])
#_(defn draw-coords [show-coords lines space-x space-y offset-x offset-y font-size]
    (when show-coords
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


(def hoshis {9 [2 4 6]
             13 [3 6 9]
             19 [3 9 15]})

(def hoshi-types {"corners" [[0 0] [0 2] [2 0] [2 2]]
                  "tengen" [[0 0] [0 2] [2 0] [2 2] [1 1]]
                  "all" [[0 0] [0 2] [2 0] [2 2] [1 1] [0 1] [2 1] [1 0] [1 2]]})

(defn draw-circle [x y space-x space-y offset-x offset-y hoshi-radius]
  (let [cx (+ (* x space-x) offset-x)
        cy (+ (* y space-y) offset-y)]
    [:circle {:cx (mm cx)
              :cy (mm cy)
              :r (mm hoshi-radius)
              :style {:fill "#000"}}]))

(defn draw-hoshis [space-x space-y offset-x offset-y hoshi-radius hoshi-diameter lines where-hoshi]
  (when (> hoshi-diameter 0)
    (when-let [hos (hoshis lines)]
      (let [hot (hoshi-types where-hoshi)]
        (for [[x y] hot]
          (draw-circle (nth hos x) (nth hos y) space-x space-y offset-x offset-y hoshi-radius))))))



(defn app []
  (let [size (r/atom "9")
        coords? (r/atom false)
        hoshi-placement (r/atom false)
        line-thickness (r/atom "1")
        font-size (r/atom 12)]
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
                                           :on-change #(reset! size %)
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
                                                #_#_:change-on-blur? false}]]]


                  (let [hoshi-diameter 4
                        lines (js/parseInt @size)
                        space-x (js/parseFloat "21.944")    ; 21.944
                        space-y (js/parseFloat "23.166")    ; 23.166
                        offset-x (if @coords? (+ 2 @font-size) 2)
                        offset-y (if @coords? (+ 2 @font-size) 2)
                        width (* (dec lines) space-x)
                        height (* (dec lines) space-y)
                        hoshi-radius (float (/ hoshi-diameter 2))
                        svg-width (+
                                    (* (dec lines) space-x)
                                    (* 2 offset-x)
                                    (if @coords? font-size 0))
                        svg-height (+
                                     (* (dec lines) space-y)
                                     (* 2 offset-y))]
                    [:svg#grid {:width (mm svg-width)
                                :height (mm svg-height)
                                :xmlns "http://www.w3.org/2000/svg"}
                     [:rect {:x (mm offset-x)
                             :y (mm offset-y)
                             :width (mm width)
                             :height (mm height)
                             :style {:fill "#fff"
                                     :stroke "#000"
                                     :stroke-width (str @line-thickness "mm")}}]
                     (draw-lines-x lines @line-thickness space-x space-y offset-x offset-y)
                     (draw-lines-y lines @line-thickness space-x space-y offset-x offset-y)
                     ;;(draw-coords @coords? lines space-x space-y offset-x offset-y font-size)
                     (draw-hoshis space-x space-y offset-x offset-y hoshi-radius hoshi-diameter lines @hoshi-placement)
                     ]
                    )
                  [:button {:type "button"
                            :on-click (fn [event]
                                        (println "clicked!")
                                        (let [svg (.-outerHTML (.getElementById js/document "grid"))
                                              blob (js/Blob. [(str svg)])]
                                          (doto (.createElement js/document "a")
                                            (.setAttribute "download" "grid.svg")
                                            (.setAttribute "href" (.. js/window -URL (createObjectURL blob)))
                                            (.click)
                                            (.remove))))}
                   "Download board as SVG"]]]
      #_[:form#inputForm
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



    )
