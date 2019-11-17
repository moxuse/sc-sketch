(
var targetGroupNum = 2;
~outBusCh = 6;
SynthDef('orca_playback', {|rate = 1.0, bufnum = 0, amp = 1.0, pan = 0.0, shape = 0.4|
	var signal, dur;
	dur = BufSamples.kr(bufnum) / s.sampleRate * (1.0 / rate);
	signal = Pan2.ar(PlayBuf.ar(1, bufnum, rate, 1, 0, 0), pan);

	shape = min(shape, 1.0 - 4e-10); // avoid division by zero
	shape = (2.0 * shape) / (1.0 - shape);

	signal = (1 + shape) * signal / (1 + (shape * abs(signal)));
	dur = dur.clip(0,1);
	Out.ar(~outBusCh, signal * EnvGen.ar(Env.new([0,1,1,0], [0.0, dur, dur]), levelScale:amp, doneAction:2))
}).store();

~chrom = Scale.chromatic.ratios;

MIDIdef.noteOn(\note, {|...args|
	var noteIndex, ratio, pan, bufnum, oct;
	// args.postln;
	oct = (args[1]/12 - 4).floor;
	if (oct <= -1.0, {
		oct = 1.0/((oct - 1.0) * (oct - 1.0));
	});
	if (oct == 0, { oct = 1/2});
	noteIndex = (args[1] - 60)%12;
	bufnum = ~dirt.soundLibrary.getEvent(~instList[args[2]].name, ~instList[args[2]].index).buffer;
	ratio = ~chrom[noteIndex] * oct;
	// args.postln;
	if(args[0] == 7, {
		s.sendMsg(9,'orca_playback', s.nextNodeID, 1, targetGroupNum, \bufnum, bufnum, \rate, ratio, \shape, 0.0, \pan, 0, \amp, 0.75);
	});
	if (args[0] == 15, {
		s.sendMsg(9, ~instList2[args[2]], s.nextNodeID, 1, targetGroupNum, \note, (args[1] - 60));
	});
	if (args[0] == 23, {

		var index = args[2];
		var nextId = s.nextNodeID;
		var sym, envVals, sendEnv, sendModEnv, envs_, mods_, out_;
		var orbitGroupIndex = 2;

			envVals = (~mposcEnvs.at("envs"))[index];
			sendEnv = envVals.at("env");
			sendModEnv = envVals.at("mod");

			envs_ = Env.new(sendEnv[0].collect({|v_| v_.asFloat}), sendEnv[1].collect({|v_| v_.asFloat})).asArray;
			mods_ = Env.new(sendModEnv[0].collect({|v_| v_.asFloat}), sendModEnv[1].collect({|v_| v_.asFloat})).asArray;

			s.postln;
			s.sendBundle(0.1,
				[\s_new, \pmosc_r, nextId, 0, targetGroupNum],
				[\n_set, nextId, \cfreq, envVals.at("ctrl").asFloat],
				[\n_set, nextId, \modfreq, envVals.at("modFreq").asFloat],
				[\n_set, nextId, \modscale, envVals.at("modeScale").asFloat],
				[\n_setn, nextId, \env, envs_.size] ++ envs_,
				[\n_setn, nextId, \modenv, mods_.size] ++ mods_
			);

	});
	if (args[0] >= 24, {
		bufnum = ~dirt.soundLibrary.getEvent(~instList3[args[2]].name, ~instList3[args[2]].index).buffer;
		s.sendMsg(9,'orca_playback', s.nextNodeID, 1, targetGroupNum, \bufnum, bufnum, \rate, ratio, \shape, 0.0, \pan, 0, \amp, 0.75);
	});
});
)
