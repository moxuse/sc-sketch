(
SynthDef(\orca_playback, {arg bufnum, note, outBus=60, sustain=1;
	var src, env, rate;
	rate = (note + 48).midicps / 60.midicps;
	src = PlayBuf.ar(1, bufnum, BufRateScale.kr(bufnum) * rate, 1.0, 0, 0, doneAction: Done.freeSelf).dup * 1.5;
	Out.ar(6, src * EnvGen.ar(Env.perc(0.0,2)));
}).store;


SynthDef(\shu, {arg note=60;
	var src, env;
	src = Klank.ar(`[[180, 478, 5001.5, 9723], nil, [0.3, 1, 0.25, 0.13]], WhiteNoise.ar(0.05)).dup * Decay2.ar(Impulse.ar(XLine.kr(2, 26, 2)), 0.01, 0.25);
	env = EnvGen.ar(Env.linen(0.5, 0.02, 4, 1, -4), doneAction:2);
	src = AllpassC.ar(src, LFNoise2.kr(0.8, 0.2, 0.2).abs, [0.1,0.12] + LFNoise2.kr(0.2, 0.2).abs, 1 +  LFNoise2.kr(4, 0.2).abs);
	Out.ar(6, RLPF.ar(src, LFNoise2.kr(2, 30, 100).midicps, 0.4) * env);
}).store;


SynthDef(\shu2, {arg note=60, out=0;
	var src, env;
	src = DynKlank.ar(`[[120, 278, 1001.5, 8723] + LFNoise2.kr(10, 10).abs, nil, [0.3, 1, 0.5, 0.9]], WhiteNoise.ar(0.1)).dup * Decay2.ar(Impulse.ar(XLine.kr(1, 16, 4)), 0.3, 1);
	env = EnvGen.ar(Env.linen(0.5, 0.2, 4, 1, -4), doneAction:2);
	2.do({src = CombC.ar(src, LFNoise2.kr(0.8, 0.1, 0.05).abs, [0.1,0.12] + LFNoise2.kr(0.2, 0.3).abs, 1 +  LFNoise2.kr(1, 0.2).abs + 0.1) });
	Out.ar(out, RLPF.ar((src * 2).softclip * 0.2, LFNoise2.kr(0.04, 30, 80).midicps, 0.3) * env);
}).store;


OSCdef(\orca_playback, {| msg |
	var bufnum, note, name, index;
	name = msg[1].asString;
	index = msg[2].asInt;
	bufnum = ~dirt.soundLibrary.buffers.at(name.asSymbol)[index].bufnum;
	note = msg[3];
	s.sendMsg(9, \orca_playback, s.nextNodeID, 0, 1, \bufnum, bufnum, \note, note, \outBus, 6, \amp,0.2,\sustain, 0.1);
}, 'p');


SynthDef("mem1", { arg note = 0, out = 0, maxd = 0.8;
    var in, chain, src, freq;
	freq = (note + 36).midicps;
	in = DynKlank.ar(`[[freq, freq/2, freq*2, freq *3, freq*7] + SinOsc.kr(1, 0, 5), nil, [1, 1, 1, 1, 1]], ClipNoise.ar([0.75, 0.75]));
    chain = FFT(LocalBuf(512), in);
	chain = PV_BinScramble(chain, MouseX.kr(0, 2.0));
	src = 0.1 * IFFT(chain).dup + PinkNoise.ar(0.75);
	7.do({src = src * 0.9 + AllpassC.ar(src, LFNoise2.kr(4,0.2, 0.15).abs, 0.015, {[0.1.rand + 0.1, 0.2.rand + 0.5] + LFNoise2.kr(2,0.18)},  ExpRand(0.03, maxd))});
	Out.ar(out, RLPF.ar((HPF.ar(src, 80) * 3).softclip * 0.2, LFNoise2.kr(1, 20, 70).midicps, 0.5) * EnvGen.ar(Env.perc(0.01,3.5,1,-2),doneAction:2));

}).store();


SynthDef("mem2", {| note = 0, coef_ = 0.37, out = 0 |
	var src, freq;
	freq = (note + 48).midicps;
	src = Pluck.ar(WhiteNoise.ar(0.05), 1, freq.reciprocal, freq.reciprocal, 10, coef:coef_);
	3.do({src = src * 0.9 + CombC.ar(src, LFNoise2.kr(4, 0.4, 0.15).abs, 0.08, {[0.1.rand + 0.1, 0.2.rand + 0.1] + LFNoise2.kr(1,0.8)},  ExpRand(0.05, 1.0))});
	4.do({src = src + CombN.ar(src, 0.1, {[0.04.rand, 0.03.rand] +  0.9}, 0.7)});
	Out.ar(6, (src* 4).softclip * 0.5  * EnvGen.ar(Env.perc(0.01,3,1,-4),doneAction:2));
}).store();


OSCdef(\orca_playback2, {| msg |
	var note, name, index;
	s.sendMsg(9, \shu2, s.nextNodeID, 0, 1, \out, 6);
}, 'q');

OSCdef(\orca_playback4, {| msg |
	var note, name, index;
	msg.postln;
	s.sendMsg(9, \mem1, s.nextNodeID, 0, 1, \out, 6, \note, msg[1],\maxd, msg[2] / 10.0);
}, 'r');

OSCdef(\orca_playback3, {| msg |
	var note, name, index;
	// msg.postln;
	s.sendMsg(9, \mem2, s.nextNodeID, 0, 1, \out, 6, \note, msg[1], \coef_, msg[2] / 10.0);
}, 'o');
)


s.sendMsg(9, \shu, s.nextNodeID, 1, 0);
s.sendMsg(9, \shu2, s.nextNodeID, 1, 0);


s.boot
