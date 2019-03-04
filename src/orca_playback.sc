(
SynthDef(\orca_playback, {arg bufnum, note;
	var src, env, rate;
	rate = (note + 60).midicps / 60.midicps;
	src = PlayBuf.ar(1, bufnum, BufRateScale.kr(bufnum) * rate, 1.0, 0, 0, doneAction: Done.freeSelf).dup * 0.15;
	Out.ar(0, src);
}).store;
)

(
OSCdef(\orca_playback, {| msg |
	var bufnum, note, name, index;
	name = msg[1].asString;
	index = msg[2].asInt;
	bufnum = ~dirt.soundLibrary.buffers.at(name.asSymbol)[index].bufnum;
	note = msg[3];
	s.sendMsg(9, \orca_playback, s.nextNodeID, 0, 1, \bufnum, bufnum, \note, note);
}, 'p');
)

