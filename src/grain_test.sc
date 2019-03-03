(
var outBusIndex  = ~dirt.orbits[0].synthBus.index;
var orbitGroupIndex = 2;
var numChannels = ~dirt.numChannels;
~rec_Buf = Buffer.alloc(s, 44100 * 6.0, 1, bufnum: 9999);
~rec_group = Group.after(orbitGroupIndex);

SynthDef('orbit_rec', {
  var in, trig;
  in = In.ar(40, 1);
  trig = Amplitude.kr(in);
  RecordBuf.ar(in, ~rec_Buf, doneAction: Done.none, trigger: trig, loop: 1);
}).store();

SynthDef('grain_player', {arg center=0.5;
  var trate, dur, clk, pos, pan, src;
    trate = MouseY.kr(8,120,1);
    dur = 4 / trate;
    clk = Impulse.kr(trate);
  pos = ((Lag2.kr(center,  0.25) * BufDur.kr(~rec_Buf))) + LFNoise2.kr(0.2, 5.0);
  pan = LFNoise2.kr(0.03, 0.5) + WhiteNoise.kr(0.6, 0.125);
  src = TGrains.ar(numChannels, clk, ~rec_Buf, 1, pos, dur, pan, 1.0);

  Out.ar(outBusIndex, src);
}).store();

t = Task.new({
  inf.do({
    s.sendMsg("/n_set",  92000, ["center", 1.0.rand]);
    "ready...".postln;
    3.0.wait;
   });
});
)

(
s.sendMsg(9, "orbit_rec", 99999, 1, ~rec_group.nodeID);
s.sendMsg(9, "grain_player", 92000, 1, 2);
t.play;
)

(
s.sendMsg(11, 92000);
s.sendMsg(11, 99999);
t.stop();
)

~rec_Buf.plot()

