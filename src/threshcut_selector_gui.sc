 (
var sf1, sf2;
var window, chooseriff, shuffles, fillchance, stopchance;
var list, listView;
var amp, chooseriff2, shuffles2, fillchance2, stopchance2;
var openDialog, playbut;
var bbClock;
var bbcut;
var headGroup;
var owncutfunc, ownrepfunc;
var tempoNum, divNum;
var replaceFunc;
var cutCombDeltime, cutCombDectime;
var outBus = 6;

window = Window("Thrashing BBCut", Rect.new(700,80,620,550));

tempoNum = NumberBox(window, Rect(10,10,20,40)).value_(2.2);

divNum = NumberBox(window, Rect(50,10,20,40)).value_(32);


//b1 button

list = [
	"sona.wav",
	"sona2.wav",
	"sona3.wav",
	"sona.aif",
	"soz4.wav",
	"soz29_m.wav",
	"superdirt/ecco_pad/mpd2_18.wav",
	"superdirt/ecco_pad/pad_sand_1.wav",
	"superdirt/mnt/Big Drain Sust.wav",
	"superdirt/mnt/Glitch Effect.wav",
	"superdirt/mnt/Ice Stoch Loop.wav",
	"superdirt/mnt/Whirling Frog.wav",
	"superdirt/mpd2/mpd2_10.wav",
	"superdirt/msoz/sona0.wav",
	"superdirt/msoz/soz0.wav",
	"superdirt/msoz/szDrone FX Mysterious Void.wav",
	"superdirt/msoz/szpacewar 2003 120.wav"
];

listView = ListView(window, Rect(10,320,340,210))
  .items_(list)
  .selectionAction_({|v_|
	var sym, envVals;

	sym = Platform.resourceDir ++ "/sounds/" ++ list[v_.selection][0];
	tempoNum.value.postln;
	divNum.value.postln;
	sym.postln;
	replaceFunc.reset();
	replaceFunc.value(( tempo: tempoNum.value, division: divNum.value, path: sym));
});


amp = EZSlider( window, Rect.new(5, 40 * 0 + 40,350,40), "amp", [0, 1.0, \linear].asSpec, {|v|
	s.sendMsg("/n_set", headGroup.nodeID, \amp, v.value);
}, 0.0);
chooseriff = EZSlider( window, Rect.new(5, 40 * 1 + 40,350,40), "chooseriff", [0, 1.1, \linear].asSpec, {|v| }, 0.75);
shuffles = EZSlider( window, Rect.new(5, 40 * 2 + 40,350,40), "shuffles", [0, 10, \linear].asSpec, {|v| }, 0);
fillchance = EZSlider( window, Rect.new(5, 40 * 3 + 40,350,40), "fillchance", [0, 1.0, \linear].asSpec, {|v| }, 0.5);
stopchance = EZSlider( window, Rect.new(5, 40 * 4 + 40,350,40), "stopchance", [0, 1.0, \linear].asSpec, {|v| }, 0.6);

cutCombDeltime = EZSlider( window, Rect.new(5, 40 * 5 + 40,350,40), "deltime", [0.01, 3.0, \linear].asSpec, {|v|
	s.sendMsg("/n_set", headGroup.nodeID, 'deltime', v.value);
}, 0.01);
cutCombDectime = EZSlider( window, Rect.new(5, 40 * 6 + 40,350,40), "dectime", [0.01, 2.0, \linear].asSpec, {|v|
	s.sendMsg("/n_set", headGroup.nodeID, 'dectime', v.value);
}, 0.01);

window.front;
window.onClose_({
	bbcut.stop;
	bbcut.free;
	headGroup.free;
});

replaceFunc = Routine{|opt|
	opt.tempo.postln;
	opt.division.postln;
	opt.path.postln;
	if (sf1 != nil, {sf1.free});
	if (bbClock != nil, {bbClock.stop});
	if (headGroup !=nil, {headGroup.free});

	headGroup = Group.head(Node.basicNew(s, 1));
	bbClock = ExternalClock(TempoClock(opt.tempo)).play;
	sf1= BBCutBuffer(opt.path, opt.division);

s.sync;

	bbcut = BBCut2(CutGroup([CutBuf1(sf1), CutComb1(cutCombDeltime.value, cutCombDectime.value), CutMixer(outBus, amp.value,{(1.0.rand + 0.75)}, 0.0)], headGroup),
		ThrashCutProc1.new(0.0,0.125,opt.division,[0.25,0.25,0.25,0.125,0.125,0.25,0.125,0.125,0.125],
		{chooseriff.value.round(1.0).asInteger},
		{shuffles.value.round(1.0).asInteger},
		{fillchance.value.coin},
		stopchance
	)

).play(bbClock);
};

// replaceFunc.run(( tempo: 2.2, division: 16, path: Platform.resourceDir ++ "/sounds/soz2.wav"));

)