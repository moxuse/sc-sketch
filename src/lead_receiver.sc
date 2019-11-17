// terminal.io.osc.select(57120)

(
var outBusIndex = ~dirt.orbits[0].dryBus.index;
var targetGroup = ~dirt.orbits[0].group;
// var outBusIndex = 0;
// var targetGroup = 1;
// var n = NetAddr("127.0.0.1", 57120);

SynthDef(\lead_0, {arg note=0, gate=1;
  var osc, env, freq,dPat;
  freq = (60 + note).midicps;
	dPat = [0.25, 0.3, 0.125, 0.06, 0.5, 0.01, 0.04 , 0.26];
  env = EnvGen.ar(Env.perc(0.1,5,0.6, -4), gate, doneAction:2);
  osc = DynKlank.ar(`[[freq, freq*1.75, freq*3], nil, [1,1,1]], WhiteNoise.ar(0.1)) * Decay2.ar(TDuty.ar(Dseq(dPat, inf)), 0.0, 0.08) * env;
  4.do({
    osc = AllpassC.ar(osc, [0.005.rand, 0.01], {0.025 + 0.002.rand}, 3) * 0.85;
  });
  Out.ar(outBusIndex, osc);
  // Out.ar(0, osc);
}).store;


SynthDef(\lead_1, {arg note=0, gate=1;
  var osc, env, freq, dPat;
	dPat = [0.14, 0.4, 0.125, 0.06, 0.5, 0.01, 0.04 , 0.26];
  freq = (60 + note).midicps;
  env = EnvGen.ar(Env.perc(0.3,5,1, -4), gate, doneAction:2);
  osc = DynKlank.ar(`[[freq*0.5, freq, freq*1.75, freq*7], nil, [1,1,1,1]], ClipNoise.ar(0.02)) * Decay2.ar(TDuty.ar(Dseq(dPat, inf)), 0.0, 0.1) * env;
  4.do({
    osc = AllpassN.ar(osc, [{0.004.rand}, {0.01.rand}], {0.15.rand}, 3) * 0.85;
  });
  Out.ar(outBusIndex, osc);
  // Out.ar(0, osc);
}).store;


SynthDef(\lead_2, {arg note=0, gate=1;
  var osc, env, freq, dPat;
	dPat = (0.01,0.035..1.4);
  freq = (60 + note).midicps;
  env = EnvGen.ar(Env.perc(0.3,5,1, -4), gate, doneAction:2);
	osc = DynKlank.ar(`[[freq*0.75, freq, freq*1.5, freq*5], nil, [1,1,1,1]], WhiteNoise.ar(0.02)) * Decay2.ar(TDuty.ar(Dseq(dPat, inf)), 0.0, 0.3) * env;
  4.do({
    osc = AllpassN.ar(osc, [{0.04.rand}, {0.03.rand}], {0.5.rand}, 0.08) * 0.85;
  });
  OffsetOut.ar(outBusIndex, osc);
  // Out.ar(0, osc);
}).store;


SynthDef(\lead_f, {arg note=0, gate=1;
  var osc, env, freq, dPat;

  freq = (60 + note).midicps;
  env = EnvGen.ar(Env.perc(2, 12, 1, -2), gate, doneAction:2);
	osc = Mix.fill(7,{
		LFPulse.ar([{[freq*1.5, freq, freq*1.01, freq*1.5, freq*3, freq*2].choose},{[freq*0.5, freq, freq*1.75, freq*2, freq*6, freq*4].choose}],0, {[0.03, 0.1, 0.04, 0.25].choose} , 0.2, -0.02) * env;
	});
  4.do({
    osc = AllpassC.ar(osc, [{0.2.rand}, {0.2.rand}], {0.2.rand}, 3) * 0.8;
  });
	OffsetOut.ar(outBusIndex, RLPF.ar(osc, LFNoise2.ar(0.2, 20, 72).midicps, 0.25));
  // Out.ar(0, osc);
}).store;


SynthDef(\lead_g, {arg note=0, gate=1;
  var osc, env, freq, dPat;

  freq = (60 + note).midicps;
  env = EnvGen.ar(Env.perc(2, 12, 1, -2), gate, doneAction:2);
	osc = Mix.fill(7,{
		LFPulse.ar([{[freq*1.5, freq, freq*0.98, freq*1.125, freq*3, freq*2].choose},{[freq*0.5, freq, freq*1.5, freq*2, freq*3, freq*4].choose}],0,{[0.03, 0.1, 0.04, 0.25].choose}, 0.75, -0.125) * Decay2.ar(Dust.ar(6), LFNoise0.kr(2,2,0.01).abs, 1) * env;
	});
  4.do({
    osc = AllpassC.ar(osc, [{0.2.rand}, {0.2.rand}], {0.2.rand}, 3) * 0.8;
  });
	OffsetOut.ar(outBusIndex, RLPF.ar(osc, LFNoise2.ar(0.2, 20, 82).midicps, 0.25));
  // Out.ar(0, osc);
}).store;

SynthDef(\lead_h, {arg note=0, gate=1;
  var osc, env, freq, dPat;

  freq = (60 + note).midicps;
  env = EnvGen.ar(Env.perc(2, 12, 1, -2), gate, doneAction:2);
	osc = Mix.fill(7,{
		LFSaw.ar([{[freq*1.5, freq, freq*0.98, freq*1.125, freq*3, freq*2].choose},{[freq*1.5, freq, freq*1.5, freq*2, freq*4, freq*3].choose}],0, 0.3) * Decay2.ar(Dust.ar(8), LFNoise0.kr(2,2,0.01).abs, 1) * env;
	});
	3.do({
		osc = AllpassC.ar(osc, [{0.2.rand}, {0.2.rand}], {0.2.rand}, 3) * 0.8;
	});
	OffsetOut.ar(outBusIndex, RLPF.ar(osc, LFNoise2.ar(0.1, 15, 102).midicps, 0.25));
  // Out.ar(0, osc);
}).store;

SynthDef(\lead_i,{|note=0, gate=1|
	var osc, env, freq, dPat;

  freq = (60 + note).midicps;

  env = EnvGen.ar(Env.perc(0.01, 8, 1, 4), gate, doneAction:2);

	osc = (LFSaw.ar([freq, freq * 1.01], SinOsc.ar(freq/2, 0, pi), 0.3)*12).softclip * 0.2 * LFNoise2.kr(8,0.3,0.05).abs * env;
	3.do({
		osc = AllpassC.ar(osc, [{0.02.rand}, {0.02.rand}] + 0.05, {0.1.rand}, 0.3);
	});
	OffsetOut.ar(outBusIndex, osc);
}).store();


SynthDef(\lead_j,{|note=0, gate=1|
	var osc, env, freq, dPat;

  freq = (60 + note).midicps;

  env = EnvGen.ar(Env.perc(0.01, 8, 1, 4), gate, doneAction:2);

	osc = SinOsc.ar([freq, freq * 1.01], SinOsc.ar(freq/2, 0, pi) * EnvGen.ar(Env.perc(0.01,1.5,2)), 0.3) * LFNoise2.kr(2,0.3,0.05, 0.5).abs * env;
	4.do({
		osc = AllpassC.ar(osc, [{0.02.rand}, {0.02.rand}] + 0.05, {0.1.rand}, 0.3);
	});
	OffsetOut.ar(outBusIndex, osc);
}).store();


SynthDef(\lead_k,{|note=0, gate=1|
	var osc, env, freq, dPat;

  freq = (60 + note).midicps;

  env = EnvGen.ar(Env.perc(0.01, 4, 1, -5), gate, doneAction:2);

	osc = SinOsc.ar([freq, freq * 1.01], SinOsc.ar(freq/2, LFSaw.ar(freq/2, 0, pi / 3 ), pi /2) * EnvGen.ar(Env.perc(0.3,1.5, 1, -4)), 0.3) * LFNoise2.kr(1.5,0.2,0.05, 0.5).abs * env;
	osc = RLPF.ar(osc, 700, 0.3);
	3.do({
		osc = CombC.ar(osc, [{0.02.rand}, {0.02.rand}] + 0.05, {0.1.rand}, 0.1);
	});
	OffsetOut.ar(outBusIndex, osc);
}).store();

/*
OSCdef(\lead_synth_0).enable;
OSCdef(\lead_synth_0, {|msg, time, addr, recvPort|
  // msg.postln
  s.sendMsg("/s_new", \lead_0, 40001 + s.nextNodeID, 3, targetGroup, \note, msg[1]);
}, 'a');
OSCdef(\lead_synth_1).enable;
OSCdef(\lead_synth_1, {|msg, time, addr, recvPort|
  // msg[1].postln;
  s.sendMsg("/s_new", \lead_1, 40001 + s.nextNodeID, 3, targetGroup, \note, msg[1]);
}, 'b');
OSCdef(\lead_synth_2).enable;
OSCdef(\lead_synth_2, {|msg, time, addr, recvPort|
  // msg[1].postln;
  s.sendMsg("/s_new", \lead_2, 40001 + s.nextNodeID, 3, targetGroup, \note, msg[1]);
}, 'c');

OSCdef(\lead_synth_f, {|msg, time, addr, recvPort|
  // msg[1].postln;
  s.sendMsg("/s_new", \lead_f, 40001 + s.nextNodeID, 3, targetGroup, \note, msg[1]);
}, 'f');*/
)

/*(
OSCdef(\lead_synth_1).disable;
OSCdef(\lead_synth_0).disable;
)*/
