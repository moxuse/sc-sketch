s.reboot;

~d = DirtSoundLibrary(s, 2);
~d.loadSoundFiles(Platform.resourceDir ++ "/sounds/superdirt/*", true);
~outBusCh = 6;


(
SynthDef(\ld4, {|out, sustain = 1, note = 0, speed = 1, begin=0, end=1, pan, accelerate, amp = 1, offset|
    var sound, d_freq, env, dec;
	d_freq = (note + 46).midicps;
	dec = Decay2.ar(Dust.ar(8), 0.01, Rand(0.85,1.2));
  env = EnvGen.ar(Env.perc(3, sustain * 4.6, amp, -4), timeScale:sustain, doneAction:2);

	sound = Mix.fill(5, {LFPulse.ar(d_freq * {[0.5,1,2,3,12,14].choose} + LFNoise1.ar(4,5),0.0, 0.3, 0.1) - LFSaw.ar(d_freq * 3 + LFNoise2.ar(2,10), SinOsc.ar(d_freq, 0, 1), 0.04 )});

    Out.ar(~outBusCh,
		Pan2.ar(RLPF.ar(sound, 5000, 0.3) * env * dec, 0)
    );
}).store;
)
(

SynthDef('orca_playback', {|rate = 1.0, bufnum = 0, amp = 1.0, pan = 0.0, shape = 0.4|
	var signal, dur;
	dur = BufSamples.kr(bufnum) / s.sampleRate * (1.0 / rate);
	signal = Pan2.ar(PlayBuf.ar(1, bufnum, rate, 1, 0, 0), pan);

	shape = min(shape, 1.0 - 4e-10); // avoid division by zero
	shape = (2.0 * shape) / (1.0 - shape);

	signal = (1 + shape) * signal / (1 + (shape * abs(signal)));

	Out.ar(~outBusCh, signal * EnvGen.ar(Env.new([0,1,1,0], [0.0, dur, dur]), levelScale:amp, doneAction:2))
}).store();


SynthDef('orca_mixer', {| chorus = 0.01, room = 0.9 |
	var signal = In.ar(~outBusCh,2), amp = 1,  out_signal, rev;

	signal = signal + Mix.fill(6, {
			DelayN.ar(signal, chorus, LFNoise1.kr(Rand(5,10),0.01,0.02));
		});

	4.do({signal = (signal * (1.0 - (room * 0.125))) + (CombN.ar(signal, 0.3, {[0.04.rand, 0.03.rand] +  0.05}, 0.5) * room) });

	rev= FreeVerb2.ar(signal[0], signal[1], (room * 2.0).clip(0,1.0), 0.5);
	out_signal = Limiter.ar(rev);


	Out.ar(0, out_signal)

}).store()

)

s.sendMsg(9, \orca_mixer, 40, 0, 1);
s.sendMsg(11, 40);



s.queryAllNodes;


(
OSCdef(\orca_synth_p, {|msg|
	var rate, pan, shape,  octave;
	octave = msg[3] - 4;
	rate = ((octave * 12) + msg[4]).midiratio;
	if (msg.size > 5, {
		pan = msg[5] * 2.0 - 1.0;
		shape = msg[6];
	}, {
		pan = 0.0;
		shape = 0.0;
	});
	msg[1].asSymbol;
	NetAddr("localhost",10000).sendMsg("/a", 0.5.rand);
	s.sendMsg(9,'orca_playback', s.nextNodeID, 0, 1, \bufnum, ~d.getEvent(msg[1].asSymbol, msg[2]).buffer, \rate, rate, \shape, shape, \pan, pan, \amp, 2.5);
}, \p);

OSCdef(\orca_set_mixer, {|msg|
	var chorus, room;
	chorus = msg[1];
	room = msg[2];
	s.sendMsg("/n_set", 40, \chorus, chorus, \room, room);
}, \q);

OSCdef(\ld4, {|msg|
	var noteIndex, ratio, pan, bufnum, oct;
	s.sendMsg(9,'ld4', s.nextNodeID, 0, 1, \note, msg[2], \shape, 0.3);
	NetAddr("localhost",10000).sendMsg("/e", 1.0);
}, \r);
)

(
OSCdef(\detect_perc, {|msg|
	var seed;
	seed = [0.3,2.4,0.6,0.05,1.4,0.5,2];

	NetAddr("localhost",10000).sendMsg("/a", seed.choose + 0.5.rand);

	if(msg[1].asString == "conv" || msg[1].asString == "accelerate",{
		NetAddr("localhost",10000).sendMsg("/e", 1.0);
		NetAddr("localhost",10000).sendMsg("/b", 1.2.rand + 0.4);
		NetAddr("localhost",10000).sendMsg("/c", 0.4.rand + 0.3 );

});
	if(msg[1].asString == "begin",{
		NetAddr("localhost",10000).sendMsg("/d", 1.7.rand + 0.2);
	});
	fork {
		0.05.wait;
		NetAddr("localhost",10000).sendMsg("/e", -1.0);
	}
},\play2)
)
~d.free;

s.sendMsg(9,'orca_playback', 10001, 0, 1, \bufnum, ~d.getEvent(\mst,30).buffer,\rate,1, \shape, 0.8);

(
~e = [
	(\folder: \zap, \index: 19),
	(\folder: \mhh2, \index: 25),
	(\folder: \mbd, \index: 15),
	(\folder: \mst, \index: 1),
	(\folder: \msn2, \index: 23),
	(\folder: \mpd2, \index: 20), // 5
	(\folder: \ml2, \index: 4 ),
	(\folder: \zap, \index: 28 ),
	(\folder: \mps, \index: 8), // 8
	(\folder: \mpd2, \index: 20),
	(\folder: \ml4, \index: 24), // a
	(\folder: \mb2, \index: 29),
]
)

(

)

(
MIDIClient.init;
MIDIIn.connect;
)


(
~chrom = Scale.chromatic.ratios;

MIDIdef.noteOn(\note, {|...args|
	var noteIndex, ratio, pan, bufnum, oct;
	oct = (args[1]/12 - 4).floor;
	if (oct <= -1.0, {
		oct = 1.0/((oct - 1.0) * (oct - 1.0));
	});
	if (oct == 0, { oct = 1/2});
	noteIndex = (args[1] - 60)%12;
	bufnum = ~d.getEvent(~e[args[2]].folder, ~e[args[2]].index).buffer;
	ratio = ~chrom[noteIndex] * oct;
	pan = (args[0] / 127) * 2.0 - 1.0;
	s.sendMsg(9,'orca_playback', s.nextNodeID, 0, 1, \bufnum, bufnum, \rate, ratio, \shape, 0.45, \pan, pan, \amp, 1.25);
});
)

MIDIIn.disconnect

s.makeGui;


