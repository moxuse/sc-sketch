(
var window, list, sf1, bbClock, headGroup, divNum, tempoNum, replaceFunc, bbcut, button, cutCombDeltime, cutCombDectime;
var listView, bw,offset, activity,pitch,pitchf,vol,b,bp,bps,bbc,isOn = false;
var outBus = 8;

window = Window("panel", Rect.new(100,100,430,600));

// outBus = NumberBox(window, Rect(10,10,20,40)).value_(0);

tempoNum = NumberBox(window, Rect(40,10,20,40)).value_(2.2);

divNum = NumberBox(window, Rect(70,10,20,40)).value_(32);

offset=EZSlider( window, Rect.new(80,0,200,40), "offset", [0.0, 1.0, \linear].asSpec, {}, 0.0);
activity=EZSlider( window, Rect.new(80,50,200,40), "activity", [4.0, 24.0, \linear].asSpec, {}, 12.0);
pitch=EZSlider( window, Rect.new(80,100,200,40), "pitch",[0.125, 8.0, \linear].asSpec, {}, 2.0);
vol=EZSlider( window, Rect.new(80,150,200,40), "vol", [0.0, 1.0, \linear].asSpec, {}, 0.0);

cutCombDeltime = EZSlider( window, Rect.new(5, 40 * 5,350,40), "deltime", [0.01, 3.0, \linear].asSpec, {}, 0.01);
cutCombDectime = EZSlider( window, Rect.new(5, 40 * 6,350,40), "dectime", [0.01, 2.0, \linear].asSpec, {}, 0.01);

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

replaceFunc = Routine{|opt|
	opt.tempo.postln;
	opt.division.postln;
	opt.path.postln;
	if (sf1 != nil, {sf1.free});
	if (bbClock != nil, {bbClock.stop});
	if (headGroup !=nil, {headGroup.free});

	headGroup = Group.head(1);
	bbClock = ExternalClock(TempoClock(opt.tempo)).play;
	sf1= BBCutBuffer(opt.path, opt.division);
	s.sync;

	bbcut = BBCut2(CutGroup([CutBuf2(sf1, offset, pitch), CutComb1(cutCombDeltime, cutCombDectime), CutMixer(outBus,vol,{(1.0.rand + 0.75)}, 0.0)], headGroup),
			CageCut.new(activity,[1,0.75,1,0.2,0.3,0.4,2,0.1,8])
		).play(bbClock);
};

window.front;
window.onClose_({
	bbcut.stop;
	bbcut.free;
	headGroup.free;
	sf1.free;
});

)