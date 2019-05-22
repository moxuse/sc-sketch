(
SynthDef(\ld3, {|out, sustain = 1, note = 0, speed = 1, begin=0, end=1, pan, accelerate, amp = 1, offset|
    var sound, d_freq, env;
	d_freq = (note + 60).midicps;

  env = EnvGen.ar(Env.perc(0.01, sustain * 1.5, amp, -4), timeScale:sustain, doneAction:2);

  sound = RLPF.ar( Mix.fill(5, {SinOsc.ar(d_freq, 0.0, 0.1) + SinOsc.ar(d_freq * 2, LFSaw.ar(d_freq * 2,0,0.1,1), 0.03)}), LFNoise2.kr(0.4, 20 ,82).midicps, 0.5);

    Out.ar(0,
      Pan2.ar(sound * env, 0)
    );
}).store;

l = Scale.major;
OSCdef(\orca_playback, {| msg |
	var note, amp, octave;
	msg.postln;
	octave = msg[1];
	note = msg[2].degreeToKey(l, 12) + ((octave - 4) * 12);
	amp = msg[3] / 16;
	s.sendMsg(9, \ld3, s.nextNodeID, 1, 2, \note, note, \amp, amp);
}, 'p');
)

s.boot

(
SynthDef(\ld4, {|out, sustain = 1, note = 0, speed = 1, begin=0, end=1, pan, accelerate, amp = 1, offset|
    var sound, d_freq, env;
	d_freq = (note + 60).midicps;

  env = EnvGen.ar(Env.perc(0.01, sustain * 3.5, amp, -4), timeScale:sustain, doneAction:2);

  sound = Mix.fill(2, {SinOsc.ar(d_freq, 0.0, 0.1) + SinOsc.ar(d_freq * 2, LFSaw.ar(d_freq * 2,0,0.1,1), 0.2)});

    Out.ar(0,
      Pan2.ar(sound * env, 0)
    );
}).store;

)
