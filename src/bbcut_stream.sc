// s.boot

(
var outBusIndex  = ~dirt.orbits[0].synthBus.index;
var orbitGroupIndex = 2;
~cut_group = Group.after(orbitGroupIndex);
~cut_clock = TempoClock(2.4).play;
~cutter = BBCut2(
  CutGroup(
    [
      CutStream1.new(40, dutycycle: 0.76, atkprop: 0.04, relprop: 1.23, curve:-1),
      CutMixer(outBusIndex + 4, 1.0, {0.3.rand + 0.7}, CutPan1.new({0.75.rand - 0.45}))
    ],
    ~cut_group
  ),
  CageCut(16.0,[1,2,1,1,2,1],{arg array; if(0.2.coin,{array.pyramid;},{array})}, {arg array; if(0.2.coin,{array.scramble;},{array.permute(4.rand)}); })
  // WarpCutProc1({[1,2,4].choose})
);
~cutter.play(~cut_clock);
)

WarpCutProc1

(
~cutter.pause;
~cutter.resume;
~cutter.end;~cut_clock.free;~cut_group.free;
)

4/19