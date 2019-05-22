
s.reboot;


SynthDef(\pr_t, {|freq = 440|
	var src;
	src = :
	Out.ar(0,src);
})


s.boot;

b = Buffer.alloc(s,2048,1);

z = Buffer.read(s, Platform.resourceDir +/+ "sounds/a11wlk01.wav");



s.freeAll

(
	a = 2048;
	g = Buffer.alloc(s, a, 1);
)

(
	g.set(0, 1.0);
	100.do { |i| g.set(a.rand, (i + 1).reciprocal) };
)

(
SynthDef(\wnoise, {|out, sustain = 0.5, note = 0, pan = 0, amp = 1.0, begin = 0, end = 1.0|
	var src, env, dur;
	dur = 1.125 * (end - begin);
	env = EnvGen.ar(Env.perc(0.02, dur, amp, -0.8), timeScale:sustain, doneAction:2);
	src = ClipNoise.ar(0.02) * amp;
	OffsetOut.ar(out, DirtPan.ar(src, 2, pan, env));
}).store()
)
