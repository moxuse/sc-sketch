p = ProxySpace.push(s);

Buffer.alloc(s,2048,2,bufnum:60);
~z.play;
~z.fadeTime=0.4;
(
~z.source={
var src,note,rate;
			rate = 2;
			note = 71;
	note =Duty.kr(
				Dseq([0.5]*8, inf), // demand ugen as durations
				0,
				Dseq([5,5,0,0]+note, inf) // demand ugen as amplitude
			);

	src=Mix.fill(3,{LFCub.ar(note.midicps*{[1,1.25,0.6,0.5123,2,1.333,7.6666].choose},LFPar.ar(note.midicps*2,LFCub.ar(note.midicps*5/7,0,LFNoise0.kr(10.4,6)),LFPar.ar(note.midicps,0,2)),0.08)})*HenonC.ar(SampleRate.ir/rate,TExpRand.ar(0.1,2.8,Dust.ar(10)),mul:0.8);
	x=src;
	src=CombC.ar(src,0.08,[0.043,0.04],0.08);
};


SynthDef("Hat",{arg amp=1,rate=1,del=0,rrate=1;
	var gate,src,cenv;
	gate=1;
	cenv=EnvGen.ar(Env.perc(0.0,0.017,amp,12),gate); // cluster envelope
	src=RHPF.ar(Impulse.ar(1200*rrate,SinOsc.ar(154*rate,SinOsc.ar(3450*rrate,0,830),6),12)*LFNoise2.ar(150,0.4),420,0.2)
		*cenv
		*EnvGen.ar(Env.linen(0.5,0.5,0.25,1,1).delay(del),doneAction:2); // synth envelope
	Out.ar(12,(src*20).softclip*0.18);
}).store;

//sine click
SynthDef("Click",{arg amp=1,rate=1,del=0,rrate=1;
	var gate,src;
	gate=1;
	src=SinOsc.ar(12660,SinOsc.ar(EnvGen.ar(Env.perc(0.0,0.15,2000,-12),gate)+60,SinOsc.ar(10,0,20),2*rate))*LFNoise2.ar(EnvGen.ar(Env.perc(0.0,0.2,600,-12))+30,0.8)
		*EnvGen.ar(Env.perc(0.0,0.05,amp,12),gate) // cluster envelope
		*EnvGen.ar(Env.linen(0.25,0.75,0.25,1,1).delay(del),doneAction:2); // synth envelope
	Out.ar(12,(src*10).softclip*0.25);
}).store;




SynthDef("rev1",{arg amp=1,delay=0.15,decay=0.35;
var src,env,z;
	z=In.ar(12);
	z=AllpassL.ar(z,0.1,0.02,0.04);
	4.do({ z = AllpassL.ar(z, 0.005, {[rrand(0.01, 0.03)+delay,rrand(0.01, 0.04)+delay]}, decay) });
	Out.ar(0, z*amp);
	}).store;
)

(
Pdef(\lamer,
Ppar([
		Pbind(\instrument,\Hat,\dur,Pseq([0.2, 0.025, 0.025, 0.125, 0.235, 0.225 ,0.225, 0.125,0.225,0.025,0.25,0.45,0.25,0.005,0.5,0.025,0.162,0.1],inf),
		\rate,Pseq([1,0.5,0.4,0.6,0.1,1,1],inf),\amp,1),

		Pbind(\instrument,\Click,\dur,Pseq([0.606,0.405,0.616,0.55,0.9],inf),
		\rate,1,\amp,Pseq([1,0.5,1,0],inf)),

		Pbind(\instrument,\bass2,\dur,Pseq([4.077]*2,inf),\note,Pseq([Pseq([[3,8,12,24]],16),Pseq([[8,10,0,24] + 24],16)]+32,inf)),

		Pbind(\instrument,\bass,\dur,Pseq([4.077]/16,inf),\note,Pseq([Pseq([[3,8,12,24,36]],16),Pseq([[8,10,0,24] + 7],16)]+32,inf)),
		Pbind(\instrument,\bass,\dur,Pseq([4.077]/16,inf),\note,Pseq([Pseq([3,8,12],4),Pseq([6,10,12],4)]+35,inf))

	])
).play;
)

~z.play;

(
SynthDef("bass",{arg amp=1,note=(60/2);
	var gate,src;
	gate=1;
	src=Mix.fill(5,{SinOsc.ar(note.midicps*{[1,1.5,0.75,0.5123,2,357,0.5].choose},SinOsc.ar(note.midicps/2,SinOsc.ar(note.midicps*2,LFNoise0.kr(4,12)),0.2),0.05)})
		*EnvGen.ar(Env.linen(0.8,0.3,0.0,1,12),doneAction:2)
	 *Decay2.ar(Impulse.ar(12),0.0,0.25); // synth envelope
	Out.ar(12,src);
}).store;


SynthDef("bass2",{arg amp=0.75,dur,note;
     var out;
	out= Pan2.ar(Klank.ar(`[[12725, 6415, 1840, 200, 3492], nil, [0.15, 0.3, 0.5, 0.05, 0.6]], SinOsc.ar(note.midicps,0,LFNoise2.ar(0.1)
) )*amp*
EnvGen.kr(Env.perc(0.1,1.75*dur,0.3,-5), 1, doneAction:2),LFNoise1.ar(0.06));
Out.ar(12,out);
}).store;

~z.source={
var src,note,rate;
			rate = 2;
	note = {[70,72,75].choose - 5};
	note =Duty.kr(
				Dseq([0.5]*8, inf), // demand ugen as durations
				0,
				Dseq([5,5,0,0]+note, inf) // demand ugen as amplitude
			);

	src=Mix.fill(3,{SinOsc.ar(note.midicps*{[1,1.25,0.5,0.5123,2,1.333,7.6666].choose * 3},LFPar.ar(note.midicps*5,LFCub.ar(note.midicps*5/7,0,LFNoise0.kr(10.4,6)),LFPar.ar(note.midicps,0,2)),0.08)})*HenonC.ar(SampleRate.ir/rate,TExpRand.ar(0.1,2.8,Dust.ar(10)),mul:0.28);
	x=src;
	src=CombC.ar(RLPF.ar(src, 2900, 0.2),0.01,[0.02,0.04],0.05);
};
)


s.sendMsg(9,"rev1",5000,1,1);

Pdef(\lamer).play;

///end
~z.end;
Pdef(\lamer).stop;
s.sendMsg(11,5000);





















