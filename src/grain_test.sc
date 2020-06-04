(
var outBusIndex  = ~dirt.orbits[0].synthBus.index;
var orbitGroupIndex = 2;
var numChannels = ~dirt.numChannels;
var task;
var window;

~rec_Buf = Buffer.alloc(s, 44100 * 5.0, 1, bufnum: 9999);
~rec_group = Group.after(orbitGroupIndex);

window = Window("grain test", Rect(10,10,50,20));

SynthDef('orbit_rec', {
  var in, trig, demand;
  in = In.ar(40, 1);
  trig = Amplitude.kr(in);
	demand = Demand.kr(trig, 0 , Dseq([0, 0, 0, 1],  inf));
  RecordBuf.ar(in, ~rec_Buf, doneAction: 0, trigger: demand, loop: 1);
}).store();

SynthDef('grain_player', {arg center=0.5;
  var trate, dur, clk, pos, pan, src, rate;
  trate = MouseY.kr(8, 120, 1);
    dur = 4 / trate;
    clk = Impulse.kr(trate);
	rate = 1.0 + Demand.kr(clk, 0 , Dseq([-0.5,1,1,1],  inf));
  pos = center;
  pan = LFNoise2.kr(0.03, 0.5) + WhiteNoise.kr(0.6, 0.125);
  src = TGrains.ar(numChannels, clk, ~rec_Buf, rate, pos, dur, pan, 1.0);

  Out.ar(outBusIndex, src);
}).store();

/*task = Task.new({
  inf.do({
    var nextPos = 0.05;
    s.sendMsg("/n_set",  92000, ["center", nextPos]);
    "position: ".postln;
	nextPos.postln;
    3.0.wait;
   });
});*/


s.sendMsg(9, "orbit_rec", 99999, 1, ~rec_group.nodeID);
s.sendMsg(9, "grain_player", 92000, 1, 2);

// task.play;

window.onClose_({
	s.sendMsg(11, 92000);
	s.sendMsg(11, 99999);
	s.sendMsg(11, ~rec_group.nodeID);
	task.stop();
});

window.front;
)



(
~v.stop();
~v = Task.new({
  inf.do({
    var nextPos = 1.0.rand;
    s.sendMsg("/n_set",  92000, ["center", nextPos]);
    "position: ".postln;
	nextPos.postln;
    0.75.wait;
   });
});
~v.play;
)


