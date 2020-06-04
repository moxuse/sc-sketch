(
var envs, yamlPath, file, str, list, nameInput;
var window, sliderC, sliderMF, sliderMS, envF, envG, playButton, saveFunc, saveButton, listView;
var targetGroupNum = 2;

yamlPath = PathName.new("~/dev/sc/src/pmosc_envs/env.yml").fullPath;

SynthDef(\pmosc_r, {|cfreq=440, modfreq=50, modephase=0.0, modscale = 4.0, out = 0|
	var src, modEnv, env, modEnvCtl, envCtl;
	modEnv = Env.newClear(8);
	env = Env.newClear(8);
	modEnvCtl = Control.names([\modenv]).kr(modEnv.asArray);
	envCtl = Control.names([\env]).kr(env.asArray);
	src = PMOsc.ar(cfreq + LFNoise2.ar(35, 40, 40), modfreq, EnvGen.ar(modEnvCtl, modscale, levelScale:2), modephase, 0.2).dup;
	3.do({src = src* 0.25 + CombC.ar(src, LFNoise2.kr(0.8, 0.1, 0.05).abs, [0.1,0.12] + LFNoise2.kr(0.2, 0.3).abs, 1 +  LFNoise2.kr(1, 0.2).abs + 0.8) });
	Out.ar(out, src * EnvGen.ar(envCtl,1, 0.85, timeScale:2 ,doneAction: 2));
}).store();


file = File(yamlPath, "rb+");

if(PathName.new("./pmosc_envs").isFolder == false, {
	"mkdir pmosc_envs".unixCmd;
});
if(file.isOpen == false, {
	"file not exist".postln;
	"echo envs: >> ./pmosc_envs/env.yml".unixCmd;
	file.close();
});

file.readAllString.postln;
envs = (yamlPath).parseYAMLFile;
~mposcEnvs = envs;

window = Window("phase modulation synth generator", Rect(150 , Window.screenBounds.height - 250, 350, 550)).front;
window.view.decorator = FlowLayout(window.view.bounds);

sliderC = EZSlider(window, Rect(0, 40, 340, 40), "Ctrl Freq", \freq);
sliderMF = EZSlider(window, Rect(0, 40, 340, 40), "Mod Freq", \freq);
sliderMS = EZSlider(window, Rect(0, 40, 340, 40), "Mod Scale", \freq);

envF = EnvelopeView(window, Rect(0, 90, 340, 80))
    .drawLines_(true)
    .selectionColor_(Color.red)
    .drawRects_(true)
    .resize_(5)
    .step_(0.05)
    .thumbSize_(15)
    .value_([[0.0, 0.1, 0.2, 0.3, 0.5, 1.0],[0.1,1.0,0.8,0.8,0.0]]);

envG = EnvelopeView(window, Rect(0, 490, 340, 80))
    .drawLines_(true)
    .selectionColor_(Color.red)
    .drawRects_(true)
    .resize_(5)
    .step_(0.05)
    .thumbSize_(15)
    .value_([[0.0, 0.1, 0.2, 0.3, 0.5, 1.0],[0.1,1.0,0.8,0.8,0.0]]);

playButton = Button(window, Rect(0,0,90,40))
  .states_([["play", Color.blue, Color.white]])
  .action_({|v|
	var sendEnv, sendModEnv, nextId;
	sendEnv = envF.asEnv(1, 1, 1).asArray;
	sendModEnv = envG.asEnv(1, 2, 1).asArray;
	nextId = s.nextNodeID;
	sendModEnv.postln;
	s.sendBundle(0.2,
		[\s_new, \pmosc_r, nextId, 0, 1],
		[\n_set, nextId, \cfreq, sliderC.value],
		[\n_set, nextId, \modfreq, sliderMF.value],
		[\n_set, nextId, \modscale, sliderMS.value],
		[\n_setn, nextId, \env, sendEnv.size] ++ sendEnv,
		[\n_setn, nextId, \modenv, sendModEnv.size] ++ sendModEnv
	);
});

saveFunc = {|name, ctr, mf, ms, envArr, modArr|
	var str_ = "";
	str_ = str_ ++ "  - name: " ++ name ++ "\n";
	str_ = str_ ++ "    ctrl: " ++ ctr ++ "\n";
	str_ = str_ ++ "    modFreq: " ++ mf ++ "\n";
	str_ = str_ ++ "    modeScale: " ++ ms ++ "\n";
	str_ = str_ ++ "    env:\n";
	str_ = str_ ++ "      - " ++ envArr[0] ++ "\n";
	str_ = str_ ++ "      - " ++ envArr[1] ++ "\n";
	str_ = str_ ++ "    mod:\n";
	str_ = str_ ++ "      - " ++ modArr[0] ++ "\n";
	str_ = str_ ++ "      - " ++ modArr[1] ++ "\n";
	str_.postln;
	file.write(str_);
};

saveButton = Button(window, Rect(0,0,90,40))
  .states_([["Save", Color.blue]])
  .action_({|b_|
	nameInput.value.postln;
	envF.value.asArray.postln;
	saveFunc.postln;
	saveFunc.value(nameInput.value, sliderC.value, sliderMF.value, sliderMS.value, envF.value.asArray, envG.value.asArray);
  });

nameInput = TextField(window, Rect(0, 0, 150, 40)).value_("myPMOsc");

list = envs.at("envs").collect({|x| x.at("name")});
listView = ListView(window, Rect(10,10,340,170))
  .items_(list)
  .selectionAction_({|v_|
	var sym, envVals;
	sym = list[v_.selection][0];
	("You choose: index: " ++ v_.selection[0] ++ " env: " ++ sym).postln;
	envVals = envs.at("envs").at(v_.selection);
	envF.value_(envVals[0].at("env"));
	envG.value_(envVals[0].at("mod"));
	sliderC.value_(envVals[0].at("ctrl"));
	sliderMF.value_(envVals[0].at("modFreq"));
	sliderMS.value_(envVals[0].at("modeScale"));
  });

window.front;
window.onClose_({
  file.close();
});

OSCdef(\pmosc_kicker,{|msg|
	var index = 0;
	var nextId = s.nextNodeID;
	var sym, envVals, sendEnv, sendModEnv, envs_, mods_, out_;
	var orbitGroupIndex = 2;

	if (msg.includes(\mposc), {
		if (msg.includes(\n), {
			index = msg[msg.indexOf(\n).asInt + 1].asInt;
			// ("\n index exists: " ++ index).postln;
		});
		envVals = (envs.at("envs"))[index];
		sendEnv = envVals.at("env");
		sendModEnv = envVals.at("mod");

		envs_ = Env.new(sendEnv[0].collect({|v_| v_.asFloat}), sendEnv[1].collect({|v_| v_.asFloat})).asArray;
		mods_ = Env.new(sendModEnv[0].collect({|v_| v_.asFloat}), sendModEnv[1].collect({|v_| v_.asFloat})).asArray;

		s.postln;
		s.sendBundle(0.05,
			[\s_new, \pmosc_r, nextId, 0, targetGroupNum],
			[\n_set, nextId, \cfreq, envVals.at("ctrl").asFloat],
			[\n_set, nextId, \modfreq, envVals.at("modFreq").asFloat],
			[\n_set, nextId, \modscale, envVals.at("modeScale").asFloat],
			[\n_setn, nextId, \env, envs_.size] ++ envs_,
			[\n_setn, nextId, \modenv, mods_.size] ++ mods_
		);
	});

}, \play2);

)
