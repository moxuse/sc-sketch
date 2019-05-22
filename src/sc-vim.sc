s.boot;

s.quit;

(
SynthDef("", {||

})
)


s.quit

s.queryAllNodes

(
w = Window("m", Rect(0,0,400,400)).front;
EZSlider(w, 200@40, "test", \freq, {|ez| 
	ez.value.postln
});
EZSlider(w, Rect(0,60,200,40), "test1", \freq, {|ez| 
	ez.value.postln
});
EZSlider(w, 200@40, "test2", \freq, {|ez| 
	ez.value.postln
});
)

